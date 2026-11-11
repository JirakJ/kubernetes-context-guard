package com.jakubjirak.k8sguard.ui

import com.jakubjirak.k8sguard.guard.ProductionGuardService
import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.rules.ContextRuleEngine
import com.jakubjirak.k8sguard.rules.TeamRulesLoader
import com.jakubjirak.k8sguard.settings.K8sGuardSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent

class ContextStatusBarWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = WIDGET_ID

    override fun getDisplayName(): String = "KubeContext Safety"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget = ContextStatusBarWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true

    companion object {
        const val WIDGET_ID = "com.jakubjirak.k8sguard.statusBarWidget"
    }
}

class ContextStatusBarWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {

    private var statusBar: StatusBar? = null

    @Volatile
    private var currentInfo: ContextInfo? = null

    private val listener: (ContextInfo?) -> Unit = { info ->
        currentInfo = info?.let { reclassify(it) }
        statusBar?.updateWidget(ID())
    }

    init {
        ProductionGuardService.getInstance().addContextInfoListener(listener)
    }

    /** Re-applies the rules including this project's `.k8s-guard.yaml` team rules. */
    private fun reclassify(info: ContextInfo): ContextInfo =
        ContextRuleEngine.classify(info.context, K8sGuardSettings.getInstance().getEffectiveRules(TeamRulesLoader.loadRules(project)))

    override fun ID(): String = ContextStatusBarWidgetFactory.WIDGET_ID

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        currentInfo = ProductionGuardService.getInstance().getCurrentContextInfo()?.let { reclassify(it) }
    }

    @NlsContexts.Label
    override fun getText(): String {
        val info = currentInfo ?: return "⎈ No context"
        return "${info.environment.icon} ${info.displayText}"
    }

    override fun getTooltipText(): String = tooltip(currentInfo)

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun getClickConsumer(): Consumer<MouseEvent> = Consumer {
        SwitchContextAction().showPopup(project)
    }

    override fun dispose() {
        ProductionGuardService.getInstance().removeContextInfoListener(listener)
        statusBar = null
    }

    companion object {
        fun tooltip(info: ContextInfo?): String {
            if (info == null) return "Kubernetes: No context detected"
            return buildString {
                appendLine("KubeContext Safety")
                appendLine("Context: ${info.context.name}")
                appendLine("Cluster: ${info.context.cluster}")
                appendLine("Namespace: ${info.context.namespace}")
                appendLine("User: ${info.context.user}")
                appendLine("Environment: ${info.environment.displayName}")
                if (info.requiresConfirmation) {
                    appendLine("⚠️ Switching to this context asks for confirmation")
                }
            }.trim()
        }
    }
}
