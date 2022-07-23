package org.mondogrua;

import java.util.Optional;

public interface IRoll {
    Optional<Integer> getPins();

    public String getReport();
}
