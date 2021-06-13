package caprita.catalin.cityquest.ui.register;

import android.os.Bundle;

public interface OnRegistrationStepChangedListener {
    void onRegistrationStepChanged(int fragmentStep, int modelStep);
    void onRegistrationStepFail();
}
