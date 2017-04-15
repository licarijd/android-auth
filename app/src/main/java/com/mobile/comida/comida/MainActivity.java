package com.mobile.comida.comida;

import com.stripe.android.model.Card;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import android.content.Context;
import com.stripe.android.TokenCallback;

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

//Database imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SignInButton signInButton;
    Button signOutButton;
    TextView statusTextView;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    public static CardInputWidget inputWidget;
    public static Card card;
    //public static Context mContext;

    private static final TokenCallback DEFAULT_TOKEN_CALLBACK = new TokenCallback() {
        @Override
        public void onError(Exception error) {
        }
        @Override
        public void onSuccess(Token token) {
        }
    };

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
        //Make reference to card widget
        inputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

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



    public void validatePurchase(/*final Context mContext*/) {

        Context mContext = getApplicationContext();

        // Remember that the card object will be null if the user inputs invalid data.

        card = inputWidget.getCard();

        if (card == null) {
            // Do not continue token creation.
        }

        Stripe stripe = new Stripe(mContext, "pk_test_tw59OlQl5z7eBiPkI5kL9bD7");
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                    }
                    public void onError(Exception error) {
                        // Show localized error message
                        /*Toast.makeText(getContext(),
                                error.getLocalizedString(getContext()),
                                Toast.LENGTH_LONG
                        ).show();*/
                    }
                }
        );
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
            case R.id.purchaseButton:
                validatePurchase();
                break;
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

            //Append rrray of reservation info to a list
            DatabaseReference mDatabase;// ...
            mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = acct.getId();
            statusTextView.setText(uid);
            //mDatabase.child("users").child(uid).child("test").setValue("22222");
            String[] reservation = new String[]{"01/01/17", "Jon's Burgers", "Justin L", "265", "1"};
            mDatabase.child("users").child(uid).child("reservations").push().setValue("01/01/17_Jon's Burgers_Justin L_265_1");
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