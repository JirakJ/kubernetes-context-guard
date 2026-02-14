package com.jakubjirak.k8sguard.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import javax.swing.table.DefaultTableModel

class K8sGuardConfigurable : BoundConfigurable("KubeContext Safety") {

    private var rulesTableModel: DefaultTableModel? = null

    override fun createPanel(): DialogPanel {
        val settings = K8sGuardSettings.getInstance()

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

        val rulesTable = JBTable(rulesTableModel)

        val rulesPanel = ToolbarDecorator.createDecorator(rulesTable)
            .setAddAction {
                rulesTableModel?.addRow(arrayOf<Any>("*pattern*", "PRODUCTION", true))
            }
            .setRemoveAction {
                val selectedRow = rulesTable.selectedRow
                if (selectedRow >= 0) {
                    rulesTableModel?.removeRow(selectedRow)
                }
            }
            .createPanel()

        return panel {
            group("General") {
                row {
                    checkBox("Enable KubeContext Safety")
                        .bindSelected(settings::enabled)
                }
                row {
                    checkBox("Show notification on production context switch")
                        .bindSelected(settings::showNotificationOnProductionSwitch)
                        .comment("Show a warning notification when switching to a production context")
                }
            }
            group("Kubeconfig") {
                row("Path override:") {
                    textFieldWithBrowseButton("Select Kubeconfig File")
                        .bindText(settings::kubeconfigPathOverride)
                        .comment("Override kubeconfig file path. Leave empty to use default (~/.kube/config)")
                        .align(AlignX.FILL)
                }
            }
            group("Custom Context Rules (evaluated before defaults)") {
                row {
                    cell(rulesPanel)
                        .align(Align.FILL)
                }.resizableRow()
            }
        }
    }

    override fun isModified(): Boolean {
        if (super.isModified()) return true
        val settings = K8sGuardSettings.getInstance()
        return getTableRules() != settings.customRules
    }

    override fun apply() {
        super.apply()
        K8sGuardSettings.getInstance().customRules = getTableRules().toMutableList()
    }

    override fun reset() {
        super.reset()
        val settings = K8sGuardSettings.getInstance()
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
        super.disposeUIResources()
        rulesTableModel = null
    }
}
