package ZKBoo;

import javax.crypto.SecretKey;

class View {

    boolean[] andGateEvaluations;
    SecretKey seed;
    int numberOfAndGates;
    int outputSize;
    int currentGate = 0;

    public View(int numberOfAndGates, SecretKey seed, int outputSize) {
        this.seed = seed;
        this.andGateEvaluations = new boolean[numberOfAndGates];
        this.numberOfAndGates = numberOfAndGates;
        this.outputSize = outputSize;
    }

    public void updateView(boolean val) {
        andGateEvaluations[currentGate] = val;
        currentGate++;
    }

    public void addInputShare(boolean[] share) {
        for (boolean b : share) {
            updateView(b);
        }
    }
}
