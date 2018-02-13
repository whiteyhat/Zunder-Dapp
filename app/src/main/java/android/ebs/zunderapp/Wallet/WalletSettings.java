package android.ebs.zunderapp.Wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.ebs.zunderapp.R;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.io.File;

public class WalletSettings extends AppCompatActivity {
    private ImageView back;
    private String privateKey, publicKey, balance, balanceInfo;
    private RelativeLayout understandRisks;
    private ImageView okayButton;
    private EditText input;
    private Switch showKey;
    private MyWallet myWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_settings);

        myWallet = new MyWallet();
        //Link XML layout with Java objects
        linkElements();

        //Set up action listeners for Java objects
        actionListeners();

    }

    /**
     * Method that links XML layout elemtns with Java objects
     * from this class
     */
    private void linkElements() {
        back = (ImageView) findViewById(R.id.arrowtowal);
        understandRisks = (RelativeLayout) findViewById(R.id.understanRisks);
        okayButton = (ImageView) findViewById(R.id.yesButton);
        showKey = (Switch) findViewById(R.id.showKey);
        input = (EditText) findViewById(R.id.inputText);

        showKey.setChecked(false);
    }

    /**
     * Set up action listeners for Java objects from this class
     */
    private void actionListeners() {
        showKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showKey.isChecked()){ //if the switch is checked
                    input.setText(""); //clean the input
                    understandRisks.setVisibility(View.VISIBLE); //display the risks layout
                }else {
                    understandRisks.setVisibility(View.GONE); //otherwise dont display it
                }
            }
        });

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().isEmpty()) {
                    input.setError("Email is required");
                    input.requestFocus();
                    return;
                }

                if (input.getText().toString().equals("I understand")) {
                    new getPK().execute("");
                }else {
                        input.setError("You need to understand the consequences");
                        input.requestFocus();
                        return;
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletSettings.this, WalletInfo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right, R.anim.push_left);

            }
        });
    }

    /**
     * Method that creates an alert dialog showing the PK
     * @param privateKey is displayed for the user
     */
    private void createAlert(String privateKey) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setTitle("Private Key").
                        setMessage(privateKey).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    /**
     * Method that gets the Private Key
     * @return the Private Key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Method the sets a new private key
     * @param privateKey is set
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Inner class that displays the Private Key
     * once the User has understood the risk factors.
     * This class provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class getPK extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            understandRisks.setVisibility(View.GONE);
            showKey.setChecked(false);
        }

        @Override
        protected void onPreExecute() {
            setPrivateKey(myWallet.searchWallet());
            createAlert(getPrivateKey());

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
