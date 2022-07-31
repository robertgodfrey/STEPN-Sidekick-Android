package stepn.sidekick.stepnsidekick;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("api/v3/simple/price?ids=solana%2Cgreen-satoshi-token%2Cbinancecoin%2Cgreen-satoshi-token-bsc%2Cethereum%2Cgreen-satoshi-token-on-eth&vs_currencies=usd")
    Call<Prices> getPosts();
}