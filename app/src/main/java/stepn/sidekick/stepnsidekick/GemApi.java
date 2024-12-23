package stepn.sidekick.stepnsidekick;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GemApi {
    @GET("run/orderlist?saleId=1&order=2001&refresh=true&page=0&otd=&type=501&gType=3&bread=0")
    Call<GemPrices> getGems(@Query("chain") int chain, @Query("level") int level);
}