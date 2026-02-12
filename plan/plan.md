# Kubernetes Context Guard — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — IntelliJ Platform 2024.3+, JVM 17
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
  - [ ] Dependency: SnakeYAML (kubeconfig parsing)
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jirak.k8sguard`
  - [ ] Závislosti: `com.intellij.modules.platform`
- [ ] Balíčková struktura: `com.jirak.k8sguard.*`
  - [ ] `.model` — datové třídy
  - [ ] `.config` — kubeconfig parser
  - [ ] `.watcher` — file watcher
  - [ ] `.rules` — context rule engine
  - [ ] `.ui` — status bar, popup, dialog
  - [ ] `.settings` — konfigurace
- [ ] `src/main/resources/messages/K8sGuardBundle.properties`
- [ ] `.gitignore`
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

## Fáze 10: Polish & Release

- [ ] Ukázkový `.k8s-guard.yaml`
- [ ] README.md s screenshoty
- [ ] `./gradlew verifyPlugin`, `./gradlew buildPlugin`
- [ ] Manuální testování s reálným kubeconfig
- [ ] Cross-IDE test
- [ ] Cross-OS test (macOS, Linux — kubeconfig paths)
- [ ] Marketplace listing (DevOps Safety Kit branding)
- [ ] Git tag `v2026.2.1`

---

## Budoucí fáze (Paid)

- [ ] Quick switch keyboard shortcut (Ctrl+K, Ctrl+C)
- [ ] Favorite kontexty
- [ ] Context history (posledních 10)
- [ ] Visual border indikátor (červený rámeček na prod)
- [ ] Audit log (context switch log s timestamps)
- [ ] Time tracking na prod kontextu (alert po >2h)
- [ ] DevOps Safety Kit bundle integrace s Terminal Safety Rails
