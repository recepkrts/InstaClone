package com.example.petsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.petsee.Adapter.KullaniciAdapter;
import com.example.petsee.Model.Kullanici;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TakipciActivity extends AppCompatActivity {
    String id,baslik;
    List<String> idList;
    RecyclerView recyclerView;
    KullaniciAdapter kullaniciAdapter;
    List<Kullanici> kullaniciList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takipci);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        baslik = intent.getStringExtra("baslik");
        Toolbar toolbar = findViewById(R.id.toolbar_takipciActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(baslik);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.rTakipci);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kullaniciList = new ArrayList<>();
        kullaniciAdapter = new KullaniciAdapter(this,kullaniciList,false);
        recyclerView.setAdapter(kullaniciAdapter);
        idList = new ArrayList<>();
        switch (baslik){
            case "begeni":
                begeniCek();
                break;
            case "takipEdilenler" :
                takipEdilenCek();
                break;
            case "takipciler" :
                takipciCek();
                break;
            case "Görüntüleyenler":
                getViews();
                break;
        }
    }
    private void getViews(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hikaye")
                .child(id).child(getIntent().getStringExtra("storyid")).child("goruldu");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                kullanicileriOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void begeniCek() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Begeniler")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                kullanicileriOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void takipEdilenCek() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Takip")
                .child(id).child("takipEdilenler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                kullanicileriOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void takipciCek(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Takip")
                .child(id).child("takipciler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             idList.clear();
             for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                 idList.add(snapshot.getKey());
             }
                kullanicileriOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void kullanicileriOku(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kullaniciList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    for (String id :idList){
                        if (kullanici.getId().equals(id)){
                            kullaniciList.add(kullanici);
                        }
                    }
                }
                kullaniciAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}