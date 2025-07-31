package com.example.friendsforeverproject;

import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Log.d("NAV_DEBUG", "Item selected: " + item.getItemId());

            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_events) {
                selectedFragment = new EventsFragment();
                Toast.makeText(this, "Events", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_friends) {
                selectedFragment = new FriendsFragment();
                Toast.makeText(this, "Friends tab selected", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // This line MUST be AFTER setting the listener
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}
