package com.jakubjirak.k8sguard.config

import com.jakubjirak.k8sguard.model.KubeConfig
import com.jakubjirak.k8sguard.model.KubeContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class KubeConfigLocatorTest {

    private val home = "/home/me"
    private val defaultPath = Paths.get(home, ".kube", "config")

    private fun existing(vararg paths: String): (Path) -> Boolean {
        val set = paths.map { Paths.get(it) }.toSet()
        return { it in set }
    }

    @Test
    fun `settings override wins over KUBECONFIG and default`() {
        val paths = KubeConfigLocator.resolvePaths(" /custom/kc ", "/a:/b", home, ":", existing("/custom/kc", "/a", defaultPath.toString()))
        assertEquals(listOf(Paths.get("/custom/kc")), paths)
    }

    @Test
    fun `missing override yields nothing rather than silently falling back`() {
        assertTrue(KubeConfigLocator.resolvePaths("/missing", null, home, ":", existing(defaultPath.toString())).isEmpty())
    }

    @Test
    fun `KUBECONFIG lists existing files in order without duplicates`() {
        val paths = KubeConfigLocator.resolvePaths("", "/a: /missing ::/b:/a", home, ":", existing("/a", "/b"))
        assertEquals(listOf(Paths.get("/a"), Paths.get("/b")), paths)
    }

    @Test
    fun `windows separator is honoured`() {
        val paths = KubeConfigLocator.resolvePaths(null, "/a;/b", home, ";", existing("/a", "/b"))
        assertEquals(2, paths.size)
    }

    @Test
    fun `falls back to the default file, or nothing`() {
        assertEquals(listOf(defaultPath), KubeConfigLocator.resolvePaths(null, "", home, ":", existing(defaultPath.toString())))
        assertEquals(listOf(defaultPath), KubeConfigLocator.resolvePaths(null, "/missing", home, ":", existing(defaultPath.toString())))
        assertTrue(KubeConfigLocator.resolvePaths(null, null, home, ":", existing()).isEmpty())
    }

    @Test
    fun `merge keeps the first current-context and first definition of each context`() {
        val a = KubeConfig("", listOf(KubeContext("dev", "c1"), KubeContext("prod", "c2")))
        val b = KubeConfig("prod", listOf(KubeContext("prod", "other"), KubeContext("stage", "c3")))
        val c = KubeConfig("dev", emptyList())
        val merged = KubeConfig.merge(listOf(a, b, c))!!
        assertEquals("prod", merged.currentContext)
        assertEquals(listOf("dev", "prod", "stage"), merged.contexts.map { it.name })
        assertEquals("c2", merged.getCurrentContextInfo()!!.cluster)
        assertNull(KubeConfig.merge(emptyList()))
    }
}
