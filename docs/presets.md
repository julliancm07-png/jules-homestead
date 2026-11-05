# Performance & Visual Presets

The pack ships the **Baseline** preset in `options.txt`. Switch presets in *Options → Video Settings* (Sodium's tabbed screen) and the *Shader Packs* menu.

| Setting | Low (laptops / iGPU) | **Baseline (default)** | Cinematic (strong GPU) |
|---|---|---|---|
| Render distance | 8 | **10** | 16 |
| Simulation distance | 6 | **8** | 10 |
| Graphics | Fast | **Fancy** | Fancy |
| Particles | Decreased | **All** | All |
| Biome blend | 1 | **2** | 5 |
| Shader pack | none (or *MakeUp Ultra Fast*) | **none** | *Complementary Reimagined* (faithful) · *Photon* (realistic, strong GPU) · *Mellow* (painterly, light) |
| Dramatic Skys (resource pack, via Nuit) | off | **on** | on (shaders draw their own sky) |
| Distant Horizons (optional mod) | off | **off** | on, LOD 256 chunks |
| Visual resource packs (Fresh Animations + extensions, Better Leaves, Fancy Crops, grass flowers, lanterns, flower pots, glass, eating animations) | turn off Fresh Animations extensions + grass flowers | **on** | on |
| Subtle Effects / Ambient Environment / Wakes | Subtle Effects: lower particle render distance | **on** | on |
| homestead-core blizzard particles | `client.blizzardParticles=false` | **true** | true |
| Instance memory | 4–5 GB | **6 GB** | 8 GB |

Notes
* *MakeUp Ultra Fast* is a light shader option for mid-range GPUs; *Mellow* is a soft, painterly look that is also easy on hardware.
* Blizzard visibility is applied through vanilla fog. Some shader packs draw their own fog; if blizzards look clear with shaders, lower the shader's fog distance or play without shaders in winter.
* Dynamic FPS lowers the frame rate when the window is unfocused. Entity Culling and More Culling help a lot in furnished houses.
* The defaults in `config/homesteadcore.json` (`client.blizzardFogStrength`, `client.blizzardParticles`) are per-player and safe to change.
