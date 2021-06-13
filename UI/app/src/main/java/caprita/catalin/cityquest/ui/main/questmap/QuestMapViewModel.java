package caprita.catalin.cityquest.ui.main.questmap;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.ResponseResourceConverter;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import caprita.catalin.cityquest.ui.api.user.UserApi;
import caprita.catalin.cityquest.ui.api.user.UserQuestsListingDto;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.enums.QuestStatus;
import caprita.catalin.cityquest.ui.util.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class QuestMapViewModel extends ViewModel {

    private static final String TAG = "QuestMapViewModel";
    private final MutableLiveData<Resource<List<QuestBriefModel>>> pendingQuestsData = new MutableLiveData<>();
    private final SessionManager sessionManager;
    private final UserApi userApi;

    @Inject
    public QuestMapViewModel(SessionManager sessionManager,
                             UserApi userApi) {
        this.sessionManager = sessionManager;
        this.userApi = userApi;
    }

    public LiveData<Resource<List<QuestBriefModel>>> observeRemainingQuests(){
        return this.pendingQuestsData;
    }

    public LiveData<Resource<QuestBriefModel>> observeQuestById(Long id){
        if(this.pendingQuestsData.getValue() != null){
            if(this.pendingQuestsData.getValue().data != null){
                QuestBriefModel model = this.pendingQuestsData.getValue().data
                        .stream()
                        .filter(questBriefModel -> questBriefModel.getId().equals(id))
                        .findFirst().orElse(null);
                MutableLiveData<Resource<QuestBriefModel>> singleQuestLiveData = new MutableLiveData<>();
                switch (pendingQuestsData.getValue().status){
                    case ERROR:
                        singleQuestLiveData.setValue(
                                Resource.error(this.pendingQuestsData.getValue().message, model));
                        break;
                    case SUCCESS:
                        singleQuestLiveData.setValue(Resource.success(model));
                    default:
                        singleQuestLiveData.setValue(Resource.loading(model));
                }
                return singleQuestLiveData;
            }
            return new MutableLiveData<>(
                    Resource.error("Could not locate quest with id" + id.toString(),
                    null));
        }
        return new MutableLiveData<>(
                Resource.error("No Quests Remaining." + id.toString(),
                        null));
    }
    public LiveData<AuthResource<LoginResponseDto>> observerLoginStatus(){
        return this.sessionManager.observeLoginResponse();
    }
    public void queryRemainingQuests(){
        Disposable disp = userApi.getUserQuestsById(QuestStatus.REMAINING.name())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleQuestsOnNext,
                        this::handleQuestsOnError);

    }


    private void handleQuestsOnError(Throwable throwable) {
        Log.d(TAG, "handleQuestsOnError: " + throwable.getMessage());
        Resource<List<QuestBriefModel>> res = ResponseResourceConverter.buildResourceFromError(throwable);
        this.pendingQuestsData.setValue(res);
    }

    private void handleQuestsOnNext(Response<UserQuestsListingDto> response) {
        try{
            Resource<UserQuestsListingDto> res = ResponseResourceConverter.buildResourceFromResponse(response);
                if(res.status == Resource.Status.SUCCESS){
//                    Will always return only a single map if status == QuestStatus.REMAINING
                    if(res.data != null) {
                        Log.d(TAG, "Quests Querying Successful. Poreccsing " + res.data.getQuestsByDay().size());
                        res.data.getQuestsByDay().get(0).forEach(model ->{
                            try {
                                Class <?> c = R.drawable.class;
                                Field idField = c.getDeclaredField("quest_" + model.getId());
                                model.setResourceId(idField.getInt(idField));
                            }
                            catch (Exception e) {
                                model.setResourceId(R.drawable.ic_baseline_error_outline_24);
                                Log.w("MyTag", "Failure to get drawable id. " + e.getMessage());
                            }
                        });
                        this.pendingQuestsData.setValue(
                                Resource.success(res.data.getQuestsByDay().get(0)));
                    }else{
                        Log.w(TAG, "Remaining Quests Data is null!");
                        this.pendingQuestsData.setValue(
                                Resource.error( Constants.Error.EMPTY_LIST,
                                        Collections.emptyList(),
                                        Constants.Error.CODE_NULL_DATA));
                    }
                }else{
                    this.pendingQuestsData.setValue(Resource.error(
                            res.message,
                            this.getCurrentOrNull(),
                            res.code));
                }

            } catch (IOException e) {
                e.printStackTrace();
                this.pendingQuestsData.setValue(Resource.error(
                    e.getMessage(),
                    this.getCurrentOrNull()));
            }

    }

    private List<QuestBriefModel> getCurrentOrNull() {
        if(this.pendingQuestsData.getValue() != null)
            return this.pendingQuestsData.getValue().data;
        return null;
    }
}
