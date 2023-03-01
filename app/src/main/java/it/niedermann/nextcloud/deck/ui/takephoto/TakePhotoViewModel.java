package it.niedermann.nextcloud.deck.ui.takephoto;

import static androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA;
import static androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class TakePhotoViewModel extends BaseViewModel {

    @NonNull
    private CameraSelector cameraSelector = DEFAULT_BACK_CAMERA;
    @NonNull
    private final MutableLiveData<Integer> cameraSelectorToggleButtonImageResource = new MutableLiveData<>(R.drawable.ic_baseline_camera_front_24);
    @NonNull
    private final MutableLiveData<Boolean> torchEnabled = new MutableLiveData<>(false);

    public TakePhotoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Long> getCurrentAccountId$() {
        return baseRepository.getCurrentAccountId$();
    }

    public LiveData<Long> getCurrentBoardId$(long accountId) {
        return baseRepository.getCurrentBoardId$(accountId);
    }

    public LiveData<Integer> getBoardColor$(long accountId, long boardId) {
        return baseRepository.getBoardColor$(accountId, boardId);
    }

    @NonNull
    public CameraSelector getCameraSelector() {
        return this.cameraSelector;
    }

    public LiveData<Integer> getCameraSelectorToggleButtonImageResource() {
        return this.cameraSelectorToggleButtonImageResource;
    }

    public void toggleCameraSelector() {
        if (this.cameraSelector == DEFAULT_BACK_CAMERA) {
            this.cameraSelector = DEFAULT_FRONT_CAMERA;
            this.cameraSelectorToggleButtonImageResource.postValue(R.drawable.ic_baseline_camera_rear_24);
        } else {
            this.cameraSelector = DEFAULT_BACK_CAMERA;
            this.cameraSelectorToggleButtonImageResource.postValue(R.drawable.ic_baseline_camera_front_24);
        }
    }

    public void toggleTorchEnabled() {
        //noinspection ConstantConditions
        this.torchEnabled.postValue(!this.torchEnabled.getValue());
    }

    public LiveData<Boolean> isTorchEnabled() {
        return this.torchEnabled;
    }

    public LiveData<Integer> getTorchToggleButtonImageResource() {
        return new ReactiveLiveData<>(isTorchEnabled())
                .map(enabled -> enabled
                        ? R.drawable.ic_baseline_flash_off_24
                        : R.drawable.ic_baseline_flash_on_24);
    }
}
