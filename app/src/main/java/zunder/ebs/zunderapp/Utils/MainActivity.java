package zunder.ebs.zunderapp.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import zunder.ebs.zunderapp.Map.MapActivity;
import zunder.ebs.zunderapp.Profile.Profile;
import zunder.ebs.zunderapp.Wallet.Wallet;
import zunder.ebs.zunderapp.Wallet.WalletInfo;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton inbox, locations, item1, item2, item3, carSharing;
    private ImageView home, wallet, store, map, profile, qrImage;
    private WebView webview;
    private SearchView searchView;
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private File PK;
    private String[] permissions = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(zunder.ebs.zunderapp.R.layout.activity_main);
        wanted = new File[0];
        PK = new File(path);

        //Instantiate XML elements to Java class
        linkElements();

        //Link the buttons to the method OnClick()
        addListening();

        //Method that opens a URL in the WebViewer
        openURL();

        //set up action listeners from the Java objects
        actionListeners();

        //Ask for reading permissions to look up the wallet
        checkReadPermissions();
    }


    /**
     * Method that sets up action listeners from the Java objects
     */
    private void actionListeners() {
        webview.setWebViewClient(new MyWebViewClient());
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
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.slide_right, zunder.ebs.zunderapp.R.anim.slide_left);
            }
        });

        qrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanQR.class);
                startActivity(intent);
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.push_right, zunder.ebs.zunderapp.R.anim.push_left);
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
     * Method that creates a link between the front end design
     * of the XML layout and its element with the logic Java class
     */
    private void linkElements() {
        qrImage = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.qrimg);
        profile = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.profile);
        searchView = (SearchView) findViewById(zunder.ebs.zunderapp.R.id.searchview);
        inbox = (ImageButton) findViewById(zunder.ebs.zunderapp.R.id.MessageButton);
        webview = (WebView) findViewById(zunder.ebs.zunderapp.R.id.webview);
        locations = (ImageButton) findViewById(zunder.ebs.zunderapp.R.id.LocationButton);
        item1 = (ImageButton) findViewById(zunder.ebs.zunderapp.R.id.EcoLockButton);
        item2 = (ImageButton) findViewById(zunder.ebs.zunderapp.R.id.SmartContractButton);
        item3 = (ImageButton) findViewById(zunder.ebs.zunderapp.R.id.GuardAInButton);
        carSharing = (ImageButton) findViewById(zunder.ebs.zunderapp.R.id.CarSharingButton);
        home = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.Home);
        wallet = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.Wallet);
        store = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.Store);
        map = (ImageView) findViewById(zunder.ebs.zunderapp.R.id.Map);

    }

    /**
     * Method that generates a new WebView client to load an URL
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    /**
     * method that opens an URL in the WEb View Client
     */
    private void openURL() {
        webview.loadUrl("https://twitter.com/AberBlockchain/status/929737528013451279");
    }

    /**
     * Method that creates an action listener to the
     * buttons when the user touch each button it is forwarded
     * to the onClick() function
     */
    private void addListening() {
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
     * method that generates an Alert Dialog
     * containing the QR code from the user wallet address
     */
    private void createAlert(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    /**
     * Method that check for WRITE/READ permissions
     *
     * @return if permission have been granted
     */
    private boolean checkReadPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    /**
     * Empty that avoids going backwards
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Method that checks if there is an
     * existing wallet in the local file
     * and if there is no it creates a new Wallet
     */
    private void goToWallet() {
        boolean valid = false;

        //If there is an existing wallet directory
        //access it if not, create one
        if (PK.exists()) {
            wanted = PK.listFiles();
            if (wanted.length == 1) {
                valid = true;
            } else {
                valid = false;
            }
            if (valid) {
                Intent intent = new Intent(MainActivity.this, WalletInfo.class);
                startActivity(intent);
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.quick_fade_in, zunder.ebs.zunderapp.R.anim.quick_fade_out);
            } else {
                Intent intent = new Intent(MainActivity.this, Wallet.class);
                startActivity(intent);
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.quick_fade_in, zunder.ebs.zunderapp.R.anim.quick_fade_out);
            }
        }
    }

    /**
     * Method to create a new event/activity when the
     * user touch any button
     *
     * @param view get the user touch
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case zunder.ebs.zunderapp.R.id.MessageButton:
                break;
            case zunder.ebs.zunderapp.R.id.qrimg:
                Intent intenta = new Intent(MainActivity.this, Wallet.class);
                startActivity(intenta);
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.push_right, zunder.ebs.zunderapp.R.anim.push_left);
                break;
            case zunder.ebs.zunderapp.R.id.Map:
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(zunder.ebs.zunderapp.R.anim.quick_fade_in, zunder.ebs.zunderapp.R.anim.quick_fade_out);
                break;
            case zunder.ebs.zunderapp.R.id.Store:
//                Intent intent = new Intent(MainActivity.this, Store.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);

                createAlert("Coming Soon", "We are working tirelessly to bring new and exciting features to Zunder Dapp");
                break;
        }
    }
}