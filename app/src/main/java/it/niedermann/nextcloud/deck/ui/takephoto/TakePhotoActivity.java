package it.niedermann.nextcloud.deck.ui.takephoto;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.Camera;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityTakePhotoBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.IMAGE_JPEG;

@RequiresApi(LOLLIPOP)
public class TakePhotoActivity extends BrandedActivity {

    private ActivityTakePhotoBinding binding;
    private TakePhotoViewModel viewModel;

    private View[] brandedViews;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private OrientationEventListener orientationEventListener;

    private final DateTimeFormatter fileNameFromCameraFormatter = DateTimeFormatter.ofPattern("'JPG_'yyyyMMdd'_'HHmmss'.jpg'");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(TakePhotoViewModel.class);

        setContentView(binding.getRoot());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                final Preview previewUseCase = getPreviewUseCase();
                final ImageCapture captureUseCase = getCaptureUseCase();
                final Camera camera = cameraProvider.bindToLifecycle(this, viewModel.getCameraSelector(), captureUseCase, previewUseCase);

                viewModel.getCameraSelectorToggleButtonImageResource().observe(this, res -> binding.switchCamera.setImageDrawable(ContextCompat.getDrawable(this, res)));
                viewModel.getTorchToggleButtonImageResource().observe(this, res -> binding.toggleTorch.setImageDrawable(ContextCompat.getDrawable(this, res)));
                viewModel.isTorchEnabled().observe(this, enabled -> camera.getCameraControl().enableTorch(enabled));

                binding.toggleTorch.setOnClickListener((v) -> viewModel.toggleTorchEnabled());
                binding.switchCamera.setOnClickListener((v) -> {
                    viewModel.toggleCameraSelector();
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, viewModel.getCameraSelector(), captureUseCase, previewUseCase);
                });
            } catch (ExecutionException | InterruptedException e) {
                DeckLog.logError(e);
                finish();
            }
        }, ContextCompat.getMainExecutor(this));

        brandedViews = new View[]{binding.takePhoto, binding.switchCamera, binding.toggleTorch};
    }

    private ImageCapture getCaptureUseCase() {
        final ImageCapture captureUseCase = new ImageCapture.Builder().setTargetResolution(new Size(720, 1280)).build();

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;

                // Monitors orientation values to determine the target rotation value
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }

                captureUseCase.setTargetRotation(rotation);
            }
        };
        orientationEventListener.enable();

        binding.takePhoto.setOnClickListener((v) -> {
            binding.takePhoto.setEnabled(false);
            final String photoFileName = Instant.now().atZone(ZoneId.systemDefault()).format(fileNameFromCameraFormatter);
            try {
                final File photoFile = AttachmentUtil.getTempCacheFile(this, "photos/" + photoFileName);
                final ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
                captureUseCase.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        final Uri savedUri = Uri.fromFile(photoFile);
                        DeckLog.info("onImageSaved - savedUri: " + savedUri.toString());
                        setResult(RESULT_OK, new Intent().setDataAndType(savedUri, IMAGE_JPEG));
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException e) {
                        e.printStackTrace();
                        //noinspection ResultOfMethodCallIgnored
                        photoFile.delete();
                        binding.takePhoto.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                ExceptionDialogFragment.newInstance(e, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });

        return captureUseCase;
    }

    private Preview getPreviewUseCase() {
        Preview previewUseCase = new Preview.Builder().build();
        previewUseCase.setSurfaceProvider(binding.preview.getSurfaceProvider());
        return previewUseCase;
    }

    @Override
    protected void onPause() {
        if (this.orientationEventListener != null) {
            this.orientationEventListener.disable();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.orientationEventListener != null) {
            this.orientationEventListener.enable();
        }
    }

    @RequiresApi(LOLLIPOP)
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, TakePhotoActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Override
    public void applyBrand(int mainColor) {
        final ColorStateList colorStateList = ColorStateList.valueOf(mainColor);
        for (View v : brandedViews) {
            v.setBackgroundTintList(colorStateList);
        }
    }
}
