package com.example.petsee.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petsee.AnaSayfa;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.Model.Yorum;
import com.example.petsee.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class YorumAdapter extends RecyclerView.Adapter<YorumAdapter.ViewHolder>{

    private Context mContext;
    private List<Yorum> mYorumList;
    private String gonderiId;
    private FirebaseUser mevcutKullanici;

    public YorumAdapter(Context mContext, List<Yorum> mYorumList,String gonderiId) {
        this.mContext = mContext;
        this.mYorumList = mYorumList;
        this.gonderiId = gonderiId;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.yorum_ogesi,parent,false);
        return new YorumAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        final Yorum yorum = mYorumList.get(i);
        holder.txt_yorum.setText(yorum.getYorum());
        kullaniciBilgisiAl(holder.profil_resmi,holder.txt_kullaniciAdi,yorum.getGonderen());
        //yorum yapanlara tıklanınca profil sayfalarına atan kısım.
        holder.txt_yorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnaSayfa.class);
                intent.putExtra("gonderenId",yorum.getGonderen());
                mContext.startActivity(intent);
            }
        });
        holder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AnaSayfa.class);
                intent.putExtra("gonderenId",yorum.getGonderen());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (yorum.getGonderen().equals(mevcutKullanici.getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Yorumu silmek istiyor musun ?");
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Hayır",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Evet",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Yorumlar")
                                            .child(gonderiId).child(yorum.getYorumid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "Silindi!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return mYorumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profil_resmi;
        public TextView txt_kullaniciAdi,txt_yorum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profil_resmi= itemView.findViewById(R.id.profil_resmi);
            txt_kullaniciAdi=itemView.findViewById(R.id.txt_kullaniciAdi);
            txt_yorum=itemView.findViewById(R.id.txt_yorum);
        }
    }
    private void kullaniciBilgisiAl(final ImageView imageView,final TextView kullaniciAdi, String gonderenId){
        DatabaseReference gonderenIdYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(gonderenId);
        gonderenIdYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                    Glide.with(mContext).load(kullanici.getResimurl()).into(imageView);
                    kullaniciAdi.setText(kullanici.getKullaniciadi());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}