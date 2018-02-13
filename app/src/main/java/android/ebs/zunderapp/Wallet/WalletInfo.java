package android.ebs.zunderapp.Wallet;

import android.content.Intent;
import android.ebs.zunderapp.AEShelper;
import android.ebs.zunderapp.MainActivity;
import android.ebs.zunderapp.R;
import android.ebs.zunderapp.Store;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;


public class WalletInfo extends AppCompatActivity {
    private TextView balanceTextview, walletAddress, settings, walletSend, walletHistory;
    private ImageView qrCode, arrow, gear, home, wallet, store, map;
    private String privateKey, publicKey, balance, balanceInfo;

    private CreateQR createQR;
    private AEShelper AESHelper;
    private MyWallet myWallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_info);

        myWallet = new MyWallet();
        //Provide safe connection
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Generate new cipher
        AESHelper = new AEShelper();

        //links elements from the XML layout to Java objects
        linkElements();

       // Get wallet
        setPrivateKey(myWallet.searchWallet());

        //create required wallet elements from Private Key
        KeyPair pair = KeyPair.fromSecretSeed(getPrivateKey());
        setPublicKey(pair.getAccountId());

        //Connect to the Stellar Network
        connectStellarTestNet(pair);

        //Displays QR code of the Wallet address
        showQR();

        //set up action listeners from the Java objects
        actionListeners();
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

        balanceTextview.setText(balance);
        walletAddress.setText(publicKey);
    }

    /**
     * Method to return the private ke
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * method that sets a new private key
     * @param privateKey is set
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * method the gets the public key
     * @return
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * method that gets the balance information
     * @return the balance information
     */
    public String getBalanceInfo() {
        return balanceInfo;
    }

    /**
     * method that sets the balance information
     * @param balanceInfo is set
     */
    public void setBalanceInfo(String balanceInfo) {
        this.balanceInfo = balanceInfo;
    }

    /**
     * method that sets a new public key
     * @param publicKey is set
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * method that gets the Balance amount
     * @return the balance amount
     */
    public String getBalance() {
        return balance;
    }

    /**
     * method that sets a new Balance amount
     * @param balance amount is set
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * Method that contains action listeners for buttons
     */
    private void actionListeners() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);

            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, Store.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });
        gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
                goToWalletSettings();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
                goToWalletSettings();

            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
                goToWalletSettings();

            }
        });
        walletSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSend.class);
                intent.putExtra("walletAddress", getPublicKey());
                intent.putExtra("privateKey", getPrivateKey());
                intent.putExtra("balance", getBalance());
                intent.putExtra("balanceInfo", getBalanceInfo());
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });
    }

    /**
     * Method that starts the Wallet Settings activity
     */
    private void goToWalletSettings() {
        Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
        intent.putExtra("walletAddress", getPublicKey());
        intent.putExtra("privateKey", getPrivateKey());
        intent.putExtra("balance", getBalance());
        intent.putExtra("balanceInfo", getBalanceInfo());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }

    /**
     * method that displays the QR code into the ImageView
     */
    private void showQR() {
       createQR = new CreateQR(600, 600);

        try {
            Bitmap bitmap = createQR.encodeAsBitmap(publicKey);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that links elements from XML to
     * Java objects
     */
    private void linkElements() {
        walletSend = (TextView) findViewById(R.id.walletSend);
        walletHistory = (TextView) findViewById(R.id.wallettHistory);
        balanceTextview = (TextView) findViewById(R.id.balance);
        walletAddress = (TextView) findViewById(R.id.walletAddress);
        settings = (TextView) findViewById(R.id.walletSet);
        gear = (ImageView) findViewById(R.id.gearWallet);
        arrow = (ImageView) findViewById(R.id.arrowToWallet);
        qrCode = (ImageView) findViewById(R.id.qrWallet);
        home = (ImageView) findViewById(R.id.Home);
        wallet = (ImageView) findViewById(R.id.Wallet);
        store = (ImageView) findViewById(R.id.Store);
        map = (ImageView) findViewById(R.id.Map);

    }

    /**
     * Method that encrypts a String using a key
     * @param strNormalText string to cipher
     * @return the encrypted text
     */
    public String encryption(String strNormalText){
        String seedValue = "!QAZ££ERFD%T";
        String normalTextEnc="";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }

    /**
     * Method that decrypts a text using a key
     * @param strEncryptedText string to decrypt
     * @return the unencrypted String
     */
    public String decryption(String strEncryptedText){
        String seedValue = "!QAZ££ERFD%T";
        String strDecryptedText="";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }
}
