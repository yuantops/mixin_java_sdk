package one.mixin.api;

import org.junit.Test;

public class AuthUtilTest {
    @Test
    public void testEncryptPin() {
        String encryptedPin = AuthUtil.EncryptPin(Config.PIN, Config.TOKEN, Config.SESSION_ID, Config.RSA_PRIVATE_KEY, 1);
        System.out.println(encryptedPin);
    }
}
