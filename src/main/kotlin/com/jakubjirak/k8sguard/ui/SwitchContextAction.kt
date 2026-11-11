package com.jakubjirak.k8sguard.ui

import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.rules.ContextRuleEngine
import com.jakubjirak.k8sguard.rules.TeamRulesLoader
import com.jakubjirak.k8sguard.settings.K8sGuardSettings
import com.jakubjirak.k8sguard.watcher.KubeConfigWatcher
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import java.io.IOException

class SwitchContextAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        showPopup(project)
    }

    fun showPopup(project: Project) {
        val config = KubeConfigWatcher.getInstance().getCurrentConfig()
        if (config == null || config.contexts.isEmpty()) {
            JBPopupFactory.getInstance()
                .createMessage("No Kubernetes contexts found. Check your kubeconfig.")
                .showCenteredInCurrentWindow(project)
            return
        }

        val rules = K8sGuardSettings.getInstance().getEffectiveRules(TeamRulesLoader.loadRules(project))
        val contextInfos = config.contexts.map { ctx ->
            ContextRuleEngine.classify(ctx, rules)
        }

        val currentContext = config.currentContext

        val step = object : BaseListPopupStep<ContextInfo>("Switch Kubernetes Context", contextInfos) {
            override fun getTextFor(value: ContextInfo): String {
                val marker = if (value.context.name == currentContext) "✓ " else "  "
                val envIcon = value.environment.icon
                return "$marker$envIcon ${value.context.name} (${value.context.namespace})"
            }

            override fun onChosen(selectedValue: ContextInfo, finalChoice: Boolean): PopupStep<*>? {
                if (!finalChoice || selectedValue.context.name == currentContext) return FINAL_CHOICE
                // Run after the popup has closed so the confirmation dialog is not nested in it.
                return doFinalStep { confirmAndSwitch(project, selectedValue) }
            }

            override fun isSpeedSearchEnabled(): Boolean = true
        }

        JBPopupFactory.getInstance()
            .createListPopup(step)
            .showCenteredInCurrentWindow(project)
    }

    private fun confirmAndSwitch(project: Project, info: ContextInfo) {
        if (needsConfirmation(info) && !ProductionGuardDialog.showAndGet(info)) return
        ApplicationManager.getApplication().executeOnPooledThread { switchContext(project, info) }
    }

    /** `kubectl config use-context` off the EDT; failures are reported as a notification. */
    private fun switchContext(project: Project, info: ContextInfo) {
        val error = try {
            val process = ProcessBuilder("kubectl", "config", "use-context", info.context.name)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            if (process.waitFor() == 0) null else output.ifBlank { "kubectl exited with an error" }
        } catch (e: IOException) {
            LOG.warn("Failed to execute kubectl config use-context", e)
            "kubectl could not be started (is it on PATH?): ${e.message}"
        }
        if (error == null) {
            LOG.info("Switched to context: ${info.context.name}")
            KubeConfigWatcher.getInstance().refreshConfig()
            return
        }
        LOG.warn("Failed to switch context: $error")
        NotificationGroupManager.getInstance()
            .getNotificationGroup("K8s Context Guard")
            .createNotification("Could not switch to ${info.context.name}", error.trim(), NotificationType.ERROR)
            .notify(project)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = K8sGuardSettings.getInstance().enabled
    }

    companion object {
        private val LOG = Logger.getInstance(SwitchContextAction::class.java)

        /** Contexts classified as production, or by a rule with "confirm actions", ask before switching. */
        fun needsConfirmation(info: ContextInfo): Boolean =
            info.requiresConfirmation || info.environment == Environment.PRODUCTION
    }
}
