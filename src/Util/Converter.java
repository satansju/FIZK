package Util;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Converter {
    public static byte[] convertBooleanArrayToByteArray(boolean[] arr) {
        if (arr.length < 8) {
            Integer number = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[arr.length - i - 1]) {
                    number += (int) Math.pow(2, i);
                }
            }
            return new byte[]{number.byteValue()};
        }

        int n = (int) Math.ceil(arr.length / 8.0);
        byte[] bytes = new byte[n];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
            }
        }
        return bytes;
    }

    public static boolean[] convertByteArrayToBooleanArray(byte[] arr) {
        boolean[] booleans = new boolean[arr.length * 8];

        for(int i = 0; i<arr.length; i++) {
            for (int j = 0; j < 8; j++) {
                // Extract individual bits from each byte
                booleans[i * 8 + j] = ((arr[i] >> (7-j)) & 1) == 1;
            }
        }
        return booleans;
    }


    // Convert int to boolean array
    public static boolean[] intToBooleanArray(int x, int arrayLength) {
        BigInteger xAsBigInt = BigInteger.valueOf(x);
        byte[] xAsBytes = xAsBigInt.toByteArray();
        boolean[] result = convertByteArrayToBooleanArray(xAsBytes);
        if(arrayLength > result.length){
            boolean[] temp = new boolean[arrayLength];
            Arrays.fill(temp, false);
            System.arraycopy(result, 0, temp, arrayLength - result.length, result.length);;
            return temp;
        }
        return Arrays.copyOfRange(result, result.length - arrayLength, result.length);
    }

    public static int convertByteArrayToInteger(byte[] challenge) {
        return new BigInteger(challenge).intValue();
    }
}
