package it.niedermann.nextcloud.deck.ui.branding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.DialogInputBinding;

public class InputAlertDialogBuilder extends AlertDialog.Builder {

    private AlertDialog dialog;
    private final DialogInputBinding binding;
    private final Map<Integer, Pair<Integer, BiConsumer<TextInputLayout, IResponseCallback<Boolean>>>> listeners;
    private final AtomicBoolean waitingForListener = new AtomicBoolean(false);
    @ColorInt
    private final int color;

    public InputAlertDialogBuilder(@NonNull Context context, @NonNull LayoutInflater inflater, @ColorInt int color, @NonNull Map<Integer, Pair<Integer, BiConsumer<TextInputLayout, IResponseCallback<Boolean>>>> buttons, @StringRes int hintId) {
        this(context, inflater, color, buttons);
        binding.inputWrapper.setHint(hintId);
    }

    public InputAlertDialogBuilder(@NonNull Context context, @NonNull LayoutInflater inflater, @ColorInt int color, @NonNull Map<Integer, Pair<Integer, BiConsumer<TextInputLayout, IResponseCallback<Boolean>>>> buttons) {
        super(context);
        this.binding = DialogInputBinding.inflate(inflater, null, false);
        super.setView(binding.getRoot());

        this.color = color;
        this.listeners = new HashMap<>(buttons.size());
        buttons.forEach((buttonId, btnConfig) -> {
            if (btnConfig.first == null) {
                throw new IllegalArgumentException("First of Pair must be a valid StringRes.");
            }
            if (btnConfig.second == null) {
                throw new IllegalArgumentException("Second of Pair must not be null.");
            }
            switch (buttonId) {
                case DialogInterface.BUTTON_POSITIVE:
                    super.setPositiveButton(btnConfig.first, null);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    super.setNegativeButton(btnConfig.first, null);
                    break;
                default:
                    throw new IllegalArgumentException("Button identifier must be BUTTON_POSITIVE, BUTTON_NEGATIVE.");
            }
            listeners.put(buttonId, btnConfig);
        });
    }

    @Override
    public AlertDialog show() {
        dialog = super.show();
        binding.input.requestFocus();
        binding.input.postDelayed(() -> {
            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.input, InputMethodManager.SHOW_IMPLICIT);
        }, 150);
        BrandingUtil.applyBrandToEditTextInputLayout(color, binding.inputWrapper);
        binding.progressCircular.setIndeterminateTintList(ColorStateList.valueOf(BrandingUtil.getSecondaryForegroundColorDependingOnTheme(getContext(), color)));
        listeners.forEach((btn, listener) -> dialog.getButton(btn).setOnClickListener(v -> {
            if (!waitingForListener.get()) {
                binding.inputWrapper.setVisibility(View.INVISIBLE);
                binding.progressCircular.setVisibility(View.VISIBLE);
                listener.second.accept(binding.inputWrapper, new IResponseCallback<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        binding.getRoot().post(() -> {
                            if (response) {
                                dialog.dismiss();
                            } else {
                                binding.input.requestFocus();
                            }
                            binding.progressCircular.setVisibility(View.GONE);
                            binding.inputWrapper.setVisibility(View.VISIBLE);
                        });
                        waitingForListener.set(false);
                    }

                    @Override
                    @SuppressLint("MissingSuperCall")
                    public void onError(Throwable throwable) {
                        binding.getRoot().post(() -> {
                            dialog.dismiss();
                            binding.progressCircular.setVisibility(View.GONE);
                            binding.inputWrapper.setVisibility(View.VISIBLE);
                        });
                        waitingForListener.set(false);
                    }
                });
            }
        }));
        return dialog;
    }

    @Override
    public InputAlertDialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        throw new UnsupportedOperationException("Don't call this method. It will be called implicitly by " + InputAlertDialogBuilder.class.getSimpleName());
    }

    @Override
    public InputAlertDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        throw new UnsupportedOperationException("Don't call this method. It will be called implicitly by " + InputAlertDialogBuilder.class.getSimpleName());
    }

    @Override
    public AlertDialog.Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        throw new UnsupportedOperationException("Don't call this method. It will be called implicitly by " + InputAlertDialogBuilder.class.getSimpleName());
    }

    @Override
    public AlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        throw new UnsupportedOperationException("Don't call this method. It will be called implicitly by " + InputAlertDialogBuilder.class.getSimpleName());
    }

    @Override
    public AlertDialog.Builder setView(View view) {
        throw new UnsupportedOperationException("Don't call this method. It will be called implicitly by " + InputAlertDialogBuilder.class.getSimpleName());
    }
}
