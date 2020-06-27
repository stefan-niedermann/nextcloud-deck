package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAccountSwitcherBinding;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;
import it.niedermann.nextcloud.deck.ui.manageaccounts.ManageAccountsActivity;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.MainActivity.ACTIVITY_MANAGE_ACCOUNTS;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class AccountSwitcherDialog extends BrandedDialogFragment {

    private AccountSwitcherAdapter adapter;
    private SyncManager syncManager;
    private DialogAccountSwitcherBinding binding;
    private MainViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        syncManager = new SyncManager(requireActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAccountSwitcherBinding.inflate(requireActivity().getLayoutInflater());
        binding.accountName.setText(viewModel.getCurrentAccount().getUserName());
        binding.accountHost.setText(Uri.parse(viewModel.getCurrentAccount().getUrl()).getHost());
        binding.check.setSelected(true);

        Glide.with(requireContext())
                .load(viewModel.getCurrentAccount().getAvatarUrl(dpToPx(binding.currentAccountItemAvatar.getContext(), R.dimen.avatar_size)))
                .error(R.drawable.ic_person_grey600_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.currentAccountItemAvatar);

        binding.accountLayout.setOnClickListener((v) -> dismiss());

        adapter = new AccountSwitcherAdapter((localAccount -> {
            viewModel.setCurrentAccount(localAccount, localAccount.getServerDeckVersionAsObject().isSupported(requireContext()));
            dismiss();
        }));

        observeOnce(syncManager.readAccounts(), requireActivity(), (accounts) -> {
            accounts.remove(viewModel.getCurrentAccount());
            adapter.setAccounts(accounts);
        });

        binding.accountsList.setAdapter(adapter);

        binding.addAccount.setOnClickListener((v) -> {
            try {
                AccountImporter.pickNewAccount(requireActivity());
            } catch (NextcloudFilesAppNotInstalledException e) {
                ExceptionUtil.handleNextcloudFilesAppNotInstalledException(requireContext(), e);
            } catch (AndroidGetAccountsPermissionNotGranted e) {
                AccountImporter.requestAndroidAccountPermissionsAndPickAccount(requireActivity());
            }
            dismiss();
        });

        binding.manageAccounts.setOnClickListener((v) -> {
            requireActivity().startActivityForResult(new Intent(requireContext(), ManageAccountsActivity.class), ACTIVITY_MANAGE_ACCOUNTS);
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    public static DialogFragment newInstance() {
        return new AccountSwitcherDialog();
    }

    @Override
    public void applyBrand(int mainColor) {
//        applyBrandToLayerDrawable((LayerDrawable) binding.check.getDrawable(), R.id.area, mainColor);
    }
}
