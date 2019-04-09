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
import one.mixin.api.network.model.Address;
import one.mixin.api.network.model.Asset;
import one.mixin.api.network.model.NetworkResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.http.AuthenticationInfo;

import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static one.mixin.api.MixinHttpUtil.requestWithAuth;

public class MixinClient {
    private static Logger LOGGER = LoggerFactory.getLogger(MixinClient.class);

    private static GsonBuilder GSON_BUILDER = new GsonBuilder();

    static {
        GSON_BUILDER.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        GSON_BUILDER.disableHtmlEscaping();
    }

    private Config config = new Config();

    /**
     * PIN is used to manage user’s addresses, assets and etc.
     * There’s no default PIN for a Mixin Network user (except APP).
     *
     * @param oldPin "" or old pin (unencrypted)
     * @param newPin new pin (unencrtyped)
     */
    public String createPin(String oldPin, String newPin) {
        String oldEncryptedPin = StringUtils.isAllEmpty(oldPin) ? "" : AuthUtil
            .encryptPin(oldPin, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());
        String newEncryptedPin = AuthUtil
            .encryptPin(newPin, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("old_pin", oldEncryptedPin);
        bodyMap.put("pin", newEncryptedPin);

        String result = "";
        try {
            result = requestWithAuth(Constants.CREATE_PIN_PATH, "POST", GSON_BUILDER.create().toJson(bodyMap), config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#createPin() error: {}", e);
            return null;
        }
        return result;
    }

    /**
     * Verify PIN if is valid or not. For example, you can verify PIN before updating it.
     *
     * @param pin
     * @param pinToken
     * @param sessionId
     * @param privateKey
     * @return
     */
    public String verifyPin(String pin, String pinToken, String sessionId, RSAPrivateKey privateKey) {

        String encryptedPin = AuthUtil
            .encryptPin(pin, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("pin", encryptedPin);

        String result = "";
        try {
            result = requestWithAuth(Constants.VERIFY_PIN_PATH, "POST", GSON_BUILDER.create().toJson(bodyMap), config);
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * Gant an asset’s deposit address, usually it is public_key, but account_name and account_tag is used for EOS. The api same as Read Asset.
     * @param assetId
     * @return
     */
    public Asset deposit(String assetId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.READ_ASSET_PATH + assetId, "GET", "", config);
        } catch (Exception e) {
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return assetList != null && assetList.size() > 0 ? assetList.get(0) : null;
    }

    /**
     * Get assets out of Mixin Network, neet to create an address for withdrawal.
     * @param addressId String: UUID
     * @param amount String: e.g. “100000”
     * @param memo TEXT: less than 140 charactars
     * @param traceId String: UUID
     * @return
     */
    public String withdrawal(String addressId, String amount, String memo, String traceId) {
        String encryptedPin = AuthUtil
            .encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> data = new HashMap<>();
        data.put("amount", amount);
        data.put("address_id", addressId);
        data.put("memo", memo);
        data.put("pin", encryptedPin);
        data.put("trace_id", traceId);

        String result = "";
        try {
            result = requestWithAuth(Constants.WITHDRAWAL_PATH, "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("transfer error:{}", e);
            return "";
        }
        return result;
    }

    /**
     * Create an address for withdrawal, you can only withdraw through an existent address.
     *
     * @param address
     * @return null if error occurs
     */
    public Address createAddress(Address address) {
        String encryptedPin = AuthUtil
            .encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> data = new HashMap<>();
        data.put("asset_id", address.getAssetId());
        if (!StringUtils.isEmpty(address.getLabel()) ) {
            data.put("label", address.getLabel());
        }
        if (!StringUtils.isEmpty(address.getPublicKey())) {
            data.put("public_key",address.getPublicKey());
        }
        if (!StringUtils.isEmpty(address.getAccountName())) {
            data.put("account_name", address.getAccountName());
        }
        if (!StringUtils.isEmpty(address.getAccountTag())) {
            data.put("account_tag", address.getAccountTag());
        }
        data.put("pin",encryptedPin);

        String result = "";
        try {
            result = requestWithAuth(Constants.ADDRESSES_PATH, "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("transfer error:{}", e);
            return null;
        }
        List<Address> addresses = parseResponse(result, Address.class);

        return (addresses!= null && addresses.size() > 0) ? addresses.get(0) : null;
    }

    /**
     * Delete an address by ID.
     * @param addressId
     */
    public void deleteAddress(String addressId) {
        String encryptedPin = AuthUtil
            .encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> data = new HashMap<>();
        data.put("pin", encryptedPin);

        try {
            requestWithAuth(Constants.ADDRESSES_PATH + "/" + addressId + "/delete", "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("transfer error:{}", e);
        }
    }

    /**
     * Read an address by ID.
     * @param addressId
     * @return
     */
    public Address readAddress(String addressId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.ADDRESSES_PATH + addressId, "GET", null, config);
        } catch (Exception e) {
            LOGGER.error("read address error:{}", e);
            return null;
        }
        List<Address> addresses = parseResponse(result, Address.class);

        return (addresses!= null && addresses.size() > 0) ? addresses.get(0) : null;
    }

    /**
     * Read addresses by asset ID.
     * @param assetId
     * @return
     */
    public List<Address> withdrawlAddresses(String assetId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.READ_ASSET_PATH +assetId+ Constants.ADDRESSES_PATH, "GET", null, config);
        } catch (Exception e) {
            LOGGER.error("read withdrawal addresses error:{}", e);
            return null;
        }
        List<Address> addresses = parseResponse(result, Address.class);
        return addresses;
    }


    /**
     * Transfer of assets between Mixin Network users.
     * @param opponentId
     * @param assetId
     * @param amount
     * @param memo
     * @param traceId
     * @return
     */
    public String transfer(String opponentId, String assetId, String amount, String memo, String traceId) {

        String encryptedPin = AuthUtil
            .encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY, System.currentTimeMillis());

        Map<String, String> data = new HashMap<>();
        data.put("amount", amount);
        data.put("asset_id", assetId);
        data.put("opponent_id", opponentId);
        data.put("memo", memo);
        data.put("pin", encryptedPin);
        data.put("trace_id", traceId);

        String result = "";
        try {
            result = requestWithAuth(Constants.TRANSFER_PATH, "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("transfer error:{}", e);
            return "";
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
            result = requestWithAuth(Constants.READ_ASSET_PATH + assetId, "GET", "", config);
        } catch (Exception e) {
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return (assetList != null && assetList.size() > 0) ? assetList.get(0) : null;
    }

    /**
     * Read user’s all assets
     *
     * @return null if exception occurred.
     */
    public List<Asset> readAssets() {
        String result;
        try {
            result = requestWithAuth(Constants.READ_ASSETS_PATH, "GET", "", config);
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
