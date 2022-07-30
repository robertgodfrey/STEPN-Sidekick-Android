package stepn.sidekick.stepnsidekick;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GeckoAPI {
    @GET("posts")
    Call<List> getPosts();
}