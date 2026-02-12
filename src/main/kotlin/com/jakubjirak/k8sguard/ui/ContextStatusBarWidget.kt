package com.jakubjirak.k8sguard.ui

import com.jakubjirak.k8sguard.guard.ProductionGuardService
import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.Environment
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.Icon

class ContextStatusBarWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = WIDGET_ID

    override fun getDisplayName(): String = "Kubernetes Context Guard"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget = ContextStatusBarWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true

    companion object {
        const val WIDGET_ID = "com.jakubjirak.k8sguard.statusBarWidget"
    }
}

class ContextStatusBarWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {

    private var statusBar: StatusBar? = null
    private var currentInfo: ContextInfo? = null

    init {
        ProductionGuardService.getInstance().addContextInfoListener { info ->
            currentInfo = info
            statusBar?.updateWidget(ID())
        }
    }

    override fun ID(): String = ContextStatusBarWidgetFactory.WIDGET_ID

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        currentInfo = ProductionGuardService.getInstance().getCurrentContextInfo()
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    @NlsContexts.Label
    override fun getText(): String {
        val info = currentInfo ?: return "⎈ No context"
        return info.displayText
    }

    override fun getTooltipText(): String {
        val info = currentInfo ?: return "Kubernetes: No context detected"
        return buildString {
            appendLine("Kubernetes Context Guard")
            appendLine("Context: ${info.context.name}")
            appendLine("Cluster: ${info.context.cluster}")
            appendLine("Namespace: ${info.context.namespace}")
            appendLine("Environment: ${info.environment.displayName}")
            if (info.requiresConfirmation) {
                appendLine("⚠️ Confirmation required for destructive actions")
            }
        }.trim()
    }

    override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

    override fun getClickConsumer(): Consumer<MouseEvent> = Consumer {
        SwitchContextAction().showPopup(project)
    }

    override fun dispose() {
        statusBar = null
    }
}
