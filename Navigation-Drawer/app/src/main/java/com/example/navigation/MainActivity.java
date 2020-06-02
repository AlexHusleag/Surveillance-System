package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.navigation.Authentication.FirebaseAuthentication;
import com.example.navigation.Livestream.Livestream;
import com.example.navigation.Servo.Servo;
import com.example.navigation.ViewFolders.ListVideoDates;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private View header;
    private Bundle extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.setDrawerTitle(GravityCompat.START, "Supraveghere video");
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Supraveghere video - Raspberry Pi");

        extra = getIntent().getBundleExtra("extra");

        header = navigationView.getHeaderView(0);
        TextView username = header.findViewById(R.id.username_id);
        TextView email = header.findViewById(R.id.email_id);

        extra = getIntent().getBundleExtra("userInformation");

        assert extra != null;
        username.setText((String) extra.getSerializable("name"));
        email.setText((String) extra.getSerializable("email"));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_livestream: {
                startActivity(new Intent(MainActivity.this, Livestream.class));
                break;
            }

            case R.id.nav_servo: {
                startActivity(new Intent(MainActivity.this, Servo.class));
                break;
            }

            case R.id.nav_videos: {
                //startActivity(new Intent(MainActivity.this, ViewVideo.class));
                startActivity(new Intent(MainActivity.this, ListVideoDates.class));
                break;
            }

            case R.id.nav_logout: {
                logout();
            }
        }
        return true;
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, FirebaseAuthentication.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        logout();
    }
}
