#!/usr/bin/env python3
"""Generate homestead-core client assets and block loot tables deterministically.

Textures are small procedural 16x16 pixel-art images in a vanilla palette (original work, MIT like the mod).
Run: python3 -I tools/gen_assets.py   (writes into custom-mods/homestead-core/src/main/resources)
"""
import json
import pathlib
import random

from PIL import Image

ROOT = pathlib.Path(__file__).resolve().parent.parent / "custom-mods" / "homestead-core" / "src" / "main" / "resources"
NS = "homesteadcore"
ASSETS = ROOT / "assets" / NS
DATA = ROOT / "data" / NS


def img(fill):
    return Image.new("RGBA", (16, 16), fill)


def noise(im, base, spread, seed, box=(0, 0, 16, 16)):
    r = random.Random(seed)
    for x in range(box[0], box[2]):
        for y in range(box[1], box[3]):
            d = r.randint(-spread, spread)
            im.putpixel((x, y), tuple(max(0, min(255, c + d)) for c in base) + (255,))
    return im


def rect(im, x0, y0, x1, y1, col):
    for x in range(x0, x1 + 1):
        for y in range(y0, y1 + 1):
            im.putpixel((x, y), col + (255,) if len(col) == 3 else col)


def frame(im, col):
    for i in range(16):
        for p in ((i, 0), (i, 15), (0, i), (15, i)):
            im.putpixel(p, col + (255,))


METAL = (168, 170, 172)
METAL_D = (110, 112, 116)
ENAMEL = (232, 232, 226)
ENAMEL_D = (196, 196, 190)
WOOD = (150, 110, 64)
WOOD_D = (104, 74, 40)
BLACK = (40, 40, 44)
GLOW = (255, 150, 60)
GLOW_HOT = (255, 210, 120)


def textures():
    t = {}
    # --- wind turbine
    side = noise(img(0), METAL, 6, 1); frame(side, METAL_D); rect(side, 7, 1, 8, 14, METAL_D)
    t["wind_turbine_side"] = side
    top = noise(img(0), METAL, 6, 2); frame(top, METAL_D); rect(top, 6, 6, 9, 9, METAL_D)
    t["wind_turbine_top"] = top
    front = noise(img(0), METAL, 5, 3); frame(front, METAL_D)
    for i in range(2, 8):  # three blades from the hub
        front.putpixel((8, 8 - i), (235, 235, 235, 255)); front.putpixel((7, 8 - i), (220, 220, 220, 255))
        front.putpixel((8 + i, 8 + i // 2), (235, 235, 235, 255)); front.putpixel((8 - i, 8 + i // 2), (235, 235, 235, 255))
    rect(front, 7, 7, 8, 8, BLACK)
    t["wind_turbine_front"] = front
    # --- water wheel
    ww = noise(img(0), WOOD, 10, 4)
    for y in (3, 7, 11, 15):
        rect(ww, 0, y, 15, y, WOOD_D)
    frame(ww, WOOD_D)
    t["water_wheel_side"] = ww
    wf = noise(img(0), WOOD, 8, 5); frame(wf, WOOD_D)
    for i in range(1, 15):
        wf.putpixel((i, i), WOOD_D + (255,)); wf.putpixel((15 - i, i), WOOD_D + (255,))
    rect(wf, 6, 6, 9, 9, METAL_D)
    t["water_wheel_front"] = wf
    t["water_wheel_top"] = noise(img(0), WOOD, 8, 6)
    # --- refrigerator
    fs = noise(img(0), ENAMEL, 3, 7); frame(fs, ENAMEL_D)
    t["refrigerator_side"] = fs
    ff = noise(img(0), ENAMEL, 3, 8); frame(ff, ENAMEL_D); rect(ff, 1, 5, 14, 5, ENAMEL_D)
    rect(ff, 12, 1, 12, 4, METAL_D); rect(ff, 12, 7, 12, 13, METAL_D)
    t["refrigerator_front"] = ff
    ffo = ff.copy(); ffo.putpixel((2, 14), (90, 200, 120, 255))  # green "cold" indicator when powered
    t["refrigerator_front_on"] = ffo
    t["refrigerator_top"] = noise(img(0), ENAMEL, 3, 9)
    # --- electric range
    rs = noise(img(0), ENAMEL, 3, 10); frame(rs, ENAMEL_D)
    t["electric_range_side"] = rs
    rf = noise(img(0), ENAMEL, 3, 11); frame(rf, ENAMEL_D)
    for x in (3, 6, 9, 12):
        rf.putpixel((x, 2), BLACK + (255,))  # knobs
    rect(rf, 2, 5, 13, 13, METAL_D); rect(rf, 4, 7, 11, 11, (60, 50, 44)); rect(rf, 3, 4, 12, 4, METAL)
    t["electric_range_front"] = rf
    rfo = rf.copy(); rect(rfo, 4, 7, 11, 11, (120, 70, 40))
    t["electric_range_front_on"] = rfo

    def cooktop(lit):
        c = noise(img(0), (60, 60, 64), 4, 12); frame(c, METAL_D)
        for cx, cy in ((4, 4), (11, 4), (4, 11), (11, 11)):
            for x in range(cx - 2, cx + 2):
                for y in range(cy - 2, cy + 2):
                    if (x - cx + 0.5) ** 2 + (y - cy + 0.5) ** 2 <= 4.5:
                        c.putpixel((x, y), (GLOW if lit else (30, 30, 32)) + (255,))
        return c
    t["electric_range_top"] = cooktop(False)
    t["electric_range_top_on"] = cooktop(True)
    # --- space heater
    hs = noise(img(0), METAL, 5, 13); frame(hs, METAL_D)
    t["space_heater_side"] = hs

    def grille(lit):
        g = noise(img(0), METAL, 5, 14); frame(g, METAL_D)
        for y in range(3, 14, 2):
            rect(g, 2, y, 13, y, GLOW_HOT if lit else BLACK)
        return g
    t["space_heater_front"] = grille(False)
    t["space_heater_front_on"] = grille(True)
    t["space_heater_top"] = noise(img(0), METAL, 5, 15)
    # --- barometer
    bs = noise(img(0), WOOD, 9, 16); frame(bs, WOOD_D)
    t["barometer_side"] = bs
    bf = noise(img(0), WOOD, 9, 17); frame(bf, WOOD_D)
    for x in range(3, 13):
        for y in range(3, 13):
            if (x - 7.5) ** 2 + (y - 7.5) ** 2 <= 20:
                bf.putpixel((x, y), (236, 228, 200, 255))
    for i in range(4):
        bf.putpixel((7 + i // 2, 8 - i), (150, 30, 30, 255))
    rect(bf, 7, 7, 8, 8, (184, 140, 60))
    t["barometer_front"] = bf
    t["barometer_top"] = noise(img(0), WOOD, 9, 18)
    return t


# block -> (flag property or None, has "on" textures for faces)
BLOCKS = {
    "wind_turbine": ("powered", set()),
    "water_wheel": ("powered", set()),
    "refrigerator": ("powered", {"front"}),
    "electric_range": ("lit", {"front", "top"}),
    "space_heater": ("lit", {"front"}),
    "barometer": ("powered", set()),
}
ROT = {"north": 0, "east": 90, "south": 180, "west": 270}


def write(path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2) + "\n")


def main():
    for name, im in textures().items():
        p = ASSETS / "textures" / "block" / f"{name}.png"
        p.parent.mkdir(parents=True, exist_ok=True)
        im.save(p)
    for block, (flag, on_faces) in BLOCKS.items():
        def model(on):
            def tex(face):
                return f"{NS}:block/{block}_{face}" + ("_on" if on and face in on_faces else "")
            return {"parent": "minecraft:block/orientable", "textures": {
                "top": tex("top"), "front": tex("front"), "side": tex("side"), "particle": tex("side")}}
        write(ASSETS / "models" / "block" / f"{block}.json", model(False))
        variants = {}
        if on_faces:
            write(ASSETS / "models" / "block" / f"{block}_on.json", model(True))
        for facing, y in ROT.items():
            for val in ("false", "true"):
                m = f"{NS}:block/{block}" + ("_on" if (val == "true" and on_faces) else "")
                v = {"model": m}
                if y:
                    v["y"] = y
                variants[f"facing={facing},{flag}={val}"] = v
        write(ASSETS / "blockstates" / f"{block}.json", {"variants": variants})
        write(ASSETS / "items" / f"{block}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/{block}"}})
        write(DATA / "loot_table" / "blocks" / f"{block}.json", {
            "type": "minecraft:block",
            "pools": [{"rolls": 1, "bonus_rolls": 0.0, "conditions": [{"condition": "minecraft:survives_explosion"}],
                       "entries": [{"type": "minecraft:item", "name": f"{NS}:{block}"}]}],
            "random_sequence": f"{NS}:blocks/{block}"})
    print("assets generated in", ROOT)


if __name__ == "__main__":
    main()
