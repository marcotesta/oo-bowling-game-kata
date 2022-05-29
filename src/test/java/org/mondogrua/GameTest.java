package org.mondogrua;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GameTest {

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
    void testScoreGivenNoRolls() {
        assertEquals(0, game.getScore());
    }

    @Test
    void testScoreGivenOneRoll() {
        addRolls(new int[]{6});
        assertEquals(0, game.getScore());
    }

    @Test
    void testScoreGivenOneOpenFrame() {
        addRolls(new int[]{6, 2});
        assertEquals(8, game.getScore());
    }

    @Test
    void testScoreGivenTwoOpenFrames() {
        addRolls(new int[]{6, 2, 3, 2});
        assertEquals(13, game.getScore());
    }

    @Test
    void testScoreGivenOneSpare() {
        addRolls(new int[]{6, 4, 3, 2});
        assertEquals(18, game.getScore());
    }
    @Test
    void testScoreGivenOneStrike() {
        addRolls(new int[]{10, 4, 3});
        assertEquals(24, game.getScore());
    }
    @Test
    void testScoreGivenCompletGame() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 10, 10, 4, 6, 10});
        assertEquals(156, game.getScore());
    }

    @Test
    void testReportGivenNoRolls() {
        assertEquals("", game.getReport());
    }
}
