# Reference Modpack Study

**Research date:** 2026-11-05. Sources: CurseForge project pages (read via WebFetch; CurseForge's API is blocked from this environment) and CFWidget metadata mirror for file lists; Modrinth API for Modrinth-hosted packs. No scripts, quests or assets from these packs are copied — only design lessons.

| Pack | Platform | Latest file seen | Focus |
|---|---|---|---|
| Homestead – A Cozy Survival Experience | 1.20.1 Fabric | 2.0.1 (2026-10-27) | Cozy farming/cooking/building, 709 quests / 27 chapters, Create chapter |
| Farming Valley | 1.10.2 Forge | 1.3.6 (2026-09-07) | Harvest-Moon style: seasons, selling crops to NPCs, money-gated progression |
| Life in the Village 3 | 1.19.2 Forge | 2.10 (2024-12-02) | Town building via MineColonies, vanilla+ |
| Create: Above and Beyond | 1.16.5 Forge | 1.3 (2021-12-15) | Create-centric guided "chapter" progression, coin market |
| StaTech Industry | 1.19.2 Fabric | 1.1.15 (2025-01-22) | Modern Industrialization tech, 750+ quests |
| The Farm (Modrinth) | 1.21.1 Fabric | 2026-06 | Cozy farming + Fabric Seasons, Terralith/Tectonic/Wilder Wild, no quests |
| Cozy Time – Cozy Survival with Quests | 1.21.1 Fabric | 3.5.x (2026) | Cozy pack with FTB Quests, MCA Reborn, Farmer's Delight, Macaw's |

## Lessons by topic

### Progression
* **CAB&B**: a *single* clear goal chain ("about fifty inventions between you and the moon") with a slow early game that accelerates once first components are automated. → Our town-tier chain (camp → homes/farms → workshop/power → bakery/market → community) is the spine; each tier unlocks the tools that make the next tier faster.
* **Farming Valley**: progression gated by *selling produce* gives farming a purpose beyond hunger. Its downside — many items are buy-only, and the pack "doesn't support removing mods" — is too rigid for us. → Use emerald trades, MCA taxes/requests and quest rewards as soft gates, never hard locks on basic survival.
* **Homestead**: chapter breadth (fishing, drinks, storage, gear) keeps a cozy pack varied, but 709 quests across 27 chapters drifts into checklist filler. → Fewer, denser chapters (~8) with quests that each teach a *decision*.

### Farming & food
* **Homestead / The Farm / Cozy Time** all centre Farmer's Delight; it is the de facto Fabric cooking backbone. → Farmer's Delight Refabricated + add-ons that share its tags, rather than parallel food suites.
* **Farming Valley**: seasonal crops make *planning* matter. → Serene Seasons fertility tags drive which crops grow when; greenhouses (glass-enclosed) are the winter answer.

### Weather & survival
* None of the references implement gameplay storms; The Farm uses seasons only. This confirms the gap that `homestead-core` fills.

### Economy & town
* **LITV3**: MineColonies gives purpose-built workers, but it is NeoForge/Forge-only and turns the game into colony management. → MCA Reborn residents + player-built houses fit "build it yourself"; MCA detects building types from block requirements.
* **CAB&B**: market accessed through the quest UI; errands pay coins. → FTB Quests reward tables + "town request" quests act as errands without adding a currency mod.

### Automation & power
* **StaTech**: deep processing lines are great for tech players but a grind for a homestead pack. → One compact electrical mod (Energized Power) with a short tier ladder; no ore-processing mandates beyond a crusher/furnace upgrade.
* **Homestead** treats Create as a self-contained chapter. → Create (via Create Fly) is an optional module, not a progression dependency.

### Quests
* **CAB&B / StaTech / Homestead** all use FTB Quests: it is the established, attractive Fabric quest UI with pinned-quest HUD tracking and reward tables. → FTB Quests, authored from scratch in SNBT.

### Performance
* **CAB&B** relied on FerriteCore, spark etc.; **The Farm** on Sodium/Lithium/Iris/Dynamic FPS. → Sodium + Lithium + FerriteCore + ImmediatelyFast + Entity Culling + Dynamic FPS baseline; shaders and Distant Horizons optional.
* **Cozy Time** recommends ≥6 GB RAM; with MCA, Create and worldgen mods we target 6 GB default, 8 GB with shaders/DH.
