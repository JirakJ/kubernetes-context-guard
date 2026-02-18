# KubeContext Safety — Screenshot Plan

## Screenshots

### 1. Status Bar Widget — Development Context
**What to capture:** The IDE status bar showing a green indicator with a development context name (e.g., "minikube" or "docker-desktop"). Include a portion of the IDE editor area for context.

### 2. Status Bar Widget — Production Context
**What to capture:** The IDE status bar showing a red indicator with a production context name (e.g., "prod-us-east-1"). The red color should be clearly visible and attention-grabbing.

### 3. Production Guard Confirmation Dialog
**What to capture:** The confirmation dialog that appears when switching to a production context. Show the warning message, the context name being switched to, and the Confirm/Cancel buttons.

### 4. Quick Context Switch Popup
**What to capture:** The context switch popup (triggered by Ctrl+Shift+K) showing a list of available contexts with color indicators. Include at least one production (red), one staging (yellow), and one development (green) context.

### 5. Settings Page
**What to capture:** The Settings → Tools → KubeContext Safety page showing plugin configuration: enable toggle, notification settings, kubeconfig path override, and the custom rules table.

### 6. Widget Tooltip with Context Details
**What to capture:** The tooltip that appears when hovering over the status bar widget, showing full context details: cluster name, namespace, user, and environment classification.

### 7. Production Switch Notification
**What to capture:** A balloon notification showing that the user has switched to a production context, with the context name and a warning message.

### 8. Team Config File
**What to capture:** A `.k8s-guard.yaml` file open in the editor showing custom context classification rules with pattern, environment, and confirmation settings.

---

## Video Demo Outline (35 seconds)

| Time | Scene | Description |
|------|-------|-------------|
| 0:00–0:05 | Intro | Show the plugin name. Point out the green status bar widget showing "minikube" context. |
| 0:05–0:12 | Context switch | Press Ctrl+Shift+K, show the popup with multiple color-coded contexts |
| 0:12–0:20 | Production guard | Select a production context, show the confirmation dialog, click Confirm |
| 0:20–0:25 | Red indicator | Show the status bar now displaying red with the production context name |
| 0:25–0:30 | Switch back | Switch back to a dev context, show the green indicator return |
| 0:30–0:35 | Outro | Show pricing (Free + $1.90/mo Pro) and install CTA |
