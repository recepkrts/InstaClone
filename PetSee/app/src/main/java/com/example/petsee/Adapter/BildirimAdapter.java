package com.example.petsee.Adapter;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petsee.Model.Bildirim;
import com.example.petsee.Model.Gonderi;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.R;
import com.example.petsee.cerceve.GonderiAyrintilarFragment;
import com.example.petsee.cerceve.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BildirimAdapter extends RecyclerView.Adapter<BildirimAdapter.ViewHolder>{

    private Context mContext;
    private List<Bildirim> bildirimList;

    public BildirimAdapter(Context mContext, List<Bildirim> bildirimList) {
        this.mContext = mContext;
        this.bildirimList = bildirimList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bildirim_ogesi,viewGroup,false);
        return new BildirimAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final Bildirim bildirim = bildirimList.get(i);
        holder.txt_bildirim.setText(bildirim.getText());
        kullaniciBilgisiAl(holder.profil_resmi,holder.txt_kAdi,bildirim.getKullaniciId());
        if (bildirim.isIspost()){
            holder.gonderi_resmi.setVisibility(View.VISIBLE);
            gonderiResminiAl(holder.gonderi_resmi,bildirim.getGonderiId());
        }else {
            holder.gonderi_resmi.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bildirim.isIspost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("gonderiId",bildirim.getGonderiId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                            new GonderiAyrintilarFragment()).commit();
                }else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profilId",bildirim.getKullaniciId()).commit();
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                            new ProfileFragment()).commit();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return bildirimList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profil_resmi,gonderi_resmi;
        public TextView txt_kAdi,txt_bildirim;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profil_resmi=itemView.findViewById(R.id.profilResmi_bildirimOgesi);
            gonderi_resmi=itemView.findViewById(R.id.gonderiResmi_bildirimOgesi);
            txt_kAdi=itemView.findViewById(R.id.txt_kAdi_bildirimOgesi);
            txt_bildirim=itemView.findViewById(R.id.txt_yorum_bildirimOgesi);
        }
    }
    private void kullaniciBilgisiAl(final ImageView imageView, final TextView kAdi, String gonderenId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(gonderenId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(mContext).load(kullanici.getResimurl()).into(imageView);
                kAdi.setText(kullanici.getKullaniciadi());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void gonderiResminiAl(final ImageView imageView, String gonderiId){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Gonderiler").child(gonderiId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Gonderi gonderi = dataSnapshot.getValue(Gonderi.class);
                    Glide.with(mContext).load(gonderi.getGonderiResmi()).into(imageView);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }
}