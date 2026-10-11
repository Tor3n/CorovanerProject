"""Reproducible Blender protagonist pipeline. Run with --background --disable-autoexec.

Source is never saved over. Pass -- after Blender arguments, then baseline or build.
"""
import argparse
import json
import math
import sys
from pathlib import Path

import bpy
import bmesh
from mathutils import Vector, Matrix, Quaternion

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'docs/characters/Protoganist/maleprot_lowpoly_balanced_2k.blend'
OUT = ROOT / 'docs/characters/Protoganist/rig_review'
MASTER = SOURCE.with_name('maleprot_rig_master.blend')
ASSET = ROOT / 'assets/models/protagonist/protagonist.glb'
CENTER = -.04682
GROUND = -.000144704754347913
SCALE = 2.2 / .933405980045790


def p(x, y, z):
    return Vector((x * SCALE, y * SCALE, (z - GROUND) * SCALE))


def smoothstep(a, b, x):
    t = max(0, min(1, (x - a) / (b - a)))
    return t * t * (3 - 2 * t)


def look_at(obj, target):
    obj.rotation_euler = (Vector(target) - obj.location).to_track_quat('-Z', 'Y').to_euler()


def setup_render():
    scene = bpy.context.scene
    scene.render.engine = 'CYCLES'
    scene.cycles.samples = 12
    scene.cycles.use_denoising = True
    scene.render.resolution_x = 640
    scene.render.resolution_y = 640
    scene.render.resolution_percentage = 100
    scene.render.image_settings.file_format = 'PNG'
    scene.world.color = (0.25, 0.25, 0.25)
    scene.view_settings.view_transform = 'Standard'
    for obj in list(scene.objects):
        if obj.type in {'CAMERA', 'LIGHT'}:
            bpy.data.objects.remove(obj, do_unlink=True)
    bpy.ops.object.camera_add()
    scene.camera = bpy.context.object
    scene.camera.data.type = 'ORTHO'
    for pos, power, size in [((2, -3, 4), 350, 4), ((-2, -1, 2), 220, 3), ((0, 3, 3), 300, 3)]:
        bpy.ops.object.light_add(type='AREA', location=pos)
        light = bpy.context.object
        light.data.energy = power
        light.data.shape = 'DISK'
        light.data.size = size
        look_at(light, (0, 0, .5))
    scene.render.film_transparent = False
    return scene


def render_views(prefix, center, extent, names=None):
    scene = bpy.context.scene
    views = {'front': (0, -4, 0), 'back': (0, 4, 0), 'side': (4, 0, 0),
             'top': (0, 0, 4), 'underside': (0, -2, -4), 'iso': (3, -3, 2.45)}
    for name, direction in views.items():
        if names and name not in names:
            continue
        scene.camera.location = Vector(center) + Vector(direction)
        look_at(scene.camera, center)
        scene.camera.data.ortho_scale = extent
        scene.render.filepath = str(OUT / (prefix + '_' + name + '.png'))
        bpy.ops.render.render(write_still=True)


def baseline():
    bpy.ops.wm.open_mainfile(filepath=str(SOURCE))
    mesh = next(o for o in bpy.context.scene.objects if o.type == 'MESH')
    OUT.mkdir(parents=True, exist_ok=True)
    mesh.data.calc_loop_triangles()
    report = {'source': str(SOURCE.relative_to(ROOT)), 'vertices': len(mesh.data.vertices),
              'triangles': len(mesh.data.loop_triangles), 'dimensions': list(mesh.dimensions),
              'positions': [list(v.co) for v in mesh.data.vertices],
              'faces': [list(p.vertices) for p in mesh.data.polygons]}
    (OUT / 'baseline_mesh.json').write_text(json.dumps(report), encoding='utf-8')
    setup_render()
    render_views('baseline', (-.0468, 0, .47), 1.25)


def repair_and_grip(mesh):
    bm = bmesh.new()
    bm.from_mesh(mesh.data)
    # Two pre-existing sliver flaps independently identified by the baseline critic.
    bad = [f for f in bm.faces if sum(e.is_boundary for e in f.edges) == 1
           and sum(len(e.link_faces) > 2 for e in f.edges) == 2 and f.calc_area() < .000001]
    assert len(bad) == 2, 'Source topology changed; review repair before proceeding'
    bmesh.ops.delete(bm, geom=bad, context='FACES')
    assert all(len(e.link_faces) == 2 for e in bm.edges)
    bm.to_mesh(mesh.data)
    bm.free()
    for v in mesh.data.vertices:
        x, y, z = v.co
        side = 1 if x > CENTER else -1
        u = side * (x - CENTER)
        if u > .442:
            # Fixed finger bank curls into a C around an equipment-sized opening.
            # Thumb lies on the -Y edge, isolated with a smooth region mask.
            thumb = (1 - smoothstep(-.010, .008, y)) * (1 - smoothstep(.488, .511, u))
            theta = max(0, (u - .482) / .025)
            theta = min(theta, 2.60)
            thickness = z - .724
            curled_u = .482 + (.025 + thickness) * math.sin(theta)
            curled_z = .724 - .025 + (.025 + thickness) * math.cos(theta)
            bank = smoothstep(.476, .490, u) * (1 - thumb)
            u2 = u * (1 - bank) + curled_u * bank
            z2 = z * (1 - bank) + curled_z * bank
            thumb_tip = smoothstep(.448, .492, u) * thumb
            y += .017 * thumb_tip
            z2 -= .020 * thumb_tip
            x = CENTER + side * u2
            z = z2
        v.co = p(x - CENTER, y, z)
    mesh.data.update()
    # Transporting the original split normals through the curl is unnecessary here:
    # explicitly recompute smooth geometric normals and review with the baked normal map.
    for face in mesh.data.polygons:
        face.use_smooth = True
    if mesh.data.has_custom_normals:
        mesh.data.normals_split_custom_set([(0, 0, 0)] * len(mesh.data.loops))


def make_rig(mesh):
    data = bpy.data.armatures.new('ProtagonistSkeleton')
    rig = bpy.data.objects.new('ProtagonistRig', data)
    bpy.context.collection.objects.link(rig)
    bpy.context.view_layer.objects.active = rig
    rig.select_set(True)
    bpy.ops.object.mode_set(mode='EDIT')
    def bone(name, head, tail, parent=None, deform=True):
        b = data.edit_bones.new(name)
        b.head, b.tail = p(*head), p(*tail)
        b.use_deform = deform
        if parent:
            b.parent = data.edit_bones[parent]
        return b
    bone('root', (0, 0, GROUND), (0, 0, .10), deform=False)
    bone('hips', (0, .025, .475), (0, .025, .55), 'root')
    bone('spine', (0, .025, .55), (0, .028, .64), 'hips')
    bone('chest', (0, .028, .64), (0, .03, .75), 'spine')
    bone('neck', (0, .03, .75), (0, .025, .807), 'chest')
    bone('head', (0, .025, .807), (0, .015, .915), 'neck')
    for suffix, s in [('l', 1), ('r', -1)]:
        bone('clavicle_' + suffix, (0, .03, .745), (s * .132, .035, .737), 'chest')
        bone('upper_arm_' + suffix, (s * .132, .035, .737), (s * .29, .038, .731), 'clavicle_' + suffix)
        bone('forearm_' + suffix, (s * .29, .038, .731), (s * .431, .026, .725), 'upper_arm_' + suffix)
        bone('hand_' + suffix, (s * .431, .026, .725), (s * .50, .026, .725), 'forearm_' + suffix)
        bone('thigh_' + suffix, (s * .062, .023, .488), (s * .087, .020, .263), 'hips')
        bone('shin_' + suffix, (s * .087, .020, .263), (s * .103, .020, .062), 'thigh_' + suffix)
        bone('foot_' + suffix, (s * .103, .020, .062), (s * .108, -.042, .022), 'shin_' + suffix)
        bone('toe_' + suffix, (s * .108, -.042, .022), (s * .108, -.068, .020), 'foot_' + suffix)
    bpy.ops.object.mode_set(mode='OBJECT')
    bpy.ops.object.select_all(action='DESELECT')
    mesh.select_set(True)
    rig.select_set(True)
    bpy.context.view_layer.objects.active = rig
    bpy.ops.object.parent_set(type='ARMATURE_AUTO')
    assert mesh.vertex_groups, 'Automatic skin binding failed'
    # Hands remain rigid; no finger bone, finger control, or animated finger weight exists.
    groups = {g.name: g for g in mesh.vertex_groups}
    for v in mesh.data.vertices:
        u = abs(v.co.x) / SCALE
        if u > .405:
            for g in mesh.vertex_groups:
                g.remove([v.index])
            side = 'l' if v.co.x > 0 else 'r'
            weight = smoothstep(.405, .463, u)
            groups['hand_' + side].add([v.index], weight, 'REPLACE')
            groups['forearm_' + side].add([v.index], 1 - weight, 'REPLACE')
    for v in mesh.data.vertices:
        weights = sorted([(g.group, g.weight) for g in v.groups if g.weight > .00001], key=lambda a: -a[1])[:4]
        assert weights, 'Unweighted vertex ' + str(v.index)
        total = sum(w for _, w in weights)
        for group in mesh.vertex_groups:
            group.remove([v.index])
        for index, weight in weights:
            mesh.vertex_groups[index].add([v.index], weight / total, 'REPLACE')
    rig.show_in_front = True
    data.display_type = 'OCTAHEDRAL'
    for pb in rig.pose.bones:
        pb.rotation_mode = 'QUATERNION'
    # Keep an explicit, unweighted ground root in the exported hierarchy.
    # Enable only AFTER heat binding so the root cannot attract foot weights.
    data.bones['root'].use_deform = True
    return rig


def aim_bone(rig, name, head, tail, rotation=None):
    rest = rig.data.bones[name]
    direction = (tail - head).normalized()
    rot = rotation or (rest.tail_local - rest.head_local).normalized().rotation_difference(direction)
    mat = (rot.to_matrix() @ rest.matrix_local.to_3x3()).to_4x4()
    mat.translation = head
    rig.pose.bones[name].matrix = mat
    bpy.context.view_layer.update()
    return rot


def solve_joint(start, end, length_a, length_b, pole):
    axis = end - start
    distance = min(axis.length, (length_a + length_b) * .998)
    axis.normalize()
    along = (length_a ** 2 - length_b ** 2 + distance ** 2) / (2 * distance)
    height = math.sqrt(max(0, length_a ** 2 - along ** 2))
    bend = pole - start
    bend -= axis * bend.dot(axis)
    bend.normalize()
    return start + along * axis + height * bend


def arm_rotation(side, direction, palm):
    x = direction.normalized() * side
    z = -palm.normalized()
    z = (z - x * z.dot(x)).normalized()
    y = z.cross(x).normalized()
    return Matrix((x, y, z)).transposed().to_quaternion()


def pose(rig, phase, gait, diagnostic=False, neutral=False):
    for pb in rig.pose.bones:
        pb.matrix_basis.identity()
    bob = .003 * math.sin(phase * 2) if gait == 0 else (-.027 if gait == 1 else -.055) + .007 * (1 - math.cos(phase * 2))
    if diagnostic:
        bob -= .15 * math.sin(phase / 2) ** 2
    hip_offset = Vector((0, 0, bob * SCALE))
    turn = Quaternion((0, 0, 1), .35 * math.sin(phase) if diagnostic else 0)
    pivot = p(0, .025, .475)
    def torso(v):
        return pivot + turn @ (v - pivot) + hip_offset
    for name in ['hips', 'spine', 'chest', 'neck', 'head']:
        b = rig.data.bones[name]
        aim_bone(rig, name, torso(b.head_local), torso(b.tail_local), turn)
    for suffix, s in [('l', 1), ('r', -1)]:
        b = rig.data.bones['clavicle_' + suffix]
        aim_bone(rig, b.name, torso(b.head_local), torso(b.tail_local), turn)
        start = torso(rig.data.bones['upper_arm_' + suffix].head_local)
        wrist = p(.055 if s == 1 else -.038, -.265 if s == 1 else -.155, .64) + hip_offset
        if neutral:
            swing = (0 if gait == 0 else .045 if gait == 1 else .065) * math.cos(phase + (math.pi if s < 0 else 0))
            wrist = p(s * .195, .010 + swing, .456) + hip_offset
        if diagnostic:
            wrist = p(s * .24, -.12, .89) + hip_offset
        a = rig.data.bones['upper_arm_' + suffix].length
        b = rig.data.bones['forearm_' + suffix].length
        elbow = solve_joint(start, wrist, a, b, p(s * .30, .01, .52))
        # Match palm and forearm roll, with twist spread through the whole arm.
        palm = Vector((0, 0, 1)) if s == 1 else Vector((1, 0, 0))
        direction = Vector((-.6, -.8, .08)) if s == 1 else Vector((0, -1, -.10))
        if neutral:
            palm = Vector((-s, 0, 0))
            direction = Vector((s * .08, -.08, -1))
        hand_rot = arm_rotation(s, direction, palm)
        upper_rot = arm_rotation(s, elbow - start, palm)
        fore_rot = arm_rotation(s, wrist - elbow, palm)
        aim_bone(rig, 'upper_arm_' + suffix, start, elbow, upper_rot)
        aim_bone(rig, 'forearm_' + suffix, elbow, wrist, fore_rot)
        hand = rig.data.bones['hand_' + suffix]
        aim_bone(rig, hand.name, wrist, wrist + hand_rot @ (hand.tail_local - hand.head_local), hand_rot)
        leg_phase = phase + (math.pi if s < 0 else 0)
        # A stance half-cycle travels backward at constant speed; swing returns airborne.
        t = (leg_phase / (2 * math.pi)) % 1
        stride = .20 if gait == 1 else .30
        if gait == 0:
            travel, lift = 0, 0
        elif t < .5:
            travel, lift = stride * (2 * t - .5), 0
        else:
            swing = (t - .5) * 2
            travel = stride * (.5 - swing)
            lift = (.045 if gait == 1 else .075) * math.sin(math.pi * swing)
        ankle = p(s * .103, .020 + travel, .062 + lift)
        hip = rig.data.bones['thigh_' + suffix].head_local + hip_offset
        knee = solve_joint(hip, ankle, rig.data.bones['thigh_' + suffix].length,
                           rig.data.bones['shin_' + suffix].length, p(s * .10, -.30, .25))
        aim_bone(rig, 'thigh_' + suffix, hip, knee)
        aim_bone(rig, 'shin_' + suffix, knee, ankle)
        foot = rig.data.bones['foot_' + suffix]
        foot_shift = ankle - foot.head_local
        aim_bone(rig, foot.name, ankle, foot.tail_local + foot_shift, Quaternion())
        toe = rig.data.bones['toe_' + suffix]
        aim_bone(rig, toe.name, toe.head_local + foot_shift, toe.tail_local + foot_shift, Quaternion())


def animate(rig, clips=None):
    rig.animation_data_create()
    scene = bpy.context.scene
    scene.render.fps = 30
    clips = clips or [('idle', 61, 0), ('walk', 17, 1), ('run', 13, 2), ('rig_test', 31, 0),
                      ('neutral_idle', 61, 0), ('neutral_walk', 17, 1), ('neutral_run', 13, 2)]
    for name, end, gait in clips:
        action = bpy.data.actions.new(name)
        action.use_fake_user = True
        rig.animation_data.action = action
        for frame in range(1, end + 1):
            scene.frame_set(frame)
            phase = (frame - 1) / (end - 1) * 2 * math.pi
            pose(rig, phase, gait, diagnostic=name == 'rig_test', neutral=name.startswith('neutral_'))
            for pb in rig.pose.bones:
                pb.keyframe_insert('location', frame=frame, group=pb.name)
                pb.keyframe_insert('rotation_quaternion', frame=frame, group=pb.name)
                pb.keyframe_insert('scale', frame=frame, group=pb.name)
        track = rig.animation_data.nla_tracks.new()
        track.name = name
        strip = track.strips.new(name, 1, action)
        track.mute = True
    rig.animation_data.action = bpy.data.actions['idle']
    scene.frame_start, scene.frame_end = 1, 61
    scene.frame_set(1)


def equipment(rig):
    """Simple visual equipment fixture; a rigid mesh weighted only to hand_r."""
    scene = bpy.context.scene
    scene.frame_set(1)
    hand = rig.pose.bones['hand_r']
    inverse_pose = hand.matrix.inverted()
    bind = rig.data.bones['hand_r'].matrix_local @ inverse_pose
    metal = bpy.data.materials.new('Weapon charcoal')
    metal.diffuse_color = (.065, .075, .085, 1)
    metal.use_nodes = True
    metal.node_tree.nodes.get('Principled BSDF').inputs['Base Color'].default_value = metal.diffuse_color
    metal.node_tree.nodes.get('Principled BSDF').inputs['Metallic'].default_value = .45
    wood = bpy.data.materials.new('Weapon walnut')
    wood.diffuse_color = (.16, .075, .028, 1)
    wood.use_nodes = True
    wood.node_tree.nodes.get('Principled BSDF').inputs['Base Color'].default_value = wood.diffuse_color
    parts = []
    for name, center, size, material in [
        ('receiver', (.006, -.245, .688), (.036, .145, .048), metal),
        ('stock', (.006, -.115, .682), (.042, .115, .057), wood),
        ('grip', (.002, -.208, .643), (.024, .026, .071), wood),
        ('foregrip', (.006, -.323, .670), (.037, .088, .026), wood),
        ('barrel', (.006, -.410, .692), (.015, .165, .015), metal),
        ('sight', (.006, -.291, .720), (.012, .026, .016), metal),
    ]:
        bpy.ops.mesh.primitive_cube_add(size=1, location=p(*center))
        obj = bpy.context.object
        obj.name = name
        obj.scale = Vector(size) * SCALE
        bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
        obj.data.materials.append(material)
        bevel = obj.modifiers.new('Soft machined edges', 'BEVEL')
        bevel.width, bevel.segments = .003 * SCALE, 1
        bpy.ops.object.modifier_apply(modifier=bevel.name)
        parts.append(obj)
    bpy.ops.object.select_all(action='DESELECT')
    for obj in parts:
        obj.select_set(True)
    bpy.context.view_layer.objects.active = parts[0]
    bpy.ops.object.join()
    gun = bpy.context.object
    gun.name = 'Equipment_Rifle'
    for v in gun.data.vertices:
        v.co = bind @ (gun.matrix_world @ v.co)
    gun.matrix_world = Matrix.Identity(4)
    gun.parent = rig
    group = gun.vertex_groups.new(name='hand_r')
    group.add(list(range(len(gun.data.vertices))), 1, 'REPLACE')
    mod = gun.modifiers.new('Rigid equipment attachment', 'ARMATURE')
    mod.object = rig
    sockets = []
    for name, parent, location in [('socket_weapon_r', 'hand_r', (.002, -.208, .643)),
                                    ('socket_support_l', 'hand_l', (.006, -.323, .670)),
                                    ('socket_back', 'chest', (0, .09, .65)),
                                    ('socket_head', 'head', (0, .015, .93))]:
        obj = bpy.data.objects.new(name, None)
        scene.collection.objects.link(obj)
        obj.empty_display_type, obj.empty_display_size = 'ARROWS', .06
        obj.parent, obj.parent_type, obj.parent_bone = rig, 'BONE', parent
        bpy.context.view_layer.update()
        obj.matrix_world = Matrix.Translation(p(*location))
        sockets.append(obj)
    return gun, sockets


def clay_material():
    mat = bpy.data.materials.new('Review clay')
    mat.use_nodes = True
    node = mat.node_tree.nodes.get('Principled BSDF')
    node.inputs['Base Color'].default_value = (.18, .21, .24, 1)
    node.inputs['Roughness'].default_value = .8
    return mat


def build(render=True):
    OUT.mkdir(parents=True, exist_ok=True)
    bpy.ops.wm.open_mainfile(filepath=str(SOURCE))
    mesh = next(o for o in bpy.context.scene.objects if o.type == 'MESH')
    mesh.name = 'ProtagonistBody'
    repair_and_grip(mesh)
    rig = make_rig(mesh)
    rig['skeleton_version'] = 'humanoid_fixed_grip_v1'
    rig['walk_reference_speed'] = .4 * SCALE / (16 / 30)
    rig['run_reference_speed'] = .6 * SCALE / (12 / 30)
    rig['grip_policy'] = 'Fixed mesh grip; hand bones only, no finger bones'
    animate(rig)
    gun, sockets = equipment(rig)
    setup_render()
    if not render:
        save_and_export(mesh, rig, gun, sockets)
        return
    render_views('idle', (0, -.10, 1.1), 1.25 * SCALE)
    rig.animation_data.action = bpy.data.actions['walk']
    bpy.context.scene.frame_set(5)
    render_views('walk', (0, -.10, 1.1), 1.25 * SCALE, ['front', 'side', 'iso'])
    for frame in [1, 9, 13]:
        bpy.context.scene.frame_set(frame)
        render_views('walk_%02d' % frame, (0, -.10, 1.1), 1.25 * SCALE, ['side', 'iso'])
    rig.animation_data.action = bpy.data.actions['rig_test']
    bpy.context.scene.frame_set(16)
    render_views('bend', (0, -.10, 1.1), 1.25 * SCALE, ['front', 'back', 'side', 'iso'])
    bpy.context.scene.frame_set(8)
    render_views('turn', (0, -.10, 1.1), 1.25 * SCALE, ['front', 'iso'])
    bpy.context.view_layer.material_override = clay_material()
    bpy.context.scene.frame_set(16)
    render_views('clay_bend', (0, -.10, 1.1), 1.25 * SCALE, ['front', 'side', 'iso'])
    bpy.context.view_layer.material_override = None
    rig.animation_data.action = bpy.data.actions['idle']
    bpy.context.scene.frame_set(1)
    render_views('grip', (0, -.52, 1.50), .75, ['front', 'top', 'side', 'underside', 'iso'])
    gun.hide_render = True
    bpy.context.view_layer.material_override = clay_material()
    render_views('clay_grip', (0, -.52, 1.50), .75, ['front', 'top', 'side', 'underside', 'iso'])
    bpy.context.view_layer.material_override = None
    gun.hide_render = False
    save_and_export(mesh, rig, gun, sockets)


def save_and_export(mesh, rig, gun, sockets):
    bpy.context.scene.render.resolution_x = 640
    bpy.context.scene.render.resolution_y = 640
    bpy.context.preferences.filepaths.save_version = 0
    camera = bpy.context.scene.camera
    camera.location = (3, -4, 2.8)
    look_at(camera, (0, -.1, 1.1))
    camera.data.ortho_scale = 1.25 * SCALE
    for screen in bpy.data.screens:
        for area in screen.areas:
            if area.type == 'VIEW_3D':
                area.spaces.active.region_3d.view_location = (0, -.1, 1.1)
                area.spaces.active.region_3d.view_distance = 4
                area.spaces.active.region_3d.view_rotation = camera.rotation_euler.to_quaternion()
    bpy.ops.object.select_all(action='DESELECT')
    mesh.select_set(True)
    rig.select_set(True)
    gun.select_set(True)
    for obj in sockets:
        obj.select_set(True)
    bpy.context.view_layer.objects.active = rig
    bpy.ops.wm.save_as_mainfile(filepath=str(MASTER), compress=True)
    ASSET.parent.mkdir(parents=True, exist_ok=True)
    bpy.ops.export_scene.gltf(filepath=str(ASSET), export_format='GLB', use_selection=True,
        export_yup=True, export_animations=True, export_animation_mode='ACTIONS',
        export_anim_slide_to_zero=True,
        export_force_sampling=True, export_def_bones=True, export_skins=True,
        export_all_influences=False, export_morph=False, export_extras=True)
    print('PROTAGONIST_EXPORT', ASSET)


def preview_clips():
    """Extend the existing editable rig without rebuilding its mesh, weights or old clips."""
    bpy.ops.wm.open_mainfile(filepath=str(MASTER))
    rig = bpy.data.objects['ProtagonistRig']
    names = ['neutral_idle', 'neutral_walk', 'neutral_run']
    for track in list(rig.animation_data.nla_tracks):
        if track.name in names:
            rig.animation_data.nla_tracks.remove(track)
    for name in names:
        if name in bpy.data.actions:
            bpy.data.actions.remove(bpy.data.actions[name])
    animate(rig, [('neutral_idle', 61, 0), ('neutral_walk', 17, 1), ('neutral_run', 13, 2)])
    gun = bpy.data.objects['Equipment_Rifle']
    gun.hide_render = True
    setup_render()
    rig.animation_data.action = bpy.data.actions['neutral_idle']
    bpy.context.scene.frame_set(1)
    render_views('neutral', (0, -.10, 1.1), 1.25 * SCALE)
    bpy.context.view_layer.material_override = clay_material()
    render_views('neutral_clay', (0, -.10, 1.1), 1.25 * SCALE, ['front', 'side', 'iso'])
    bpy.context.view_layer.material_override = None
    rig.animation_data.action = bpy.data.actions['neutral_walk']
    bpy.context.scene.frame_set(5)
    render_views('neutral_walk', (0, -.10, 1.1), 1.25 * SCALE, ['front', 'side', 'iso'])
    rig.animation_data.action = bpy.data.actions['idle']
    bpy.context.scene.frame_set(1)
    gun.hide_render = False
    save_and_export(bpy.data.objects['ProtagonistBody'], rig, gun,
                    [bpy.data.objects[n] for n in ['socket_weapon_r', 'socket_support_l', 'socket_back', 'socket_head']])


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('stage', choices=['baseline', 'build', 'preview-clips'])
    parser.add_argument('--skip-renders', action='store_true')
    parser.add_argument('--replace-generated', action='store_true', help='Explicitly replace rig master/export; preserve manual edits separately first')
    args = parser.parse_args(sys.argv[sys.argv.index('--') + 1:])
    if args.stage != 'baseline' and (MASTER.exists() or ASSET.exists()) and not args.replace_generated:
        parser.error('Generated outputs exist. Preserve any manual edits, then pass --replace-generated.')
    if args.stage == 'baseline':
        baseline()
    elif args.stage == 'preview-clips':
        preview_clips()
    else:
        build(not args.skip_renders)
