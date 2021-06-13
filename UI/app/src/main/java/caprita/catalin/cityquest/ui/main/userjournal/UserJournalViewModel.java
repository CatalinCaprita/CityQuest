package caprita.catalin.cityquest.ui.main.userjournal;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.api.quest.QuestApi;
import caprita.catalin.cityquest.ui.api.user.UserApi;
import caprita.catalin.cityquest.ui.api.user.UserQuestsListingDto;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserCompanion;
import caprita.catalin.cityquest.ui.models.UserModel;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import caprita.catalin.cityquest.ui.models.enums.QuestStatus;
import caprita.catalin.cityquest.ui.util.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;


public class UserJournalViewModel extends ViewModel {

    private static final String TAG = UserJournalViewModel.class.getSimpleName();
    private final SessionManager sessionManager;
    private final MediatorLiveData<Resource<List<UserCompanion>>> userCompanions = new MediatorLiveData<>();
    private final List<MutableLiveData<Resource<List<QuestBriefModel>>>> questsByDay =
            new ArrayList<>();

    private final UserApi userApi;
    private final QuestApi questApi;

    @Inject
    public UserJournalViewModel(UserApi userApi,
                                QuestApi questApi,
                                SessionManager sessionManager) {
        this.userApi = userApi;
        this.questApi = questApi;
        this.sessionManager = sessionManager;
        userCompanions.removeSource(sessionManager.observeUserModel());
        userCompanions.addSource(sessionManager.observeUserModel(), new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> resource) {
                if (resource != null) {
                    Log.d(TAG, "onChanged: Setting up new user companions resource");
                    switch (resource.status) {
                        case ERROR:
                            if (resource.data != null)
                                userCompanions.setValue(Resource.error(resource.message, resource.data.getCompanions()));
                            else
                                userCompanions.setValue(Resource.error(resource.message, Collections.emptyList()));
                            break;
                        case SUCCESS:
                            if (resource.data != null)
                                userCompanions.setValue(Resource.success(resource.data.getCompanions()));
                            else
                                userCompanions.setValue(Resource.success(Collections.emptyList()));
                            break;
                        case LOADING:
                            if (userCompanions.getValue() != null)
                                userCompanions.setValue(Resource.loading(userCompanions.getValue().data));
                            else
                                userCompanions.setValue(Resource.loading(Collections.emptyList()));
                            break;
                    }
                }
            }
        });
        for(int i=0 ; i< 3; i++)
            questsByDay.add(new MutableLiveData<>());

        sessionManager.observeLoginResponse().observeForever(new Observer<AuthResource<LoginResponseDto>>() {
            @Override
            public void onChanged(AuthResource<LoginResponseDto> authResource) {
                switch (authResource.status){
                    case ERROR:
                    case NOT_AUTHENTICATED:
                        clearUserData();
                        break;

                }
            }
        });
    }

    private void clearUserData() {
        Log.d(TAG, "clearUserData: User Logged Out. Clearing Chached Data.");
        questsByDay.clear();
        questsByDay.forEach(resourceMutableLiveData -> {
            if(resourceMutableLiveData.getValue() != null
                    && resourceMutableLiveData.getValue().data != null){
                resourceMutableLiveData.getValue().data.clear();
            }
        });
        if(userCompanions.getValue() != null &&  userCompanions.getValue().data != null)
            userCompanions.getValue().data.clear();
    }

    public LiveData<AuthResource<LoginResponseDto>> observerAuthResource() {
        return sessionManager.observeLoginResponse();
    }

    public LiveData<Resource<List<UserCompanion>>> observerUserCompanions() {
        return userCompanions;
    }

    public LiveData<Resource<UserModel>> observerUserModelResource() {
        return this.sessionManager.observeUserModel();
    }

    public LiveData<Resource<List<QuestBriefModel>>> observeQuestForDay(int index){
        return this.questsByDay.get(index);
    }

    public void queryUserDetails() {
        final LiveData<Resource<UserModel>> source = LiveDataReactiveStreams.fromPublisher(
                userApi.getUserDetails()
                        .onErrorReturn(this::manageErrorFromRequest)
                        .map(this::buildResourceFromResponse)
                        .subscribeOn(Schedulers.io())
        );
        sessionManager.updateUserModel(source);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    public void queryUserQuests() {

        userApi.getUserQuestsById(QuestStatus.FINISHED.name())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Resource<UserQuestsListingDto> resource = buildResourceFromResponse(response);
                            switch (resource.status) {
                                case ERROR:
                                    questsByDay.forEach(ldata -> {
                                        ldata.setValue(Resource.error(resource.message, Collections.emptyList()));
                                    });
                                    break;
                                case SUCCESS:
                                    if (resource.data != null) {
                                        buildQuestsByDayFromMap(resource.data.getQuestsByDay());
                                    }
                            }

                        },
                        err -> {
                    final String errm;
                            if (err instanceof HttpException) {
                                HttpException err2 = (HttpException) err;
                                Log.e(TAG, "queryUserQuests: " + err2.message());
                                errm = err2.message();
                            }else
                                errm = err.getMessage();
                            questsByDay.forEach(ldata -> {
                                ldata.setValue(Resource.error(errm, Collections.emptyList()));
                            });
                        });
    }

    private void buildQuestsByDayFromMap(Map<Integer, List<QuestBriefModel>> questsByDayMap) {
        Log.d(TAG, "buildQuestsByDayFromMap: Initializing For Map");
        int totalFinishedQuests = questsByDayMap.values().stream().map(List::size)
                .reduce(Integer::sum).orElseGet( ()-> 0);
        sessionManager.addUserQuestCount(totalFinishedQuests);

        questsByDayMap.entrySet().forEach( integerListEntry -> {
            integerListEntry.getValue().forEach(model ->{
                try {
                    Class <?> c = R.drawable.class;
                    Field idField = c.getDeclaredField("quest_" + model.getId());
                    model.setResourceId(idField.getInt(idField));
                }
                catch (Exception e) {
                    Log.e("MyTag", "Failure to get drawable id." + e.getMessage());
                }
            });
            questsByDay.get(integerListEntry.getKey()).setValue(
                    Resource.success(integerListEntry.getValue())
            );
        });

    }

    @NonNull
    private <T> Resource<T> buildResourceFromResponse(@NonNull Response<T> response) throws IOException {
        if (response.isSuccessful()) {
            Log.d(TAG, "buildResourceFromResponse: Successful return!");
            return Resource.success(response.body());
        }
        String errm;
        switch (response.code()) {
            case 500:
                errm = Constants.Error.SOMETHING_WENT_WRONG;
                break;
            case 404:
                errm = Constants.Error.COULD_NOT_LOCATE_USER;
                break;
            default:
                errm = response.errorBody() != null ? response.errorBody().string() : Constants.Error.SOMETHING_WENT_WRONG;
                break;
        }
        return Resource.error(errm, (T) null);
    }

    @NonNull
    private Response<UserModel> manageErrorFromRequest(@NonNull Throwable err) {
        if (err instanceof HttpException) {
            HttpException err2 = (HttpException) err;
            if (err2.response().errorBody() != null)
                return Response.error(err2.code(), err2.response().errorBody());
            return Response.error(err2.code(), ResponseBody.create(MediaType.parse("text/plain"), err.getMessage()));
        }
        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), err.getMessage()));
    }
    @NonNull
    private <T> Response<T> manageErrorFromGenericRequest(@NonNull Throwable err) {
        Log.d(TAG, "manageErrorFromGenericRequest: Enter");
        if (err instanceof HttpException) {
            HttpException err2 = (HttpException) err;
            if (err2.response().errorBody() != null)
                return Response.error(err2.code(), err2.response().errorBody());
            return Response.error(err2.code(), ResponseBody.create(MediaType.parse("text/plain"), err.getMessage()));
        }
        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), err.getMessage()));
    }
}
