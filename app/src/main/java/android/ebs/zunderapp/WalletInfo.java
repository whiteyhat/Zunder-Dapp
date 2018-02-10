package android.ebs.zunderapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class WalletInfo extends AppCompatActivity {
    private TextView balanceTextview, walletAddress, settings, walletSend, walletHistory;
    private ImageView qrCode, arrow, gear, home, wallet, store, map;
    private String privateKey, publicKey, balance, balanceInfo;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 600;
    public final static int HEIGHT = 600;
    private SecretKey secret;
    private AEShelper AESHelper;

    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private  File PK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_info);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        wanted = new File[0];
        AESHelper = new AEShelper();

        linkElements();

       // getElementsFromPreviouClass();
        autologin();

        KeyPair pair = KeyPair.fromSecretSeed(getPrivateKey());
        setPublicKey(pair.getAccountId());
        connectStellarTestNet(pair);

        showQR();

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

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getBalance() {
        return balance;
    }

    public String getBalanceInfo() {
        return balanceInfo;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setBalanceInfo(String balanceInfo) {
        this.balanceInfo = balanceInfo;
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
        try {
            Bitmap bitmap = encodeAsBitmap(publicKey);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that generates a QR code from a String
     *
     * @param str is converted into a QR code (Public key)
     * @return a Bitmap object
     * @throws WriterException if the String is null
     */
    @Nullable
    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
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

    private void autologin() {
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
                String PK = wanted[0].getName();
                setPrivateKey(PK);
            }
        }

    }

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
