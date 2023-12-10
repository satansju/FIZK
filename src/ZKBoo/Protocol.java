package ZKBoo;

import BooleanCircuit.Circuit;

import java.security.SecureRandom;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Protocol {
    // TODO / FIXME: run multiple times to get better soundness error
    public static boolean runProtocol(String path, int input) {
        Circuit circuit = new Circuit(path);
        circuit.parseCircuit();
        return runZKBooProtocol(circuit, input);
    }

    public static boolean runZKBooProtocol(Circuit circuit, int input) {
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
            return true;
        } else {
            return false;
        }
    }
    
    
    
    public static double runProtocolSeveralTimes(String path, long numberOfRounds) {
        Circuit circuit = new Circuit(path);
        circuit.parseCircuit();
        long totalDuration = 0;
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < numberOfRounds; i++) {
            int input = secureRandom.nextInt();
            long start = System.currentTimeMillis();
            if(!runZKBooProtocol(circuit, input)) {
                throw new Error("Protocol failed");
            }
            long end = System.currentTimeMillis();
            totalDuration += (end - start);
        }
        return (double) totalDuration / numberOfRounds;
    } 
}
