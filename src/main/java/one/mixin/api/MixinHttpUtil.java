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

    public static String requestWithAuth(String path, String httpMethod, String body, Config config) throws IOException {
        String authToken = AuthUtil
            .signAuthenticationToken(config.APP_ID, config.SESSION_ID, config.RSA_PRIVATE_KEY, httpMethod.toUpperCase(),
                path, body);
        switch (httpMethod.toUpperCase()) {
            case "GET":
                return getWithAuthToken(Constants.NETWORK_BASE_URL + path, authToken);
            case "POST":
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", JSON.toString());
                headers.put("Authorization", "Bearer " + authToken);
                return post(Constants.NETWORK_BASE_URL + path, headers, body);
            default:
                return "";
        }
    }

    private static String getWithAuthToken(String url, String authToken) throws IOException {
        Request request = new Request.Builder().url(url).addHeader("Content-Type", JSON.toString())
            .addHeader("Authorization", "Bearer " + authToken).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return response.body().string();
    }

    public static void main(String[] args) {
    }

}
