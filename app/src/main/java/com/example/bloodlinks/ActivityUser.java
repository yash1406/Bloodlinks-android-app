package com.example.bloodlinks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivityUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private Toolbar toolBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DrawerLayout drawer;
    private ArrayList<String> mTitles = new ArrayList<>();
    private TextView txtUsername,txtUseremail;
    private NavigationView navigationView;
    private View headerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donorsRef = db.collection("Donors");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setupViews();


        txtUseremail.setText(firebaseUser.getEmail());
        donorsRef.whereEqualTo("email",firebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Donor donor = documentSnapshot.toObject(Donor.class);
                    txtUsername.setText(donor.getName());

                }


            }
        });

        setSupportActionBar(toolBar);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));



        final LinearLayout holder = findViewById(R.id.holder);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                //this code-block is the real player behind this beautiful ui
                // basically, it's a mathemetical calculation which handles the shrinking of
                // our content view.

                float scaleFactor = 7f;
                float slideX = drawerView.getWidth() * slideOffset;

                holder.setTranslationX(slideX);
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            setTitle(mTitles.get(0));

            navigationView.setCheckedItem(R.id.nav_home);
        }
    }


    private void setupViews(){

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        navigationView = findViewById(R.id.nav_view);
        toolBar = findViewById(R.id.actionbar);
        drawer = findViewById(R.id.drawer_layout);

        headerView = navigationView.getHeaderView(0);
        txtUseremail=headerView.findViewById(R.id.txtUseremail);
        txtUsername=headerView.findViewById(R.id.txtUsername);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                setTitle("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;

            case R.id.nav_profile:
                setTitle("Edit Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;

            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ActivityUser.this,ActivityHome.class));
                break;
            case R.id.nav_del_acc:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Account")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeDonor();
                                firebaseAuth.signOut();
                                firebaseUser.delete();
                                finish();
                                startActivity(new Intent(ActivityUser.this,ActivityHome.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    private void removeDonor() {
        donorsRef.whereEqualTo("email",firebaseUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                donorsRef.document(document.getId()).delete();
                            }
                            Toast.makeText(ActivityUser.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(ActivityUser.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
