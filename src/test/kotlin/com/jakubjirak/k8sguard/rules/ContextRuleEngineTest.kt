package com.jakubjirak.k8sguard.rules

import com.jakubjirak.k8sguard.model.ContextRule
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.model.KubeContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ContextRuleEngineTest {

    @Test
    fun `classify production context by name`() {
        val ctx = KubeContext("prod-cluster", "my-cluster", "default")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.PRODUCTION, info.environment)
        assertTrue(info.requiresConfirmation)
    }

    @Test
    fun `classify staging context by name`() {
        val ctx = KubeContext("staging-app", "staging-cluster", "default")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.STAGING, info.environment)
    }

    @Test
    fun `classify dev context by name`() {
        val ctx = KubeContext("dev-local", "minikube", "default")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.DEVELOPMENT, info.environment)
        assertFalse(info.requiresConfirmation)
    }

    @Test
    fun `classify minikube as development`() {
        val ctx = KubeContext("minikube", "minikube", "default")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.DEVELOPMENT, info.environment)
    }

    @Test
    fun `classify docker-desktop as development`() {
        val ctx = KubeContext("docker-desktop", "docker-desktop", "default")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.DEVELOPMENT, info.environment)
    }

    @Test
    fun `classify unknown context`() {
        val ctx = KubeContext("my-random-cluster", "random", "ns")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.UNKNOWN, info.environment)
        assertFalse(info.requiresConfirmation)
    }

    @Test
    fun `custom rules take precedence over defaults`() {
        val ctx = KubeContext("my-safe-prod", "my-cluster", "default")
        val customRules = listOf(
            ContextRule("*safe*", Environment.DEVELOPMENT, false)
        )

        val info = ContextRuleEngine.classify(ctx, customRules)

        assertEquals(Environment.DEVELOPMENT, info.environment)
        assertFalse(info.requiresConfirmation)
    }

    @Test
    fun `production patterns with confirmation required`() {
        val prodNames = listOf("production-eu", "prod-west", "live-api", "prd-backend")
        for (name in prodNames) {
            val ctx = KubeContext(name, "cluster", "default")
            val info = ContextRuleEngine.classify(ctx)

            assertEquals(Environment.PRODUCTION, info.environment,
                "Expected PRODUCTION for context: $name")
            assertTrue(info.requiresConfirmation,
                "Expected confirmation for context: $name")
        }
    }

    @Test
    fun `staging patterns detected correctly`() {
        val stagingNames = listOf("staging-api", "stg-backend", "preprod-service", "pre-prod-eu", "uat-release")
        for (name in stagingNames) {
            val ctx = KubeContext(name, "cluster", "default")
            val info = ContextRuleEngine.classify(ctx)

            assertEquals(Environment.STAGING, info.environment,
                "Expected STAGING for context: $name")
        }
    }

    @Test
    fun `development patterns detected correctly`() {
        val devNames = listOf("dev-local", "development-api", "kind-cluster", "k3d-test", "local-dev")
        for (name in devNames) {
            val ctx = KubeContext(name, "cluster", "default")
            val info = ContextRuleEngine.classify(ctx)

            assertEquals(Environment.DEVELOPMENT, info.environment,
                "Expected DEVELOPMENT for context: $name")
        }
    }

    @Test
    fun `classify with empty rules returns unknown`() {
        val ctx = KubeContext("anything", "cluster", "default")
        val info = ContextRuleEngine.classify(ctx, emptyList())

        assertEquals(Environment.UNKNOWN, info.environment)
    }

    @Test
    fun `first matching rule wins`() {
        val ctx = KubeContext("prod-staging", "cluster", "default")
        val rules = listOf(
            ContextRule("*staging*", Environment.STAGING, false),
            ContextRule("*prod*", Environment.PRODUCTION, true)
        )

        val info = ContextRuleEngine.classify(ctx, rules)
        assertEquals(Environment.STAGING, info.environment)
    }

    @Test
    fun `glob patterns are case-insensitive`() {
        val ctx = KubeContext("PRODUCTION-EU", "cluster", "default")
        val info = ContextRuleEngine.classify(ctx)

        assertEquals(Environment.PRODUCTION, info.environment)
    }
}
