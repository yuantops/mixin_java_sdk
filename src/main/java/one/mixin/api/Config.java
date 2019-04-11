package one.mixin.api;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

public class Config {
    public Config() {
    }

    // 修改为你自己的 APP_ID
    public String APP_ID = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 SECRET
    public String SECRET = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 PIN
    public String PIN = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 SESSION_ID
    public String SESSION_ID = "";
    // 修改为在 developers.mixin.one/dashboard 上获取到的 TOKEN
    public String TOKEN = "";
    // 修改为你自己（即 APP 作者）的 Mixin 账号的 UUID
    public String ADMIN_ID = "";
    // 修改为你自己的ras_private_key内容。注意，一定要保留换行格式.
    public String RSA_KEY_CONTENT = "";

    public RSAPrivateKey RSA_PRIVATE_KEY = loadPrivateKey();

    public byte[] PAY_KEY = MixinUtil.decrypt(RSA_PRIVATE_KEY, TOKEN, SESSION_ID);

    private RSAPrivateKey loadPrivateKey() {
        try {
            PrivateKey key = new PrivateKeyReader().getPrivateKey(RSA_KEY_CONTENT);
            return (RSAPrivateKey) key;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
