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
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class AccessControlAdapter extends RecyclerView.Adapter<AccessControlAdapter.ActivitiesViewHolder> {

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
    public ActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_access_control, parent, false);
        return new ActivitiesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesViewHolder holder, int position) {
        AccessControl ac = accessControls.get(position);

        if (context != null) {
            try {
                ViewUtil.addAvatar(context, holder.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
            } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                e.printStackTrace();
            }
        }
        holder.username.setText(ac.getUser().getDisplayname());

        holder.switchEdit.setChecked(ac.isPermissionEdit());
        holder.switchEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ac.setPermissionEdit(isChecked);
            holder.username.setCompoundDrawables(null, null, holder.syncIcon, null);
            accessControlChangedListener.updateAccessControl(ac);
            holder.username.setCompoundDrawables(null, null, null, null);
        });

        holder.switchManage.setChecked(ac.isPermissionManage());
        holder.switchManage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ac.setPermissionManage(isChecked);
            holder.username.setCompoundDrawables(null, null, holder.syncIcon, null);
            accessControlChangedListener.updateAccessControl(ac);
            holder.username.setCompoundDrawables(null, null, null, null);
        });

        holder.switchShare.setChecked(ac.isPermissionShare());
        holder.switchShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ac.setPermissionShare(isChecked);
            holder.username.setCompoundDrawables(null, null, holder.syncIcon, null);
            accessControlChangedListener.updateAccessControl(ac);
            holder.username.setCompoundDrawables(null, null, null, null);
        });
    }

    @Override
    public int getItemCount() {
        return accessControls.size();
    }

    static class ActivitiesViewHolder extends RecyclerView.ViewHolder {
        @BindDrawable(R.drawable.ic_sync_blue_24dp)
        Drawable syncIcon;
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.permission_edit)
        SwitchCompat switchEdit;
        @BindView(R.id.permission_manage)
        SwitchCompat switchManage;
        @BindView(R.id.permission_share)
        SwitchCompat switchShare;

        private ActivitiesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface AccessControlChangedListener {
        void updateAccessControl(AccessControl accessControl);
    }
}
