package stepn.sidekick.stepnsidekick;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GemApi {
    @GET(" ")
    Call<GemPrices> getPosts();
}