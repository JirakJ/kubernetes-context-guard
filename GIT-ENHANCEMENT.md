# KubeContext Safety — Git Enhancement Specifikace

**Verze:** 2026.2.1
**Status:** Plánováno
**Závislosti:** Git4Idea API, `git branch`, `git tag`

---

## Přehled

Rozšíření pluginu o Git integraci pro automatické mapování Git větví na Kubernetes clustery,
bezpečnostní pravidla dle typu větve (Gitflow) a sledování deploymentů přes Git tagy.

---

## FR-GE-001: Branch-to-Cluster Mapping

### Popis

Automatické mapování Git branch patterns na Kubernetes clustery. Při přepnutí větve
se automaticky přepne nebo doporučí odpovídající K8s kontext.

### Funkční požadavky

- [ ] **FR-GE-001.1** — Konfigurovatelné mapování branch pattern → cluster/context
  - [ ] `feature/*` → dev cluster
  - [ ] `develop` → dev cluster
  - [ ] `main` / `master` → staging cluster
  - [ ] `release/*` → production cluster
  - [ ] `hotfix/*` → production cluster
- [ ] **FR-GE-001.2** — Auto-switch: při `git checkout` doporučení/přepnutí K8s kontextu
- [ ] **FR-GE-001.3** — Confirmation dialog při přepnutí na production cluster
- [ ] **FR-GE-001.4** — Mismatch warning: aktuální branch neodpovídá K8s kontextu
- [ ] **FR-GE-001.5** — Status bar: zobrazení branch → cluster mappingu
- [ ] **FR-GE-001.6** — Team-shared mapping přes `.k8s-guard.yaml` v repo root

### Acceptance Criteria

1. Checkout `feature/login` automaticky navrhne přepnutí na dev cluster
2. Checkout `release/2.0` zobrazí confirmation dialog pro production cluster
3. Mismatch warning se zobrazí pokud branch je `feature/*` ale kontext je production
4. Mapping je konfigurovatelný v settings i v `.k8s-guard.yaml`
5. Team config (`.k8s-guard.yaml`) má prioritu nad user settings
6. Auto-switch lze vypnout (default: doporučení, ne automatické přepnutí)

### Datový model — rozšíření

```kotlin
data class BranchClusterMapping(
    val branchPattern: String,         // glob: "feature/*", "main", "release/*"
    val targetContext: String,         // K8s context name
    val targetNamespace: String?,      // optional namespace override
    val autoSwitch: Boolean = false,   // true = auto, false = suggest only
    val requireConfirmation: Boolean = true
)

data class BranchClusterStatus(
    val currentBranch: String,
    val currentContext: String,
    val expectedContext: String?,
    val matchStatus: MatchStatus       // MATCHED, MISMATCHED, UNMAPPED
)

enum class MatchStatus {
    MATCHED,     // branch odpovídá kontextu
    MISMATCHED,  // branch neodpovídá kontextu → warning
    UNMAPPED     // branch nemá mapping → info only
}
```

### Technická implementace

- `BranchClusterMapper.kt` — pattern matching branch → cluster
- `BranchChangeListener.kt` — Git4Idea listener pro checkout events
- `ClusterSwitchSuggestion.kt` — balloon notification s „Switch Context" action
- `MismatchWarningService.kt` — periodická kontrola branch-cluster konzistence

---

## FR-GE-002: Gitflow-Aware Safety

### Popis

Různá bezpečnostní pravidla pro různé typy Git větví v kontextu Kubernetes operací.
Přísnější pravidla pro release/hotfix větve, volnější pro feature větve.

### Funkční požadavky

- [ ] **FR-GE-002.1** — Safety level per branch typ:
  - [ ] `feature/*` → LOW (volné operace, žádné potvrzení)
  - [ ] `develop` → MEDIUM (warning při destructive operacích)
  - [ ] `main`/`master` → HIGH (potvrzení pro všechny operace)
  - [ ] `release/*` → CRITICAL (double confirmation, audit log)
  - [ ] `hotfix/*` → CRITICAL (double confirmation, audit log)
- [ ] **FR-GE-002.2** — Destructive operation detection: `kubectl delete`, `kubectl apply --force`
- [ ] **FR-GE-002.3** — Terminal integration: interceptování kubectl příkazů v IDE terminálu
- [ ] **FR-GE-002.4** — Audit log: záznam všech K8s operací na HIGH/CRITICAL úrovni
- [ ] **FR-GE-002.5** — Cooldown period: po context switch na prod čekací doba před operacemi

### Acceptance Criteria

1. Na `feature/*` větvi nejsou žádná omezení pro dev cluster operace
2. Na `release/*` větvi se zobrazí double confirmation pro destructive operace
3. Audit log zaznamenává: timestamp, branch, context, operace, uživatel
4. Cooldown period je konfigurovatelný (default: 5 sekund po switch na prod)
5. Terminal integration funguje s vestavěným IDE terminálem
6. Safety pravidla jsou přepisovatelná v `.k8s-guard.yaml`

### Datový model — rozšíření

```kotlin
data class BranchSafetyRule(
    val branchPattern: String,
    val safetyLevel: SafetyLevel,
    val requireConfirmation: Boolean,
    val requireDoubleConfirmation: Boolean = false,
    val auditLogEnabled: Boolean = false,
    val cooldownSeconds: Int = 0,
    val blockedOperations: List<String> = emptyList()  // regex patterns
)

enum class SafetyLevel {
    LOW,       // feature branches — žádná omezení
    MEDIUM,    // develop — warning
    HIGH,      // main/master — confirmation
    CRITICAL   // release/hotfix — double confirmation + audit
}

data class AuditLogEntry(
    val timestamp: Instant,
    val branch: String,
    val context: String,
    val namespace: String,
    val operation: String,
    val user: String,
    val safetyLevel: SafetyLevel,
    val approved: Boolean
)
```

### Technická implementace

- `GitflowSafetyEngine.kt` — mapování branch type → safety level
- `DestructiveOperationDetector.kt` — regex matching na kubectl příkazy
- `TerminalCommandInterceptor.kt` — IntelliJ Terminal API listener
- `AuditLogService.kt` — persisted log (`.idea/k8s-guard-audit.log` nebo external file)
- `CooldownTimer.kt` — timer po context switch, blokuje operace po dobu cooldown

---

## FR-GE-003: Deployment Tracking

### Popis

Sledování, který commit je deployed na kterém clusteru. Využívá Git tagy pro mapování
verze → cluster a zobrazuje deployment stav v IDE.

### Funkční požadavky

- [ ] **FR-GE-003.1** — Git tag pattern matching: `deployed/{cluster}/{version}` → cluster mapping
- [ ] **FR-GE-003.2** — Zobrazení aktuálně deployed verze per cluster v tool window
- [ ] **FR-GE-003.3** — Diff view: aktuální HEAD vs. deployed verze (unreleased changes)
- [ ] **FR-GE-003.4** — Deployment timeline: vizualizace deploymentů přes čas per cluster
- [ ] **FR-GE-003.5** — Tag-based deployment marker: gutter ikona u commitů s deploy tagem
- [ ] **FR-GE-003.6** — „What's deployed?" quick action: zobrazí stav všech clusterů

### Acceptance Criteria

1. Tag `deployed/prod/v2.1.0` je správně rozpoznán jako deployment na prod cluster
2. Tool window zobrazuje: cluster → tag → commit hash → datum
3. Diff view ukazuje commity mezi deployed verzí a HEAD
4. Timeline je renderován s barevným kódováním per cluster
5. Gutter ikona se zobrazí u commitů s deploy tagem v Git log view
6. Quick action je dostupná přes Search Everywhere (Shift+Shift)

### Datový model — rozšíření

```kotlin
data class DeploymentInfo(
    val cluster: String,
    val namespace: String?,
    val tag: String,
    val version: String,
    val commitHash: String,
    val commitDate: Instant,
    val deployer: String?,           // tag author
    val deployedAt: Instant          // tag date
)

data class ClusterDeploymentStatus(
    val cluster: String,
    val currentDeployment: DeploymentInfo?,
    val unreleasedCommits: Int,      // commity mezi deployed a HEAD
    val lastDeployedAt: Instant?
)

data class DeploymentTimeline(
    val cluster: String,
    val deployments: List<DeploymentInfo>,
    val periodStart: Instant,
    val periodEnd: Instant
)
```

### Technická implementace

- `DeploymentTagScanner.kt` — `git tag -l "deployed/*"` parser
- `DeploymentStatusService.kt` — project-level service, aggregace per cluster
- `DeploymentDiffCalculator.kt` — `git log deployed/prod/latest..HEAD` pro unreleased
- `DeploymentTimelinePanel.kt` — vizualizace v tool window
- `DeploymentGutterProvider.kt` — gutter ikony v Git log view

---

## Společné technické požadavky

### Performance

- Branch change detection: event-driven, latence <100ms
- Tag scanning: cached, invalidace při `git fetch`/`git pull`
- Deployment status refresh: lazy, on-demand nebo při tool window otevření

### Konfigurace — rozšíření `K8sGuardSettings`

```kotlin
// Nová pole v existujícím Settings
val branchClusterMappingEnabled: Boolean = true
val branchClusterMappings: List<BranchClusterMapping> = listOf(
    BranchClusterMapping("feature/*", "dev-cluster", autoSwitch = false),
    BranchClusterMapping("develop", "dev-cluster", autoSwitch = false),
    BranchClusterMapping("main", "staging-cluster", requireConfirmation = true),
    BranchClusterMapping("release/*", "prod-cluster", requireConfirmation = true)
)
val gitflowSafetyEnabled: Boolean = true
val safetyRules: List<BranchSafetyRule> = emptyList()  // custom override
val auditLogEnabled: Boolean = false
val cooldownSeconds: Int = 5
val deploymentTrackingEnabled: Boolean = true
val deploymentTagPattern: String = "deployed/{cluster}/{version}"
```

### Freemium rozdělení

| Feature | Free | Paid |
|---------|------|------|
| Branch-to-cluster mapping (basic) | ✅ | ✅ |
| Auto-switch suggestion | ✅ | ✅ |
| Gitflow-aware safety | ❌ | ✅ |
| Audit log | ❌ | ✅ |
| Deployment tracking | ❌ | ✅ |
| Deployment timeline | ❌ | ✅ |
| Terminal command interception | ❌ | ✅ |
