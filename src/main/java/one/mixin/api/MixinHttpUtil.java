package one.mixin.api;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import one.mixin.api.network.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MixinHttpUtil {

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    public static String post(String url, HashMap<String, String> headers, String body) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).post(RequestBody.create(JSON, body));
        if (headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    public static String requestWithDefaultAuth(String path) throws IOException {
        String authToken =
            AuthUtil.SignAuthenticationToken(Config.APP_ID, Config.SESSION_ID, Config.RSA_PRIVATE_KEY, "GET", path, "");
        return requestWithAuth(Constants.NETWORK_BASE_URL + path, authToken);
    }

    public static String requestWithAuth(String url, String authToken) throws IOException {
        Request request = new Request.Builder().url(url).addHeader("Content-Type", JSON.toString())
            .addHeader("Authorization", "Bearer " + authToken).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    public static void main(String[] args) {
        try {
            String assetId = "43d61dcd-e413-450d-80b8-101d5e903357";
            String result = requestWithDefaultAuth(String.format(Constants.READ_ASSETS_PATH, assetId));
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

}
