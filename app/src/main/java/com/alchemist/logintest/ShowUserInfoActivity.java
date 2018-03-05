package com.alchemist.logintest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linkedin.platform.LISessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowUserInfoActivity extends AppCompatActivity {

    private static final String KEY_NAME = "FirstName";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_IMAGE = "IMAGE";
    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_info);
        ButterKnife.bind(this);

        getIntentExtra();
    }


    @OnClick(R.id.btnSignOut)
    void onClick() {
        LISessionManager.getInstance(this).clearSession();
        finish();
    }

    private void getIntentExtra() {
        String name = "", imageUrl = "", email = "";

        if (getIntent().getExtras() != null) {
            name = getIntent().getExtras().getString(KEY_NAME);
            imageUrl = getIntent().getExtras().getString(KEY_IMAGE);
            email = getIntent().getExtras().getString(KEY_EMAIL);
        }

        Glide.with(this).load(imageUrl).into(ivProfile);
        tvName.setText(name);
        tvEmail.setText(email);

    }

    public static Intent getIntent(Context context, String name, String email, String imageUrl) {
        Intent intent = new Intent(context, ShowUserInfoActivity.class);
        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_EMAIL, email);
        intent.putExtra(KEY_IMAGE, imageUrl);
        return intent;
    }
}
