package one.mixin.api.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import one.mixin.api.network.model.Asset;
import one.mixin.api.network.model.NetworkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static one.mixin.api.MixinHttpUtil.requestWithDefaultAuth;

public class MixinClient {
    private static Logger LOGGER = LoggerFactory.getLogger(MixinClient.class);

    private static GsonBuilder GSON_BUILDER= new GsonBuilder();
    static {
        GSON_BUILDER.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    }

    /**
     * Read asset by assetId
     * @param assetId asset_id
     * @return null if exception occurred
     */
    public static Asset readAsset(String assetId) {
        String result = "";
        try {
            result = requestWithDefaultAuth(String.format(Constants.READ_ASSET_PATH, assetId));
        } catch (Exception e) {
        }
        List<Asset> assetList = parseResponse(result, Asset.class);
        return assetList != null && assetList.size() > 0 ? assetList.get(0) : null;
    }

    /**
     * Read user’s all assets
     * @return null if exception occurred.
     */
    public static List<Asset> readAssets() {
        String result;
        try {
            result = requestWithDefaultAuth(Constants.READ_ASSETS_PATH);
        } catch (Exception e) {
            LOGGER.error("MixinClient#readAssets() {}", e.toString());
            return null;
        }
        return parseResponse(result, Asset.class);
    }

    private static <T> List<T> parseResponse(String json, Class<T> clazz) {
        Gson gson = GSON_BUILDER.create();
        NetworkResponse response= gson.fromJson(json, NetworkResponse.class);

        List<T> listOfT = new ArrayList<T>();
        JsonElement data = response.getData();
        if (data instanceof JsonArray) {
            TypeToken typeToken = new TypeToken<List<JsonObject>>() {};
            Type type = typeToken.getType();
            List<JsonObject> arrayOfT = gson.fromJson(data, type);
            for (JsonObject obj: arrayOfT) {
                listOfT.add(gson.fromJson(obj, clazz));
            }
        } else if (data instanceof JsonObject) {
            listOfT.add(gson.fromJson(data, clazz));
        }

        return listOfT;
    }

    public static void main(String[] args) {
        try {
            Long time1 = System.currentTimeMillis();
//            List<Asset> assets = readAssets();
            Long time2 = System.currentTimeMillis();
//            System.out.println(assets);

            System.out.println(time2 - time1);

            String assetId = "a2c5d22b-62a2-4c13-b3f0-013290dbac60";
            Asset asset = readAsset(assetId);
            System.out.println(asset);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

}
