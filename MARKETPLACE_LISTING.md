# JetBrains Marketplace Listing — KubeContext Safety

---

## 📝 Plugin Information

| Field       | Value                            |
|-------------|----------------------------------|
| **Name**    | KubeContext Safety               |
| **Plugin ID** | com.jakubjirak.kubecontextsafety |
| **Version** | 2026.2.15                        |
| **Category** | DevOps                          |
| **Tags**    | kubernetes, kubectl, devops, production-safety, context-switching |

---

## 🎯 Short Description

> Never accidentally deploy to production again. Real-time kubectl context indicator in the status bar with color-coded environments, production guard dialogs, and quick context switching.

*(195 characters)*

---

## 📖 Full Description

### 🛡️ Never Run `kubectl` Against Production by Accident Again

**The Problem:** A single misplaced `kubectl apply` against the wrong cluster can take down production. Kubernetes context mistakes are the #1 cause of accidental production incidents. Terminal prompts and shell aliases help — but they don't protect you inside the IDE.

**The Solution:** KubeContext Safety puts a real-time, color-coded kubectl context indicator in your IDE status bar. Production contexts glow 🔴 red with mandatory confirmation dialogs, staging contexts show 🟡 yellow, and development contexts stay 🟢 green. You always know exactly which cluster you're targeting.

---

### ✨ Key Features

#### 🚦 Color-Coded Status Bar Widget
- **🔴 Production** — `*prod*`, `*prd*`, `*live*` contexts highlighted in red
- **🟡 Staging** — `*staging*`, `*stg*`, `*uat*`, `*preprod*` contexts in yellow
- **🟢 Development** — `*dev*`, `*local*`, `*minikube*`, `*kind*`, `*docker-desktop*`, `*test*`, `*k3d*` in green
- **17 built-in patterns** covering the most common context naming conventions

#### 🛑 Production Guard Dialog
- Mandatory confirmation when switching to a production context
- Prevents accidental `kubectl` commands against live clusters
- Balloon notifications on every production context switch

#### ⌨️ Quick Context Switch
- **Ctrl+Shift+K** — switch kubectl context without leaving the IDE
- Context details tooltip showing cluster, namespace, and user info

#### 📡 Live kubeconfig Monitoring
- Watches `~/.kube/config` (or `KUBECONFIG` env var) with 2-second polling
- Automatic updates when context changes externally (e.g., via terminal `kubectl config use-context`)

#### 🎨 Visual IDE Border Indicator
- **Red border** around the entire IDE window when connected to a production context
- Impossible to miss — even with multiple IDE windows

#### 🔧 Custom Context Rules
- Define unlimited custom pattern → environment mappings
- Override built-in classifications for your organization's naming conventions
- Team-shared rules via `.k8s-guard.yaml` committed to your repository

#### ⏰ Configurable Reminders
- Periodic reminders when connected to production (default: every 30 minutes)
- Never forget you're still on the production cluster

#### 📝 Audit Logging
- Timestamped log of all context switches
- Track who switched to production and when

#### 🔀 Namespace Switching
- Switch namespaces within a context without leaving the IDE

---

### 💚 Free & open source

All features are free for everyone — no license needed. Source code under Apache-2.0: https://github.com/JirakJ/kubernetes-context-guard

---

### 🎯 Perfect For

- **DevOps engineers** managing multiple Kubernetes clusters
- **Platform teams** enforcing production safety across the organization
- **Developers** who access staging and production clusters from their IDE
- **SREs** who need audit trails for cluster access
- Part of the **DevOps Safety Kit** alongside Terminal Safety Rails & Local Secrets Tripwire

---

### ⚙️ Requirements

| Requirement       | Value                            |
|-------------------|----------------------------------|
| **IDE Version**   | IntelliJ IDEA 2024.3+ (build 232+) |
| **Java Version**  | 17+                             |
| **External Tools** | `kubectl` installed on PATH     |
| **Configuration** | Valid kubeconfig (`~/.kube/config` or `KUBECONFIG` env var) |
| **OS**            | macOS, Linux, Windows            |

---

### 🚀 Quick Start

1. **Install** the plugin from JetBrains Marketplace
2. Ensure **kubectl** is installed and configured with at least one context
3. Check the **status bar** — your current context appears with a colored indicator
4. Press **Ctrl+Shift+K** to switch contexts
5. Try switching to a production context — the guard dialog activates!
6. Hover over the status bar widget for cluster, namespace, and user details

---

## 📸 Screenshots & Media

| # | Screenshot Description                                                       |
|---|-----------------------------------------------------------------------------|
| 1 | **Status Bar Widget** — Color-coded kubectl context indicator (green for dev, red for prod) |
| 2 | **Production Guard Dialog** — Confirmation prompt when switching to a production context |
| 3 | **Quick Context Switcher** — Ctrl+Shift+K popup listing available contexts with environment colors |
| 4 | **Context Details Tooltip** — Hover tooltip showing cluster, namespace, and user information |
| 5 | **Visual IDE Border** — Red border around the entire IDE window on production context |
| 6 | **Custom Rules Configuration** — Settings panel for custom pattern → environment mappings |
| 7 | **Balloon Notification** — Warning notification on production context switch |
| 8 | **Team Shared Config** — `.k8s-guard.yaml` file with team-wide context classification rules |

---

## 🏷️ Marketplace Categories

| Type          | Category                    |
|---------------|-----------------------------|
| **Primary**   | DevOps                      |
| **Secondary** | Cloud & Infrastructure, Productivity |

---

## 🔍 Keywords (SEO)

`kubernetes`, `kubectl`, `kubeconfig`, `context switching`, `production safety`, `kubernetes context`, `cluster management`, `devops safety`, `kubectl context`, `k8s`, `production guard`, `namespace switching`, `kubernetes IDE`, `cluster indicator`, `devops plugin`, `kubernetes status bar`

---

## 📋 Compatibility

| IDE               | Compatible |
|-------------------|------------|
| IntelliJ IDEA     | ✅          |
| WebStorm          | ✅          |
| PyCharm           | ✅          |
| GoLand            | ✅          |
| Rider             | ✅          |
| PhpStorm          | ✅          |
| CLion             | ✅          |
| RubyMine          | ✅          |
| Android Studio    | ✅          |

**Minimum Version:** IntelliJ Platform 2024.3+ (build 232)

---

## 📞 Vendor Information

| Field        | Value                          |
|--------------|--------------------------------|
| **Vendor**   | Jakub Jirak                    |
| **Email**    | jirak@jakubjirak.com           |
| **Website**  | https://jakubjirak.com         |
| **Support**  | support@jakubjirak.com         |

---

## ✅ Pre-Launch Checklist

- [ ] Plugin icon (SVG, 40×40px) — designed and included
- [ ] Plugin description finalized (< 10,000 characters)
- [ ] Short description finalized (< 255 characters)
- [ ] Screenshots captured (minimum 5, 1280×800px recommended)
- [ ] Plugin signed with JetBrains Marketplace certificate
- [ ] Changelog written for initial release
- [ ] Vendor verified on JetBrains Marketplace
- [ ] Compatibility range tested (2024.3+)
- [ ] kubectl requirement documented
- [ ] Support URL / email configured
- [ ] README and documentation published
- [ ] Landing page live at vendor website
- [ ] Social media announcements prepared
