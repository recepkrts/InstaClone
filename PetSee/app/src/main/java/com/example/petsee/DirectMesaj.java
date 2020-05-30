package com.example.petsee;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import com.example.petsee.Adapter.MesajAdapter;
import com.example.petsee.Bildirim.Token;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.Model.Mesaj;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.ArrayList;
import java.util.List;

public class DirectMesaj extends AppCompatActivity{

    private FirebaseUser mevcutKullanici;
    private ImageView geri;
    private RecyclerView rDm;
    private MesajAdapter mesajAdapter;
    private List<Kullanici> mKullaniciler;
//    private List<String> kullaniciList;
    MaterialEditText edt_aramaBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_mesaj);

        mevcutKullanici=FirebaseAuth.getInstance().getCurrentUser();
        rDm= findViewById(R.id.rDMesaj);
        rDm.setHasFixedSize(true);
        rDm.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        edt_aramaBar=findViewById(R.id.edt_aramabar);
        mKullaniciler=new ArrayList<>();
      //  kullaniciList=new ArrayList<>();
        mesajAdapter = new MesajAdapter(getApplicationContext(),mKullaniciler);
        rDm.setAdapter(mesajAdapter);
        geri= findViewById(R.id.geri_dm);

        kullaniciOku();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectMesaj.this,AnaSayfa.class));
            }
        });
        edt_aramaBar.addTextChangedListener(new TextWatcher() {
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
  /*      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mesajlar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kullaniciList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Mesaj mesaj =  snapshot.getValue(Mesaj.class);
                    if (mesaj.getGonderen().equals(mevcutKullanici.getUid())){
                        kullaniciList.add(mesaj.getAlici());
                    }
                    if (mesaj.getAlici().equals(mevcutKullanici.getUid())){
                        kullaniciList.add(mesaj.getGonderen());
                    }
                }
               // mesajOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/
    }
/*    private void mesajOku(){
        mKullaniciler=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mKullaniciler.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    for (String id : kullaniciList){
                        if (kullanici.getId().equals(id)){
                            if (mKullaniciler.size() != 0){
                                for (Kullanici kul : mKullaniciler){
                                    if (!kullanici.getId().equals(kul.getId())){
                                        mKullaniciler.add(kullanici);
                                    }
                                }
                            }else {
                                mKullaniciler.add(kullanici);
                            }
                        }
                    }
                }
                mesajAdapter = new MesajAdapter(getApplicationContext(),mKullaniciler);
                rDm.setAdapter(mesajAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }*/
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(mevcutKullanici.getUid()).setValue(token1);
    }
    private void kullaniciAra(String s){
        Query sorgu = FirebaseDatabase.getInstance().getReference("Kullanicilar").orderByChild("kullaniciadi")
                .startAt(s)
                .endAt(s+"\uf8ff");
        sorgu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mKullaniciler.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Kullanici kullanici=snapshot.getValue(Kullanici.class);
                    mKullaniciler.add(kullanici);
                }
                mesajAdapter.notifyDataSetChanged();//güncelleme
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
                if (edt_aramaBar.getText().toString().equals("")){//eğer aramabarda birşey yazmıyorsa tüm kullanıcıları göster.
                    mKullaniciler.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Kullanici kullanici=dataSnapshot1.getValue(Kullanici.class);
                        if (!kullanici.getId().equals(mevcutKullanici.getUid())){
                            mKullaniciler.add(kullanici);
                        }
                    }
                    mesajAdapter.notifyDataSetChanged();//otomatik güncelleme
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DirectMesaj.this,AnaSayfa.class));
        finish();
    }
}