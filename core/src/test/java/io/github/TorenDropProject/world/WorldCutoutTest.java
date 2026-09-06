package io.github.TorenDropProject.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorldCutoutTest {
    @BeforeClass public static void natives() { GdxNativesLoader.load(); }

    @Test public void completeSpriteStaysAboveTerrainWithoutMovingItsProjectedAnchor() {
        OrthographicCamera camera = new WorldCameraRig().camera();
        camera.viewportWidth = 20;
        camera.viewportHeight = 13;
        // Item, tall prop, and both tree atlases, including edge anchors.
        for (int[] sprite : new int[][]{{64,64,32,16},{64,96,32,16},{126,126,63,6},
            {128,190,64,8},{64,64,32,0},{64,64,32,64}}) {
            for (float elevation : new float[]{0,2,4}) for (float size : new float[]{0.5f,1,2}) {
                float[][] points = WorldMapImporter.cutoutPoints(sprite[0], sprite[1], sprite[2], sprite[3],
                    5, elevation, -7, 64, size);
                for (float zoom : new float[]{0.4f,1,2}) {
                    camera.zoom = zoom;
                    camera.update();
                    Vector3 anchor = camera.project(new Vector3(5, elevation, -7), 0, 0, 1200, 780);
                    float pixelsPerArtPixel = 60f / zoom * (float)Math.sqrt(2) * size / 64;
                    for (int i = 0; i < points.length; i++) {
                        assertTrue("Sprite pixels must not be buried", points[i][1] >= elevation);
                        Vector3 screen = camera.project(new Vector3(points[i]), 0, 0, 1200, 780);
                        float pixelX = i % 2 == 0 ? -sprite[2] : sprite[0] - sprite[2];
                        float pixelY = i < 2 ? -sprite[3] : i < 4 ? 0 : sprite[1] - sprite[3];
                        assertEquals(anchor.x + pixelX * pixelsPerArtPixel, screen.x, 0.002f);
                        assertEquals(anchor.y + pixelY * pixelsPerArtPixel, screen.y, 0.002f);
                    }
                }
            }
        }
    }
}
