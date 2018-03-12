package android.ebs.zunderapp.Wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.ebs.zunderapp.R;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;

import javax.crypto.SecretKey;

public class Info extends Fragment {
    private View mView;
    private TextView balanceTextview, walletAddress, settings, walletSend, walletHistory;
    private ImageView qrCode, arrow, gear, home, wallet, store, map;
    private String privateKey, publicKey, balance, balanceInfo;

    private CreateQR createQR;
    private MyWallet myWallet;
    Server server;

    public Info() {
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
        mView = inflater.inflate(R.layout.fragment_info, container, false);
        return mView;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myWallet = new MyWallet();

        myWallet = new MyWallet();

        //Provide connection
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // TODO Generate new encryption

        //links elements from the XML layout to Java objects
        linkElements();

        // Get wallet
        lookUpWallet();

        //Displays QR code of the Wallet address
        showQR();

        //set up action listeners from the Java objects
        actionListeners();

        //receiveTransaction();
        SecretKey secret = null;

    }


    private void lookUpWallet() {
        setPrivateKey(myWallet.searchWallet());
        try {
            //create required wallet elements from Private Key
            KeyPair pair = KeyPair.fromSecretSeed(getPrivateKey());
            setPublicKey(pair.getAccountId());
            //Connect to the Stellar Network
            try {
                connectStellarTestNet(pair);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method that generates an Alert Dialog
     * containing the QR code from the user wallet address
     */
    private void createAlert(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext())
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

//    private void receiveTransaction() {
//        KeyPair account = KeyPair.fromSecretSeed(getPrivateKey());
//
//        // Create an API call to query payments involving the account.
//        PaymentsRequestBuilder paymentsRequest = server.payments().forAccount(account);
//
//        // If some payments have already been handled, start the results from the
//        // last seen payment. (See below in `handlePayment` where it gets saved.)
//        String lastToken = loadLastPagingToken();
//        if (lastToken != null) {
//            paymentsRequest.cursor(lastToken);
//        }
//
//        // `stream` will send each recorded payment, one by one, then keep the
//        // connection open and continue to send you new payments as they occur.
//        paymentsRequest.stream(new EventListener<OperationResponse>() {
//            @Override
//            public void onEvent(OperationResponse payment) {
//                // Record the paging token so we can start from here next time.
//                savePagingToken(payment.getPagingToken());
//
//                // The payments stream includes both sent and received payments. We only
//                // want to process received payments here.
//                if (payment instanceof PaymentOperationResponse) {
//                    if (((PaymentOperationResponse) payment).getTo().equals(account)) {
//                        return;
//                    }
//
//                    String amount = ((PaymentOperationResponse) payment).getAmount();
//
//                    Asset asset = ((PaymentOperationResponse) payment).getAsset();
//                    String assetName;
//                    if (asset.equals(new AssetTypeNative())) {
//                        assetName = "lumens";
//                    } else {
//                        StringBuilder assetNameBuilder = new StringBuilder();
//                        assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getCode());
//                        assetNameBuilder.append(":");
//                        assetNameBuilder.append(((AssetTypeCreditAlphaNum) asset).getIssuer().getAccountId());
//                        assetName = assetNameBuilder.toString();
//                    }
//
//                    StringBuilder output = new StringBuilder();
//                    output.append(amount);
//                    output.append(" ");
//                    output.append(assetName);
//                    output.append(" from ");
//                    output.append(((PaymentOperationResponse) payment).getFrom().getAccountId());
//                    System.out.println(output.toString());
//                }
//
//            }
//        });
//    }

    /**
     * Method that connects to the Stellar Test Network server.
     *
     * @param pair is the user wallet Account manager
     */
    private void connectStellarTestNet(KeyPair pair) {
        server = new Server("https://horizon-testnet.stellar.org");
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
     *
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * method that sets a new private key
     *
     * @param privateKey is set
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * method the gets the public key
     *
     * @return
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * method that gets the balance information
     *
     * @return the balance information
     */
    public String getBalanceInfo() {
        return balanceInfo;
    }

    /**
     * method that sets the balance information
     *
     * @param balanceInfo is set
     */
    public void setBalanceInfo(String balanceInfo) {
        this.balanceInfo = balanceInfo;
    }

    /**
     * method that sets a new public key
     *
     * @param publicKey is set
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * method that gets the Balance amount
     *
     * @return the balance amount
     */
    public String getBalance() {
        return balance;
    }

    /**
     * method that sets a new Balance amount
     *
     * @param balance amount is set
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * Method that contains action listeners for buttons
     */
    private void actionListeners() {
//        cipher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Crytpography crypto = new Crytpography();
//                String Shit = "this shit is real";
//                try {
//                    byte[] text = crypto.encrypt(Shit);
//
//                Toast.makeText(getApplicationContext(), String.valueOf(text), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WalletSettings.class);
                goToWalletSettings();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WalletSettings.class);
                goToWalletSettings();

            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WalletSettings.class);
                goToWalletSettings();

            }
        });
    }

    /**
     * Method that starts the Wallet Settings activity
     */
    private void goToWalletSettings() {
        Intent intent = new Intent(getContext(), WalletSettings.class);
        startActivity(intent);
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
        walletSend = (TextView) getView().findViewById(R.id.gotosend);
        walletHistory = (TextView) getView().findViewById(R.id.walletHistory);
        balanceTextview = (TextView) getView().findViewById(R.id.balance);
        walletAddress = (TextView) getView().findViewById(R.id.walletAddress);
        settings = (TextView) getView().findViewById(R.id.walletSet);
        gear = (ImageView) getView().findViewById(R.id.gearWallet);
        arrow = (ImageView) getView().findViewById(R.id.arrowToWallet);
        qrCode = (ImageView) getView().findViewById(R.id.qrWallet);
        home = (ImageView) getView().findViewById(R.id.Home);
        wallet = (ImageView) getView().findViewById(R.id.Wallet);
        store = (ImageView) getView().findViewById(R.id.Store);
        map = (ImageView) getView().findViewById(R.id.Map);

    }


}