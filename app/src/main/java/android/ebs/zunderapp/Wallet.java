package android.ebs.zunderapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Wallet extends AppCompatActivity {
    private TextView output, importWallet;
    private ImageView generateWalllet, home, wallet, store, map, walletCreated;
    private ProgressBar bar;
    private KeyPair pair;
    private String privateKey, publicKey, balance, balanceInfo;
    String[] permissions = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private File PK;
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);


        linkElements();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);

            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, Store.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });

    }

    /**
     * Method that links the XML elements from the Layour with
     * the Java programming objects.
     */
    private void linkElements() {
        generateWalllet = (ImageView) findViewById(R.id.generateWallet);
        bar = (ProgressBar) findViewById(R.id.bar);
        output = (TextView) findViewById(R.id.output);
        home = (ImageView)findViewById(R.id.Home);
        wallet = (ImageView)findViewById(R.id.Wallet);
        store = (ImageView)findViewById(R.id.Store);
        map = (ImageView)findViewById(R.id.Map);
        importWallet = (TextView)findViewById(R.id.bringWallet);
        walletCreated = (ImageView)findViewById(R.id.walletDone);
        bar.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        generateWalllet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectStellar().execute("");
            }
        });
    }


    /**
     * Method that creates a Stellar Account (Wallet) in the Testnet.
     *
     * @return information about the account to the terminal
     */
    @NonNull
    private KeyPair createWallet() {
        KeyPair pair = KeyPair.random();
        setPrivateKey(new String(pair.getSecretSeed()));
        setPublicKey(pair.getAccountId());
        InputStream response = null;

        try {
            //Asks the testnet to get a test account
            String friendbotUrl = String.format(
                    "https://horizon-testnet.stellar.org/friendbot?addr=%s",
                    pair.getAccountId());

            response = new URL(friendbotUrl).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        System.out.println("SUCCESS! You have a new account :)\n" + body);
        return pair;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getBalanceInfo() {
        return balanceInfo;
    }

    public void setBalanceInfo(String balanceInfo) {
        this.balanceInfo = balanceInfo;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * Inner class that provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class ConnectStellar extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            pair = createWallet();
            checkPermissions();
            savePK();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            output.setText("Wallet created");
            bar.setVisibility(View.INVISIBLE);
            walletCreated.setVisibility(View.VISIBLE);
            output.setVisibility(View.VISIBLE);


            Intent intent = new Intent(Wallet.this, WalletInfo.class);
            startActivity(intent);
            overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            finishActivity(0);
        }

        @Override
        protected void onPreExecute() {
            importWallet.setVisibility(View.INVISIBLE);
            output.setText("Creating new Wallet...");
            bar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "We are giving you some money to test it", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void savePK() {
        PK = new File(path + "/" + getPrivateKey());
        if (!PK.exists()) {
            PK.mkdirs();
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
}
