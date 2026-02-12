# Copilot Instructions — Kubernetes Context Guard

## Projekt
JetBrains IDE plugin — zobrazuje kubectl kontext ve status baru a varuje před akcemi na produkčním clusteru. Součást DevOps Safety Kit.

## Technický stack
- **Jazyk:** Kotlin (JVM 17+)
- **Build:** Gradle (Kotlin DSL) s `org.jetbrains.intellij.platform` plugin 2.x
- **Platform:** IntelliJ Platform SDK 2024.3+
- **Testování:** JUnit 5 + `BasePlatformTestCase`

## Build a testy
```bash
./gradlew build
./gradlew test
./gradlew test --tests "*.KubeConfigParserTest.testProductionDetection"
./gradlew runIde
./gradlew verifyPlugin
```

## Architektura
- **Config:** `KubeConfigParser` — parsuje `~/.kube/config` (YAML), extrahuje kontexty
- **Watcher:** `KubeConfigWatcher` — VirtualFileListener na kubeconfig soubor (polling 2s fallback)
- **Guard:** `ProductionGuardService` — vyhodnocuje context pravidla, triggeruje dialogy
- **UI:** `ContextStatusBarWidget` (StatusBarWidgetFactory), `ContextSwitchPopup` (quick switch), `GuardDialog` (potvrzení)
- **Rules:** `ContextRuleEngine` — pattern matching pro environment classification

## Konvence
- Balíčky: `com.jakubjirak.k8sguard.*`
- Environments: PRODUCTION (🔴), STAGING (🟡), DEVELOPMENT (🟢), UNKNOWN (⚪)
- Team rules v `.k8s-guard.yaml`
- Kubeconfig parsing přes SnakeYAML
- Texty v `messages/K8sGuardBundle.properties`
- Verzování: `2026.2.X`
- Bundle: DevOps Safety Kit (s Terminal Safety Rails)

## Production-grade požadavky
- Všechny I/O operace na background threadech (`Task.Backgroundable`, coroutines)
- Read/Write actions správně (`ReadAction.run {}`, `WriteAction.run {}`)
- `Disposable` pattern pro listeners a services
- Exception handling: žádné unhandled exceptions, graceful degradation
- Logging: `Logger.getInstance(MyClass::class.java)` v každé třídě
- EDT nesmí být blokován — žádné síťové/git/file operace na EDT
- Smart mode check (`DumbService.isDumb()`) kde potřeba

## Freemium architektura
- `FreemiumGate` centrální guard: `FreemiumGate.isPaidFeatureAvailable("feature.id")`
- JetBrains Marketplace Licensing API přes `<product-descriptor>` v `plugin.xml`
- Paid UI elementy: skryté nebo disabled ve free tieru (ne crash/error)
- Trial: 30 dní plný přístup, pak downgrade na free tier
- Upgrade prompt: max 1× denně, non-intrusive balloon

## CI/CD
- GitHub Actions: `./gradlew check`, `verifyPlugin`, `runPluginVerifier`
- Detekt pro statickou analýzu Kotlin kódu
- Tag-based release → `publishPlugin` na Marketplace
- Kotlin coding conventions (JetBrains official style)

## Ikona
- `src/main/resources/META-INF/pluginIcon.svg` (40×40, light)
- `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark)
- Action ikony: 16×16 + 13×13 SVG (light + dark varianty)

## Freemium hranice
- Free: status bar widget, notifikace, 3 environment patterny
- Paid: custom patterny, quick switch, audit log, visual border
