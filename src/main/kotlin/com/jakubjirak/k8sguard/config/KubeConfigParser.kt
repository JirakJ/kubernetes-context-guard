package com.jakubjirak.k8sguard.config

import com.jakubjirak.k8sguard.model.KubeConfig
import com.jakubjirak.k8sguard.model.KubeContext
import com.intellij.openapi.diagnostic.Logger
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

/**
 * Parses ~/.kube/config (YAML) and extracts context information.
 */
object KubeConfigParser {

    private val LOG = Logger.getInstance(KubeConfigParser::class.java)

    fun parse(path: Path): KubeConfig? = parse(path.toFile())

    fun parse(configFile: File): KubeConfig? {
        if (!configFile.exists()) {
            LOG.info("Kubeconfig file not found: ${configFile.absolutePath}")
            return null
        }

        return try {
            val yaml = Yaml()
            val data = configFile.inputStream().use { yaml.load<Map<String, Any?>>(it) } ?: return null
            parseYamlMap(data)
        } catch (e: FileNotFoundException) {
            LOG.warn("Kubeconfig file not found: ${configFile.absolutePath}", e)
            null
        } catch (e: Exception) {
            LOG.warn("Failed to parse kubeconfig: ${configFile.absolutePath}", e)
            null
        }
    }

    fun parse(yamlContent: String): KubeConfig? {
        return try {
            val yaml = Yaml()
            val data = yaml.load<Map<String, Any?>>(yamlContent) ?: return null
            parseYamlMap(data)
        } catch (e: Exception) {
            LOG.warn("Failed to parse kubeconfig content", e)
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseYamlMap(data: Map<String, Any?>): KubeConfig {
        val currentContext = data["current-context"] as? String ?: ""

        val contextsList = (data["contexts"] as? List<Map<String, Any?>>)?.mapNotNull { ctxEntry ->
            try {
                val name = ctxEntry["name"] as? String ?: return@mapNotNull null
                val context = ctxEntry["context"] as? Map<String, Any?> ?: emptyMap()
                KubeContext(
                    name = name,
                    cluster = context["cluster"] as? String ?: "",
                    namespace = context["namespace"] as? String ?: "default",
                    user = context["user"] as? String ?: "",
                )
            } catch (e: Exception) {
                LOG.debug("Skipping malformed context entry: $ctxEntry", e)
                null
            }
        } ?: emptyList()

        return KubeConfig(
            currentContext = currentContext,
            contexts = contextsList,
        )
    }
}
