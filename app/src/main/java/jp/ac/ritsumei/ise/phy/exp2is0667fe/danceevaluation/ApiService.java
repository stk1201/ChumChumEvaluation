package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @POST("results/sort")
    Call<List<HistoryApiResponseItem>> sortResults(@Body HistoryRequestBody body);
}
