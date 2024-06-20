package com.youtube.fizantofuzz.Dialog;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.youtube.fizantofuzz.R;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class AddStoryFeedDialog extends DialogFragment{
    TextInputEditText title, date, url;
    ImageView thumbnail, add_feed_back;
    Button add_feed_upload;
    MaterialCardView thumbnail_parent;
    StorageReference storageReference;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    DatabaseReference reference;
    Boolean inside;
    String mUri, parent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_story_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title = view.findViewById(R.id.add_title);
        date = view.findViewById(R.id.add_date);
        url = view.findViewById(R.id.add_body);
        thumbnail = view.findViewById(R.id.add_thumbnail);
        add_feed_upload = view.findViewById(R.id.add_feed_upload);
        add_feed_back = view.findViewById(R.id.add_feed_back);
        thumbnail_parent = view.findViewById(R.id.thumbnail_parent);

        inside = getArguments().getBoolean("feed_inside");

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                thumbnail.setImageURI(uri);
                imageUri = uri;
                uploadImage();
            }
        });

        if (inside) {
            MaterialToolbar feed_bar = view.findViewById(R.id.toolbar_add_feed);
            feed_bar.setTitle("Update Story");
            String title_str = getArguments().getString("title");
            String url_str = getArguments().getString("Urlofv");
            String date_str = getArguments().getString("date");
            String image_str = getArguments().getString("pic");
            imageUri = Uri.parse(image_str);
            mUri = image_str;
            parent = getArguments().getString("parent");

            title.setText(title_str);
            date.setText(date_str);
            url.setText(url_str);
            Glide.with(getActivity()).load(image_str).into(thumbnail);

        }

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker dialogs = MaterialDatePicker.Builder.datePicker().setTitleText("Select Publish Date").build();
                dialogs.show(getActivity().getSupportFragmentManager(), "tag");
                dialogs.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(selection.toString()));
                        String myDate = format.format(calendar.getTime());
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
                            myDate = myDate.replace("/", "-") + "T00:00:00";
                            LocalDateTime dateTime = LocalDateTime.parse(myDate, formatter);
                            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                            date.setText(dateTime.format(formatter1));
                        } else {
                            date.setText(myDate);
                        }
                    }
                });
            }
        });

        add_feed_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_feed_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                dismiss();
            }
        });

        add_feed_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String time = -new Date().getTime() + "";
                add_feed_upload.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                if (!inside) {
                    if (!title.getText().toString().equals("") && !date.getText().toString().equals("") && !url.getText().toString().equals("") && imageUri != null) {
                        reference = FirebaseDatabase.getInstance().getReference("Stories").child(time);

                        final HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("url", url.getText().toString());
                        hashMap.put("date", date.getText().toString());
                        hashMap.put("pic", mUri);
                        hashMap.put("title", title.getText().toString());
                        hashMap.put("parent", time);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Enter details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!title.getText().toString().equals("") && !date.getText().toString().equals("") && !url.getText().toString().equals("") && imageUri != null) {
                        reference = FirebaseDatabase.getInstance().getReference("Stories").child(parent);

                        final HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("url", url.getText().toString());
                        hashMap.put("date", date.getText().toString());
                        hashMap.put("pic", mUri);
                        hashMap.put("title", title.getText().toString());
                        hashMap.put("parent", parent);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Enter details", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        thumbnail_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbnail_parent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                openImage();
            }
        });
    }

    private void openImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {

        if (imageUri != null) {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
            dialogBuilder.setView(R.layout.dialog_progress_pic)
                    .setCancelable(false).create();

            AlertDialog pd = dialogBuilder.create();
            pd.show();

            storageReference = FirebaseStorage.getInstance().getReference("Feeds");
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
