package com.example.petsee.cerceve;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petsee.Adapter.GonderiAdapter;
import com.example.petsee.Model.Gonderi;
import com.example.petsee.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GonderiAyrintilarFragment extends Fragment {

     String gonderiId;
     private RecyclerView recyclerView;
     private GonderiAdapter gonderiAdapter;
     private List<Gonderi> gonderiList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gonderi_ayrintilar,container,false);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        gonderiId = preferences.getString("gonderiId","none");
        recyclerView = view.findViewById(R.id.rGonderi_Ayrintilar);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        gonderiList=new ArrayList<>();
        gonderiAdapter = new GonderiAdapter(getContext(),gonderiList);
        recyclerView.setAdapter(gonderiAdapter);

        gonderileriOku();
        return view;
    }
    private void gonderileriOku() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Gonderiler").child(gonderiId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList.clear();
                Gonderi gonderi = dataSnapshot.getValue(Gonderi.class);
                gonderiList.add(gonderi);
                gonderiAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}