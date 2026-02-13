package com.jakubjirak.k8sguard.git

import com.intellij.openapi.diagnostic.Logger
import java.time.Instant

/**
 * Tracks deployments by parsing git tags following the pattern `deploy/{cluster}/{version}`.
 * Provides deployment status per cluster and identifies unreleased commits.
 */
class DeploymentTracker(
    private val tagPattern: String = DEFAULT_TAG_PATTERN,
) {

    fun parseDeploymentTag(tag: String): DeploymentInfo? {
        val regex = tagPattern
            .replace("{cluster}", "(?<cluster>[^/]+)")
            .replace("{version}", "(?<version>.+)")
            .let { Regex("^$it$") }

        val match = regex.matchEntire(tag) ?: return null

        return try {
            DeploymentInfo(
                commitHash = "",
                cluster = match.groups["cluster"]?.value ?: return null,
                tag = tag,
                version = match.groups["version"]?.value ?: return null,
                timestamp = Instant.now(),
            )
        } catch (e: Exception) {
            LOG.warn("Failed to parse deployment tag: $tag", e)
            null
        }
    }

    fun parseDeploymentTags(tags: List<String>): List<DeploymentInfo> {
        return tags.mapNotNull { parseDeploymentTag(it) }
    }

    fun getLatestPerCluster(deployments: List<DeploymentInfo>): Map<String, DeploymentInfo> {
        return deployments
            .groupBy { it.cluster }
            .mapValues { (_, entries) -> entries.maxBy { it.timestamp } }
    }

    companion object {
        private val LOG = Logger.getInstance(DeploymentTracker::class.java)

        const val DEFAULT_TAG_PATTERN = "deploy/{cluster}/{version}"
    }
}

data class DeploymentInfo(
    val commitHash: String,
    val cluster: String,
    val tag: String,
    val version: String,
    val timestamp: Instant,
    val deployer: String? = null,
)
