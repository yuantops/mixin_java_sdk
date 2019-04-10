package one.mixin.api.network.model;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = -8299996920417716464L;

    public Address() {
    }

    String AddressId;
    String AssetId;
    String Label;
    String PublicKey;
    String AccountName;
    String AccountTag;
    String Fee;
    String Reserve;
    String UpdatedAt;

    public String getAddressId() {
        return AddressId;
    }

    public void setAddressId(String addressId) {
        AddressId = addressId;
    }

    public String getAssetId() {
        return AssetId;
    }

    public void setAssetId(String assetId) {
        AssetId = assetId;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
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

    public String getFee() {
        return Fee;
    }

    public void setFee(String fee) {
        Fee = fee;
    }

    public String getReserve() {
        return Reserve;
    }

    public void setReserve(String reserve) {
        Reserve = reserve;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Address{" + "AddressId='" + AddressId + '\'' + ", AssetId='" + AssetId + '\'' + ", Label='" + Label
            + '\'' + ", PublicKey='" + PublicKey + '\'' + ", AccountName='" + AccountName + '\'' + ", AccountTag='"
            + AccountTag + '\'' + ", Fee='" + Fee + '\'' + ", Reserve='" + Reserve + '\'' + ", UpdatedAt='" + UpdatedAt
            + '\'' + '}';
    }
}
