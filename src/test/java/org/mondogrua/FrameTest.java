package org.mondogrua;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FrameTest {

    Game game;
    private void addRolls(int[] rolls) {
        for (int roll : rolls) {
            game.add(new Roll(roll));
        }
    }

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    void testAddRoll() {
        addRolls(new int[]{6});
        assertEquals(0, game.getScore());
    }

    @Test
    void testAdd2RollsOpenFramework() {
        addRolls(new int[]{6, 2});
        assertEquals(8, game.getScore());
    }

    @Test
    void testAdd4RollsBothFramesOpen() {
        addRolls(new int[]{6, 2, 3, 2});
        assertEquals(13, game.getScore());
    }

}
