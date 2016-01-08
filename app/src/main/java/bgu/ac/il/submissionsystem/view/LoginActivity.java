package bgu.ac.il.submissionsystem.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bgu.ac.il.submissionsystem.Controller.RefreshService;
import bgu.ac.il.submissionsystem.Controller.RefreshServiceConnection;
import bgu.ac.il.submissionsystem.R;
import bgu.ac.il.submissionsystem.Utils.Constants;
import bgu.ac.il.submissionsystem.model.CustomSubmissionSystemRequest;
import bgu.ac.il.submissionsystem.model.ErrorListener;
import bgu.ac.il.submissionsystem.model.FrodoBodyRequest;
import bgu.ac.il.submissionsystem.model.InformationHolder;
import bgu.ac.il.submissionsystem.model.LoginRequest;
import bgu.ac.il.submissionsystem.model.RequestListener;
import bgu.ac.il.submissionsystem.model.SubmissionSystemActions;
import bgu.ac.il.submissionsystem.model.SubmissionSystemResponse;
import bgu.ac.il.submissionsystem.model.SubmissionSystemStartRequest;

public class LoginActivity extends AppCompatActivity {


    private EditText mUsernameView;
    private EditText mPasswordView;
    private boolean requested;
    private RequestQueue requestQueue;
    private ActionProcessButton mSignInButton;
    private RefreshServiceConnection refreshServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        requested=false;
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mSignInButton = (ActionProcessButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        populateCreds();

        registerBroadcasts();
        requestQueue= Volley.newRequestQueue(this);


    }

    private void populateCreds(){
        CheckBox c=(CheckBox)findViewById(R.id.rememberLoginIn);
        SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String username = pref.getString(Constants.PREF_USERNAME, null);
        String password = pref.getString(Constants.PREF_PASSWORD, null);
        if (username != null && password != null) {
            mUsernameView.setText(username);
            mPasswordView.setText(password);
            c.setChecked(true);
        }


        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(Constants.PREF_USERNAME, mUsernameView.getText().toString())
                            .putString(Constants.PREF_PASSWORD, mPasswordView.getText().toString())
                            .commit();
                } else {
                    getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                            .edit().remove(Constants.PREF_USERNAME).remove(Constants.PREF_PASSWORD)
                            .commit();
                }
            }
        });
    }

    public void startMain(){
        Intent act = new Intent(this, MainActivity.class);
        startActivity(act);
        if(refreshServiceConnection!=null&&refreshServiceConnection.isBound()){
            unbindService(refreshServiceConnection);
        }

    }
    public void startRefreshService(){
        Intent intent= new Intent(this, RefreshService.class);
        refreshServiceConnection= new RefreshServiceConnection();
        bindService(intent, refreshServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void requestFrodoBody(){
        RequestListener<Integer> requestListener= new RequestListener<>(Constants.frodobodyIntentName,this);
        ErrorListener<Integer> errorListener= new ErrorListener<>(Constants.frodobodyIntentName+"error",this);
        HashMap<String,String> map= new HashMap<>();
        map.put("csid",InformationHolder.getCsid());
        map.put("action",Constants.ShowGreetings);

        String url=CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(),map);
        FrodoBodyRequest frodoBodyRequest= new FrodoBodyRequest(url,requestListener,errorListener);
        requestQueue.add(frodoBodyRequest);

    }

    public void requestSubmissionSystemStart(int val){
        RequestListener<Boolean> requestListener= new RequestListener<>(Constants.submissionSystemStartIntentName,this);
        ErrorListener<Boolean> errorListener= new ErrorListener<>(Constants.submissionSystemStartIntentName+"error",this);
        HashMap<String,String> map= new HashMap<>();
        map.put("csid",InformationHolder.getCsid());
        map.put("action",Constants.chooseSon);
        map.put("user-hash-code",val+"");
        String url=CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(),map);
        SubmissionSystemStartRequest frodoBodyRequest= new SubmissionSystemStartRequest(url,requestListener,errorListener);
        requestQueue.add(frodoBodyRequest);

    }


    /**
     * registers recivers for - 1.login 2.frodo greetings 3.submission system start
     */
private void registerBroadcasts(){
    BroadcastReceiver loginReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSignInButton.setProgress(50);
           SubmissionSystemResponse response=(SubmissionSystemResponse) intent.getSerializableExtra("response");
            String csid=response.get("csid");
            String username=response.get("username");
            InformationHolder.setCsid(csid);
            InformationHolder.setUsername(username);
            startRefreshService();
            requestFrodoBody();



        }
    };
    IntentFilter loginIntentFilter= new IntentFilter(Constants.loginIntentName);

    BroadcastReceiver loginReceivererror= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                mUsernameView.setError("error reciving csid from server");
                mSignInButton.setProgress(-1);
            requested=false;
        }
    };
    IntentFilter loginIntentFiltererror= new IntentFilter(Constants.loginIntentName+"error");

    BroadcastReceiver frodobodyReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSignInButton.setProgress(75);
            int val= intent.getIntExtra("response",-1);
            requestSubmissionSystemStart(val);



        }
    };
    IntentFilter frodobodyIntentFilter= new IntentFilter(Constants.frodobodyIntentName);

    BroadcastReceiver frodobodyReceivererror= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mUsernameView.setError("error getting Frodo Greetings");
            mSignInButton.setProgress(-1);
            requested=false;
        }
    };
    IntentFilter frodobodyIntentFiltererror= new IntentFilter(Constants.frodobodyIntentName+"error");
    BroadcastReceiver submissionSystemStartReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSignInButton.setProgress(100);
           startMain();
            requested=false;



        }
    };
    IntentFilter submissionSystemStartIntentFilter= new IntentFilter(Constants.submissionSystemStartIntentName);

    BroadcastReceiver submissionSystemStartReceivererror= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mUsernameView.setError("error reciving csid from server");
            mSignInButton.setProgress(-1);
            requested=false;
        }
    };
    IntentFilter submissionSystemStartIntentFiltererror= new IntentFilter(Constants.submissionSystemStartIntentName+"error");

    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
    localBroadcastManager.registerReceiver(loginReceiver,loginIntentFilter);

    localBroadcastManager.registerReceiver(loginReceivererror,loginIntentFiltererror);
    localBroadcastManager.registerReceiver(frodobodyReceiver,frodobodyIntentFilter);
    localBroadcastManager.registerReceiver(frodobodyReceivererror,frodobodyIntentFiltererror);
    localBroadcastManager.registerReceiver(submissionSystemStartReceiver,submissionSystemStartIntentFilter);
    localBroadcastManager.registerReceiver(submissionSystemStartReceivererror,submissionSystemStartIntentFiltererror);
}

    private void attemptLogin() {
        if (requested) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mSignInButton.setMode(ActionProcessButton.Mode.ENDLESS);
            mSignInButton.setProgress(25);
            request(username, password);

        }
    }

    public void  request(String username,String password){
        RequestListener<SubmissionSystemResponse> listener= new RequestListener<>(Constants.loginIntentName,this);
        ErrorListener<SubmissionSystemResponse> errorListener= new ErrorListener<>(Constants.loginIntentName+"error",this);
        Map<String,String> params = new HashMap<>();
        params.put("login", username);
        params.put("password", password);
        params.put("module", Constants.module);
        params.put("type", Constants.type);
        params.put("action", SubmissionSystemActions.login.name());
        String url=CustomSubmissionSystemRequest.attachParamsToUrl(InformationHolder.getBaseUrl(),params);
        LoginRequest loginRequest= new LoginRequest(url,listener,errorListener);
        loginRequest.setParams(params);
        requestQueue.add(loginRequest);

    }






}

