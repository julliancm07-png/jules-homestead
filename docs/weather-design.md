# Weather & Survival Design (homestead-core)

No Fabric mod at any Minecraft version provides gameplay blizzards/storms (see `research/version-decision.md`). `homestead-core` adds **severe fronts** on top of vanilla weather, integrating with Thermoo (temperature) and Serene Seasons (season) rather than replacing them.

## What is simulated vs. simplified

| Aspect | Implementation | Simulation level |
|---|---|---|
| Event scheduling | Server schedules the *next* front in advance (saved state); season-weighted chance; min/max gaps. | Simplified game rule |
| Event kind | `STORM` or `BLIZZARD`. Winter (Serene Seasons) strongly favours blizzards. | Simplified |
| Manifestation | Per position: if vanilla precipitation at that block is **snow** → blizzard effects; **rain** → storm effects; **none** → dry wind only. Seasons change biome temperature, so a winter front over plains is a blizzard. | Uses real biome/season data |
| Intensity | 0–1 curve: ramp-up (first 15%), plateau, ramp-down (last 20%), scaled by event severity. | Simplified |
| Wind | Global direction (slow drift) + speed in m/s: calm random-walk baseline 2–8 m/s, + up to 20 m/s gusting during fronts. | Simplified, documented |
| Vanilla flags | While a front is active the server keeps `rain=true, thunder=true` so vanilla rendering, Immersive Storms fog, solar darkening and Energized Power's Lightning Generator all react. | Integration |
| Forecast | Next front's start time, kind and severity are known in advance; forecast severity is fuzzed by ±1 level. Warnings broadcast at configurable lead times (default 1 in-game day and ~2 minutes before). | Simplified |
| Shelter | **Sheltered** = no open sky above the head **and** the upwind horizontal ray hits a solid block within 8 blocks. Doorways facing into the wind are exposed. | Heuristic, cheap (one heightmap check + one short raycast per player per second) |
| Cold exposure | Exposed during a blizzard: extra Thermoo temperature loss per tick, scaled by intensity, through Thermoo's `environment` source so Frostiful resistances (fur armour, etc.) apply. | Integration |
| Travel | Exposed during a front: movement-speed modifier (blizzard −25%×intensity, storm −10%×intensity), removed when sheltered. Horses/boats unaffected. | Simplified |
| Visibility | Client: dense fog when exposed in a blizzard (`fogStrength` config), extra snow particles; Immersive Storms adds its own snowy/sandy fog under thunder. | Visual |
| World changes | Default: **snow-layer accumulation** on exposed natural ground near players during blizzards (max 2 layers, never on farmland/crops/paths/player blocks list). Crop damage, block breaking, fire: **off by default** and not implemented beyond config stubs. | Conservative |

## Protection
* **Buildings**: any roofed space whose windward side is closed counts as shelter.
* **Clothing**: Frostiful fur armour / Thermoo cold-resistance attributes reduce blizzard cold automatically.
* **Heat**: Frostiful's warmth-from-light, campfires/fireplaces, and the powered **Space Heater** (light 15 + extra Thermoo warmth in a 7-block radius while powered).

## Commands (op level 2)
* `/homestead weather status` — current front, intensity, wind, your shelter/exposure.
* `/homestead weather start <storm|blizzard> [seconds] [severity 1-3]` — start a front now.
* `/homestead weather stop` — end the current front (ramps down quickly).
* `/homestead weather forecast` — show the next scheduled front.
* `/homestead weather schedule <seconds>` — reschedule the next front (testing).

Everyone can read the forecast by using a **Barometer** (crafted from copper, glass and a compass; or placed as a block).

## Config (`config/homesteadcore.json`)
All gameplay numbers are config values: event frequency, durations, severity weights per season, cold per tick, slowdown, warning lead times, snow accumulation (on/off, max layers, radius), fog strength (client), and a master `enableSevereWeather` toggle.

## Saved state
Overworld `SavedData` `homesteadcore_weather`: current front (kind, start, end, severity, wind direction), next scheduled front, issued-warning flags, wind state. Survives restarts.

## Known limitations (stated plainly)
* Weather is **global** (vanilla model). A front affects the whole dimension; per-biome manifestation comes only from precipitation type.
* Shelter is a heuristic, not airflow simulation.
* Solar/wind/hydro respond to *our* wind model and *vanilla* rain/thunder darkening; there is no cloud-cover simulation.
