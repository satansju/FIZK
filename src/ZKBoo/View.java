package ZKBoo;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class View {

    boolean[] views;
    SecretKey seed;
    int size;
    int outputSize;
    int currentGate = 0;

    public View(int size, SecretKey seed, int outputSize) {
        seed = seed;
        //views = new boolean[size];
        views = new boolean[size];
        size = size;
        outputSize = outputSize;
    }

    public void updateView(boolean val) {
        views[currentGate] = val;
        currentGate++;
    }

    // todo: test this
    public void addInputShare(boolean[] share) {
        for (boolean b : share) {
            updateView(b);
        }
    }
}
