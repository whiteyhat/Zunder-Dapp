package android.ebs.zunderapp.Wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.ebs.zunderapp.MainActivity;
import android.ebs.zunderapp.R;
import android.ebs.zunderapp.Store;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.stellar.sdk.KeyPair;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Wallet extends AppCompatActivity {
    private TextView output, importWallet;
    private ImageView generateWalllet, home, wallet, store, map, walletCreated;
    private ProgressBar bar;
    private KeyPair pair;
    private String privateKey;
    private MyWallet myWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        myWallet = new MyWallet();

        //Instantiate elements from the XML layout
        linkElements();

        //ACtivate action listeners for the Java Objects
        actionListeners();

    }

    /**
     * Method that stores all the action listeners
     * form the Activity
     */
    private void actionListeners() {
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
                askToWallet();
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
         pair = KeyPair.random();
        setPrivateKey(new String(pair.getSecretSeed()));
        myWallet.setPrivateKey(new String(pair.getSecretSeed()));
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

    /**
     * method that sets a new private key
     * @param privateKey is set
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Method that delivers to the use rinformation
     * about the storage of the secret key + suggest
     * to export it
     */
    private void askToWallet() {
        final boolean[] bool = {false};
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this)
                        .setTitle("Export a secret key!")
                        .setMessage("Attention! For security" +
                                " reasons Zunder App does not store your secret keys to the purses on " +
                                "the server. You must take care of the safe storage of your secret key." +
                                " This is necessary to restore the purse in the future, in case of changing" +
                                " or losing the device or reinstalling the application, etc.")
                        .setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new ConnectStellar().execute("");
                            }
                        });
        builder.create().show();
    }


    /**
     * Inner class that connects to the stellar network,
     * ask permissions to write and read and saves the PK locally.
     * This inner class provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class ConnectStellar extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
           try {
               myWallet = new MyWallet();
               pair = createWallet();
               myWallet.savePK();
           }catch (Exception e){
               e.printStackTrace();
           }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
           try {

               output.setText("Wallet created");
               bar.setVisibility(View.INVISIBLE);
               walletCreated.setVisibility(View.VISIBLE);
               output.setVisibility(View.VISIBLE);

               Intent intent = new Intent(Wallet.this, WalletInfo.class);
               startActivity(intent);
               overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
               finishActivity(0);
           }catch (Exception e){
               e.printStackTrace();
           }
        }

        @Override
        protected void onPreExecute() {
            try {

                importWallet.setVisibility(View.INVISIBLE);
                output.setText("Creating new Wallet...");
                bar.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "We are giving you some money to test it", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
