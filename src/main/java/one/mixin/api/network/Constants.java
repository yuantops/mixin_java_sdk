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
    public static String READ_ASSET_PATH = "/assets/";

    /**
     * URI(without hostname) for creating/updating pin
     */
    public static String CREATE_PIN_PATH = "/pin/update";

    /**
     * URI(without hostname) for verifying pin
     */
    public static String VERIFY_PIN_PATH = "/pin/verify";

    /**
     * URI(without hostname) for transferring
     */
    public static String TRANSFER_PATH = "/transfers";

    /**
     * URI(without hostname) for withdrawal
     */
    public static String WITHDRAWAL_PATH = "/withdrawals";

    /**
     * URI(without hostname) for creating address
     */
    public static String ADDRESSES_PATH = "/addresses";

}
