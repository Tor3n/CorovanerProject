#!/usr/bin/env python3
"""Rebuild three Tiled locations using only the mountain-pass tilesets.

Uses the Python standard library. Running this overwrites the generated maps
and descriptors; hand edits in Tiled should be saved under a different name.
"""

import copy
import json
import math
from pathlib import Path
import random
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
MAPS = ROOT / "assets/maps"
SIZE = 64


def distance(x, y, points):
    result = float("inf")
    for (ax, ay), (bx, by) in zip(points, points[1:]):
        dx, dy = bx - ax, by - ay
        t = max(0, min(1, ((x - ax) * dx + (y - ay) * dy) / (dx * dx + dy * dy)))
        result = min(result, math.hypot(x - ax - t * dx, y - ay - t * dy))
    return result


def properties(element, values):
    parent = ET.SubElement(element, "properties")
    for name, value in values.items():
        attributes = {"name": name, "value": str(value)}
        if isinstance(value, bool):
            attributes.update(type="bool", value=str(value).lower())
        elif isinstance(value, float):
            attributes["type"] = "float"
        ET.SubElement(parent, "property", attributes)


class Location:
    def __init__(self, name, title, seed, routes, clearings, spawns):
        self.name, self.title = name, title
        self.rng = random.Random(seed)
        self.routes, self.clearings, self.spawns = routes, clearings, spawns
        self.layers = {}
        self.tileset_sources = {}
        self.add_layer("Ground", "ground")
        self.add_layer("Raised stone shelf", "ground", elevation=2.0)
        self.add_layer("Cliffs and obstacles", "cutout", solid=True)
        self.add_layer("Scattered details", "cutout")
        self.add_layer("Trees", "cutout", solid=True)
        for y in range(SIZE):
            for x in range(SIZE):
                # Broad patches with occasional variation, rather than white noise.
                patch = math.sin(x * .19) + math.cos(y * .23) + math.sin((x + y) * .11)
                palette = [2, 3, 5] if patch > .6 else [6, 8, 9]
                self.put("Ground", x, y, self.rng.choice(palette))

    def add_layer(self, name, role, elevation=0.0, solid=False):
        props = {"worldRole": role}
        if role == "ground":
            props["worldElevation"] = elevation
        if solid:
            props["worldSolid"] = True
        self.layers[name] = ([[0] * SIZE for _ in range(SIZE)], props)

    def put(self, layer, x, y, gid):
        if 0 <= x < SIZE and 0 <= y < SIZE:
            self.layers[layer][0][y][x] = gid

    def route_distance(self, x, y):
        return min(distance(x, y, path) - width for path, width in self.routes)

    def clearing(self, x, y, margin=0):
        return any(((x - cx) / (rx + margin)) ** 2 + ((y - cy) / (ry + margin)) ** 2 < 1
                   for cx, cy, rx, ry in self.clearings)

    def paths(self):
        for y in range(SIZE):
            for x in range(SIZE):
                d = self.route_distance(x, y)
                if d <= 0 or self.clearing(x, y):
                    self.put("Ground", x, y, self.rng.choice([16, 17, 19, 21, 23]))
                elif d < 1.3 and self.rng.random() < .6:
                    self.put("Ground", x, y, self.rng.choice([14, 26, 28, 30]))

    def vegetation(self, density, allowed=lambda x, y: True):
        trees = []
        for y in range(1, SIZE - 1):
            for x in range(1, SIZE - 1):
                if not allowed(x, y) or self.route_distance(x, y) < 2 or self.clearing(x, y, 2):
                    continue
                if self.layers["Cliffs and obstacles"][0][y][x]:
                    continue
                cluster = .7 + .3 * math.sin(x * .3) * math.cos(y * .27)
                if self.rng.random() < density * cluster and all((x - tx) ** 2 + (y - ty) ** 2 >= 8 for tx, ty in trees):
                    self.put("Trees", x, y, self.rng.choice([111, 112, 115, 116, 117, 118]))
                    trees.append((x, y))
                elif self.rng.random() < .12:
                    self.put("Scattered details", x, y, self.rng.choice([79, 80, 83, 84, 88, 90, 92, 93, 94, 95, 98, 103]))

    def save(self):
        template = ET.parse(MAPS / "mountinPass.tmx").getroot()
        active = [(name, grid, props) for name, (grid, props) in self.layers.items() if any(any(row) for row in grid)]
        root = ET.Element("map", {
            "version": "1.10", "tiledversion": "1.11.1", "orientation": "isometric",
            "renderorder": "right-down", "width": str(SIZE), "height": str(SIZE),
            "tilewidth": "64", "tileheight": "32", "infinite": "0",
            "nextlayerid": str(len(active) + 1), "nextobjectid": "1",
        })
        properties(root, {"locationName": self.title})
        for tileset in template.findall("tileset"):
            definition = copy.deepcopy(tileset)
            definition.set("source", self.tileset_sources.get(definition.get("source"), definition.get("source")))
            root.append(definition)
        for index, (name, grid, props) in enumerate(active, 1):
            layer = ET.SubElement(root, "layer", id=str(index), name=name, width=str(SIZE), height=str(SIZE))
            properties(layer, props)
            data = ET.SubElement(layer, "data", encoding="csv")
            data.text = "\n" + ",\n".join(",".join(map(str, row)) for row in grid) + "\n"
        ET.indent(root, space=" ")
        ET.ElementTree(root).write(MAPS / (self.name + ".tmx"), encoding="UTF-8", xml_declaration=True)
        with (MAPS / (self.name + ".tmx")).open("a") as output:
            output.write("\n")
        descriptor = {"map": self.name + ".tmx", "cellSize": 1.0,
                      "spawns": [{"column": x, "row": y} for x, y in self.spawns]}
        (MAPS / (self.name + ".world.json")).write_text(json.dumps(descriptor, indent=2) + "\n")
        print("Wrote", self.name)


def forest():
    location = Location("forestCrossroads", "Forest Crossroads", 1701, [
        ([(22, 63), (24, 48), (31, 33), (28, 19), (34, 0)], 2.0),
        ([(0, 27), (15, 25), (31, 33), (46, 29), (63, 20)], 1.8),
        ([(31, 33), (39, 43), (47, 45)], 1.3),
    ], [(31, 33, 6, 5), (47, 45, 7, 6)], [(23, 57), (25, 56), (23, 54)])
    location.paths()
    location.vegetation(.38)
    # A sheltered caravan stop beside the road: paired tent tiles, fire, supplies.
    for x, y, gid in [(45, 42, 55), (46, 42, 56), (49, 44, 57), (50, 44, 58)]:
        location.put("Cliffs and obstacles", x, y, gid)
    for x, y, gid in [(46, 46, 69), (43, 45, 63), (43, 46, 67), (50, 48, 65),
                      (48, 48, 98), (35, 30, 105), (20, 28, 103)]:
        location.put("Scattered details", x, y, gid)
    for x in range(42, 47):
        location.put("Cliffs and obstacles", x, 50, 73)
    location.save()


def quarry():
    location = Location("abandonedQuarry", "Abandoned Quarry", 2718, [
        ([(39, 63), (38, 50), (32, 38), (31, 26), (39, 15), (52, 0)], 2.4),
        ([(32, 38), (21, 36), (20, 24), (27, 17), (39, 15)], 1.7),
        ([(38, 50), (49, 45), (63, 44)], 1.6),
    ], [(30, 29, 12, 11), (49, 45, 7, 5)], [(39, 57), (37, 55), (40, 54)])
    location.paths()
    # A stepped escarpment uses the same straight/corner combinations as the pass.
    previous = 14
    for y in range(SIZE):
        edge = 14 - min(5, y // 11)
        for x in range(edge):
            location.put("Raised stone shelf", x, y, location.rng.choice([3, 7, 12, 13, 25]))
        if edge != previous:
            location.put("Cliffs and obstacles", edge, y, 39)
            location.put("Cliffs and obstacles", previous, y, 51)
        else:
            location.put("Cliffs and obstacles", edge, y, 35)
        previous = edge
    # Remaining rock seams leave two routes around the worked-out floor.
    for x, y in [(23, 25), (24, 25), (25, 25), (25, 26), (36, 30), (37, 30),
                 (38, 30), (37, 31), (26, 42), (27, 42), (27, 43), (48, 23), (49, 23)]:
        location.put("Cliffs and obstacles", x, y, location.rng.choice([99, 100, 101, 102]))
    for y in range(12, 50):
        for x in range(17, 54):
            if location.route_distance(x, y) > 1 and location.rng.random() < .08:
                location.put("Scattered details", x, y, location.rng.choice([95, 96, 97, 98, 103]))
    for x, y, gid in [(47, 42, 70), (48, 42, 67), (50, 42, 68), (52, 43, 63),
                      (47, 47, 65), (50, 47, 66), (42, 48, 105)]:
        location.put("Scattered details", x, y, gid)
    location.vegetation(.16, lambda x, y: x > 53 or (x > 17 and y > 51) or x < 7)
    location.save()


def graveyard():
    location = Location("forgottenGraveyard", "Forgotten Graveyard", 3141, [
        ([(32, 63), (32, 46), (32, 31), (32, 13)], 2.0),
        ([(17, 29), (32, 29), (48, 29)], 1.3),
        ([(32, 46), (47, 43), (53, 33), (52, 16), (43, 8), (32, 13)], 1.3),
    ], [(32, 13, 7, 6), (32, 45, 5, 4)], [(31, 57), (33, 55), (31, 53)])
    location.paths()
    # Sparse, irregular burial rows with a broad central aisle and cross-aisle.
    for y in [18, 23, 35, 40]:
        for x in [20, 24, 28, 37, 41, 45]:
            if (x, y) in [(24, 23), (41, 35), (20, 40)]:
                continue
            location.put("Ground", x, y + 1, 25)
            location.put("Ground", x, y + 2, 25)
            location.put("Cliffs and obstacles", x, y, location.rng.choice([107, 108, 109, 110]))
            if location.rng.random() < .4:
                location.put("Scattered details", x + 1, y + 1, 88)
    # Broken fence enclosure with a southern entrance and gaps onto the outer loop.
    for x in range(16, 50):
        if x not in range(29, 36) and x not in [21, 22, 44]:
            location.put("Cliffs and obstacles", x, 46, 73)
        if x not in range(28, 37):
            location.put("Cliffs and obstacles", x, 10, 73)
    for y in range(11, 46):
        if y not in [20, 21, 28, 29, 30, 38, 39]:
            location.put("Cliffs and obstacles", 16, y, 71)
            location.put("Cliffs and obstacles", 49, y, 71)
    for x, y, gid in [(29, 46, 101), (35, 46, 102), (29, 10, 99), (35, 10, 100),
                      (32, 10, 109), (27, 13, 107), (37, 13, 108)]:
        location.put("Cliffs and obstacles", x, y, gid)
    location.vegetation(.30, lambda x, y: x < 14 or x > 51 or y < 7 or y > 49)
    for x, y in [(18, 15), (46, 16), (18, 33), (46, 37), (27, 43), (38, 43)]:
        location.put("Scattered details", x, y, 94)
    location.save()


if __name__ == "__main__":
    forest()
    quarry()
    graveyard()
