package android.ebs.zunderapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WalletSettings extends AppCompatActivity {
    private ImageView back;
    private String privateKey, publicKey, balance, balanceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_settings);

        back = (ImageView) findViewById(R.id.arrowtowal);
        getElementsFromPreviouClass();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                   Intent intent = new Intent(WalletSettings.this, WalletInfo.class);
                   intent.putExtra("walletAddress", getPublicKey());
                   intent.putExtra("privateKey", getPrivateKey());
                   intent.putExtra("balance", getBalance());
                   intent.putExtra("balanceInfo", getBalanceInfo());
                   startActivity(intent);
                   overridePendingTransition(R.anim.push_right, R.anim.push_left);
               }catch (Exception e){
                   e.getMessage();
               }

            }
        });

    }

    public String getPrivateKey() {
        return privateKey;
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

    private void getElementsFromPreviouClass() {
        setPrivateKey(getIntent().getStringExtra("privateKey"));
        setPublicKey(getIntent().getStringExtra("walletAddress"));
        setBalance(getIntent().getStringExtra("balance"));
        setBalanceInfo(getIntent().getStringExtra("balanceInfo"));

    }
}
