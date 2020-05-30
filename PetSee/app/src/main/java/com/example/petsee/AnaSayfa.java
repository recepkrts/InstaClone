package com.example.petsee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.petsee.cerceve.AramaFragment;
import com.example.petsee.cerceve.BildirimFragment;
import com.example.petsee.cerceve.HomeFragment;
import com.example.petsee.cerceve.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AnaSayfa extends FragmentActivity {

    BottomNavigationView bottomNavigationView;
    Fragment seciliCerceve = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anasayfa_activity);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
                String gonderen = intent.getString("gonderenId");
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profilId", gonderen);
                editor.apply();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici, new ProfileFragment())
                        .addToBackStack(null).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici, new HomeFragment())
                    .addToBackStack(null).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
    new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_home:
                            //ana çerçeve
                            seciliCerceve=new HomeFragment();
                            break;
                        case R.id.nav_arama:
                            //arama çerçeve
                            seciliCerceve=new AramaFragment();
                            break;
                        case R.id.nav_ekle:
                            //ekleme sayfası
                            seciliCerceve=null;
                            startActivity(new Intent(AnaSayfa.this,GonderiActivity.class));
                            break;
                        case R.id.nav_kalp:
                            //bildirim çerçeve
                            seciliCerceve=new BildirimFragment();
                            break;
                        case R.id.nav_profil:
                            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profilId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            //profil çerçeve
                            seciliCerceve=new ProfileFragment();
                            break;
                    }
                    if (seciliCerceve != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_kapsayici, seciliCerceve)
                                .addToBackStack(null).commit();
                    }
                    return true;
            }
    };
}