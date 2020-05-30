package com.example.petsee.cerceve;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.petsee.Adapter.KullaniciAdapter;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AramaFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Kullanici> kullaniciList;
    private KullaniciAdapter kullaniciAdapter;
    private EditText arama_bar;

    public AramaFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_arama, container, false);

        recyclerView=view.findViewById(R.id.rArama);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arama_bar=view.findViewById(R.id.edit_aramabar);
        kullaniciList=new ArrayList<>();
        kullaniciAdapter = new KullaniciAdapter(getContext(),kullaniciList,true);
        recyclerView.setAdapter(kullaniciAdapter);

        kullaniciOku();
        arama_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kullaniciAra(s.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }
    private void kullaniciAra(String s){
        Query sorgu = FirebaseDatabase.getInstance().getReference("Kullanicilar").orderByChild("kullaniciadi")
                .startAt(s)
                .endAt(s+"\uf8ff");
        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kullaniciList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Kullanici kullanici=snapshot.getValue(Kullanici.class);
                    kullaniciList.add(kullanici);
                }
                kullaniciAdapter.notifyDataSetChanged();//güncelleme
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void kullaniciOku(){
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (arama_bar.getText().toString().equals("")){//eğer aramabarda birşey yazmıyorsa tüm kullanıcıları göster.
                    kullaniciList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Kullanici kullanici=dataSnapshot1.getValue(Kullanici.class);
                        kullaniciList.add(kullanici);
                    }
                    kullaniciAdapter.notifyDataSetChanged();//otomatik güncelleme
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}