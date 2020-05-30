package com.example.petsee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petsee.MesajGonderme;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.Model.Mesaj;
import com.example.petsee.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.ViewHolder>  {

    private Context mContext;
    private List<Kullanici> mKullaniciList;

    String theSonMesaj;

    public MesajAdapter(Context mContext, List<Kullanici> mKullaniciList) {
        this.mContext = mContext;
        this.mKullaniciList = mKullaniciList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_kullanici_ogesi,parent,false);
        return new MesajAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Kullanici kullanici = mKullaniciList.get(position);
        kullaniciBilgisiAl(holder.profilResmi,holder.kullaniciAdi,kullanici.getId());
        Glide.with(mContext).load(kullanici.getResimurl()).into(holder.profilResmi);

        sonMesaj(kullanici.getId(),holder.mesaj);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,MesajGonderme.class);
                intent.putExtra("userid",kullanici.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mKullaniciList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView kullaniciAdi,mesaj;
        public CircleImageView profilResmi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciAdi=itemView.findViewById(R.id.txt_kullaniciAdi);
            mesaj=itemView.findViewById(R.id.txt_mesaj);
            profilResmi=itemView.findViewById(R.id.profil_resmi);
        }
    }
   private void kullaniciBilgisiAl(final CircleImageView profilResmi, final TextView kullaniciAdi, String gonderenid) {
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(gonderenid);
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
               Glide.with(mContext).load(kullanici.getResimurl()).into(profilResmi);
               kullaniciAdi.setText(kullanici.getKullaniciadi());
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
           }
       });
   }
   private void sonMesaj(final String userid, final TextView mesaj){
        theSonMesaj = "default";
       final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Mesajlar");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                   Mesaj mesaj = snapshot.getValue(Mesaj.class);
                   if (mesaj.getAlici().equals(firebaseUser.getUid()) && mesaj.getGonderen().equals(userid) ||
                       mesaj.getGonderen().equals(firebaseUser.getUid()) && mesaj.getAlici().equals(userid)){
                        theSonMesaj = mesaj.getMesaj();
                   }
               }
               switch (theSonMesaj){
                   case "default":
                       mesaj.setText("Mesaj yok");
                       break;
                   default:
                       mesaj.setText(theSonMesaj);
                       break;
               }
               theSonMesaj = "default";
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
           }
       });
   }
}