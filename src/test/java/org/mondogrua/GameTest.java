package org.mondogrua;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
        assertEquals(Optional.empty(), game.getScore());
    }

    @Test
    void testScoreGivenOneRoll() {
        addRolls(new int[]{6});
        assertEquals(Optional.empty(), game.getScore());
    }

    @Test
    void testScoreGivenOneOpenFrame() {
        addRolls(new int[]{6, 2});
        assertEquals(Optional.of(8), game.getScore());
    }

    @Test
    void testScoreGivenTwoOpenFrames() {
        addRolls(new int[]{6, 2, 3, 2});
        assertEquals(Optional.of(13), game.getScore());
    }

    @Test
    void testScoreGivenOneSpare() {
        addRolls(new int[]{6, 4, 3, 2});
        assertEquals(Optional.of(18), game.getScore());
    }
    @Test
    void testScoreGivenOneStrike() {
        addRolls(new int[]{10, 4, 3});
        assertEquals(Optional.of(24), game.getScore());
    }
    @Test
    void testScoreGivenCompletGameEndingWithSpare() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 10, 10, 4, 6, 10});
        assertEquals(Optional.of(156), game.getScore());
    }
    @Test
    void testScoreGivenCompletGameEndingWithStrike() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 4, 6, 4, 6, 10, 5, 2});
        assertEquals(Optional.of(143), game.getScore());
    }

    @Test
    void testReportGivenNoRolls() {
        assertEquals("", game.getReport());
    }

    @Test
    void testReportGivenOneRoll() {
        addRolls(new int[]{6});
        assertEquals("6", game.getReport());
    }
    @Test
    void testReportGivenOneOpenFrame() {
        addRolls(new int[]{6, 2});
        assertEquals("6, 2, score: 8", game.getReport());
    }
    @Test
    void testReportGivenTwoOpenFrames() {
        addRolls(new int[]{6, 2, 3, 2});
        assertEquals(
                "6, 2, score: 8\n" +
                "3, 2, score: 13", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndNoBonus() {
        addRolls(new int[]{6, 4});
        assertEquals("6, /", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndOneBonus() {
        addRolls(new int[]{6, 4, 3});
        assertEquals("6, /, score: 13\n" +
                "3", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndOneBonusAndNextRoll() {
        addRolls(new int[]{6, 4, 6, 3});
        assertEquals("6, /, score: 16\n" +
                "6, 3, score: 25", game.getReport());
    }
    @Test
    void testReportGivenOneSpareAndOneOpenFrame() {
        addRolls(new int[]{6, 4, 3, 2});
        assertEquals("6, /, score: 13\n" +
                "3, 2, score: 18", game.getReport());
    }
    @Test
    void testReportGivenOneStrikeAndNoBonus() {
        addRolls(new int[]{10});
        assertEquals("X", game.getReport());
    }
    @Test
    void testReportGivenOneStrikeAndOneBonus() {
        addRolls(new int[]{10, 4});
        assertEquals("X\n" +
                "4", game.getReport());
    }
    @Test
    void testReportGivenOneStrikeAndTwoBonuses() {
        addRolls(new int[]{10, 4, 2});
        assertEquals("X, score: 16\n" +
                "4, 2, score: 22", game.getReport());
    }
    @Test
    void testReportingGivenCompletGameEndingWithSpare() {
        addRolls(new int[]{6, 4, 6, 3, 10, 10, 5, 3, 6, 2, 7, 1, 10, 10, 4, 6, 10});
        assertEquals("6, /, score: 16\n" +
                "6, 3, score: 25\n" +
                "X, score: 50\n" +
                "X, score: 68\n" +
                "5, 3, score: 76\n" +
                "6, 2, score: 84\n" +
                "7, 1, score: 92\n" +
                "X, score: 116\n" +
                "X, score: 136\n" +
                "4, /, score: 156", game.getReport());
    }
}
