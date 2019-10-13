package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class UserAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context activity;
    private List<User> userList = new ArrayList<>();
    private SyncManager syncManager;
    private long accountId;
    private long cardId;
    private LifecycleOwner owner;

    public UserAutoCompleteAdapter(@NonNull LifecycleOwner owner, Activity activity, long accountId, long cardId) {
        this.owner = owner;
        this.activity = activity;
        this.accountId = accountId;
        this.cardId = cardId;
        syncManager = new SyncManager(activity);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dropdown_item_singleline, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        try {
            SingleSignOnAccount account = SingleAccountHelper.getCurrentSingleSignOnAccount(activity);
            ViewUtil.addAvatar(
                    activity,
                    holder.icon,
                    account.url,
                    getItem(position).getUid(),
                    R.drawable.ic_person_grey600_24dp
            );
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            DeckLog.logError(e);
        } catch (NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }

        holder.label.setText(getItem(position).getDisplayname());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            final FilterResults filterResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    Objects.requireNonNull(((Fragment) owner).getActivity()).runOnUiThread(() -> {
                        LiveData<List<User>> liveData = constraint.length() > 0
                                ? syncManager.searchUserByUidOrDisplayName(accountId, constraint.toString())
                                : syncManager.findProposalsForUsersToAssign(accountId, cardId, 3);
                        observeOnce(liveData, owner, users -> {
                            if (users != null) {
                                filterResults.values = users;
                                filterResults.count = users.size();
                                publishResults(constraint, filterResults);
                            } else {
                                filterResults.values = new ArrayList<>();
                                filterResults.count = 0;
                            }
                        });
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    if (!userList.equals(results.values)) {
                        userList = (List<User>) results.values;
                    }
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    static class ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.label)
        TextView label;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
