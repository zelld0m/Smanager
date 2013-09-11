package com.search.manager.utility;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public final class CryptUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(CryptUtil.class);
    private static final String ALGO = "AES";
    private static final String ALGOMODE = "AES/ECB/PKCS5Padding";
    private static final String PRIVATEKEY = "$0G2#y3M%7*P5s0@";
    /**
     * this chars must be 16 only *
     */
    private static Key SUSI = null;
    private static Key SUSI2 = null;

    static {
        try {
            SUSI = new SecretKeySpec(PRIVATEKEY.getBytes(), ALGO);
            SUSI2 = KeyGenerator.getInstance(ALGO).generateKey();
        } catch (Exception e) {
            logger.error("error at CryptUtil class", e);
        }
    }

    private CryptUtil() {
        /**
         * * nothing here **
         */
    }

    /**
     * Provides simple and static encryption
     *
     * @param plainText
     * @return encrypted String
     */
    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGOMODE);
            cipher.init(Cipher.ENCRYPT_MODE, SUSI);

            byte[] inputBytes = plainText.getBytes("UTF8");
            byte[] outputBytes = cipher.doFinal(inputBytes);

            /**
             * encode to base64 so that it will be safe for web *
             */
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(outputBytes);

            return URLEncoder.encode(base64, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decryption for the simple Encryption
     *
     * @param encrytedText
     * @return decrypted String
     */
    public static String decrypt(String encrytedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGOMODE);
            cipher.init(Cipher.DECRYPT_MODE, SUSI);

            /**
             * decode base64 prior decipher *
             */
            BASE64Decoder decoder = new BASE64Decoder();

            byte[] inputBytes = decoder.decodeBuffer(URLDecoder.decode(encrytedText, "UTF-8"));
            byte[] outputBytes = cipher.doFinal(inputBytes);

            String result = new String(outputBytes, "UTF8");

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates hash string using provided userinfo
     *
     * @param id
     * @param userName
     * @param userPassword
     * @param extraInfo
     * @return hash string
     */
    public static String hashUserInfo(String id, String userName, String userPassword, String extraInfo) {
        return StringUtil.getHashString(PRIVATEKEY, id + userName + userPassword + extraInfo);
    }

    /**
     * Generates hash string using provided userinfo
     *
     * @param id
     * @param userName
     * @param userPassword
     * @param extraInfo
     * @return hash string
     */
    public static String hashUserId(String id, String extraInfo) {
        return StringUtil.getHashString(PRIVATEKEY, id + extraInfo);
    }

    /**
     * Validates hash string against hash string generated using the userinfo
     *
     * @param hash
     * @param id
     * @param userName
     * @param userPassword
     * @param extraInfo
     * @return true if hashStr == hash(id + userName + userPassword + extraInfo)
     */
    public static boolean isHashedUserInfoValid(String hash, String id, String userName, String userPassword, String extraInfo) {
        String hash1 = StringUtil.getHashString(PRIVATEKEY, id + userName + userPassword + extraInfo);
        return hash1.equals(hash);
    }

    /**
     * Generates random/dynamic encrypted text
     *
     * @param plainText
     * @return encrypted String
     */
    public static String strongEncrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGOMODE);
            cipher.init(Cipher.ENCRYPT_MODE, SUSI2);

            byte[] inputBytes = plainText.getBytes("UTF8");
            byte[] outputBytes = cipher.doFinal(inputBytes);

            /**
             * encode to base64 so that it will be safe for web *
             */
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(outputBytes);

            return base64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decryption for the Strong Encryption
     *
     * @param encrytedText
     * @return decrypted String
     */
    public static String strongDecrypt(String encrytedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGOMODE);
            cipher.init(Cipher.DECRYPT_MODE, SUSI2);

            /**
             * decode base64 prior decipher *
             */
            BASE64Decoder decoder = new BASE64Decoder();

            byte[] inputBytes = decoder.decodeBuffer(encrytedText);
            byte[] outputBytes = cipher.doFinal(inputBytes);

            String result = new String(outputBytes, "UTF8");

            return result;
        } catch (Exception e) {
            logger.error("Error at CryptUtil.strongDecrypt()", e);
            return null;
        }
    }

    /**
     * For testing only
     *
     * @param args
     */
    public static void main(String[] args) {
        String plain = "The quick brown fox jumps over the lazy dog. 12345678910. !@#$%^&*():;,></'=|{}";
        String enc = encrypt(plain);
        logger.info(String.format("plaintext: %s", plain));
        logger.info(String.format("encrypted: %s", enc));
        logger.info(String.format("decrypted: %s", decrypt(enc)));
    }

    public static String encodeBase64(byte[] baseByte) {
        // Convert a byte array to base64 string
        String encoded = new sun.misc.BASE64Encoder().encode(baseByte);
        return encoded;
    }

    public static String decodeBase64(String encoded) throws IOException {
        // Convert base64 string to a byte array	
        String decoded = "";
        byte[] baseByte = null;
        baseByte = new sun.misc.BASE64Decoder().decodeBuffer(encoded);
        decoded = new String(baseByte);
        return decoded;
    }
}
