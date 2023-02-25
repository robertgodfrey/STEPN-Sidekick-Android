package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class TokenPrices {
    @SerializedName("stepn")
    private final NestedPrice gmt;
    @SerializedName("solana")
    private final NestedPrice solana;
    @SerializedName("green-satoshi-token")
    private final NestedPrice gstSol;
    @SerializedName("binancecoin")
    private final NestedPrice binancecoin;
    @SerializedName("green-satoshi-token-bsc")
    private final NestedPrice gstBsc;
    @SerializedName("ethereum")
    private final NestedPrice ethereum;
    @SerializedName("green-satoshi-token-on-eth")
    private final NestedPrice gstEth;


    public TokenPrices(NestedPrice gmt, NestedPrice solana, NestedPrice gstSol, NestedPrice binancecoin,
                       NestedPrice gstBsc, NestedPrice ethereum, NestedPrice gstEth) {
        this.gmt = gmt;
        this.solana = solana;
        this.gstSol = gstSol;
        this.binancecoin = binancecoin;
        this.gstBsc = gstBsc;
        this.ethereum = ethereum;
        this.gstEth = gstEth;
    }

    public double getGmtPrice() {
        return gmt.getPrice();
    }

    public double getSolanaPrice() {
        return solana.getPrice();
    }

    public double getGstSol() {
        return gstSol.getPrice();
    }

    public double getBinancecoin() {
        return binancecoin.getPrice();
    }

    public double getGstBsc() {
        return gstBsc.getPrice();
    }

    public double getEthereum() {
        return ethereum.getPrice();
    }

    public double getGstEth() {
        return gstEth.getPrice();
    }

}
