package com.example.petsee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

public class MesajGondermeAdapter extends RecyclerView.Adapter<MesajGondermeAdapter.ViewHolder>  {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    private Context mContext;
    private List<Mesaj> mMesajList;
    private String resimUrl;

    FirebaseUser firebaseUser;
 //   String kullaniciId;
//    Intent intent;

    public MesajGondermeAdapter(Context mContext, List<Mesaj> mMesajList,String resimUrl) {
        this.mContext = mContext;
        this.mMesajList = mMesajList;
        this.resimUrl = resimUrl;
    }

    @NonNull
    @Override
    public MesajGondermeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_ogesi_gonderici,parent,false);
            return new MesajGondermeAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.mesaj_ogesi_alici,parent,false);
            return new MesajGondermeAdapter.ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MesajGondermeAdapter.ViewHolder holder,int position) {
        Mesaj mesaj = mMesajList.get(position);
        holder.show_mesaj.setText(mesaj.getMesaj());
        if (resimUrl.equals("default")){
         holder.profilResmi.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(resimUrl).into(holder.profilResmi);
        }
    }
    @Override
    public int getItemCount() {
        return mMesajList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_mesaj;
        public CircleImageView profilResmi;
        private ImageView img_on,img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_mesaj=itemView.findViewById(R.id.show_mesaj);
            profilResmi=itemView.findViewById(R.id.profil_resmi);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
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
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMesajList.get(position).getGonderen().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}