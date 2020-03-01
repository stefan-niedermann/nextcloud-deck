package it.niedermann.nextcloud.deck.ui.board;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccessControlBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAccessControlOwnerBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class AccessControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @NonNull
    private List<AccessControl> accessControls;
    @NonNull
    private AccessControlChangedListener accessControlChangedListener;
    @Nullable
    private Context context;

    AccessControlAdapter(@NonNull List<AccessControl> accessControls, @NonNull AccessControlChangedListener accessControlChangedListener, @Nullable Context context) {
        super();
        this.accessControls = accessControls;
        this.accessControlChangedListener = accessControlChangedListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            ItemAccessControlOwnerBinding binding = ItemAccessControlOwnerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new OwnerViewHolder(binding);
        } else if (viewType == TYPE_ITEM) {
            ItemAccessControlBinding binding = ItemAccessControlBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new AccessControlViewHolder(binding);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccessControl ac = accessControls.get(position);
        if (holder instanceof OwnerViewHolder) {
            OwnerViewHolder ownerHolder = (OwnerViewHolder) holder;
            ownerHolder.binding.owner.setText(ac.getUser().getDisplayname());

            if (context != null) {
                try {
                    ViewUtil.addAvatar(context, ownerHolder.binding.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                }
            }
        } else if (holder instanceof AccessControlViewHolder) {
            AccessControlViewHolder acHolder = (AccessControlViewHolder) holder;

            if (context != null) {
                try {
                    ViewUtil.addAvatar(context, acHolder.binding.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                }
            }

            acHolder.binding.username.setText(ac.getUser().getDisplayname());
            acHolder.binding.username.setCompoundDrawables(null, null, ac.getStatus() == DBStatus.LOCAL_EDITED.getId() ? context.getResources().getDrawable(R.drawable.ic_sync_blue_24dp) : null, null);
            // TODO remove from list when deleted
            acHolder.binding.delete.setOnClickListener((v) -> accessControlChangedListener.deleteAccessControl(ac));

            acHolder.binding.permissionEdit.setChecked(ac.isPermissionEdit());
            acHolder.binding.permissionEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ac.setPermissionEdit(isChecked);
                accessControlChangedListener.updateAccessControl(ac);
            });

            acHolder.binding.permissionManage.setChecked(ac.isPermissionManage());
            acHolder.binding.permissionManage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ac.setPermissionManage(isChecked);
                accessControlChangedListener.updateAccessControl(ac);
                acHolder.binding.username.setCompoundDrawables(null, null, null, null);
            });

            acHolder.binding.permissionShare.setChecked(ac.isPermissionShare());
            acHolder.binding.permissionShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ac.setPermissionShare(isChecked);
                accessControlChangedListener.updateAccessControl(ac);
            });
        }
    }

    @Override
    public int getItemCount() {
        return accessControls.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    private static class AccessControlViewHolder extends RecyclerView.ViewHolder {
        //        @BindDrawable(R.drawable.ic_sync_blue_24dp)
//        Drawable syncIcon;
        private ItemAccessControlBinding binding;

        private AccessControlViewHolder(ItemAccessControlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class OwnerViewHolder extends RecyclerView.ViewHolder {
        private ItemAccessControlOwnerBinding binding;

        private OwnerViewHolder(ItemAccessControlOwnerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface AccessControlChangedListener {
        void updateAccessControl(AccessControl accessControl);

        void deleteAccessControl(AccessControl ac);
    }
}
