package android.ebs.zunderapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class WalletInfo extends AppCompatActivity {
    private TextView balanceTextview, walletAddress, settings;
    private ImageView qrCode, arrow, gear, home, wallet, store, map;
    private String privateKey, publicKey, balance, balanceInfo;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 600;
    public final static int HEIGHT = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_info);

        linkElements();

        getElementsFromPreviouClass();

        showQR();

        actionListeners();
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

    /**
     * Method that contains action listeners for buttons
     */
    private void actionListeners() {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, MainActivity.class);
                startActivity(intent);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, Store.class);
                startActivity(intent);
            }
        });
        gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
                intent.putExtra("walletAddress", getPublicKey());
                intent.putExtra("privateKey", getPrivateKey());
                intent.putExtra("balance", getBalance());
                intent.putExtra("balanceInfo", getBalanceInfo());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletInfo.this, WalletSettings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
        });
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
     * Method that gets elements from the previous class
     */
    private void getElementsFromPreviouClass() {
        privateKey = getIntent().getStringExtra("privateKey");
        publicKey = getIntent().getStringExtra("walletAddress");
        balance = getIntent().getStringExtra("balance");
        balanceInfo = getIntent().getStringExtra("balanceInfo");
        balanceTextview.setText(balance);
        walletAddress.setText(publicKey);


    }

    /**
     * Method that links elements from XML to
     * Java objects
     */
    private void linkElements() {
        balanceTextview = (TextView)findViewById(R.id.balance);
        walletAddress = (TextView)findViewById(R.id.walletAddress);
        settings = (TextView)findViewById(R.id.walletSet);
        gear = (ImageView)findViewById(R.id.gearWallet);
        arrow = (ImageView)findViewById(R.id.arrowToWallet);
        qrCode = (ImageView)findViewById(R.id.qrWallet);
        home = (ImageView)findViewById(R.id.Home);
        wallet = (ImageView)findViewById(R.id.Wallet);
        store = (ImageView)findViewById(R.id.Store);
        map = (ImageView)findViewById(R.id.Map);

    }
}
