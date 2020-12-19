package com.retrytech.vilo.view.base;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.retrytech.vilo.utils.SessionManager;

import org.jetbrains.annotations.NotNull;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {


    private Context context;
    public SessionManager sessionManager;

    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        if (context != null) {
            sessionManager = new SessionManager(context);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }


}
