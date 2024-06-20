package com.youtube.fizantofuzz.Dialog;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.youtube.fizantofuzz.Activity.HomeActivity;
import com.youtube.fizantofuzz.R;
import java.util.HashMap;

public class CreateAccountDialog extends DialogFragment{
    private TextInputEditText email, password, name;
    private TextView signup;
    private ImageView image;
    Uri uri, myImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;
    private Uri imageUri;
    private static final int IMAGE_REQUEST = 1;
    private FirebaseAuth mauth;
    String mUri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    DatabaseReference base;
    FirebaseStorage storage;
    StorageReference reference;
    float v = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.screen_background, typedValue,true);
        getActivity().getWindow().setStatusBarColor(typedValue.data);

        email = view.findViewById(R.id.emailsign);
        password = view.findViewById(R.id.passwordsign);
        name = view.findViewById(R.id.namesign);
        mUri = "default";
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        signup = view.findViewById(R.id.signup);
        image = view.findViewById(R.id.imagesign);
        mauth = FirebaseAuth.getInstance();
        myImageUri = Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/default_profile_picture");

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri ->{
            if (uri != null){
                imageUri = uri;
                image.setImageURI(uri);
                uploadImage();
            }
        });

        MaterialButton existing_user = view.findViewById(R.id.existing_user);
        existing_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existing_user.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                dismiss();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email_str = email.getText().toString();
                final String password_str = password.getText().toString();
                final String name_str = name.getText().toString();

                if(email_str.isEmpty() || password_str.isEmpty() || name_str.isEmpty()){
                    Toast.makeText(getContext(), "Something went wrong. Please check your details", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6) {
                    Toast.makeText(getContext(), "Password must be at least 6 characters long", Toast.LENGTH_LONG).show();
                } else{
                    signup.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    CreateUserAccount();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                openImage();
            }
        });
    }

    private void openImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setView(R.layout.dialog_progress_pic)
                .setCancelable(false).create();

        AlertDialog materialDialogs = dialogBuilder.create();
        materialDialogs.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString();
                    }

                    else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                    materialDialogs.dismiss();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    materialDialogs.dismiss();
                }
            });
        }

        else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void CreateUserAccount() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setView(R.layout.dialog_progress_wait)
                .setCancelable(false).create();

        AlertDialog materialDialogs = dialogBuilder.create();
        materialDialogs.show();

        mauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    base = FirebaseDatabase.getInstance().getReference("Users").child(mauth.getCurrentUser().getUid());
                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", mauth.getCurrentUser().getUid());
                    hashMap.put("username", name.getText().toString());
                    hashMap.put("status", "offline");
                    hashMap.put("imageURL", "" + mUri);
                    hashMap.put("bio", "Hey there! I'm using Fizanto Fuzz.");
                    hashMap.put("search", name.getText().toString().toLowerCase());
                    hashMap.put("name", name.getText().toString());
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("verification", "no");
                    base.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()){
                                Intent home = new Intent(getActivity(), HomeActivity.class);
                                Toast.makeText(getContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                                materialDialogs.dismiss();
                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(home);
                                            getActivity().finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Sign in unsuccessful. " + task1.getException() + "", Toast.LENGTH_LONG).show();
                                materialDialogs.dismiss();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Sign in unsuccessful. " + task.getException() + "", Toast.LENGTH_LONG).show();
                    materialDialogs.dismiss();
                }
            }
        });
    }
}
