# Kubernetes Context Guard — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 0: Plugin Ikona & Branding

- [ ] Navrhnout plugin ikonu (SVG + PNG)
  - [ ] Koncept: ⎈ K8s wheel se štítem
  - [ ] Hlavní ikona: 40×40 SVG pro Marketplace listing
  - [ ] Toolbar ikona: 16×16 + 13×13 SVG (light + dark varianta)
  - [ ] Tool window ikona: 13×13 SVG
  - [ ] `src/main/resources/META-INF/pluginIcon.svg` (40×40, light theme)
  - [ ] `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark theme)
  - [ ] Action ikony pro toolbar/menu (16×16 SVG, light + dark)
- [ ] Barevná paleta konzistentní s DevOps Safety Kit brandem
- [ ] Marketplace banner obrázek (1280×800)
- [ ] Screenshoty pro Marketplace listing (min 3, 1280×800 PNG)

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — IntelliJ Platform 2024.3+, JVM 17
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
  - [ ] Dependency: SnakeYAML (kubeconfig parsing)
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jakubjirak.k8sguard`
  - [ ] Závislosti: `com.intellij.modules.platform`
- [ ] Balíčková struktura: `com.jakubjirak.k8sguard.*`
  - [ ] `.model` — datové třídy
  - [ ] `.config` — kubeconfig parser
  - [ ] `.watcher` — file watcher
  - [ ] `.rules` — context rule engine
  - [ ] `.ui` — status bar, popup, dialog
  - [ ] `.settings` — konfigurace
- [ ] `src/main/resources/messages/K8sGuardBundle.properties`
- [ ] `.gitignore` (build/, .idea/, .gradle/, .kotlin/, out/)
- [ ] `gradle/libs.versions.toml` — version catalog
- [ ] `LICENSE` (Apache-2.0)
- [ ] `.editorconfig` (Kotlin style conventions)
- [ ] Detekt konfigurace (`detekt.yml`)
- [ ] GitHub Actions CI workflow (`.github/workflows/build.yml`)
- [ ] Ověřit `./gradlew build`

## Fáze 2: Datový model

- [ ] `KubeContext.kt`
  - [ ] `name: String`, `cluster: String`, `namespace: String`, `user: String`
- [ ] `ContextInfo.kt`
  - [ ] `context: KubeContext`
  - [ ] `environment: Environment` (PRODUCTION, STAGING, DEVELOPMENT, UNKNOWN)
  - [ ] `color: Color`, `requiresConfirmation: Boolean`
  - [ ] `reminderInterval: Duration?`
- [ ] `Environment.kt` — enum s default barvami (RED, YELLOW, GREEN, GRAY)
- [ ] `ContextRule.kt`
  - [ ] `pattern: String` (glob: `*prod*`, `*prd*`, `*live*`)
  - [ ] `environment: Environment`
  - [ ] `confirmActions: Boolean`
  - [ ] `reminderInterval: Duration?`
- [ ] Unit testy

## Fáze 3: Kubeconfig Parser

- [ ] `KubeConfigParser.kt`
  - [ ] `fun parse(configFile: File): KubeConfig`
  - [ ] Parsování `~/.kube/config` (YAML)
  - [ ] Extrakce: contexts, current-context, clusters, users
  - [ ] Podpora KUBECONFIG env var (multiple config files, merge)
- [ ] `KubeConfig.kt`
  - [ ] `contexts: List<KubeContext>`, `currentContext: String`
- [ ] `KubeConfigLocator.kt`
  - [ ] `fun getConfigPaths(): List<Path>`
  - [ ] Default: `~/.kube/config`
  - [ ] KUBECONFIG env var: split by `:` (Unix) / `;` (Windows)
- [ ] Unit testy (fixture kubeconfig soubory — single, multi, malformed)

## Fáze 4: Config File Watcher

- [ ] `KubeConfigWatcher.kt`
  - [ ] VirtualFileListener na kubeconfig soubor(y)
  - [ ] Fallback: polling každé 2 sekundy (pro external file changes)
  - [ ] Při detekci změny → re-parse → update UI
  - [ ] Events: `ContextChangedEvent`
- [ ] `KubeConfigService.kt` — application-level service
  - [ ] Singleton: aktuální kontext, listener registrace
  - [ ] Registrace jako `ApplicationService` v `plugin.xml`
- [ ] Unit testy (mock file changes)

## Fáze 5: Context Rule Engine

- [ ] `ContextRuleEngine.kt`
  - [ ] `fun classify(context: KubeContext, rules: List<ContextRule>): ContextInfo`
  - [ ] Glob pattern matching na context name
  - [ ] Default pravidla:
    - [ ] `*prod*`, `*prd*`, `*production*`, `*live*` → PRODUCTION (🔴)
    - [ ] `*staging*`, `*stg*`, `*stage*`, `*preprod*` → STAGING (🟡)
    - [ ] Vše ostatní → DEVELOPMENT (🟢)
  - [ ] Priorita: custom rules > team rules > default rules
- [ ] `TeamRulesLoader.kt`
  - [ ] Načtení `.k8s-guard.yaml` z projekt root
  - [ ] File watcher
- [ ] Unit testy (pattern matching, priority, edge cases)

## Fáze 6: Status Bar Widget

- [ ] `ContextStatusBarWidget.kt` implements `StatusBarWidget.MultipleTextValuesPresentation`
  - [ ] Registrace: `StatusBarWidgetFactory` v `plugin.xml`
  - [ ] Zobrazení: `⎈ context-name (namespace)`
  - [ ] Barva pozadí dle environment (🔴/🟡/🟢)
  - [ ] Update při `ContextChangedEvent`
- [ ] Click action → `ContextSwitchPopup`
- [ ] Tooltip: cluster, namespace, environment, user

## Fáze 7: Context Switch Popup

- [ ] `ContextSwitchPopup.kt`
  - [ ] JBPopup s listou dostupných kontextů
  - [ ] Barevné kódování per kontext
  - [ ] Aktuální kontext zvýrazněný
  - [ ] Výběr → spustí `kubectl config use-context <name>`
  - [ ] Namespace sub-menu (pokud podporováno)
- [ ] Klávesová zkratka (registrace v `plugin.xml`)

## Fáze 8: Production Guard Dialog

- [ ] `ProductionGuardService.kt`
  - [ ] Listener na context change → pokud nový kontext je PRODUCTION:
    - [ ] Balloon notification: „⚠️ You are now connected to PRODUCTION cluster"
    - [ ] Pokud `confirmActions=true`: modální dialog
  - [ ] Auto-reminder: periodický balloon na prod kontextu (interval z rule)
- [ ] `ProductionGuardDialog.kt` extends `DialogWrapper`
  - [ ] „You are about to work on PRODUCTION cluster: {name}"
  - [ ] „Continue" / „Switch Context" tlačítka
  - [ ] Checkbox: „Don't remind for this session"

## Fáze 9: Konfigurace

- [ ] `K8sGuardSettings.kt` implements `PersistentStateComponent`
  - [ ] Custom rules (pattern, environment, confirm, reminder)
  - [ ] Global enable/disable
  - [ ] Reminder interval default
  - [ ] Kubeconfig path override
- [ ] `K8sGuardConfigurable.kt` — Settings → Tools → K8s Context Guard
  - [ ] Rules tabulka (pattern, environment dropdown, confirm checkbox)
  - [ ] Add/Edit/Remove rule
  - [ ] Kubeconfig path
  - [ ] Enable/disable toggle
- [ ] Unit testy

## Fáze 10: Freemium & Licensing Infrastructure

- [ ] Integrace JetBrains Marketplace Licensing API
  - [ ] `LicenseChecker.kt` — ověření licence přes `com.intellij.ide.plugins.PluginManager` a Marketplace API
  - [ ] `LicenseStatus.kt` — enum: FREE, TRIAL, PAID, EXPIRED
  - [ ] `FreemiumGate.kt` — centrální guard pro paid features
    - [ ] `fun isPaidFeatureAvailable(featureId: String): Boolean`
    - [ ] Graceful degradation — paid UI elementy skryté/disabled ve free tieru
  - [ ] Trial period: 30 dní plný přístup
- [ ] `plugin.xml` — `<product-descriptor>` element pro Marketplace licensing
  - [ ] `code` atribut (unique product code)
  - [ ] `release-date`, `release-version`
- [ ] Nag screen / upgrade prompt
  - [ ] Non-intrusive balloon notification při pokusu o paid feature
  - [ ] „Upgrade to Pro" action → Marketplace stránka
  - [ ] Max 1× denně (ne spam)
- [ ] Feature flags
  - [ ] `FeatureRegistry.kt` — definice free vs. paid features
  - [ ] Každý paid feature má `@PaidFeature("feature.id")` anotaci nebo guard check
- [ ] Unit testy pro licensing logic (mock Marketplace API)

## Fáze 11: Production-Grade Quality

- [ ] Error Handling & Resilience
  - [ ] Globální exception handler (`ErrorReportSubmitter` implementace)
  - [ ] Graceful degradation — plugin nesmí crashnout IDE
  - [ ] Try-catch na všech EP entry pointech
  - [ ] Timeout na všech I/O operacích (network, git, file system)
  - [ ] Rate limiting na API volání
- [ ] Logging
  - [ ] `Logger` instance v každé třídě (IntelliJ `Logger.getInstance()`)
  - [ ] Structured logging: DEBUG pro vývoj, INFO pro operace, WARN/ERROR pro problémy
  - [ ] Žádné citlivé údaje v logech
- [ ] Telemetrie (opt-in, anonymní)
  - [ ] `PluginUsageTracker.kt` — FUS (Feature Usage Statistics) integrace
  - [ ] Metriky: aktivace featury, error rate, latence
  - [ ] Registrace FUS group v `plugin.xml`
- [ ] Performance
  - [ ] Profiling na velkých projektech (10k+ souborů)
  - [ ] Background thread pro všechny I/O operace (nesmí blokovat EDT)
  - [ ] Read/write action správně (ReadAction, WriteAction, smart mode check)
  - [ ] Disposal pattern: `Disposable` pro všechny listeners a services
  - [ ] Memory leak check (WeakReference kde potřeba)
- [ ] Testing — production úroveň
  - [ ] Unit testy: ≥80 % coverage core logiky
  - [ ] Integrační testy: `BasePlatformTestCase` pro PSI, inspections, EP
  - [ ] UI testy: `HeavyPlatformTestCase` pro tool windows, dialogy
  - [ ] Performance testy: benchmark na velkých souborech
- [ ] CI/CD Pipeline
  - [ ] GitHub Actions workflow:
    - [ ] `./gradlew check` — kompilace + testy
    - [ ] `./gradlew verifyPlugin` — plugin strukturální validace
    - [ ] `./gradlew verifyPluginProjectConfiguration` — metadata validace
    - [ ] `./gradlew runPluginVerifier` — binární kompatibilita s target IDE verzemi
    - [ ] Qodana scan (JetBrains code quality)
  - [ ] Release workflow:
    - [ ] Tag-based release (push tag → build → publish)
    - [ ] `./gradlew publishPlugin` — automatický upload na Marketplace
    - [ ] Changelog generace z git commitů
  - [ ] Dependabot / Renovate pro dependency updates
- [ ] Kódová kvalita
  - [ ] Kotlin coding conventions (official JetBrains style)
  - [ ] Detekt nebo ktlint pro statickou analýzu
  - [ ] Žádné deprecated API (IntelliJ Platform compatibility)
  - [ ] `@ApiStatus.Experimental` / `@ApiStatus.Internal` anotace
- [ ] Dokumentace
  - [ ] KDoc pro všechny public API
  - [ ] README.md: instalace, konfigurace, screenshoty, FAQ
  - [ ] CHANGELOG.md (Keep a Changelog format)
  - [ ] CONTRIBUTING.md (pokud open source)

## Fáze 12: Polish & Release

- [ ] Ukázkový `.k8s-guard.yaml`
- [ ] README.md s screenshoty
- [ ] `./gradlew verifyPlugin`, `./gradlew buildPlugin`
- [ ] Manuální testování s reálným kubeconfig
- [ ] Cross-IDE test
- [ ] Cross-OS test (macOS, Linux — kubeconfig paths)
- [ ] Marketplace listing (DevOps Safety Kit branding)
- [ ] Git tag `v2026.2.1`

## Fáze 13: Git Enhancement — Branch-to-Cluster Mapping

- [ ] `BranchClusterMapper.kt` — pattern matching branch → cluster
- [ ] `BranchChangeListener.kt` — Git4Idea listener pro checkout events
- [ ] `ClusterSwitchSuggestion.kt` — balloon notification s „Switch Context" action
- [ ] `MismatchWarningService.kt` — periodická kontrola branch-cluster konzistence
- [ ] `BranchClusterMapping.kt`, `BranchClusterStatus.kt`, `MatchStatus.kt` — datový model
- [ ] Default mapping: `feature/*` → dev, `main` → staging, `release/*` → prod
- [ ] Team-shared mapping přes `.k8s-guard.yaml` v repo root
- [ ] Confirmation dialog při přepnutí na production cluster
- [ ] Unit testy pro pattern matching a mismatch detection

## Fáze 14: Git Enhancement — Gitflow-Aware Safety

- [ ] `GitflowSafetyEngine.kt` — mapování branch type → safety level
- [ ] `DestructiveOperationDetector.kt` — regex matching na kubectl příkazy
- [ ] `TerminalCommandInterceptor.kt` — IntelliJ Terminal API listener
- [ ] `AuditLogService.kt` — persisted log K8s operací
- [ ] `CooldownTimer.kt` — timer po context switch, blokuje operace po dobu cooldown
- [ ] `BranchSafetyRule.kt`, `SafetyLevel.kt`, `AuditLogEntry.kt` — datový model
- [ ] Safety levels: LOW (feature), MEDIUM (develop), HIGH (main), CRITICAL (release/hotfix)
- [ ] Double confirmation pro destructive operace na CRITICAL úrovni
- [ ] Unit testy pro safety engine a operation detection

## Fáze 15: Git Enhancement — Deployment Tracking

- [ ] `DeploymentTagScanner.kt` — `git tag -l "deployed/*"` parser
- [ ] `DeploymentStatusService.kt` — project-level service, agregace per cluster
- [ ] `DeploymentDiffCalculator.kt` — `git log deployed/prod/latest..HEAD` pro unreleased changes
- [ ] `DeploymentTimelinePanel.kt` — vizualizace deploymentů v tool window
- [ ] `DeploymentGutterProvider.kt` — gutter ikony v Git log view
- [ ] `DeploymentInfo.kt`, `ClusterDeploymentStatus.kt`, `DeploymentTimeline.kt` — datový model
- [ ] Konfigurovatelný tag pattern: `deployed/{cluster}/{version}`
- [ ] „What's deployed?" quick action přes Search Everywhere
- [ ] Unit testy pro tag parsing a diff calculation

---

## Budoucí fáze (Paid)

- [ ] Quick switch keyboard shortcut (Ctrl+K, Ctrl+C)
- [ ] Favorite kontexty
- [ ] Context history (posledních 10)
- [ ] Visual border indikátor (červený rámeček na prod)
- [ ] Audit log (context switch log s timestamps)
- [ ] Time tracking na prod kontextu (alert po >2h)
- [ ] DevOps Safety Kit bundle integrace s Terminal Safety Rails
