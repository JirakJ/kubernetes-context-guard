<h2>KubeContext Safety — Color-Coded Kubernetes Context Guard for Your IDE</h2>

<p>
One wrong <code>kubectl</code> command in the wrong context can take down production. KubeContext Safety adds a color-coded context indicator to your IDE status bar and requires confirmation before switching to production environments — giving your team a critical safety net.
</p>

<h3>Context Awareness</h3>
<ul>
  <li>Status bar widget showing current kubectl context name at all times</li>
  <li>Color-coded environment indicators: red (production), yellow (staging), green (development)</li>
  <li>Tooltip with full context details: cluster, namespace, user</li>
  <li>Automatic kubeconfig monitoring with 2-second polling</li>
</ul>

<h3>Safety Features</h3>
<ul>
  <li>Confirmation dialog before switching to production contexts</li>
  <li>Balloon notifications on production context switches</li>
  <li>17 built-in rules auto-classify contexts (prod, staging, dev, minikube, kind, k3d, etc.)</li>
  <li>Configurable reminder intervals for long production sessions (Pro)</li>
</ul>

<h3>Productivity</h3>
<ul>
  <li>Quick context switch with Ctrl+Shift+K keyboard shortcut</li>
  <li>Context switch popup listing all available contexts</li>
  <li>No terminal needed to change kubectl context</li>
</ul>

<h3>Team Features</h3>
<ul>
  <li>Custom context classification rules with pattern matching</li>
  <li>Team-shared rules via <code>.k8s-guard.yaml</code> in the project root</li>
  <li>Audit logging of context switch history (Pro)</li>
</ul>

<h3>Pricing</h3>
<ul>
  <li><b>Free:</b> Status bar widget, color-coded contexts, production guard, quick switching, 17 built-in rules</li>
  <li><b>Pro ($1.90/month):</b> Custom rules, team-shared configuration, audit logging, production session reminders</li>
</ul>

<h3>Requirements</h3>
<ul>
  <li>IntelliJ IDEA 2024.3+ (or any JetBrains IDE based on IntelliJ Platform)</li>
  <li>kubectl installed and available on PATH</li>
  <li>Valid kubeconfig file (~/.kube/config or KUBECONFIG env var)</li>
  <li>Java 17+</li>
</ul>

<h3>Support</h3>
<ul>
  <li>GitHub: <a href="https://github.com/JirakJ/kubernetes-context-guard">github.com/JirakJ/kubernetes-context-guard</a></li>
  <li>Issues: <a href="https://github.com/JirakJ/kubernetes-context-guard/issues">Report a Bug</a></li>
  <li>Author: Jakub Jirák</li>
</ul>
