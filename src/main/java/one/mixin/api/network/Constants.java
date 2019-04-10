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

    /**
     * URI(without hostname) for verifying a transfer
     */
    public static String PAYMENTS_PATH = "/payments";

    /**
     * URI(without hostname) for reading transfer
     */
    public static String READ_TRANSFER_PATH = "/transfers/trace/";

    /**
     * URI(without hostname) for reading top assets of Mixin Network
     */
    public static String TOP_ASSETS = "/network/assets/top";

    /**
     * URI(without hostname) for reading public asset information
     */
    public static String NETWORK_ASSETS = "/network/assets/";

    /**
     * URI(without hostname) for network snapshots
     */
    public static String NETWORK_SNAPSHOT = "/network/snapshots";

    /**
     * URI(without hostname) for external transactions
     */
    public static String EXT_TRANSACTIONS = "/external/transactions";

    /**
     * URI(without hostname) for searching assets
     */
    public static String NET_ASSET_SEARCH = "/network/assets/search/";

    /**
     * URI(without hostname) for creating users
     */
    public static String CREATE_USERS = "/users";
}
