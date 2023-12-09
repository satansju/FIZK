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
        System.out.println("No of AND gates: " + circuit.getNumberOfAndGates());
        Prover prover = new Prover(input, gates, circuit.numberOfInputs, circuit.numberOfOutputs, circuit.getNumberOfAndGates());
        Verifier verifier = new Verifier(gates);

        prover.doMPCInTheHead();
        return verifier.receiveProof(prover.sendProofToVerifier());
    }
}
