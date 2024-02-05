package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class MbChances {
    @SerializedName("predictions")
    private final int[] predictions;

    public MbChances(int[] predictions) {
        this.predictions = predictions;
    }

    public int[] getPredictions() {
        return this.predictions;
    }
}
