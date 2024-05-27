package stepn.sidekick.stepnsidekick;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SidekickApi {
    @GET("mb/{energy}")
    Call<MbChances> getMbChances(@Path("energy") double energy);

    @GET("gmt/numbers")
    Call<GmtMagicNumbers> getGmtNumbers();
}