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

    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private File PK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_settings);

        back = (ImageView) findViewById(R.id.arrowtowal);
        understandRisks = (RelativeLayout) findViewById(R.id.understanRisks);
        okayButton = (ImageView) findViewById(R.id.yesButton);
        showKey = (Switch) findViewById(R.id.showKey);
        input = (EditText) findViewById(R.id.inputText);

        showKey.setChecked(false);
        showKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showKey.isChecked()){
                    input.setText("");
                    understandRisks.setVisibility(View.VISIBLE);

                }else {
                    understandRisks.setVisibility(View.GONE);
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

    public String getPrivateKey() {
        return privateKey;
    }

    private void getPK() {
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
     * Inner class that provides 3 key elements
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
            getPK();
            createAlert(getPrivateKey());

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
