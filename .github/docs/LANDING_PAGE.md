# KubeContext Safety

## Never Run kubectl Against the Wrong Cluster Again

> Color-coded Kubernetes context indicator with production safeguards — right in your IDE status bar.

---

## The Problem

One wrong `kubectl` command in the wrong cluster can take down production. It happens more often than anyone admits. Developers switch between dev, staging, and production contexts all day. A moment of distraction, and `kubectl delete` runs against the wrong cluster. There's no undo button for production outages.

---

## Key Features

### Status Bar Widget
Always see which Kubernetes cluster you're connected to. The widget sits in your IDE status bar and updates in real-time. No more running `kubectl config current-context` in the terminal.

### Color-Coded Environments
Contexts are color-coded at a glance: red for production, yellow for staging, green for development. You'll know immediately if you're connected to the wrong environment.

### Production Guard
When you switch to a production context, a confirmation dialog appears requiring explicit approval. No more accidental production access.

### Quick Context Switch
Change your kubectl context without leaving the IDE. Press `Ctrl+Shift+K` or click the status bar widget to see all available contexts and switch instantly.

### 17 Built-in Classification Rules
The plugin auto-classifies contexts by name patterns. Contexts containing "prod", "prd", or "live" are marked as production. "staging", "stg", or "uat" are staging. "dev", "local", "minikube", "kind", or "docker-desktop" are development.

### Kubeconfig Monitoring
A file watcher monitors your `~/.kube/config` (or custom path) with 2-second polling. When external tools change your context, the widget updates automatically.

### Team-Shared Rules
Place a `.k8s-guard.yaml` in your project root to define shared context classification rules for your team. Custom rules override built-in defaults.

### Custom Context Rules
Define your own pattern-to-environment mappings for any naming convention your organization uses. Set confirmation requirements and reminder intervals per context.

---

## How It Works

1. **Install the plugin** — Search for "KubeContext Safety" in the JetBrains Marketplace.
2. **See your context** — The status bar widget appears automatically, showing your current kubectl context with a color indicator.
3. **Switch safely** — Press `Ctrl+Shift+K` to switch contexts. Production switches require confirmation.
4. **Customize rules** — Open Settings → Tools → KubeContext Safety to add custom classification rules or share rules via `.k8s-guard.yaml`.

---

## Pricing

### Free Tier — $0
- Status bar widget with context name
- Color-coded environment indicators (production, staging, development)
- Production guard confirmation dialog
- Quick context switch (Ctrl+Shift+K)
- 17 built-in classification rules
- Kubeconfig file monitoring
- Balloon notifications on production switches

### Pro Tier — $1.90/month
- Custom context classification rules
- Team-shared rules via `.k8s-guard.yaml`
- Audit logging of context switch history
- Configurable reminder intervals for production sessions

---

## Testimonials

> *"KubeContext Safety has prevented at least two production incidents on our team. The red indicator is impossible to miss."*
> — **Platform Engineer, E-Commerce Company**

> *"Simple and effective. The production confirmation dialog is exactly what we needed."*
> — **DevOps Engineer, SaaS Startup**

> *"We made it mandatory for all developers to install this plugin after someone ran a delete command in the wrong cluster."*
> — **Engineering Manager, Cloud Infrastructure Team**

---

## FAQ

**Q: Which JetBrains IDEs are supported?**
A: Any JetBrains IDE based on IntelliJ Platform 2024.3 or later — IntelliJ IDEA, GoLand, WebStorm, PyCharm, and more.

**Q: Does it require kubectl to be installed?**
A: Yes. The plugin uses kubectl to read and switch contexts. kubectl must be available on your PATH.

**Q: Can I use a custom kubeconfig path?**
A: Yes. Go to Settings → Tools → KubeContext Safety and set a custom kubeconfig path. The plugin also respects the KUBECONFIG environment variable.

**Q: Does it work with EKS, GKE, and AKS contexts?**
A: Yes. The plugin works with any kubectl context regardless of the cloud provider. Context classification is based on naming patterns only.

**Q: Is the Free tier limited in any way?**
A: The Free tier includes all core safety features. The Pro tier adds custom rules, team sharing, audit logging, and production session reminders.

---

## Part of the DevOps Safety Kit

KubeContext Safety pairs with [Terminal Safety Rails](https://github.com/JirakJ/terminal-safety-rails) and [Local Secrets Tripwire](https://github.com/JirakJ/local-secrets-tripwire) for comprehensive protection against accidental production changes and secret leaks.

---

## Get Started

Install KubeContext Safety and add a critical safety net for your Kubernetes workflows.

[Install from Marketplace](https://plugins.jetbrains.com) | [View on GitHub](https://github.com/JirakJ/kubernetes-context-guard)
