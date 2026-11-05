# Installing Jules' Homestead (Windows + Prism Launcher)

## What you need
* **Prism Launcher** 9.x or newer, signed in with your Microsoft account.
* About **8 GB of system RAM** (the instance uses 6 GB; 8 GB if you enable shaders or Distant Horizons).
* **Java 25.** Minecraft 26.1.2 requires it. Prism can download it for you (step 4).

## Steps
1. Download `JulesHomestead-<version>.mrpack` from this repository's build output (or build it: see README → Building).
2. Prism → **Add Instance** → **Import** → choose the `.mrpack` file → **OK**.
3. Prism shows two prompts:
   * **Optional mods**: tick what you want. **Create (Create Fly)** is optional; it is mechanical only, not electricity. **Distant Horizons** is off by default because of its performance cost. The two **shaders** are optional too.
   * **"Untrusted mods"**: four FTB files (FTB Quests, FTB Library, FTB Teams, FTB XMod Compat) download from FTB's official maven (`maven.ftb.dev`). Their licence doesn't let the pack re-host them, so the pack links to the official source. Accept.
4. Select the instance → **Edit** → **Settings → Java**:
   * Tick **Java installation** and use **Auto-detect…** or **Download Java** to pick **Java 25** (e.g. Temurin 25).
   * Set **Maximum memory allocation** to **6144 MB** (8192 MB with shaders/DH).
5. Launch. The first start takes a few minutes while mods initialise.
6. Create a new world (any world type works; terrain comes from Terralith + Tectonic). Open the quest book with the FTB Quests button in your inventory, or with the keybind under **Options → Controls**.

## Optional extras
* **Shaders**: Options → Video Settings → Shader Packs → *Complementary Reimagined* (cinematic) or *MakeUp Ultra Fast* (light). See `docs/presets.md`.
* **Physics Mod Pro** (your own file): copy your jar into the instance's `mods` folder. It isn't bundled and hasn't been tested with this pack; remove it first if you hit a crash.
* **AI chat for residents** (MCA): off by default and not needed. If you want it, read MCA's wiki first, since it sends conversations to an external service.

## Updating
Import the new `.mrpack` as a **new instance** and copy your world from `saves/` across. Back up first.

## Dedicated server (optional)
`python3 tools/install_server.py <dir>` installs the server-side mod set (same pins and hashes) plus configs and quests. Run it with Java 25: `java -Xmx6G -jar fabric-server-launch.jar nogui`.
