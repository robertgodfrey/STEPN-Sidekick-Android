package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class GemPrices {
    @SerializedName("1")
    private final int levelOneGem;
    @SerializedName("2")
    private final int levelTwoGem;
    @SerializedName("3")
    private final int levelThreeGem;


    public GemPrices(int levelOneGem, int levelTwoGem, int levelThreeGem) {
        this.levelOneGem = levelOneGem;
        this.levelTwoGem = levelTwoGem;
        this.levelThreeGem = levelThreeGem;
    }

    public int getLevelOnePrice() {
        return levelOneGem;
    }

    public int getLevelTwoPrice() {
        return levelTwoGem;
    }

    public int getLevelThreePrice() {
        return levelThreeGem;
    }

}
