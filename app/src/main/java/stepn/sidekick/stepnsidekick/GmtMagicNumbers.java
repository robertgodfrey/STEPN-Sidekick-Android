package stepn.sidekick.stepnsidekick;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class GmtMagicNumbers {
    @SerializedName("a")
    private final double a;
    @SerializedName("b")
    private final double b;
    @SerializedName("c")
    private final double c;

    public GmtMagicNumbers(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double getA() {
        return this.a;
    }

    public double getB() {
        return this.b;
    }

    public double getC() {
        return this.c;
    }
}
