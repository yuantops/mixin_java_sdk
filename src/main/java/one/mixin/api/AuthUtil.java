package one.mixin.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Authentication Utility
 */
public class AuthUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtil.class);

    private AuthUtil() {
    }

    /**
     * @param uid    : ClientId or Bear User Id
     * @param sid    : SessionId or Bear User SessionId
     * @param secret : privateKey
     * @param method : HTTP Request method, e.g.: GET, POST
     * @param uri    : URL path without hostname, e.g.: /transfers
     * @param body   : HTTP Request body, e.g.: {"pin": "encrypted pin token"}
     * @return
     */
    public static String SignAuthenticationToken(String uid, String sid, RSAPrivateKey secret, String method,
        String uri, String body) {

        long now = System.currentTimeMillis() / 1000;
        long expire = now + 60 * 60 * 24 * 30 * 3;

        String token = "";
        String signature = DigestUtils.sha256Hex(method + uri + body);
        try {
            Algorithm algorithm = Algorithm.RSA512(null, secret);
            token = JWT.create().withClaim("uid", uid).withClaim("sid", sid).withClaim("iat", new Date(now * 1000))
                .withClaim("exp", new Date(expire * 1000)).withClaim("jti", UUID.randomUUID().toString())
                .withClaim("sig", signature).sign(algorithm);
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            LOGGER.error("AuthUtil#signAuthenticationToken() {}", exception.toString());
            return "";
        }

        return token;
    }

    /**
     * Before you withdraw or tranfer in Mixin Network. You must have an Encrypted PIN.
     *
     * @param pin
     * @param pinToken
     * @param privateKey
     * @param iterator
     * @return
     */
    public static String EncryptPin(String pin, String pinToken, String sessionId, RSAPrivateKey privateKey, int iterator) {
        // decrypt pinToken to obtain aes key
        byte[] keyBytes = null;
        try {
            Cipher decryptionCipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            OAEPParameterSpec oaepParameterSpec =
                new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, new PSource.PSpecified(sessionId.getBytes()));
            decryptionCipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParameterSpec);
            keyBytes = decryptionCipher.doFinal(Base64.decodeBase64(pinToken));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("rsa-oaep decrypt error:{}", e.toString());
            return "";
        }

        // aes cipher init
        Cipher aesCipher = null;
        byte[] pinBytes = pin.getBytes();
        try {
            aesCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            byte[] iv = new byte[aesCipher.getBlockSize()];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        } catch (Exception e) {
            LOGGER.error("aes cipher init error:{}", e.toString());
            return "";
        }

        // encrypt pin with aes cipher
        int now = (int) System.currentTimeMillis() / 1000;
        byte[] timeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(now).array();
        pinBytes = ArrayUtils.addAll(pinBytes, timeBytes);
        byte[] iteratorBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(iterator).array();
        pinBytes = ArrayUtils.addAll(pinBytes, iteratorBytes);

        int toPadCount = aesCipher.getBlockSize() - (pinBytes.length) % aesCipher.getBlockSize();
        byte[] paddingBytes = new byte[toPadCount];
        for (int i = 0; i < toPadCount; i++) {
            paddingBytes[i] = (byte) toPadCount;
        }
        pinBytes = ArrayUtils.addAll(pinBytes, paddingBytes);

        try {
            byte[] encrypted = aesCipher.doFinal(pinBytes);
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
