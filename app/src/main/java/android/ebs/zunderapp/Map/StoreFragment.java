package android.ebs.zunderapp.Map;

import android.content.Intent;
import android.ebs.zunderapp.R;
import android.ebs.zunderapp.Wallet.Wallet;
import android.ebs.zunderapp.Wallet.WalletInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.io.File;


public class StoreFragment extends Fragment{
    private Animation fade_in, fade_out;
    private ViewFlipper viewFlipper;
    private ImageView home, map, wallet;
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/ZunderApp";
    private File[] wanted;
    private  File PK;
    public StoreFragment() {
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
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Display slide show of images that link to products + services
        slideshow();
    }

    /**
     * Method that checks if there is an
     * existing wallet in the local file
     * and if there is no it creates a new Wallet
     */
    private void goToWallet() {
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
                Intent intent = new Intent(getContext(), WalletInfo.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(getContext(), Wallet.class);
                startActivity(intent);
            }
        }

    }

    /**
     * Method that automatically plays a group of Images
     */
    private void slideshow() {
        viewFlipper = (ViewFlipper) getView().findViewById(R.id.bckgrndViewFlipper1);
        fade_in = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_out);
        viewFlipper.setInAnimation(fade_in);
        viewFlipper.setOutAnimation(fade_out);
        //sets auto flipping
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.startFlipping();
    }
}