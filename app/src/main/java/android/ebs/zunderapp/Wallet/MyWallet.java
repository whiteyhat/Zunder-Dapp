package android.ebs.zunderapp.Wallet;

import org.stellar.sdk.KeyPair;
import android.os.Environment;

import java.io.File;

/**
 * Created by Carlos on 13/02/2018.
 */

public class MyWallet{
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private  File PK;
    private String privateKey, publicKey, accountID;

    public MyWallet(){
        wanted = new File[0];
        searchWallet();
        try {
            KeyPair pair = KeyPair.fromSecretSeed(getPrivateKey());
            setPublicKey(pair.getAccountId());
            setAccountID(new String(pair.getPublicKey()));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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
