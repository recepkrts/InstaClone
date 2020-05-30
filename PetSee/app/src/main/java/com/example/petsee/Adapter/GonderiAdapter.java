package com.example.petsee.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petsee.Model.Gonderi;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.R;
import com.example.petsee.TakipciActivity;
import com.example.petsee.YorumlarActivity;
import com.example.petsee.cerceve.GonderiAyrintilarFragment;
import com.example.petsee.cerceve.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class GonderiAdapter extends  RecyclerView.Adapter<GonderiAdapter.ViewHolder>{

    public Context mContext;
    public List<Gonderi> gonderiList;

    private FirebaseUser firebaseUser;

    public GonderiAdapter(Context mContext, List<Gonderi> gonderiList) {
        this.mContext = mContext;
        this.gonderiList = gonderiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gonderi_ogesi,viewGroup,false);
        return new GonderiAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Gonderi gonderi = gonderiList.get(i);
        Glide.with(mContext).load(gonderi.getGonderiResmi())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(viewHolder.gonderi_resmi);
        if (gonderi.getGonderiHakkinda().equals("")){
            viewHolder.txt_gonderiHakkinda.setVisibility(View.GONE);
        }else{
            viewHolder.txt_gonderiHakkinda.setVisibility(View.VISIBLE);
            viewHolder.txt_gonderiHakkinda.setText(gonderi.getGonderiHakkinda());
        }
        //metotları çağırma
        gonderen(viewHolder.profil_resmi,viewHolder.txt_kullaniciAdi,viewHolder.txt_gonderen,gonderi.getGonderen());
        begenildi(gonderi.getGonderiId(),viewHolder.begeni_resmi);
        begeniSayisi(viewHolder.txt_begeniSayisi,gonderi.getGonderiId());
        yorumlariAl(gonderi.getGonderiId(),viewHolder.txt_yorumlar);
        kayitlar(gonderi.getGonderiId(),viewHolder.kaydetme_resmi);

        viewHolder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profilId",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                        new ProfileFragment()).commit();
            }
        });
        viewHolder.txt_kullaniciAdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profilId",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                        new ProfileFragment()).commit();
            }
        });
        viewHolder.txt_gonderen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profilId",gonderi.getGonderen());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                        new ProfileFragment()).commit();
            }
        });
        viewHolder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("gonderiId",gonderi.getGonderiId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici,
                        new GonderiAyrintilarFragment()).commit();
            }
        });

        viewHolder.kaydetme_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.kaydetme_resmi.getTag().equals("kaydet")){
                    FirebaseDatabase.getInstance().getReference().child("kaydedilenler").child(firebaseUser.getUid())
                            .child(gonderi.getGonderiId()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("kaydedilenler").child(firebaseUser.getUid())
                            .child(gonderi.getGonderiId()).removeValue();
                }
            }
        });

        viewHolder.begeni_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.begeni_resmi.getTag().equals("begen")){
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(firebaseUser.getUid()).setValue(true);
                    bildirimEkle(gonderi.getGonderen(),gonderi.getGonderiId());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderi.getGonderiId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        viewHolder.yorum_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,YorumlarActivity.class);
                intent.putExtra("gonderiId",gonderi.getGonderiId());
                intent.putExtra("gonderenId",gonderi.getGonderen());
                mContext.startActivity(intent);
            }
        });
        viewHolder.txt_yorumlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,YorumlarActivity.class);
                intent.putExtra("gonderiId",gonderi.getGonderiId());
                intent.putExtra("gonderenId",gonderi.getGonderen());
                mContext.startActivity(intent);
            }
        });
        viewHolder.txt_begeniSayisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TakipciActivity.class);
                intent.putExtra("id",gonderi.getGonderiId());
                intent.putExtra("baslik","begeni");
                mContext.startActivity(intent);
            }
        });
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.düzenle:
                                goneriDuzenle(gonderi.getGonderiId());
                                return true;
                            case R.id.sil:
                                FirebaseDatabase.getInstance().getReference("Gonderiler")
                                        .child(gonderi.getGonderiId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Silindi ! ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                return true;
                            case R.id.bildir:
                                //bildire tıklanınca yapılacak işlemler
                                Toast.makeText(mContext, "Bildire tıklandı", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.gonderi_menu);
                if (!gonderi.getGonderen().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.düzenle).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.sil).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return gonderiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profil_resmi,gonderi_resmi,begeni_resmi,yorum_resmi,kaydetme_resmi,more;
        public TextView txt_kullaniciAdi,txt_begeniSayisi,txt_gonderen,txt_gonderiHakkinda,txt_yorumlar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profil_resmi = itemView.findViewById(R.id.profil_resmi);
            gonderi_resmi = itemView.findViewById(R.id.gonderi_resmi);
            begeni_resmi = itemView.findViewById(R.id.begeni);
            yorum_resmi = itemView.findViewById(R.id.yorum);
            kaydetme_resmi = itemView.findViewById(R.id.kaydet);
            txt_kullaniciAdi = itemView.findViewById(R.id.txt_kullaniciAdi);
            txt_begeniSayisi = itemView.findViewById(R.id.txt_begeniler);
            txt_gonderen = itemView.findViewById(R.id.txt_gonderen);
            txt_gonderiHakkinda = itemView.findViewById(R.id.txt_gonderiHakkinda);
            txt_yorumlar = itemView.findViewById(R.id.txt_yorum);
            more = itemView.findViewById(R.id.more);
        }
    }
    private void bildirimEkle(String kullaniciId,String gonderiId){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Bildirimler").child(kullaniciId);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciId",firebaseUser.getUid());
        hashMap.put("text","gönderiniz beğenildi");
        hashMap.put("gonderiId",gonderiId);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }
    private void yorumlariAl(String gonderiId, final TextView yorum){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Yorumlar").child(gonderiId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    yorum.setText(dataSnapshot.getChildrenCount()+" yorumun tümünü görüntüle");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }
    private void begenildi(String gonderiId, final ImageView imageView){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderiId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_begenildi);
                    imageView.setTag("begenildi");
                }else{
                    imageView.setImageResource(R.drawable.ic_begeni);
                    imageView.setTag("begen");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void begeniSayisi(final TextView begeniler, String gonderiId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Begeniler").child(gonderiId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                begeniler.setText(dataSnapshot.getChildrenCount()+" begeni");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void gonderen(final ImageView profil_resmi, final TextView kullaniciAdi, final TextView gonderen, final String kullaniciId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(kullaniciId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(mContext).load(kullanici.getResimurl()).placeholder(R.drawable.placeholder).into(profil_resmi);
                kullaniciAdi.setText(kullanici.getKullaniciadi());
                gonderen.setText(kullanici.getKullaniciadi());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void kayitlar(final String gonderiId, final ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("kaydedilenler")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(gonderiId).exists()){
                    imageView.setImageResource(R.drawable.ic_kaydet_s);
                    imageView.setTag("kaydedildi");
                }else{
                    imageView.setImageResource(R.drawable.ic_kaydet_siyah);
                    imageView.setTag("kaydet");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void goneriDuzenle(final String gonderiId){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Gonderi Duzenle");
        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        getText(gonderiId,editText);
        alertDialog.setPositiveButton("Duzenle",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterfacelog, int i) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("gonderiHakkinda",editText.getText().toString());
                        FirebaseDatabase.getInstance().getReference("Gonderiler").child(gonderiId)
                                .updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Iptal",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    private void getText(String gonderiId, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Gonderiler").child(gonderiId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Gonderi.class).getGonderiHakkinda());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}