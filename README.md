# KubeContext Safety

[![Version](https://img.shields.io/badge/version-2026.8.0-blue)](https://plugins.jetbrains.com/plugin/30204)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)
[![Build](https://github.com/JirakJ/kubernetes-context-guard/actions/workflows/build.yml/badge.svg)](https://github.com/JirakJ/kubernetes-context-guard/actions/workflows/build.yml)

> Prominently displays your current kubectl context in the IDE status bar with color-coded environment indicators and production safeguards.

## ✨ Features

- **Status bar widget** — always see which Kubernetes cluster you're connected to
- **Color-coded environments** — 🔴 production, 🟡 staging, 🟢 development at a glance
- **Production guard** — confirmation dialog when switching to production context
- **Quick context switch** — change kubectl context without leaving the IDE
- **17 built-in rules** — auto-classifies contexts by name patterns (prod, staging, dev, minikube, k3d, etc.)
- **Kubeconfig monitoring** — file watcher with 2-second polling on `~/.kube/config`
- **Notifications** — balloon alerts on production context switches
- **Custom context rules** — define your own pattern → environment mappings
- **Team-shared rules** — share context classification via `.k8s-guard.yaml` in your project
- **Audit logging** — track context switch history
- **Reminder intervals** — configurable reminders for long-running production sessions

## 📦 Installation

### From JetBrains Marketplace
1. Open **Settings → Plugins → Marketplace**
2. Search for **"KubeContext Safety"**
3. Click **Install**

### From ZIP
1. Download the latest release from [Releases](https://github.com/JirakJ/kubernetes-context-guard/releases)
2. Open **Settings → Plugins → ⚙️ → Install Plugin from Disk**
3. Select the downloaded ZIP file

## 🚀 Quick Start

1. Install the plugin and restart your IDE
2. The status bar widget appears automatically showing your current kubectl context
3. The context is color-coded based on built-in rules:
   - 🔴 **Red** — production contexts (`*prod*`, `*prd*`, `*live*`)
   - 🟡 **Orange** — staging contexts (`*staging*`, `*stg*`, `*uat*`, `*preprod*`)
   - 🟢 **Green** — development contexts (`*dev*`, `*local*`, `*minikube*`, `*kind*`, `*docker-desktop*`, `*test*`, `*k3d*`)
4. Click the status bar widget or use `Ctrl+Shift+K` to switch contexts
5. When switching to a production context, a confirmation dialog will appear

## ⚙️ Configuration

**Settings → Tools → KubeContext Safety**

| Setting | Default | Description |
|---------|---------|-------------|
| Enable plugin | `true` | Enable or disable the plugin entirely |
| Show notification on production switch | `true` | Show a balloon notification when switching to a production context |
| Kubeconfig path override | *(empty)* | Custom path to kubeconfig file (defaults to `~/.kube/config` or `KUBECONFIG` env var) |
| Custom rules | *(empty)* | Table of custom context classification rules (pattern, environment, confirm actions, reminder interval) |

## 🎯 Usage

### Status Bar Widget
The widget displays the current kubectl context name with a colored indicator matching the detected environment. Hover over it to see a tooltip with full context details (cluster, namespace, user).

### Quick Context Switch
Press `Ctrl+Shift+K` or go to **Tools → Switch Kubernetes Context** to open a popup list of all available contexts. Select a context to switch to it — the plugin executes `kubectl config use-context` under the hood.

### Production Guard
When you switch to a context classified as production, a confirmation dialog appears requiring explicit approval before the switch completes.

### Team-Shared Rules
Place a `.k8s-guard.yaml` file in your project root to define shared context classification rules for your team. Custom rules take priority over built-in defaults.

### Built-in Context Patterns

| Pattern | Environment | Requires Confirmation |
|---------|-------------|----------------------|
| `*preprod*`, `*pre-prod*`, `*staging*`, `*stg*`, `*stage*`, `*uat*` | 🟡 Staging | No |
| `*prod*`, `*prd*`, `*production*`, `*live*` | 🔴 Production | Yes |
| `*dev*`, `*local*`, `*minikube*`, `*kind*`, `*docker-desktop*`, `*test*`, `*k3d*` | 🟢 Development | No |

## 🔌 Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+Shift+K` | Switch Kubernetes Context |

## 📋 Requirements

- IntelliJ IDEA 2024.3+ (or any JetBrains IDE based on IntelliJ Platform)
- Java 17+
- `kubectl` installed and available on PATH
- Valid kubeconfig file (`~/.kube/config` or set via `KUBECONFIG` env var)

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/kubernetes-context-guard.git
cd kubernetes-context-guard
./gradlew build
```

## 🤝 Part of DevOps Safety Kit

KubeContext Safety pairs with [Terminal Safety Rails](https://github.com/JirakJ/terminal-safety-rails) and [Local Secrets Tripwire](https://github.com/JirakJ/local-secrets-tripwire) for comprehensive protection against accidental production changes and secret leaks.

## 👤 Author

**Jakub Jirák**
- GitHub: [@JirakJ](https://github.com/JirakJ)

## Free & open source

As of version 2026.8.0 this plugin is completely free — every feature is unlocked and no license is needed. The source code is available at https://github.com/JirakJ/kubernetes-context-guard under the Apache License 2.0.

## Contributing

Issues and pull requests are welcome — please open them on GitHub.

## License

Copyright 2026 Jakub Jirák. Licensed under the [Apache License, Version 2.0](LICENSE).
