package com.jakubjirak.k8sguard.config

import com.intellij.openapi.application.ApplicationManager
import com.jakubjirak.k8sguard.settings.K8sGuardSettings
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Locates kubeconfig files the way kubectl does: the path override from Settings first,
 * then every existing file listed in `KUBECONFIG`, then `~/.kube/config`.
 */
object KubeConfigLocator {

    private const val DEFAULT_FILENAME = "config"
    private const val KUBE_DIR = ".kube"
    private const val KUBECONFIG_ENV = "KUBECONFIG"

    fun getConfigPaths(): List<Path> = resolvePaths(
        override = settingsOverride(),
        kubeconfigEnv = System.getenv(KUBECONFIG_ENV),
        home = System.getProperty("user.home"),
        pathSeparator = File.pathSeparator,
    )

    /** Pure resolution logic; [exists] is injectable for tests. */
    fun resolvePaths(
        override: String?,
        kubeconfigEnv: String?,
        home: String,
        pathSeparator: String,
        exists: (Path) -> Boolean = { Files.isRegularFile(it) },
    ): List<Path> {
        override?.trim()?.takeIf { it.isNotEmpty() }?.let { path ->
            return listOf(Paths.get(path)).filter(exists)
        }
        val fromEnv = kubeconfigEnv.orEmpty().split(pathSeparator)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { Paths.get(it) }
            .filter(exists)
            .distinct()
        if (fromEnv.isNotEmpty()) return fromEnv
        return listOf(Paths.get(home, KUBE_DIR, DEFAULT_FILENAME)).filter(exists)
    }

    fun getDefaultConfigPath(): Path = Paths.get(System.getProperty("user.home"), KUBE_DIR, DEFAULT_FILENAME)

    private fun settingsOverride(): String? =
        if (ApplicationManager.getApplication() != null) K8sGuardSettings.getInstance().kubeconfigPathOverride else null
}
