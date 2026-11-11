package com.jakubjirak.k8sguard.ui

import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.Environment
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * Confirmation dialog shown when switching to a production context.
 */
class ProductionGuardDialog(private val contextInfo: ContextInfo) : DialogWrapper(true) {

    init {
        title = "⚠️ Production Context Warning"
        setOKButtonText("Continue — I know what I'm doing")
        setCancelButtonText("Cancel")
        init()
    }

    override fun createCenterPanel(): JComponent {
        val warningLabel = JBLabel(
            "<html><b style='font-size:14pt; color:#E53935;'>⚠ PRODUCTION CONTEXT ACTIVE</b></html>"
        ).apply {
            horizontalAlignment = SwingConstants.CENTER
        }

        val contextLabel = JBLabel(
            "<html><div style='text-align:center; margin:10px;'>" +
                "You are about to switch kubectl to:<br/>" +
                "<b style='font-size:12pt;'>${contextInfo.context.name}</b><br/>" +
                "<br/>" +
                "Cluster: <b>${contextInfo.context.cluster}</b><br/>" +
                "Namespace: <b>${contextInfo.context.namespace}</b><br/>" +
                "<br/>" +
                "<span style='color:#E53935;'>Destructive operations in this context affect " +
                "<b>production</b> systems.</span>" +
                "</div></html>"
        ).apply {
            horizontalAlignment = SwingConstants.CENTER
        }

        return FormBuilder.createFormBuilder()
            .addComponent(warningLabel)
            .addVerticalGap(10)
            .addComponent(contextLabel)
            .addVerticalGap(10)
            .panel.apply {
                border = JBUI.Borders.empty(20)
                preferredSize = JBUI.size(450, 280)
            }
    }

    companion object {
        fun showAndGet(contextInfo: ContextInfo): Boolean {
            return ProductionGuardDialog(contextInfo).showAndGet()
        }
    }
}
