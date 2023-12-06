package ZKBoo;

import BooleanCircuit.Circuit;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Protocol {
    // TODO / FIXME: run multiple times to get better soundness error
    public static void main(String[] args) {
        Circuit circuit = new Circuit("src/BooleanCircuit/input/sha256.txt");
        circuit.parseCircuit();
        int[][] gates = circuit.getGates();
        int input = 1;
        System.out.println("No of AND gates: " + circuit.getNumberOfAndGates());
        Prover prover = new Prover(input, gates, circuit.numberOfInputs, circuit.numberOfOutputs, circuit.getNumberOfAndGates());
        Verifier verifier = new Verifier();

        prover.doMPCInTheHead();
        verifier.receiveProof(prover.sendProofToVerifier());

        // verifier.receiveProof(prover.views, prover.getSeed, prover.size, prover.outputSize, gates);
    }
}
