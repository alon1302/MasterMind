package com.example.mastermind.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.ImageUploadListener;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.serviceAndBroadcast.NetworkChangeReceiver;
import com.example.mastermind.model.user.CurrentUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements ImageUploadListener, MethodCallBack {

    private static final int PICK_IMAGE = 100;
    private static final int GOOGLE = 1;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Dialog register_dialog;
    Dialog login_dialog;

    String uName;

    private GoogleSignInClient mGoogleSignInClient;

    Uri imageUri;

    ProgressDialog progressDialogUpload;

    Dialog offlineDialog;
    Button btnOfflineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialogUpload = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog);

        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (currentUser != null)
            openHomeActivity();

        createDialogs();

        Button btn_howToPlay = findViewById(R.id.btn_howToPlay);
        btn_howToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HowToPlayActivity.class);
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

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(this);
        registerReceiver(networkChangeReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    //////////////////////////////// Google ////////////////////////////////////
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            addOrUpdateUser(currentUser);
                            Toast.makeText(getApplicationContext(), "Authentication succeed", Toast.LENGTH_SHORT).show();
                            openHomeActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void addOrUpdateUser(final FirebaseUser user) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Const.USER_NAME_IN_FIREBASE, user.getDisplayName());
        map.put(Const.USER_EMAIL_IN_FIREBASE, user.getEmail());
        map.put(Const.USER_ID_IN_FIREBASE, user.getUid());
        map.put(Const.USER_IMG_URL_IN_FIREBASE, user.getPhotoUrl().toString());
        FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(user.getUid()).updateChildren(map);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE);
    }

    //////////////////////// Email //////////////////////////////////
    private void signUpWithEmail(final String name, String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(Uri.parse("https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png"))
                                    .build();
                            if (user != null) {
                                user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        myRef.child(Const.USERS_IN_FIREBASE).child(mAuth.getCurrentUser().getUid()).setValue(CurrentUser.getInstance());
                                        if (imageUri != null)
                                            uploadProfileImage(CurrentUser.getInstance().getId(), imageUri);
                                        else
                                            openHomeActivity();
                                    }
                                });
                            }
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
                            Toast.makeText(LoginActivity.this, "Authentication succeed", Toast.LENGTH_SHORT).show();
                            CurrentUser.logout();
                            myRef.child(Const.USERS_IN_FIREBASE).child(currentUser.getUid()).setValue(CurrentUser.getInstance());
                            openHomeActivity();
                        }
                    }
                });
    }

    public void uploadProfileImage(final String id, Uri data) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child(Const.PROFILE_IMG_IN_STORAGE).child(id).putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.child(Const.PROFILE_IMG_IN_STORAGE).child(id).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(id).child(Const.USER_IMG_URL_IN_FIREBASE).setValue(task.getResult().toString());
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(task.getResult().toString()))
                                        .build();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null)
                                    user.updateProfile(profileUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            openHomeActivity();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            reference.child(Const.PROFILE_IMG_IN_STORAGE).child(id).delete();
                                        }
                                    });
                            } else
                                reference.child(Const.PROFILE_IMG_IN_STORAGE).child(id).delete();
                        }
                    });
                }
            }
        });
    }

    public void onClickRegister(View view) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Creating Your Account, Please Wait...");
        EditText displayET = register_dialog.findViewById(R.id.register_et_Name);
        EditText emailET = register_dialog.findViewById(R.id.register_et_Mail);
        EditText passET = register_dialog.findViewById(R.id.register_et_Pass);
        EditText passConfirmET = register_dialog.findViewById(R.id.register_et_conPass);
        String display = displayET.getText().toString();
        String email = emailET.getText().toString();
        String pass = passET.getText().toString();
        String passConfirm = passConfirmET.getText().toString();
        if (validateSignUp(display, email, pass, passConfirm)) {
            progressDialog.show();
            signUpWithEmail(display, email, pass);
        }
    }

    private boolean validateSignUp(String display, String email, String pass, String passConfirm) {
        if (display.matches("")) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.matches("") || email.indexOf('@') == -1 || email.indexOf('.') == -1 || email.charAt(0) == '@') {
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (pass.length() < 6) {
            Toast.makeText(this, "A Password Needs To Contained At Least 6 Characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pass.equals(passConfirm)) {
            Toast.makeText(this, "Passwords Are Not Identical", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                    Toast.makeText(getApplicationContext(), "Authentication succeed", Toast.LENGTH_SHORT).show();
                    login_dialog.dismiss();
                    openHomeActivity();
                } else
                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        });
    }

    public void openHomeActivity() {
        progressDialogUpload.dismiss();
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
        offlineDialog = new Dialog(this);
        offlineDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        offlineDialog.setContentView(R.layout.dialog_offline);
        btnOfflineMode = offlineDialog.findViewById(R.id.offlineModeBtn);
        offlineDialog.setCancelable(false);
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
        if (requestCode == PICK_IMAGE)
            if (resultCode == RESULT_OK) {
                CircleImageView iv = register_dialog.findViewById(R.id.register_iv_image);
                iv.setImageURI(data.getData());
                imageUri = data.getData();
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

    private void toggleIsOnline(int mode){
        if (mode == Const.ONLINE)
            offlineDialog.dismiss();
        else if (mode == Const.OFFLINE) {
            offlineDialog.show();
            btnOfflineMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, OnePlayerActivity.class);
                    intent.putExtra(Const.INTENT_EXTRA_KEY_IS_ONLINE, false);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onCallBack(int action, Object value) {
        toggleIsOnline(action);
    }

    public void onClickOfflineMode (View v){
        Intent intent = new Intent(this, OnePlayerActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_KEY_IS_ONLINE ,false);
        startActivity(intent);
    }
}