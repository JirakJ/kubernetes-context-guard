package com.jakubjirak.k8sguard.model

import java.time.Duration

/**
 * A rule that classifies a Kubernetes context into an environment.
 */
data class ContextRule(
    val pattern: String,
    val environment: Environment,
    val confirmActions: Boolean = false,
    val reminderInterval: Duration? = null,
)
