package Util;

/**
 * @author @{USER} on @{DATE}
 * @project @{PROJECT}
 */
public class Converter {
    public static byte[] convertBooleanArrayToByteArray(boolean[] arr) {
        if (arr.length < 8) {
            Integer number = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i]) {
                    number += (int) Math.pow(2, i);
                }
            }
            return new byte[]{number.byteValue()};
        }

        int n = arr.length / 8;
        byte[] bytes = new byte[n];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
            }
            /*bytes[i] = Byte.parseByte(Arrays.asList(Arrays.copyOfRange(arr, i * 8, (i + 1) * 8)).stream().map(e -> Integer.parseInt(String.valueOf(e))).toString());*/
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
        boolean[] booleanArray = new boolean[arrayLength];
        for (int i = arrayLength-1; i >= 0; i--) {
            booleanArray[i] = (x & (1 << i)) != 0;
        }
        return booleanArray;
    }
}
