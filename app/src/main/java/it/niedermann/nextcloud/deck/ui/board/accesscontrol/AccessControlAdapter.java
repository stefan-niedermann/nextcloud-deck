package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccessControlBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAccessControlOwnerBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

public class AccessControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Themed {

    public static final long HEADER_ITEM_LOCAL_ID = -1L;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @NonNull
    private ThemeUtils utils;

    @NonNull
    private final Account account;
    @NonNull
    private final List<AccessControl> accessControls = new LinkedList<>();
    @NonNull
    private final AccessControlChangedListener accessControlChangedListener;
    @NonNull
    private final Context context;
    private boolean hasManagePermission = false;

    AccessControlAdapter(@NonNull Account account, @NonNull AccessControlChangedListener accessControlChangedListener, @NonNull Context context) {
        this.account = account;
        this.accessControlChangedListener = accessControlChangedListener;
        this.context = context;
        this.utils = ThemeUtils.of(ContextCompat.getColor(context, R.color.primary), context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (accessControls.size() > position) {
            return accessControls.get(position).getLocalId();
        }
        throw new NoSuchElementException("Current list contains only " + accessControls.size() + " elements.");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            final ItemAccessControlOwnerBinding binding = ItemAccessControlOwnerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new OwnerViewHolder(binding);
        } else if (viewType == TYPE_ITEM) {
            final ItemAccessControlBinding binding = ItemAccessControlBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new AccessControlViewHolder(binding);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AccessControl ac = accessControls.get(position);
        switch (getItemViewType(position)) {
            case TYPE_HEADER: {
                final var ownerHolder = (OwnerViewHolder) holder;
                ownerHolder.binding.owner.setText(ac.getUser().getDisplayname());
                Glide.with(ownerHolder.binding.avatar.getContext())
                        .load(account.getAvatarUrl(ownerHolder.binding.avatar.getResources().getDimensionPixelSize(R.dimen.avatar_size), ac.getUser().getUid()))
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_person_24dp)
                        .error(R.drawable.ic_person_24dp)
                        .into(ownerHolder.binding.avatar);
                break;
            }
            case TYPE_ITEM:
            default: {
                final var acHolder = (AccessControlViewHolder) holder;
                Glide.with(acHolder.binding.avatar.getContext())
                        .load(account.getAvatarUrl(acHolder.binding.avatar.getResources().getDimensionPixelSize(R.dimen.avatar_size), ac.getUser().getUid()))
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_person_24dp)
                        .error(R.drawable.ic_person_24dp)
                        .into(acHolder.binding.avatar);

                acHolder.binding.username.setText(ac.getUser().getDisplayname());
                acHolder.binding.username.setCompoundDrawables(null, null, ac.getStatus() == DBStatus.LOCAL_EDITED.getId()
                        ? ContextCompat.getDrawable(context, R.drawable.ic_sync_18dp) : null, null);
                acHolder.binding.delete.setOnClickListener((v) -> accessControlChangedListener.deleteAccessControl(ac));

                if (hasManagePermission) {
                    acHolder.binding.permissionEdit.setVisibility(View.VISIBLE);
                    acHolder.binding.permissionEdit.setChecked(ac.isPermissionEdit());
                    acHolder.binding.permissionEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        ac.setPermissionEdit(isChecked);
                        accessControlChangedListener.updateAccessControl(ac);
                    });

                    acHolder.binding.permissionManage.setVisibility(View.VISIBLE);
                    acHolder.binding.permissionManage.setChecked(ac.isPermissionManage());
                    acHolder.binding.permissionManage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        ac.setPermissionManage(isChecked);
                        accessControlChangedListener.updateAccessControl(ac);
                        acHolder.binding.username.setCompoundDrawables(null, null, null, null);
                    });
                } else {
                    acHolder.binding.permissionEdit.setVisibility(View.GONE);
                    acHolder.binding.permissionManage.setVisibility(View.GONE);
                }

                acHolder.binding.permissionShare.setChecked(ac.isPermissionShare());
                acHolder.binding.permissionShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    ac.setPermissionShare(isChecked);
                    accessControlChangedListener.updateAccessControl(ac);
                });

                if (hasManagePermission) {
                    utils.androidx.colorSwitchCompat(acHolder.binding.permissionEdit);
                    utils.androidx.colorSwitchCompat(acHolder.binding.permissionManage);
                }
                utils.androidx.colorSwitchCompat(acHolder.binding.permissionShare);
                break;
            }
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

    public void remove(AccessControl ac) {
        final int index = this.accessControls.indexOf(ac);
        if (this.accessControls.remove(ac)) {
            notifyItemRemoved(index);
        }
    }

    public void update(@NonNull List<AccessControl> accessControls, boolean hasManagePermission) {
        this.accessControls.clear();
        this.accessControls.addAll(accessControls);
        this.hasManagePermission = hasManagePermission;
        notifyDataSetChanged();
    }

    @Override
    public void applyTheme(int color) {
        this.utils = ThemeUtils.of(color, context);
        notifyDataSetChanged();
    }
}
