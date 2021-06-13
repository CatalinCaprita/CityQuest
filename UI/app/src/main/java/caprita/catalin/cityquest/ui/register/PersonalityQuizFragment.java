package caprita.catalin.cityquest.ui.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import dagger.android.support.DaggerFragment;


public class PersonalityQuizFragment extends DaggerFragment {


    /*Fragment Container*/
    private FragmentContainerView fragmentContainerView;
    private FragmentManager fragmentManager;

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;

    public PersonalityQuizFragment() {
        // Required empty public constructor
    }

    public static PersonalityQuizFragment newInstance(String param1, String param2) {
        PersonalityQuizFragment fragment = new PersonalityQuizFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personality_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentContainerView = view.findViewById(R.id.fragment_question_container);
        fragmentManager = getChildFragmentManager();
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(RegisterViewModel.class);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        Bundle bundle = new Bundle();
        bundle.putInt(QuestionChildFragment.QUESTION_ID_KEY, 0);
        fragmentManager.beginTransaction()
//                .setPrimaryNavigationFragment(this)
                .replace(R.id.fragment_question_container, QuestionChildFragment.class,bundle,null)
                .addToBackStack(null)
                .commit();
    }
}