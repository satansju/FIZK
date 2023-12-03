package ZKBoo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class View {

    List<Boolean> views;
    int seed;
    int size;
    int outputSize;

    public View(int size, int seed, int outputSize) {
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

    // TODO: skal vi have output med i viewet?
    public boolean[] output() {
//        return Arrays.copyOfRange(views, size - outputSize, size);
        boolean[] output = new boolean[outputSize];

        // convert list to bool array containing the output shares
        for (int i = 0; i < outputSize; i++) {
            output[i] = views.get(i + (size - outputSize));
        }

        return output;
    }
}
