package it.niedermann.nextcloud.deck.ui.theme;

import androidx.fragment.app.DialogFragment;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.repository.BaseRepository;

public abstract class ThemedDialogFragment extends DialogFragment implements Themed {

    @Override
    public void onStart() {
        super.onStart();

        final var baseRepository = new BaseRepository(requireContext());

        new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .combineWith(baseRepository::getCurrentBoardId$)
                .flatMap(ids -> baseRepository.getBoardColor$(ids.first, ids.second))
                .observe(this, this::applyTheme);
    }
}
