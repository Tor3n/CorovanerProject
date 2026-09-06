package io.github.TorenDropProject.menus;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class CharacterStatsTest {
    private FrontierCatalog catalog() {
        File file = new File("../assets/data/frontier.json");
        if (!file.exists()) file = new File("assets/data/frontier.json");
        return new Json().fromJson(FrontierCatalog.class, new FileHandle(file));
    }
    @Test public void matchingBackgroundAndCallingTrainOnlyOnce() {
        CharacterRecord c = new CharacterRecord(); c.standardArray();
        c.origin = "vault"; c.archetype = "tinker";
        assertTrue(CharacterStats.trained(c, catalog(), CharacterStats.Skill.TECHNOLOGY));
        assertEquals(2, CharacterStats.skillBonus(c, catalog(), CharacterStats.Skill.TECHNOLOGY));
        assertEquals(-1, CharacterStats.skillBonus(c, catalog(), CharacterStats.Skill.PERSUASION));
    }
    @Test public void trainingUsesTheGoverningAbilityAndProficiency() {
        CharacterRecord c = new CharacterRecord(); c.standardArray();
        assertEquals(4, CharacterStats.skillBonus(c, catalog(), CharacterStats.Skill.SURVIVAL));
        assertEquals(4, CharacterStats.skillBonus(c, catalog(), CharacterStats.Skill.PERCEPTION));
        assertEquals(1, CharacterStats.skillBonus(c, catalog(), CharacterStats.Skill.ATHLETICS));
    }
}
