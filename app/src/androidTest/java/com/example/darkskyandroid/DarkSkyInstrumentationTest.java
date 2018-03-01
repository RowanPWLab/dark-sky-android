package com.example.darkskyandroid;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DarkSkyInstrumentationTest {
    @Test
    public void testDarkSkyApiCall() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

        final WeatherSync[] sync = new WeatherSync[1];
        sync[0] = new WeatherSync(appContext, new DownloadManager() {
            @Override
            public void onDownloadSuccess(String response) {
                // Here we can test specific properties of our response.
                DataAnalysis dataAnalysis = new DataAnalysis(appContext, sync[0]);
                try {
                    dataAnalysis.generate();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onDownloadFailed(String response) {
                throw new RuntimeException(response);
            }

            @Override
            public void onNotDownloaded() {

            }
        });
        final String newYorkCity = sync[0].getUrl(40.7, -70);
        sync[0].startForcedDownload(newYorkCity);
    }
}
