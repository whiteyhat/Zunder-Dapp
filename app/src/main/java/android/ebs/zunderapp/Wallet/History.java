package android.ebs.zunderapp.Wallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.ebs.zunderapp.R;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class History extends Fragment {
    private View mView;
    private WebView webView;
    private MyWallet myWallet;
    private TextView walletInfo, walletSend;

    public History() {
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
        mView = inflater.inflate(R.layout.fragment_history, container, false);
        return mView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        final String address = myWallet.getPublicKey();
        webView.setWebViewClient(new MyWebViewClient());
        //webView.loadUrl("https://google.com");
        webView.loadUrl("http://testnet.stellarchain.io/address/" + address);
        //webView.loadUrl("https://stellar.expert/explorer/public/account/" + address);
        webView.requestFocus();
    }

    /**
     * Method that searched the wallet
     */
    private void lookUpWallet() {
        myWallet = new MyWallet();
    }

    /**
     * Method that links the XML  layout elements with
     * the Java class objects
     */
    private void linkElements() {
        webView = (WebView) getView().findViewById(R.id.history);
        walletInfo = (TextView) getView().findViewById(R.id.gotowallet);
        walletSend = (TextView) getView().findViewById(R.id.gotosend);
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