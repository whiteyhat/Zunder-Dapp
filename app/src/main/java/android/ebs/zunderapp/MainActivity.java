package android.ebs.zunderapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import net.i2p.crypto.eddsa.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton inbox, locations, item1, item2, item3, carSharing;
    private ImageView home, wallet, store, map, profile, qrImage;
    private WebView webview;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate XML elements to Java class
        instantiateElements();

        //Link the buttons to the method OnClick()
        addListening();

       // webview.getSettings().setJavaScriptEnabled(true);
        // webview.loadUrl("https://twitter.com/AberBlockchain");
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconifiedByDefault(false);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
                overridePendingTransition( R.anim.slide_right, R.anim.slide_left);
            }
        });

        qrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanQR.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right, R.anim.push_left);
            }
        });


    }


    /**
     * Method that creates a link between the front end design
     * of the XML layout and its element with the logic Java class
     */
    private void instantiateElements(){
        qrImage = (ImageView) findViewById(R.id.qrimg);
        profile = (ImageView) findViewById(R.id.profile);
        searchView = (SearchView) findViewById(R.id.searchview);
        inbox = (ImageButton) findViewById(R.id.MessageButton);
        webview = (WebView) findViewById(R.id.webview);
        locations = (ImageButton) findViewById(R.id.LocationButton);
        item1 = (ImageButton) findViewById(R.id.EcoLockButton);
        item2 = (ImageButton) findViewById(R.id.SmartContractButton);
        item3 = (ImageButton) findViewById(R.id.GuardAInButton);
        carSharing = (ImageButton) findViewById(R.id.CarSharingButton);
        home = (ImageView) findViewById(R.id.Home);
        wallet = (ImageView) findViewById(R.id.Wallet);
        store = (ImageView) findViewById(R.id.Store);
        map = (ImageView) findViewById(R.id.Map);

    }

    /**
     * Method that creates an action listener to the
     * buttons when the user touch each button it is forwarded
     * to the onClick() function
     */
    private void addListening(){
        inbox.setOnClickListener(this);
        locations.setOnClickListener(this);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        carSharing.setOnClickListener(this);
        home.setOnClickListener(this);
        wallet.setOnClickListener(this);
        store.setOnClickListener(this);
        map.setOnClickListener(this);
    }

    /**
     * Method to create a new event/activity when the
     * user touch any button
     * @param view get the user touch
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MessageButton:
                break;
            case R.id.Store:
                Intent intent = new Intent(MainActivity.this, Store.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
                break;
            case R.id.Wallet:
                Intent intento = new Intent(MainActivity.this, Wallet.class);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
                startActivity(intento);
                break;
            case R.id.qrimg:
                Intent intenta = new Intent(MainActivity.this, Wallet.class);
                overridePendingTransition(R.anim.push_right, R.anim.push_left);
                startActivity(intenta);
                break;
        }
    }
}
