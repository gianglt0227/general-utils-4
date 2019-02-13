/*
 * Copyright(C) 2009 Viettel telecom
 *
 * EncryptManager.java, Feb 06, 2009, SonPN
 */
package com.dts.util;

/**
 *
 * @author ManhPS
 */
public final class EncryptManager {
    // logger

//    private static final char[] kDigits = {'0', '1', '2', '3', '4', '5', '6',
//        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//
//    public static String md5Encrypt(String password) {
//        try {
//            MessageDigest m = MessageDigest.getInstance("MD5");
//            m.update(password.getBytes(), 0, password.length());
//            return new BigInteger(1, m.digest()).toString(16);
//        } catch (Exception ex) {
//            log.error("md5Encrypt", ex);
//        }
//        return "";
//    }
//
//    public static String decryptRSA(String toDecrypt, String strPrivateKey) {
//
//        RSAPrivateCrtKeyParameters rsaPrKey = getBCPrivateKeyFromString(strPrivateKey);
//        // log.info("running decryptRSA ....");
//        if (rsaPrKey == null) {
//            log.info("RSAPrivateKey == null");
//            return null;
//        }
//
//        try {
//            AsymmetricBlockCipher theEngine = new RSAEngine();
//            theEngine = new PKCS1Encoding(theEngine);
//            theEngine.init(false, rsaPrKey);
//            return new String(theEngine.processBlock(Base64.decode(toDecrypt),
//                    0, Base64.decode(toDecrypt).length));
//        } catch (Exception ex) {
//            log.error("decryptRSA", ex);
//        }
//        return null;
//    }
//
//    public static String encryptRSA(String toEncrypt, String strPublicKey) {
//        RSAKeyParameters rsaPbKey = getBCPublicKeyFromString(strPublicKey);
//        if (rsaPbKey == null) {
//            log.info("RSAPublicKey == null");
//            return null;
//        }
//
//        try {
//            AsymmetricBlockCipher theEngine = new RSAEngine();
//            theEngine = new PKCS1Encoding(theEngine);
//            theEngine.init(true, rsaPbKey);
//            return new String(Base64.encode(theEngine.processBlock(
//                    toEncrypt.getBytes(), 0, toEncrypt.getBytes().length)));
//        } catch (InvalidCipherTextException ex) {
//            log.error("encryptRSA", ex);
//        }
//        return null;
//    }
//
//    private static RSAPrivateCrtKeyParameters getBCPrivateKeyFromString(
//            String strPrivateKey) {
//        try {
//            PrivateKey prvKey = getPrivateKeyFromString(strPrivateKey);
//            KeyFactory keyFac = KeyFactory.getInstance("RSA");
//            RSAPrivateCrtKeySpec pkSpec = keyFac.getKeySpec(prvKey,
//                    RSAPrivateCrtKeySpec.class);
//            RSAPrivateCrtKeyParameters priv = new RSAPrivateCrtKeyParameters(
//                    pkSpec.getModulus(), pkSpec.getPublicExponent(),
//                    pkSpec.getPrivateExponent(), pkSpec.getPrimeP(),
//                    pkSpec.getPrimeQ(), pkSpec.getPrimeExponentP(),
//                    pkSpec.getPrimeExponentQ(), pkSpec.getCrtCoefficient());
//            return priv;
//        } catch (Exception e) {
//            return null;
//        }
//
//    }
//
//    private static RSAKeyParameters getBCPublicKeyFromString(String strPublicKey) {
//        try {
//            PublicKey prvKey = getPublicKeyFromString(strPublicKey);
//
//            KeyFactory keyFac = KeyFactory.getInstance("RSA");
//            RSAPublicKeySpec pkSpec = keyFac.getKeySpec(prvKey,
//                    RSAPublicKeySpec.class);
//
//            RSAKeyParameters pub = new RSAKeyParameters(false,
//                    pkSpec.getModulus(), pkSpec.getPublicExponent());
//            return pub;
//        } catch (Exception e) {
//            return null;
//        }
//
//    }
//
//    /**
//     *
//     * @param data
//     * @return
//     */
//    public static String byteToHex(byte[] data) {
//        StringBuilder buf = new StringBuilder();
//        for (int i = 0; i < data.length; i++) {
//            int halfbyte = (data[i] >>> 4) & 0x0F;
//            int two_halfs = 0;
//
//            do {
//                if ((0 <= halfbyte) && (halfbyte <= 9)) {
//                    buf.append((char) ('0' + halfbyte));
//                } else {
//                    buf.append((char) ('a' + (halfbyte - 10)));
//                }
//                halfbyte = data[i] & 0x0F;
//            } while (two_halfs++ < 1);
//        }
//
//        return buf.toString();
//    }
//
//    public static byte[] hexToBytes(char[] hex) {
//        int length = hex.length / 2;
//        byte[] raw = new byte[length];
//        for (int i = 0; i < length; i++) {
//            int high = Character.digit(hex[i * 2], 16);
//            int low = Character.digit(hex[i * 2 + 1], 16);
//            int value = (high << 4) | low;
//            if (value > 127) {
//                value -= 256;
//            }
//            raw[i] = (byte) value;
//        }
//        return raw;
//    }
//
//    public static byte[] hexToBytes(String hex) {
//        return hexToBytes(hex.toCharArray());
//    }
//
//    /**
//     * create a message signature by using private key
//     *
//     * @param data
//     * @param strPrivateKey
//     * @return
//     */
//    public static String createMsgSignature(String data, String strPrivateKey) {
//        String encryptData = "";
//        try {
//            PrivateKey privateKey = getPrivateKeyFromString(strPrivateKey);
//            java.security.Signature s = java.security.Signature
//                    .getInstance("SHA1withRSA");
//            s.initSign(privateKey);
//            s.update(data.getBytes());
//            byte[] signature = s.sign();
//            // Encrypt data
//            encryptData = new String(Base64.encode(signature));
//        } catch (Exception e) {
//            log.error("createMsgSignature", e);
//        }
//        return encryptData;
//    }
//
//    /**
//     * decrypt a message signature by using private key
//     *
//     * @param encodeText
//     * @param data
//     * @param strPrivateKey
//     * @return
//     */
//    public static boolean verifyMsgSignature(String encodeText,
//            String strPublicKey, String input) {
//
//        try {
//            PublicKey publicKey = getPublicKeyFromString(strPublicKey);
//            // decode base64
//            byte[] base64Bytes = Base64.decode(encodeText);
//            java.security.Signature sig = java.security.Signature
//                    .getInstance("SHA1WithRSA");
//            sig.initVerify(publicKey);
//            sig.update(input.getBytes());
//
//            return sig.verify(base64Bytes);
//        } catch (Exception e) {
//            log.error("verifyMsgSignature", e);
//        }
//        return false;
//    }
//
//    /**
//     * create a private key from an encode string
//     *
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static PrivateKey getPrivateKeyFromString(String key)
//            throws Exception {
//        PrivateKey privateKey = null;
//        try {
//            PEMReader reader = new PEMReader(new StringReader(key), null,
//                    "SunRsaSign");
//            KeyPair pemPair = (KeyPair) reader.readObject();
//
//            reader.close();
//
//            privateKey = (PrivateKey) pemPair.getPrivate();
//        } catch (Exception e) {
//        }
//        return privateKey;
//
//    }
//
//    /**
//     * create a public key from an encode string
//     *
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static PublicKey getPublicKeyFromString(String key) throws Exception {
//        PublicKey publicKey = null;
//        try {
//            PEMReader reader = new PEMReader(new StringReader(key), null,
//                    "SunRsaSign");
//            publicKey = (PublicKey) reader.readObject();
//
//            reader.close();
//
//        } catch (Exception e) {
//            log.error("getPublicKeyFromString", e);
//        }
//        return publicKey;
//    }
//
//    public static String encodeString(String data, String sample) {
//
//        StringBuilder res = new StringBuilder();
//
//        if (data.length() > sample.length()) {
//            for (int i = 0; i < sample.length(); i++) {
//                res.append(sample.charAt(i));
//                res.append(data.charAt(i));
//            }
//            res.append(data.substring(sample.length()));
//        } else {
//            for (int i = 0; i < data.length(); i++) {
//                res.append(sample.charAt(i));
//                res.append(data.charAt(i));
//            }
//            res.append(sample.substring(data.length()));
//        }
//
//        return new String(Base64.encode(res.toString().getBytes()));
//    }
//
//    public static String decodeString(String encode, int padd) {
//
//        String res = new String(Base64.decode(encode));
//        char[] item = new char[res.length() - padd];
//        int j = 0;
//        if (padd * 2 < res.length()) {
//            // sample < data
//            for (int i = 0; i < padd * 2; i++) {
//                if (i % 2 == 1) {
//                    item[j++] = res.charAt(i);
//                }
//            }
//            for (int i = padd * 2; i < res.length(); i++) {
//                item[j++] = res.charAt(i);
//            }
//        } else {
//            log.info("padd = " + padd + " & len = " + res.length());
//            // sample > data
//            for (int i = 0; i < (res.length() - padd) * 2; i++) {
//                if (i % 2 == 1) {
//                    item[j++] = res.charAt(i);
//                }
//            }
//        }
//
//        return new String(item);
//    }
//
//    public static String getKeyFile(String filePath) {
//        File file = new File(filePath);
//        StringBuilder contents = new StringBuilder();
//        BufferedReader reader = null;
//
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String text = null;
//
//            // repeat until all lines is read
//            while ((text = reader.readLine()) != null) {
//                contents.append(text).append(
//                        System.getProperty("line.separator"));
//            }
//        } catch (IOException e) {
//            log.error("getKeyFile", e);
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                log.error("getKeyFile", e);
//            }
//        }
//
//        return contents.toString();
//    }
//
//    public static String AESKeyGen() throws NoSuchAlgorithmException {
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//            keyGen.init(128, new SecureRandom());
//            SecretKey secretKey = keyGen.generateKey();
//            return EncryptManager.byteToHex(secretKey.getEncoded());
//        } catch (NoSuchAlgorithmException noSuchAlgo) {
//            log.error(noSuchAlgo.getMessage());
//        }
//        return null;
//    }
//
//    public static String encryptAES(String data, String key) throws Exception {
//        String dataEncrypted = new String();
//        try {
//            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            byte[] raw = hexToBytes(key);
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//            byte[] byteDataToEncrypt = data.getBytes();
//            byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
//            dataEncrypted = new BASE64Encoder().encode(byteCipherText);
//            return dataEncrypted;
//        } catch (Exception ex) {
//            log.error(ex.getMessage());
//        }
//        return dataEncrypted;
//    }
//
//    public static String decryptAES(String dataEncrypt, String key)
//            throws Exception {
//        String dataDecrypted = new String();
//        try {
//            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            byte[] raw = hexToBytes(key);
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            byte[] decordedValue = new BASE64Decoder()
//                    .decodeBuffer(dataEncrypt);
//            aesCipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] byteDecryptedText = aesCipher.doFinal(decordedValue);
//            dataDecrypted = new String(byteDecryptedText);
//            return dataDecrypted;
//        } catch (Exception ex) {
//            log.error(ex.getMessage());
//        }
//        return dataDecrypted;
//    }
//
//    private static final Logger log = LoggerFactory.getLogger(EncryptManager.class);
}
