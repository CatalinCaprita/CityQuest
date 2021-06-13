package caprita.catalin.cityquest.ui.api.quest;

import caprita.catalin.cityquest.ui.models.quest.QuestModel;
import caprita.catalin.cityquest.ui.models.quest.UserQuestResponseModel;
import caprita.catalin.cityquest.ui.models.quest.UserQuestResultModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface QuestApi {
//    Fetch all the quests completed by the user
    @GET("/api/quests/{id}")
    Observable<Response<QuestModel>> getQuestById(@Path("id") Long id);
    @POST("/api/quests/{id}")
    Observable<Response<UserQuestResultModel>> submitUserQuest(@Path("id")Long id,
                                                               @Body UserQuestResponseModel resp);
}
