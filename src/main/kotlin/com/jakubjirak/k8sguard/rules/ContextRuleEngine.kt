package com.jakubjirak.k8sguard.rules

import com.jakubjirak.k8sguard.model.ContextInfo
import com.jakubjirak.k8sguard.model.ContextRule
import com.jakubjirak.k8sguard.model.Environment
import com.jakubjirak.k8sguard.model.KubeContext

/**
 * Classifies Kubernetes contexts into environments based on matching rules.
 * Rules are evaluated in order — first match wins.
 */
object ContextRuleEngine {

    val DEFAULT_RULES: List<ContextRule> = listOf(
        // Staging patterns first (more specific, to avoid *prod* matching "preprod")
        ContextRule("*preprod*", Environment.STAGING),
        ContextRule("*pre-prod*", Environment.STAGING),
        ContextRule("*staging*", Environment.STAGING),
        ContextRule("*stg*", Environment.STAGING),
        ContextRule("*stage*", Environment.STAGING),
        ContextRule("*uat*", Environment.STAGING),
        // Production patterns
        ContextRule("*prod*", Environment.PRODUCTION, confirmActions = true),
        ContextRule("*prd*", Environment.PRODUCTION, confirmActions = true),
        ContextRule("*production*", Environment.PRODUCTION, confirmActions = true),
        ContextRule("*live*", Environment.PRODUCTION, confirmActions = true),
        // Development patterns
        ContextRule("*dev*", Environment.DEVELOPMENT),
        ContextRule("*local*", Environment.DEVELOPMENT),
        ContextRule("*minikube*", Environment.DEVELOPMENT),
        ContextRule("*kind*", Environment.DEVELOPMENT),
        ContextRule("*docker-desktop*", Environment.DEVELOPMENT),
        ContextRule("*test*", Environment.DEVELOPMENT),
        ContextRule("*k3d*", Environment.DEVELOPMENT),
    )

    fun classify(context: KubeContext, rules: List<ContextRule> = DEFAULT_RULES): ContextInfo {
        for (rule in rules) {
            if (matchesGlob(context.name, rule.pattern)) {
                return ContextInfo(
                    context = context,
                    environment = rule.environment,
                    requiresConfirmation = rule.confirmActions,
                )
            }
        }
        return ContextInfo(
            context = context,
            environment = Environment.UNKNOWN,
            requiresConfirmation = false,
        )
    }

    /**
     * Simple glob matching supporting * as wildcard.
     * Pattern "*prod*" matches "my-prod-cluster".
     */
    fun matchesGlob(text: String, pattern: String): Boolean {
        val regex = buildString {
            append("^")
            for (ch in pattern) {
                when (ch) {
                    '*' -> append(".*")
                    '?' -> append(".")
                    '.', '(', ')', '[', ']', '{', '}', '\\', '^', '$', '|', '+' -> {
                        append("\\")
                        append(ch)
                    }
                    else -> append(ch)
                }
            }
            append("$")
        }
        return text.lowercase().matches(Regex(regex, RegexOption.IGNORE_CASE))
    }
}
