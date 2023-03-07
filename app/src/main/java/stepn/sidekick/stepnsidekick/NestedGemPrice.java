package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class NestedGemPrice {
    @SerializedName("sellPrice")
    private final int gemPrice;

    public int getPrice() {
        return gemPrice;
    }

    public NestedGemPrice(int gemPrice) {
        this.gemPrice = gemPrice;
    }
}
