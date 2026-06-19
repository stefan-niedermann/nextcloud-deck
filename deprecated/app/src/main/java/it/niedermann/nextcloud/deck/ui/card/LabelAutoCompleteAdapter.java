package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteLabelBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;

public class LabelAutoCompleteAdapter extends AutoCompleteAdapter<Label> {

    @NonNull
    protected final Context context;
    @ColorInt
    private final int createLabelColor;
    private final ReactiveLiveData<Boolean> canManage$;

    public LabelAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId, long cardId) throws NextcloudFilesAppAccountNotFoundException {
        super(activity, account, boardId);
        this.context = activity;
        final String[] colors = activity.getResources().getStringArray(R.array.board_default_colors);
        createLabelColor = Color.parseColor(colors[new Random().nextInt(colors.length)]);

        canManage$ = new ReactiveLiveData<>(syncRepository.getFullBoardById(account.getId(), boardId))
                .map(FullBoard::getBoard)
                .map(Board::isPermissionManage);

        constraint$
                .flatMap(constraint -> TextUtils.isEmpty(constraint)
                        ? syncRepository.findProposalsForLabelsToAssign(account.getId(), boardId, cardId)
                        : syncRepository.searchNotYetAssignedLabelsByTitle(account, boardId, cardId, constraint))
                .map(this::filterExcluded)
                .flatMap(this::addCreateLabelIfNeeded)
                .distinctUntilChanged()
                .observe(activity, this::publishResults);
    }

    private ReactiveLiveData<List<Label>> addCreateLabelIfNeeded(@NonNull List<Label> labels) {
        return canManage$
                .combineWith(() -> constraint$)
                .map(args -> {
                    final var canManage = args.first;
                    final var constraint = args.second;
                    if (canManage && !TextUtils.isEmpty(constraint) && !labelTitleIsPresent(labels, constraint)) {
                        labels.add(createLabel(constraint));
                    }
                    return labels;
                });
    }

    private boolean labelTitleIsPresent(@NonNull Collection<Label> labels, @NonNull CharSequence title) {
        return labels.stream().map(Label::getTitle).anyMatch(title::equals);
    }

    @NonNull
    private Label createLabel(String title) {
        final var label = new Label();
        label.setLocalId(null);
        label.setBoardId(boardId);
        label.setAccountId(account.getId());
        label.setTitle(title);
        label.setColor(createLabelColor);
        return label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemAutocompleteLabelBinding binding;

        if (convertView != null) {
            binding = ItemAutocompleteLabelBinding.bind(convertView);
        } else {
            binding = ItemAutocompleteLabelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        final var label = getItem(position);
        final int labelColor = label.getColor();
        final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);

        if (label.getLocalId() == null) {
            binding.label.setText(String.format(context.getString(R.string.label_add, label.getTitle())));
        } else {
            binding.label.setText(label.getTitle());
        }
        binding.label.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
        binding.label.setTextColor(color);

        if (label.getLocalId() == null) {
            final var plusIcon = DrawableCompat.wrap(ContextCompat.getDrawable(binding.label.getContext(), R.drawable.ic_plus));
            DrawableCompat.setTint(plusIcon, color);
            binding.label.setChipIcon(plusIcon);
        } else {
            binding.label.setChipIcon(null);
        }

        return binding.getRoot();
    }
}
