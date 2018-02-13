package android.ebs.zunderapp.Wallet;

import android.content.Intent;
import android.ebs.zunderapp.MainActivity;
import android.ebs.zunderapp.R;
import android.ebs.zunderapp.Store;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.TransactionBuilderAccount;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.File;
import java.io.IOException;

public class WalletSend extends AppCompatActivity {
    private TextInputEditText addressInput, amountInput, attachmentInput;
    private Button send, cancel;
    private ProgressBar bar;
    private TextView walletInfo, walletHistory;
    private ImageView home, map, store, wallet;
    private String privateKey, publicKey, balance, balanceInfo, destination;
    private MyWallet myWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_send);

        //links elements from the XML layout to Java objects
        linkElements();

        //Search for a Wallet locally
        myWallet = new MyWallet();
        setPrivateKey(myWallet.searchWallet());

        //create required wallet elements from Private Key
        KeyPair pair = KeyPair.fromSecretSeed(getPrivateKey());
        setPublicKey(pair.getAccountId());

        //set up action listeners from the Java objects
        actionListeners();


    }

    /**
     * Method that sets up action listeners from the Java objects
     */
    private void actionListeners() {

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendTransaction().execute("");
            }
        });

        walletInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletSend.this, WalletInfo.class);

                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletSend.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);

            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletSend.this, Store.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletSend.this, WalletInfo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);

            }
        });
        //set the a new color
        bar.getIndeterminateDrawable()
                .setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

    }

    /**
     * method that sets a new destination address
     * @param destination wallet address to send balance
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Method that generates a new transaction to the
     * destination wallet address
     */
    private void sendTransaction() {
        Network.useTestNetwork();
        Server server = new Server("https://horizon-testnet.stellar.org");

        try {
            setDestination(getIntent().getStringExtra("destination"));
        }catch (Exception e){

        }
        if (destination == null){
            destination.equals(addressInput.getText().toString().trim());
        }
        KeyPair source = KeyPair.fromSecretSeed(getPrivateKey());
        KeyPair destination = KeyPair.fromAccountId(this.destination);

        // First, check to make sure that the destination account exists.
        // You could skip this, but if the account does not exist, you will be charged
        // the transaction fee when the transaction fails.
        // It will throw HttpResponseException if account does not exist or there was another error.
        try {
            server.accounts().account(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            // If there was no error, load up-to-date information on your account.
            TransactionBuilderAccount sousorceAccount = server.accounts().account(source);

            // Start building the transaction.
            Transaction transaction = new Transaction.Builder(sousorceAccount)
                    .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), amountInput.getText().toString()).build())
                    // A memo allows you to add your own metadata to a transaction. It's
                    // optional and does not affect how Stellar treats the transaction.
                    .addMemo(Memo.text(attachmentInput.getText().toString()))
                    .build();

            // Sign the transaction to prove you are actually the person sending it.
            transaction.sign(source);

            SubmitTransactionResponse response = server.submitTransaction(transaction);
            Toast.makeText(getApplicationContext(), "Transaction sent", Toast.LENGTH_SHORT).show();
            System.out.println(response);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method that links elements from the XML layout to Java objects
     */
    private void linkElements() {
        walletInfo = (TextView) findViewById(R.id.walletInfo);
        walletHistory = (TextView) findViewById(R.id.wallettHistory);
        bar = (ProgressBar) findViewById(R.id.datbar);
        addressInput = (TextInputEditText) findViewById(R.id.walletInput);
        amountInput = (TextInputEditText) findViewById(R.id.amountInput);
        attachmentInput = (TextInputEditText) findViewById(R.id.attachmentInput);
        send = (Button) findViewById(R.id.sendButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        walletHistory = (TextView) findViewById(R.id.walletHistory);
        walletInfo = (TextView) findViewById(R.id.walletInfo);
        home = (ImageView) findViewById(R.id.Home);
        wallet = (ImageView) findViewById(R.id.Wallet);
        store = (ImageView) findViewById(R.id.Store);
        map = (ImageView) findViewById(R.id.Map);
    }

    /**
     * Mehod that gets the private key
     * @return private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     *  Method that sets a new Private Key
     * @param privateKey is set up
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * method that sets a new Public key
     * @param publicKey is set up
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Inner class that executes a few actions when building a new transaction.
     * It displays a progressing bar before building the transaction.
     * generate a new transaction.
     * This inner class provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class SendTransaction extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            sendTransaction();

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            bar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
