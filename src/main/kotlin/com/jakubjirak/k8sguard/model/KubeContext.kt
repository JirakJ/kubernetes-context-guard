package com.jakubjirak.k8sguard.model

/**
 * Represents a single Kubernetes context from kubeconfig.
 */
data class KubeContext(
    val name: String,
    val cluster: String,
    val namespace: String = "default",
    val user: String = "",
)
