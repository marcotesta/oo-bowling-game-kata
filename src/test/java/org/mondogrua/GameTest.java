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
    void testScoreGivenCompletGameEndingWithSpare() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 10, 10, 4, 6, 10});
        assertEquals(156, game.getScore());
    }
    @Test
    void testScoreGivenCompletGameEndingWithStrike() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 4, 6, 4, 6, 10, 5, 2});
        assertEquals(143, game.getScore());
    }
    @Test
    void testScoreGivenCompletGameAllZero() {
        addRolls(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        assertEquals(0, game.getScore());
    }

    @Test
    void testReportGivenNoRolls() {
        assertEquals("Frame 1: \n" +
                "Frame 2: \n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }

    @Test
    void testReportGivenOneRoll() {
        addRolls(new int[]{6});
        assertEquals("Frame 1: 6\n" +
                "Frame 2: \n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneOpenFrame() {
        addRolls(new int[]{6, 2});
        assertEquals("Frame 1: 6, 2, score: 8\n" +
                "Frame 2: \n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenTwoOpenFrames() {
        addRolls(new int[]{6, 2, 3, 2});
        assertEquals("Frame 1: 6, 2, score: 8\n" +
                "Frame 2: 3, 2, score: 13\n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndNoBonus() {
        addRolls(new int[]{6, 4});
        assertEquals("Frame 1: 6, /\n" +
                "Frame 2: \n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndOneBonus() {
        addRolls(new int[]{6, 4, 3});
        assertEquals("Frame 1: 6, /, score: 13\n" +
                "Frame 2: 3\n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndOneBonusAndNextRoll() {
        addRolls(new int[]{6, 4, 6, 3});
        assertEquals("Frame 1: 6, /, score: 16\n" +
                "Frame 2: 6, 3, score: 25\n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndOneOpenFrame() {
        addRolls(new int[]{6, 4, 3, 2});
        assertEquals("Frame 1: 6, /, score: 13\n" +
                "Frame 2: 3, 2, score: 18\n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneStrikeAndNoBonus() {
        addRolls(new int[]{10});
        assertEquals("Frame 1: X\n" +
                "Frame 2: \n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneStrikeAndOneBonus() {
        addRolls(new int[]{10, 4});
        assertEquals("Frame 1: X\n" +
                "Frame 2: 4\n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportGivenOneStrikeAndTwoBonuses() {
        addRolls(new int[]{10, 4, 2});
        assertEquals("Frame 1: X, score: 16\n" +
                "Frame 2: 4, 2, score: 22\n" +
                "Frame 3: \n" +
                "Frame 4: \n" +
                "Frame 5: \n" +
                "Frame 6: \n" +
                "Frame 7: \n" +
                "Frame 8: \n" +
                "Frame 9: \n" +
                "Frame 10: ", game.getReport());
    }
    @Test
    void testReportingGivenCompletGameEndingWithSpare() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 10, 10, 4, 6, 10});
        assertEquals("Frame 1: 6, /, score: 16\n" +
                "Frame 2: 6, 3, score: 25\n" +
                "Frame 3: X, score: 50\n" +
                "Frame 4: X, score: 68\n" +
                "Frame 5: 5, 3, score: 76\n" +
                "Frame 6: 6, 2, score: 84\n" +
                "Frame 7: 7, 1, score: 92\n" +
                "Frame 8: X, score: 116\n" +
                "Frame 9: X, score: 136\n" +
                "Frame 10: 4, /, score: 156", game.getReport());
    }
}
