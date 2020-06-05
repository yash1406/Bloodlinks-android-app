package com.example.bloodlinks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ramotion.circlemenu.CircleMenuView;

import javax.annotation.Nullable;

public class ActivityHome extends AppCompatActivity {
    private LocationManager locationManager;
    private Task<Void> fusedLocationProviderClient;
    private CircleMenuView cm;
    private Button bd;
    private Button bb;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dr = db.collection("Donors");
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cm=findViewById(R.id.cm);
        bd=findViewById(R.id.bd);
        bb=findViewById(R.id.bb);

        cm.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, final int index) {
                final String bgs[]={"A+","B+","O+","AB+","A-","B-","O-","AB-"};

                dr.whereEqualTo("bloodgroup",bgs[index])
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                             @Override
                             public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                 if (e != null) {
                                     Toast.makeText(ActivityHome.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                     return;
                                 }
                                 for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                     Donor d = doc.toObject(Donor.class);
                                     count++;
                                 }
                                 if(count!=0) {
                                     Intent i = new Intent(ActivityHome.this, ActivityDonors.class);
                                     i.putExtra("bg", bgs[index]);
                                     startActivity(i);
                                 } else
                                     Toast.makeText(ActivityHome.this, "No donors with specified Blood group !!", Toast.LENGTH_SHORT).show();
                                 bd.setEnabled(true);
                                 count=0;
                             }
                         });

            }

            @Override
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                bd.setEnabled(false);
            }

            @Override
            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
                bd.setEnabled(true);
            }
        });

        bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ActivityHome.this,ActivityLogin.class);
                startActivity(i);
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityHome.this,MapsActivity.class));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ActivityHome.this, "Location services Denied! ", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                        .requestLocationUpdates(new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                                .setInterval(10 * 1000)
                                .setFastestInterval(2000), new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                onLocationChanged(locationResult.getLastLocation());
                            }
                        }, Looper.myLooper());
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false){
                    new AlertDialog.Builder(this)
                            .setTitle("Location needed!")
                            .setMessage("Location required for better user experience.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            }).create().show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed!")
                        .setMessage("Location services required for better user experience.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ActivityHome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }).create().show();
            }
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                            .setInterval(10 * 1000)
                            .setFastestInterval(2000), new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                onLocationChanged(locationResult.getLastLocation());
                            }
                        }, Looper.myLooper());
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false){
                new AlertDialog.Builder(this)
                        .setTitle("Location needed!")
                        .setMessage("Location required for better user experience.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }).create().show();
            }
        }
    }

    private void onLocationChanged(Location location) {
        Donor.lati=location.getLatitude();
        Donor.longi=location.getLongitude();
    }
}
