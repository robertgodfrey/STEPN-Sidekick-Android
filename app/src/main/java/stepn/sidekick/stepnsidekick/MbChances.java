package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;

@Keep
public class MbChances {
    @SerializedName("luck")
    private final int[] luck;

    @SerializedName("probabilities")
    private final int[][] probabilities;

    public MbChances(int[] luck, int[][] probabilities) {
        this.luck = luck;
        this.probabilities = probabilities;
    }

    public int[] getLuck() {
        return this.luck;
    }

    public int[][] getProbabilities() {
        return this.probabilities;
    }

}
