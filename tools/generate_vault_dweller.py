#!/usr/bin/env python3
"""Build the static blue vault-suit character from the supplied visual reference.

Standard library only. Run `python3 tools/generate_vault_dweller.py`.
This is an editable mesh prototype, not a rigged/animated production character.
"""
from math import sin, cos, pi, sqrt
from pathlib import Path
import json
from obj_geometry import ObjMesh

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'assets/models/vault_dweller'
PALETTE = {
    'suit_blue': ('294a78', .025, 10),
    'suit_light': ('3b608c', .025, 10),
    'suit_shadow': ('233954', .02, 8),
    'gold_trim': ('c3a344', .04, 15),
    'gold_edge': ('8f793c', .03, 10),
    'skin': ('b58a65', .04, 14),
    'skin_shadow': ('946b50', .025, 12),
    'skin_light': ('c29a76', .04, 14),
    'hair': ('392d26', .025, 8),
    'hair_light': ('504033', .025, 8),
    'leather': ('403a30', .06, 18),
    'sole': ('262b2b', .025, 10),
    'steel': ('6b7063', .18, 32),
    'screen': ('405a36', .06, 15),
    'screen_text': ('b4bd79', .05, 15),
    'eye': ('302f2a', .02, 10),
}


def build():
    m = ObjMesh()
    # Feet and legs: slim boots, shaped calves/knees/thighs, subtle cloth folds.
    for side in [-1, 1]:
        x = side * .124
        m.loft('sole', [(x,0,.05,.088,.154),(x,.027,.05,.091,.156),
                       (x,.045,.049,.086,.15)], 20, 3.5)
        m.loft('leather', [(x,.039,.05,.087,.149),(x,.083,.046,.086,.145),
                 (x,.12,.026,.077,.118),(x,.16,.004,.067,.083),
                 (x,.225,-.012,.065,.069),(x,.24,-.011,.068,.071)], 24, 2.8)
        m.tube('gold_edge', [(x+.087*cos(i*pi/16),.045,.05+.151*sin(i*pi/16))
                            for i in range(33)], .0025, 6)
        for i in range(5):
            m.box('sole',(x,.013,-.057+i*.052),(.177,.012,.018))
        for i in range(4):
            y,z=.145+i*.020,.081-i*.004
            m.tube('leather',[(x-.041,y,z),(x+.041,y+.012,z-.003)],.0025,6)
            m.tube('gold_edge',[(x+.041,y,z),(x-.041,y+.012,z-.003)],.0018,6)
        m.loft('suit_blue', [(x,.216,-.012,.067,.070),(x,.27,-.012,.074,.076),
             (x,.33,-.014,.071,.075),(x,.42,-.011,.084,.079),
             (x,.51,-.012,.089,.087),(x,.60,.006,.083,.085),
             (x,.667,.018,.088,.092),(x,.70,.012,.092,.095),
             (x,.78,-.008,.097,.106),(x,.91,-.016,.108,.122),
             (x,1.075,-.012,.119,.125),(x,1.145,-.008,.115,.129)], 28, ripple=.014)
        # Knee reinforcement is integrated cloth, not oversized armor.
        m.ellipsoid('suit_shadow',(x,.652,.093),(.135,.172,.025),20,10)
        m.tube('suit_light',[(x-.047,.728,.082),(x,.737,.095),(x+.047,.725,.080)],.0028,6)
        for i in range(3):
            m.tube('suit_shadow',[(x-.055,.29+i*.029,.043),(x,.28+i*.029,.066),
                    (x+.054,.296+i*.029,.043)],.0035,8)
        for i in range(2):
            m.tube('suit_light',[(x-.055,.546+i*.027,.051),(x,.539+i*.027,.076),
                    (x+.055,.555+i*.027,.052)],.0025,6)
        m.tube('suit_light',[(x+side*.067,.25,.01),(x+side*.084,.45,.012),
               (x+side*.091,.70,.028),(x+side*.109,.95,.024),(x+side*.114,1.10,.021)],.0028,6)
        # Flat side pocket and reinforced opening.
        m.box('suit_blue',(x+side*.055,.971,.096),(.085,.147,.021),side*-4)
        m.tube('suit_light',[(x+side*.055-.035,1.035,.113),
                            (x+side*.055+.035,1.035,.113)],.003,6)

    m.loft('suit_blue',[(0,1.04,-.014,.225,.124),(0,1.13,-.014,.238,.143),
         (0,1.23,-.016,.215,.129),(0,1.29,-.015,.204,.121)],28,2.4)
    # Torso cross sections create waist, rib cage, chest, shoulders, and neck.
    m.loft('suit_blue',[(0,1.235,-.015,.205,.124),(0,1.32,-.016,.206,.126),
         (0,1.40,-.019,.218,.131),(0,1.52,-.02,.244,.146),
         (0,1.65,-.021,.269,.150),(0,1.735,-.022,.279,.136),
         (0,1.80,-.025,.252,.12),(0,1.855,-.026,.144,.088)],32,ripple=.008)
    for side in [-1,1]:
        # Front tailoring seams emphasize natural pectoral/abdominal forms.
        m.tube('suit_light',[(side*.062,1.75,.111),(side*.147,1.733,.10),
               (side*.223,1.69,.071)],.003,6)
        m.tube('suit_shadow',[(side*.050,1.535,.122),(side*.123,1.529,.109),
               (side*.198,1.547,.079)],.0035,6)
        m.tube('suit_light',[(side*.184,1.31,.052),(side*.204,1.41,.058),
               (side*.221,1.54,.072),(side*.238,1.68,.067)],.003,6)
        for i in range(3):
            m.tube('suit_shadow',[(side*.051,1.33+i*.035,.112),
                   (side*.115,1.315+i*.035,.093),(side*.17,1.33+i*.035,.057)],.0025,6)
    # The reference's yellow collar, center stripe, waist and shoulder accents.
    m.loft('gold_trim',[(0,1.221,-.014,.217,.134),(0,1.273,-.014,.213,.132)],32,2.5)
    m.ribbon('gold_trim',[(0,1.268,.122),(0,1.40,.115),(0,1.52,.129),
             (0,1.65,.133),(0,1.735,.120),(0,1.80,.102),(0,1.855,.069)],.052)
    m.tube('gold_edge',[(0,1.274,.126),(0,1.40,.119),(0,1.52,.135),
             (0,1.65,.138),(0,1.735,.124),(0,1.80,.107),(0,1.855,.073)],.002,6)
    m.box('steel',(0,1.787,.104),(.012,.03,.007))
    m.loft('suit_shadow',[(0,1.84,-.023,.095,.074),(0,1.888,-.023,.093,.073)],28)
    m.loft('gold_trim',[(0,1.844,-.023,.097,.076),(0,1.864,-.023,.099,.077),
                       (0,1.891,-.023,.094,.074)],28)
    for side in [-1,1]:
        m.tube('gold_trim',[(side*.109,1.842,-.013),(side*.178,1.83,-.018),
               (side*.239,1.802,-.021),(side*.295,1.753,-.012)],.018,12)
        # Sleeves have a sloping shoulder and bent elbow, not cylindrical arms.
        m.loft('suit_blue',[(side*.367,1.104,.04,.051,.056),
             (side*.369,1.17,.027,.063,.065),(side*.364,1.29,.013,.075,.073),
             (side*.350,1.39,.006,.077,.078),(side*.337,1.455,.005,.077,.074),
             (side*.328,1.54,-.014,.083,.084),(side*.310,1.665,-.022,.098,.094),
             (side*.286,1.752,-.024,.091,.087),(side*.269,1.782,-.025,.070,.074)],24,ripple=.018)
        m.loft('gold_trim',[(side*.367,1.09,.04,.053,.058),
                           (side*.368,1.122,.036,.055,.059)],24)
        for i in range(3):
            m.tube('suit_shadow',[(side*.35-.044,1.365+i*.023,.064),
                   (side*.35,1.35+i*.023,.089),(side*.35+.044,1.367+i*.023,.066)],.003,6)
        m.tube('suit_light',[(side*.371,1.15,.063),(side*.386,1.29,.073),
               (side*.374,1.40,.061),(side*.359,1.51,.061),(side*.337,1.68,.067)],.0028,6)
        # Bare wrists and hands. Four tapered fingers, thumb, restrained knuckles.
        m.loft('skin',[(side*.367,1.029,.045,.040,.035),
             (side*.367,1.09,.040,.044,.038),(side*.367,1.115,.037,.047,.043)],20)
        m.ellipsoid('skin',(side*.368,1.01,.048),(.097,.121,.068),20,12)
        for i in range(4):
            x=side*.368+(i-1.5)*.021
            length=[.059,.075,.071,.052][i]
            m.tube('skin',[(x,.985,.066),(x,.967,.081),(x,.967-length*.60,.076),
                          (x,.967-length,.060)],[.012,.012,.010,.0075],10)
            m.ellipsoid('skin_light',(x,.99,.079),(.014,.019,.010),10,6)
            m.tube('skin_shadow',[(x-.006,.953,.084),(x+.006,.953,.084)],.0012,6)
        m.tube('skin',[(side*.334,1.041,.063),(side*.307,1.017,.085),
                      (side*.313,.984,.088)],[.018,.017,.012],12)

    # Small period-appropriate wrist computer, proportioned to the arm.
    m.box('leather',(-.367,1.18,.048),(.136,.12,.113),-2)
    m.box('steel',(-.371,1.19,.111),(.126,.113,.037),-2)
    m.box('sole',(-.385,1.195,.133),(.081,.074,.008),-2)
    m.box('screen',(-.385,1.195,.139),(.064,.056,.004),-2)
    for i in range(4):
        m.box('screen_text',(-.389,1.213-i*.012,.142),(.046-i*.007,.002,.0015))
    for i in range(3):
        m.ellipsoid('gold_edge',(-.321,1.223-i*.027,.133),(.009,.009,.007),8,6)
    # Quiet utility pouch/holster; the suit remains the dominant silhouette.
    m.box('leather',(.22,1.172,.022),(.072,.13,.118),-4)
    m.box('gold_edge',(.224,1.223,.085),(.06,.034,.013),-4)
    m.ellipsoid('steel',(.224,1.222,.094),(.011,.011,.006),8,6)

    # Neck and head use multiple anatomical cross sections, not an oval mask.
    m.loft('skin',[(0,1.86,-.023,.064,.059),(0,1.933,-.018,.061,.055),
                  (0,1.963,-.013,.069,.058)],28)
    m.loft('skin',[(0,1.943,.008,.042,.051),(0,1.957,.016,.064,.060),
         (0,1.978,.013,.078,.074),(0,2.007,.005,.087,.086),
         (0,2.041,-.002,.097,.091),(0,2.074,-.006,.095,.088),
         (0,2.11,-.011,.091,.091),(0,2.147,-.016,.085,.089),
         (0,2.179,-.021,.066,.071),(0,2.195,-.021,.032,.040)],32)
    for side in [-1,1]:
        m.ellipsoid('skin',(side*.096,2.052,-.009),(.031,.062,.032),16,10)
        m.ellipsoid('skin_shadow',(side*.109,2.052,.002),(.012,.035,.010),12,8)
        # Cheek planes sit below recessed eyes; small dark pupils avoid a toy look.
        m.ellipsoid('skin_shadow',(side*.042,2.071,.077),(.048,.020,.017),16,8)
        m.ellipsoid('skin_light',(side*.042,2.069,.087),(.029,.008,.004),12,6)
        m.ellipsoid('eye',(side*.042,2.070,.09),(.010,.008,.003),10,6)
        m.tube('hair',[(side*.022,2.091,.086),(side*.047,2.094,.083),
                      (side*.066,2.087,.074)],.0032,8)
    # Sculpted nose bridge, nostrils, mouth and chin.
    nose=[(-.012,2.083,.083),(.012,2.083,.083),(-.011,2.029,.108),(.011,2.029,.108),
          (0,2.032,.128),(-.019,2.020,.092),(.019,2.020,.092),(0,2.017,.106)]
    m.mesh('skin_light',nose,[(0,2,4),(0,4,1),(1,4,3),(2,5,7,4),(4,7,6,3),(5,6,7)])
    for side in [-1,1]:
        m.ellipsoid('skin_shadow',(side*.010,2.020,.108),(.010,.004,.006),10,6)
    m.tube('skin_shadow',[(-.026,1.997,.081),(0,1.996,.091),(.026,1.997,.081)],.002,8)
    m.tube('skin_light',[(-.018,1.990,.081),(0,1.989,.088),(.018,1.990,.081)],.0028,8)
    m.ellipsoid('skin_light',(0,1.967,.066),(.067,.024,.023),16,8)

    # Short, swept hair. The hairline rises above the forehead and drops at back.
    vs,fs=[],[]
    segments,rows=40,12
    for j in range(rows+1):
        t=j/rows
        for i in range(segments):
            a=i*2*pi/segments
            bottom=2.059+.068*max(0,sin(a))-.016*max(0,-sin(a))
            y=bottom+(2.212-bottom)*sin(t*pi/2)
            r=sqrt(max(0,1-((y-2.055)/.157)**2))
            vs.append((.114*r*cos(a),y,-.019+.118*r*sin(a)))
    for j in range(rows):
        for i in range(segments):
            a,b=j*segments+i,j*segments+(i+1)%segments
            fs.append((a,a+segments,b+segments,b))
    m.mesh('hair',vs,fs)
    for i in range(8):
        x=-.065+i*.018
        y=2.198-.043*(abs(x)/.085)**2
        m.tube('hair_light',[(x,2.14,.072),(x-.012,2.18,.025),
                            (x-.014,y,-.015),(x-.010,2.17,-.075)],.0022,6)
    # Raised, small back number in yellow: readable from the rear, not mirrored.
    for x,y,w,h in [(.047,1.68,.013,.118),(.034,1.733,.033,.013),(.047,1.625,.052,.012),
                    (-.047,1.736,.066,.014),(-.047,1.683,.062,.012),(-.047,1.625,.066,.014),
                    (-.075,1.710,.014,.055),(-.075,1.654,.014,.056)]:
        z=-.022-.151*sqrt(max(.01,1-(x/.274)**2))-.003
        m.box('gold_trim',(x,y,z),(w,h,.005))
    # Rear tailoring and seat folds visible in multi-character scenes.
    for side in [-1,1]:
        m.tube('suit_light',[(side*.115,1.3,-.119),(side*.148,1.47,-.134),
               (side*.185,1.64,-.133),(side*.183,1.75,-.119)],.0025,6)
        m.tube('suit_shadow',[(side*.04,1.085,-.143),(side*.12,1.062,-.135),
               (side*.20,1.088,-.10)],.003,6)
    return m


if __name__=='__main__':
    stats=build().export(OUT,'vault_dweller',PALETTE)
    (OUT/'model-info.json').write_text(json.dumps({
        'id':'vault_dweller','format':'obj','forward':'+Z','up':'+Y',
        'nominalHeight':2.2,'rigged':False,'textures':False,**stats
    },indent=2)+'\n')
    print(json.dumps(stats,indent=2))
