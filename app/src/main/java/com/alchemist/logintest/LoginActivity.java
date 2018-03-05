package com.alchemist.logintest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String PACKAGE = "com.alchemist.logintest";

    String name, email, imageUrl;


    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnShowData)
    Button btnShowData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        generateKeyHash();
    }

    private void generateKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    @OnClick({R.id.btnLogin, R.id.btnShowData})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                btnShowData.setVisibility(View.GONE);
                StartLoginProcedure();
                break;
            case R.id.btnShowData:
                StartUserInfoActivity();
        }
    }

    private void StartUserInfoActivity() {
        Intent intent = ShowUserInfoActivity.getIntent(this, name, email, imageUrl);
        startActivity(intent);
    }

    private void StartLoginProcedure() {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Log.e(TAG, "onAuthSuccess: ");
                fetchPersonalInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.e(TAG, "onAuthError: " + error.toString());
            }
        }, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " ResultCode:" + resultCode);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private void fetchPersonalInfo() {
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,num-connections,picture-url,email-address,picture-urls::(original))";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {

                try {
                    JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                    try {
                        Log.e(TAG, "onApiSuccess: " + jsonObject.toString());
                    } catch (Exception e) {

                    }

                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
                    String mail = jsonObject.getString("emailAddress");
                    String numberOFConnection = jsonObject.getString("numConnections");
                    String publicProfile = jsonObject.getString("publicProfileUrl");
                    String url = jsonObject.getString("pictureUrl");

                    name = firstName + " " + lastName;
                    email = mail;
                    imageUrl = url;

                    btnShowData.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                Log.e(TAG, "onApiError: " + liApiError.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LISessionManager.getInstance(this).clearSession();
    }
}
