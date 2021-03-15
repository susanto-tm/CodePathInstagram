package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.instagram.databinding.LogoutFragmentBinding;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LogoutFragment extends Fragment {

    private LogoutFragmentBinding binding;
    private Button btnLogout;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.logout_fragment, container, false);

        btnLogout = binding.btnLogout;

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(context, "Unable to logout", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(MainActivity.TAG, "Logging out: " + ParseUser.getCurrentUser());
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
