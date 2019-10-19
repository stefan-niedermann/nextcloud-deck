package it.niedermann.nextcloud.deck.ui.board;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.AccessControl;

public class AccessControlAdapter extends RecyclerView.Adapter<AccessControlAdapter.ActivitiesViewHolder> {

    @NonNull
    private List<AccessControl> accessControls;
    @NonNull
    private AccessControlChangedListener accessControlChangedListener;

    AccessControlAdapter(@NonNull List<AccessControl> accessControls, @NonNull AccessControlChangedListener accessControlChangedListener) {
        super();
        this.accessControls = accessControls;
        this.accessControlChangedListener = accessControlChangedListener;
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

        if (ac.getUser() != null)
            holder.username.setText(ac.getUser().getUid());

        holder.switchEdit.setChecked(ac.isPermissionEdit());
        holder.switchEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ac.setPermissionEdit(isChecked);
            accessControlChangedListener.updateAccessControl(ac);
        });

        holder.switchManage.setChecked(ac.isPermissionManage());
        holder.switchManage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ac.setPermissionManage(isChecked);
            accessControlChangedListener.updateAccessControl(ac);
        });

        holder.switchShare.setChecked(ac.isPermissionShare());
        holder.switchShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ac.setPermissionShare(isChecked);
            accessControlChangedListener.updateAccessControl(ac);
        });
    }

    @Override
    public int getItemCount() {
        return accessControls.size();
    }

    static class ActivitiesViewHolder extends RecyclerView.ViewHolder {
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
