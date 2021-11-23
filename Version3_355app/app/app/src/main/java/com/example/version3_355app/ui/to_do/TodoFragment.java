package com.example.version3_355app.ui.to_do;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.version3_355app.databinding.FragmentToDoBinding;

public class TodoFragment extends Fragment {

    private FragmentToDoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TodoViewModel todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        binding = FragmentToDoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTodo;
        todoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}