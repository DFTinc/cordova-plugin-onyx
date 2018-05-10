package com.dft.cordova.plugin.onyx;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.core;

import org.opencv.core.Mat;

/**
 * Created by mjwheatley on 3/5/18.
 */

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
            Mat probe = new Mat();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(mException.getMessage())
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setTitle("Verification error");

            AlertDialog dialog = builder.create();
            dialog.show();
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
        Toast.makeText(mContext, "Match Score: " + score, Toast.LENGTH_SHORT).show();
    }

    public interface MatchResultCallback {
        public void onMatchFinished(boolean match, float score);
    }
}
