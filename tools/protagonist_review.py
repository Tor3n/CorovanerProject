"""Render fitted skeleton overlays without saving the rig or runtime asset.

Run from any directory with Blender --background --disable-autoexec
--python-exit-code 1 --python tools/protagonist_review.py.
The joints are fitted authoring landmarks, not anatomical measurements.
"""
import hashlib
import json
import sys
import warnings
from pathlib import Path

import bpy
from mathutils import Vector

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / 'tools'))
from protagonist_pipeline import MASTER, OUT, ASSET, setup_render, look_at

warnings.filterwarnings('ignore', category=DeprecationWarning)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def material(name, color):
    mat = bpy.data.materials.new(name)
    mat.use_nodes = True
    nodes = mat.node_tree.nodes
    nodes.clear()
    emission = nodes.new('ShaderNodeEmission')
    emission.inputs['Color'].default_value = (*color, 1)
    emission.inputs['Strength'].default_value = 1
    output = nodes.new('ShaderNodeOutputMaterial')
    mat.node_tree.links.new(emission.outputs[0], output.inputs['Surface'])
    return mat


def line(points, mat, radius, dashed=False):
    curve = bpy.data.curves.new('Review overlay line', 'CURVE')
    curve.dimensions = '3D'
    curve.bevel_depth = radius
    curve.bevel_resolution = 2
    for a, b in zip(points, points[1:]):
        count = max(1, int((b - a).length / .018)) if dashed else 1
        for index in range(count):
            if dashed and index % 2:
                continue
            segment = curve.splines.new('POLY')
            segment.points.add(1)
            segment.points[0].co = (*(a.lerp(b, index / count)), 1)
            segment.points[1].co = (*(a.lerp(b, (index + 1) / count)), 1)
    obj = bpy.data.objects.new('Review overlay line', curve)
    bpy.context.collection.objects.link(obj)
    curve.materials.append(mat)
    return obj


def label(camera, text, position, mat, size=.034):
    data = bpy.data.curves.new('Review overlay label', 'FONT')
    data.body = text
    data.size = size
    obj = bpy.data.objects.new('Review overlay label', data)
    bpy.context.collection.objects.link(obj)
    obj.matrix_world = camera.matrix_world.copy()
    obj.location = camera.matrix_world @ Vector((*position, -.5))
    data.materials.append(mat)
    return obj


def arm_points(rig, side, rest):
    bones = rig.data.bones if rest else rig.pose.bones
    attr_head = 'head_local' if rest else 'head'
    attr_tail = 'tail_local' if rest else 'tail'
    return [rig.matrix_world @ getattr(bones[name + '_' + side], attr_head)
            for name in ['upper_arm', 'forearm', 'hand']] + [
                rig.matrix_world @ getattr(bones['hand_' + side], attr_tail)]


def main():
    before = {str(p): digest(p) for p in [MASTER, ASSET]}
    bpy.ops.wm.open_mainfile(filepath=str(MASTER))
    rig = bpy.data.objects['ProtagonistRig']
    rig.animation_data.action = bpy.data.actions['idle']
    bpy.context.scene.frame_set(1)
    scene = setup_render()
    scene.cycles.samples = 24
    scene.render.resolution_x = scene.render.resolution_y = 960
    camera = scene.camera
    camera.data.ortho_scale = 3.15
    colors = {'left': material('Review left green', (.12, 1, .30)),
              'right': material('Review right magenta', (1, .15, .52)),
              'mirror': material('Review mirrored amber', (1, .72, .08)),
              'center': material('Review center white', (.9, .95, 1))}
    report = {'meaning': 'Fitted rig joint lengths, not measured human anatomy or evaluated skin proportions.',
              'center_plane_blender': 'X=0, recentered from accepted source torso X=-0.04682',
              'sides': 'Anatomical left=+X; front view shows anatomical left on image right.',
              'landmarks': ['shoulder=upper_arm head', 'elbow=forearm head',
                            'wrist=hand head', 'distal_hand=hand tail (not fingertip)'],
              'length_units': 'Blender units; character rest height 2.2',
              'rest_lengths': {}, 'relative_difference_percent': {}, 'views': []}
    segments = ['upper_arm', 'forearm', 'hand_landmark']
    for side in ['l', 'r']:
        points = arm_points(rig, side, True)
        report['rest_lengths'][side] = dict(zip(segments, [(b-a).length for a,b in zip(points,points[1:])]))
        report['rest_lengths'][side]['total'] = sum(report['rest_lengths'][side].values())
    for name in segments + ['total']:
        report['relative_difference_percent'][name] = 100 * abs(
            report['rest_lengths']['r'][name] / report['rest_lengths']['l'][name] - 1)
    for pose in ['rest', 'idle']:
        rest = pose == 'rest'
        rig.data.pose_position = 'REST' if rest else 'POSE'
        bpy.data.objects['Equipment_Rifle'].hide_render = rest
        bpy.context.view_layer.update()
        left, right = [arm_points(rig, side, rest) for side in ['l', 'r']]
        mirrored = [Vector((-p.x, p.y, p.z)) for p in left]
        for view, offset in [('front', (0, -5, 0)), ('iso', (3, -3, 2.45))]:
            camera.location = Vector((0, -.1, 1.1)) + Vector(offset)
            look_at(camera, (0, -.1, 1.1))
            bpy.context.view_layer.update()
            inverse = camera.matrix_world.inverted()
            def project(point, depth=1.01):
                local = inverse @ point
                return camera.matrix_world @ Vector((local.x, local.y, -depth))
            overlays = []
            overlays.append(line([project(Vector((0, 0, z))) for z in [0, 2.3]], colors['center'], .002, True))
            for points, name, dashed, radius in [(left, 'left', False, .004),
                                                   (right, 'right', False, .004),
                                                   (mirrored, 'mirror', True, .0025)]:
                overlays.append(line([project(p, 1 if dashed else 1.01) for p in points], colors[name], radius, dashed))
                if not dashed:
                    for point in points:
                        bpy.ops.mesh.primitive_uv_sphere_add(segments=12, ring_count=6, radius=.009,
                                                            location=project(point, .99))
                        marker = bpy.context.object
                        marker.name = 'Review fitted joint marker'
                        marker.data.materials.append(colors[name])
                        overlays.append(marker)
            for text, position, mat in [
                (pose.upper() + ' / ' + view.upper() + ' - fitted joints', (-1.49, 1.44), 'center'),
                ('Left (+X)', (-1.49, -1.24), 'left'),
                ('Right (-X)', (-1.49, -1.31), 'right'),
                ('Dashed: mirrored left around X=0', (-1.49, -1.38), 'mirror'),
                ('Hand endpoint is a rig landmark, not fingertip.', (-1.49, -1.45), 'center')]:
                overlays.append(label(camera, text, position, colors[mat]))
            name = 'overlay_' + pose + '_' + view + '.png'
            scene.render.filepath = str(OUT / name)
            bpy.ops.render.render(write_still=True)
            report['views'].append(name)
            for obj in overlays:
                bpy.data.objects.remove(obj, do_unlink=True)
    # Assign an explicit temporary matte material for readable untextured grip
    # close-ups. Existing review images and delivery files remain untouched.
    clay = bpy.data.materials.new('Review dark matte clay')
    clay.use_nodes = True
    bsdf = clay.node_tree.nodes.get('Principled BSDF')
    bsdf.inputs['Base Color'].default_value = (.08, .10, .13, 1)
    bsdf.inputs['Roughness'].default_value = 1
    bsdf.inputs['Specular IOR Level'].default_value = .15
    body = bpy.data.objects['ProtagonistBody']
    body.data.materials.clear()
    body.data.materials.append(clay)
    bpy.data.objects['Equipment_Rifle'].hide_render = True
    camera.data.ortho_scale = .75
    for view, direction in [('front', (0, -4, 0)), ('iso', (3, -3, 2.45)),
                            ('underside', (0, -2, -4))]:
        center = Vector((0, -.52, 1.5))
        camera.location = center + Vector(direction)
        look_at(camera, center)
        scene.render.filepath = str(OUT / ('overlay_clay_grip_' + view + '.png'))
        bpy.ops.render.render(write_still=True)
        report['views'].append('overlay_clay_grip_' + view + '.png')
    report['interpretation'] = ('Symmetric fitted arm chains establish a stable rig contract. '
        'Idle arms intentionally differ to fit rifle grips; mirrored idle lines are comparison guides, '
        'not desired targets. These overlays do not establish equal skin proportions or exact anatomy.')
    assert before == {str(p): digest(p) for p in [MASTER, ASSET]}, 'Review must not modify delivery files'
    (OUT / 'overlay_measurements.json').write_text(json.dumps(report, indent=2), encoding='utf-8')
    print('OVERLAY_REVIEW', json.dumps(report))


if __name__ == '__main__':
    main()
