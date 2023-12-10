package Test.ZKBoo;

import BooleanCircuit.Shares;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static Util.Converter.convertBooleanArrayToByteArray;
import static Util.Converter.intToBooleanArray;
import static org.junit.Assert.assertTrue;

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
        Shares sharesObj = new Shares();
        boolean[][] shares = sharesObj.getShares(input, numberOfInputs);
        // System.out.println(sharesObj.getSecretKeys().length);
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

        assertTrue(Arrays.equals(xCombined, inputBits));
    }

    @Test
    public void generateRandomBits() throws Exception {
        Shares sharesObj = new Shares();
        int length = 512;
        boolean[] randomBits = sharesObj.generateRandomBits(length, sharesObj.getSecretKeys()[0]);
        Assert.assertEquals(length, randomBits.length);
        System.out.println(randomBits.length);
        System.out.println(Arrays.toString(randomBits));
    }

    @Test
    public void generateBitStreamsTests() {
        Shares sharesObj = new Shares();
        int length = 512;
        boolean[][] randomBits = sharesObj.generateBitStreams(length);
        Assert.assertEquals(length, randomBits[0].length);
        Assert.assertEquals(length, randomBits[1].length);
        Assert.assertEquals(length, randomBits[2].length);
    }

    @Test
    public void testThatSeededBitStreamsAreEqual() {
        Shares sharesObj = new Shares();
        int length = 512;
        boolean[][] randomBits = sharesObj.generateBitStreams(length);
        Assert.assertEquals(length, randomBits[0].length);
        Assert.assertEquals(length, randomBits[1].length);
        Assert.assertEquals(length, randomBits[2].length);

        boolean[][] randomBits2 = sharesObj.generateBitStreams(length);
        Assert.assertTrue(Arrays.equals(randomBits[0], randomBits2[0]));
        Assert.assertTrue(Arrays.equals(randomBits[1], randomBits2[1]));
        Assert.assertTrue(Arrays.equals(randomBits[2], randomBits2[2]));
    }

    @Test
    public void testThatDifferentlySeededBitStreamsAreDifferent() {
        Shares sharesObj1 = new Shares();
        int length = 512;
        boolean[][] randomBits = sharesObj1.generateBitStreams(length);
        Assert.assertEquals(length, randomBits[0].length);
        Assert.assertEquals(length, randomBits[1].length);
        Assert.assertEquals(length, randomBits[2].length);

        Shares sharesObj2 = new Shares();
        boolean[][] randomBits2 = sharesObj2.generateBitStreams(length);
        Assert.assertFalse(Arrays.equals(randomBits[0], randomBits2[0]));
        Assert.assertFalse(Arrays.equals(randomBits[1], randomBits2[1]));
        Assert.assertFalse(Arrays.equals(randomBits[2], randomBits2[2]));
    }

    @Test
    public void testGenerateBitStreamsWithLengthAndSecretKey() {
        Shares sharesObj = new Shares();
        int length = 512;
        boolean[] randomBits = sharesObj.generateRandomBits(length, sharesObj.getSecretKeys()[0]);
        Assert.assertEquals(length, randomBits.length);
        System.out.println(randomBits.length);
        System.out.println(Arrays.toString(randomBits));
    }

    @Test
    public void testGenerateTwoBitStreamsWithSameSecretKey() {
        Shares sharesObj = new Shares();
        int length = 512;
        boolean[] randomBits1 = sharesObj.generateRandomBits(length, sharesObj.getSecretKeys()[0]);
        boolean[] randomBits2 = sharesObj.generateRandomBits(length, sharesObj.getSecretKeys()[0]);
        Assert.assertTrue(Arrays.equals(randomBits1, randomBits2));
        System.out.println(randomBits1.length);
        System.out.println(randomBits2.length);
        System.out.println(Arrays.toString(randomBits1));
        System.out.println(Arrays.toString(randomBits2));
    }
}
