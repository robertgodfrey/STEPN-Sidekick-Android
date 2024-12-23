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
    @SerializedName("polygon-ecosystem-token")
    private final NestedTokenPrice polygon;
    @SerializedName("green-satoshi-token-on-pol")
    private final NestedTokenPrice gstPol;


    public TokenPrices(NestedTokenPrice gmt, NestedTokenPrice solana, NestedTokenPrice gstSol, NestedTokenPrice binancecoin,
                       NestedTokenPrice gstBsc, NestedTokenPrice polygon, NestedTokenPrice gstPol) {
        this.gmt = gmt;
        this.solana = solana;
        this.gstSol = gstSol;
        this.binancecoin = binancecoin;
        this.gstBsc = gstBsc;
        this.polygon = polygon;
        this.gstPol = gstPol;
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

    public double getPolygon() {
        return polygon.getPrice();
    }

    public double getGstPol() {
        return gstPol.getPrice();
    }

}
