package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.instagram.databinding.PostFragmentBinding;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PostFragment extends Fragment {
    private final String TAG = "PostFragment";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    PostFragmentBinding binding;
    private EditText etCaption;
    private Button btnTakePicture;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private Context context;

    private Snackbar snackbarError;
    private Snackbar snackbarDescription;
    private Snackbar snackbarSubmitted;

    private CoordinatorLayout mainCoordinatorLayout;

    private ProgressBar prgBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.post_fragment, container, false);

        etCaption = binding.etCaption;
        btnTakePicture = binding.btnTakePicture;
        ivPostImage = binding.ivPostImage;
        btnSubmit = binding.btnSubmit;
        prgBar = binding.submitProgress;

        mainCoordinatorLayout = getActivity().findViewById(R.id.mainCoordinatorLayout);

        snackbarError = Snackbar.make(mainCoordinatorLayout, "Description can't be empty", Snackbar.LENGTH_SHORT);
        snackbarDescription = Snackbar.make(mainCoordinatorLayout, "There is no image", Snackbar.LENGTH_SHORT);
        snackbarSubmitted = Snackbar.make(mainCoordinatorLayout, "Post uploaded", Snackbar.LENGTH_SHORT);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String caption = etCaption.getText().toString();
                if (caption.isEmpty()) {
                    snackbarError.show();
                    return;
                }
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    snackbarDescription.show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(caption, currentUser, photoFile);
            }
        });

        launchCamera();

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(context,"com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        Log.i(TAG, "ResolveActivity: " + intent.resolveActivity(context.getPackageManager()));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            }
        }
    }

    public File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);

    }


    public void savePost(String caption, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(caption);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);
        prgBar.setVisibility(View.VISIBLE);

        Runnable prgRun = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    waitPrg();
                    final int prg = i;
                    prgBar.post(new Runnable() {

                        @Override
                        public void run() {
                            prgBar.setProgress(prg);
                        }
                    });
                }
            }
        };
        Thread prgThread = new Thread(prgRun);
        prgThread.start();

        post.saveInBackground(new SaveCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }
                prgThread.interrupt();
                prgBar.setProgress(prgBar.getMax(), false);
                prgBar.setVisibility(View.GONE);
                snackbarSubmitted.show();
                Log.i(TAG, "Success!");
                etCaption.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }

    private void waitPrg() {
        SystemClock.sleep(10);
    }
}
