package android.ebs.zunderapp;

import android.content.Intent;
import android.ebs.zunderapp.Wallet.MyWallet;
import android.ebs.zunderapp.Wallet.WalletInfo;
import android.ebs.zunderapp.Wallet.WalletSend;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.security.KeyPair;

public class WalletHistory extends Activity {
    private WebView webView;
    private MyWallet myWallet;
    private TextView walletInfo, walletSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);

        // Get the wallet
        lookUpWallet();

        // Link the layout with the Java class
        linkElements();

        //Enable action listeners
        actionListeners();


    }

    /**
     * Method that enables the Action Listeners for
     * The events in the Activity
     */
    private void actionListeners() {
        webView.setWebViewClient(new MyWebViewClient());
        // webView.loadUrl("https://google.com");
        webView.loadUrl("http://testnet.stellarchain.io/address/" + myWallet.getPublicKey());
        webView.requestFocus();
        walletInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletHistory.this, WalletInfo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });

        walletSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletHistory.this, WalletSend.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        });
    }

    /**
     * Method that searched the wallet
     */
    private void lookUpWallet() {
        myWallet = new MyWallet();}

    /**
     * Method that links the XML  layout elements with
     * the Java class objects
     */
    private void linkElements() {
        webView = (WebView) findViewById(R.id.history);
        walletInfo = (TextView) findViewById(R.id.gotowallet);
        walletSend = (TextView) findViewById(R.id.gotosend);
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
}
