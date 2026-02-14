# Changelog

## [2026.2.8] - 2026-02-14

### Fixed
- Removed deprecated API usage (postStartupActivity → projectActivity, StatusBarWidget.getPresentation)
- No experimental API usage


## [2026.2.7] - 2026-02-13

### Added
- Added ProGuard obfuscation for code protection


## [2026.2.6] - 2026-02-13

### Changed
- Version bump for JetBrains Marketplace publication


## [2026.2.5] - 2026-02-13

### Changed
- Renamed plugin from "Kubernetes Context Guard" to "KubeContext Safety"
- New plugin ID: `com.jakubjirak.kubecontextsafety`
- Resolves JetBrains Marketplace name and ID collision


## [2026.2.4] - 2026-02-13

### Changed
- Renamed plugin ID to `com.jakubjirak.kubernetescontextguard` (marketplace conflict resolution)


## [2026.2.3] - 2026-02-13

### Changed
- Expanded compatibility range: sinceBuild 232.0+ (IntelliJ 2023.2+)
- Removed upper version bound for future IDE compatibility


All notable changes to this project will be documented in this file.

## [2026.2.2] - 2026-02-13

### Added
- Initial release
- Status bar widget showing current kubectl context with color-coded environment indicator
- Color-coded environments: 🔴 production, 🟡 staging, 🟢 development
- Production guard with confirmation dialog when switching to production contexts
- Quick context switch popup via `Ctrl+Shift+K` (executes `kubectl config use-context`)
- 17 built-in context classification rules (glob patterns for prod, staging, dev, minikube, kind, k3d, etc.)
- Kubeconfig file watcher with 2-second polling interval
- Balloon notifications on production context switches
- Configurable settings page (Settings → Tools → KubeContext Safety)
- Custom kubeconfig path override (supports `KUBECONFIG` env var)
- Custom context classification rules (pattern, environment, confirm actions, reminder interval)
- Team-shared rules via `.k8s-guard.yaml` project file
- Persistent settings stored in `k8sGuardSettings.xml`
- Kubeconfig YAML/JSON parser
- Shannon-entropy–based context name matching
- Tooltip with full context details (cluster, namespace, user)
- Part of DevOps Safety Kit bundle
