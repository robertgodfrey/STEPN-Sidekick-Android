package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class TokenPrices {
    @SerializedName("stepn")
    private final NestedTokenPrice gmt;
    @SerializedName("solana")
    private final NestedTokenPrice solana;
    @SerializedName("green-satoshi-token")
    private final NestedTokenPrice gstSol;
    @SerializedName("binancecoin")
    private final NestedTokenPrice binancecoin;
    @SerializedName("green-satoshi-token-bsc")
    private final NestedTokenPrice gstBsc;
    @SerializedName("ethereum")
    private final NestedTokenPrice ethereum;
    @SerializedName("green-satoshi-token-on-eth")
    private final NestedTokenPrice gstEth;


    public TokenPrices(NestedTokenPrice gmt, NestedTokenPrice solana, NestedTokenPrice gstSol, NestedTokenPrice binancecoin,
                       NestedTokenPrice gstBsc, NestedTokenPrice ethereum, NestedTokenPrice gstEth) {
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
