# KubeContext Safety — JetBrains Marketplace Listing

## Plugin Name
KubeContext Safety

## Tagline
Never accidentally deploy to production again — see your kubectl context at a glance.

## Short Description
Prevent accidental production deployments with real-time kubectl context display, color-coded environment indicators, and confirmation dialogs before risky context switches.

## Full Description

```html
<p>
  <b>Stop production incidents before they start.</b> KubeContext Safety displays your current
  kubectl context directly in the IDE status bar with color-coded environment indicators, so you always
  know which cluster you're targeting. A simple glance prevents costly mistakes.
</p>

<h2>Key Features</h2>
<ul>
  <li><b>Real-time status bar widget</b> — displays your current kubectl context at all times</li>
  <li><b>Color-coded environment indicators</b> — 🔴 production, 🟡 staging, 🟢 development — instantly recognizable</li>
  <li><b>Production confirmation guard</b> — mandatory confirmation dialog when switching to production contexts</li>
  <li><b>Quick context switching</b> — change kubectl context without leaving the IDE (<code>Ctrl+Shift+K</code>)</li>
  <li><b>17 built-in pattern rules</b> — auto-classifies contexts including prod, staging, dev, minikube, k3d, docker-desktop, and more</li>
  <li><b>Kubeconfig file monitoring</b> — watches <code>~/.kube/config</code> for external changes in real time</li>
  <li><b>Context details tooltip</b> — hover to see cluster, namespace, and user information</li>
  <li><b>Balloon notifications</b> — alerts on production context switches</li>
</ul>

<h2>Free Tier</h2>
<p>Everything you need to stay safe — no credit card required:</p>
<ul>
  <li>Status bar widget with current context display</li>
  <li>Color-coded environment indicators (production, staging, development)</li>
  <li>Production confirmation guard dialog</li>
  <li>Quick context switching (<code>Ctrl+Shift+K</code>)</li>
  <li>17 built-in context classification patterns</li>
  <li>Kubeconfig file monitoring</li>
  <li>Context details tooltip</li>
  <li>Balloon notifications on production switches</li>
</ul>

<h2>Pro Tier ($1.90/month)</h2>
<p>Designed for teams who manage multiple clusters and need full control:</p>
<ul>
  <li><b>Custom context rules</b> — unlimited user-defined pattern → environment mappings</li>
  <li><b>Team-shared rules</b> — define classification rules in <code>.k8s-guard.yaml</code> and commit to your repo</li>
  <li><b>Visual border indicator</b> — red border around the entire IDE when connected to production</li>
  <li><b>Audit logging</b> — track every context switch with timestamps</li>
  <li><b>Configurable reminders</b> — periodic reminders when you've been on production for too long (default: every 30 minutes)</li>
  <li><b>Namespace switching</b> — manage namespaces within contexts directly from the IDE</li>
</ul>

<h2>Getting Started</h2>
<ol>
  <li><b>Install</b> — search "KubeContext Safety" in <code>Settings → Plugins</code> and click Install</li>
  <li><b>Verify</b> — look at the status bar to see your current kubectl context with color coding</li>
  <li><b>Switch safely</b> — press <code>Ctrl+Shift+K</code> to switch contexts with production confirmation protection</li>
</ol>

<h2>Compatibility</h2>
<ul>
  <li>IntelliJ IDEA 2024.3+ and all JetBrains IDEs</li>
  <li>macOS, Linux, and Windows</li>
  <li>Requires <code>kubectl</code> on PATH and a valid kubeconfig</li>
</ul>

<h2>Part of the Developer Productivity Suite</h2>
<p>
  KubeContext Safety is part of a comprehensive developer safety toolkit. Combine it with
  <b>Terminal Safety Rails</b> (intercept dangerous terminal commands),
  <b>Local Secrets Tripwire</b> (prevent secret leaks in code),
  <b>Architecture Breadcrumbs</b> (embed architectural decisions in code), and
  <b>ADR Companion</b> (manage Architecture Decision Records)
  for complete development workflow protection.
</p>
```

## Tags
kubernetes, kubectl, devops, production-safety, context-switching

## Category
DevOps

## Pricing
| Tier | Price | Features |
|------|-------|----------|
| Free | $0 | Status bar widget, color-coded indicators, production guard, quick switch, 17 built-in patterns, kubeconfig monitoring, notifications |
| Pro | $1.90/month or $19/year | Custom rules, team-shared rules, visual border indicator, audit logging, configurable reminders, namespace switching |

## Screenshots Needed
1. **Status bar widget** — showing the kubectl context with color-coded environment indicator (green for dev, red for production)
2. **Production confirmation dialog** — the modal dialog that appears when switching to a production context
3. **Quick context switch popup** — the dropdown list of available contexts triggered by Ctrl+Shift+K
4. **Visual border indicator (Pro)** — the red border around the IDE when connected to a production cluster
5. **Settings panel** — the plugin configuration page showing custom rules and pattern settings
