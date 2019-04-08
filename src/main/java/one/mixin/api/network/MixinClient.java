package one.mixin.api.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import one.mixin.api.AuthUtil;
import one.mixin.api.Config;
import one.mixin.api.MixinHttpUtil;
import one.mixin.api.network.model.Asset;
import one.mixin.api.network.model.NetworkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.http.AuthenticationInfo;

import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static one.mixin.api.MixinHttpUtil.requestWithDefaultAuth;

public class MixinClient {
    private static Logger LOGGER = LoggerFactory.getLogger(MixinClient.class);

    private static GsonBuilder GSON_BUILDER = new GsonBuilder();

    static {
        GSON_BUILDER.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        GSON_BUILDER.disableHtmlEscaping();
    }

    /**
     * PIN is used to manage user’s addresses, assets and etc.
     * There’s no default PIN for a Mixin Network user (except APP).
     *
     * @param oldPin old pin
     * @param newPin new pin
     */
    public void createPin(String oldPin, String newPin) {

    }

    /**
     * Verify PIN if is valid or not. For example, you can verify PIN before updating it.
     * @param pin
     * @param pinToken
     * @param sessionId
     * @param privateKey
     * @return
     */
    public Object verifyPin(String pin, String pinToken, String sessionId, RSAPrivateKey privateKey) {

        String encryptedPin = AuthUtil.encryptPin(pin, Config.TOKEN, Config.SESSION_ID, Config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("pin", encryptedPin);

        String result = "";
        try {
            result = requestWithDefaultAuth(Constants.VERIFY_PIN_PATH, "POST", GSON_BUILDER.create().toJson(bodyMap));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * Read asset by assetId
     *
     * @param assetId asset_id
     * @return null if exception occurred
     */
    public Asset readAsset(String assetId) {
        String result = "";
        try {
            result = requestWithDefaultAuth(Constants.READ_ASSET_PATH + assetId, "GET","");
        } catch (Exception e) {
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return assetList != null && assetList.size() > 0 ? assetList.get(0) : null;
    }

    /**
     * Read user’s all assets
     *
     * @return null if exception occurred.
     */
    public List<Asset> readAssets() {
        String result;
        try {
            result = requestWithDefaultAuth(Constants.READ_ASSETS_PATH, "GET", "");
        } catch (Exception e) {
            LOGGER.error("MixinClient#readAssets() {}", e.toString());
            return null;
        }
        return parseResponse(result, Asset.class);
    }

    private static <T> List<T> parseResponse(String json, Class<T> clazz) {
        Gson gson = GSON_BUILDER.create();
        NetworkResponse response = gson.fromJson(json, NetworkResponse.class);

        List<T> listOfT = new ArrayList<T>();
        JsonElement data = response.getData();
        if (data instanceof JsonArray) {
            TypeToken typeToken = new TypeToken<List<JsonObject>>() {
            };
            Type type = typeToken.getType();
            List<JsonObject> arrayOfT = gson.fromJson(data, type);
            for (JsonObject obj : arrayOfT) {
                listOfT.add(gson.fromJson(obj, clazz));
            }
        } else if (data instanceof JsonObject) {
            listOfT.add(gson.fromJson(data, clazz));
        }

        return listOfT;
    }
}
