package com.jakubjirak.k8sguard.rules

import com.jakubjirak.k8sguard.model.ContextRule
import com.jakubjirak.k8sguard.model.Environment
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.time.Duration

/**
 * Loads team-shared context rules from .k8s-guard.yaml in the project root.
 */
object TeamRulesLoader {

    private val LOG = Logger.getInstance(TeamRulesLoader::class.java)
    private const val CONFIG_FILENAME = ".k8s-guard.yaml"

    fun loadFromDirectory(directory: Path): List<ContextRule> {
        val configFile = directory.resolve(CONFIG_FILENAME).toFile()
        if (!configFile.exists()) return emptyList()
        return try {
            parseRules(configFile.readText())
        } catch (e: Exception) {
            LOG.warn("Failed to load team rules from $directory/$CONFIG_FILENAME", e)
            emptyList()
        }
    }

    fun findConfigFile(project: Project): VirtualFile? {
        return project.guessProjectDir()?.findChild(CONFIG_FILENAME)
    }

    fun loadRules(project: Project): List<ContextRule> {
        val configFile = findConfigFile(project) ?: return emptyList()
        return try {
            val content = String(configFile.contentsToByteArray())
            parseRules(content)
        } catch (e: Exception) {
            LOG.warn("Failed to load team rules from $CONFIG_FILENAME", e)
            emptyList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun parseRules(yamlContent: String): List<ContextRule> {
        val yaml = Yaml()
        val data = yaml.load<Map<String, Any?>>(yamlContent) ?: return emptyList()
        val contexts = (data["rules"] as? List<Map<String, Any?>>)
            ?: (data["contexts"] as? List<Map<String, Any?>>)
            ?: return emptyList()

        return contexts.mapNotNull { entry ->
            try {
                val pattern = entry["pattern"] as? String ?: return@mapNotNull null
                val envName = (entry["environment"] as? String)?.uppercase() ?: "UNKNOWN"
                val environment = try {
                    Environment.valueOf(envName)
                } catch (_: IllegalArgumentException) {
                    Environment.UNKNOWN
                }
                val confirmActions = (entry["confirmActions"] as? Boolean)
                    ?: (entry["confirm_actions"] as? Boolean)
                    ?: (environment == Environment.PRODUCTION)
                val reminderStr = entry["reminder_interval"] as? String
                val reminderInterval = reminderStr?.let { parseDuration(it) }

                ContextRule(
                    pattern = pattern,
                    environment = environment,
                    confirmActions = confirmActions,
                    reminderInterval = reminderInterval,
                )
            } catch (e: Exception) {
                LOG.debug("Skipping malformed context rule: $entry", e)
                null
            }
        }
    }

    private fun parseDuration(value: String): Duration? {
        return try {
            val trimmed = value.trim().lowercase()
            when {
                trimmed.endsWith("m") -> Duration.ofMinutes(trimmed.dropLast(1).toLong())
                trimmed.endsWith("h") -> Duration.ofHours(trimmed.dropLast(1).toLong())
                trimmed.endsWith("s") -> Duration.ofSeconds(trimmed.dropLast(1).toLong())
                else -> Duration.ofMinutes(trimmed.toLong())
            }
        } catch (e: Exception) {
            LOG.debug("Failed to parse duration: $value", e)
            null
        }
    }
}
