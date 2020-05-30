package com.example.petsee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petsee.Adapter.KullaniciAdapter;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.cerceve.ProfileFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilDuzenle extends FragmentActivity {

    private final FirebaseDatabase data= FirebaseDatabase.getInstance();
    private final FirebaseAuth firebase= FirebaseAuth.getInstance();
    private final FirebaseUser mevcutKullanici = firebase.getCurrentUser();
    DatabaseReference yol;
    private Uri resimUri;
    private StorageTask yüklemeGörevi;
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("profilResmi");
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        final MaterialEditText ad_ProfiliDuzenle = findViewById(R.id.edt_ad_profiliDuzenle);
        final MaterialEditText soyad_ProfiliDuzenle = findViewById(R.id.edt_Soyad_profiliDuzenle);
        final MaterialEditText kAdi_profiliDuzenle = findViewById(R.id.edt_petAdi_profilDuzenle);
        final MaterialEditText bio_profiliDuzenle = findViewById(R.id.edt_bio_profiliDuzenle);
        final MaterialEditText cins_profiliDuzenle = findViewById(R.id.edt_cins_profiliDuzenle);
        final ImageView image_geri = findViewById(R.id.geri_profilDuzenle);

        ad_ProfiliDuzenle.setText("");
        soyad_ProfiliDuzenle.setText("");
        bio_profiliDuzenle.setText("");
        kAdi_profiliDuzenle.setText("");
        cins_profiliDuzenle.setText("");

        final CircleImageView foto_profiliDuzenle=findViewById(R.id.profil_resmi_profiliDuzenle);
        final TextView txt_fotoSec=findViewById(R.id.txt_foto_degistir_profiliDuzenle);
        final TextView txt_kaydet=findViewById(R.id.txt_kaydet_profilDuzenle);

        yol=data.getReference("Kullanicilar").child(mevcutKullanici.getUid());
        yol.addValueEventListener(new ValueEventListener() {//verileri edittext de göstermek için
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                    if (mevcutKullanici.getUid().equals(kullanici.getId())){
                        ad_ProfiliDuzenle.setText(kullanici.getAd());
                        soyad_ProfiliDuzenle.setText(kullanici.getSoyad());
                        bio_profiliDuzenle.setText(kullanici.getBio());
                        kAdi_profiliDuzenle.setText(kullanici.getKullaniciadi());
                        cins_profiliDuzenle.setText(kullanici.getCins());
                        Picasso.get().load(kullanici.getResimurl()).placeholder(R.color.colorBlack).into(foto_profiliDuzenle);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        image_geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
        txt_fotoSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //foto değiştire tıklandığında yapılacak işlemler.
                CropImage.activity()
                   .setAspectRatio(1,1)
                   .start(ProfilDuzenle.this);
            }
        });
        foto_profiliDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fotoya tıklandığında yapılacak işlemler.
                CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(ProfilDuzenle.this);
            }
        });

        txt_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(ProfilDuzenle.this);
                pd.setMessage("Lütfen Bekleyiniz...");
                pd.show();
                String str_ad = ad_ProfiliDuzenle.getText().toString();
                String str_soyad = soyad_ProfiliDuzenle.getText().toString();
                String str_kullaniciAdi = kAdi_profiliDuzenle.getText().toString().toLowerCase();
                String str_bio=bio_profiliDuzenle.getText().toString();
                String str_cins=cins_profiliDuzenle.getText().toString();

                profilGuncelle(str_ad,str_soyad,str_kullaniciAdi,str_bio,str_cins);
                pd.dismiss();
                Toast.makeText(ProfilDuzenle.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void profilGuncelle(String str_ad, String str_soyad, String str_kullaniciAdi,
                                String str_bio, String str_cins) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ad",str_ad);
        hashMap.put("bio",str_bio);
        hashMap.put("cins",str_cins);
        hashMap.put("kullaniciadi",str_kullaniciAdi);
        hashMap.put("soyad",str_soyad);
        reference.updateChildren(hashMap);
    }
    private String dosyaYolu(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void resimYükle(){
        pd = new ProgressDialog(ProfilDuzenle.this);
        pd.setMessage("Yükleniyor...");
        pd.show();

        if (resimUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                +"."+ dosyaYolu(resimUri));
            yüklemeGörevi = fileReference.putFile(resimUri);
            yüklemeGörevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri indirmeUrisi = task.getResult();
                    String myUrl = indirmeUrisi.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("resimurl",""+myUrl);
                    reference.updateChildren(hashMap);
                    pd.dismiss();
                }else{
                    Toast.makeText(ProfilDuzenle.this, "Hata! Yükleme Başarısız", Toast.LENGTH_SHORT).show();
                }
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilDuzenle.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Fotoğraf seçilemedi", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri = result.getUri();
            resimYükle();
        }else{
            Toast.makeText(this, "Hata! Birşeyler ters gitti", Toast.LENGTH_SHORT).show();
        }
    }
}