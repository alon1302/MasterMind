package com.example.mastermind;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mastermind.model.listeners.ImageUploadListener;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.activities.HomeActivity;
import com.example.mastermind.ui.activities.HowToPlayActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements ImageUploadListener {

    private static final String TAG = "FacebookAuthentication";

    static int PICK_IMAGE = 100;
    static int GOOGLE = 1;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Dialog register_dialog;
    Dialog login_dialog;

    String uName;

    private GoogleSignInClient mGoogleSignInClient;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (currentUser != null) {
            openHomeActivity();
        }

        createDialogs();

        Button btn_howToPlay = findViewById(R.id.btn_howToPlay);
        btn_howToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), HowToPlayActivity.class);
               startActivity(intent);
            }
        });
        SignInButton btn_google = findViewById(R.id.signInButton);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

//    public void onClickDebug(View view) {
//        Intent intent = new Intent(this, OnePlayerActivity.class);
//        startActivity(intent);
//    }

    //////////////////////////////// Google ////////////////////////////////////
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            myRef.child("Users").child(currentUser.getUid()).setValue(CurrentUser.getInstance());
                            Toast.makeText(getApplicationContext(), "Authentication succeed.", Toast.LENGTH_SHORT).show();
                            openHomeActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE);
    }

    //////////////////////// Email //////////////////////////////////
    private void signUpWithEmail(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            uName = name;
                            uploadProfilePhoto(imageUri, currentUser.getUid());
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onImageUploaded() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(uName).build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication succeed", Toast.LENGTH_SHORT).show();
                            CurrentUser.logout();
                            myRef.child("Users").child(currentUser.getUid()).setValue(CurrentUser.getInstance());
                            openHomeActivity();
                        }
                    }
                });
    }

    private void uploadProfilePhoto(Uri imageUri, final String userId) {
        final ProgressDialog progressDialogUpload = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog);
        progressDialogUpload.setTitle("Uploading Please Wait....");
        progressDialogUpload.show();
        mStorageRef.child(userId).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mStorageRef.child(userId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(task.getResult().toString()))
                                        .build();

                                currentUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Photo Upload Succeed", Toast.LENGTH_SHORT).show();
                                                    progressDialogUpload.dismiss();
                                                    ImageUploadListener imageUploadListener = (ImageUploadListener) MainActivity.this;
                                                    imageUploadListener.onImageUploaded();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(MainActivity.this, "Photo Wont Upload", Toast.LENGTH_SHORT).show();
                                progressDialogUpload.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void onClickRegister(View view) {
        EditText register_et_Name = register_dialog.findViewById(R.id.register_et_Name);
        EditText register_et_Mail = register_dialog.findViewById(R.id.register_et_Mail);
        EditText register_et_Pass = register_dialog.findViewById(R.id.register_et_Pass);
        EditText register_et_conPass = register_dialog.findViewById(R.id.register_et_conPass);
        String name = register_et_Name.getText().toString();
        String pass = register_et_Pass.getText().toString();
        String email = register_et_Mail.getText().toString();
        String conPass = register_et_conPass.getText().toString();
        if (!name.equals("") && !pass.equals("") && !email.equals("") && !conPass.equals(""))
            if (pass.equals(conPass))
                signUpWithEmail(name, email, pass);
            else
                Toast.makeText(this, "Passwords Not Identical", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Please Type All Fields", Toast.LENGTH_SHORT).show();
    }

    public void onClickLogin(View view) {
        EditText login_et_Mail = login_dialog.findViewById(R.id.login_et_Mail);
        EditText login_et_Pass = login_dialog.findViewById(R.id.login_et_Pass);
        String email = login_et_Mail.getText().toString();
        String pass = login_et_Pass.getText().toString();
        if (!email.equals("") && !pass.equals(""))
            loginUser(email, pass);
        else
            Toast.makeText(this, "Please Type All Fields", Toast.LENGTH_SHORT).show();
    }

    public void onClickDismiss(View view) {
        login_dialog.dismiss();
        register_dialog.dismiss();
    }

    private void loginUser(String email, String password) {
        final ProgressDialog d = new ProgressDialog(this);
        d.setTitle("wait for login");
        d.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Authentication succeed.", Toast.LENGTH_SHORT).show();
                    login_dialog.dismiss();
                    openHomeActivity();
                } else
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        });
    }

    private void openHomeActivity() {
        Log.d(TAG, "openHomeActivity: " + CurrentUser.getInstance());
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void createDialogs() {
        login_dialog = new Dialog(this);
        login_dialog.setContentView(R.layout.dialog_login);
        login_dialog.setCancelable(true);
        register_dialog = new Dialog(this);
        register_dialog.setContentView(R.layout.dialog_register);
        register_dialog.setCancelable(true);
    }

    public void createLoginDialog(View view) {
        login_dialog.show();
    }

    public void createRegisterDialog(View view) {
        register_dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                CircleImageView iv = register_dialog.findViewById(R.id.register_iv_image);
                iv.setImageURI(data.getData());
                imageUri = data.getData();
            }
        }
        if (requestCode == GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Authentication failed, Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
}