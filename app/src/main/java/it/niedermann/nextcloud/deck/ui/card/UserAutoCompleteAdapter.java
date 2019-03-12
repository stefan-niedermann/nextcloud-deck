package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.SupportUtil;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class UserAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<User> userList = new ArrayList<>();
    private SyncManager syncManager;
    private long accountId;
    private LifecycleOwner owner;

    public UserAutoCompleteAdapter(@NonNull LifecycleOwner owner, Context context, long accountId) {
        this.owner = owner;
        this.context = context;
        this.accountId = accountId;
        syncManager = new SyncManager(context,null);
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
        // TODO implement proper butterknife implementation
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_dropdown_item_singleline, parent, false);
        }

        // TODO duplicated from CardDetailsFragment, to be centralized
        try {
            SingleSignOnAccount account =  SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            String baseUrl = account.url;
            int px = SupportUtil.getAvatarDimension(context);
            String uri = baseUrl + "/index.php/avatar/" + Uri.encode(getItem(position).getUid()) + "/" + px;
            ImageView avatar = (ImageView) convertView.findViewById(R.id.user_avatar);
            Glide.with(context)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar);
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            DeckLog.logError(e);
        } catch (NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }

        ((TextView) convertView.findViewById(R.id.user_displayname)).setText(getItem(position).getDisplayname());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    ((Fragment)owner).getActivity().runOnUiThread(() -> {
                        LiveData<List<User>> userLiveData = syncManager.searchUserByUidOrDisplayName(accountId, constraint.toString());
                        Observer<List<User>> observer = new Observer<List<User>>() {
                            @Override
                            public void onChanged(List<User> users) {
                                userLiveData.removeObserver(this);
                                if (users != null) {
                                    filterResults.values = users;
                                    filterResults.count = users.size();
                                    publishResults(constraint, filterResults);
                                }
                            }
                        };
                        userLiveData.observe(owner, observer);
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    userList = (List<User>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
    }
}
