package it.niedermann.nextcloud.deck.deprecated.ui.accountswitcher;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import java.util.Objects;
import java.util.stream.Collectors;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAccountSwitcherBinding;
import it.niedermann.nextcloud.deck.deprecated.ui.manageaccounts.ManageAccountsActivity;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.ImportAccountActivity;

public class AccountSwitcherDialog extends DialogFragment {

    private AccountSwitcherAdapter adapter;
    private DialogAccountSwitcherBinding binding;
    private AccountViewModel accountViewModel;

    private final ActivityResultLauncher<Intent> importAccountLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) {
            requireActivity().finish();
        }
    });

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAccountSwitcherBinding.inflate(requireActivity().getLayoutInflater());
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        adapter = new AccountSwitcherAdapter((localAccount -> {
            accountViewModel.saveCurrentAccount(localAccount);
            dismiss();
        }));

        binding.accountLayout.setOnClickListener((v) -> dismiss());
        binding.check.setSelected(true);
        binding.accountsList.setAdapter(adapter);
        binding.addAccount.setOnClickListener((v) -> {
            importAccountLauncher.launch(ImportAccountActivity.createIntent(requireContext()));
            dismiss();
        });
        binding.manageAccounts.setOnClickListener((v) -> {
            requireActivity().startActivity(ManageAccountsActivity.createIntent(requireContext()));
            dismiss();
        });

        new ReactiveLiveData<>(accountViewModel.getCurrentAccount())
                .flatMap(currentAccount -> {
                    binding.accountName.setText(
                            TextUtils.isEmpty(currentAccount.getUserDisplayName())
                                    ? currentAccount.getUserName()
                                    : currentAccount.getUserDisplayName()
                    );
                    binding.accountHost.setText(Uri.parse(currentAccount.getUrl()).getHost());

                    Glide.with(requireContext())
                            .load(currentAccount.getAvatarUrl(binding.currentAccountItemAvatar.getResources().getDimensionPixelSize(R.dimen.avatar_size)))
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.ic_account_circle_24)
                            .error(R.drawable.ic_account_circle_24)
                            .into(binding.currentAccountItemAvatar);

                    applyTheme(currentAccount.getColor());

                    return new ReactiveLiveData<>(accountViewModel.readAccounts())
                            .map(accounts -> accounts
                                    .stream()
                                    .filter(account -> !Objects.equals(account.getId(), currentAccount.getId()))
                                    .collect(Collectors.toList())
                            );
                })
                .observe(this, adapter::setAccounts);

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static DialogFragment newInstance() {
        return new AccountSwitcherDialog();
    }

    private void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, requireContext());
        utils.deck.themeSelectedCheck(binding.check.getContext(), binding.check.getDrawable());
        utils.platform.colorImageView(binding.addAccountIcon, ColorRole.ON_SURFACE);
        utils.platform.colorImageView(binding.manageAccountsIcon, ColorRole.ON_SURFACE);
        utils.platform.colorTextView(binding.addAccountLabel, ColorRole.ON_SURFACE);
        utils.platform.colorTextView(binding.manageAccountsLabel, ColorRole.ON_SURFACE);
    }
}
