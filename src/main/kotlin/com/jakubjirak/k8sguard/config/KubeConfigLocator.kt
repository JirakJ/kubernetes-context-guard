package com.jakubjirak.k8sguard.config

import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Locates kubeconfig files on the system.
 * Supports KUBECONFIG environment variable and default ~/.kube/config location.
 */
object KubeConfigLocator {

    private val LOG = Logger.getInstance(KubeConfigLocator::class.java)

    private const val DEFAULT_FILENAME = "config"
    private const val KUBE_DIR = ".kube"
    private const val KUBECONFIG_ENV = "KUBECONFIG"

    fun getConfigPaths(): List<Path> {
        val envValue = System.getenv(KUBECONFIG_ENV)
        if (!envValue.isNullOrBlank()) {
            val separator = if (System.getProperty("os.name").lowercase().contains("win")) ";" else ":"
            val paths = envValue.split(separator)
                .map { Paths.get(it.trim()) }
                .filter { it.toFile().exists() }
            if (paths.isNotEmpty()) {
                LOG.info("Using KUBECONFIG paths: $paths")
                return paths
            }
        }

        val defaultPath = getDefaultConfigPath()
        if (defaultPath.toFile().exists()) {
            return listOf(defaultPath)
        }

        LOG.info("No kubeconfig found")
        return emptyList()
    }

    fun getDefaultConfigPath(): Path {
        val home = System.getProperty("user.home")
        return Paths.get(home, KUBE_DIR, DEFAULT_FILENAME)
    }

    fun getPrimaryConfigFile(): File? {
        return getConfigPaths().firstOrNull()?.toFile()
    }
}
