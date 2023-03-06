package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class GemPrices {
    @SerializedName("prices")
    private final int[] prices;

    public GemPrices(int[] prices) {
        this.prices = prices;
    }

    public int getLevelOnePrice() {
        return prices[0];
    }

    public int getLevelTwoPrice() {
        return prices[1];
    }

    public int getLevelThreePrice() {
        return prices[2];
    }

}
