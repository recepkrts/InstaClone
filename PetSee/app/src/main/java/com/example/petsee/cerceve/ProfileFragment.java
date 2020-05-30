package com.example.petsee.cerceve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.petsee.Adapter.GonderiAdapter;
import com.example.petsee.Ayarlar;
import com.example.petsee.Hakkinda;
import com.example.petsee.MainActivity;
import com.example.petsee.Model.Gonderi;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.ProfilDuzenle;
import com.example.petsee.R;
import com.example.petsee.TakipciActivity;
import com.google.android.material.navigation.NavigationView;
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

public class ProfileFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Gonderi> gonderiList;
    private GonderiAdapter gonderiAdapter;
    private RecyclerView rProfil_cerceve;
    private FirebaseUser mevcutKullanici;
    private DrawerLayout drawerLayout;
    private NavigationView leftMenu;
    private FirebaseAuth auth;
    private List<Gonderi> gonderiList_kaydedilenler;
    private GonderiAdapter gonderiAdapter_kayit;
    private List<String> mySaves;

    ImageView resimSecenekler,profil_resmi;
    TextView txt_gonderiler,txt_takipciler,txt_takipEdilenler,txt_Ad,txt_bio,txt_kullaniciAdi,txt_cins;
    Button btn_profiliDuzenle;
    ImageButton imagebtn_fotograflarim,imagebtn_kaydedilenFotograflar;
    String profilId;
    RecyclerView rKaydet_profilCerceve;

    public ProfileFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        rProfil_cerceve=view.findViewById(R.id.rProfilCerceve);
        rProfil_cerceve.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rProfil_cerceve.setLayoutManager(linearLayoutManager);
        gonderiList = new ArrayList<>();
        gonderiAdapter= new GonderiAdapter(getContext(),gonderiList);
        rProfil_cerceve.setAdapter(gonderiAdapter);

        rKaydet_profilCerceve=view.findViewById(R.id.rKaydet_profilCerceve);
        rKaydet_profilCerceve.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager_kayit = new LinearLayoutManager(getContext());
        linearLayoutManager_kayit.setReverseLayout(true);
        rKaydet_profilCerceve.setLayoutManager(linearLayoutManager_kayit);
        gonderiList_kaydedilenler=new ArrayList<>();
        gonderiAdapter_kayit=new GonderiAdapter(getContext(),gonderiList_kaydedilenler);
        rKaydet_profilCerceve.setAdapter(gonderiAdapter_kayit);

        rProfil_cerceve.setVisibility(View.VISIBLE);
        rKaydet_profilCerceve.setVisibility(View.GONE);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        profilId = sharedPreferences.getString("profilId","none");

        resimSecenekler = view.findViewById(R.id.secenekler_profilCerceve);
        profil_resmi = view.findViewById(R.id.profil_resmi);

        resimSecenekler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(leftMenu);
            }
        });

        txt_gonderiler=view.findViewById(R.id.txt_gonderiler);
        txt_takipciler=view.findViewById(R.id.txt_takipciler);
        txt_takipEdilenler=view.findViewById(R.id.txt_takipEdilenler);
        txt_bio=view.findViewById(R.id.txt_bio);
        txt_Ad=view.findViewById(R.id.txt_ad);
        txt_kullaniciAdi=view.findViewById(R.id.txt_kullaniciAdi);
        txt_cins=view.findViewById(R.id.txt_cins);
        btn_profiliDuzenle=view.findViewById(R.id.btn_profiliDuzenle);
        imagebtn_fotograflarim=view.findViewById(R.id.imageBtn_fotograflarim);
        imagebtn_kaydedilenFotograflar=view.findViewById(R.id.imageBtn_kaydedilenler);

        //metotları çağırma
        kullaniciBilgisi();
        takipcileriAl();
        gonderiSayisiAl();
        gonderileriOku();
        kaydedilenleriAl();

        if (profilId.equals(mevcutKullanici.getUid())){
            btn_profiliDuzenle.setText("Profili Düzenle");

            drawerLayout = view.findViewById(R.id.drawer);
            leftMenu = view.findViewById(R.id.secenekler_profilFragment);
            leftMenu.setNavigationItemSelectedListener(this);

            imagebtn_fotograflarim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rProfil_cerceve.setVisibility(View.VISIBLE);
                    rKaydet_profilCerceve.setVisibility(View.GONE);
                }
            });
            imagebtn_kaydedilenFotograflar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rProfil_cerceve.setVisibility(View.GONE);
                    rKaydet_profilCerceve.setVisibility(View.VISIBLE);
                }
            });
        }else{
            takipKontrolü();
            imagebtn_kaydedilenFotograflar.setVisibility(View.GONE);
            resimSecenekler.setVisibility(View.GONE);
        }

        btn_profiliDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btn_profiliDuzenle.getText().toString();
                if (btn.equals("Profili Düzenle")){
                    //profil düzenle sayfasına gitsin
                   startActivity(new Intent(getContext(),ProfilDuzenle.class));
                }else if (btn.equals("takip et")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profilId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipciler").child(mevcutKullanici.getUid()).setValue(true);
                    bildirimEkle();
                }else if (btn.equals("takip ediliyor")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profilId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profilId)
                            .child("takipciler").child(mevcutKullanici.getUid()).removeValue();
                }
            }
        });
        txt_takipciler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TakipciActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takipciler");
                startActivity(intent);
            }
        });
        txt_takipEdilenler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TakipciActivity.class);
                intent.putExtra("id",profilId);
                intent.putExtra("baslik","takipEdilenler");
                startActivity(intent);
            }
        });
        return view;
    }
    private void bildirimEkle(){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Bildirimler").child(profilId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciId",mevcutKullanici.getUid());
        hashMap.put("text","seni takip etmeye başladı");
        hashMap.put("gonderiId","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }
    private void kullaniciBilgisi(){
        DatabaseReference kullaniciYolu = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(profilId);
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (getContext() == null){
                  return;
           }
           Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(getContext()).load(kullanici.getResimurl()).into(profil_resmi);
                txt_kullaniciAdi.setText(kullanici.getKullaniciadi());
                txt_Ad.setText(kullanici.getAd() + " " + kullanici.getSoyad());
                txt_bio.setText(kullanici.getBio());
                txt_cins.setText(kullanici.getCins());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void takipKontrolü(){
        DatabaseReference takipYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(mevcutKullanici.getUid()).child("takipEdilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profilId).exists()){
                    btn_profiliDuzenle.setText("takip ediliyor");
                }else{
                    btn_profiliDuzenle.setText("takip et");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void takipcileriAl(){
        //Takipçi sayısını alır
        DatabaseReference takipciYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profilId).child("takipciler");
        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_takipciler.setText(""+dataSnapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //Takip edilen sayısını alır
        DatabaseReference takipEdilenYolu = FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profilId).child("takipEdilenler");
        takipEdilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_takipEdilenler.setText(""+dataSnapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void gonderiSayisiAl(){
        DatabaseReference gonderiYolu = FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i =0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Gonderi gonderi = snapshot.getValue(Gonderi.class);
                    if (gonderi.getGonderen().equals(profilId)){
                        i++;
                    }
                }
                txt_gonderiler.setText(""+i);
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
                   if (gonderi.getGonderen().equals(profilId)){
                       gonderiList.add(gonderi);
                   }
                }
                gonderiAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void kaydedilenleriAl(){
        mySaves = new ArrayList<>();
        DatabaseReference kayitYolu = FirebaseDatabase.getInstance().getReference("kaydedilenler").child(mevcutKullanici.getUid());
        kayitYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                kayitlariOku();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void kayitlariOku(){
        DatabaseReference yol = FirebaseDatabase.getInstance().getReference("Gonderiler");
        yol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList_kaydedilenler.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                     Gonderi gonderi = snapshot.getValue(Gonderi.class);
                     for (String id : mySaves){
                         if (gonderi.getGonderiId().equals(id)){
                             gonderiList_kaydedilenler.add(gonderi);
                         }
                     }
                }
                gonderiAdapter_kayit.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.nav_ayarlar){
            startActivity(new Intent(getContext(),Ayarlar.class));
        }else if (id == R.id.nav_hakkinda){
            startActivity(new Intent(getContext(), Hakkinda.class));
        }else if (id == R.id.nav_cikis){
            auth.signOut();
            getActivity().finish();
            startActivity(new Intent(getContext(),MainActivity.class));
        }
        return true;
    }
}