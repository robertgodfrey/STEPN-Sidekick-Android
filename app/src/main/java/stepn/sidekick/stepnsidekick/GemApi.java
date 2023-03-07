package stepn.sidekick.stepnsidekick;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GemApi {
    @GET("run/orderlist?saleId=1&order=2001&refresh=true&page=0&otd=&type=501&gType=3&quality=&level=2010&bread=0")
    Call<GemPrices> getLevelOne(@Query("chain") int chain);

    @GET("run/orderlist?saleId=1&order=2001&refresh=true&page=0&otd=&type=501&gType=3&quality=&level=3010&bread=0")
    Call<GemPrices> getLevelTwo(@Query("chain") int chain);

    @GET("run/orderlist?saleId=1&order=2001&refresh=true&page=0&otd=&type=501&gType=3&quality=&level=4010&bread=0")
    Call<GemPrices> getLevelThree(@Query("chain") int chain);
}