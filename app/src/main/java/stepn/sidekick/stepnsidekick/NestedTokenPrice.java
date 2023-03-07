package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class NestedTokenPrice {
    @SerializedName("usd")
    private final double usd;

    public double getPrice() {
        return usd;
    }

    public NestedTokenPrice(double usd) {
        this.usd = usd;
    }
}
