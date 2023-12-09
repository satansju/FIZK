package ZKBoo;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Util.Converter.convertBooleanArrayToByteArray;

class View {

    public final boolean[] randomness;
    boolean[] views;
    SecretKey seed;
    int size;
    int outputSize;
    int currentGate = 0;

    public View(int size, SecretKey seed, int outputSize, boolean[] randomness) {
        this.seed = seed;
        //views = new boolean[size];
        this.views = new boolean[size];
        this.size = size;
        this.outputSize = outputSize;
        this.randomness = randomness;
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
