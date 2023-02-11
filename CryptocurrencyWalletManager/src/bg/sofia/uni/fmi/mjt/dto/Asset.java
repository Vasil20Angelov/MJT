package bg.sofia.uni.fmi.mjt.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Asset {

    private static final int CRYPTO_CODE = 1;

    @SerializedName("asset_id")
    private String id;
    private String name;
    @SerializedName("price_usd")
    private double price;
    @SerializedName("type_is_crypto")
    private int assetType;

    public Asset(String id, String name, double price, int assetType) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.assetType = assetType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isCrypto() {
        return assetType == CRYPTO_CODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Double.compare(asset.price, price) == 0 && assetType == asset.assetType && Objects.equals(id, asset.id) && Objects.equals(name, asset.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, assetType);
    }
}
