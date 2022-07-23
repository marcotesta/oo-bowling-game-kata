package org.mondogrua;

import java.util.Optional;

public interface IRoll {

    void addPinsTo(ScoreAccumulator accumulator);

    String getReport();
}
