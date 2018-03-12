package android.ebs.zunderapp.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.ebs.zunderapp.R;
import android.ebs.zunderapp.Wallet.CreateQR;
import android.ebs.zunderapp.Wallet.WalletInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.WriterException;

import java.io.IOException;

public class ScanQR extends AppCompatActivity {

    private SurfaceView cameraPreview;
    private TextView txtResult, walletAddr;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private final int RequestCameraPermissionID = 1001;
    private ImageView back;
    private String walletAddress;
    private Button newTx, newContact, scanAgain;
    private ImageView qrImage;
    private CreateQR createQR;

    /**
     * Method that asks for camera permissions before executing the
     * camera QR scanner.
     * @param requestCode number of the permission request
     * @param permissions open camera permission
     * @param grantResults result from the request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        //links elements from the XML layout to Java objects
        linkElements();

        //set up action listeners from the Java objects
        actionListeners();

        //generate resources to display the camera in the Surface View
        generateResources();

        //enable the camera preview with Autofocus and more settings
        enableCamera();

        //c
        qrDetection();
    }

    /**
     * method that set up action listener from the Java objects
     */
    private void actionListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanQR.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
        });

        scanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContact.setVisibility(View.INVISIBLE);
                newTx.setVisibility(View.INVISIBLE);
                qrImage.setVisibility(View.INVISIBLE);
                walletAddr.setVisibility(View.INVISIBLE);
                scanAgain.setVisibility(View.INVISIBLE);
                cameraPreview.setVisibility(View.VISIBLE);

                enableCamera();
            }
        });

        newTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewTx();
            }
        });
    }

    /**
     * Method that links elements from the XML layout to Java objects
     */
    private void linkElements() {
        back = (ImageView) findViewById(R.id.arrowtomain);
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);
        walletAddr = (TextView) findViewById(R.id.walletaddr);
        newTx = (Button) findViewById(R.id.newTx);
        scanAgain = (Button) findViewById(R.id.scanAgain);
        newContact = (Button) findViewById(R.id.newContact);
        qrImage = (ImageView) findViewById(R.id.qrimg);
    }

    /**
     * method generate resources to display the camera in the Surface View
     */
    private void generateResources() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(800, 1200)
                .setAutoFocusEnabled(true)
                .build();
    }

    /**
     * enable the camera preview with Autofocus and more settings
     */
    private void enableCamera() {
        //Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(ScanQR.this,
                            new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                    //set camera to continually auto-focu
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });
    }

    /**
     * method that contains QR detection algorithm
     */
    private void qrDetection() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0) {
                    //Create vibrate
                    setWalletAddress(qrcodes.valueAt(0).displayValue);
                    new afterQR().execute("");
                }


            }
        });
    }

    /**
     * Method that open the wallet to generate a new
     * transaction to the scanned wallet address
     */
    private void generateNewTx() {
        Intent intent = new Intent(ScanQR.this, WalletInfo.class);
        intent.putExtra("destination", getWalletAddress());
        startActivity(intent);
        overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
    }

    /**
     * method that gets the wallet adress
     * @return the wallet address
     */
    public String getWalletAddress() {
        return walletAddress;
    }

    /**
     * method that sets a new wallet address
     * @param walletAddress is set up
     */
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    /**
     * Inner class that executes a few actions once a new QR code is scanned.
     * When a QR code is scanned the user has the 2 options: Add to bookmark or
     * generate a new transaction.
     * This inner class provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class afterQR extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
           createQR = new CreateQR(600,600);
            try {
                Bitmap bitmap = createQR.encodeAsBitmap(getWalletAddress());
                qrImage.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
           walletAddr.setText(getWalletAddress());

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            cameraPreview.setVisibility(View.GONE);
            cameraSource.stop();
            newContact.setVisibility(View.VISIBLE);
            newTx.setVisibility(View.VISIBLE);
            qrImage.setVisibility(View.VISIBLE);
            walletAddr.setVisibility(View.VISIBLE);
            scanAgain.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}