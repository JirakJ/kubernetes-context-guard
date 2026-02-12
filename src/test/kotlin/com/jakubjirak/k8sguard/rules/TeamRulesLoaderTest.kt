package com.jakubjirak.k8sguard.rules

import com.jakubjirak.k8sguard.model.ContextRule
import com.jakubjirak.k8sguard.model.Environment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class TeamRulesLoaderTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `load valid team rules file`() {
        tempDir.resolve(".k8s-guard.yaml").apply {
            writeText("""
                rules:
                  - pattern: "*our-prod*"
                    environment: PRODUCTION
                    confirmActions: true
                  - pattern: "*our-staging*"
                    environment: STAGING
                    confirmActions: false
            """.trimIndent())
        }

        val rules = TeamRulesLoader.loadFromDirectory(tempDir.toPath())

        assertEquals(2, rules.size)
        assertEquals("*our-prod*", rules[0].pattern)
        assertEquals(Environment.PRODUCTION, rules[0].environment)
        assertTrue(rules[0].confirmActions)
        assertEquals("*our-staging*", rules[1].pattern)
        assertEquals(Environment.STAGING, rules[1].environment)
    }

    @Test
    fun `load from directory without rules file returns empty`() {
        val rules = TeamRulesLoader.loadFromDirectory(tempDir.toPath())

        assertTrue(rules.isEmpty())
    }

    @Test
    fun `load malformed yaml returns empty`() {
        tempDir.resolve(".k8s-guard.yaml").apply {
            writeText("not: valid: rules: {{{")
        }

        val rules = TeamRulesLoader.loadFromDirectory(tempDir.toPath())

        assertTrue(rules.isEmpty())
    }

    @Test
    fun `load rules with missing fields uses defaults`() {
        tempDir.resolve(".k8s-guard.yaml").apply {
            writeText("""
                rules:
                  - pattern: "*test*"
            """.trimIndent())
        }

        val rules = TeamRulesLoader.loadFromDirectory(tempDir.toPath())

        assertEquals(1, rules.size)
        assertEquals("*test*", rules[0].pattern)
        assertEquals(Environment.UNKNOWN, rules[0].environment)
        assertFalse(rules[0].confirmActions)
    }
}
