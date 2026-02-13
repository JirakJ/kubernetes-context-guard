# Changelog

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
- Configurable settings page (Settings → Tools → Kubernetes Context Guard)
- Custom kubeconfig path override (supports `KUBECONFIG` env var)
- Custom context classification rules (pattern, environment, confirm actions, reminder interval)
- Team-shared rules via `.k8s-guard.yaml` project file
- Persistent settings stored in `k8sGuardSettings.xml`
- Kubeconfig YAML/JSON parser
- Shannon-entropy–based context name matching
- Tooltip with full context details (cluster, namespace, user)
- Part of DevOps Safety Kit bundle
