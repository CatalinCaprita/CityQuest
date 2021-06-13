package caprita.catalin.cityquest.ui.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserResource extends Resource<UserModel> {
    public UserResource(@NonNull Status status, @Nullable UserModel data, @Nullable String message) {
        super(status, data, message, null);
    }

}
