package android.ebs.zunderapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.ebs.zunderapp.Wallet.WalletSend;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

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
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

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

        linkElements();

        actionListeners();

        generateResources();

        enableCamera();


        qrDetection();
    }

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

    private void generateNewTx() {
        Intent intent = new Intent(ScanQR.this, WalletSend.class);
        intent.putExtra("destination", getWalletAddress());
        startActivity(intent);
        overridePendingTransition(R.anim.quick_fade_in, R.anim.quick_fade_out);
    }

    /**
     * Method that generates a QR code from a String
     *
     * @param str is converted into a QR code (Public key)
     * @return a Bitmap object
     * @throws WriterException if the String is null
     */
    @Nullable
    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public String getWalletAddress() {
        return walletAddress;
    }


    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    /**
     * Inner class that provides 3 key elements
     * - Run a task in background
     * - Run a task beforehand
     * - Run a task afterwards
     */
    private class afterQR extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Bitmap bitmap = encodeAsBitmap(getWalletAddress());
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