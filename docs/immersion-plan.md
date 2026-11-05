# Immersion & Screens Plan (in progress — user request 2026-11-05)

> User: "I want the screens to be pleasant and immersive, and I also want you to seek out the resource packs, data
> packs, and shaders to make the experience top notch."

## Verified so far (cloud)
* Full client renders a Terralith world on Mesa llvmpipe; blizzard fog + wind-driven snow + Frostiful frozen hearts
  visible (`docs/screenshots/client-smoke-blizzard.png`). MCA "Who are you?" first-join screen appears (normal-scenario
  screenshot) — test instance sets `launchIntoDestiny=false` only for screenshots.
* For doc screenshots use Peaceful difficulty in the test world (AFK test player died to a spider in the blizzard).

## Candidate shortlist (Modrinth, 26.1.2, by downloads; each still needs requirement/licence/side check)
**Resource packs** (vanilla-faithful, cozy):
fresh-animations (have) + fresh-animations-extensions / fa-player-extension / fresh-animations-details /
fresh-animations-objects · better-leaves (have) · fancy-crops or 3d-crops · better-lanterns · fresh-flower-pots ·
simple-grass-flowers / os-colorful-grasses · clean-connected-glass (needs Continuity — have) · new-glowing-ores ·
low-on-fire / low-shield-pack · even-better-enchants / enchanted-books-re-covered · visual-armor-trims ·
theones-eating-animation-pack or fresh-food · animated-items · dramatic-skys (needs a sky mod: check fabricskyboxes /
nuit for 26.1.2) · recolourful-containers-gui (UI) · cozyui-plus (UI, 64x) · enhanced-audio / vvi (audio).
**Data packs / structure mods**: dungeons-and-taverns (taverns = cozy), explorify, structory (+towers), trek,
remove-terralith-intro-message, gardeners-dream (pots), nullscape (End) / incendium (Nether, has hard dungeons → maybe).
Avoid: geophilic (overlaps Terralith), from-the-fog (horror), veinminer (grind bypass?), guns.
**Shaders** (optional, Iris): complementary-reimagined (have, default cinematic), photon-shader (high-end),
mellow (painterly/cozy), makeup-ultra-fast-shaders (have, low-end), bliss/solas (alt styles).
**Immersion mods to probe**: legendary-tooltips, fancymenu (+drippy loading screen), rrls, sounds, subtle-effects,
particular (fireflies/waterfall spray — replaces Wilder Wild's lost ambience), camera-overhaul, snow-under-trees,
wakes, overflowing-bars (thermoo-patches has a patch), fabricskyboxes/nuit (for Dramatic Skys).

## Original assets to build (resourcepacks/homestead-resources, MIT, bundled in overrides)
1. Title screen: panorama captured from the pack's own world (vanilla panorama capture if available in 26.1.2),
   homestead splash texts (`assets/minecraft/texts/splashes.txt`), "Homestead Edition" banner (original pixel lettering).
2. FTB Quests theme (warm parchment/wood) via resource-pack theme file — inspect format in the FTB Quests jar.
3. Jade theme matching the quest book (inspect Jade theme JSON format).
4. Calm loading screen: options.txt `darkMojangStudiosBackground:true`.
5. options.txt resourcePacks order incl. Continuity built-ins (`continuity:default`, glass culling fix) — verify ids.

## Verification plan
Client smoke test variants with screenshots: title screen (no --join-world), in-world clear weather (Peaceful),
blizzard, and — if python-xlib/XTEST works on Xvfb — open inventory / quest book for screenshots.
