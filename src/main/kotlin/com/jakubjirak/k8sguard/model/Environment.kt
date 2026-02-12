package com.jakubjirak.k8sguard.model

import java.awt.Color

/**
 * Environment classification for a Kubernetes context.
 */
enum class Environment(
    val displayName: String,
    val color: Color,
    val darkColor: Color,
    val icon: String,
) {
    PRODUCTION("Production", Color(0xE5, 0x39, 0x35), Color(0xEF, 0x53, 0x50), "🔴"),
    STAGING("Staging", Color(0xFB, 0x8C, 0x00), Color(0xFF, 0xA7, 0x26), "🟡"),
    DEVELOPMENT("Development", Color(0x43, 0xA0, 0x47), Color(0x66, 0xBB, 0x6A), "🟢"),
    UNKNOWN("Unknown", Color(0x75, 0x75, 0x75), Color(0xBD, 0xBD, 0xBD), "⚪"),
}
