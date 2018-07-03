package com.dft.cordova.plugin.onyx;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.core;

import org.opencv.core.Mat;

public class OnyxMatch extends AsyncTask<FingerprintTemplate, Void, Float> {
    private Exception mException = null;
    private Context mContext = null;
    private MatchResultCallback mMatchResultCallback = null;

    public OnyxMatch(Context context,  MatchResultCallback matchResult) {
        mContext = context;
        mMatchResultCallback = matchResult;
    }

    @Override
    protected Float doInBackground(FingerprintTemplate... templates) {
        try {
            return core.verify(templates[0], templates[1]);
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
