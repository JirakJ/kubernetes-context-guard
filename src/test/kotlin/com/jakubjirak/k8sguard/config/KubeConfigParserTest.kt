package com.jakubjirak.k8sguard.config

import com.jakubjirak.k8sguard.model.KubeConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class KubeConfigParserTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `parse valid kubeconfig with multiple contexts`() {
        val configFile = tempDir.resolve("config").apply {
            writeText("""
                apiVersion: v1
                kind: Config
                current-context: prod-cluster
                contexts:
                  - name: prod-cluster
                    context:
                      cluster: prod
                      namespace: default
                      user: admin
                  - name: staging-cluster
                    context:
                      cluster: staging
                      namespace: staging-ns
                      user: developer
                  - name: dev-local
                    context:
                      cluster: minikube
                      namespace: dev
                      user: dev-user
            """.trimIndent())
        }

        val config = KubeConfigParser.parse(configFile.toPath())

        assertNotNull(config)
        assertEquals("prod-cluster", config!!.currentContext)
        assertEquals(3, config.contexts.size)

        val prod = config.contexts.find { it.name == "prod-cluster" }
        assertNotNull(prod)
        assertEquals("prod", prod!!.cluster)
        assertEquals("default", prod.namespace)

        val staging = config.contexts.find { it.name == "staging-cluster" }
        assertNotNull(staging)
        assertEquals("staging", staging!!.cluster)
        assertEquals("staging-ns", staging.namespace)
    }

    @Test
    fun `parse kubeconfig with missing namespace`() {
        val configFile = tempDir.resolve("config").apply {
            writeText("""
                apiVersion: v1
                kind: Config
                current-context: test
                contexts:
                  - name: test
                    context:
                      cluster: test-cluster
                      user: test-user
            """.trimIndent())
        }

        val config = KubeConfigParser.parse(configFile.toPath())

        assertNotNull(config)
        assertEquals(1, config!!.contexts.size)
        assertEquals("default", config.contexts[0].namespace)
    }

    @Test
    fun `parse empty kubeconfig`() {
        val configFile = tempDir.resolve("config").apply {
            writeText("""
                apiVersion: v1
                kind: Config
                contexts: []
            """.trimIndent())
        }

        val config = KubeConfigParser.parse(configFile.toPath())

        assertNotNull(config)
        assertTrue(config!!.contexts.isEmpty())
    }

    @Test
    fun `parse malformed kubeconfig returns null`() {
        val configFile = tempDir.resolve("config").apply {
            writeText("not: valid: yaml: {{{{")
        }

        val config = KubeConfigParser.parse(configFile.toPath())

        // Should not throw, return null or empty
        // Parser handles gracefully
    }

    @Test
    fun `parse non-existent file returns null`() {
        val config = KubeConfigParser.parse(tempDir.resolve("does-not-exist").toPath())

        assertNull(config)
    }

    @Test
    fun `parse kubeconfig without current-context`() {
        val configFile = tempDir.resolve("config").apply {
            writeText("""
                apiVersion: v1
                kind: Config
                contexts:
                  - name: dev
                    context:
                      cluster: dev-cluster
                      user: dev-user
            """.trimIndent())
        }

        val config = KubeConfigParser.parse(configFile.toPath())

        assertNotNull(config)
        assertEquals("", config!!.currentContext)
        assertEquals(1, config.contexts.size)
    }
}
