package shiran.movies.authentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.mainApp.MainAppActivity;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtEmail, txtPassword;
    private TextView lblMag;
    private Button btnFinish;
    private CheckBox chbRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mAuth = FirebaseAuth.getInstance();


        boolean prefAutoLogin = U.loadBoolPreferences(this, U.PREFS_AUTO_LOGIN);
        if (prefAutoLogin ) {
            String prefEmail = U.loadPreferences(this, U.PREFS_AUTO_EMAIL);
            String prefPass = U.loadPreferences(this, U.PREFS_AUTO_PASS);
            if (prefEmail!=null && prefPass!=null)
                signIn(prefEmail,prefPass);
            else
                initActivity();
        }else {
            initActivity();
        }
    }

    private void initActivity(){
        setContentView(R.layout.activity_sign_in);
        txtEmail = (EditText) findViewById(R.id.register_email);
        txtPassword = (EditText) findViewById(R.id.register_password);
        lblMag = (TextView) findViewById(R.id.register_messageControl);
        btnFinish = (Button) findViewById(R.id.btn_finish_sign_in);
        chbRememberMe = (CheckBox) findViewById(R.id.chk_remember_me);
        txtEmail.addTextChangedListener(onTextChange);
        txtPassword.addTextChangedListener(onTextChange);

        setTitle(getString(R.string.login));

        chbRememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                U.saveBoolPreferences(SignInActivity.this,U.PREFS_AUTO_LOGIN,chbRememberMe.isChecked());
            }
        });

    }


    private void showMessage(final String message, int color) {
        lblMag.setVisibility(View.VISIBLE);
        lblMag.setText(message);
        lblMag.setTextColor(color);
    }

    private void dismissMessage() {
        lblMag.setVisibility(View.GONE);
    }


    public void onClickFinish(View view) {
        dismissMessage();

        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();

        if (password.isEmpty() || email.isEmpty()) {
            showMessage("Email or password is incorrect", Color.RED);
            return;
        }

        signIn(email, password);

    }

    private void signIn(final String email, final String password) {
        if (!U.isNetworkConnected(this)) {
            if (txtPassword==null) initActivity();
            showMessage(getString(R.string.check_internet), Color.RED);
            return;
        }
        U.showProgressDialog(this);

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
                            showMessage(getString(R.string.email_or_pass_incorect), Color.RED);
                            U.hideProgressDialog();
                            return;

                        }
                        U.userId = mAuth.getCurrentUser().getUid();

                        boolean prefLogin = U.loadBoolPreferences(SignInActivity.this, U.PREFS_AUTO_LOGIN);
                        if (prefLogin) {
                            U.savePreferences(SignInActivity.this, U.PREFS_AUTO_EMAIL, email);
                            U.savePreferences(SignInActivity.this, U.PREFS_AUTO_PASS, password);
                        }else{
                            U.savePreferences(SignInActivity.this, U.PREFS_AUTO_EMAIL, null);
                            U.savePreferences(SignInActivity.this, U.PREFS_AUTO_PASS, null);
                        }

                        startActivity(new Intent(SignInActivity.this, MainAppActivity.class));

                        if (txtPassword==null) initActivity();
                        U.hideProgressDialog();
                        //txtPassword.setText("");
                            //chbRememberMe.setChecked(false);

                    }

                });

    }


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


    public void onClickToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
