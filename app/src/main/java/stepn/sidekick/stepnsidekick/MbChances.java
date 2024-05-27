package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

@Keep
public class MbChances {
    @SerializedName("luck")
    private final Map<String, Integer> luck;

    @SerializedName("probabilities")
    private final int[][] probabilities;

    public MbChances(Map<String, Integer> luck, int[][] probabilities) {
        this.luck = luck;
        this.probabilities = probabilities;
    }

    public Map<String, Integer> getLuck() {
        return this.luck;
    }

    public int[][] getProbabilities() {
        return this.probabilities;
    }

}
