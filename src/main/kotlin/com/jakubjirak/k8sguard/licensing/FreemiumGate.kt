package com.jakubjirak.k8sguard.licensing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

/**
 * Central gate for freemium feature access.
 * Free tier: status bar widget, notifications, 3 default environment patterns.
 * Paid tier: custom patterns, quick switch, audit log, visual border.
 */
@Service(Service.Level.APP)
class FreemiumGate {

    enum class LicenseStatus {
        FREE, TRIAL, PAID, EXPIRED
    }

    /**
     * Check if a paid feature is available for the current license.
     */
    @Suppress("UNUSED_PARAMETER")
    fun isPaidFeatureAvailable(featureId: String): Boolean {
        val status = getLicenseStatus()
        return status == LicenseStatus.PAID || status == LicenseStatus.TRIAL
    }

    fun getLicenseStatus(): LicenseStatus {
        // TODO: Integrate with JetBrains Marketplace Licensing API
        // For now, return FREE — all paid features gated behind this check
        return LicenseStatus.FREE
    }

    fun getMaxCustomRules(): Int {
        return when (getLicenseStatus()) {
            LicenseStatus.FREE -> MAX_FREE_CUSTOM_RULES
            LicenseStatus.EXPIRED -> MAX_FREE_CUSTOM_RULES
            LicenseStatus.TRIAL, LicenseStatus.PAID -> Int.MAX_VALUE
        }
    }

    companion object {
        const val MAX_FREE_CUSTOM_RULES = 3

        // Feature IDs
        const val FEATURE_QUICK_SWITCH = "quick_switch"
        const val FEATURE_AUDIT_LOG = "audit_log"
        const val FEATURE_VISUAL_BORDER = "visual_border"
        const val FEATURE_UNLIMITED_RULES = "unlimited_rules"
        const val FEATURE_FAVORITES = "favorites"
        const val FEATURE_CONTEXT_HISTORY = "context_history"

        fun getInstance(): FreemiumGate =
            ApplicationManager.getApplication().getService(FreemiumGate::class.java)
    }
}
