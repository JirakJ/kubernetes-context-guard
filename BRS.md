# KubeContext Safety — Business Requirements Specification (BRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Executive Summary

KubeContext Safety je jednoduchý, virální plugin zabraňující nechtěným akcím na produkčním Kubernetes clusteru. Vstupní bod do DevOps Safety Kit ekosystému — free lead-gen s upsell na Terminal Safety Rails a paid features.

---

## 2. Obchodní cíle

### 2.1 Primární role
**Lead-gen plugin** — maximální adopce, brand awareness pro DevOps Safety Kit.

### 2.2 KPI
| Metrika | Cíl (6 měsíců) | Cíl (12 měsíců) |
|---------|:---:|:---:|
| Celkové instalace | 10 000 | 40 000 |
| MAU | 5 000 | 20 000 |
| Cross-install Terminal Safety Rails | 15 % | 25 % |
| Marketplace rating | ≥ 4.3 | ≥ 4.5 |

---

## 3. Cílový trh

- **Kubernetes uživatelé v JetBrains IDE** — ~500k+
- Primárně: DevOps, SRE, backend vývojáři s K8s přístupem

---

## 4. Konkurenční prostředí

Na JetBrains Marketplace existuje Kubernetes plugin od JetBrains (bundled v Ultimate), ale **nemá context guard / safety funkce**. Žádný přímý konkurent.

---

## 5. Revenue model

| Tier | Cena |
|------|------|
| **Free** | $0 — widget, notifikace, 3 patterny |
| **Pro** | $1.90/měsíc nebo $19/rok — quick switch, audit, custom patterny |

Hlavní monetizace: **cross-sell na Terminal Safety Rails Pro/Team**.

---

## 6. Go-to-Market

1. **Virální launch** — Reddit r/kubernetes, r/devops
2. **Meme marketing** — „I accidentally deleted production. Again."
3. **Bundle** s Terminal Safety Rails → DevOps Safety Kit

---

## 7. Milníky

| Milestone | Popis |
|-----------|-------|
| **M1: MVP** | Status bar widget, barevné kódování, notifikace |
| **M2: Guard** | Potvrzovací dialog, visual border |
| **M3: Switch** | Quick switch, favorites, history |
| **M4: Bundle** | DevOps Safety Kit integrace |
| **M5: Audit** | Context switch log, time tracking |

---

## 8. Success Criteria

1. 20 000+ instalací do 9 měsíců (lead-gen)
2. ≥ 15 % cross-install Terminal Safety Rails
3. Marketplace rating ≥ 4.3
4. DevOps Safety Kit brand recognition
