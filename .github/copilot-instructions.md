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
- Balíčky: `com.jirak.k8sguard.*`
- Environments: PRODUCTION (🔴), STAGING (🟡), DEVELOPMENT (🟢), UNKNOWN (⚪)
- Team rules v `.k8s-guard.yaml`
- Kubeconfig parsing přes SnakeYAML
- Texty v `messages/K8sGuardBundle.properties`
- Verzování: `2026.2.X`
- Bundle: DevOps Safety Kit (s Terminal Safety Rails)

## Freemium hranice
- Free: status bar widget, notifikace, 3 environment patterny
- Paid: custom patterny, quick switch, audit log, visual border
