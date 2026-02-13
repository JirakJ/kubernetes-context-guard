package com.jakubjirak.k8sguard.settings

import com.jakubjirak.k8sguard.model.Environment
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBoxTableRenderer
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class K8sGuardConfigurable : Configurable {

    private var enabledCheckbox: JBCheckBox? = null
    private var notifyCheckbox: JBCheckBox? = null
    private var kubeconfigField: JBTextField? = null
    private var rulesTableModel: DefaultTableModel? = null
    private var mainPanel: JPanel? = null

    override fun getDisplayName(): String = "KubeContext Safety"

    override fun createComponent(): JComponent {
        val settings = K8sGuardSettings.getInstance()

        enabledCheckbox = JBCheckBox("Enable KubeContext Safety", settings.enabled)
        notifyCheckbox = JBCheckBox("Show notification on production context switch", settings.showNotificationOnProductionSwitch)
        kubeconfigField = JBTextField(settings.kubeconfigPathOverride).apply {
            emptyText.text = "~/.kube/config (default)"
        }

        // Rules table
        val columnNames = arrayOf("Pattern", "Environment", "Confirm Actions")
        rulesTableModel = object : DefaultTableModel(columnNames, 0) {
            override fun getColumnClass(columnIndex: Int): Class<*> = when (columnIndex) {
                2 -> java.lang.Boolean::class.java
                else -> String::class.java
            }

            override fun isCellEditable(row: Int, column: Int): Boolean = true
        }

        settings.customRules.forEach { rule ->
            rulesTableModel?.addRow(arrayOf<Any>(rule.pattern, rule.environment, rule.confirmActions))
        }

        val rulesTable = JBTable(rulesTableModel).apply {
            columnModel.getColumn(1).cellEditor = com.intellij.ui.components.fields.ExtendableTextField()
                .let { null } // Will use default string editor; environment validated on save
        }

        val toolbarDecorator = ToolbarDecorator.createDecorator(rulesTable)
            .setAddAction {
                rulesTableModel?.addRow(arrayOf<Any>("*pattern*", "PRODUCTION", true))
            }
            .setRemoveAction {
                val selectedRow = rulesTable.selectedRow
                if (selectedRow >= 0) {
                    rulesTableModel?.removeRow(selectedRow)
                }
            }

        val rulesPanel = toolbarDecorator.createPanel().apply {
            border = IdeBorderFactory.createTitledBorder("Custom Context Rules (evaluated before defaults)")
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(enabledCheckbox!!)
            .addComponent(notifyCheckbox!!)
            .addLabeledComponent(JBLabel("Kubeconfig path override:"), kubeconfigField!!)
            .addComponentFillVertically(rulesPanel, 10)
            .panel

        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val settings = K8sGuardSettings.getInstance()
        if (enabledCheckbox?.isSelected != settings.enabled) return true
        if (notifyCheckbox?.isSelected != settings.showNotificationOnProductionSwitch) return true
        if (kubeconfigField?.text != settings.kubeconfigPathOverride) return true
        if (getTableRules().size != settings.customRules.size) return true
        return getTableRules() != settings.customRules
    }

    override fun apply() {
        val settings = K8sGuardSettings.getInstance()
        settings.enabled = enabledCheckbox?.isSelected ?: true
        settings.showNotificationOnProductionSwitch = notifyCheckbox?.isSelected ?: true
        settings.kubeconfigPathOverride = kubeconfigField?.text ?: ""
        settings.customRules = getTableRules().toMutableList()
    }

    override fun reset() {
        val settings = K8sGuardSettings.getInstance()
        enabledCheckbox?.isSelected = settings.enabled
        notifyCheckbox?.isSelected = settings.showNotificationOnProductionSwitch
        kubeconfigField?.text = settings.kubeconfigPathOverride

        rulesTableModel?.rowCount = 0
        settings.customRules.forEach { rule ->
            rulesTableModel?.addRow(arrayOf<Any>(rule.pattern, rule.environment, rule.confirmActions))
        }
    }

    private fun getTableRules(): List<SerializableContextRule> {
        val model = rulesTableModel ?: return emptyList()
        return (0 until model.rowCount).map { row ->
            SerializableContextRule(
                pattern = model.getValueAt(row, 0) as? String ?: "",
                environment = model.getValueAt(row, 1) as? String ?: "UNKNOWN",
                confirmActions = model.getValueAt(row, 2) as? Boolean ?: false,
            )
        }
    }

    override fun disposeUIResources() {
        enabledCheckbox = null
        notifyCheckbox = null
        kubeconfigField = null
        rulesTableModel = null
        mainPanel = null
    }
}
