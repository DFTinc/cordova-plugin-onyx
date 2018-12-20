package com.dft.cordova.plugin.onyx;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.core;
import org.opencv.core.Mat;

public class OnyxMatch extends AsyncTask<Object, Void, Float> {
    private Exception mException = null;
    private MatchResultCallback mMatchResultCallback = null;

    public OnyxMatch(Context context,  MatchResultCallback matchResult) {
        mMatchResultCallback = matchResult;
    }

    @Override
    protected Float doInBackground(Object... params) {
        try {
            FingerprintTemplate referenceTemplate = (FingerprintTemplate) params[0];
            Mat probeMat = (Mat) params[1];
            double[] pyramidScales = (double[]) params[2];
            if (null == pyramidScales) {
                pyramidScales = new double[]{0.8, 1.0, 1.2};
            }
            return core.pyramidVerify(referenceTemplate, probeMat, pyramidScales);
        } catch (Exception e) {
            mException = e;
            Log.e("OnyxMatch", "Exception verifying templates.", e);
        }
        return -1.0f;
    }

    @Override
    protected void onPostExecute(Float matchScore) {
        if (mException != null) {
            mMatchResultCallback.onMatchFinished(false, -1);
        } else {
            sendMatchScore(matchScore);
        }
    }

    private void sendMatchScore(float score) {
        float threshold = 34.0f;
        if (score < threshold) {
            if (mMatchResultCallback != null) {
                mMatchResultCallback.onMatchFinished(false, score);
            }
        } else {
            if (mMatchResultCallback != null) {
                mMatchResultCallback.onMatchFinished(true, score);
            }
        }
    }

    public interface MatchResultCallback {
        public void onMatchFinished(boolean match, float score);
    }
}
