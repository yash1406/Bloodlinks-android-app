package com.example.bloodlinks;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bloodlinks.R.drawable.bgregister;

public class ProfileFragment extends Fragment{

    private Spinner spinner;
    private EditText etusername,etusermobile;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference donorsRef = db.collection("Donors");
    private CollectionReference placesRef = db.collection("Places");
    private ProgressDialog progressDialog;
    private String bloodgroup;
    private Button btnsave;
    private TextView txtusername;
    private AutoCompleteTextView actv;
    private List<String> places;
    private Double longitude, latitude;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        places= Arrays.asList("Akurdi", "Ambegaon", "Aundh", "Balewadi", "Baner", "Bavdhan", "Bhavani Peth", "Bhosari", "Bibvewadi", "Budhwar Peth", "Chakan", "Charholi Budruk", "Chikhli", "Chinchwad", "Dapodi", "Dehu Road", "Dhankawadi", "Dhanori", "Dhayari", "Dighi", "Dudulgaon", "Erandwane", "Fursungi", "Ganesh Peth", "Ganesh khind", "Ghorpade Peth", "Ghorpadi", "Guruwar Peth", "Hadapsar", "Hinjwadi", "Kalas", "Kalewadi", "Kasarwadi", "Kasba Peth", "Katraj", "Khadki", "Kharadi", "Kondhwa", "Koregaon Park", "Kothrud", "Mahatma Phule Peth", "Mangalwar Peth", "Manjri", "Markal", "Mohammedwadi", "Moshi", "Mundhwa", "Nana Peth", "Narayan Peth", "Navi Peth", "Panmala", "Parvati", "Pashan", "Phugewadi", "Pimple Gurav", "Pimple Nilakh", "Pimple Saudagar", "Pimpri", "Pirangut", "Rahatani", "Rasta Peth", "Ravet", "Raviwar Peth", "Sadashiv Peth", "Sangvi", "Saswad", "Shaniwar Peth", "Shivajinagar ", "Shukrawar Peth", "Somwar Peth", "Talawade", "Tathawade", "Thergaon", "Undri", "Vadgaon Budruk", "Vishrantwadi", "Vitthalwadi", "Wadgaon Sheri", "Wagholi", "Wakad", "Wanwadi", "Warje", "Yerwada");


        setupViews(view);



        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(validate()){

                    progressDialog.setTitle("Updating");
                    progressDialog.show();

                    placesRef.whereEqualTo("location",actv.getText().toString())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    for(QueryDocumentSnapshot doc:queryDocumentSnapshots) {
                                        latitude=Double.valueOf(doc.getString("latitude"));
                                        longitude=Double.valueOf(doc.getString("longitude"));
                                    }



                                    donorsRef.whereEqualTo("email",firebaseUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Map<String, String> map = new HashMap<>();
                                                    Map<String, Double> map1 = new HashMap<>();
                                                    map.put("name",etusername.getText().toString().trim());
                                                    map.put("mobile",etusermobile.getText().toString().trim());
                                                    map.put("bloodgroup",bloodgroup);
                                                    map.put("location",actv.getText().toString().trim());

                                                    map1.put("longitude",longitude);
                                                    map1.put("latitude",latitude);

                                                    donorsRef.document(document.getId()).set(map, SetOptions.merge());
                                                    donorsRef.document(document.getId()).set(map1, SetOptions.merge());


                                                }


                                                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                                View headerView = navigationView.getHeaderView(0);
                                                txtusername = headerView.findViewById(R.id.txtUsername);
                                                txtusername.setText(etusername.getText().toString());

                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(),"Updated",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                }
                            });




                }

            }
        });

        return view;
    }

    private void setupViews(View view) {


        etusermobile = view.findViewById(R.id.etchangemobile);
        etusername = view.findViewById(R.id.etchangename);
        progressDialog = new ProgressDialog(getContext());
        btnsave = view.findViewById(R.id.btnsave);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        actv = view.findViewById(R.id.eteditprofileautocomp);
        ArrayAdapter<String>plac=new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,places);
        actv.setAdapter(plac);
        actv.setDropDownBackgroundResource(bgregister);


        spinner = view.findViewById(R.id.spinnerchangebgroup);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.bloodgroup,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bloodgroup = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        progressDialog.show();
        donorsRef.whereEqualTo("email",firebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                progressDialog.dismiss();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Donor donor = documentSnapshot.toObject(Donor.class);
                    etusermobile.setText(donor.getMobile());
                    etusername.setText(donor.getName());
                    actv.setText(donor.getLocation());
                    int spinnerPosition = adapter.getPosition(donor.getBloodgroup());
                    spinner.setSelection(spinnerPosition);
                    //Toast.makeText(ActivityUser.this,"hi"+donor.getName(),Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    private Boolean validate() {

        Boolean result = false;

        String uname = etusername.getText().toString().trim();
        String umobile = etusermobile.getText().toString().trim();
        String uplace = actv.getText().toString();

        if (uname.isEmpty() || umobile.isEmpty() || uplace.isEmpty()) {
            Toast.makeText(getContext(), "fill all the above fields", Toast.LENGTH_SHORT).show();
        }
        else if(!android.util.Patterns.PHONE.matcher(umobile).matches() || umobile.length()!=10){
            Toast.makeText(getContext(), "incorrect mobile number", Toast.LENGTH_SHORT).show();

        }
        else if(!places.contains(actv.getText().toString())) {
            actv.setText("");
            Toast.makeText(getContext(), "Enter valid location", Toast.LENGTH_SHORT).show();
        }
        else {
            result = true;
        }

        return result;
    }


}
