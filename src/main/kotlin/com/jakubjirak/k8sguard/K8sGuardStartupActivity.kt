package com.jakubjirak.k8sguard

import com.jakubjirak.k8sguard.guard.ProductionGuardService
import com.jakubjirak.k8sguard.watcher.KubeConfigWatcher
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Starts the kubeconfig watcher and production guard service when the IDE opens.
 */
class K8sGuardStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        try {
            LOG.info("Kubernetes Context Guard starting up")

            val watcher = KubeConfigWatcher.getInstance()
            watcher.start()

            val guard = ProductionGuardService.getInstance()
            guard.start()

            LOG.info("Kubernetes Context Guard started successfully")
        } catch (e: Exception) {
            LOG.warn("Failed to start Kubernetes Context Guard", e)
        }
    }

    companion object {
        private val LOG = Logger.getInstance(K8sGuardStartupActivity::class.java)
    }
}
