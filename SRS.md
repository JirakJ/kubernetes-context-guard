# KubeContext Safety — Software Requirements Specification (SRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Úvod

### 1.1 Účel
KubeContext Safety zobrazuje aktuální kubectl kontext prominentně v IDE a varuje před akcemi na produkčním clusteru. Prevence nechtěného deploye/delete na produkci.

### 1.2 Rozsah
Plugin monitoruje `~/.kube/config`, zobrazuje aktuální kontext ve status baru s barevným kódováním a interceptuje K8s akce v IDE.

### 1.3 Reference
- Bundle partner: Terminal Safety Rails (DevOps Safety Kit)

---

## 2. Celkový popis

### 2.1 Perspektiva produktu
Free lead-gen plugin v DevOps Safety Kit bundlu. Jednoduchý, virální, řeší konkrétní bolest Kubernetes uživatelů.

### 2.2 Uživatelské skupiny
- **Kubernetes vývojáři/ops** — denně přepínají kontexty
- **DevOps inženýři** — spravují více clusterů
- **Platform engineers** — produkční přístupy

---

## 3. Funkční požadavky

### FR-1: Context Monitoring
- **FR-1.1:** Čtení aktuálního kontextu z `~/.kube/config` (file watcher)
- **FR-1.2:** Podpora pro KUBECONFIG env var (multiple config files)
- **FR-1.3:** Polling interval: 2 sekundy

### FR-2: Status Bar Widget
- **FR-2.1:** Prominentní widget ve status baru: `⎈ production-cluster`
- **FR-2.2:** Barevné kódování:
  - 🔴 Produkční kontexty (konfigurovatelné patterny: `*prod*`, `*prd*`, `*live*`)
  - 🟡 Staging kontexty (`*staging*`, `*stg*`)
  - 🟢 Dev/local kontexty (vše ostatní)
- **FR-2.3:** Klik na widget → rychlé přepnutí kontextu (dropdown)
- **FR-2.4:** Context + namespace zobrazení

### FR-3: Production Guard
- **FR-3.1:** Při produkčním kontextu: potvrzovací dialog před K8s akcemi
- **FR-3.2:** Vizuální indikátor: červený rámeček kolem IDE / toolbar
- **FR-3.3:** Notification: „You are connected to PRODUCTION cluster"
- **FR-3.4:** Auto-reminder každých 30 minut na prod kontextu

### FR-4: Context Rules
- **FR-4.1:** Definovatelné pravidla:
  ```yaml
  contexts:
    - pattern: "*prod*"
      environment: production
      color: red
      confirm_actions: true
      reminder_interval: 30m
    - pattern: "*staging*"
      environment: staging
      color: yellow
  ```
- **FR-4.2:** Team-shared rules v `.k8s-guard.yaml`

### FR-5: Quick Switch (Paid)
- **FR-5.1:** Keyboard shortcut pro context switch (Ctrl+K, Ctrl+C)
- **FR-5.2:** Favorite kontexty
- **FR-5.3:** Context history (posledních 10)
- **FR-5.4:** Namespace switch v rámci kontextu

### FR-6: Audit (Paid)
- **FR-6.1:** Log context switchů s timestamps
- **FR-6.2:** Alerting: „Spent >2h on production context"

---

## 4. Nefunkční požadavky

### NFR-1: Výkon
- File watcher na kubeconfig — minimální overhead
- Widget update < 100ms

### NFR-2: Kompatibilita
- IntelliJ IDEA 2024.3+, všechny JetBrains IDE
- macOS, Linux, Windows
- kubectl ne-required (jen čtení config souboru)

---

## 5. Datový model

### 5.1 ContextInfo
```
ContextInfo {
  name: String
  cluster: String
  namespace: String
  user: String
  environment: Enum (PRODUCTION, STAGING, DEVELOPMENT, UNKNOWN)
  color: Color
  requiresConfirmation: Boolean
}
```

---

## 6. Freemium model

### Free tier
- Status bar widget s barevným kódováním
- Produkční varování (notification)
- 3 předdefinované environment patterny

### Paid tier
- Custom patterny (neomezené)
- Quick switch (shortcut, favorites, history)
- Visual border indikátor
- Auto-reminder
- Audit log
- Team-shared rules
