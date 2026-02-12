package com.jakubjirak.k8sguard.model

/**
 * Parsed kubeconfig file representation.
 */
data class KubeConfig(
    val currentContext: String,
    val contexts: List<KubeContext>,
) {
    fun getCurrentContextInfo(): KubeContext? =
        contexts.find { it.name == currentContext }
}
