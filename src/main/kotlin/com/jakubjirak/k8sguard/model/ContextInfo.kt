package com.jakubjirak.k8sguard.model

/**
 * Enriched context information with environment classification.
 */
data class ContextInfo(
    val context: KubeContext,
    val environment: Environment,
    val requiresConfirmation: Boolean,
) {
    val displayText: String
        get() = buildString {
            append("⎈ ")
            append(context.name)
            if (context.namespace != "default") {
                append(" (${context.namespace})")
            }
        }
}
