package android.ebs.zunderapp.Wallet;

import android.content.Context;
import android.content.Intent;
import android.ebs.zunderapp.MainActivity;
import android.ebs.zunderapp.Store;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.ebs.zunderapp.R;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.IOException;

import javax.crypto.SecretKey;


public class Send extends Fragment {
    private View mView;
    private EditText addressInput, amountInput, attachmentInput;
    private Button send, cancel;
    private ProgressBar bar;
    private TextView walletInfo, walletHistory;
    private ImageView home, map, store, wallet;
    private String privateKey, publicKey, destination;
    private MyWallet myWallet;
    private boolean valid;

    public Send() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_send, container, false);
        return mView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Provide safe connection
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
        Server server = null;
        try {
            Network.useTestNetwork();
            server = new Server("https://horizon-testnet.stellar.org");
        }catch (Exception e){
            e.printStackTrace();
        }

        setDestination(addressInput.getText().toString());
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
            System.out.println(response);
            valid = true;
        } catch (IOException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method that links elements from the XML layout to Java objects
     */
    private void linkElements() {
        walletInfo = (TextView) getView().findViewById(R.id.gotowallet);
        walletHistory = (TextView) getView().findViewById(R.id.walletHistory);
        bar = (ProgressBar) getView().findViewById(R.id.datbar);
        addressInput = (EditText) getView().findViewById(R.id.walletInput);
        amountInput = (EditText) getView().findViewById(R.id.amountInput);
        attachmentInput = (EditText) getView().findViewById(R.id.attachmentInput);
        send = (Button) getView().findViewById(R.id.sendButton);
        cancel = (Button) getView().findViewById(R.id.cancelButton);
        walletHistory = (TextView) getView().findViewById(R.id.walletHistory);
        walletInfo = (TextView) getView().findViewById(R.id.gotowallet);
        home = (ImageView) getView().findViewById(R.id.Home);
        wallet = (ImageView) getView().findViewById(R.id.Wallet);
        store = (ImageView) getView().findViewById(R.id.Store);
        map = (ImageView) getView().findViewById(R.id.Map);
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
            try {
                sendTransaction();
            }catch (Exception e){
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (valid){
                Toast.makeText(getContext(), "Transaction sent", Toast.LENGTH_SHORT).show();
            }
            bar.setVisibility(View.INVISIBLE);
            addressInput.setText("");
            amountInput.setText("");
            attachmentInput.setText("");
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