package com.example.crp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.crp.tab1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainScreen extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String userId, name;
    private FirebaseAuth firebaseAuth;
    private TextView email, username;
    private FirebaseFirestore  firebaseFirestore;
    private AlertDialog dialog_explain;
    private ViewPager viewpager;
    public PageAdapter pageAdapter;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setDrawerLayout(drawer)
                .build();
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabItem tab1 = findViewById(R.id.Tab1);
        TabItem tab2 = findViewById(R.id.Tab2);
        viewpager = findViewById(R.id.viewpager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewpager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    pageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        View headerView = navigationView.getHeaderView(0);

        email = headerView.findViewById(R.id.email_header);
        username = headerView.findViewById(R.id.username_header);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        userId = user.getUid();
        firebaseFirestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    change_info(document);
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "get failed: " + task.getException());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.About) {
            AlertDialog.Builder dialogBuilder_explain = new AlertDialog.Builder(this);
            final View explainPopup = getLayoutInflater().inflate(R.layout.popup_explain, null);

            Button popUpBtn_explain = (Button) explainPopup.findViewById(R.id.close_btn);

            dialogBuilder_explain.setView(explainPopup);
            dialog_explain = dialogBuilder_explain.create();
            dialog_explain.show();

            popUpBtn_explain.setOnClickListener(view -> {
                dialog_explain.dismiss();
            });
        } else if (id == R.id.btn_logout) {
            firebaseAuth.signOut();

            Intent intent = new Intent(MainScreen.this, LogIn.class);
            startActivity(intent);
        } else if (id == R.id.contracts) {
            Intent intent = new Intent(MainScreen.this, Contratos.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void change_info(DocumentSnapshot document) {
        HashMap data = (HashMap) document.getData();

        assert data != null;
        name = (String) data.get("username");
        String mail = (String) data.get("email");
        String isFirst = (String) data.get("firstTime").toString();

        if (isFirst.equals("1")) {
            explanation();
        }

        username.setText(name);
        email.setText(mail);
    }

    private void explanation() {
        AlertDialog.Builder dialogBuilder_explain = new AlertDialog.Builder(this);
        final View explainPopup = getLayoutInflater().inflate(R.layout.popup_explain, null);

        Button popUpBtn_explain = (Button) explainPopup.findViewById(R.id.close_btn);

        dialogBuilder_explain.setView(explainPopup);
        dialog_explain = dialogBuilder_explain.create();
        dialog_explain.show();

        popUpBtn_explain.setOnClickListener(view -> {
            DocumentReference reference = firebaseFirestore.collection("users").document(userId);
            Map <String, Object> userUp = new HashMap<>();
            userUp.put("firstTime", 0);
            reference.update(userUp).addOnSuccessListener(aVoid -> Log.d("TAG",
                    "onSuccess: user profile was created for " + userUp))
                    .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));
            dialog_explain.dismiss();
        });
    }
}