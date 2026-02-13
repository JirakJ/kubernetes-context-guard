package com.jakubjirak.k8sguard.git

import com.intellij.openapi.diagnostic.Logger

/**
 * Maps git branch patterns to Kubernetes cluster contexts.
 * Supports glob-style patterns and provides auto-switch recommendations.
 */
class BranchClusterMapper(
    private val mappings: List<BranchClusterMapping> = DEFAULT_MAPPINGS,
) {

    fun findMapping(branchName: String): BranchClusterMapping? {
        return mappings.firstOrNull { matchesBranchPattern(branchName, it.branchPattern) }
    }

    fun getStatus(branchName: String, currentContext: String): BranchClusterStatus {
        val mapping = findMapping(branchName)
        val expectedContext = mapping?.targetContext

        val matchStatus = when {
            expectedContext == null -> MatchStatus.UNMAPPED
            expectedContext == currentContext -> MatchStatus.MATCHED
            else -> MatchStatus.MISMATCHED
        }

        return BranchClusterStatus(
            currentBranch = branchName,
            currentContext = currentContext,
            expectedContext = expectedContext,
            matchStatus = matchStatus,
        )
    }

    companion object {
        private val LOG = Logger.getInstance(BranchClusterMapper::class.java)

        val DEFAULT_MAPPINGS: List<BranchClusterMapping> = listOf(
            BranchClusterMapping("feature/*", "dev-cluster", autoSwitch = false),
            BranchClusterMapping("develop", "staging-cluster", autoSwitch = false),
            BranchClusterMapping("main", "staging-cluster", requireConfirmation = true),
            BranchClusterMapping("master", "staging-cluster", requireConfirmation = true),
            BranchClusterMapping("release/*", "prod-cluster", requireConfirmation = true),
            BranchClusterMapping("hotfix/*", "prod-cluster", requireConfirmation = true),
        )

        /**
         * Matches a branch name against a glob-style pattern.
         * Supports `*` as wildcard within a single path segment.
         */
        fun matchesBranchPattern(branchName: String, pattern: String): Boolean {
            val regex = buildString {
                append("^")
                for (ch in pattern) {
                    when (ch) {
                        '*' -> append("[^/]*")
                        '?' -> append("[^/]")
                        '.', '(', ')', '[', ']', '{', '}', '\\', '^', '$', '|', '+' -> {
                            append("\\")
                            append(ch)
                        }
                        else -> append(ch)
                    }
                }
                append("$")
            }
            return try {
                branchName.matches(Regex(regex))
            } catch (e: Exception) {
                LOG.warn("Invalid branch pattern: $pattern", e)
                false
            }
        }
    }
}

data class BranchClusterMapping(
    val branchPattern: String,
    val targetContext: String,
    val targetNamespace: String? = null,
    val autoSwitch: Boolean = false,
    val requireConfirmation: Boolean = true,
)

data class BranchClusterStatus(
    val currentBranch: String,
    val currentContext: String,
    val expectedContext: String?,
    val matchStatus: MatchStatus,
)

enum class MatchStatus {
    MATCHED,
    MISMATCHED,
    UNMAPPED,
}
