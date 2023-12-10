package ZKBoo;

import BooleanCircuit.Circuit;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Protocol {
    // TODO / FIXME: run multiple times to get better soundness error
    public static boolean runProtocol(String path, int input) {
        Circuit circuit = new Circuit(path);
        circuit.parseCircuit();
        int[][] gates = circuit.getGates();
        // System.out.println("No of AND gates: " + circuit.getNumberOfAndGates());
        int numberOfInputs = circuit.getNumberOfInputs();
        int numberOfOutputs = circuit.getNumberOfOutputs();
        int numberOfAndGates = circuit.getNumberOfAndGates();
        Prover prover = new Prover(input, gates, numberOfInputs, numberOfOutputs, numberOfAndGates);
        Verifier verifier = new Verifier(gates, numberOfInputs, numberOfOutputs, numberOfAndGates);

        prover.doMPCInTheHead();
        verifier.receiveProof(prover.sendProofToVerifier());
        if(verifier.verify()) {
            // System.out.println("Proof is correct");
            return true;
        } else {
            // System.out.println("Proof is incorrect");
            return false;
        }
    }
}
