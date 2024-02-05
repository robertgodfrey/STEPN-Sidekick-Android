package stepn.sidekick.stepnsidekick;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SidekickApi {
    @GET("mb")
    Call<MbChances> getMbChances(
            @Header("API-Key") String apiKey,
            @Query("energy") double energy,
            @Query("luck") double luck
    );

    @GET("gmt")
    Call<GmtMagicNumbers> getGmtNumbers(@Header("API-Key") String apiKey);
}