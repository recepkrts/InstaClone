package com.example.petsee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petsee.Adapter.MesajGondermeAdapter;
import com.example.petsee.Bildirim.Client;
import com.example.petsee.Bildirim.Data;
import com.example.petsee.Bildirim.MyResponse;
import com.example.petsee.Bildirim.Sender;
import com.example.petsee.Bildirim.Token;
import com.example.petsee.Model.APIService;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesajGonderme extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private MaterialEditText edt_mesaj;
    private ImageView gonder,dosyaSec;
    private TextView kullaniciAdi;
    private CircleImageView profil_resmi;
    Intent intent;
    String mesaj,kullaniciId,mesajId,aktifTarih,aktifZaman;

    MesajGondermeAdapter mesajGondermeAdapter;
    List<Mesaj> mMesajList;
    RecyclerView rMesaj;

    APIService apiService;
    boolean notify = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesaj_ogesi);

        intent = getIntent();
        kullaniciId = intent.getStringExtra("userid");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MesajGonderme.this,DirectMesaj.class));
            }
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        rMesaj = findViewById(R.id.recyclerview_dm);
        rMesaj.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rMesaj.setLayoutManager(linearLayoutManager);
        profil_resmi = findViewById(R.id.imageKisi);
        kullaniciAdi = findViewById(R.id.txt_kAdi);
        edt_mesaj = findViewById(R.id.edt_mesaj);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar")
                .child(kullaniciId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                kullaniciAdi.setText(kullanici.getKullaniciadi());
                if (kullanici.getResimurl().equals("default")){
                    profil_resmi.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MesajGonderme.this).load(kullanici.getResimurl())
                            .placeholder(R.color.colorBlack).into(profil_resmi);
                }
                mesajOku(firebaseUser.getUid(), kullaniciId, kullanici.getResimurl());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        gonder=findViewById(R.id.image_gonder);
        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                mesaj = edt_mesaj.getText().toString();
                if (!mesaj.equals("")){
                    mesajGonder(firebaseUser.getUid(),kullaniciId,mesaj);
                }else{
                    Toast.makeText(MesajGonderme.this, "Boş mesaj gönderemezsiniz !", Toast.LENGTH_LONG).show();
                }
                edt_mesaj.setText("");
            }
        });
    }
    private void mesajGonder(String gonderenId, final String aliciId, String mesaj) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        mesajId = reference.push().getKey();
        Calendar tarihFormati = Calendar.getInstance();
        SimpleDateFormat aktifTarihFormati = new SimpleDateFormat("MMM dd, yyyy");
        aktifTarih=aktifTarihFormati.format(tarihFormati.getTime());
        Calendar zamanFormati = Calendar.getInstance();
        SimpleDateFormat aktifZamanFormati = new SimpleDateFormat("hh:mm:ss a");
        aktifZaman = aktifZamanFormati.format(zamanFormati.getTime());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("mesajId",mesajId);
        hashMap.put("gonderen",gonderenId);
        hashMap.put("alici",aliciId);
        hashMap.put("mesaj",mesaj);
        hashMap.put("zaman",aktifZaman);
        hashMap.put("tarih",aktifTarih);

        reference.child("Mesajlar").push().setValue(hashMap);

        final String msg = mesaj;
        DatabaseReference referenceA = FirebaseDatabase.getInstance().getReference("Kullanicilar")
                .child(firebaseUser.getUid());
        referenceA.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                if (notify){
                    sendNotification(aliciId,kullanici.getKullaniciadi(),msg);
                }
                notify = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void sendNotification(String aliciId, final String kullaniciAdi, final String mesaj){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(aliciId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,
                            kullaniciAdi+": "+mesaj,"Yeni Mesaj",kullaniciId);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                if (response.body().success != 1){
                                    Toast.makeText(MesajGonderme.this, "Hata!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void mesajOku(final String myId, final String kullaniciId, final String resimUrl){
        mMesajList = new ArrayList<>();
        DatabaseReference mesajYolu = FirebaseDatabase.getInstance().getReference("Mesajlar");
        mesajYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMesajList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                   Mesaj mesaj= snapshot.getValue(Mesaj.class);
                    if (mesaj != null && mesaj.getAlici().equals(myId) && mesaj.getGonderen().equals(kullaniciId) ||
                            mesaj != null &&  mesaj.getGonderen().equals(myId) && mesaj.getAlici().equals(kullaniciId)){
                        mMesajList.add(mesaj);
                    }
                    mesajGondermeAdapter = new MesajGondermeAdapter(MesajGonderme.this, mMesajList, resimUrl);
                    rMesaj.setAdapter(mesajGondermeAdapter);
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
        startActivity(new Intent(MesajGonderme.this,DirectMesaj.class));
        finish();
    }
  /*  private void durum(String durum){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("durum",durum);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        durum("çevrimiçi");
    }
    @Override
    protected void onPause() {
        super.onPause();
        durum("çevrimdışı");
        finish();
    }*/
}