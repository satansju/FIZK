package Test.ZKBoo;

import BooleanCircuit.Shares;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static Util.Converter.convertBooleanArrayToByteArray;
import static Util.Converter.intToBooleanArray;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class SharesTest {
    @Test
    public void testGetShares() throws Exception {
        // Generate a random secret key for AES with 256-bit key size
        int input = 1;
        int numberOfInputs = 512;
        boolean[] inputBits = intToBooleanArray(input, numberOfInputs);
        boolean[][] shares = Shares.getShares(input);
        boolean[] x1 = shares[0];
        boolean[] x2 = shares[1];
        boolean[] x3 = shares[2];

        assert(x3.length == 512);

        // Convert to BigInteger for printing
        byte[] x1Bytes = convertBooleanArrayToByteArray(x1);
        byte[] x2Bytes = convertBooleanArrayToByteArray(x2);
        byte[] x3Bytes = convertBooleanArrayToByteArray(x3);


        BigInteger x1Int = new BigInteger(x1Bytes);
        BigInteger x2Int = new BigInteger(x2Bytes);
        BigInteger x3Int = new BigInteger(x3Bytes);

        boolean[] xCombined = new boolean[x1.length];

        for (int i = 0; i < x1.length; i++) {
            xCombined[i] = x1[i] ^ x2[i] ^ x3[i];
        }

        BigInteger xCombinedInt = new BigInteger(convertBooleanArrayToByteArray(xCombined));

        // Verify that x1 xor x2 xor x3 = x and x3 = x xor x1 xor x2
        System.out.println("x1: " + x1Int);
        System.out.println("x2: " + x2Int);
        System.out.println("x3: " + x3Int);
        System.out.println("x1 xor x2 xor x3: " + xCombinedInt);

        Assert.assertTrue(Arrays.equals(xCombined, inputBits));
    }
}
