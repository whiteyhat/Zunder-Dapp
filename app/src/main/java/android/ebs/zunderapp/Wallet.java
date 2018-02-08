package android.ebs.zunderapp;

import android.content.Intent;
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
import android.widget.Toast;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class Wallet extends AppCompatActivity {
    private TextView output, importWallet;
    private ImageView generateWalllet, home, wallet, store, map, walletCreated;
    private ProgressBar bar;
    private KeyPair pair;
    private String privateKey, publicKey, balance, balanceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        linkElements();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, MainActivity.class);
                startActivity(intent);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, Store.class);
                startActivity(intent);
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
     * Method that connects to the Stellar Test Network server.
     *
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
            setBalance(balance.getBalance());
            setBalanceInfo("Type of asset: " + balance.getAssetType() +
                    "Asset code: " + balance.getAssetCode() +
                    "Balance Amount: " + balance.getBalance());
        }
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
            connectStellarTestNet(pair);

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            output.setText("Wallet created");
            bar.setVisibility(View.INVISIBLE);
            walletCreated.setVisibility(View.VISIBLE);
            output.setVisibility(View.VISIBLE);

            Intent intent = new Intent(Wallet.this, WalletInfo.class);
            intent.putExtra("walletAddress", getPublicKey());
            intent.putExtra("privateKey", getPrivateKey());
            intent.putExtra("balance", getBalance());
            intent.putExtra("balanceInfo", getBalanceInfo());
            startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
            importWallet.setVisibility(View.INVISIBLE);
            output.setText("Creating new Wallet...");
            Toast.makeText(getApplicationContext(), "We are giving you some money to test it", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
