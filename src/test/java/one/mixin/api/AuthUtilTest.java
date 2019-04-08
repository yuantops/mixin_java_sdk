package one.mixin.api;

import one.mixin.api.network.Constants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class AuthUtilTest {
    @Test
    public void testEncryptPin() {
        String encryptedPin =
            AuthUtil.EncryptPin(Config.PIN, Config.TOKEN, Config.SESSION_ID, Config.RSA_PRIVATE_KEY, 1);
        System.out.println(encryptedPin);
    }

    @Test
    public void testRequestWithDefaultAuth() {
        String result = "";
        try {
            result = MixinHttpUtil.requestWithDefaultAuth(Constants.READ_ASSETS_PATH, "GET", "");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(StringUtils.isNotEmpty(result));
    }
}
