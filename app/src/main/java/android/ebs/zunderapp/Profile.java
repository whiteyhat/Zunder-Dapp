package android.ebs.zunderapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class Profile extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private TextView name, profileTittle;
    private FirebaseAuth mAuth;
    private ImageView profilePic, addItem;
    private EditText nameInput;
    private Uri uriProfileImage;
    private String profileImageUrl;
    private ImageView saveBtn, cancelBtn, qrButton;
    private ScrollView scrollView;
    private LinearLayout submenu;

    /**
     * Method that creates the screen once it is running.
     * the Main method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Link the XMl elements with the code
        LinkElements();

        //Action listeners for elements
        ActionListeners();

        //Get authentification from DB (Firebase)
        mAuth = FirebaseAuth.getInstance();

        //Load user Information
        loadUserInformation();
    }

    /**
     * Method that generates an action when an element is touched
     */
    private void ActionListeners() {
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameInput.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                submenu.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                submenu.setVisibility(View.VISIBLE);

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                submenu.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Method that links the XMl elements from the Layour to the variables
     * instantiated in this Java class
     */
    private void LinkElements() {
        addItem = (ImageView) findViewById(R.id.addItem);
        saveBtn = (ImageView) findViewById(R.id.saveBtn);
        cancelBtn = (ImageView) findViewById(R.id.deleteBtn);
        nameInput = (EditText)findViewById(R.id.nameInput);
        name = (TextView) findViewById(R.id.ProfileName);
        profileTittle = (TextView) findViewById(R.id.ProfileTitle);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        qrButton = (ImageView) findViewById(R.id.QrButton);
        submenu = (LinearLayout) findViewById(R.id.subMenu);
        scrollView = (ScrollView) findViewById(R.id.scrollMenu);
    }

    /**
     * Method that saves the
     */
    private void saveUserInformation() {


        String displayName = name.getText().toString();

        if (displayName.isEmpty()) {
            name.setError("Name required");
            name.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Method that provides an action when an int is requested and
     * provides an operational result in form of intent (new Screen)
     * @param requestCode int requested as the beginning of the operation
     * @param resultCode int as a result of the operation
     * @param data Intent (Screen) which is directed to
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK  &&  data != null &&
                data.getData() != null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profilePic.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that upload an image to the DB (Firebase)
     */
    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageReference = FirebaseStorage.getInstance().
                getReference("profilepics/"+System.currentTimeMillis()+".jpg");

        if (uriProfileImage != null){
            profileImageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT);
                }
            });
        }
    }

    /**
     * Method that loads the information from the user DB
     */
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

    /**
     * Method which is read when the activity is just created
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }
    }

    /**
     * Mehtod that disploys a native Image Chooser
     * to choose the desired Profile Image
     */
    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);

    }

}
