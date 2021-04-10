package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.DeckApplication;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAccountSwitcherBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.manageaccounts.ManageAccountsActivity;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class AccountSwitcherDialog extends DialogFragment {

    private AccountSwitcherAdapter adapter;
    private DialogAccountSwitcherBinding binding;
    private MainViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAccountSwitcherBinding.inflate(requireActivity().getLayoutInflater());
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        new Thread(() -> {
            final String displayName = viewModel.getUserByUidDirectly(viewModel.getCurrentAccount().getId(), viewModel.getCurrentAccount().getUserName()).getDisplayname();
            requireActivity().runOnUiThread(() -> binding.accountName.setText(displayName));
        }).start();
        binding.accountHost.setText(Uri.parse(viewModel.getCurrentAccount().getUrl()).getHost());
        binding.check.setSelected(true);

        Glide.with(requireContext())
                .load(viewModel.getCurrentAccount().getAvatarUrl(DimensionUtil.INSTANCE.dpToPx(binding.currentAccountItemAvatar.getContext(), R.dimen.avatar_size)))
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.currentAccountItemAvatar);

        binding.accountLayout.setOnClickListener((v) -> dismiss());

        adapter = new AccountSwitcherAdapter((localAccount -> {
            viewModel.setCurrentAccount(localAccount);
            dismiss();
        }));

        observeOnce(viewModel.readAccounts(), requireActivity(), (accounts) -> {
            accounts.remove(viewModel.getCurrentAccount());
            new Thread(() -> {
                List<Pair<Account, String>> accountUserPairs = accounts.stream().map(account -> new Pair<>(account, viewModel.getUserByUidDirectly(account.getId(), account.getUserName()).getDisplayname())).collect(Collectors.toList());
                requireActivity().runOnUiThread(() -> adapter.setAccounts(accountUserPairs));
            }).start();
        });

        observeOnce(DeckApplication.readCurrentBoardColor(), requireActivity(), this::applyBrand);

        binding.accountsList.setAdapter(adapter);

        binding.addAccount.setOnClickListener((v) -> {
            try {
                AccountImporter.pickNewAccount(requireActivity());
            } catch (NextcloudFilesAppNotInstalledException e) {
                UiExceptionManager.showDialogForException(requireContext(), e);
                DeckLog.warn("=============================================================");
                DeckLog.warn("Nextcloud app is not installed. Cannot choose account");
                DeckLog.logError(e);
            } catch (AndroidGetAccountsPermissionNotGranted e) {
                AccountImporter.requestAndroidAccountPermissionsAndPickAccount(requireActivity());
            }
            dismiss();
        });

        binding.manageAccounts.setOnClickListener((v) -> {
            requireActivity().startActivity(ManageAccountsActivity.createIntent(requireContext()));
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    public static DialogFragment newInstance() {
        return new AccountSwitcherDialog();
    }

    private void applyBrand(int mainColor) {
//        applyBrandToLayerDrawable((LayerDrawable) binding.check.getDrawable(), R.id.area, mainColor);
    }
}
