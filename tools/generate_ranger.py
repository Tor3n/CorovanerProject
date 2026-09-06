#!/usr/bin/env python3
"""Author the original wasteland ranger OBJ/MTL with Python's standard library.

Y-up, +Z forward, meters, feet at zero. Separate closed clothing/equipment
shells, smooth vertex normals, local UVs (overlapping; no texture atlas).
Run from any directory. No Blender, downloads, or proprietary assets required.
"""
from collections import defaultdict
from math import cos, sin, pi, sqrt, copysign
from pathlib import Path
import random

OUT = Path(__file__).resolve().parents[1] / 'assets/models/ranger'
MATERIALS = {
    'canvas': ('706650', .04, 8),
    'canvas_light': ('8b7d61', .03, 8),
    'canvas_dark': ('514b3c', .03, 8),
    'leather': ('49372c', .10, 18),
    'leather_edge': ('806348', .08, 14),
    'rubber': ('242725', .04, 8),
    'steel': ('626c69', .32, 45),
    'steel_edge': ('9a9d8b', .4, 55),
    'rust': ('77503a', .06, 12),
    'brass': ('aa8b4f', .3, 48),
    'scarf': ('663c32', .03, 8),
    'glass': ('867346', .6, 90),
    'glass_glint': ('e7d59a', .6, 90),
    'screen': ('658950', .15, 30),
    'screen_glow': ('b7cc82', .15, 30),
    'wood': ('634732', .12, 18),
}
vertices, normals, uvs = [], [], []
faces = defaultdict(list)
parts = defaultdict(int)


def add(a, b): return tuple(x + y for x, y in zip(a, b))
def sub(a, b): return tuple(x - y for x, y in zip(a, b))
def mul(a, s): return tuple(x * s for x in a)
def dot(a, b): return sum(x * y for x, y in zip(a, b))
def cross(a, b): return (a[1]*b[2]-a[2]*b[1], a[2]*b[0]-a[0]*b[2], a[0]*b[1]-a[1]*b[0])
def unit(a): return mul(a, 1 / max(1e-12, sqrt(dot(a, a))))


def mesh(name, mat, vs, fs, tex=None):
    """Area-weighted normals; reject degenerate triangles before export."""
    ns = [(0, 0, 0) for _ in vs]
    triangles = []
    for face in fs:
        for i in range(1, len(face)-1):
            tri = (face[0], face[i], face[i+1])
            a, b, c = (vs[j] for j in tri)
            n = cross(sub(b, a), sub(c, a))
            if dot(n, n) < 1e-18:
                continue
            triangles.append(tri)
            for j in tri:
                ns[j] = add(ns[j], n)
    offset = len(vertices) + 1
    vertices.extend(vs)
    normals.extend(unit(n) for n in ns)
    uvs.extend(tex or [(v[0], v[1]) for v in vs])
    faces[mat].extend(tuple(offset + j for j in f) for f in triangles)
    parts[name] += 1


def loft(name, mat, rings, n=16, power=2, ripple=0):
    """Elliptical/superelliptical garment cross sections: (cx,y,cz,rx,rz)."""
    vs, fs, tex = [], [], []
    for j, (x, y, z, rx, rz) in enumerate(rings):
        for i in range(n):
            a = 2*pi*i/n
            r = 1 + ripple * sin(a*5 + j*.8)
            sx = copysign(abs(cos(a)) ** (2/power), cos(a))
            sz = copysign(abs(sin(a)) ** (2/power), sin(a))
            vs.append((x+rx*sx*r, y, z+rz*sz*r))
            tex.append((i/n, j/(len(rings)-1)))
    for j in range(len(rings)-1):
        for i in range(n):
            a, b = j*n+i, j*n+(i+1)%n
            fs.append((a, a+n, b+n, b))
    # Caps use separate vertices so the rim remains crisp.
    for j, reverse in [(0, False), (len(rings)-1, True)]:
        start = len(vs)
        vs.extend(vs[j*n:(j+1)*n])
        tex.extend(tex[j*n:(j+1)*n])
        cap = tuple(range(start, start+n))
        fs.append(tuple(reversed(cap)) if reverse else cap)
    mesh(name, mat, vs, fs, tex)


def ellipsoid(name, mat, c, size, n=16, rows=10):
    rings = []
    for j in range(rows+1):
        a = -.5*pi + pi*j/rows
        rings.append((c[0], c[1]+sin(a)*size[1]/2, c[2],
                      max(.00001, cos(a))*size[0]/2, max(.00001, cos(a))*size[2]/2))
    loft(name, mat, rings, n)


def tube(name, mat, path, radius, n=10):
    vs, fs, tex = [], [], []
    for j, p in enumerate(path):
        direction = unit(sub(path[min(j+1, len(path)-1)], path[max(0, j-1)]))
        ref = (0, 0, 1) if abs(direction[2]) < .9 else (0, 1, 0)
        u = unit(cross(direction, ref))
        v = cross(direction, u)
        r = radius[j] if isinstance(radius, list) else radius
        for i in range(n):
            a = i*2*pi/n
            vs.append(add(p, add(mul(u, r*cos(a)), mul(v, r*sin(a)))))
            tex.append((i/n, j/(len(path)-1)))
    for j in range(len(path)-1):
        for i in range(n):
            a, b = j*n+i, j*n+(i+1)%n
            fs.append((a, b, b+n, a+n))
    fs.append(tuple(reversed(range(n))))
    fs.append(tuple(range((len(path)-1)*n, len(path)*n)))
    mesh(name, mat, vs, fs, tex)


def box(name, mat, c, size, angle=0):
    # Beveled rectangular solid using clipped-corner cross sections.
    w, h, d = size
    bevel = min(w, h, d)*.18
    outline = [(-w/2+bevel, -d/2), (w/2-bevel, -d/2), (w/2, -d/2+bevel),
               (w/2, d/2-bevel), (w/2-bevel, d/2), (-w/2+bevel, d/2),
               (-w/2, d/2-bevel), (-w/2, -d/2+bevel)]
    vs, fs = [], []
    for y, s in [(-h/2, .88), (-h/2+bevel, 1), (h/2-bevel, 1), (h/2, .88)]:
        for x, z in outline:
            x, z = x*s, z*s
            a = angle*pi/180
            vs.append((c[0]+x*cos(a)-y*sin(a), c[1]+x*sin(a)+y*cos(a), c[2]+z))
    for j in range(3):
        for i in range(8):
            a, b = j*8+i, j*8+(i+1)%8
            fs.append((a, a+8, b+8, b))
    fs.extend([tuple(range(8)), tuple(reversed(range(24, 32)))])
    mesh(name, mat, vs, fs)


def seam(name, path, mat='leather_edge', radius=.003):
    tube(name, mat, path, radius, 6)


def rivet(p, radius=.009, mat='brass'):
    ellipsoid('rivet', mat, p, (radius*2, radius*2, radius), 8, 4)


def buckle(c, w=.07, h=.065, zsign=1):
    x, y, z = c
    seam('buckle', [(x-w/2,y-h/2,z), (x+w/2,y-h/2,z), (x+w/2,y+h/2,z),
                    (x-w/2,y+h/2,z), (x-w/2,y-h/2,z)], 'brass', .007)
    seam('buckle pin', [(x,y-h/2,z+.003*zsign),(x,y+h/2,z+.003*zsign)], 'steel', .003)


def pouch(x, y, z, w=.15, h=.18, facing=1):
    box('pouch', 'leather', (x,y,z), (w,h,.10))
    box('pouch flap', 'leather_edge', (x,y+h*.29,z+facing*.052), (w*.94,h*.36,.017))
    rivet((x,y+h*.22,z+facing*.064), .009)
    for side in [-1,1]:
        for i in range(7):
            yy = y-h*.4+i*h*.12
            seam('pouch stitches', [(x+side*w*.41,yy,z+facing*.054),
                                    (x+side*w*.41,yy+.009,z+facing*.055)], radius=.0018)


def character():
    # Feet and anatomically shaped cloth legs. Folds are actual geometry.
    for side in [-1, 1]:
        x = side*.155
        loft('lug sole', 'rubber', [(x,.012,.065,.125,.225),(x,.05,.065,.127,.226),
                                  (x,.07,.06,.12,.218)], 16, 4)
        loft('boot upper', 'leather', [(x,.065,.065,.12,.211),(x,.13,.06,.117,.207),
             (x,.19,.035,.108,.176),(x,.25,-.005,.085,.098),
             (x,.36,-.013,.09,.10),(x,.39,-.013,.097,.105)], 16, 3)
        box('toe cap', 'rubber', (x,.125,.225), (.204,.085,.087))
        for i in range(5):
            box('sole tread', 'canvas_dark', (x,.026,-.10+i*.079), (.255,.021,.024))
        for i in range(5):
            yy, zz = .20+i*.034, .13-i*.009
            seam('boot lace', [(x-.06,yy,zz),(x+.06,yy+.025,zz-.006)], 'canvas_light', .003)
            seam('boot lace', [(x+.06,yy,zz),(x-.06,yy+.025,zz-.006)], 'canvas_light', .003)
            rivet((x-.065,yy,zz), .006, 'steel')
            rivet((x+.065,yy,zz), .006, 'steel')
        loft('trousers', 'canvas_dark', [(x,.35,-.015,.081,.085),(x,.43,-.015,.093,.093),
             (x,.50,-.006,.087,.091),(x,.56,.005,.102,.107),(x,.64,.005,.108,.119),
             (x,.70,0,.10,.115),(x,.79,-.016,.12,.128),(x,.96,-.015,.132,.15),
             (x,1.12,0,.125,.14)], 20, ripple=.035)
        for i in range(3):
            seam('trouser fold', [(x-.075,.43+i*.045,.066),(x,.415+i*.045,.089),
                 (x+.075,.43+i*.045,.065)], 'canvas', .006)
        ellipsoid('knee guard', 'steel' if side < 0 else 'leather',
                  (x,.626,.116), (.191,.24,.087), 16, 8)
        for yy in [.55,.70]:
            box('knee strap', 'leather', (x,yy,.05), (.224,.033,.17))
            rivet((x-side*.077,yy,.14), .008, 'steel_edge')
        seam('trouser seam', [(x+side*.109,.75,.053),(x+side*.126,.93,.057),
                              (x+side*.12,1.10,.055)], radius=.003)

    loft('pelvis', 'leather', [(0,1.02,0,.265,.15),(0,1.15,0,.27,.166),(0,1.23,0,.247,.156)], 24)
    loft('duster torso', 'canvas', [(0,1.16,0,.255,.155),(0,1.29,0,.27,.168),
         (0,1.43,-.014,.295,.18),(0,1.61,-.022,.318,.18),
         (0,1.73,-.019,.32,.151),(0,1.79,-.015,.245,.126)], 24, ripple=.017)

    # Open-front, flared coat with an irregular hem, lining, and raised seams.
    n = 36
    vs, fs = [], []
    for j, (y, rx, rz) in enumerate([(1.23,.264,.164),(1.06,.29,.19),(.85,.335,.215),(.61,.375,.245)]):
        for i in range(n+1):
            a = .34+(2*pi-.68)*i/n
            fold = .009*sin(a*12+j*.6)
            yy = y + (.014*sin(i*.9)+.006*sin(i*2.4) if j==3 else 0)
            vs.append(((rx+fold)*sin(a), yy, (rz+fold)*cos(a)))
    for j in range(3):
        for i in range(n):
            a=j*(n+1)+i
            fs.append((a,a+1,a+n+2,a+n+1))
    # Reverse outer winding for this sin/cos parameterization and duplicate inner shell.
    mesh('duster skirt', 'canvas', vs, [tuple(reversed(f)) for f in fs])
    inner = [(x*.974,y,z*.974) for x,y,z in vs]
    mesh('duster lining', 'canvas_dark', inner, fs)
    for i in [0,7,14,22,29,36]:
        seam('coat panel seam', [vs[j*(n+1)+i] for j in range(4)], radius=.004)
    seam('worn hem', vs[-(n+1):], 'canvas_light', .004)
    for i in [5,16,27,33]:
        x,y,z=vs[3*(n+1)+i]
        seam('loose hem thread', [(x,y,z),(x+.005,y-.024,z+.005)], 'canvas_light', .0016)

    # Layered cuirass and split lapels.
    loft('leather cuirass', 'leather', [(0,1.27,.007,.277,.177),(0,1.40,-.005,.3,.192),
         (0,1.62,-.01,.32,.191),(0,1.70,-.01,.28,.164)], 24, power=2.4)
    for side in [-1,1]:
        box('lapel', 'leather_edge', (side*.146,1.675,.164), (.11,.26,.037), side*-23)
        seam('cuirass seam', [(side*.25,1.29,.117),(side*.28,1.43,.14),
                              (side*.27,1.62,.149)], radius=.004)
        for i in range(6):
            rivet((side*(.226+i*.005),1.32+i*.055,.166), .007)
    loft('belt', 'leather', [(0,1.14,.004,.282,.183),(0,1.218,.004,.282,.183)], 28, 2.6)
    buckle((0,1.18,.195), .094,.065)
    for x in [-.20,-.13,.13,.20]:
        box('belt loop', 'leather_edge', (x,1.18,.16), (.027,.098,.041))
    pouch(-.22,1.11,.206)
    pouch(.21,1.105,.21,.145,.21)
    pouch(-.285,1.22,-.19,.14,.19,-1)

    for side in [-1,1]:
        loft('sleeve', 'canvas', [(side*.39,1.74,-.012,.125,.133),
             (side*.44,1.62,-.009,.125,.137),(side*.465,1.49,.008,.105,.116),
             (side*.473,1.39,.027,.102,.109),(side*.485,1.29,.04,.094,.098),
             (side*.49,1.19,.061,.076,.08)][::-1], 20, ripple=.04)
        for i in range(3):
            seam('elbow fold', [(side*.49-.065,1.34+i*.043,.09),
                  (side*.49,1.32+i*.043,.14),(side*.49+.065,1.35+i*.043,.10)], 'canvas_dark', .006)
        loft('gauntlet', 'leather', [(side*.493,1.04,.07,.071,.073),
              (side*.492,1.12,.065,.085,.081),(side*.49,1.27,.05,.096,.098)], 16)
        ellipsoid('glove palm', 'rubber', (side*.495,1.012,.08), (.142,.169,.105), 12, 8)
        for i in range(4):
            x=side*.495+(i-1.5)*.032
            tube('glove finger', 'leather', [(x,.999,.115),(x,.947,.13),(x,.923,.10)], [.018,.018,.014], 8)
            box('knuckle plate', 'steel', (x,1.025,.131), (.027,.026,.015))
        tube('thumb', 'leather', [(side*.445,1.043,.11),(side*.411,1.0,.132),
                                (side*.427,.964,.129)], [.025,.025,.016], 10)

    ellipsoid('scrap pauldron', 'steel', (-.374,1.737,-.004), (.36,.19,.38), 24, 10)
    for j in range(2):
        box('pauldron lames', 'steel', (-.447-j*.019,1.674-j*.045,.018), (.17,.07,.32), -16)
    for i in range(7):
        a=pi*.12+i*pi*.125
        rivet((-.374+.158*cos(a),1.767,.169*sin(a)), .010, 'steel_edge')
    for i in range(3):
        tube('armor spike', 'steel_edge', [(-.47+i*.077,1.79,-.015),
              (-.49+i*.08,1.89+(i%2)*.025,-.025)], [.024,.001], 8)
    ellipsoid('leather shoulder', 'leather', (.373,1.737,-.005), (.32,.155,.34), 20, 8)
    for i in range(5):
        rivet((.285+i*.035,1.78,.112), .008)

    # Retrofuturist wrist computer: inset display, bezel, buttons and vent slats.
    box('wrist terminal housing', 'steel', (.497,1.18,.135), (.19,.19,.09), -3)
    box('screen bezel', 'rubber', (.478,1.195,.184), (.12,.12,.021))
    box('phosphor screen', 'screen', (.478,1.195,.197), (.096,.088,.008))
    for i in range(4):
        box('terminal text', 'screen_glow', (.469,1.221-i*.017,.202), (.061-i*.009,.003,.002))
    for i in range(3):
        rivet((.552,1.231-i*.036,.19), .008, 'brass')
        box('terminal vent', 'rubber', (.463+i*.026,1.117,.184), (.015,.025,.005))

    # Scarf and fitted leather hood: face opening is recessed behind goggles/mask.
    loft('scarf collar', 'scarf', [(0,1.745,.015,.16,.124),(0,1.80,.012,.178,.135),
         (0,1.855,0,.142,.113)], 24, ripple=.035)
    for i in range(3):
        seam('scarf folds', [(-.12,1.77+i*.024,.083),(0,1.761+i*.024,.148),
                             (.12,1.79+i*.024,.084)], 'leather_edge', .004)
    box('scarf tail', 'scarf', (.145,1.60,.205), (.10,.31,.025), 10)
    for i in range(6):
        seam('scarf fringe', [(.09+i*.015,1.455,.22),(.087+i*.015,1.427,.225)], 'scarf', .002)
    ellipsoid('hood', 'leather', (0,2.004,-.025), (.355,.392,.335), 28, 18)
    ellipsoid('recessed face', 'rubber', (0,1.997,.10), (.269,.271,.137), 24, 14)
    seam('hood brow welt', [(-.141,2.026,.13),(-.132,2.11,.128),(-.078,2.166,.094),
         (0,2.188,.055),(.078,2.166,.094),(.132,2.11,.128),(.141,2.026,.13)], 'leather_edge', .008)
    seam('hood center seam', [(0,2.185,.06),(0,2.198,-.025),(0,2.16,-.125),
         (0,2.07,-.19),(0,1.96,-.188)], 'leather_edge', .004)
    box('goggle bridge', 'steel', (0,2.055,.197), (.075,.025,.027))
    for side in [-1,1]:
        ellipsoid('goggle rim', 'brass', (side*.076,2.06,.176), (.137,.092,.060), 20, 10)
        ellipsoid('goggle glass', 'glass', (side*.076,2.06,.204), (.108,.065,.027), 20, 10)
        seam('glass reflection', [(side*.076-.026,2.073,.217),(side*.076+.012,2.079,.217)], 'glass_glint', .003)
        box('temple strap', 'rubber', (side*.157,2.056,.018), (.027,.046,.20))
        rivet((side*.152,2.055,.136), .009, 'steel')
    ellipsoid('mask facepiece', 'rubber', (0,1.945,.161), (.192,.155,.123), 20, 12)
    tube('central respirator', 'steel', [(0,1.94,.21),(0,1.94,.252)], .041, 20)
    for i in range(5):
        box('mask intake slot', 'rubber', (-.025+i*.012,1.94,.255), (.005,.049,.005))
    for side in [-1,1]:
        tube('filter canister', 'steel', [(side*.105,1.928,.166),(side*.135,1.912,.238)], .047, 20)
        for i in range(4):
            tube('filter rib', 'rubber', [(side*(.111+i*.006),1.925-i*.003,.181+i*.013),
                 (side*(.113+i*.006),1.924-i*.003,.187+i*.013)], .049, 16)
        tube('filter end', 'rubber', [(side*.136,1.912,.237),(side*.137,1.912,.244)], .033, 16)
    tube('breathing hose', 'rubber', [(-.11,1.918,.193),(-.19,1.85,.19),(-.22,1.74,.21),
         (-.20,1.66,.229),(-.17,1.64,.23)], .019, 12)
    for i in range(9):
        y=1.84-i*.018
        tube('hose corrugation', 'steel', [(-.207,y,.205),(-.207,y-.007,.205)], .022, 10)

    # Diagonal ammunition sling follows the chest instead of floating above it.
    strap=[(-.205,1.727,.166),(-.14,1.60,.207),(-.025,1.44,.218),(.105,1.27,.195),(.18,1.19,.171)]
    for a,b in zip(strap,strap[1:]):
        mid=mul(add(a,b),.5)
        length=sqrt(dot(sub(b,a),sub(b,a)))
        box('bandolier', 'leather_edge', mid, (.063,length+.017,.028), 34)
    for i in range(7):
        x,y,z=-.13+i*.038,1.588-i*.054,.244
        tube('cartridge case', 'brass', [(x-.025,y-.016,z),(x+.025,y+.016,z)], .011, 10)
        tube('bullet tip', 'steel_edge', [(x+.025,y+.016,z),(x+.039,y+.025,z)], [.011,.001], 10)
        box('ammo loop', 'leather', (x,y,z+.005), (.016,.035,.024), 34)

    # Pack, bedroll, canteen and fully modeled rifle on the back.
    box('rucksack', 'canvas_dark', (0,1.465,-.247), (.39,.45,.24))
    box('pack flap', 'canvas', (0,1.643,-.376), (.40,.13,.036))
    for side in [-1,1]:
        box('pack strap', 'leather', (side*.114,1.455,-.384), (.044,.40,.018))
        buckle((side*.114,1.455,-.399), .057,.067,-1)
        pouch(side*.13,1.315,-.396,.15,.17,-1)
        seam('shoulder harness', [(side*.145,1.73,-.31),(side*.18,1.81,-.10),
             (side*.21,1.735,.10)], 'leather', .023)
    tube('rolled blanket', 'canvas_light', [(-.259,1.745,-.269),(.259,1.745,-.269)], .09, 24)
    for x in [-.17,.17]:
        tube('bedroll tie', 'leather', [(x-.015,1.745,-.269),(x+.015,1.745,-.269)], .094, 24)
    for side in [-1,1]:
        path=[]
        for i in range(80):
            a=i*.21
            r=.008+.0045*a
            path.append((side*.261,1.745+r*cos(a),-.269+r*sin(a)))
        seam('blanket spiral', path, 'canvas_dark', .0025)
    ellipsoid('canteen', 'steel', (-.309,1.083,-.075), (.154,.227,.124), 16, 12)
    tube('canteen cap', 'rubber', [(-.309,1.189,-.075),(-.309,1.227,-.075)], .027, 12)
    box('canteen strap', 'leather', (-.309,1.08,-.003), (.045,.20,.017))
    # Long axis is slightly canted outward, safely below overall head height.
    box('rifle butt', 'wood', (.266,.91,-.385), (.115,.31,.084), -12)
    box('butt plate', 'steel', (.234,.763,-.385), (.127,.028,.092), -12)
    box('rifle action', 'steel', (.322,1.205,-.389), (.091,.30,.078), -12)
    box('rifle fore stock', 'wood', (.369,1.427,-.389), (.085,.21,.09), -12)
    tube('rifle barrel', 'steel', [(.387,1.52,-.389),(.484,1.973,-.389)], .018, 16)
    tube('rifle muzzle', 'rubber', [(.484,1.973,-.389),(.486,1.983,-.389)], .012, 16)
    box('front sight', 'steel', (.475,1.944,-.361), (.044,.035,.022), -12)
    box('magazine', 'steel', (.351,1.175,-.33), (.075,.116,.065), -12)
    seam('trigger guard', [(.296,1.123,-.335),(.29,1.062,-.315),(.337,1.06,-.315),
         (.347,1.118,-.335)], 'steel', .007)
    tube('bolt handle', 'steel_edge', [(.337,1.29,-.382),(.401,1.282,-.358)], .008, 10)
    ellipsoid('bolt knob', 'rubber', (.405,1.282,-.358), (.028,.028,.028), 10, 6)
    seam('rifle sling', [(.48,1.90,-.42),(.40,1.55,-.47),(.245,.89,-.44)], 'leather', .013)

    # Deterministic chips and scuffs, deliberately restrained at isometric scale.
    rng=random.Random(12)
    for i in range(30):
        x=rng.uniform(-.20,.20)
        y=rng.uniform(1.32,1.64)
        z=.204 - .10*(abs(x)/.3)**2
        seam('cuirass scratch', [(x,y,z),(x+rng.uniform(.009,.027),y+.006,z)],
             'leather_edge', .0015)
    for i in range(12):
        x=-.374+rng.uniform(-.10,.10)
        z=rng.uniform(-.08,.10)
        y=1.737+.095*sqrt(max(.01,1-((x+.374)/.18)**2-(z/.19)**2))
        seam('armor chip', [(x,y+.002,z),(x+.015,y+.002,z+.01)], 'rust', .003)

    # Field repairs on the skirt, with mismatched cloth and individual stitches.
    for x,y,z,angle in [(.262,.89,.159,-8),(-.255,.77,.183,11)]:
        box('sewn repair patch', 'canvas_dark', (x,y,z), (.09,.12,.009), angle)
        for side in [-1,1]:
            for i in range(6):
                yy=y-.046+i*.018
                seam('repair stitch', [(x+side*.036,yy,z+.008),
                     (x+side*.046,yy+.004,z+.008)], 'canvas_light', .0018)
    # Worn boot welts and stitched gauntlet cuffs.
    for side in [-1,1]:
        x=side*.155
        seam('boot welt', [(x+.119*cos(i*2*pi/32),.072,.065+.217*sin(i*2*pi/32))
                           for i in range(33)], 'leather_edge', .003)
        for i in range(9):
            xx=side*.49-.061+i*.015
            seam('cuff stitching', [(xx,1.255,.118),(xx+.005,1.246,.12)], 'leather_edge', .002)


def export():
    OUT.mkdir(parents=True, exist_ok=True)
    with (OUT/'wasteland_ranger.mtl').open('w', newline='\n') as f:
        f.write('# Original wasteland ranger materials; keep beside the OBJ.\n')
        for name, (color,spec,shine) in MATERIALS.items():
            rgb=[int(color[i:i+2],16)/255 for i in (0,2,4)]
            f.write(f'newmtl {name}\nKa 0.12 0.12 0.12\n')
            f.write('Kd '+' '.join(f'{c:.5f}' for c in rgb)+'\n')
            f.write(f'Ks {spec} {spec} {spec}\nNs {shine}\nd 1\nillum 2\n\n')
    with (OUT/'wasteland_ranger.obj').open('w', newline='\n') as f:
        f.write('# Original Fallout 1-2 inspired wasteland ranger, authored for this project.\n')
        f.write('# Y up; +Z forward; feet Y=0; approximately 2.2 units tall.\n')
        f.write('# Local UVs overlap; solid MTL colors; static mesh, no skeleton.\n')
        f.write('mtllib wasteland_ranger.mtl\n')
        floor = min(v[1] for v in vertices)
        for x,y,z in vertices: f.write(f'v {x:.6f} {y-floor:.6f} {z:.6f}\n')
        for uv in uvs: f.write('vt '+' '.join(f'{x:.6f}' for x in uv)+'\n')
        for n in normals: f.write('vn '+' '.join(f'{x:.6f}' for x in n)+'\n')
        # libGDX's basic loader expects one material per group. Batch small
        # detail pieces by material and keep each group below 32767 indices.
        for mat, tris in faces.items():
            for chunk in range(0,len(tris),6000):
                f.write(f'g ranger_{mat}_{chunk//6000}\nusemtl {mat}\ns 1\n')
                for tri in tris[chunk:chunk+6000]:
                    f.write('f '+' '.join(f'{i}/{i}/{i}' for i in tri)+'\n')
    count=sum(map(len,faces.values()))
    print(f'Exported {len(vertices):,} vertices, {count:,} triangles, {sum(parts.values())} modeled pieces.')
    print(f'Bounds: '+str([(round(min(v[i] for v in vertices),4),round(max(v[i] for v in vertices),4)) for i in range(3)]))
    print(OUT/'wasteland_ranger.obj')


if __name__ == '__main__':
    character()
    export()
