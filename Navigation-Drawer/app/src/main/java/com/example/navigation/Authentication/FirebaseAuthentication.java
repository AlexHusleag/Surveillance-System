package com.example.navigation.Authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.navigation.MainActivity;
import com.example.navigation.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FirebaseAuthentication extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 1998;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_authentication);

        // Initialize providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(false).build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        showSignInOptions();
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.FirebaseAuthentication)
                        .build(), MY_REQUEST_CODE

        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportActionBar().setTitle("Supraveghere video - Raspberry Pi");
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Bundle bundle = new Bundle();
                bundle.putSerializable("email", Objects.requireNonNull(user).getEmail());
                bundle.putSerializable("name", user.getDisplayName());

                finish();
                Intent intent = new Intent(FirebaseAuthentication.this, MainActivity.class);
                intent.putExtra("userInformation", bundle);

                startActivity(intent);

            } else {
                // Sign in failed
                if (response == null) {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
