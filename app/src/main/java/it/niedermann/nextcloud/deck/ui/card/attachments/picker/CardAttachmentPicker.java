package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.niedermann.nextcloud.deck.databinding.DialogAttachmentPickerBinding;

public class CardAttachmentPicker extends BottomSheetDialogFragment {

    private DialogAttachmentPickerBinding binding;
    private CardAttachmentPickerListener listener;

    public CardAttachmentPicker() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof CardAttachmentPickerListener) {
            this.listener = (CardAttachmentPickerListener) getParentFragment();
        } else {
            throw new IllegalArgumentException("Caller must implement " + CardAttachmentPickerListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAttachmentPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.pickCamera.setOnClickListener((v) -> {
            listener.pickCamera();
            dismiss();
        });
        binding.pickContact.setOnClickListener((v) -> {
            listener.pickContact();
            dismiss();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.pickFile.setOnClickListener((v) -> {
                listener.pickFile();
                dismiss();
            });
        } else {
            binding.pickFile.setVisibility(View.GONE);
        }
    }

    public static DialogFragment newInstance() {
        return new CardAttachmentPicker();
    }
}
