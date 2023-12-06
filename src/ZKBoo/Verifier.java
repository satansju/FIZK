package ZKBoo;

import java.util.HashMap;

// Verify the output of the prover is correct and reject otherwise
public class Verifier {
    View[] views;
    int seed;
    int size;
    int outputSize;
    int[][] circuit;

    public void receiveProof(View[] views, int seed, int size, int outputSize, int[][] circuit) {
        this.views = views;
        this.seed = seed;
        this.size = size;
        this.outputSize = outputSize;
        this.circuit = circuit;

        if(verify()) {
            System.out.println("Proof is correct");
        } else {
            System.out.println("Proof is incorrect");
        }
    }

    public boolean verify() {
        HashMap<Integer, Boolean> wires = new HashMap<>();
        for(int i = 0; i < 2; i++) {
            for (int j = 0; j< circuit.length; j++) {
                int[] gate = circuit[j];
                int numberOfInputWires = gate[0];
                int outputWireIndex = gate.length - 2;
                int outputWire = gate[outputWireIndex];
                String op = String.valueOf(gate[gate.length - 1]);
                int[] inputWires = new int[numberOfInputWires];

                for (int k = 0; k < numberOfInputWires; k++) {
                    inputWires[k] = gate[k + 2];
                }

                boolean result = evalGate(op, inputWires);
                /*System.out.println(result);*/
                wires.put(outputWire, result);
                if(! wires.get(i).equals(views[i].output()[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean evalGate(String op, int[] inputWires) {
        return false;
    }
}
