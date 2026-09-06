#!/usr/bin/env python3
"""Rebuild the three destination-specific maps. Existing wilderness maps are untouched."""
import json
import math
from generate_locations import Location, MAPS, SIZE


def part(x, y, z, width, height, depth, color, shape="box", **rotation):
    return dict(x=x, y=y, z=z, width=width, height=height, depth=depth,
                color=color, shape=shape, **rotation)


def structure(column, row, width, depth, parts):
    return dict(column=column, row=row, footprintWidth=width, footprintDepth=depth, parts=parts)


def tower(column, row, water=False):
    iron, rust = "454f4a", "75513b"
    parts = []
    for x in [-1.1, 1.1]:
        for z in [-1.1, 1.1]:
            parts += [part(x, 2.6, z, .17, 5.2, .17, iron), part(x, .12, z, .5, .24, .5, "817c68")]
    for y in [1.4, 3.6]:
        for z in [-1.1, 1.1]:
            parts.append(part(0, y, z, 3.05, .12, .12, rust, roll=44))
            parts.append(part(0, y, z, 3.05, .12, .12, iron, roll=-44))
        for x in [-1.1, 1.1]:
            parts.append(part(x, y, 0, .12, .12, 3.05, rust, pitch=44))
    parts.append(part(0, 5.05, 0, 2.9, .2, 2.9, rust))
    if water:
        parts.append(part(0, 6.25, 0, 2.7, 2.25, 2.7, "658079", "cylinder"))
        for y in [5.2, 5.7, 6.8, 7.35]:
            parts.append(part(0, y, 0, 2.78, .12, 2.78, rust, "cylinder"))
        parts.append(part(0, 7.45, 0, 2.86, .16, 2.86, "484f48", "cylinder"))
        parts.append(part(1.6, 2.7, 0, .14, 5.4, .14, rust, "cylinder"))
        parts.append(part(1.8, .6, 0, .7, .12, .12, iron))
    else:
        parts.append(part(0, 6.6, 0, .22, 3.2, .22, iron))
        for y, width in [(6, 3.8), (7, 2.7), (7.9, 1.7)]:
            parts.append(part(0, y, 0, width, .1, .1, "a29b7f"))
            for x in [-width / 2, width / 2]:
                parts.append(part(x, y, 0, .09, .75, .09, rust))
        parts.append(part(0, 8.35, 0, .17, .22, .17, "d08543"))
        parts.append(part(.7, 5.7, 0, .12, 1.3, 1.3, "a8ad9b", "cylinder", roll=90))
    # Ladder faces the approach; rungs share the tower's rust palette.
    for x in [-.3, .3]:
        parts.append(part(x, 2.55, 1.42, .07, 5.1, .07, rust))
    for i in range(16):
        parts.append(part(0, .25 + i * .3, 1.42, .6, .065, .065, iron))
    return structure(column, row, 3.3, 3.3, parts)


def shelter(column, row, color="837a56"):
    parts = [part(0, .1, 0, 4.2, .2, 3.4, "696553"),
             part(0, 2.6, 0, 4.5, .14, 3.7, color, roll=5),
             part(0, 1.1, -1.55, 4.1, 2, .15, "685543"),
             part(0, .8, 1, 3.4, .17, .65, "8b7657")]
    for x in [-1.85, 1.85]:
        for z in [-1.4, 1.4]:
            parts.append(part(x, 1.3, z, .16, 2.6, .16, "514e40"))
    for x in [-1.25, -.35, .75]:
        parts.append(part(x, .45, -.7, .7, .7, .65, "89734f"))
    for x in [-1.4, -.7, 0, .7, 1.4]:
        parts.append(part(x, 2.74 + x * .087, 0, .06, .06, 3.7, "b2a079"))
    return structure(column, row, 4.6, 3.8, parts)


def save(location, structures):
    location.save()
    path = MAPS / (location.name + ".world.json")
    descriptor = json.loads(path.read_text())
    descriptor["structures"] = structures
    path.write_text(json.dumps(descriptor, indent=2) + "\n")


def mercy():
    loc = Location("mercyCrossing", "Mercy Crossing", 4610, [
        ([(0, 36), (20, 36), (34, 34), (63, 32)], 2.3),
        ([(32, 63), (32, 42), (34, 34), (36, 0)], 2.1),
        ([(20, 36), (20, 23), (42, 23), (44, 35)], 1.4),
    ], [(33, 35, 12, 10), (29, 23, 14, 5)], [(31, 43), (34, 43), (32, 46)])
    loc.tileset_sources = {
        "background.tsx": "wasteland_background.tsx", "mountin.tsx": "wasteland_mountain.tsx",
        "items1.tsx": "wasteland_items1.tsx", "items2.tsx": "wasteland_items2.tsx",
        "trees1.tsx": "wasteland_trees1.tsx", "trees2.tsx": "wasteland_trees2.tsx",
    }
    loc.paths()
    # The new atlas keeps GIDs but changes their meaning. Avoid randomly scattering
    # hazard stripes/toxic ground where the old generator chose grass variants.
    # This consumes no RNG, preserving the authored prop and tree placement.
    for y in range(SIZE):
        for x in range(SIZE):
            road = loc.route_distance(x, y)
            patch = math.sin((x // 4) * 1.3) + math.cos((y // 4) * .9)
            if road <= 0 or loc.clearing(x, y):
                tile = 19 if patch > .6 else 22 if patch < -.6 else 18
            elif road < 1.3:
                tile = 20
            else:
                tile = 2 if patch > .5 else 9 if patch < -.5 else 13
            loc.put("Ground", x, y, tile)
    for x in [30, 31, 32, 33, 34]:
        loc.put("Ground", x, 46, 6)
    loc.vegetation(.15, lambda x, y: x < 12 or x > 51 or y < 10 or y > 52)
    for x in range(17, 48):
        if x not in range(28, 38):
            loc.put("Cliffs and obstacles", x, 46, 73)
    # Join each tent's atlas halves horizontally in screen space.
    for x, y, gid in [(21, 28, 55), (22, 27, 56), (24, 28, 57), (25, 27, 58),
                      (41, 42, 63), (42, 42, 67), (40, 41, 68), (28, 33, 69),
                      (25, 40, 65), (38, 39, 66), (29, 46, 105)]:
        loc.put("Cliffs and obstacles" if gid != 69 else "Scattered details", x, y, gid)
    save(loc, [tower(40, 29, True), shelter(24, 36), shelter(34, 23, "687e74")])


def relay():
    loc = Location("saintsRelay", "Saint's Relay", 5720, [
        ([(30, 63), (30, 48), (27, 38), (30, 28), (43, 20), (63, 17)], 2.1),
        ([(27, 38), (41, 39), (45, 30), (43, 20)], 1.6),
    ], [(34, 31, 13, 13)], [(28, 43), (30, 45), (27, 46)])
    loc.paths()
    for y in range(SIZE):
        for x in range(9):
            loc.put("Raised stone shelf", x, y, loc.rng.choice([7, 12, 13, 25]))
        loc.put("Cliffs and obstacles", 9, y, 35)
    loc.vegetation(.10, lambda x, y: x > 52 or y > 52)
    for x in range(20, 50):
        if x not in range(24, 33) and x not in range(40, 47):
            loc.put("Cliffs and obstacles", x, 43, 73)
        if x not in range(39, 47):
            loc.put("Cliffs and obstacles", x, 18, 73)
    for x, y, gid in [(42, 36, 67), (43, 36, 70), (45, 36, 68), (24, 26, 99),
                      (22, 31, 100), (48, 25, 102), (26, 46, 105)]:
        loc.put("Cliffs and obstacles", x, y, gid)
    save(loc, [tower(35, 29), shelter(39, 38, "666d66")])


def salt():
    loc = Location("saltGraves", "The Salt Graves", 6830, [
        ([(30, 63), (30, 44), (31, 27), (31, 10)], 2),
        ([(12, 29), (31, 29), (53, 29)], 1.5),
        ([(30, 44), (48, 43), (54, 30), (49, 14), (31, 10)], 1.3),
    ], [(31, 12, 8, 6), (31, 43, 6, 5)], [(30, 46), (32, 48), (29, 49)])
    loc.paths()
    loc.layers["Ground"][1].update(worldTint="b5abff", worldBrightness=3.3)
    markers = []
    for y in [17, 22, 34, 39]:
        for x in [18, 23, 27, 37, 42, 47]:
            if (x + y) % 7 == 0: continue
            loc.put("Ground", x, y + 1, 25)
            loc.put("Ground", x, y + 2, 25)
            markers.append(structure(x, y, .65, .65, [
                part(0, .13, 0, .65, .26, .6, "484a43"),
                part(0, .75, 0, .23, 1.4, .22, "292e2a", roll=loc.rng.choice([-8, 0, 5])),
                part(0, 1.03, 0, .85, .17, .2, "333b35")]))
    for i in range(90):
        x, y = loc.rng.randrange(2, 62), loc.rng.randrange(2, 62)
        if loc.route_distance(x, y) > 2 and not loc.clearing(x, y):
            loc.put("Scattered details", x, y, loc.rng.choice([95, 96, 97, 98]))
    markers.append(structure(31, 10, 2, 2, [part(0, .2, 0, 2, .4, 2, "656858"),
        part(0, 1.9, 0, .8, 3.4, .8, "383e36"), part(0, 3.7, 0, 1.1, .25, 1.1, "555d50")]))
    save(loc, markers)


if __name__ == "__main__":
    mercy()
    relay()
    salt()
