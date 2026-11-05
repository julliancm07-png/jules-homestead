#!/usr/bin/env python3
"""Generate the pack's own resource pack: resourcepacks/homestead-resources (all original, MIT).

Contents
  * Title screen: "Homestead Edition" banner (own pixel lettering), homestead splash texts, and — when captured by
    tools/client_smoke_test.py --scenario panorama — a panorama of the pack's own world.
  * FTB Quests theme: warm walnut book with cream text; chapter tags give Weather (frost) / Power (ember) variants.
  * Jade theme "homesteadres:homestead": walnut tooltip box with a gold frame (vanilla tooltip sprite format).
  * pack.png icon.
Usage: python3 -I tools/gen_resources.py
"""
import json
import pathlib
import random
import shutil

from PIL import Image, ImageDraw, ImageFilter

ROOT = pathlib.Path(__file__).resolve().parent.parent
OUT = ROOT / "resourcepacks" / "homestead-resources"
PANORAMA_SRC = ROOT / "resourcepacks" / "homestead-resources-src" / "panorama"
NS = "homesteadres"
PACK_FORMAT = 84  # Minecraft 26.1.2 resource_major (client version.json)

SPLASHES = """Check the barometer!
Batteries not included!
Now with refrigeration!
Mind the wind!
Bread for the bakery!
Glass counts as a roof!
Wear your fur!
Six days a sub-season!
Feed the herd!
Hidden wiring!
Taxes are due!
Tomatoes love summer!
Garlic overwinters!
Water wheels need flow!
12 m/s is perfect!
Hello, Jules!
Grow a town!
Light keeps you warm!
Shelter from the storm!
Fresh from the oven!
Root cellars rock!
Snow on the roof, soup on the stove!
Pin a quest!
Say hello to the neighbours!
Sun, wind and water!
Plant cabbage!
Hot soup!
Mind the gusts!
Stock up before the snow!
Home sweet homestead!
Barrels keep it cool!
Rice likes wet feet!
Cozy!
Close the windward door!
Mixed generation wins!
The fridge remembers the cold!
Sunday dinner!
Fair weather ahead?
A lantern in every window!
Compost makes rich soil!""".strip().splitlines()

# Original 5x7 pixel glyphs for the banner.
GLYPHS = {
    "H": ["X...X", "X...X", "X...X", "XXXXX", "X...X", "X...X", "X...X"],
    "O": [".XXX.", "X...X", "X...X", "X...X", "X...X", "X...X", ".XXX."],
    "M": ["X...X", "XX.XX", "X.X.X", "X.X.X", "X...X", "X...X", "X...X"],
    "E": ["XXXXX", "X....", "X....", "XXXX.", "X....", "X....", "XXXXX"],
    "S": [".XXXX", "X....", "X....", ".XXX.", "....X", "....X", "XXXX."],
    "T": ["XXXXX", "..X..", "..X..", "..X..", "..X..", "..X..", "..X.."],
    "A": [".XXX.", "X...X", "X...X", "XXXXX", "X...X", "X...X", "X...X"],
    "D": ["XXXX.", "X...X", "X...X", "X...X", "X...X", "X...X", "XXXX."],
    "I": ["XXX", ".X.", ".X.", ".X.", ".X.", ".X.", "XXX"],
    "N": ["X...X", "XX..X", "X.X.X", "X..XX", "X...X", "X...X", "X...X"],
    " ": ["..", "..", "..", "..", "..", "..", ".."],
}


def seamless(size, base, spread, seed, blur=1.2, grain=None):
    """Tileable noise texture: blur a 3x3 tiling of noise and crop the centre."""
    r = random.Random(seed)
    tile = Image.new("RGB", (size, size))
    for x in range(size):
        for y in range(size):
            d = r.randint(-spread, spread)
            if grain:
                d += int(grain * ((y * 7 + r.randint(0, 3)) % 11 - 5))
            tile.putpixel((x, y), tuple(max(0, min(255, c + d)) for c in base))
    big = Image.new("RGB", (size * 3, size * 3))
    for i in range(3):
        for j in range(3):
            big.paste(tile, (i * size, j * size))
    big = big.filter(ImageFilter.GaussianBlur(blur))
    return big.crop((size, size, 2 * size, 2 * size)).convert("RGBA")


def banner():
    scale, depth = 4, 2
    text = "HOMESTEAD EDITION"
    width = sum((len(GLYPHS[c][0]) + 1) * scale for c in text) - scale
    im = Image.new("RGBA", (512, 64), (0, 0, 0, 0))
    x0 = (512 - width) // 2
    y0 = (64 - 7 * scale) // 2 - depth * scale // 2
    face_top, face_bottom = (255, 240, 205), (232, 196, 128)
    side = (120, 78, 40)
    edge = (70, 44, 22)
    for layer in range(depth * scale, -1, -1):  # extrude down-right, then the face
        x = x0
        for c in text:
            g = GLYPHS[c]
            for gy, row in enumerate(g):
                for gx, ch in enumerate(row):
                    if ch != "X":
                        continue
                    for py in range(scale):
                        for px in range(scale):
                            X, Y = x + gx * scale + px + layer // 2, y0 + gy * scale + py + layer
                            if layer == 0:
                                t = (gy * scale + py) / (7 * scale)
                                col = tuple(int(a + (b - a) * t) for a, b in zip(face_top, face_bottom))
                            else:
                                col = edge if layer == depth * scale else side
                            if 0 <= X < 512 and 0 <= Y < 64:
                                im.putpixel((X, Y), col + (255,))
            x += (len(g[0]) + 1) * scale
    return im


def tooltip_sprites():
    bg = Image.new("RGBA", (100, 100), (0, 0, 0, 0))
    d = ImageDraw.Draw(bg)
    fill = (36, 25, 17, 238)
    d.rectangle([9, 8, 90, 91], fill=fill)
    d.rectangle([8, 9, 91, 90], fill=fill)
    fr = Image.new("RGBA", (100, 100), (0, 0, 0, 0))
    top, bottom = (232, 186, 104, 230), (150, 100, 52, 230)
    for y in range(9, 91):
        t = (y - 9) / 81
        col = tuple(int(a + (b - a) * t) for a, b in zip(top, bottom))
        fr.putpixel((9, y), col)
        fr.putpixel((90, y), col)
    for x in range(9, 91):
        fr.putpixel((x, 9), top)
        fr.putpixel((x, 90), bottom)
    return bg, fr


def pack_icon():
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for y in range(64):  # dusk sky
        t = y / 40
        d.line([(0, y), (63, y)], fill=(int(70 + 150 * t), int(90 + 80 * t), int(150 - 20 * t), 255))
    d.rectangle([0, 46, 63, 63], fill=(86, 140, 62, 255))  # field
    d.rectangle([0, 44, 63, 46], fill=(236, 240, 245, 255))  # snow line
    d.rectangle([16, 30, 46, 50], fill=(150, 104, 62, 255))  # cabin walls
    d.polygon([(12, 31), (31, 16), (50, 31)], fill=(236, 240, 245, 255))  # snowy roof
    d.polygon([(15, 31), (31, 19), (47, 31)], fill=(110, 60, 40, 255))
    d.rectangle([28, 40, 34, 50], fill=(84, 52, 30, 255))  # door
    for wx in (19, 38):
        d.rectangle([wx, 35, wx + 5, 40], fill=(255, 210, 120, 255))  # lit windows
    d.rectangle([40, 14, 44, 24], fill=(120, 110, 105, 255))  # chimney
    for i, (sx, sy) in enumerate([(43, 10), (46, 6), (50, 3)]):
        d.ellipse([sx - 2 - i, sy - 2 - i, sx + 2 + i, sy + 2 + i], fill=(225, 225, 230, 170))
    return im


THEME = """// Jules' Homestead — FTB Quests theme (original; MIT). Overrides FTB defaults (theme files merge).
[*]
background:               homesteadres:textures/gui/quest_bg_walnut.png; tile_size=64
key_reference_background: homesteadres:textures/gui/quest_bg_walnut.png; tile_size=64
chapter_panel_background: hollow_rectangle:{{widget_border}}; round_edges + homesteadres:textures/gui/quest_panel.png; tile_size=32; padding=1
context_menu:             hollow_rectangle:{{widget_border}}; round_edges + homesteadres:textures/gui/quest_panel.png; tile_size=32; padding=1
selected_chapter_highlight_1: #50F0C070
selected_chapter_highlight_2: #20F0C070
text_color:          #F5EBD7
hover_text_color:    #FFD27F
disabled_text_color: #A08E78
widget_border:       #23160D
widget_background:   #40000000
tasks_text_color:    #A9D1FF
rewards_text_color:  #FFC266
quest_view_title:    #E9D8B8
dependency_line_completed_color:   #8EE07A
dependency_line_uncompleted_color: #B4E8C9A0
dependency_line_unavailable_color: #64B89F80

// Weather chapter: frosty slate
[#weather]
background: homesteadres:textures/gui/quest_bg_frost.png; tile_size=64

// Power & kitchen chapters: ember-warm
[#power]
background: homesteadres:textures/gui/quest_bg_ember.png; tile_size=64
"""


def write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    if isinstance(data, Image.Image):
        data.save(path)
    elif isinstance(data, (dict, list)):
        path.write_text(json.dumps(data, indent=2) + "\n")
    else:
        path.write_text(data)


def main():
    if OUT.exists():
        shutil.rmtree(OUT)
    write(OUT / "pack.mcmeta", {"pack": {"description": "§6Jules' Homestead§r — screens, quest book & tooltips", "min_format": PACK_FORMAT, "max_format": PACK_FORMAT}})
    write(OUT / "pack.png", pack_icon())
    write(OUT / "assets/minecraft/texts/splashes.txt", "\n".join(SPLASHES) + "\n")
    write(OUT / "assets/minecraft/textures/gui/title/edition.png", banner())
    g = OUT / f"assets/{NS}/textures/gui"
    write(g / "quest_bg_walnut.png", seamless(64, (58, 40, 28), 10, 1, grain=0.8))
    write(g / "quest_bg_frost.png", seamless(64, (52, 62, 74), 9, 2, grain=0.5))
    write(g / "quest_bg_ember.png", seamless(64, (66, 38, 26), 10, 3, grain=0.8))
    write(g / "quest_panel.png", seamless(32, (84, 60, 42), 8, 4, grain=0.6))
    write(OUT / "assets/ftbquests/ftb_quests_theme.txt", THEME)
    bg, fr = tooltip_sprites()
    sp = OUT / f"assets/{NS}/textures/gui/sprites/tooltip"
    write(sp / "homestead_background.png", bg)
    write(sp / "homestead_background.png.mcmeta", {"gui": {"scaling": {"type": "nine_slice", "width": 100, "height": 100, "border": 9}}})
    write(sp / "homestead_frame.png", fr)
    write(sp / "homestead_frame.png.mcmeta", {"gui": {"scaling": {"type": "nine_slice", "width": 100, "height": 100, "border": 10, "stretch_inner": True}}})
    write(OUT / f"assets/{NS}/jade_themes/homestead.json", {"version": 200, "tooltipStyle": {"sprite": f"{NS}:homestead", "tooltip": True}})
    write(OUT / f"assets/{NS}/lang/en_us.json", {"jade.theme.homesteadres.homestead": "Homestead (walnut)"})
    pano = sorted(PANORAMA_SRC.glob("panorama_*.png")) if PANORAMA_SRC.exists() else []
    if len(pano) == 6:
        dest = OUT / "assets/minecraft/textures/gui/title/background"
        dest.mkdir(parents=True, exist_ok=True)
        for p in pano:
            shutil.copy(p, dest / p.name)
    (OUT / "LICENSE.txt").write_text("Jules' Homestead resources — original work, MIT licence (see repository LICENSE). Panorama images are in-game captures of the pack.\n")
    print(f"generated {OUT.relative_to(ROOT)} (panorama: {'yes' if len(pano) == 6 else 'not captured yet'})")


if __name__ == "__main__":
    main()
