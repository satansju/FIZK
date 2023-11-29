package ZKBoo;

import java.util.ArrayList;
import java.util.Arrays;

class View {
    boolean[] views;
    int seed;
    int size;
    int outputSize;

    public View(int size, int seed, int outputSize) {
        seed = seed;
        views = new boolean[size];
        size = size;
        outputSize = outputSize;
    }

    public void updateView(int i, boolean val) {
        views[i] = val;
    }

    public boolean[] output() {
        return Arrays.copyOfRange(views, size-outputSize, size);
    }
}
