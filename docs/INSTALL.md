# Installing Jules' Homestead (Windows + Prism Launcher)

## What you need
* **Prism Launcher** 9.x or newer, signed in with your Microsoft account.
* About **8 GB of system RAM** (the instance uses 6 GB; 8 GB if you enable shaders or Distant Horizons).
* **Java 25.** Minecraft 26.1.2 requires it. Prism can download it for you (step 4).

## Steps
1. Download **`dist/JulesHomestead-1.0.0.mrpack`** from this repository. On GitHub, open the file and click **Download raw file**. The work branch is `claude/exciting-planck-7nagkx` until it's merged. To check the file, compare its SHA-256 with `dist/SHA256SUMS`. You can also build it yourself: see README → Building.
2. Prism → **Add Instance** → **Import** → choose the `.mrpack` file → **OK**.
3. Prism shows two prompts:
   * **Optional mods**: tick what you want. **Create (Create Fly)** is optional; it is mechanical only, not electricity. **Distant Horizons** is off by default because of its performance cost. The four **shader packs** are optional too.
   * **"Untrusted mods"**: four FTB files (FTB Quests, FTB Library, FTB Teams, FTB XMod Compat) download from FTB's official maven (`maven.ftb.dev`). Their licence doesn't let the pack re-host them, so the pack links to the official source. Accept.
4. Select the instance → **Edit** → **Settings → Java**:
   * Tick **Java installation** and use **Auto-detect…** or **Download Java** to pick **Java 25** (e.g. Temurin 25).
   * Set **Maximum memory allocation** to **6144 MB** (8192 MB with shaders/DH).
5. Launch. The first start takes a few minutes while mods initialise.
6. Create a new world (any world type works; terrain comes from Terralith + Tectonic). Open the quest book with the FTB Quests button in your inventory, or with the keybind under **Options → Controls**.

## Optional extras
* **Shaders**: Options → Video Settings → Shader Packs:
  * *Complementary Reimagined*: cinematic
  * *Photon*: semi-realistic
  * *Mellow*: soft, painterly
  * *MakeUp Ultra Fast*: light

  See `docs/presets.md` and `docs/visuals-and-immersion.md`.
* **3D inventory panels (Spatial GUI)** are on by default: your inventory and containers open as a panel beside your character. To get flat screens back, or for a first-person panel, open *Mod Menu → Spatial GUI*.
* **Physics Mod Pro** (your own file): copy your jar into the instance's `mods` folder. It isn't bundled and hasn't been tested with this pack; remove it first if you hit a crash.
* **AI chat for residents** (MCA): off by default and not needed. If you want it, read MCA's wiki first, since it sends conversations to an external service.

## If the game runs slowly
The pack already ships the usual performance mods (Sodium, Lithium, C2ME, FerriteCore, ImmediatelyFast, Entity Culling,
Particle Core and others), so try these in order:
1. **Options → Video Settings**: lower *Render Distance* first (8–10 is plenty without Distant Horizons), then
   *Simulation Distance*. Sodium's extra pages (Reese's Sodium Options) hold more switches.
2. **Turn off what you added**: shaders and Distant Horizons cost the most.
3. **Animations** can be toned down or switched off one by one in *Mod Menu*: Chunks Fade In, Fancy World
   Animations (`/fwaConfig`), Smooth Swapping, A Good Place, Particular.
4. **Find the cause**: run `/sparkc profiler start`, play for a minute, then `/sparkc profiler stop`. spark prints a link
   to a report showing what is using the time.
5. **Optional Java setting** (not tested on your PC): in Prism, open *Edit → Settings → Java → JVM arguments* and add
   `-XX:+UseZGC`. On Java 25 this is the low-pause garbage collector, which can smooth out stutters. It uses a little more
   memory, so set the maximum memory to 8192 MB with it. If anything gets worse, delete the line again.

## Updating
Import the new `.mrpack` as a **new instance** and copy your world from `saves/` across. Back up first.

## Dedicated server (optional)
`python3 tools/install_server.py <dir>` installs the server-side mod set (same pins and hashes) plus configs and quests. Run it with Java 25: `java -Xmx6G -jar fabric-server-launch.jar nogui`.
