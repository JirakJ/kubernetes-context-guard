package com.jakubjirak.k8sguard.watcher

import com.jakubjirak.k8sguard.config.KubeConfigLocator
import com.jakubjirak.k8sguard.config.KubeConfigParser
import com.jakubjirak.k8sguard.model.KubeConfig
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Monitors kubeconfig file(s) for changes and notifies listeners.
 * Uses polling (2s interval) since kubeconfig changes are external to the IDE.
 */
@Service(Service.Level.APP)
class KubeConfigWatcher : Disposable {

    private var timer: Timer? = null
    private var lastModified: Long = 0
    private var lastConfig: KubeConfig? = null
    private val listeners = CopyOnWriteArrayList<ContextChangeListener>()

    fun interface ContextChangeListener {
        fun onContextChanged(config: KubeConfig)
    }

    fun addListener(listener: ContextChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ContextChangeListener) {
        listeners.remove(listener)
    }

    fun start() {
        if (timer != null) return

        LOG.info("Starting kubeconfig watcher")

        // Initial load
        refreshConfig()

        timer = Timer("K8sGuard-ConfigWatcher", true).apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    try {
                        checkForChanges()
                    } catch (e: Exception) {
                        LOG.debug("Error checking kubeconfig", e)
                    }
                }
            }, POLL_INTERVAL_MS, POLL_INTERVAL_MS)
        }
    }

    fun stop() {
        timer?.cancel()
        timer = null
    }

    fun getCurrentConfig(): KubeConfig? = lastConfig

    fun refreshConfig(): KubeConfig? {
        val configFile = KubeConfigLocator.getPrimaryConfigFile() ?: return null
        val config = KubeConfigParser.parse(configFile)
        if (config != null) {
            val previousContext = lastConfig?.currentContext
            lastConfig = config
            lastModified = configFile.lastModified()

            if (previousContext != null && previousContext != config.currentContext) {
                notifyListeners(config)
            } else if (previousContext == null) {
                notifyListeners(config)
            }
        }
        return config
    }

    private fun checkForChanges() {
        val configFile = KubeConfigLocator.getPrimaryConfigFile() ?: return
        val currentModified = configFile.lastModified()
        if (currentModified != lastModified) {
            LOG.info("Kubeconfig changed, reloading")
            refreshConfig()
        }
    }

    private fun notifyListeners(config: KubeConfig) {
        ApplicationManager.getApplication().invokeLater {
            listeners.forEach { listener ->
                try {
                    listener.onContextChanged(config)
                } catch (e: Exception) {
                    LOG.warn("Error notifying context change listener", e)
                }
            }
        }
    }

    override fun dispose() {
        stop()
        listeners.clear()
    }

    companion object {
        private const val POLL_INTERVAL_MS = 2000L
        private val LOG = Logger.getInstance(KubeConfigWatcher::class.java)

        fun getInstance(): KubeConfigWatcher =
            ApplicationManager.getApplication().getService(KubeConfigWatcher::class.java)
    }
}
