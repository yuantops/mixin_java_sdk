package one.mixin.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
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
            token = JWT.create()
                .withClaim("uid", uid)
                .withClaim("sid", sid)
                .withClaim("iat", new Date(now * 1000))
                .withClaim("exp", new Date(expire * 1000))
                .withClaim("jti", UUID.randomUUID().toString())
                .withClaim("sig", signature)
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            LOGGER.error("AuthUtil#signAuthenticationToken() {}", exception.toString());
            return "";
        }

        return token;
    }

}
