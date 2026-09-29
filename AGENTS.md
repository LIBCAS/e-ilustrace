# AGENTS.md

Tento soubor definuje, jak mají agenti pracovat v tomto repozitáři.

## Scope

Řeš pouze frontend části:
- `fe`
- `fe-admin`
- `fe-shared`

Mimo scope (pokud uživatel výslovně neřekne jinak):
- backend (`eil-api`, `common`, `entity-views`, `index`, apod.)
- infrastruktura (Docker/Gradle) mimo nutné FE nastavení

## Co je co

- `fe`: veřejný frontend (React + TypeScript + Vite + Yarn 4)
- `fe-admin`: administrace (React + TypeScript + Vite + Yarn 4)
- `fe-shared`: sdílené FE typy/kontrakty (`@types/*`)

## Základní pravidla práce

- Před změnou nejdřív projdi relevantní část kódu a navrhni minimální bezpečnou úpravu.
- Dodrž existující styl projektu (lint, naming, struktura složek).
- Neprováděj široké refactory bez výslovného zadání.
- Pokud měníš API kontrakt, zkontroluj dopad v `fe` i `fe-admin` a případně uprav `fe-shared`.
- Preferuj malé, dobře ověřitelné změny.

## Jak spouštět projekty

Používej příkazy v konkrétní složce projektu.

### `fe`

```bash
yarn dev
yarn lint
yarn build
```

### `fe-admin`

```bash
yarn dev
yarn lint
yarn build
```

### `fe-shared`

- Obsahuje primárně sdílené TypeScript typy.
- Po změně typů ověř build/lint v konzumentech (`fe`, `fe-admin`).

## Ověření před odevzdáním

- Spusť minimálně `yarn lint` v dotčeném FE projektu.
- U změn s dopadem na kompilaci spusť `yarn build`.
- Zkontroluj, že se změna nepromítla nechtěně do jiné části UI.

## Výstup pro uživatele

Při předání práce uveď:
- co bylo změněno
- kde přesně (soubor/složka)
- jak bylo ověřeno (lint/build)
- případná rizika nebo navazující kroky
