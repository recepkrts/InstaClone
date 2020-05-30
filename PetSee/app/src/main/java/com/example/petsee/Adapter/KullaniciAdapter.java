package com.example.petsee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petsee.AnaSayfa;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.R;
import com.example.petsee.cerceve.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KullaniciAdapter extends RecyclerView.Adapter<KullaniciAdapter.ViewHolder> {

    private Context mContext;
    private List<Kullanici> mKullanicilar;
    private FirebaseUser firebaseKullanici;
    private boolean isfragment;

    public KullaniciAdapter(Context mContext, List<Kullanici> mKullanicilar,boolean isfragment) {
        this.mContext = mContext;
        this.mKullanicilar = mKullanicilar;
        this.isfragment = isfragment;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kullanici_ogesi,parent,false);
        return new KullaniciAdapter.ViewHolder (view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        firebaseKullanici= FirebaseAuth.getInstance().getCurrentUser();
        final Kullanici kullanici=mKullanicilar.get(i);
        holder.btn_takip_et.setVisibility(View.VISIBLE);
        holder.kullaniciadi.setText(kullanici.getKullaniciadi());
        holder.ad.setText(kullanici.getAd()+" ");
        holder.soyad.setText(kullanici.getSoyad());
        Glide.with(mContext).load(kullanici.getResimurl()).into(holder.profil_resmi);//veri tabanından url ile resim göstermek
        takipEdiliyor(kullanici.getId(),holder.btn_takip_et);

        if (kullanici.getId().equals(firebaseKullanici.getUid())){
            holder.btn_takip_et.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profilId", kullanici.getId());
                    editor.apply();
                    editor.putString("macadresi", kullanici.getMacadresi());
                    editor.apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                            new ProfileFragment()).commit();
                }else {
                    Intent intent = new Intent(mContext, AnaSayfa.class);
                    intent.putExtra("gonderenId",kullanici.getId());
                    mContext.startActivity(intent);
                }
            }
        });
        holder.btn_takip_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (holder.btn_takip_et.getText().toString().equals("Takip Et")){//Butonun texti Takip et ise yapılacak işlemler
                FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid())
                        .child("takipEdilenler").child(kullanici.getId()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId())
                        .child("takipciler").child(firebaseKullanici.getUid()).setValue(true);
                bildirimEkle(kullanici.getId());
            }else {//Butonun texti takip et değilse yapılacak işlemler
                FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid())
                        .child("takipEdilenler").child(kullanici.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId())
                        .child("takipciler").child(firebaseKullanici.getUid()).removeValue();
            }
            }
        });
    }
    private void bildirimEkle(String kullaniciId){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Bildirimler").child(kullaniciId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciId",firebaseKullanici.getUid());
        hashMap.put("text","seni takip etmeye başladı");
        hashMap.put("gonderiId","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }
    @Override
    public int getItemCount() {
        return mKullanicilar.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView kullaniciadi;
        public TextView ad;
        public TextView soyad;
        public CircleImageView profil_resmi;
        public Button btn_takip_et;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ad=itemView.findViewById(R.id.txt_ad);
            kullaniciadi=itemView.findViewById(R.id.txt_kullaniciAdi);
            soyad=itemView.findViewById(R.id.txt_soyad);
            profil_resmi=itemView.findViewById(R.id.profil_resmi);
            btn_takip_et=itemView.findViewById(R.id.btn_takipEt);
        }
    }
    private void takipEdiliyor (final String kullaniciId, final Button button){
        DatabaseReference takipYolu= FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(firebaseKullanici.getUid()).child("takipEdilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(kullaniciId).exists()){
                    button.setText("Takip Ediliyor");
                }else{
                    button.setText("Takip Et");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}