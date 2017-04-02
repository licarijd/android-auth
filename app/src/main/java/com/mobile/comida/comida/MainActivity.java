package com.mobile.comida.comida;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SignInButton signInButton;
    Button signOutButton;
    TextView statusTextView;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    //private FirebaseAuth mAuth;
    // [END declare_auth]

   // private GoogleApiClient mGoogleApiClient;
    //private TextView mStatusTextView;
    //private TextView mDetailTextView;
   // private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        // Views
        statusTextView = (TextView) findViewById(R.id.status_textview);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);
        //DetailTextView = (TextView) findViewById(R.id.detail);


        // Button listeners
        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.signOutButton).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);


        // [START initialize_auth]
       // mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.signOutButton:
                signOut();
                break;
            //case R.id.disconnect_button:
            //    revokeAccess();
             //   break;
        }
    }


    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // [START_EXCLUDE]
            handleSignInResult(result);
            // [END_EXCLUDE]
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Successful Google sign in, authenticate with Firebase.
            GoogleSignInAccount acct = result.getSignInAccount();
            //firebaseAuthWithGoogle(acct);
            statusTextView.setText("Hello " + acct.getDisplayName());

        } else {
            statusTextView.setText("Unsuccessfull");
            //updateUI(null);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

   // private void signOut() {
     //   Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>()(
      //      @Override
       //     public void onResult(@NonNull Status status) {
        //        statusTextView.setText(("Signed Out"));
        //        });
  //  }

    private void signOut() {
        // Firebase sign out
        // mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        statusTextView.setText(("Signed Out"));
                    }
                });
    }

}