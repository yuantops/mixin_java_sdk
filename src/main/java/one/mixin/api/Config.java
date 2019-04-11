package one.mixin.api;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

public class Config {
    public Config() {
    }

    // 修改为你自己的 APP_ID
    public static final String APP_ID = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 SECRET
    public static final String SECRET = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 PIN
    public static final String PIN = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 SESSION_ID
    public static final String SESSION_ID = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 TOKEN
    public static final String TOKEN = "";
    // 修改为你自己（即 APP 作者）的 Mixin 账号的 UUID
    public static final String ADMIN_ID = "";

    private static RSAPrivateKey loadPrivateKey() {
        try {
            PrivateKey key =
                new PrivateKeyReader(Loader.getResource("rsa_private_key.txt").openStream())
                    .getPrivateKey();
            return (RSAPrivateKey) key;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static final RSAPrivateKey RSA_PRIVATE_KEY = loadPrivateKey();

    public static final byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, TOKEN, SESSION_ID);
}
