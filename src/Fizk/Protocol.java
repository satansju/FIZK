package Fizk;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */


import BooleanCircuit.Circuit;
import BooleanCircuit.Shares;
import ZKBoo.Proof;
import ZKBoo.Prover;
import ZKBoo.Verifier;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Arrays;

import static Util.Util.hash;

/**
 * FISH signature scheme
 * Gen(1κ) → (pk, sk): Sample key k ∈R Kκ and secret key x ∈R Dκ. Compute the image y =
 * fk(x). Set public key pair: pk = (y, k) and sk = (pk, x).
 * Sign(sk, m) → σ: Compute the proof of knowledge by running the prover algorithm and use it
 * as the signature: σ = (a, z) = ProveH(sk). Here, the generated hash challenge depends on
 * a and the message m: e = H(a, m).
 * Verify(pk, m, σ) → accept/reject: Run the verification algorithm on the public key and the proof
 * from the prover: VerifyH(pk, σ). Again, the hash challenge is computed as e = H(a, m).
 * Output accept if VerifyH(pk, σ) = 1 and reject otherwise.
 */
public class Protocol {
    PublicKeyPair publicKeyPair;
    SecretkeyPair secretkeyPair;

    Shares shareobj;
    Circuit circuit;
    Prover prover;

    int input;
    int[][] gates;
    int numberOfInputs;
    int numberOfOutputs;
    int numberOfAndGates;
    public Protocol() {
        this.shareobj = new Shares();
        String path = "src/BooleanCircuit/input/sha256.txt";
        this.circuit = new Circuit(path);
        this.circuit.parseCircuit();
        this.gates = circuit.getGates();
        this.numberOfInputs = circuit.getNumberOfInputs();
        this.numberOfOutputs = circuit.getNumberOfOutputs();
        this.numberOfAndGates = circuit.getNumberOfAndGates();
    }

    /**
     * Gen(1κ) → (pk, sk): Sample key k ∈R Kκ and secret key x ∈R Dκ. Compute the image y =
     *  * fk(x). Set public key pair: pk = (y, k) and sk = (pk, x).
     */
    public void generateSecretkeyPair() {
        SecureRandom secureRandom = new SecureRandom();
        this.input = secureRandom.nextInt();
        this.prover = new Prover(input, gates, numberOfInputs, numberOfOutputs, numberOfAndGates);
        prover.doMPCInTheHead();

        // Set secret key first
        byte[] y = prover.getOutputAsByteArray();
        SecretKey secretKey = shareobj.generateSecretKey();
        PublicKeyPair publicKeyPair = new PublicKeyPair(y, secretKey);
        SecretkeyPair secretkeyPair = new SecretkeyPair(publicKeyPair, input);
        this.publicKeyPair = publicKeyPair;
        this.secretkeyPair = secretkeyPair;
    }

    /**
     * Sign(sk, m) → σ: Compute the proof of knowledge by running the prover algorithm and use it
     *  * as the signature: σ = (a, z) = ProveH(sk). Here, the generated hash challenge depends on
     *  * a and the message m: e = H(a, m).
     */
    public Signature sign(SecretkeyPair keyPair, byte[] message) {
        // Get prove from prover
        Proof prove = prover.sendProofToVerifier();

        // Set signature
        Signature signature = new Signature(prove, message);

        return signature;
    }

    /**
     * Verify(pk, m, σ) → accept/reject: Run the verification algorithm on the public key and the proof
     *  * from the prover: VerifyH(pk, σ). Again, the hash challenge is computed as e = H(a, m).
     *  * Output accept if VerifyH(pk, σ) = 1 and reject otherwise.
     */
    public boolean verify(PublicKeyPair keyPair, byte[] message, Signature signature) {
        // Get proof from signature
        Proof proof = signature.getProof();

        // Check verify
        Verifier verifier = new Verifier(gates, numberOfInputs, numberOfOutputs, numberOfAndGates);
        verifier.receiveProof(proof);
        boolean verification = verifier.verify();
        if(!verification) {
            return false;
        }

        // Check hash challenge
        byte[] aArray = proof.aArray;
        byte[] combined = new byte[aArray.length + message.length];
        System.arraycopy(aArray, 0, combined, 0, aArray.length);
        System.arraycopy(message, 0, combined, aArray.length, message.length);

        byte[] computedHashChallenge = hash(combined);
        byte[] hashChallenge = signature.getChallenge();
        boolean checkHashChallenge = Arrays.equals(computedHashChallenge, hashChallenge);
        if (!checkHashChallenge) {
            return false;
        }

        // Check outputs are equal
        byte[] output = signature.getProof().output;
        byte[] y = keyPair.getY();
        boolean checkOutputs = Arrays.equals(output, y);
        if (!checkOutputs) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        Protocol protocol = new Protocol();
        protocol.generateSecretkeyPair();
        SecretkeyPair secretkeyPair = protocol.secretkeyPair;
        PublicKeyPair publicKeyPair = protocol.publicKeyPair;
        byte[] message = "Hello World".getBytes();
        Signature signature = protocol.sign(secretkeyPair, message);
        boolean verification = protocol.verify(publicKeyPair, message, signature);
        System.out.println(verification);
    }

    public SecretkeyPair getSecretkeyPair() {
        return secretkeyPair;
    }

    public PublicKeyPair getPublicKeyPair() {
        return publicKeyPair;
    }
}
