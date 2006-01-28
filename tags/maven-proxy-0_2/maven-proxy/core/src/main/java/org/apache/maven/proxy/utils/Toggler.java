package org.apache.maven.proxy.utils;

/**
 * @author Ben Walding
 *
 */
public class Toggler {
    private int current;
    private final int start;
    private final String[] states;

    public Toggler(String[] states, int start) {
        this.states = states;
        this.start = start;
        reset();
    }

    public String getNext() {
        current = (current + 1) % states.length;
        String c = states[current];
        return c;
    }

    public String getCurrent() {
        return states[current];
    }

    public void reset() {
        current = start - 1;
    }
}
