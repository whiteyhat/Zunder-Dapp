package android.ebs.zunderapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Profile extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private TextView name, tittle;
    private FirebaseAuth mAuth;
    private ImageView profilePic, addItem;
    private EditText nameInput;
    private Uri uriProfileImage;
    private String profileImageUrl;
    private ImageView saveBtn, cancelBtn, qrButton, home, wallet, store, map;
    private ScrollView scrollView;
    private LinearLayout submenu;
    private boolean Bname, Btitle;

    DatabaseReference myRef;

    /**
     * Method that creates the screen once it is running.
     * the Main method
     *
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
        myRef = FirebaseDatabase.getInstance().getReference("user");

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
                Intent intent = new Intent(Profile.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBtitle(true);
                setBname(false);
                nameInput.setVisibility(View.VISIBLE);
                nameInput.setHint("Job role");
                scrollView.setVisibility(View.INVISIBLE);
                submenu.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBtitle(false);
                setBname(true);
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
                if (isBname()) {
                    saveUserInformation();
                }

                if (isBtitle()) {
                    addTitle();
                }
                saveBtn.setVisibility(View.GONE);
                nameInput.setText("");
                nameInput.setVisibility(View.GONE);
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
                nameInput.setVisibility(View.GONE);
                submenu.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addTitle() {

        if (!TextUtils.isEmpty(nameInput.getText().toString())) {

            String id = myRef.push().getKey();

            User user = new User(name.getText().toString(), nameInput.getText().toString().trim(), id);
            myRef.child(id).setValue(user);

            Toast.makeText(this, "Job Role updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Type job role", Toast.LENGTH_LONG).show();
            ;
        }

        tittle.setText(nameInput.getText().toString());

        nameInput.setText("");
    }

    /**
     * Method that links the XMl elements from the Layour to the variables
     * instantiated in this Java class
     */
    private void LinkElements() {
        addItem = (ImageView) findViewById(R.id.addItem);
        saveBtn = (ImageView) findViewById(R.id.saveBtn);
        cancelBtn = (ImageView) findViewById(R.id.deleteBtn);
        nameInput = (EditText) findViewById(R.id.nameInput);
        name = (TextView) findViewById(R.id.ProfileName);
        tittle = (TextView) findViewById(R.id.ProfileTitle);
        tittle = (TextView) findViewById(R.id.ProfileTitle);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        qrButton = (ImageView) findViewById(R.id.QrButton);
        submenu = (LinearLayout) findViewById(R.id.subMenu);
        scrollView = (ScrollView) findViewById(R.id.scrollMenu);
        home = (ImageView)findViewById(R.id.Home);
        wallet = (ImageView)findViewById(R.id.Wallet);
        store = (ImageView)findViewById(R.id.Store);
        map = (ImageView)findViewById(R.id.Map);
    }

    /**
     * Method that saves the
     */
    private void saveUserInformation() {
        name.setText(nameInput.getText().toString());

        if (name.getText().toString().isEmpty()) {
            name.setError("Name required");
            name.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (isBname()) {
                updateName(user);
            }
            if (isBtitle()) {
                updateTitle(user);
            }
        }
    }

    public boolean isBname() {
        return Bname;
    }

    public void setBname(boolean bname) {
        this.Bname = bname;
    }

    public boolean isBtitle() {
        return Btitle;
    }

    public void setBtitle(boolean btitle) {
        this.Btitle = btitle;
    }

    private void updateTitle(FirebaseUser user) {
        tittle.setText(nameInput.getText().toString());
    }

    /**
     * Method to save the the updated picture
     *
     * @param user is selecting the image
     */
    private void updatePicture(FirebaseUser user) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(profileImageUrl))
                .build();

        user.updateProfile(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Picture Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * method to save the updated Bname
     *
     * @param user is selecting the new Bname
     */
    private void updateName(FirebaseUser user) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        user.updateProfile(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Name Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Method that provides an action when an int is requested and
     * provides an operational result in form of intent (new Screen)
     *
     * @param requestCode int requested as the beginning of the operation
     * @param resultCode  int as a result of the operation
     * @param data        Intent (Screen) which is directed to
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profilePic.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
                updatePicture(mAuth.getCurrentUser());

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
                name.setText(user.getDisplayName());
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
//        DatabaseReference ref = myRef.child("L4h33L1SjOlNQh7ob6R");
//        Query phoneQuery = ref.orderByChild("user").equalTo("L4h33L1SjOlNQh7ob6R");
//
//        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                    User user = singleSnapshot.getValue(User.class);
//                    user.getTitle();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }
//

    /**
     * Mehtod that disploys a native Image Chooser
     * to choose the desired Profile Image
     */

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

}
