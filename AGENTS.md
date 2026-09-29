# AGENTS.md

Tento soubor definuje, jak mají agenti pracovat v tomto repozitáři.

## Scope

Řeš celý repozitář podle zadání uživatele.

Preferuj minimální bezpečné změny v dotčených částech (`fe`, `fe-admin`, `fe-shared`, backend i infrastruktura).

## Co je co

- `fe`: veřejný frontend (React + TypeScript + Vite + pnpm)
- `fe-admin`: administrace (React + TypeScript + Vite + pnpm)
- `fe-shared`: sdílené FE typy/kontrakty (`@types/*`)

## Základní pravidla práce

- Před změnou nejdřív projdi relevantní část kódu a navrhni minimální bezpečnou úpravu.
- Dodrž existující styl projektu (lint, naming, struktura složek).
- Neprováděj široké refactory bez výslovného zadání.
- Pokud měníš API kontrakt, zkontroluj dopad v backendu i konzumentech (`fe`, `fe-admin`) a případně uprav `fe-shared`.
- Preferuj malé, dobře ověřitelné změny.

## Jak spouštět projekty

Používej příkazy v konkrétní složce projektu.

### `fe`

```bash
pnpm dev
pnpm lint
pnpm build
```

### `fe-admin`

```bash
pnpm dev
pnpm lint
pnpm build
```

### `fe-shared`

- Obsahuje primárně sdílené TypeScript typy.
- Po změně typů ověř build/lint v konzumentech (`fe`, `fe-admin`).

## Ověření před odevzdáním

- Spusť minimálně lint/testy v dotčeném projektu.
- U změn s dopadem na kompilaci nebo runtime spusť build/relevantní testy.
- Zkontroluj, že se změna nepromítla nechtěně do jiných částí systému.

## Výstup pro uživatele

Při předání práce uveď:
- co bylo změněno
- kde přesně (soubor/složka)
- jak bylo ověřeno (lint/build)
- případná rizika nebo navazující kroky
