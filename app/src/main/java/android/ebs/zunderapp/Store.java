package android.ebs.zunderapp;

import android.content.Intent;
import android.ebs.zunderapp.Wallet.Wallet;
import android.ebs.zunderapp.Wallet.WalletInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.io.File;

public class Store extends AppCompatActivity {
    private Animation fade_in, fade_out;
    private ViewFlipper viewFlipper;
    private ImageView home, map, wallet;
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private  File PK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //link elements from the XML layout to Java objects
        linkElements();

        //Display slide show of images that link to products + services
        slideshow();

        //set up action listeners from the Java objects
        actionListeners();

    }

    /**
     * Method that sets up action listeners from the Java objects
     */
    private void actionListeners() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Store.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);

            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goToWallet();
            }
        });
    }

    /**
     * Method that links elements from the XML layout to Java objects
     */
    private void linkElements() {
        home = (ImageView)findViewById(R.id.Home);
        wallet = (ImageView)findViewById(R.id.Wallet);
        map = (ImageView)findViewById(R.id.Map);
    }

    /**
     * Method that checks if there is an
     * existing wallet in the local file
     * and if there is no it creates a new Wallet
     */
    private void goToWallet() {
        try {
            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean valid = false;
        PK = new File(path);
        if (PK.exists()) {
            wanted = PK.listFiles();
            if (wanted.length == 1) {
                valid = true;
            } else {
                valid = false;
            }
            if (valid) {
                Intent intent = new Intent(Store.this, WalletInfo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }else {
                Intent intent = new Intent(Store.this, Wallet.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        }

    }

    /**
     * Method that automatically plays a group of Images
     */
    private void slideshow() {
        viewFlipper = (ViewFlipper) this.findViewById(R.id.bckgrndViewFlipper1);
        fade_in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        viewFlipper.setInAnimation(fade_in);
        viewFlipper.setOutAnimation(fade_out);
        //sets auto flipping
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(2500);
        viewFlipper.startFlipping();
    }
}