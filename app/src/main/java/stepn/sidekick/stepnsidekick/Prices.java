package stepn.sidekick.stepnsidekick;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Prices {
    private NestedPrice solana;
    @SerializedName("green-satoshi-token")
    private NestedPrice gstSol;
    private NestedPrice binancecoin;
    @SerializedName("green-satoshi-token-bsc")
    private NestedPrice gstBsc;
    private NestedPrice ethereum;
    @SerializedName("green-satoshi-token-on-eth")
    private NestedPrice gstEth;


    public Prices(NestedPrice solana, NestedPrice gstSol, NestedPrice binancecoin, NestedPrice gstBsc, NestedPrice ethereum, NestedPrice gstEth) {
        this.solana = solana;
        this.gstSol = gstSol;
        this.binancecoin = binancecoin;
        this.gstBsc = gstBsc;
        this.ethereum = ethereum;
        this.gstEth = gstEth;
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
