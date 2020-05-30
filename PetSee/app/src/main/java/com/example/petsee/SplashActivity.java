package com.example.petsee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petsee.Adapter.KullaniciAdapter;
import com.example.petsee.Adapter.YorumAdapter;
import com.example.petsee.Model.Kullanici;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {
  /*  TextView txt_oku;
    String mac_adress;
    FirebaseUser mevcutKullanici;
    FirebaseAuth mYetki;*/

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

/*        txt_oku=findViewById(R.id.txt_oku);
        mYetki=FirebaseAuth.getInstance();
        mevcutKullanici=mYetki.getCurrentUser();
        macGetir();
        mac_adress=get_mac_adress();*/

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(4800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                   /* if (mac_adress.equals(txt_oku.getText().toString())){
                        startActivity(new Intent(SplashActivity.this,AnaSayfa.class));
                        finish();
                    }else{*/
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    //}
                }
            }
        };
        timerThread.start();
    }
  /*  private String get_mac_adress(){
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
    private void macGetir(){
        txt_oku.setText("");
        DatabaseReference gelenMac = FirebaseDatabase.getInstance().getReference("Kullanicilar");
        gelenMac.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Kullanici kullanici = snapshot.getValue(Kullanici.class);
                    if (mevcutKullanici.getUid().equals(kullanici.getId())){
                        txt_oku.append(snapshot.getValue(Kullanici.class).getMacadresi());
                    }else{
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
   /* private void durum(String durum){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(mevcutKullanici.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("durum",durum);
        reference.updateChildren(hashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        durum("çevrimiçi");
    }*/
    @Override
    protected void onPause() {
        super.onPause();
       // durum("çevrimdışı");
        finish();
    }
}