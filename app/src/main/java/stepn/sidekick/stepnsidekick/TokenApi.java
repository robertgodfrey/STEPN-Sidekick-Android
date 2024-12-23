package stepn.sidekick.stepnsidekick;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TokenApi {
    @GET("api/v3/simple/price?ids=stepn%2Csolana%2Cgreen-satoshi-token%2Cbinancecoin%2Cgreen-satoshi-token-bsc%2Cpolygon-ecosystem-token%2Cgreen-satoshi-token-on-pol&vs_currencies=usd")
    Call<TokenPrices> getPosts();
}