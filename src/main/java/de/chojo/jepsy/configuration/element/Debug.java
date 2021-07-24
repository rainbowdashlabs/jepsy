package de.chojo.jepsy.configuration.element;

import java.util.ArrayList;
import java.util.List;

public class Debug {
    boolean debug;
    long[] guildIds = new long[0];

    public boolean isDebug() {
        return debug;
    }

    public long[] guildIds() {
        return guildIds;
    }
}
