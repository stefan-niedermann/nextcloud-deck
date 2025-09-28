package it.niedermann.nextcloud.deck.feature.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.feature.about.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(AboutFragment.class.getName());

    private AboutViewModel vm;
    private FragmentAboutBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thread.currentThread().setUncaughtExceptionHandler(new it.niedermann.nextcloud.deck.feature_shared.exception.ExceptionHandler(requireActivity()));
        vm = new ViewModelProvider(requireActivity()).get(AboutViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        binding.setVariable(BR.vm, vm);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();

    }

    @Override
    public void onDestroy() {
        binding = null;
        vm = null;
        super.onDestroy();
    }
}