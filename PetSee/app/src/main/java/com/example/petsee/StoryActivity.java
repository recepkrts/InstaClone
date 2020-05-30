package com.example.petsee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petsee.Model.Kullanici;
import com.example.petsee.Model.Story;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;
    StoriesProgressView storiesProgressView;
    ImageView image,story_photo,story_delete;
    TextView story_username,seen_number;
    List<String> images;
    List<String> storyids;
    String userid;
    LinearLayout r_seen;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stori);

        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        story_delete = findViewById(R.id.story_delete);
        storiesProgressView = findViewById(R.id.stories);
        image=findViewById(R.id.image);
        story_photo=findViewById(R.id.story_photo);
        story_username=findViewById(R.id.story_username);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        userid = getIntent().getStringExtra("userid");

        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }
        //metotları çağırma
        hikayeleriAl(userid);
        kullaniciBilgisi(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this,TakipciActivity.class);
                intent.putExtra("id",userid);
                intent.putExtra("storyid",storyids.get(counter));
                intent.putExtra("baslik","Görüntüleyenler");
                startActivity(intent);
            }
        });
        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hikaye")
                        .child(userid).child(storyids.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(StoryActivity.this, "Silindi!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onNext() {
        Picasso.get().load(images.get(++counter)).placeholder(R.color.colorBlack).into(image);
        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }
    @Override
    public void onPrev() {
        if ((counter - 1)<0) return;
        Picasso.get().load(images.get(--counter)).placeholder(R.color.colorBlack).into(image);
        seenNumber(storyids.get(counter));
    }
    @Override
    public void onComplete() {
        finish();
    }
    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }
    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }
    private void hikayeleriAl(String userid){
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hikaye")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    long mevcutsure =  System.currentTimeMillis();
                    if (mevcutsure > story.getTimestart() && mevcutsure < story.getTimeend()){
                        images.add(story.getImageurl());
                        Log.d("storyurl"," : "+story.getImageurl());
                        storyids.add(story.getStoryid());
                        Log.d("storyid"," : "+story.getStoryid());
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);
                Picasso.get().load(images.get(counter)).placeholder(R.color.colorBlack).into(image);
                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void kullaniciBilgisi(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici = dataSnapshot.getValue(Kullanici.class);
                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(story_photo);
                story_username.setText(kullanici.getKullaniciadi());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void addView(String storyid){
        FirebaseDatabase.getInstance().getReference("Hikaye").child(userid).child(storyid)
                .child("goruldu").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }
    private void seenNumber(String storyid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hikaye")
                .child(userid).child(storyid).child("goruldu");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seen_number.setText(""+dataSnapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}