package android.ebs.zunderapp;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;

public class Login extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private TextView profileTextiew, profileTittle;
    private FirebaseAuth mAuth;
    private ImageView profilePic, save, delete;

    private Uri uriProfileImage;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profilePic = (ImageView) findViewById(R.id.profilePic);
        save = (ImageView) findViewById(R.id.saveBtn);
        delete = (ImageView) findViewById(R.id.deleteBtn);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                saveUserInformation();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        loadUserInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profilePic.setImageBitmap(bitmap);
                save.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Login.this, "New Image Added", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageReference = FirebaseStorage.getInstance().
                getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(profilePic);
            }
            if (user.getDisplayName() != null) {
                String displayName = user.getDisplayName();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);

    }

}