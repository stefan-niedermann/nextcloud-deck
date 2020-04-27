package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccessControlBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAccessControlOwnerBinding;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.ui.branding.BrandedActivity.getSecondaryForegroundColorDependingOnTheme;

public class AccessControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Branded {

    public static final long HEADER_ITEM_LOCAL_ID = -1L;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private int mainColor;

    @NonNull
    private List<AccessControl> accessControls = new LinkedList<>();
    @NonNull
    private AccessControlChangedListener accessControlChangedListener;
    @NonNull
    private Context context;

    AccessControlAdapter(@NonNull AccessControlChangedListener accessControlChangedListener, @NonNull Context context) {
        super();
        this.accessControlChangedListener = accessControlChangedListener;
        this.context = context;
        this.mainColor = context.getResources().getColor(R.color.primary);
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
                final OwnerViewHolder ownerHolder = (OwnerViewHolder) holder;
                ownerHolder.binding.owner.setText(ac.getUser().getDisplayname());

                try {
                    ViewUtil.addAvatar(ownerHolder.binding.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TYPE_ITEM:
            default: {
                final AccessControlViewHolder acHolder = (AccessControlViewHolder) holder;

                try {
                    ViewUtil.addAvatar(acHolder.binding.avatar, SingleAccountHelper.getCurrentSingleSignOnAccount(context).url, ac.getUser().getUid(), R.drawable.ic_person_grey600_24dp);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                }

                acHolder.binding.username.setText(ac.getUser().getDisplayname());
                acHolder.binding.username.setCompoundDrawables(null, null, ac.getStatus() == DBStatus.LOCAL_EDITED.getId() ? context.getResources().getDrawable(R.drawable.ic_sync_blue_24dp) : null, null);
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

                if (Application.isBrandingEnabled(context)) {
                    brandSwitch(context, acHolder.binding.permissionEdit, mainColor);
                    brandSwitch(context, acHolder.binding.permissionManage, mainColor);
                    brandSwitch(context, acHolder.binding.permissionShare, mainColor);
                }
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

    public void update(@NonNull List<AccessControl> accessControls) {
        this.accessControls.clear();
        this.accessControls.addAll(accessControls);
        notifyDataSetChanged();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        if (Application.isBrandingEnabled(context)) {
            this.mainColor = BrandedActivity.getSecondaryForegroundColorDependingOnTheme(context, mainColor);
            notifyDataSetChanged();
        }
    }

    /**
     * Helper method to apply branding to a {@link SwitchCompat}
     */
    private static void brandSwitch(@NonNull Context context, @NonNull SwitchCompat switchCompat, @ColorInt int mainColor) {
        final int finalMainColor = getSecondaryForegroundColorDependingOnTheme(context, mainColor);
        DrawableCompat.setTintList(switchCompat.getThumbDrawable(), new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{finalMainColor, context.getResources().getColor(R.color.fg_secondary)}
        ));
        final int trackColor = context.getResources().getColor(R.color.fg_secondary);
        final int lightTrackColor = Color.argb(77, Color.red(trackColor), Color.green(trackColor), Color.blue(trackColor));
        final int lightTrackColorChecked = Color.argb(77, Color.red(finalMainColor), Color.green(finalMainColor), Color.blue(finalMainColor));
        DrawableCompat.setTintList(switchCompat.getTrackDrawable(), new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{lightTrackColorChecked, lightTrackColor}
        ));
    }
}
