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
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Authentication Utility
 */
public class AuthUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtil.class);

    private AuthUtil() {
    }

    /**
     * Create JWT signature
     *
     * @param uid    : ClientId or Bear User Id
     * @param sid    : SessionId or Bear User SessionId
     * @param secret : privateKey
     * @param method : HTTP Request method, e.g.: GET, POST
     * @param uri    : URL path without hostname, e.g.: /transfers
     * @param body   : HTTP Request body, e.g.: {"pin": "encrypted pin token"}
     * @return
     */
    public static String signAuthenticationToken(String uid, String sid, RSAPrivateKey secret, String method,
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
     * Translated from go-sdk version.
     *
     * @param pin
     * @param pinToken
     * @param privateKey
     * @param iterator
     * @return
     */
    public static String encryptPin(String pin, String pinToken, String sessionId, RSAPrivateKey privateKey,
        long iterator) {
        byte[] keyBytes = MixinUtil.decrypt(privateKey, pinToken, sessionId);

        byte[] pinBytes = pin.getBytes();
        byte[] timeBytes =
            ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(System.currentTimeMillis() / 1000).array();
        byte[] iteratorBytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(iterator).array();

        byte[] paddedBytes = new byte[6 + 8 + 8 + 10];
        int toPadCount = 10;
        Arrays.fill(paddedBytes, (byte) toPadCount);
        copyOfRange(pinBytes, paddedBytes, 0);
        copyOfRange(timeBytes, paddedBytes, 6);
        copyOfRange(iteratorBytes, paddedBytes, 6 + 8);

        byte[] ivv = new byte[16];
        new Random().nextBytes(ivv);

        IvParameterSpec ivSpec = new IvParameterSpec(ivv);
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
        try {
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
            byte[] encrypted = aesCipher.doFinal(paddedBytes);
            return Base64.encodeBase64String(ArrayUtils.addAll(ivv, encrypted));
        } catch (Exception e) {
            LOGGER.error("AuthUtil#encryptPin() {}", e.toString());
            return "";
        }
    }

    private static void copyOfRange(byte[] from, byte[] to, int index) {
        for (int i = 0; i < from.length; i++) {
            to[index + i] = from[i];
        }
    }
}
