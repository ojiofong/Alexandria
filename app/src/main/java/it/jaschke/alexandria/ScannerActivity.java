package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Utilizing Barcode scanner library project
 *
 * @link https://github.com/dm77/barcodescanner
 */
public class ScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private static String TAG = ScannerActivity.class.getSimpleName();
    ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        String isbn = result.getContents();
        String format = result.getBarcodeFormat().getName();

        Log.v(TAG, isbn); // Prints scan results
        Log.v(TAG, format); // Prints the scan format (qrcode, pdf417 etc.)


        if (TextUtils.isDigitsOnly(isbn)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(AddBook.SCAN_CONTENTS, isbn);
            intent.putExtra(AddBook.SCAN_FORMAT, format);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Barcode " + isbn, Toast.LENGTH_LONG).show();
            finish();
        }


    }
}
