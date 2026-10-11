"""Reload the rig and GLB; validate geometry, skins, clips, bounds and sockets."""
import hashlib
import json
import math
import struct
from pathlib import Path

import bmesh
import bpy
from mathutils import Vector, kdtree

ROOT = Path(__file__).resolve().parents[1]
FOLDER = ROOT / 'docs/characters/Protoganist'
ASSET = ROOT / 'assets/models/protagonist/protagonist.glb'
REPORT = FOLDER / 'rig_review/validation.json'


def evaluated_positions(obj):
    evaluated = obj.evaluated_get(bpy.context.evaluated_depsgraph_get())
    mesh = evaluated.to_mesh()
    result = [evaluated.matrix_world @ v.co for v in mesh.vertices]
    evaluated.to_mesh_clear()
    return result


def bounds(points):
    return {'min': [min(v[i] for v in points) for i in range(3)],
            'max': [max(v[i] for v in points) for i in range(3)]}


def distance(a, b):
    tree = kdtree.KDTree(len(b))
    for i, point in enumerate(b):
        tree.insert(point, i)
    tree.balance()
    return max(tree.find(point)[2] for point in a)


bpy.ops.wm.open_mainfile(filepath=str(FOLDER / 'maleprot_rig_master.blend'))
rig = bpy.data.objects['ProtagonistRig']
body = bpy.data.objects['ProtagonistBody']
gun = bpy.data.objects['Equipment_Rifle']
report = {'source_sha256': hashlib.sha256((FOLDER / 'maleprot_lowpoly_balanced_2k.blend').read_bytes()).hexdigest(),
          'glb_sha256': hashlib.sha256(ASSET.read_bytes()).hexdigest(),
          'source_height': .933405980045790, 'runtime_height': 2.2,
          'bones': [b.name for b in rig.data.bones], 'finger_bones': [],
          'clips': {}, 'meshes': {}, 'sockets': {}}
assert len(report['bones']) == 22
assert not any(any(word in n for word in ('finger', 'thumb', 'index')) for n in report['bones'])
for obj in [body, gun]:
    obj.data.calc_loop_triangles()
    bm = bmesh.new()
    bm.from_mesh(obj.data)
    error = max(abs(sum(g.weight for g in v.groups) - 1) for v in obj.data.vertices)
    entry = {'vertices': len(obj.data.vertices), 'triangles': len(obj.data.loop_triangles),
             'boundary_edges': sum(e.is_boundary for e in bm.edges),
             'nonmanifold_edges': sum(not e.is_manifold for e in bm.edges),
             'max_influences': max(len(v.groups) for v in obj.data.vertices),
             'unweighted_vertices': sum(not v.groups for v in obj.data.vertices),
             'max_weight_sum_error': error}
    bm.free()
    assert entry['boundary_edges'] == entry['nonmanifold_edges'] == entry['unweighted_vertices'] == 0
    assert entry['max_influences'] <= 4 and error < .000001
    report['meshes'][obj.name] = entry
all_positions = []
animated_reference = {}
socket_reference = {}
for clip in ['idle', 'walk', 'run', 'rig_test', 'neutral_idle', 'neutral_walk', 'neutral_run']:
    action = bpy.data.actions[clip]
    rig.animation_data.action = action
    start, end = map(int, action.frame_range)
    positions = []
    samples = []
    animated_reference[clip] = {}
    socket_reference[clip] = {}
    for frame in range(start, end + 1):
        bpy.context.scene.frame_set(frame)
        pts = evaluated_positions(body) + ([] if clip.startswith('neutral_') else evaluated_positions(gun))
        assert all(math.isfinite(c) for p in pts for c in p)
        positions.extend(pts)
        samples.append({'frame': frame, 'bounds': bounds(pts)})
        if frame in {start, start + (end - start) // 4, start + (end - start) // 2, start + 3 * (end - start) // 4, end}:
            animated_reference[clip][frame - start] = {o.name: evaluated_positions(o) for o in [body, gun]}
            socket_reference[clip][frame - start] = {n: bpy.data.objects[n].matrix_world.copy() for n in
                ['socket_weapon_r', 'socket_support_l', 'socket_back', 'socket_head']}
    all_positions.extend(positions)
    bpy.context.scene.frame_set(start)
    first = evaluated_positions(body)
    bpy.context.scene.frame_set(end)
    last = evaluated_positions(body)
    loop_error = max((a - b).length for a, b in zip(first, last))
    assert loop_error < .00001
    report['clips'][clip] = {'duration_seconds': (end - start) / 30, 'loop_position_error': loop_error,
                            'bounds_blender': bounds(positions), 'samples': samples}
report['all_clip_bounds_blender'] = bounds(all_positions)
scale = 2.2 / report['source_height']
report['locomotion_reference_speed'] = {'walk': .4 * scale / (16 / 30), 'run': .6 * scale / (12 / 30)}
rig.animation_data.action = bpy.data.actions['idle']
bpy.context.scene.frame_set(1)
for name in ['socket_weapon_r', 'socket_support_l', 'socket_back', 'socket_head']:
    obj = bpy.data.objects[name]
    report['sockets'][name] = {'parent_bone': obj.parent_bone, 'idle_position_blender': list(obj.matrix_world.translation)}
rig.data.pose_position = 'REST'
bpy.context.view_layer.update()
reference = {o.name: evaluated_positions(o) for o in [body, gun]}
with ASSET.open('rb') as stream:
    magic, version, length = struct.unpack('<III', stream.read(12))
    assert magic == 0x46546C67 and version == 2 and length == ASSET.stat().st_size
    size, kind = struct.unpack('<II', stream.read(8))
    gltf = json.loads(stream.read(size))
report['export'] = {'bytes': length, 'skin_joint_counts': [len(s['joints']) for s in gltf['skins']],
                    'animations': [a['name'] for a in gltf['animations']],
                    'nodes': [n.get('name') for n in gltf['nodes']],
                    'required_extensions': gltf.get('extensionsRequired', [])}
assert set(report['export']['animations']) == {'idle', 'walk', 'run', 'rig_test', 'neutral_idle', 'neutral_walk', 'neutral_run'}
assert 'root' in report['export']['nodes']
assert all(n in report['export']['nodes'] for n in report['sockets'])
assert max(report['export']['skin_joint_counts']) <= 32
assert not report['export']['required_extensions']
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.context.scene.render.fps = 30
bpy.ops.import_scene.gltf(filepath=str(ASSET))
for obj in bpy.context.scene.objects:
    if obj.type == 'ARMATURE':
        obj.data.pose_position = 'REST'
bpy.context.view_layer.update()
report['reimport'] = {}
for name, positions in reference.items():
    obj = bpy.data.objects[name]
    obj.data.calc_loop_triangles()
    reloaded = evaluated_positions(obj)
    error = max(distance(positions, reloaded), distance(reloaded, positions))
    assert len(obj.data.loop_triangles) == report['meshes'][name]['triangles']
    assert error < .00001, (name, error)
    report['reimport'][name] = {'triangles': len(obj.data.loop_triangles), 'vertices': len(obj.data.vertices),
                               'max_bidirectional_position_error': error}
report['reimport_images'] = [{'name': im.name, 'size': list(im.size)} for im in bpy.data.images if im.size[0]]
assert len(report['reimport_images']) == 3
assert all(i['size'] == [2048, 2048] for i in report['reimport_images'])
report['animated_reimport'] = {}
imported_rig = next(o for o in bpy.context.scene.objects if o.type == 'ARMATURE')
imported_rig.data.pose_position = 'POSE'
for track in imported_rig.animation_data.nla_tracks:
    track.mute = True
for clip, frames in animated_reference.items():
    action = bpy.data.actions[clip]
    imported_rig.animation_data.action = action
    imported_rig.animation_data.action_slot = action.slots[0]
    assert abs(action.frame_range[0]) < .00001
    assert abs(action.frame_range[1] / 30 - report['clips'][clip]['duration_seconds']) < .00001
    max_error, socket_error = 0, 0
    for frame, meshes in frames.items():
        bpy.context.scene.frame_set(frame)
        for name, positions in meshes.items():
            actual = evaluated_positions(bpy.data.objects[name])
            max_error = max(max_error, distance(positions, actual), distance(actual, positions))
        for name, expected in socket_reference[clip][frame].items():
            actual = bpy.data.objects[name].matrix_world
            socket_error = max(socket_error, max(abs(actual[i][j] - expected[i][j]) for i in range(4) for j in range(4)))
    assert max_error < .00005, (clip, max_error)
    assert socket_error < .00005, (clip, socket_error)
    report['animated_reimport'][clip] = {'max_bidirectional_position_error': max_error,
                                        'max_socket_matrix_element_error': socket_error,
                                        'sample_frames': list(frames)}
REPORT.write_text(json.dumps(report, indent=2), encoding='utf-8')
print('VALIDATED', REPORT)
print(json.dumps({k: report[k] for k in ['meshes', 'export', 'reimport', 'locomotion_reference_speed', 'all_clip_bounds_blender']}, indent=2))
