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

    companion object {
        /**
         * Merges several kubeconfig files like kubectl does for `KUBECONFIG=a:b`: the first file that
         * sets `current-context` wins, and for duplicate context names the first definition wins.
         */
        fun merge(configs: List<KubeConfig>): KubeConfig? {
            if (configs.isEmpty()) return null
            return KubeConfig(
                currentContext = configs.map { it.currentContext }.firstOrNull { it.isNotEmpty() }.orEmpty(),
                contexts = configs.flatMap { it.contexts }.distinctBy { it.name },
            )
        }
    }
}
