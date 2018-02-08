package android.ebs.zunderapp;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class Wallet extends AppCompatActivity {
    private TextView privateKey, publicKey, output;
    private ImageView generateWalllet;
    private ProgressBar bar;
    private KeyPair pair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        linkElements();

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
     * Method that links the XML elements from the Layour with
     * the Java programming objects.
     */
    private void linkElements() {
        privateKey = (TextView)findViewById(R.id.Private);
        publicKey = (TextView)findViewById(R.id.Public);
        generateWalllet = (ImageView) findViewById(R.id.generateWallet);
        bar = (ProgressBar) findViewById(R.id.bar);
        output = (TextView) findViewById(R.id.output);

    }

    /**
     * Method that connects to the Stellar Test Network server.
     * @param pair is the user wallet Account manager
     */
    private void connectStellarTestNet(KeyPair pair) {
        Server server = new Server("https://horizon-testnet.stellar.org");
        AccountResponse account = null;
        try {
            account = server.accounts().account(pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Balances for account " + pair.getAccountId());
        for (AccountResponse.Balance balance : account.getBalances()) {
            System.out.println(String.format(
                    "Type: %s, Code: %s, Balance: %s",
                    balance.getAssetType(),
                    balance.getAssetCode(),
                    balance.getBalance()));
        }
    }

    /**
     * Method that creates a Stellar Account (Wallet) in the Testnet.
     * @return information about the account to the terminal
     */
    @NonNull
    private KeyPair createWallet() {
        KeyPair pair = KeyPair.random();
        privateKey.setText(new String(pair.getSecretSeed()));
        InputStream response = null;

        publicKey.setText(pair.getAccountId());
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
     * Inner class that provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class ConnectStellar extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            pair = createWallet();
            connectStellarTestNet(pair);

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            output.setText("Wallet created");
            bar.setVisibility(View.INVISIBLE);
            output.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            output.setText("Creating new Wallet...");

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
