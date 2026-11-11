package com.jakubjirak.k8sguard.guard

import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.ContextRule
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.model.KubeContext
import com.jakubjirak.k8sguard.rules.ContextRuleEngine
import com.jakubjirak.k8sguard.settings.K8sGuardSettings
import com.jakubjirak.k8sguard.settings.SerializableContextRule
import com.jakubjirak.k8sguard.ui.ContextStatusBarWidget
import com.jakubjirak.k8sguard.ui.SwitchContextAction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration

class GuardLogicTest {

    private fun info(env: Environment, confirm: Boolean = false, name: String = "ctx") =
        ContextInfo(KubeContext(name, "cluster-1", "ns", "alice"), env, confirm)

    @Test
    fun `warns only when entering production with notifications enabled`() {
        val prod = info(Environment.PRODUCTION)
        val dev = info(Environment.DEVELOPMENT)
        assertTrue(ProductionGuardService.shouldWarn(dev, prod, enabled = true, notifyOnProduction = true))
        assertTrue(ProductionGuardService.shouldWarn(null, prod, enabled = true, notifyOnProduction = true))
        assertFalse(ProductionGuardService.shouldWarn(prod, prod, enabled = true, notifyOnProduction = true))
        assertFalse(ProductionGuardService.shouldWarn(dev, prod, enabled = true, notifyOnProduction = false))
        assertFalse(ProductionGuardService.shouldWarn(dev, prod, enabled = false, notifyOnProduction = true))
        assertFalse(ProductionGuardService.shouldWarn(prod, dev, enabled = true, notifyOnProduction = true))
    }

    @Test
    fun `production or confirm rules require confirmation before switching`() {
        assertTrue(SwitchContextAction.needsConfirmation(info(Environment.PRODUCTION)))
        assertTrue(SwitchContextAction.needsConfirmation(info(Environment.STAGING, confirm = true)))
        assertFalse(SwitchContextAction.needsConfirmation(info(Environment.DEVELOPMENT)))
    }

    @Test
    fun `effective rules order custom, team, defaults and skip blank patterns`() {
        val custom = listOf(ContextRule("*acme*", Environment.DEVELOPMENT), ContextRule(" ", Environment.PRODUCTION))
        val team = listOf(ContextRule("*acme-live*", Environment.PRODUCTION))
        val rules = K8sGuardSettings.effectiveRules(custom, team)
        assertEquals("*acme*", rules[0].pattern)
        assertEquals("*acme-live*", rules[1].pattern)
        assertEquals(2 + ContextRuleEngine.DEFAULT_RULES.size, rules.size)
        // custom rule wins over the team rule and the built-in "*live*" production rule
        assertEquals(Environment.DEVELOPMENT, ContextRuleEngine.classify(KubeContext("acme-live", "c"), rules).environment)
        assertEquals(
            Environment.PRODUCTION,
            ContextRuleEngine.classify(KubeContext("x-acme-live", "c"), K8sGuardSettings.effectiveRules(emptyList(), team)).environment
        )
    }

    @Test
    fun `serializable rules round-trip and tolerate unknown environments`() {
        val rule = ContextRule("*prd*", Environment.PRODUCTION, confirmActions = true, reminderInterval = Duration.ofMinutes(15))
        assertEquals(rule, SerializableContextRule.fromContextRule(rule).toContextRule())
        assertEquals(Environment.UNKNOWN, SerializableContextRule("x", "NOPE").toContextRule().environment)
        assertNull(SerializableContextRule("x", "STAGING").toContextRule().reminderInterval)
    }

    @Test
    fun `tooltip lists context details`() {
        val tooltip = ContextStatusBarWidget.tooltip(info(Environment.PRODUCTION, confirm = true, name = "prod-eu"))
        assertTrue(tooltip.contains("Context: prod-eu"))
        assertTrue(tooltip.contains("Cluster: cluster-1"))
        assertTrue(tooltip.contains("User: alice"))
        assertTrue(tooltip.contains("Environment: Production"))
        assertTrue(tooltip.contains("asks for confirmation"))
        assertEquals("Kubernetes: No context detected", ContextStatusBarWidget.tooltip(null))
    }

    @Test
    fun `display text shows namespace only when not default`() {
        assertEquals("⎈ ctx (ns)", info(Environment.DEVELOPMENT).displayText)
        assertEquals("⎈ c", ContextInfo(KubeContext("c", "x"), Environment.UNKNOWN, false).displayText)
    }
}
