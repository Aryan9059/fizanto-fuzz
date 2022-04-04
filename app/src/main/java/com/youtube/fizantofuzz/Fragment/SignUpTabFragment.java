package com.youtube.fizantofuzz.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Activity.HomeActivity;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.pierry.progress.Progress;
import static android.app.Activity.RESULT_OK;

public class SignUpTabFragment extends Fragment {

    private TextInputEditText email, password, name, username;
    private TextView signup;
    private CircleImageView image;
    Uri uri, myimageuri;
    private StorageTask uploadTask;
    StorageReference storageReference;
    private Uri imageUri;
    private static final int IMAGE_REQUEST = 1;
    private Progress dialoglogin;
    private FirebaseAuth mauth;
    String mUri;
    DatabaseReference mbase;
    FirebaseStorage storage;
    StorageReference reference;
    float v = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sign_up_fragment, container, false);

        email = root.findViewById(R.id.emailsign);
        password = root.findViewById(R.id.passwordsign);
        name = root.findViewById(R.id.namesign);
        mUri = "default";
        username = root.findViewById(R.id.usersign);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        signup = root.findViewById(R.id.signup);
        image = root.findViewById(R.id.imagesign);
        mauth = FirebaseAuth.getInstance();
        myimageuri = Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/picsign");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String semail = email.getText().toString();
                final String spassword = password.getText().toString();
                final String susername = username.getText().toString();
                final String sname = name.getText().toString();

                if(semail.isEmpty() || spassword.isEmpty() || sname.isEmpty() || susername.isEmpty()){
                    Toast.makeText(getContext(), "Something went wrong. Please check your details", Toast.LENGTH_LONG).show();
                    } else if (password.length() < 6) {
                    Toast.makeText(getContext(), "Password must be at least 6 characters long", Toast.LENGTH_LONG).show();
                } else{
                    CreateUserAccount();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        return root;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setIndeterminateDrawable(getResources().getDrawable(R.drawable.ic_picture));

        pd.setMessage("Uploading...");
        pd.show();

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
                    pd.dismiss();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }

        else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void CreateUserAccount() {
        dialoglogin = new Progress(getActivity());
        dialoglogin.light("Signing you up!");
        mauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mbase = FirebaseDatabase.getInstance().getReference("Users").child(mauth.getCurrentUser().getUid());
                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", mauth.getCurrentUser().getUid());
                    hashMap.put("username", name.getText().toString());
                    hashMap.put("status", "offline");
                    hashMap.put("imageURL", ""+mUri);
                    hashMap.put("bio", username.getText().toString());
                    hashMap.put("search", name.getText().toString().toLowerCase());
                    hashMap.put("name", name.getText().toString());
                    hashMap.put("email", email.getText().toString());
                    mbase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()){
                                Intent home = new Intent(getActivity(), HomeActivity.class);
                                Toast.makeText(getContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                                dialoglogin.dismiss();
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
                                dialoglogin.dismiss();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "Sign in unsuccessful. " + task.getException() + "", Toast.LENGTH_LONG).show();
                    dialoglogin.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            image.setImageURI(imageUri);

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }

            else {
                uploadImage();
            }
        }
    }
}
