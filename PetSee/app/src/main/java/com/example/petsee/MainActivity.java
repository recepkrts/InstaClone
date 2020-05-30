package com.example.petsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private final FirebaseDatabase data= FirebaseDatabase.getInstance();
    private final FirebaseAuth firebase= FirebaseAuth.getInstance();

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        final EditText mail = (EditText) findViewById(R.id.email);
        final EditText sifre = (EditText) findViewById(R.id.pass);
        final Button giris = (Button) findViewById(R.id.btn1);
        final Button kayit = (Button) findViewById(R.id.btn2);

        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pdGiris = new ProgressDialog(MainActivity.this);
                pdGiris.setMessage("Giriş Yapılıyor...");
                pdGiris.show();

                String str_mail = mail.getText().toString();
                String str_sifre = sifre.getText().toString();
                if (TextUtils.isEmpty(str_mail) || TextUtils.isEmpty(str_sifre)){
                    pdGiris.dismiss();
                    Toast.makeText(MainActivity.this, "Bütün Alanları Doldurun...", Toast.LENGTH_LONG).show();
                }else {//Giriş Kodları
                    firebase.signInWithEmailAndPassword(str_mail,str_sifre).addOnCompleteListener(MainActivity.this,    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            DatabaseReference yolGiris = data.getReference().
                                    child("Kullanicilar").child(firebase.getCurrentUser().getUid());
                            yolGiris.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    pdGiris.dismiss();
                                    Intent intent =new Intent(MainActivity.this,AnaSayfa.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    pdGiris.dismiss();
                                }
                            });
                        }else{
                            pdGiris.dismiss();
                            Toast.makeText(MainActivity.this, "Giriş Başarısız Oldu...", Toast.LENGTH_LONG).show();
                        }
                        }
                    });
                }
            }
        });
        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Kayit.class));
            }
        });
    }
}