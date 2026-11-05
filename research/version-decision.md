# Platform & Version Decision

**Research date:** 2026-11-05 (UTC)
**Decision:** Minecraft **26.1.2**, Fabric Loader **0.20.1**, Fabric API **0.155.3+26.1.2**, Java **25** (Temurin 25.0.5+8), Gradle **9.8.2**, Fabric Loom **1.19.3**.

## Sources used

| Source | What it established |
|---|---|
| `piston-meta.mojang.com/mc/game/version_manifest_v2.json` | Release list: 26.3 (2026-09-15), 26.2 (2026-06-16), 26.1.2 (2026-04-09), 26.1.1, 26.1, 1.21.11 (2025-12-09)… Latest snapshot 26.4-snapshot-7. |
| Mojang version JSON for 26.1.2 / 26.2 / 26.3 | `javaVersion.majorVersion = 25` for all three. |
| `meta.fabricmc.net/v2/versions/loader` | Loader 0.20.1 is the newest *stable* loader and supports 26.1.2. |
| `maven.fabricmc.net` fabric-api metadata | Newest Fabric API for 26.1.2 is `0.155.3+26.1.2`. |
| `raw.githubusercontent.com/FabricMC/fabric-example-mod/26.1.2` | Official template: `net.fabricmc.fabric-loom` (no mappings — 26.1+ is unobfuscated), Gradle 9.8.2 wrapper, Java 25 release target. |
| Modrinth API v2 (`api.modrinth.com/v2`) | Per-project Fabric version lists filtered with `loaders=["fabric"]` (see `research/data/probe-*.json`, produced by `tools/modrinth_probe.py`). |
| CurseForge (via WebFetch + `api.cfwidget.com` metadata mirror) | FTB Quests/Library/Teams Fabric file lists. **CurseForge's own site/API return HTTP 403 to this environment's egress proxy**; CFWidget is a third-party mirror and is treated as secondary evidence only. |
| `maven.ftb.dev` (FTB's official maven) | Confirms FTB Quests Fabric `26.1.2.9` jar + sha1/sha256/sha512 hashes; its `fabric.mod.json` declares `minecraft >=26.1.0`, `ftblibrary >=26.1.2.8`, `ftbteams >=26.1.2.4`, license *All Rights Reserved*. |
| FTB-Quests `main` branch `gradle.properties` | `minecraft_version=26.1.2`, `supported_minecraft_versions=26.1,26.1.1,26.1.2` — no 26.2/26.3 Fabric line yet. |

## Fabric ecosystem depth (Modrinth `total_hits`, Fabric mods)

| Version | Fabric mods |
|---|---|
| 26.3 | 9,279 |
| 26.2 | 13,717 |
| **26.1.2** | **13,628** |
| 26.1 | 11,199 |
| 1.21.11 | 18,258 |
| 1.21.1 | 19,968 |
| 1.20.1 | 17,919 |

## Essential-system support (Fabric builds only)

✓ = Fabric build exists for that exact version; ✗ = none found. Data: `research/data/probe-round1.json`, `probe-round2.json`, `probe-26.1.2.json`.

| System / mod | 26.3 | 26.2 | **26.1.2** | 1.21.11 | 1.21.1 | 1.20.1 |
|---|---|---|---|---|---|---|
| **Quests:** FTB Quests (Fabric) | ✗ | ✗ | ✓ 26.1.2.9 | ✓ | ✓ | ✓ |
| **Seasons:** Serene Seasons | ✓ | ✓ | ✓ 26.1.2.0.7 | ✓ | ✓ | ✓ |
| **Body temperature:** Thermoo + Frostiful | ✗ | ✗ | ✓ 10.1.5 / 3.1.0-beta.3 | ✓ | ✓ | ✓ |
| Thermoo ⇄ Serene Seasons bridge (thermoo-patches) | ✗ | ✗ | ✓ 5.0.0 | ✓ | ✓ | ✓ |
| Alt. temperature: Homeostatic (+Seasons) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ (Seasons ✗) |
| **Electricity:** Energized Power (TR Energy on Fabric) | ✓ | ✓ | ✓ 3.0.7 | ✓ | ✓ | ✓ |
| Electricity alt.: Tech Reborn | ✓ | ✓ | ✓ 6.0.5 | ✗ | ✓ | ✓ |
| Electricity alt.: Oritech / Modern Industrialization | ✗ | ✗ | ✗ | ✗ | ✓ / ✗ | ✗ / ✓ |
| **Cooking:** Farmer's Delight Refabricated | ✓ | ✓ | ✓ 3.6.30 | ✓ | ✓ | ✓ |
| Let's Do (Bakery/Vinery/Farm & Charm…) | ✗ | ✗ | ✗ | ✗ | ✓ (some) | ✓ |
| **Furniture:** Macaw's suite | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Handcrafted / Another Furniture / Supplementaries | ✗ | ✗ | ✗ | ✗ | ✓ | ✓ |
| **Residents:** MCA Reborn | ✗ | ✓ | ✓ 8.1.11 | ✗ | ✓ | ✓ |
| Easy NPC | ✓ | ✓ | ✓ 7.18.0 | ✓ | ✓ | ✓ |
| MineColonies | ✗ (NeoForge only) | | | | | |
| **Create (official Fabric port)** | ✗ | ✗ | ✗ | ✗ | ✗ | ✓ |
| Create Fly (community Fabric port) | ✗ | ✓ | ✓ 6.0.9-4 | ✓ | ✗ | ✗ |
| Create Crafts & Additions (rotation→electricity) | ✗ | ✗ | ✗ | ✗ | ✗ (NeoForge) | ✓ |
| **Recipe viewer:** JEI / REI / EMI | ✓/✓/✗ | ✓/✓/✗ | ✓/✓/✗ | ✓/✓/✗ | ✓/✓/✓ | ✓/✓/✓ |
| **Worldgen:** Terralith + Tectonic | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **Gameplay blizzards/storms** (any) | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ (Weather2 is Forge/NeoForge) |

## Comparison of viable targets

**26.3 (newest stable).** Loses the quest system (FTB Quests Fabric) and the entire Thermoo temperature stack. A quest framework is a core requirement; the only 26.3 Fabric alternatives are tiny or single-purpose (e.g. Theseus Quests, <1k downloads). *Rejected.*

**26.2.** Same two gaps (no FTB Quests Fabric, no Thermoo/Frostiful). Gains nothing essential over 26.1.2. *Rejected.*

**26.1.2 — chosen.** Newest version where every essential system has a maintained Fabric build: quests, seasons, temperature with a *verified* seasons bridge, electricity (TR Energy API), Farmer's Delight, Macaw's furniture, MCA Reborn residents, worldgen, JEI. Create is available as an optional community port. Unobfuscated Mojang names make custom-mod development and decompiled-source verification straightforward.

**1.21.11.** Largest late-1.21 ecosystem, but MCA Reborn and Tech Reborn have no 1.21.11 Fabric build; no advantage over 26.1.2 for the essentials.

**1.21.1.** Biggest ecosystem (EMI, Let's Do, Handcrafted, Supplementaries, Oritech). But it is two years old, still has no Fabric Create, and its only extra wins are content breadth. Not worth the downgrade given 26.1.2 covers every essential system.

**1.20.1.** The only version with *official* Create Fabric + Create Crafts & Additions (a real rotation→electricity bridge). That is a major downgrade (three years, Java 17 era) for one desirable-not-mandatory system; and weather (none on Fabric) and quests would not improve. *Rejected per brief: never trade core homestead/weather/electricity for Create.*

## Consequences of choosing 26.1.2

* Java **25** is required (Prism: set the instance to a Java 25 runtime; Prism can auto-download it).
* No EMI → **JEI** is the single recipe viewer (FTB Quests has JEI integration; Create Fly recommends JEI/REI).
* Food breadth comes from Farmer's Delight + add-ons on 26.1.2 rather than Let's Do.
* **Create is optional** (user decision 2026-11-05): ship it as a separate optional module via Create Fly, only if it passes local testing. Create rotation is **not** electricity in this pack — no verified rotation→TR Energy converter exists on 26.1.2 Fabric.
* No gameplay blizzard mod exists on Fabric at any version → **custom `homestead-core` mod** provides storm/blizzard events (see `docs/weather-design.md`).
* FTB mods are CurseForge/FTB-maven only and *All Rights Reserved* → the `.mrpack` references FTB's **official** maven URLs (no re-hosting). Prism shows a one-time "untrusted source" confirmation for non-Modrinth URLs (verified in PrismLauncher `ModrinthInstanceCreationTask.cpp`, `promptForUntrustedMods`).

## Toolchain pins

| Item | Pin | Verified by |
|---|---|---|
| JDK | Temurin 25.0.5+8 | Adoptium API checksum `68982529…d7fb` matched |
| Gradle | 9.8.2 (wrapper) | wrapper jar SHA-256 `3b8a2577…8e98` matches services.gradle.org |
| Fabric Loom | 1.19.3 | resolved from maven.fabricmc.net; skeleton built OK |
| Minecraft | 26.1.2 | Loom download + `genSources` succeeded |
| Fabric Loader | 0.20.1 | |
| Fabric API | 0.155.3+26.1.2 | |

Cloud-only note: `repo.maven.apache.org` intermittently returns HTTP 429 to this environment's proxy. `tools/ci/central-mirror.init.gradle` (opt-in `--init-script`) rewrites Maven Central to Google's official mirror. Not needed on a normal machine.
