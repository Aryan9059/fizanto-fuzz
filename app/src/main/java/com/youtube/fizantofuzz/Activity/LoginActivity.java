package com.youtube.fizantofuzz.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.youtube.fizantofuzz.Dialog.CreateAccountDialog;
import com.youtube.fizantofuzz.R;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences prefs;
    private TextInputEditText email, password;
    private TextView forgot;
    private MaterialButton login, create;
    private FirebaseAuth mauth;

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Themes", MODE_PRIVATE);
        String themeMode = prefs.getString("current", "");
        switch (themeMode){
            case "":
            case "Main":
                setTheme(R.style.Theme_Main);
                break;
            case "Blue":
                setTheme(R.style.Theme_Blue);
                break;
            case "Yellow":
                setTheme(R.style.Theme_Yellow);
                break;
            case "Pink":
                setTheme(R.style.Theme_Pink);
                break;
            case "Green":
                setTheme(R.style.Theme_Green);
                break;
            case "Teal":
                setTheme(R.style.Theme_Teal);
                break;
            case "Purple":
                setTheme(R.style.Theme_Purple);
                break;
            case "Red":
                setTheme(R.style.Theme_Red);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.screen_background, typedValue,true);
        getWindow().setStatusBarColor(typedValue.data);

        email = findViewById(R.id.emaillog);
        password = findViewById(R.id.passwordlog);
        login = findViewById(R.id.sign_in_btn);
        forgot = findViewById(R.id.forgot_password_btn);

        create = findViewById(R.id.supporter_btn);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                DialogFragment dialog = new CreateAccountDialog();
                dialog.show(getSupportFragmentManager(), "create");
            }
        });

        mauth = FirebaseAuth.getInstance();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email_str = email.getText().toString();
                if(email_str.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter your Email", Toast.LENGTH_SHORT).show();
                } else {
                    mauth.sendPasswordResetEmail(email_str).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                create.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                                alertDialog.setTitle("Reset Password");
                                alertDialog.setMessage("A Reset Password mail has been sent to your Email Address. Please Reset your Password by following the instructions given in the respective mail.");
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email_str = email.getText().toString();
                final String password_str = password.getText().toString();

                if(email_str.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter your Email", Toast.LENGTH_SHORT).show();
                } else if (password_str.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter your Password", Toast.LENGTH_SHORT).show();
                } else if (email_str.isEmpty() && password_str.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter your Credentials", Toast.LENGTH_SHORT).show();
                } else{
                    login.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    SignIn();
                }
            }
        });
    }

    private void SignIn() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(LoginActivity.this);
        dialogBuilder.setView(R.layout.dialog_progress_wait)
                .setCancelable(false).create();

        AlertDialog materialDialogs = dialogBuilder.create();
        materialDialogs.show();

        mauth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        materialDialogs.dismiss();

                        if(task.isSuccessful()){
                            Intent intent = new Intent (LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else{
                            Log.e("ERROR",task.getException().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage() + "", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}




