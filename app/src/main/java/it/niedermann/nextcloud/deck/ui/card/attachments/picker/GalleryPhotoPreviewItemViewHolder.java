package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.annotation.NonNull;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ItemPhotoPreviewBinding;

import static androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA;

public class GalleryPhotoPreviewItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemPhotoPreviewBinding binding;
    private ProcessCameraProvider cameraProvider;

    public GalleryPhotoPreviewItemViewHolder(@NonNull ItemPhotoPreviewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Runnable openNativePicker, @NonNull LifecycleOwner lifecycleOwner) {
        itemView.setOnClickListener((v) -> openNativePicker.run());
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(itemView.getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                unbind();
                cameraProvider = cameraProviderFuture.get();
                Preview previewUseCase = new Preview.Builder().build();
                previewUseCase.setSurfaceProvider(binding.preview.getSurfaceProvider());
                cameraProvider.bindToLifecycle(lifecycleOwner, DEFAULT_BACK_CAMERA, previewUseCase);
            } catch (ExecutionException | InterruptedException | IllegalArgumentException e) {
                DeckLog.logError(e);
            }
        }, ContextCompat.getMainExecutor(itemView.getContext()));
    }


    public void unbind() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
