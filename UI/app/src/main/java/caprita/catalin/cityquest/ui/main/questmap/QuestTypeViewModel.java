package caprita.catalin.cityquest.ui.main.questmap;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.api.ResponseResourceConverter;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import caprita.catalin.cityquest.ui.api.quest.QuestApi;
import caprita.catalin.cityquest.ui.api.user.UserApi;
import caprita.catalin.cityquest.ui.models.quest.QuestModel;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.quest.SubtaskModel;
import caprita.catalin.cityquest.ui.models.quest.UserQuestResponseModel;
import caprita.catalin.cityquest.ui.models.quest.UserQuestResultModel;
import caprita.catalin.cityquest.ui.models.quest.UserSubtaskResponseModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class QuestTypeViewModel extends ViewModel {

    private static final String TAG = "QuestTypeViewModel";
    private final SessionManager sessionManager;
    private final QuestApi questApi;
    private final UserApi userApi;
    private final MutableLiveData<Resource<QuestModel>> questData = new MutableLiveData<>();
    private final MutableLiveData<Resource<UserQuestResultModel>> questResultData = new MutableLiveData<>();
    private final List<UserSubtaskResponseModel> userSubtaskResponses = new ArrayList<>();
    private final UserQuestResponseModel userQuestResponse = new UserQuestResponseModel();

    @Inject
    public QuestTypeViewModel(SessionManager sessionManager,
                              QuestApi questApi,
                              UserApi userApi) {
        this.sessionManager = sessionManager;
        this.questApi = questApi;
        this.userApi = userApi;
    }

    public LiveData<Resource<QuestModel>> observeQuest(){
        return this.questData;
    }
    public LiveData<Resource<UserQuestResultModel>> observeQuestResult(){return this.questResultData;}

    public LiveData<Resource<SubtaskModel>> observeSubtaskByIndex(int index){
        MutableLiveData<Resource<SubtaskModel>> subtaskLiveData = new MutableLiveData();
        if(this.questData.getValue() == null || this.questData.getValue().data == null)
            subtaskLiveData.setValue(Resource.success(null));
        else{
            try {
                subtaskLiveData.setValue(
                        Resource.success(this.questData.getValue().data.getSubtasks().get(index))
                );
            }catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
                subtaskLiveData.setValue(Resource.error(ex.getMessage(),null));
            }
        }
        return subtaskLiveData;
    }
    public void queryQuestDetails(Long questId){
        this.questData.setValue(Resource.loading(null));
        Disposable disp = questApi.getQuestById(questId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestResponse, this::onQuestError);
    }

    public void completeSubtaskWithAnswer(int subtaskIndex, Long possibleAnswerId) throws IndexOutOfBoundsException{
        if(this.questData.getValue() != null && this.questData.getValue().data != null){
            try {
                UserSubtaskResponseModel st = this.userSubtaskResponses.get(subtaskIndex);
                st.setUserAnswerId(possibleAnswerId != null ? possibleAnswerId : -1L);
            }catch (IndexOutOfBoundsException ex){
                throw ex;
            }
        }
    }

    public void completeSubtaskWithAnswer(int subtaskIndex, Long possibleAnswerId, String userAnswerContent) throws IndexOutOfBoundsException{
        if(this.questData.getValue() != null && this.questData.getValue().data != null){
            try {
                UserSubtaskResponseModel st = this.userSubtaskResponses.get(subtaskIndex);
                st.setUserAnswerId(possibleAnswerId != null ? possibleAnswerId : -1L);
                st.setUserAnswerValue(userAnswerContent);
            }catch (IndexOutOfBoundsException ex){
                throw ex;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void submitUserAnswer(){
        this.userQuestResponse.setResponses(this.userSubtaskResponses);
        this.userQuestResponse.setCompletionDate(LocalDate.now().toString());
        Disposable disp = questApi
                .submitUserQuest(this.userQuestResponse.getQuestId(), this.userQuestResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUserSubmitResponse, this::onUserSubmitError);
    }

    private void onUserSubmitResponse(Response<UserQuestResultModel> resultResponse)  {
        try {
            Resource<UserQuestResultModel> res = ResponseResourceConverter.buildResourceFromResponse(resultResponse);
            questResultData.setValue(res);
        } catch (IOException e) {
            questResultData.setValue(Resource.error(e.getMessage(),null));
            e.printStackTrace();
        }

    }

    private void onUserSubmitError(Throwable throwable) {
        Resource<UserQuestResultModel> res = ResponseResourceConverter.buildResourceFromError(throwable);
        questResultData.setValue(res);
    }


    private void onQuestError(Throwable throwable) {
        Log.e(TAG, "onQuestError: " + throwable.getMessage());
        throwable.printStackTrace();
        Resource<QuestModel> res = ResponseResourceConverter.buildResourceFromError(throwable);
        this.questData.setValue(res);
    }
    private void onQuestResponse(Response<QuestModel> questModelResponse) {
        try {
            Resource<QuestModel> res = ResponseResourceConverter.buildResourceFromResponse(questModelResponse);
            if(res.status == Resource.Status.SUCCESS){

                res.data.getSubtasks().forEach(subtaskModel -> {
                    UserSubtaskResponseModel resp = new UserSubtaskResponseModel();
                    resp.setSubtaskId(subtaskModel.getId());
                    resp.setUserAnswerId(-1L);
//                    Set the actually correct answer so that we avoid countless checking
                    userSubtaskResponses.add(resp);
                });
                this.userQuestResponse.setQuestId(res.data.getId());
                this.questData.setValue(res);
            }
        } catch (IOException e) {
            this.questData.setValue(Resource.error(e.getMessage(), null));
            e.printStackTrace();
        }
    }
}
