# FantaSensi Mobile

Versões mobile do **FantaSensi** (baseado no projeto `PCOptimizer`), um
"game booster" com foco em otimização de dispositivo e ajustes de mira.

> **Importante:** este projeto **não** é um cheat. Ele usa apenas APIs
> públicas do sistema. Não há edição de memória, nem modificação de arquivos
> do jogo, nem root/jailbreak. Os ajustes são **recomendações** e otimizações
> legítimas do sistema.

## Estrutura

```
FantaSensiMobile/
├── android/   # App Android (Kotlin + Jetpack Compose) — funcional e legítimo
└── ios/       # App iOS (SwiftUI) — apenas visual (sem mudanças reais)
```

O layout reproduz o dashboard do **PCOptimizer** (dark + laranja, glassmorphism).

---

## Android

**Stack:** Kotlin + Jetpack Compose (Material 3), minSdk 24, targetSdk 34.

### Tela única (Dashboard)

| Seção | O que faz |
|-------|-----------|
| **Hero** | Logo + título "FantaSensi" + subtítulo |
| **Stats** | CPU, RAM e Disco em tempo real (3 cards) |
| **Configurações de Mira** | MIRA LEVE / ALTA / PESADA / FULL CAPA (níveis de redução de animação/latência) |
| **Otimização única** | Limpa cache e aplica o perfil gamer |
| **Serviços** | Ativar/reduzir serviços (visual) |

### Detalhes técnicos

- `SystemStatsService` — lê RAM (`ActivityManager.MemoryInfo`), armazenamento
  (`StatFs`), bateria (`BatteryManager`) e CPU (`/proc/stat`).
- `BoostService` — limpa apenas o cache do próprio app via
  `File.deleteRecursively()`.
- `AnimationService` — ajusta `WINDOW_ANIMATION_SCALE`,
  `TRANSITION_ANIMATION_SCALE` e `ANIMATOR_DURATION_SCALE` via
  `Settings.Global` (requer permissão especial `WRITE_SETTINGS`).

### Como compilar

1. Abra a pasta `android/` no **Android Studio**.
2. Deixe o Gradle sincronizar.
3. Conecte um dispositivo/emulador e clique em **Run**.

---

## iOS

**Stack:** SwiftUI. **Apenas visual** — as telas simulam estatísticas e o
fluxo de otimização, mas **nenhuma mudança real** é feita no sistema.

### Como abrir no Xcode

1. Abra o **Xcode** e crie um novo projeto: **File > New > Project > App**.
2. Nomeie o produto como `FantaSensi` (Interface: SwiftUI, Language: Swift).
3. Arraste para dentro do projeto o conteúdo da pasta `ios/FantaSensi/`:
   - `FantaSensiApp.swift`
   - `Theme.swift`
   - `ContentView.swift`
   - `Views/DashboardView.swift`
   - `favicon.png` (adicione ao **Assets.xcassets** com o nome `favicon`)
4. Selecione um simulador de iPhone e clique em **Run**.

---

## Notas

- O tema (dark + laranja, glassmorphism) segue a identidade visual do
  `PCOptimizer` original.
- Nenhuma licença/KeyAuth/HWID/ofuscação foi portada: o projeto é aberto.
