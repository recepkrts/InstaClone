package com.example.petsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petsee.Adapter.YorumAdapter;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.Model.Yorum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YorumlarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private YorumAdapter yorumAdapter;
    private List<Yorum> yorumList;

    EditText edt_yorum_ekle;
    ImageView profil_resmi;
    TextView txt_gonder;
    String gonderiId,gonderenId;

    FirebaseUser mevcutKullanici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorumlar);

        Toolbar toolbar = findViewById(R.id.toolbar_yorumlarActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yorumlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        gonderiId=intent.getStringExtra("gonderiId");
        gonderenId=intent.getStringExtra("gonderenId");

        recyclerView = findViewById(R.id.rYorumlarActivity);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        yorumList=new ArrayList<>();
        yorumAdapter=new YorumAdapter(this,yorumList,gonderiId);
        recyclerView.setAdapter(yorumAdapter);

        edt_yorum_ekle = findViewById(R.id.edt_yorumEkle_yorumlarActivity);
        profil_resmi=findViewById(R.id.profil_resmi_yorumlar);
        txt_gonder=findViewById(R.id.txt_gonder_yorumlarActivity);

        txt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_yorum_ekle.getText().toString().equals("")){
                    Toast.makeText(YorumlarActivity.this, "Boş Yorum Gönderemezsiniz...", Toast.LENGTH_SHORT).show();
                }else{
                    yorumEkle();
                }
            }
        });
        //metotları çağırma
        yorumlariOku();
        resimAl();
    }
    private void yorumEkle() {
        DatabaseReference yorumlarYolu = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        String yorumid = yorumlarYolu.getKey();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("yorum",edt_yorum_ekle.getText().toString());
        hashMap.put("gonderen",mevcutKullanici.getUid());
        hashMap.put("yorumid",yorumid);

        yorumlarYolu.push().setValue(hashMap);
        bildirimEkle();
        edt_yorum_ekle.setText("");
    }
    private void bildirimEkle(){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Bildirimler").child(gonderenId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciId",mevcutKullanici.getUid());
        hashMap.put("text","yorum yaptı : "+edt_yorum_ekle.getText().toString());
        hashMap.put("gonderiId",gonderiId);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }
    private void resimAl(){
        DatabaseReference resimAlmaYolu=FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());

        resimAlmaYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici =dataSnapshot.getValue(Kullanici.class);
                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(profil_resmi);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void yorumlariOku(){
        DatabaseReference yorumlariOkuma=FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId);
        yorumlariOkuma.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                yorumList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Yorum yorum =snapshot.getValue(Yorum.class);
                    yorumList.add(yorum);
                }
                yorumAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}