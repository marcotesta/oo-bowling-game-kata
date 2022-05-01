package org.mondogrua;

import java.util.Optional;

public class Game {

    private final Frame frame10;
    private final Frame frame09;
    private final Frame frame08;
    private final Frame frame07;
    private final Frame frame06;
    private final Frame frame05;
    private final Frame frame04;
    private final Frame frame03;
    private final Frame frame02;
    private final Frame frame01;
    public Game() {
        frame10 = new Frame(10, Optional.empty());
        frame09 = new Frame(9, Optional.of(frame10));
        frame08 = new Frame(8, Optional.of(frame09));
        frame07 = new Frame(7, Optional.of(frame08));
        frame06 = new Frame(6, Optional.of(frame07));
        frame05 = new Frame(5, Optional.of(frame06));
        frame04 = new Frame(4, Optional.of(frame05));
        frame03 = new Frame(3, Optional.of(frame04));
        frame02 = new Frame(2, Optional.of(frame03));
        frame01 = new Frame(1, Optional.of(frame02));
    }

    public void add(Roll roll) {
        this.frame01.add(roll);
    }

    public Integer getScore() {
        ScoreAccumulator scoreAccumulator = new ScoreAccumulator();
        frame01.addScore(scoreAccumulator);
        return scoreAccumulator.value();
    }
}
