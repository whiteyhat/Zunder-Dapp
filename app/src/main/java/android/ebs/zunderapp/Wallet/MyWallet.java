package android.ebs.zunderapp.Wallet;

import android.app.Activity;
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

public class MyWallet{
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private  File PK;
    private String privateKey;

    public MyWallet(){
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

}
