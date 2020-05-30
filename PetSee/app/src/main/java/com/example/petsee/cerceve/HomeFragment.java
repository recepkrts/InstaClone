package com.example.petsee.cerceve;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.petsee.Adapter.GonderiAdapter;
import com.example.petsee.Adapter.StoryAdapter;
import com.example.petsee.DirectMesaj;
import com.example.petsee.GonderiActivity;
import com.example.petsee.Model.Gonderi;
import com.example.petsee.Model.Story;
import com.example.petsee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private GonderiAdapter gonderiAdapter;
    private List<Gonderi> gonderiList;
    private List<String> takipListesi;
    private FirebaseUser mevcutKullanici;
    private ProgressBar progressBar;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    ImageView baslik,dm,camera;

    public HomeFragment(){
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.rHome_fragment);

        baslik = view.findViewById(R.id.baslik);
        dm = view.findViewById(R.id.dm_home);
        camera = view.findViewById(R.id.camera_home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        gonderiList = new ArrayList<>();
        gonderiAdapter=new GonderiAdapter(getContext(),gonderiList);
        recyclerView.setAdapter(gonderiAdapter);

        recyclerView_story=view.findViewById(R.id.rHome_fragment_storiler);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

        progressBar = view.findViewById(R.id.pd_circular);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),GonderiActivity.class));
            }
        });
        baslik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(getContext(), DirectMesaj.class));
            }
        });

        takipKontrolu();
        return view;
    }
    private void takipKontrolu(){
        takipListesi=new ArrayList<>();
        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference("Takip")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("takipEdilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                takipListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    takipListesi.add(snapshot.getKey());
                }
                gonderileriOku();
                hikayeOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void gonderileriOku(){
        DatabaseReference gonderiYolu= FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);
                    for (String id : takipListesi){
                        if (gonderi.getGonderen().equals(id)){
                            gonderiList.add(gonderi);
                        }
                    }
                    if (gonderi.getGonderen().equals(mevcutKullanici.getUid())){
                        gonderiList.add(gonderi);
                    }
                }
                gonderiAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void hikayeOku(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hikaye");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             long mevcutsure = System.currentTimeMillis();
             storyList.clear();
             storyList.add(new Story("",0,0,"",
                     FirebaseAuth.getInstance().getCurrentUser().getUid()));
             for (String id : takipListesi){
                 int hikayeSayisi = 0;
                 Story story = null;
                 for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                    story = snapshot.getValue(Story.class);
                    if (mevcutsure > story.getTimestart() && mevcutsure < story.getTimeend()){
                        hikayeSayisi++;
                    }
                 }
                 if (hikayeSayisi > 0){
                     storyList.add(story);
                 }
             }
             storyAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}