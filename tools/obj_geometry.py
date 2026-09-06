"""Small deterministic OBJ authoring utility. Standard library only; Y-up.

Each connected shell supplies its own vertices and smooth normals. Export groups
are batched by material for libGDX's single-material-per-group OBJ loader.
"""
from collections import defaultdict
from math import cos, sin, pi, sqrt, copysign, isfinite


def add(a, b): return tuple(x+y for x,y in zip(a,b))
def sub(a, b): return tuple(x-y for x,y in zip(a,b))
def mul(a, s): return tuple(x*s for x in a)
def dot(a, b): return sum(x*y for x,y in zip(a,b))
def cross(a,b): return (a[1]*b[2]-a[2]*b[1], a[2]*b[0]-a[0]*b[2], a[0]*b[1]-a[1]*b[0])
def unit(a): return mul(a, 1/max(1e-12,sqrt(dot(a,a))))


class ObjMesh:
    def __init__(self):
        self.vertices, self.normals, self.uvs = [], [], []
        self.faces = defaultdict(list)
        self.pieces = 0

    def mesh(self, material, vertices, faces, uvs=None):
        normals = [(0,0,0) for _ in vertices]
        triangles = []
        for face in faces:
            for i in range(1,len(face)-1):
                tri = (face[0],face[i],face[i+1])
                a,b,c = (vertices[j] for j in tri)
                normal = cross(sub(b,a),sub(c,a))
                if dot(normal,normal)<1e-16:
                    continue
                triangles.append(tri)
                for j in tri:
                    normals[j] = add(normals[j],normal)
        offset=len(self.vertices)+1
        self.vertices.extend(vertices)
        self.normals.extend(unit(n) for n in normals)
        self.uvs.extend(uvs or [(v[0],v[1]) for v in vertices])
        self.faces[material].extend(tuple(offset+j for j in t) for t in triangles)
        self.pieces += 1

    def loft(self, material, rings, segments=24, power=2, ripple=0):
        """Rings ascend in Y: (centerX, Y, centerZ, radiusX, radiusZ)."""
        vs, fs, uv = [], [], []
        for j,(x,y,z,rx,rz) in enumerate(rings):
            for i in range(segments):
                a=2*pi*i/segments
                r=1+ripple*sin(5*a+j*.7)
                cx=copysign(abs(cos(a))**(2/power),cos(a))
                cz=copysign(abs(sin(a))**(2/power),sin(a))
                vs.append((x+rx*cx*r,y,z+rz*cz*r))
                uv.append((i/segments,j/(len(rings)-1)))
        for j in range(len(rings)-1):
            for i in range(segments):
                a,b=j*segments+i,j*segments+(i+1)%segments
                fs.append((a,a+segments,b+segments,b))
        for j,reverse in [(0,False),(len(rings)-1,True)]:
            start=len(vs)
            vs.extend(vs[j*segments:(j+1)*segments])
            uv.extend(uv[j*segments:(j+1)*segments])
            cap=tuple(range(start,start+segments))
            fs.append(tuple(reversed(cap)) if reverse else cap)
        self.mesh(material,vs,fs,uv)

    def ellipsoid(self, material, center, size, segments=16, rows=10):
        rings=[]
        for j in range(rows+1):
            a=-pi/2+pi*j/rows
            rings.append((center[0],center[1]+sin(a)*size[1]/2,center[2],
                          max(.00001,cos(a))*size[0]/2,max(.00001,cos(a))*size[2]/2))
        self.loft(material,rings,segments)

    def tube(self, material, path, radius, segments=10):
        vs,fs,uv=[],[],[]
        for j,p in enumerate(path):
            direction=unit(sub(path[min(j+1,len(path)-1)],path[max(0,j-1)]))
            ref=(0,0,1) if abs(direction[2])<.9 else (0,1,0)
            u=unit(cross(direction,ref)); v=cross(direction,u)
            r=radius[j] if isinstance(radius,list) else radius
            for i in range(segments):
                a=i*2*pi/segments
                vs.append(add(p,add(mul(u,r*cos(a)),mul(v,r*sin(a)))))
                uv.append((i/segments,j/(len(path)-1)))
        for j in range(len(path)-1):
            for i in range(segments):
                a,b=j*segments+i,j*segments+(i+1)%segments
                fs.append((a,b,b+segments,a+segments))
        fs.extend([tuple(reversed(range(segments))),
                   tuple(range((len(path)-1)*segments,len(path)*segments))])
        self.mesh(material,vs,fs,uv)

    def box(self, material, center, size, angle=0):
        w,h,d=size
        bevel=min(size)*.18
        outline=[(-w/2+bevel,-d/2),(w/2-bevel,-d/2),(w/2,-d/2+bevel),
                 (w/2,d/2-bevel),(w/2-bevel,d/2),(-w/2+bevel,d/2),
                 (-w/2,d/2-bevel),(-w/2,-d/2+bevel)]
        vs,fs=[],[]
        for y,s in [(-h/2,.91),(-h/2+bevel,1),(h/2-bevel,1),(h/2,.91)]:
            for x,z in outline:
                x,z=x*s,z*s
                a=angle*pi/180
                vs.append((center[0]+x*cos(a)-y*sin(a),center[1]+x*sin(a)+y*cos(a),center[2]+z))
        for j in range(3):
            for i in range(8):
                a,b=j*8+i,j*8+(i+1)%8
                fs.append((a,a+8,b+8,b))
        fs.extend([tuple(range(8)),tuple(reversed(range(24,32)))])
        self.mesh(material,vs,fs)

    def ribbon(self, material, path, width, thickness=.003):
        """A narrow raised strip facing +Z (or -Z when thickness is negative)."""
        vs=[]
        for x,y,z in path:
            vs.extend([(x-width/2,y,z),(x+width/2,y,z)])
        fs=[(i,i+1,i+3,i+2) for i in range(0,len(vs)-2,2)]
        if path[-1][1]<path[0][1]:
            fs=[tuple(reversed(f)) for f in fs]
        if thickness<0:
            fs=[tuple(reversed(f)) for f in fs]
        self.mesh(material,vs,fs)

    def export(self, directory, name, materials):
        directory.mkdir(parents=True,exist_ok=True)
        assert self.vertices and set(self.faces)<=set(materials)
        assert all(isfinite(v) for p in self.vertices for v in p)
        floor=min(v[1] for v in self.vertices)
        with (directory/(name+'.mtl')).open('w',newline='\n') as f:
            f.write('# Original character material palette. Keep beside the OBJ.\n')
            for mat,(color,spec,shine) in materials.items():
                rgb=[int(color[i:i+2],16)/255 for i in (0,2,4)]
                f.write(f'newmtl {mat}\nKa 0.12 0.12 0.12\n')
                f.write('Kd '+' '.join(f'{c:.5f}' for c in rgb)+'\n')
                f.write(f'Ks {spec} {spec} {spec}\nNs {shine}\nd 1\nillum 2\n\n')
        with (directory/(name+'.obj')).open('w',newline='\n') as f:
            f.write('# Original vault suit character inspired by the supplied Fallout 1-2 reference.\n')
            f.write('# Y-up, +Z front, feet at zero. Static shells; local overlapping UVs.\n')
            f.write(f'mtllib {name}.mtl\n')
            for x,y,z in self.vertices: f.write(f'v {x:.6f} {y-floor:.6f} {z:.6f}\n')
            for u,v in self.uvs: f.write(f'vt {u:.6f} {v:.6f}\n')
            for x,y,z in self.normals: f.write(f'vn {x:.6f} {y:.6f} {z:.6f}\n')
            for mat,tris in self.faces.items():
                for chunk in range(0,len(tris),6000):
                    f.write(f'g {name}_{mat}_{chunk//6000}\nusemtl {mat}\ns 1\n')
                    for tri in tris[chunk:chunk+6000]:
                        f.write('f '+' '.join(f'{i}/{i}/{i}' for i in tri)+'\n')
        stats={'vertices':len(self.vertices),'triangles':sum(map(len,self.faces.values())),
               'pieces':self.pieces,'materials':len(self.faces),
               'height':round(max(v[1] for v in self.vertices)-floor,4)}
        return stats
