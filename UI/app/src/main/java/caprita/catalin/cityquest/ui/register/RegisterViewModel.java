package caprita.catalin.cityquest.ui.register;

import android.annotation.SuppressLint;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.api.ResponseResourceConverter;
import caprita.catalin.cityquest.ui.api.auth.AuthApi;
import caprita.catalin.cityquest.ui.api.register.RegisterApi;
import caprita.catalin.cityquest.ui.api.register.RegisterDto;
import caprita.catalin.cityquest.ui.api.register.UserPersonalDataDto;
import caprita.catalin.cityquest.ui.api.register.UserRegisterDetailsDto;
import caprita.catalin.cityquest.ui.api.register.UserRollbackDto;
import caprita.catalin.cityquest.ui.models.BfpiQuestion;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserCompanion;
import caprita.catalin.cityquest.ui.models.UserModel;
import caprita.catalin.cityquest.ui.util.Constants;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private final MediatorLiveData<Resource<UserModel>> newUserModel = new MediatorLiveData<>();
    private final MutableLiveData<List<BfpiQuestion>> quizModel =new MutableLiveData<>();
    private final MediatorLiveData<Resource<Void>> rollbackModel = new MediatorLiveData<>();
    private RegisterDto registerDto;
    private final RegisterApi registerApi;
    private static final String TAG = "RegisterViewModel";
    public static final int STEP_CREATE = 0 ;
    public static final int STEP_PERS_DATA = 1 ;
    public static final int STEP_ADD_COMPANIONS = 2 ;
    public static final int STEP_QUIZ = 3;
    public static final int STEP_WAIT = 4;
    public static final int STEP_PREDICTIONS = 5;
    public static final int STEP_FINISH = 6;



    @Inject
    public RegisterViewModel(RegisterApi registerApi) {
        this.registerApi = registerApi;
    }

    public LiveData<Resource<UserModel>> observeNewUser(){
        return this.newUserModel;
    }

    public LiveData<List<BfpiQuestion>> observeQuizModel(){
        return this.quizModel;
    }
    public LiveData<Resource<Void>> observeRollback(){
        return this.rollbackModel;
    }


//    ROLLBACK USER REGISTRATION

    public void rollbackUserRegistration(){
        if(this.newUserModel.getValue() != null && this.newUserModel.getValue().data != null){
            UserRollbackDto dto = new UserRollbackDto();
            dto.setUsername(registerDto.getUsername());
            final LiveData<Resource<Void>> rbSource = LiveDataReactiveStreams.fromPublisher(
                    registerApi.rollbackRegistration(dto)
                    .onErrorReturn(ResponseResourceConverter::buildResponseFromError)
                    .map(ResponseResourceConverter::buildResourceFromResponse)
                    .subscribeOn(Schedulers.io())
            );
            this.rollbackModel.addSource(rbSource,
                    voidResource -> {
                        if(voidResource != null) {
                            rollbackModel.setValue(voidResource);
                            rollbackModel.removeSource(rbSource);
                        }
                    });
        }
    }


    //    STEP CREATE
    public void attemptRegisterUser(RegisterDto registerDto){
        this.registerDto = registerDto;
        newUserModel.setValue(Resource.loading(getCurrentOrNull()));
        final LiveData<Resource<UserModel>> source = LiveDataReactiveStreams.fromPublisher(
                registerApi.registerNewUser(registerDto)
                        .onErrorReturn(ResponseResourceConverter::buildResponseFromError)
                        .map(ResponseResourceConverter::buildResourceFromResponse)
                        .subscribeOn(Schedulers.io())
        );

        newUserModel.addSource(source, new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> userModelResource) {
                if(userModelResource != null){
                    if(userModelResource.data != null) {
                        userModelResource.data.setRegistrationStep(STEP_PERS_DATA);
                    }
                    newUserModel.setValue(userModelResource);
//                    Get to next step
                    newUserModel.removeSource(source);
                }
            }
        });
    }

//    STEP PERS_DATA
    public void addNewUserDetails(UserPersonalDataDto dto){
        if(this.newUserModel.getValue() != null &&
                this.newUserModel.getValue().data != null){
            UserModel model = this.newUserModel.getValue().data;
            model.setFirstName(dto.getFirstName());
            model.setLastName(dto.getLastName());
            model.setGender(dto.getGender());
            model.setAlone(dto.isAlone());
            if(model.isAlone()) {
                model.setRegistrationStep(STEP_QUIZ);
                Log.d(TAG, "addNewUserDetails: User details Added. Setting up the 10 item Quiz.");
                final List<BfpiQuestion> list = new ArrayList<>();
                for(int i=0 ;i < 10; i++){
                    list.add(new BfpiQuestion(i, -1));
                }
                this.quizModel.setValue(list);
            }
            else
                model.setRegistrationStep(STEP_ADD_COMPANIONS);
            this.newUserModel.setValue(Resource.success(model));

        }
    }
//    STEP ADD COMPANIONS
    public void addUserCompanions(List<UserCompanion> companions){
        if(this.newUserModel.getValue() != null && this.newUserModel.getValue().data != null){
            UserModel model = this.newUserModel.getValue().data;
            model.setCompanions(companions);
            model.setRegistrationStep(STEP_QUIZ);
            Log.d(TAG, "addUserCompanions: User companions Added. Setting up the 10 item Quiz.");
            final List<BfpiQuestion> list = new ArrayList<>();
            for(int i=0 ;i < 10; i++){
                list.add(new BfpiQuestion(i, -1));
            }
            this.quizModel.setValue(list);
            this.newUserModel.setValue(Resource.success(model));
        }
    }
//    STEP QUIZ
    public void addOrUpdateQuestionAnswer(int questionId, int checkId, int answerValue){
        if(this.quizModel.getValue() != null && questionId < this.quizModel.getValue().size()){
            List<BfpiQuestion> current = this.quizModel.getValue();
            current.get(questionId).setSelectedAnswer(checkId);
            current.get(questionId).setAnswerValue(answerValue);
            this.quizModel.setValue(current);
        }

    }
    public void preCompleteUserRegistration() throws InvalidParameterException{
        String error = "Please proceed to answer all the questions.";
        int [] responses = new int[quizModel.getValue().size()];
        for(BfpiQuestion q : this.quizModel.getValue()) {
            if (q.getSelectedAnswer() == -1) {
                throw new InvalidParameterException(error);
            }
        }
    }

//    STEP_WAIT
    @SuppressLint("DefaultLocale")
    public void completeUserRegistration(){
        newUserModel.setValue(Resource.loading(getCurrentOrNull()));
        /*First off check if all questions have been answered*/
        String error = "Please proceed to answer all the questions.";
        int [] responses = new int[quizModel.getValue().size()];
        for(BfpiQuestion q : this.quizModel.getValue()) {
            responses[q.getIndex()] = q.getAnswerValue();
        }

        UserModel model = newUserModel.getValue().data;
        if(model == null) {
            Log.d(TAG, "completeUserRegistration: User data is null");
            newUserModel.setValue(Resource.error(Constants.Error.SOMETHING_WENT_WRONG, null, 500));
            return;
        }
        UserRegisterDetailsDto dto = new UserRegisterDetailsDto();
        dto.setId(model.getId());
        dto.setFirstName(model.getFirstName());
        dto.setGender(model.getGender());
        dto.setLastName(model.getLastName());
        dto.setQuizResponses(responses);
        dto.setCompanions(model.getCompanions());

        Log.d(TAG, "completeUserRegistration: Setting user ID: " + dto.getId());
        final LiveData<Resource<UserModel>> source = LiveDataReactiveStreams.fromPublisher(
                registerApi.completeUserRegistration(dto)
                        .onErrorReturn(ResponseResourceConverter::buildResponseFromError)
                        .map(ResponseResourceConverter::buildResourceFromResponse)
                        .subscribeOn(Schedulers.io())
        );

        newUserModel.addSource(source, new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> userModelResource) {
                if(userModelResource != null){
                    if(userModelResource.data != null &&
                            userModelResource.status == Resource.Status.SUCCESS) {
                        userModelResource.data.setRegistrationStep(STEP_PREDICTIONS);
                        userModelResource.data.setEnabled(true);
                    }
                    newUserModel.setValue(userModelResource);
                    newUserModel.removeSource(source);
                }
            }
        });
    }

//    private <T> Response<T> buildResponseFromError(Throwable err) {
//        err.printStackTrace();
//        Log.e(TAG, "Error Encountered");
//        if (err instanceof HttpException) {
//            HttpException err2 = (HttpException) err;
//            return Resource.error(Integer.toString(err2.code()), (T) null);
//        }
//        return Resource.error(err.getMessage(), (T) null);
//    }
//  STEP_PREDICTIONS
    public void computeUserPredictions(){
        final UserModel model = getCurrentOrNull();
        if(model == null) {
            Log.d(TAG, "completeUserRegistration: User data is null");
            return;
        }
        newUserModel.setValue(Resource.loading(model));
        final LiveData<Resource<UserModel>> source = LiveDataReactiveStreams.fromPublisher(
                registerApi.computeUserPredictions(model.getId())
                        .onErrorReturn(err ->{
                            final int errcode;
                            if(err instanceof HttpException)
                                errcode = ((HttpException)err).code();
                            else
                                errcode = 500;
                            return Response.error(errcode,
                                    ResponseBody.create(MediaType.parse("text/plain"),
                                            err.getMessage()));
                        })
                        .map( response ->{
                            if(response.isSuccessful()) {
                                Log.d(TAG, "buildResourceFromResponse: Successful return!");
                                return Resource.success(model);
                            }
                            return Resource.error(response.errorBody() != null? response.errorBody().string():
                                    "Error"
                                    ,model, response.code());
                        })
                        .subscribeOn(Schedulers.io())
        );
        newUserModel.addSource(source, new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> userModelResource) {
                if(userModelResource != null){
                    if(userModelResource.data != null &&
                            userModelResource.status == Resource.Status.SUCCESS) {
//                        If properly returned from STEP_PREDICTIONS, it means we can finish
                        userModelResource.data.setRegistrationStep(STEP_FINISH);
                        userModelResource.data.setEnabled(true);
                    }
//                    Sets UserModelResource Status to either ERROR or SUCCESS, to it is to be
//                    checked inside the view controller
                    newUserModel.setValue(userModelResource);
                    newUserModel.removeSource(source);
                }
            }
        });

    }



    @NonNull
    private Resource<UserModel> buildResourceFromResponse(@NonNull Response<UserModel> response) throws IOException {
        if(response.isSuccessful()) {
            Log.d(TAG, "buildResourceFromResponse: Successful return!");
            return Resource.success((UserModel)response.body());
        }

        String error;
        switch (response.code()){
            case 500:
                error = "Something went wrong";
                break;
            case 403:
            case 401:
                error = "Unauthenciated.";
                break;
            default:
                error = response.errorBody() != null? response.errorBody().string(): "Something Went Wrong";
                break;
        }
        return Resource.error(
                 error,
                this.getCurrentOrNull());
    }

    private UserModel getCurrentOrNull(){
        return this.newUserModel.getValue() != null ? this.newUserModel.getValue().data : null;
    }
}
