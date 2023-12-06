package ZKBoo;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class View {

    List<Boolean> views;
    SecretKey seed;
    int size;
    int outputSize;

    public View(int size, SecretKey seed, int outputSize) {
        seed = seed;
        //views = new boolean[size];
        views = new ArrayList<>();
        size = size;
        outputSize = outputSize;
    }

    public void updateView(boolean val) {
        views.add(val);
    }

    // todo: test this
    public void addInputShare(boolean[] share) {
        for (boolean b : share) {
            updateView(b);
        }
    }
}
