package com.example.petsee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class StoryEkleActivity extends AppCompatActivity {

    Uri resimUri;
    String myUrl = "";
    StorageTask storageTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_ekle);

        storageReference = FirebaseStorage.getInstance().getReference("hikaye");
        CropImage.activity().setAspectRatio(16,16).start(StoryEkleActivity.this);
    }
    private String dosyaUzantisiAl(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void hikayePaylas(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Paylaşılıyor...");
        pd.show();

        if (resimUri != null){
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                +"."+dosyaUzantisiAl(resimUri));
            storageTask = imageReference.putFile(resimUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri= task.getResult();
                        myUrl =downloadUri.toString();

                        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference veriYolu = FirebaseDatabase.getInstance().getReference("Hikaye").child(myId);
                        String hikayeId = veriYolu.push().getKey();

                        long timeend = System.currentTimeMillis()+86400000; //1 Gün
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl",myUrl);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timeend",timeend);
                        hashMap.put("storyid",hikayeId);
                        hashMap.put("userid",myId);
                        veriYolu.child(hikayeId).setValue(hashMap);
                        pd.dismiss();
                        finish();
                    }else{
                        Toast.makeText(StoryEkleActivity.this, "Hata ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StoryEkleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Fotoğraf Seçilemedi...", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri= result.getUri();
            hikayePaylas();
        }else{
            Toast.makeText(this, "Birşeyler ters gitti!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoryEkleActivity.this,AnaSayfa.class));
            finish();
        }
    }
}