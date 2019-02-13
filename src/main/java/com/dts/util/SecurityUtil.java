/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author GiangLT
 */
public class SecurityUtil {

    /**
     * From a base 64 representation, returns the corresponding byte[]
     *
     * @param data String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    public static String byteToBase64(byte[] data) {
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }

    public static byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }

    public static String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
//        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // Salt generation 64 bits long
        byte[] bSalt = new byte[8];
        random.nextBytes(bSalt);
        // Digest computation
        return byteToBase64(bSalt);
    }

    public static String getHash(int iterationNb, String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        byte[] saltArray = SecurityUtil.base64ToByte(salt);
        byte[] hash = SecurityUtil.getHash(iterationNb, password, saltArray);
        return byteToBase64(hash);
    }

    public static boolean isMatched(String strToCompare, String strHash, String salt, int iterationNb) throws Exception {
        byte[] saltArray = SecurityUtil.base64ToByte(salt);
        byte[] hash = SecurityUtil.getHash(iterationNb, strToCompare, saltArray);
        return Arrays.equals(hash, SecurityUtil.base64ToByte(strHash));
    }

    public static void main(String[] args) {
        try {
//            String salt = SecurityUtil.generateSalt();
            String salt = "VjV3rSWm/XM=";
            String password = "admin";
            System.out.println("Original Password: " + password);
            System.out.println("Salt: " + salt);

            byte[] hash = SecurityUtil.getHash(5, password, SecurityUtil.base64ToByte(salt));
            String passwordHash = SecurityUtil.byteToBase64(hash);
            System.out.println("Password Hash: " + passwordHash);
            System.out.println("======================================");

            String salt1 = "vOE/gtpVfb4=";
            String password1 = "FacesUtil";
            System.out.println("Original Password: " + password1);
            System.out.println("Salt: " + salt1);

            byte[] hash1 = SecurityUtil.getHash(5, password1, SecurityUtil.base64ToByte(salt1));
            String passwordHash1 = SecurityUtil.byteToBase64(hash1);
            System.out.println("Password Hash: " + passwordHash1);
            System.out.println(passwordHash1.equals("nwPJlcS+VGjbCGJP57wun9MeN0NDzosOZd9GRWc+wXI="));
            System.out.println(Arrays.equals(hash1, SecurityUtil.base64ToByte("WB8fJFpB6TwrtPtYII/OjzfzTBtfdVvKHkwkKILbInY=")));
            //WB8fJFpB6TwrtPtYII/OjzfzTBtfdVvKHkwkKILbInY=
//            System.out.println("String: " + SecurityUtil.byteToBase64(byteData));

        } catch (Exception ex) {
            LoggerFactory.getLogger(SecurityUtil.class.getName()).error("", ex);
        }

    }
}
