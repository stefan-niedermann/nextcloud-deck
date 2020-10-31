package it.niedermann.nextcloud.deck.ui.takephoto;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

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

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA;
import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.IMAGE_JPEG;

public class TakePhotoActivity extends BrandedActivity {

    private ActivityTakePhotoBinding binding;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private final DateTimeFormatter fileNameFromCameraFormatter = DateTimeFormatter.ofPattern("'JPG_'yyyyMMdd'_'HHmmss'.jpg'");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                final ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                final Preview previewUseCase = new Preview.Builder()
                        .build();

                previewUseCase.setSurfaceProvider(binding.preview.getSurfaceProvider());

                final ImageCapture captureUseCase;
                final ImageCapture.Builder captureUseCaseBuilder = new ImageCapture.Builder();
                if (SDK_INT >= LOLLIPOP) {
                    captureUseCaseBuilder.setTargetResolution(new Size(720, 1280)).build();
                }
                captureUseCase = captureUseCaseBuilder.build();

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
                Camera camera = cameraProvider.bindToLifecycle(this, DEFAULT_BACK_CAMERA, captureUseCase, previewUseCase);
            } catch (ExecutionException | InterruptedException e) {
                DeckLog.logError(e);
            }

        }, ContextCompat.getMainExecutor(this));
    }


    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, TakePhotoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void applyBrand(int mainColor) {
        binding.takePhoto.setBackgroundTintList(ColorStateList.valueOf(mainColor));
    }
}
