package com.example.darkskyandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements DownloadManager {
    private WeatherSync mWeatherSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherSync = new WeatherSync(MainActivity.this, this);
        final String newYorkCity = mWeatherSync.getUrl(40.7, -70);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherSync.startForcedDownload(newYorkCity);
            }
        });
    }

    @Override
    public void onDownloadSuccess(String response) {
        // Make sure we run on the UI thread when doing UI operations.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataAnalysis dataAnalysis = new DataAnalysis(MainActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message = "High of " +
                            dataAnalysis.getTemperatureAnalysis().getTempHighMax() +
                            " and a low of " +
                            dataAnalysis.getTemperatureAnalysis().getTempLowMax();

                    ContextThemeWrapper wrapper =
                            new ContextThemeWrapper(MainActivity.this, R.style.AppTheme);
                    new AlertDialog.Builder(wrapper)
                            .setTitle(mWeatherSync.getCurrentSummary())
                            .setMessage(message)
                            .show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onDownloadFailed(String response) {
        new AlertDialog.Builder(this)
                .setTitle("Download Failed")
                .setMessage(response)
                .show();
    }

    @Override
    public void onNotDownloaded() {
        new AlertDialog.Builder(this)
                .setTitle("Data not downloaded")
                .show();
    }
}
