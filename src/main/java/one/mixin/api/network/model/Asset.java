package one.mixin.api.network.model;

import java.io.Serializable;

public class Asset implements Serializable {
    private static final long serialVersionUID = 4159723888293757053L;

    private String Type;

    private String AssetId;

    private String ChainId;

    private String Symbol;

    private String Name;

    private String IconUrl;

    private String Balance;

    private String PublicKey;

    private String AccountName;

    private String AccountTag;

    private String PriceBtc;

    private String PriceUsd;

    private String ChangeBtc;

    private String ChangeUsd;

    private String AssetKey;

    private Long Confirmations;

    private Double Capitalization;

    public Asset() {
    }
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getAssetId() {
        return AssetId;
    }

    public void setAssetId(String assetId) {
        AssetId = assetId;
    }

    public String getChainId() {
        return ChainId;
    }

    public void setChainId(String chainId) {
        ChainId = chainId;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getPublicKey() {
        return PublicKey;
    }

    public void setPublicKey(String publicKey) {
        PublicKey = publicKey;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getAccountTag() {
        return AccountTag;
    }

    public void setAccountTag(String accountTag) {
        AccountTag = accountTag;
    }

    public String getPriceBtc() {
        return PriceBtc;
    }

    public void setPriceBtc(String priceBtc) {
        PriceBtc = priceBtc;
    }

    public String getPriceUsd() {
        return PriceUsd;
    }

    public void setPriceUsd(String priceUsd) {
        PriceUsd = priceUsd;
    }

    public String getChangeBtc() {
        return ChangeBtc;
    }

    public void setChangeBtc(String changeBtc) {
        ChangeBtc = changeBtc;
    }

    public String getChangeUsd() {
        return ChangeUsd;
    }

    public void setChangeUsd(String changeUsd) {
        ChangeUsd = changeUsd;
    }

    public String getAssetKey() {
        return AssetKey;
    }

    public void setAssetKey(String assetKey) {
        AssetKey = assetKey;
    }

    public Long getConfirmations() {
        return Confirmations;
    }

    public void setConfirmations(Long confirmations) {
        Confirmations = confirmations;
    }

    public Double getCapitalization() {
        return Capitalization;
    }

    public void setCapitalization(Double capitalization) {
        Capitalization = capitalization;
    }

    @Override
    public String toString() {
        return "Asset{" + "Type='" + Type + '\'' + ", AssetId='" + AssetId + '\'' + ", ChainId='" + ChainId + '\''
            + ", Symbol='" + Symbol + '\'' + ", Name='" + Name + '\'' + ", IconUrl='" + IconUrl + '\'' + ", Balance='"
            + Balance + '\'' + ", PublicKey='" + PublicKey + '\'' + ", AccountName='" + AccountName + '\''
            + ", AccountTag='" + AccountTag + '\'' + ", PriceBtc='" + PriceBtc + '\'' + ", PriceUsd='" + PriceUsd + '\''
            + ", ChangeBtc='" + ChangeBtc + '\'' + ", ChangeUsd='" + ChangeUsd + '\'' + ", AssetKey='" + AssetKey + '\''
            + ", Confirmations=" + Confirmations + ", Capitalization=" + Capitalization + '}';
    }
}
