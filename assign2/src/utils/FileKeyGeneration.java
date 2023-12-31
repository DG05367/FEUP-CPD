package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileKeyGeneration {

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String keyGen(FileInputStream inputStream) throws IOException {
        String key = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.reset();
            DigestInputStream digest = new DigestInputStream(inputStream, algorithm);
            while(digest.read() != -1) {}
            key = bytesToHex(algorithm.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }
}
