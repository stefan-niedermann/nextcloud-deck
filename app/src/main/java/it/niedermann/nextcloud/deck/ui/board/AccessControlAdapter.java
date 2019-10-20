package it.niedermann.nextcloud.deck.ui.board;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_access_control_owner, parent, false);
            return new OwnerViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_access_control, parent, false);
            return new AccessControlViewHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccessControl ac = accessControls.get(position);
        if (holder instanceof OwnerViewHolder) {
            OwnerViewHolder ownerHolder = (OwnerViewHolder) holder;
            ownerHolder.owner.setText(ac.getUser().getDisplayname());

            if (context != null) {
                try {
                    ViewUtil.addAvatar(context, ownerHolder.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                }
            }
        } else if (holder instanceof AccessControlViewHolder) {
            AccessControlViewHolder acHolder = (AccessControlViewHolder) holder;

            if (context != null) {
                try {
                    ViewUtil.addAvatar(context, acHolder.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                }
            }

            acHolder.username.setText(ac.getUser().getDisplayname());
            acHolder.username.setCompoundDrawables(null, null, ac.getStatus() == DBStatus.LOCAL_EDITED.getId() ? acHolder.syncIcon : null, null);
            // TODO remove from list when deleted
            acHolder.deleteButton.setOnClickListener((v) -> accessControlChangedListener.deleteAccessControl(ac));

            acHolder.switchEdit.setChecked(ac.isPermissionEdit());
            acHolder.switchEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ac.setPermissionEdit(isChecked);
                accessControlChangedListener.updateAccessControl(ac);
            });

            acHolder.switchManage.setChecked(ac.isPermissionManage());
            acHolder.switchManage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ac.setPermissionManage(isChecked);
                accessControlChangedListener.updateAccessControl(ac);
                acHolder.username.setCompoundDrawables(null, null, null, null);
            });

            acHolder.switchShare.setChecked(ac.isPermissionShare());
            acHolder.switchShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
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

    static class AccessControlViewHolder extends RecyclerView.ViewHolder {
        @BindDrawable(R.drawable.ic_sync_blue_24dp)
        Drawable syncIcon;
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.delete)
        AppCompatImageButton deleteButton;
        @BindView(R.id.permission_edit)
        SwitchCompat switchEdit;
        @BindView(R.id.permission_manage)
        SwitchCompat switchManage;
        @BindView(R.id.permission_share)
        SwitchCompat switchShare;

        private AccessControlViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class OwnerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.owner)
        TextView owner;

        private OwnerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface AccessControlChangedListener {
        void updateAccessControl(AccessControl accessControl);

        void deleteAccessControl(AccessControl ac);
    }
}
