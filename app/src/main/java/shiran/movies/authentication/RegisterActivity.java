package shiran.movies.authentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import shiran.movies.R;
import shiran.movies.U;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtEmail, txtUserName, txtPassword, txtValidPassword;
    private TextView lblMag, lblRegSuccessfully;
    private View card;
    private Button btnContiue, btnFinish;
    private boolean isImageEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // initialize stuff
        txtEmail = (EditText) findViewById(R.id.register_email);
        txtUserName = (EditText) findViewById(R.id.register_username);
        txtPassword = (EditText) findViewById(R.id.register_password);
        txtValidPassword = (EditText) findViewById(R.id.register_valid_password);
        lblMag = (TextView) findViewById(R.id.register_messageControl);
        card = findViewById(R.id.card_more_details);
        btnContiue = (Button) findViewById(R.id.btn_continue_register);
        btnFinish = (Button) findViewById(R.id.btn_finish_register);
        lblRegSuccessfully = (TextView) findViewById(R.id.lbl_register_successfully);
        txtEmail.addTextChangedListener(onTextChange);
        txtPassword.addTextChangedListener(onTextChange);
        txtUserName.addTextChangedListener(onTextChange);
        txtValidPassword.addTextChangedListener(onTextChange);
        setTitle(getString(R.string.register));

        imgFace = (ImageView) findViewById(R.id.img_face);
        //imgFace.setImageResource(R.drawable.no_image);//default img
    }

    //private MenuItem actionBarItems;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);

        //actionBarItems = menu.findItem(R.id.action_back);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_back) {
            Toast.makeText(this, "sss", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMessage(final String message, int color) {
        lblMag.setVisibility(View.VISIBLE);
        lblMag.setText(message);
        lblMag.setTextColor(color);
    }

    private void dismissMessage() {
        lblMag.setVisibility(View.GONE);
    }

    public void onClickContinue(View view) {
        dismissMessage();

        // get all user info
        final String email = txtEmail.getText().toString();
        //final String username = txtUserName.getText().toString();
        final String password = txtPassword.getText().toString();

        // validation for info, show error when need
        if (password.length() < 8) {
            showMessage(getString(R.string.INVALID_PASSWORD), Color.RED);
            return;
        }
        if (!U.validate(txtEmail.getText().toString())) {
            showMessage(getString(R.string.invalid_email), Color.RED);
            return;
        }

        if (!txtPassword.getText().toString().equals(txtValidPassword.getText().toString())) {
            showMessage("Invalid password authentication", Color.RED);
            return;
        }

        // if all ok, create new user object

        createAccount(email, password);
    }

//    private void createUserInRealtimeDB(final String email, final String password){
//        DatabaseReference myRef;
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        myRef = database.getReference(U.USER_TABLE);
//        User user = new User();
//        //ArrayList<String> fav = new ArrayList<>();
//        //ArrayList<String> ord = new ArrayList<>();
//        //user.setFavorite(fav);
//        //user.setOrdered(ord);
//
//        myRef.child(mAuth.getCurrentUser().getUid()).setValue(user);
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                lblRegSuccessfully.setVisibility(View.VISIBLE);
//                signIn(email, password);
//                Toast.makeText(RegisterActivity.this, "user add real time", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(RegisterActivity.this, "user fail add real time", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }

    public void onClickFinish(View view) {
        if (!U.isNetworkConnected(this)) {
            showMessage(getString(R.string.check_internet), Color.RED);
            return;
        }

        if (txtUserName.getText().toString().trim().isEmpty()) {
            txtUserName.setError(getString(R.string.required));
            return;
        }
        final String name = mAuth.getCurrentUser().getUid();
        //final Uri imgURL = Uri.parse("http://dreamicus.com/data/face/face-02.jpg");
        if (!isImageEdit) {
            showMessage(getString(R.string.insert_image), Color.RED);
            return;
        }

        uploadImg("images", name, "jpg");
    }

    private void createAccount(final String email, final String password) {
        if (!U.isNetworkConnected(this)) {
            showMessage(getString(R.string.check_internet), Color.RED);
            return;
        }

        //showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.create_account_faild, Toast.LENGTH_SHORT).show();
                        } else {
                            lblRegSuccessfully.setVisibility(View.VISIBLE);
                            signIn(email, password);
                            showMoreDetails();
                            Toast.makeText(RegisterActivity.this, "Successfully create account", Toast.LENGTH_SHORT).show();
                        }
                        //hideProgressDialog();
                    }
                });
        // [END create_user_with_email]
    }

    void showMoreDetails() {
        card.setVisibility(View.VISIBLE);
        btnContiue.setVisibility(View.GONE);
        txtEmail.setEnabled(false);
        txtPassword.setEnabled(false);
        txtValidPassword.setEnabled(false);
        btnFinish.setVisibility(View.VISIBLE);
    }

    void updateUser(String name, Uri imgURL) {


        UserProfileChangeRequest.Builder profile = new UserProfileChangeRequest.Builder();
        profile.setDisplayName(name);
        if (imgURL != null) profile.setPhotoUri(imgURL);
        UserProfileChangeRequest profileUpdates = profile.build();

        mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener
                (new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()) {
                             Log.d("test", "User2 profile updated.");
                             finish();
                         }
                     }
                 }
                );
    }

    private void signIn(String email, String password) {

        //showProgressDialog();

        // [START sign_in_with_email]

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("test", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            Log.w("test", "signInWithEmail:failed", task.getException());
                        }

                        //hideProgressDialog();
                        lblRegSuccessfully.setText(lblRegSuccessfully.getText() + "\n Sign in successfully");
                        showMoreDetails();
                    }
                });
    }

    static final int GALLERY = 1, CAMERA = 2;
    ImageView imgFace;

    //---------------photo handle
    public void onClickCamera(View view) {
        //implicit Intent - for image capture from camera
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA);
    }

    public void onClickGallery(View view) {
        //implicit Intent - for Image pick from gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY);//open gallery
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (resultCode == RESULT_OK) {//good response
            if (requestCode == GALLERY || requestCode == CAMERA) {
                imgFace.setImageURI(i.getData());
                isImageEdit = true;
                U.fixSize(imgFace);
            }
        }
    }
    //-----------------------------------

    TextWatcher onTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            dismissMessage();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void uploadImg(String folder, String fileName, String type) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(folder + "/" + fileName + "." + type);

        imgFace.setDrawingCacheEnabled(true);
        imgFace.buildDrawingCache();
        Bitmap bitmap = imgFace.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(RegisterActivity.this, "image failed upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri imgURL = taskSnapshot.getDownloadUrl();
                Toast.makeText(RegisterActivity.this, "image succeeded upload", Toast.LENGTH_SHORT).show();

                final String name = txtUserName.getText().toString();
                //final Uri imgURL = Uri.parse("http://dreamicus.com/data/face/face-02.jpg");
                updateUser(name, imgURL);
            }
        });
    }
}
