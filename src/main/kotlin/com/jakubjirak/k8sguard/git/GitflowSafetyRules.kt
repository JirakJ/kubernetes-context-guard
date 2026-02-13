package com.jakubjirak.k8sguard.git

import com.intellij.openapi.diagnostic.Logger
import java.time.Instant

/**
 * Provides safety rules based on the current Git branch type following Gitflow conventions.
 * Different branch types map to different safety levels for Kubernetes operations.
 */
class GitflowSafetyRules(
    private val rules: List<BranchSafetyRule> = DEFAULT_RULES,
) {

    fun getSafetyLevel(branchName: String): SafetyLevel {
        return findRule(branchName)?.safetyLevel ?: SafetyLevel.MEDIUM
    }

    fun findRule(branchName: String): BranchSafetyRule? {
        return rules.firstOrNull { BranchClusterMapper.matchesBranchPattern(branchName, it.branchPattern) }
    }

    fun requiresConfirmation(branchName: String): Boolean {
        return findRule(branchName)?.requireConfirmation ?: true
    }

    fun requiresDoubleConfirmation(branchName: String): Boolean {
        return findRule(branchName)?.requireDoubleConfirmation ?: false
    }

    fun isAuditRequired(branchName: String): Boolean {
        return findRule(branchName)?.auditLogEnabled ?: false
    }

    companion object {
        @Suppress("unused")
        private val LOG = Logger.getInstance(GitflowSafetyRules::class.java)

        val DEFAULT_RULES: List<BranchSafetyRule> = listOf(
            BranchSafetyRule(
                branchPattern = "feature/*",
                safetyLevel = SafetyLevel.LOW,
                requireConfirmation = false,
            ),
            BranchSafetyRule(
                branchPattern = "develop",
                safetyLevel = SafetyLevel.MEDIUM,
                requireConfirmation = false,
            ),
            BranchSafetyRule(
                branchPattern = "main",
                safetyLevel = SafetyLevel.HIGH,
                requireConfirmation = true,
            ),
            BranchSafetyRule(
                branchPattern = "master",
                safetyLevel = SafetyLevel.HIGH,
                requireConfirmation = true,
            ),
            BranchSafetyRule(
                branchPattern = "release/*",
                safetyLevel = SafetyLevel.CRITICAL,
                requireConfirmation = true,
                requireDoubleConfirmation = true,
                auditLogEnabled = true,
            ),
            BranchSafetyRule(
                branchPattern = "hotfix/*",
                safetyLevel = SafetyLevel.CRITICAL,
                requireConfirmation = true,
                requireDoubleConfirmation = true,
                auditLogEnabled = true,
            ),
        )
    }
}

data class BranchSafetyRule(
    val branchPattern: String,
    val safetyLevel: SafetyLevel,
    val requireConfirmation: Boolean,
    val requireDoubleConfirmation: Boolean = false,
    val auditLogEnabled: Boolean = false,
    val cooldownSeconds: Int = 0,
    val blockedOperations: List<String> = emptyList(),
)

enum class SafetyLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
}

data class AuditLogEntry(
    val timestamp: Instant,
    val branch: String,
    val context: String,
    val namespace: String,
    val operation: String,
    val user: String,
    val safetyLevel: SafetyLevel,
    val approved: Boolean,
)
