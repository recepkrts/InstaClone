package com.example.petsee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Kayit extends AppCompatActivity {
    private final FirebaseDatabase data= FirebaseDatabase.getInstance();
    private final FirebaseAuth firebase= FirebaseAuth.getInstance();
    ProgressDialog pd;
    DatabaseReference yol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);

        final String mac_adresi=get_mac_adres();

        final EditText ad1 = (EditText) findViewById(R.id.edt1);
        final EditText soyad1 = (EditText) findViewById(R.id.edt2);
        final EditText mail1 = (EditText) findViewById(R.id.edt3);
        final EditText sifre1 = (EditText) findViewById(R.id.edt4);
        final EditText kAdi1 = (EditText) findViewById(R.id.edt_kullanici_adi);
        final Button kayit1 = (Button) findViewById(R.id.btn3);
        final TextView txtGiris = (TextView) findViewById(R.id.txt_giris);

        txtGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Kayit.this,MainActivity.class));
            }
        });

        kayit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd = new ProgressDialog(Kayit.this);
                pd.setMessage("Lütfen Bekleyiniz...");
                pd.show();

                String str_ad = ad1.getText().toString();
                String str_soyad = soyad1.getText().toString();
                String str_kullaniciAdi = kAdi1.getText().toString();
                String str_mailAd = mail1.getText().toString();
                String str_paswd = sifre1.getText().toString();
                String str_mac=mac_adresi;

                if (TextUtils.isEmpty(str_ad) || TextUtils.isEmpty(str_soyad)|| TextUtils.isEmpty(str_kullaniciAdi)
                        || TextUtils.isEmpty(str_mailAd)|| TextUtils.isEmpty(str_paswd)){
                    pd.dismiss();
                    Toast.makeText(Kayit.this, "Lütfen Bütün Alanları Doldurun...", Toast.LENGTH_LONG).show();
                }else if (str_paswd.length()<6){
                    pd.dismiss();
                    Toast.makeText(Kayit.this, "Şifreniz minimum 6 karakter olmalıdır...", Toast.LENGTH_LONG).show();
                }else{
                    //Yeni Kullanıcı Ekleme Kodlarını Çağırma
                    kullaniciOlustur(str_ad,str_soyad,str_kullaniciAdi,str_mac,str_mailAd,str_paswd);
                }
            }
        });
    }

    private  void kullaniciOlustur(final String Ad,final String Soyad,final String kullaniciAdi,
                                   final String mac_adresi, String mailAd, String paswd){
      //Kullanıcı Ekleme Kodları
        firebase.createUserWithEmailAndPassword(mailAd,paswd).addOnCompleteListener(Kayit.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser fireKullanici=firebase.getCurrentUser();

                    String kullaniciId = fireKullanici.getUid();
                    yol = data.getInstance().getReference().child("Kullanicilar").child(kullaniciId);

                    HashMap<String,Object> hashMap = new HashMap<>();

                    hashMap.put("id",kullaniciId);
                    hashMap.put("kullaniciadi",kullaniciAdi.toLowerCase());
                    hashMap.put("ad",Ad);
                    hashMap.put("soyad",Soyad);
                    hashMap.put("bio","");
                    hashMap.put("cins","");
                    hashMap.put("macadresi",mac_adresi);
                    hashMap.put("resimurl","https://firebasestorage.googleapis.com/v0/b/petsee-de689.appspot.com/o/placeholder.png?alt=media&token=857d5a21-2f32-46be-bd05-7bf2ac5e5ecb");

        yol.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(Kayit.this, "Kayıt Başarılı", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent(Kayit.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            });
                }else{
                    pd.dismiss();
                    Toast.makeText(Kayit.this, "Bu mail ve şifreyle kayıt başarısızdır", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private String get_mac_adres(){
        ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        try {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo= wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        }catch (Exception e){
            return "00:00:00:00:00:00";
        }
    }
}