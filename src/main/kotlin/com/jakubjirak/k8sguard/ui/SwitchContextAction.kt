package com.jakubjirak.k8sguard.ui

import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.rules.ContextRuleEngine
import com.jakubjirak.k8sguard.settings.K8sGuardSettings
import com.jakubjirak.k8sguard.watcher.KubeConfigWatcher
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import java.awt.Color
import javax.swing.Icon

class SwitchContextAction : AnAction() {

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

        val settings = K8sGuardSettings.getInstance()
        val rules = settings.getEffectiveRules()
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
                if (finalChoice && selectedValue.context.name != currentContext) {
                    switchContext(selectedValue)
                }
                return FINAL_CHOICE
            }

            override fun isSpeedSearchEnabled(): Boolean = true
        }

        JBPopupFactory.getInstance()
            .createListPopup(step)
            .showCenteredInCurrentWindow(project)
    }

    private fun switchContext(info: ContextInfo) {
        try {
            val process = ProcessBuilder("kubectl", "config", "use-context", info.context.name)
                .redirectErrorStream(true)
                .start()

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                LOG.info("Switched to context: ${info.context.name}")
                // Trigger watcher refresh
                KubeConfigWatcher.getInstance().refreshConfig()
            } else {
                val error = process.inputStream.bufferedReader().readText()
                LOG.warn("Failed to switch context: $error")
            }
        } catch (e: Exception) {
            LOG.warn("Failed to execute kubectl config use-context", e)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = K8sGuardSettings.getInstance().enabled
    }

    companion object {
        private val LOG = Logger.getInstance(SwitchContextAction::class.java)
    }
}
