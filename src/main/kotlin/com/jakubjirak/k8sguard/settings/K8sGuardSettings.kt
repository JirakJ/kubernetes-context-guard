package com.jakubjirak.k8sguard.settings

import com.jakubjirak.k8sguard.model.ContextRule
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.rules.ContextRuleEngine
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import java.time.Duration

@State(
    name = "K8sGuardSettings",
    storages = [Storage(value = "k8sGuardSettings.xml", roamingType = RoamingType.DEFAULT)],
)
class K8sGuardSettings : PersistentStateComponent<K8sGuardSettings> {

    var enabled: Boolean = true
    var showNotificationOnProductionSwitch: Boolean = true
    var kubeconfigPathOverride: String = ""

    @XCollection(style = XCollection.Style.v2)
    var customRules: MutableList<SerializableContextRule> = mutableListOf()

    override fun getState(): K8sGuardSettings = this

    override fun loadState(state: K8sGuardSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    /** Rules in evaluation order: custom (Settings), then team rules (`.k8s-guard.yaml`), then built-in defaults. */
    fun getEffectiveRules(teamRules: List<ContextRule> = emptyList()): List<ContextRule> =
        effectiveRules(customRules.map { it.toContextRule() }, teamRules)

    companion object {
        fun effectiveRules(custom: List<ContextRule>, team: List<ContextRule>): List<ContextRule> =
            custom.filter { it.pattern.isNotBlank() } + team + ContextRuleEngine.DEFAULT_RULES

        fun getInstance(): K8sGuardSettings =
            ApplicationManager.getApplication().getService(K8sGuardSettings::class.java)
    }
}

@Tag("rule")
data class SerializableContextRule(
    var pattern: String = "",
    var environment: String = "UNKNOWN",
    var confirmActions: Boolean = false,
    var reminderIntervalMinutes: Long = 0,
) {
    // reminderIntervalMinutes is kept only so existing settings files keep loading; reminders are not implemented.
    fun toContextRule(): ContextRule = ContextRule(
        pattern = pattern,
        environment = try {
            Environment.valueOf(environment)
        } catch (_: IllegalArgumentException) {
            Environment.UNKNOWN
        },
        confirmActions = confirmActions,
        reminderInterval = if (reminderIntervalMinutes > 0) Duration.ofMinutes(reminderIntervalMinutes) else null,
    )

    companion object {
        fun fromContextRule(rule: ContextRule): SerializableContextRule = SerializableContextRule(
            pattern = rule.pattern,
            environment = rule.environment.name,
            confirmActions = rule.confirmActions,
            reminderIntervalMinutes = rule.reminderInterval?.toMinutes() ?: 0,
        )
    }
}
