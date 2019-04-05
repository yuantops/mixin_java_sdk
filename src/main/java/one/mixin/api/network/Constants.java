package one.mixin.api.network;

public class Constants {
    private Constants() {
    }

    /**
     * Base url
     */
    public static String NETWORK_BASE_URL = "https://api.mixin.one";
    /**
     * URI(without hostname) for reading user's all assets
     */
    public static String READ_ASSETS_PATH = "/assets";

    /**
     * URI(without hostname) for reading asset by asset_id
     */
    public static String READ_ASSET_PATH = "/assets/%s";
}
