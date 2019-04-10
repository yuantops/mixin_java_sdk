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
import one.mixin.api.network.model.Address;
import one.mixin.api.network.model.Asset;
import one.mixin.api.network.model.NetworkResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String oldEncryptedPin = StringUtils.isAllEmpty(oldPin) ?
            "" :
            AuthUtil.encryptPin(oldPin, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY,
                System.currentTimeMillis());
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
            LOGGER.error("MixinClient#verifyPin() error: {}", e);
        }
        return result;
    }

    /**
     * Gant an asset’s deposit address, usually it is public_key, but account_name and account_tag is used for EOS. The api same as Read Asset.
     *
     * @param assetId
     * @return
     */
    public Asset deposit(String assetId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.READ_ASSET_PATH + assetId, "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#deposit() error: {}", e);
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return assetList != null && assetList.size() > 0 ? assetList.get(0) : null;
    }

    /**
     * Get assets out of Mixin Network, need to create an address for withdrawal.
     *
     * @param addressId String: UUID
     * @param amount    String: e.g. “100000”
     * @param memo      TEXT: less than 140 charactars
     * @param traceId   String: UUID
     * @return
     */
    public String withdrawal(String addressId, String amount, String memo, String traceId) {
        String encryptedPin = AuthUtil.encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY,
            System.currentTimeMillis());

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
            LOGGER.error("MixinClient#withdrawal() error: {}", e);
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
        String encryptedPin = AuthUtil.encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY,
            System.currentTimeMillis());

        Map<String, String> data = new HashMap<>();
        data.put("asset_id", address.getAssetId());
        if (!StringUtils.isEmpty(address.getLabel())) {
            data.put("label", address.getLabel());
        }
        if (!StringUtils.isEmpty(address.getPublicKey())) {
            data.put("public_key", address.getPublicKey());
        }
        if (!StringUtils.isEmpty(address.getAccountName())) {
            data.put("account_name", address.getAccountName());
        }
        if (!StringUtils.isEmpty(address.getAccountTag())) {
            data.put("account_tag", address.getAccountTag());
        }
        data.put("pin", encryptedPin);

        String result = "";
        try {
            result = requestWithAuth(Constants.ADDRESSES_PATH, "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#createAddress() error: {}", e);
        }
        List<Address> addresses = parseResponse(result, Address.class);

        return (addresses != null && addresses.size() > 0) ? addresses.get(0) : null;
    }

    /**
     * Delete an address by ID.
     *
     * @param addressId
     */
    public void deleteAddress(String addressId) {
        String encryptedPin = AuthUtil.encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY,
            System.currentTimeMillis());

        Map<String, String> data = new HashMap<>();
        data.put("pin", encryptedPin);

        try {
            requestWithAuth(Constants.ADDRESSES_PATH + "/" + addressId + "/delete", "POST",
                GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#deleteAddress() error: {}", e);
        }
    }

    /**
     * Read an address by ID.
     *
     * @param addressId
     * @return
     */
    public Address readAddress(String addressId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.ADDRESSES_PATH + addressId, "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#readAddress() error: {}", e);
            return null;
        }
        List<Address> addresses = parseResponse(result, Address.class);

        return (addresses != null && addresses.size() > 0) ? addresses.get(0) : null;
    }

    /**
     * Read addresses by asset ID.
     *
     * @param assetId
     * @return
     */
    public List<Address> withdrawalAddresses(String assetId) {
        String result = "";
        try {
            result =
                requestWithAuth(Constants.READ_ASSET_PATH + assetId + Constants.ADDRESSES_PATH, "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#withdrawalAddresses() error: {}", e);
            return null;
        }
        List<Address> addresses = parseResponse(result, Address.class);
        return addresses;
    }

    /**
     * Transfer of assets between Mixin Network users.
     *
     * @param opponentId
     * @param assetId
     * @param amount
     * @param memo
     * @param traceId
     * @return
     */
    public String transfer(String opponentId, String assetId, String amount, String memo, String traceId) {

        String encryptedPin = AuthUtil.encryptPin(config.PIN, config.TOKEN, config.SESSION_ID, config.RSA_PRIVATE_KEY,
            System.currentTimeMillis());

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
            LOGGER.error("MixinClient#transfer() error: {}", e);
        }
        return result;
    }

    /**
     * Read transfer by trace trace_id.
     *
     * @param traceId
     * @return
     */
    public String readTransfer(String traceId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.READ_TRANSFER_PATH + traceId, "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#readTransfer() error: {}", e);
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
            LOGGER.error("MixinClient#readAsset() error: {}", e);
            return null;
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return (assetList != null && assetList.size() > 0) ? assetList.get(0) : null;
    }

    /**
     * Verify a transfer, payment status if it is paid or pending.
     *
     * @param assetId
     * @param opponentId
     * @param amount
     * @param traceId
     * @return
     */
    public String verifyPayment(String opponentId, String assetId, String amount, String traceId) {
        Map<String, String> data = new HashMap<>();
        data.put("asset_id", assetId);
        data.put("opponent_id", opponentId);
        data.put("amount", amount);
        data.put("trace_id", traceId);

        String result = "";
        try {
            result = requestWithAuth(Constants.PAYMENTS_PATH, "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#verifyPayment() error: {}", e);
        }
        return result;
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
            LOGGER.error("MixinClient#readAssets() error {}", e.toString());
            return null;
        }
        return parseResponse(result, Asset.class);
    }

    /**
     * Read top valuable assets of Mixin Network.
     *
     * @return
     */
    public List<Asset> topAssets() {
        String result;
        try {
            result = MixinHttpUtil.get(Constants.NETWORK_BASE_URL + Constants.TOP_ASSETS);
        } catch (Exception e) {
            LOGGER.error("MixinClient#topAssets() error {}", e.toString());
            return null;
        }
        return parseResponse(result, Asset.class);
    }

    /**
     * Read public asset information by asset_id from Mixin Network.
     *
     * @param assetId asset_id
     * @return null if exception occurred
     */
    public Asset networkAsset(String assetId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.NETWORK_ASSETS + assetId, "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#networkAssets() error {}", e.toString());
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return (assetList != null && assetList.size() > 0) ? assetList.get(0) : null;
    }

    /**
     * Read public snapshots of Mixin Network
     *
     * @param limit   Integer: Max 500
     * @param offset  String: format RFC3339Nano, UTC or non UTC time
     * @param assetId UUID: OPTION, return all network snapshots or specific asset snapshots.
     * @param order   string: OPTION, ASC or DESC. DEFAULT DESC
     * @return
     */
    public String networkSnapshots(int limit, String offset, String assetId, String order) {
        String result = "";
        StringBuffer path = new StringBuffer(Constants.NETWORK_SNAPSHOT);
        path.append("?limit=").append(limit);
        path.append("&offset=").append(offset);
        if (!StringUtils.isEmpty(assetId)) {
            path.append("&asset=").append(assetId);
        }
        if (!StringUtils.isEmpty(order)) {
            path.append("&order=").append(order);
        }
        try {
            result = requestWithAuth(path.toString(), "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#networkSnapshots() error,{}", e);
        }
        return result;
    }

    /**
     * Read public snapshots of Mixin Network by snapshot_id.
     *
     * @param snapshotId
     * @return
     */
    public String networkSnapshot(String snapshotId) {
        String result = "";
        try {
            result = requestWithAuth(Constants.NETWORK_SNAPSHOT + "/" + snapshotId, "GET", "", config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#networkSnapshots() error,{}", e);
        }
        return result;
    }

    /**
     * Read external transactions (pending deposits) by public_key and asset_id, use account_tag for EOS.
     *
     * @param assetId     String: UUID (optional)
     * @param publicKey   String: except EOS (optional)
     * @param accountTag  String: only for EOS (optional)
     * @param accountName String: only for EOS (optional)
     * @param limit       Integer: Max 500 (optional)
     * @param offset      String: format RFC3339Nano, e.g.: 2006-01-02T15:04:05.999999999Z (optional)
     * @return
     */
    public String externalTransactions(String assetId, String publicKey, String accountTag, String accountName,
        Integer limit, String offset) {
        List<String> params = new ArrayList<>();
        if (StringUtils.isNotEmpty(assetId)) {
            params.add("asset=" + assetId);
        }
        if (StringUtils.isNotEmpty(publicKey)) {
            params.add("public_key=" + publicKey);
        }
        if (StringUtils.isNotEmpty(accountTag)) {
            params.add("account_tag=" + accountTag);
        }
        if (StringUtils.isNotEmpty(accountName)) {
            params.add("account_name=" + accountName);
        }
        if (limit != null && limit >= 0) {
            params.add("limit=" + limit);
        }
        if (StringUtils.isNotEmpty(offset)) {
            params.add("offset=" + offset);
        }
        String paramStr = params.stream().collect(Collectors.joining("&"));
        String path =
            StringUtils.isEmpty(paramStr) ? Constants.EXT_TRANSACTIONS : Constants.EXT_TRANSACTIONS + "?" + paramStr;

        String result = "";
        try {
            result = MixinHttpUtil.get(Constants.NETWORK_BASE_URL + path);
        } catch (Exception e) {
            LOGGER.error("MixinClient#externalTransactions() error,{}", e);
        }
        return result;
    }

    /**
     * Search assets by symbol or name, only popular assets will be list.
     *
     * @param symbol
     * @return
     */
    public List<Asset> searchAssets(String symbol) {
        String result = "";
        try {
            result = MixinHttpUtil.get(Constants.NETWORK_BASE_URL + Constants.NET_ASSET_SEARCH);
        } catch (Exception e) {
            LOGGER.error("MixinClient#searchAssets() error,{}", e);
            return null;
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return assetList;
    }

    /**
     * Create a new Mixin Network user (like a normal Mixin Messenger user).
     *
     * @param fullName
     * @param sessionSecret
     * @return
     */
    public String createUser(String fullName, String sessionSecret) {
        Map<String, String> data = new HashMap<>();
        data.put("full_name", fullName);
        data.put("session_secret", sessionSecret);

        String result = "";
        try {
            result = requestWithAuth(Constants.CREATE_USERS, "POST", GSON_BUILDER.create().toJson(data), config);
        } catch (Exception e) {
            LOGGER.error("MixinClient#createUser() error,{}", e);
        }
        return result;
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
