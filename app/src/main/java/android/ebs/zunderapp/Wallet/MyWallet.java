package android.ebs.zunderapp.Wallet;

import android.content.Context;
import android.content.Intent;
import android.ebs.zunderapp.MainActivity;
import android.ebs.zunderapp.R;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

/**
 * Created by Carlos on 13/02/2018.
 */

public class MyWallet extends AppCompatActivity{
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private  File PK;
    private String privateKey;

    public void MyWalley(){
        wanted = new File[0];

    }

    /**
     * Method that gets a private key
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * method that sets up a new private key
     * @param privateKey is set up
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Method that searches the wallet internally
     */
    public String searchWallet() {
        String wallet = "Wallet not found";
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
               wallet = wanted[0].getName();
               setPrivateKey(wallet);
            }
        }
        return wallet;

    }

    /**
     * Method that saves the Private Key locally as a folder
     * under the directorry of "Zunder App"
     */
    public void savePK() {
        PK = new File(path + "/" + getPrivateKey());
        if (!PK.exists()) {
            PK.mkdirs();
        }
    }

    /**
     * Method that checks if there is an
     * existing wallet in the local file
     * and if there is no it creates a new Wallet
     */
    public void goToWallet(Context context) {
        boolean valid = false;
        PK = new File(path);
        //If there is an existing wallet directory
        //access it if not, create one
        if (PK.exists()) {
            wanted = PK.listFiles();
            if (wanted.length == 1) {
                valid = true;
            } else {
                valid = false;
            }

            if (valid) {
                Intent intent = new Intent(context, WalletInfo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            } else {
                Intent intent = new Intent(context, Wallet.class);
                startActivity(intent);
                overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
            }
        }

    }
}
