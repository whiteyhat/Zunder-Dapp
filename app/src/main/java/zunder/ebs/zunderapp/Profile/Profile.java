package zunder.ebs.zunderapp.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import zunder.ebs.zunderapp.Utils.Login;
import zunder.ebs.zunderapp.Utils.MainActivity;
import zunder.ebs.zunderapp.Wallet.CreateQR;
import zunder.ebs.zunderapp.Wallet.MyWallet;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;

import org.stellar.sdk.KeyPair;

import java.io.IOException;

public class Profile extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    private TextView name, tittle;
    private FirebaseAuth mAuth;
    private ImageView profilePic, addItem;
    private EditText nameInput;
    private Uri uriProfileImage;
    private String profileImageUrl, publicKey, privateKey;
    private ImageView saveBtn, cancelBtn, qrButton, back;
    private ScrollView scrollView;
    private LinearLayout submenu;
    private boolean Bname, Btitle, Bimage;

    private MyWallet myWallet;
    private CreateQR createQR;
    private DatabaseReference myRef;


    /**
     * Method that creates the screen once it is running.
     * the Main method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(zunder.ebs.zunderapp.R.layout.activity_profile);

        // Link the XMl elements with the code
        LinkElements();

        //Get authentication from DB (Firebase)
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("user");

        //Load user Information
        loadUserInformation();

        //Action listeners for elements
        ActionListeners();
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
                cancelBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
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
                if (Bimage){
                    updatePicture(mAuth.getCurrentUser());
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

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert();
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.push_right, zunder.ebs.zunderapp.R.anim.push_left);
            }
        });
    }

    /**
     * method that ads a job role to the Firebase DB
     */
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
     * method that gets the private key
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * method that sets a new private key
     * @param privateKey is set up
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Method that links the XMl elements from the Layour to the variables
     * instantiated in this Java class
     */
    private void LinkElements() {
        back = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.arrow);
        addItem = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.addItem);
        saveBtn = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.saveBtn);
        cancelBtn = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.deleteBtn);
        nameInput = (EditText) findViewById(zunder.ebs.zunderapp.R.id.nameInput);
        name = (TextView) findViewById(zunder.ebs.zunderapp.R.id.ProfileName);
        tittle = (TextView) findViewById(zunder.ebs.zunderapp.R.id.ProfileTitle);
        tittle = (TextView) findViewById(zunder.ebs.zunderapp.R.id.ProfileTitle);
        profilePic = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.profilePic);
        qrButton = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.QrButton);
        submenu = (LinearLayout) findViewById(zunder.ebs.zunderapp.R.id.subMenu);
        scrollView = (ScrollView) findViewById(zunder.ebs.zunderapp.R.id.scrollMenu);
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

    /**
     * method that gets the public key
     * @return the public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * method tha sets a new public key
     * @param publicKey is set up
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * method that generates an Alert Dialog
     * containing the QR code from the user wallet address
     */
        private void createAlert() {
            ImageView image = new ImageView(this);
            image.setImageResource(zunder.ebs.zunderapp.R.drawable.qr);

            myWallet = new MyWallet();
            setPrivateKey(myWallet.searchWallet());

            createQR = new CreateQR(700, 700);
            try {
                KeyPair pair = KeyPair.fromSecretSeed(getPrivateKey());
                setPublicKey(pair.getAccountId());
                try {
                    Bitmap bitmap = createQR.encodeAsBitmap(getPublicKey());
                    image.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.getMessage();
            }



        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }

    /**
     * method that gets a the boolean ready to save Name
     * @return the boolean
     */
    public boolean isBname() {
        return Bname;
    }

    /**
     * method that sets if is ready to save the name
     * @param bname the boolean
     */
    public void setBname(boolean bname) {
        this.Bname = bname;
    }

    /**
     * method that gets a the boolean ready to save job role
     * @return the boolean
     */
    public boolean isBtitle() {
        return Btitle;
    }

    /**
     * method that sets if is ready to save the job role
     * @param btitle is the boolean
     */
    public void setBtitle(boolean btitle) {
        this.Btitle = btitle;
    }

    /**
     * method that updates the job role in the UI
     * @param user FB user
     */
    private void updateTitle(FirebaseUser user) {
        tittle.setText(nameInput.getText().toString());
    }

    /**
     * Method to save the the updated picture
     *
     * @param user is selecting the image
     */
    private void updatePicture(FirebaseUser user) {

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
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
                Bimage = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that upload an image to the DB (Firebase)
     */
    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //This is not working because it is null
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    /**
     * Method that displays a native Image Chooser
     * to choose the desired Profile Image
     */
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

}
