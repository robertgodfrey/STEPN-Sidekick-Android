package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class GemPrices {
    @SerializedName("data")
    private final NestedGemPrice[] data;

    public GemPrices(NestedGemPrice[] data) {
        this.data = data;
    }

    public int getPrice() {
        return data[2].getPrice();
    }

}
