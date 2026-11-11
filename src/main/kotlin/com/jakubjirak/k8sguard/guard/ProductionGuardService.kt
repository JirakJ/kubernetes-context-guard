package com.jakubjirak.k8sguard.guard

import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.model.KubeConfig
import com.jakubjirak.k8sguard.rules.ContextRuleEngine
import com.jakubjirak.k8sguard.settings.K8sGuardSettings
import com.jakubjirak.k8sguard.watcher.KubeConfigWatcher
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger

/**
 * Central guard service that monitors context changes and triggers appropriate actions.
 */
@Service(Service.Level.APP)
class ProductionGuardService : Disposable, KubeConfigWatcher.ContextChangeListener {

    @Volatile
    private var currentContextInfo: ContextInfo? = null
    private val contextInfoListeners = java.util.concurrent.CopyOnWriteArrayList<(ContextInfo?) -> Unit>()

    fun start() {
        val watcher = KubeConfigWatcher.getInstance()
        watcher.addListener(this)

        // Process initial config
        watcher.getCurrentConfig()?.let { onContextChanged(it) }
    }

    fun getCurrentContextInfo(): ContextInfo? = currentContextInfo

    fun addContextInfoListener(listener: (ContextInfo?) -> Unit) {
        contextInfoListeners.add(listener)
    }

    fun removeContextInfoListener(listener: (ContextInfo?) -> Unit) {
        contextInfoListeners.remove(listener)
    }

    override fun onContextChanged(config: KubeConfig) {
        val context = config.getCurrentContextInfo() ?: return
        val settings = K8sGuardSettings.getInstance()
        val rules = settings.getEffectiveRules()
        val info = ContextRuleEngine.classify(context, rules)

        val previousInfo = currentContextInfo
        currentContextInfo = info

        // Notify UI listeners
        contextInfoListeners.forEach { listener ->
            try {
                listener(info)
            } catch (e: Exception) {
                LOG.warn("Error notifying context info listener", e)
            }
        }

        // Show notification on production context switch
        if (shouldWarn(previousInfo, info, settings.enabled, settings.showNotificationOnProductionSwitch)) {
            showProductionWarning(info)
        }
    }

    private fun showProductionWarning(info: ContextInfo) {
        ApplicationManager.getApplication().invokeLater {
            try {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("K8s Context Guard")
                    .createNotification(
                        "Production Context Active",
                        "You are now connected to <b>${info.context.name}</b> " +
                            "(cluster: ${info.context.cluster}, namespace: ${info.context.namespace}). " +
                            "Be careful with destructive operations.",
                        NotificationType.WARNING,
                    )
                    .notify(null)
            } catch (e: Exception) {
                LOG.warn("Failed to show production warning notification", e)
            }
        }
    }

    override fun dispose() {
        contextInfoListeners.clear()
        try {
            KubeConfigWatcher.getInstance().removeListener(this)
        } catch (_: Exception) {
            // Service may already be disposed
        }
    }

    companion object {
        private val LOG = Logger.getInstance(ProductionGuardService::class.java)

        /** Warn only when entering production (not on every refresh while already there) and when enabled. */
        fun shouldWarn(previous: ContextInfo?, current: ContextInfo, enabled: Boolean, notifyOnProduction: Boolean): Boolean =
            enabled && notifyOnProduction &&
                current.environment == Environment.PRODUCTION &&
                previous?.environment != Environment.PRODUCTION

        fun getInstance(): ProductionGuardService =
            ApplicationManager.getApplication().getService(ProductionGuardService::class.java)
    }
}
