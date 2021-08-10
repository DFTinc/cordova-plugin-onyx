package com.dft.cordova.plugin.onyx;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.dft.onyx.FingerprintTemplate;

public class OnyxPlugin extends CordovaPlugin implements OnyxMatch.MatchResultCallback {

    public static final String TAG = "OnyxPlugin";
    public static final String IMAGE_URI_PREFIX = "data:image/jpeg;base64,";
    public static String mPackageName;

    public static CallbackContext mCallbackContext;
    public static PluginResult mPluginResult;

    private Activity mActivity;
    private Context mContext;
    public static JSONObject mArgs;
    private static String mExecuteAction;

    public static PluginAction mPluginAction;

    public enum PluginAction {
        CAPTURE("capture"),
        MATCH("match");
        private final String key;

        PluginAction(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    public enum OnyxConfig {
        ONYX_LICENSE("onyxLicense"),
        RETURN_RAW_IMAGE("returnRawImage"),
        RETURN_PROCESSED_IMAGE("returnProcessedImage"),
        RETURN_ENHANCED_IMAGE("returnEnhancedImage"),
        RETURN_WSQ("returnWSQ"),
        RETURN_FINGERPRINT_TEMPLATE("returnFingerprintTemplate"),
        SHOULD_CONVERT_TO_ISO_TEMPLATE("shouldConvertToISOTemplate"),
        COMPUTE_NFIQ_METRICS("computeNfiqMetrics"),
        CROP_SIZE("cropSize"),
        CROP_SIZE_WIDTH("width"),
        CROP_SIZE_HEIGHT("height"),
        CROP_FACTOR("cropFactor"),
        SHOW_LOADING_SPINNER("showLoadingSpinner"),
        USE_MANUAL_CAPTURE("useManualCapture"),
        USE_ONYX_LIVE("useOnyxLive"),
        USE_FLASH("useFlash"),
        RETICLE_ORIENTATION("reticleOrientation"),
        RETICLE_ORIENTATION_LEFT("LEFT"),
        RETICLE_ORIENTATION_RIGHT("RIGHT"),
        RETICLE_ORIENTATION_THUMB_PORTRAIT("THUMB_PORTRAIT"),
        BACKGROUND_COLOR_HEX_STRING("backgroundColorHexString"),
        SHOW_BACK_BUTTON("showBackButton"),
        SHOW_MANUAL_CAPTURE_TEXT("showManualCaptureText"),
        MANUAL_CAPTURE_TEXT("manualCaptureText"),
        BACK_BUTTON_TEXT("backButtonText"),
        REFERENCE("reference"),
        PROBE("probe"),
        PYRAMID_SCALES("pyramidScales");
        private final String key;

        OnyxConfig(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    /**
     * Constructor
     */
    public OnyxPlugin() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init Onyx");
        mPackageName = cordova.getActivity().getApplicationContext().getPackageName();
        mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        mActivity = cordova.getActivity();
        mContext = cordova.getActivity().getApplicationContext();
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    public boolean execute(final String action,
                           JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;
        Log.v(TAG, "OnyxPlugin action: " + action);
        mExecuteAction = action;

        mArgs = args.getJSONObject(0);
        if (!mArgs.has("onyxLicense") || !mArgs.has("action")) {
            mPluginResult = new PluginResult(PluginResult.Status.ERROR);
            mCallbackContext.error("Missing required parameters");
            mCallbackContext.sendPluginResult(mPluginResult);
            return true;
        }

        if (action.equalsIgnoreCase(PluginAction.MATCH.getKey())) {
            mPluginAction = PluginAction.MATCH;
        } else if (action.equalsIgnoreCase(PluginAction.CAPTURE.getKey())) {
            mPluginAction = PluginAction.CAPTURE;
        }

        if (null != mPluginAction) {
            switch (mPluginAction) {
                case MATCH:
                    doMatch();
                    break;
                case CAPTURE:
                    launchOnyx();
                    break;
            }
        } else {
            onError("Invalid plugin action.");
        }
        return true;
    }

    public static void onFinished(int resultCode, JSONObject result) {
        if (resultCode == Activity.RESULT_OK) {
            mPluginResult = new PluginResult(PluginResult.Status.OK);
            try {
                result.put("action", mExecuteAction);
            } catch (JSONException e) {
                String errorMessage = "Failed to set JSON key value pair: " + e.getMessage();
                mCallbackContext.error(errorMessage);
                mPluginResult = new PluginResult(PluginResult.Status.ERROR);
            }
            mCallbackContext.success(result);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mPluginResult = new PluginResult(PluginResult.Status.ERROR);
            mCallbackContext.error("Cancelled");
        }

        mCallbackContext.sendPluginResult(mPluginResult);
    }

    private void keepCordovaCallback() {
        mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        mPluginResult.setKeepCallback(true);
        mCallbackContext.sendPluginResult(mPluginResult);
    }

    public static void onError(String errorMessage) {
        Log.e(TAG, errorMessage);
        mCallbackContext.error(errorMessage);
        mPluginResult = new PluginResult(PluginResult.Status.ERROR);
        mCallbackContext.sendPluginResult(mPluginResult);
    }

    private void launchOnyx() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Intent onyxIntent = new Intent(mContext, OnyxActivity.class);
                onyxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(onyxIntent);
            }
        });
        keepCordovaCallback();
    }

    private void doMatch() throws JSONException {
        // Get values for JSON keys
        String encodedReference = mArgs.getString(OnyxConfig.REFERENCE.getKey());
        String encodedProbe = mArgs.getString(OnyxConfig.PROBE.getKey());
        JSONArray scalesJSONArray = null;
        if (mArgs.has(OnyxConfig.PYRAMID_SCALES.getKey())) {
            scalesJSONArray = mArgs.getJSONArray(OnyxConfig.PYRAMID_SCALES.getKey());
        }

        // Decode reference fingerprint template data
        byte[] referenceBytes = Base64.decode(encodedReference, 0);

        // Get encoded probe processed fingerprint image data from image URI
        String encodedProbeDataString = encodedProbe.substring(IMAGE_URI_PREFIX.length(), encodedProbe.length());

        // Decode probe probe image data
        byte[] probeBytes = Base64.decode(encodedProbeDataString, 0);

        // Create a bitmap from the probe bytes
        Bitmap probeBitmap = BitmapFactory.decodeByteArray(probeBytes, 0, probeBytes.length);

        // Create a mat from the bitmap
        Mat matProbe = new Mat();
        Utils.bitmapToMat(probeBitmap, matProbe);
        Imgproc.cvtColor(matProbe, matProbe, Imgproc.COLOR_RGB2GRAY);

        // Create reference fingerprint template from bytes
        FingerprintTemplate ftRef = new FingerprintTemplate(referenceBytes, 0);

        // Convert pyramid scales from JSON array to double array
        double[] argsScales = null;
        if (null != scalesJSONArray && scalesJSONArray.length() > 0) {
            argsScales = new double[scalesJSONArray.length()];
            for (int i = 0; i < argsScales.length; i++) {
                argsScales[i] = Double.parseDouble(scalesJSONArray.optString(i));
            }
        }
        final double[] pyramidScales = argsScales;

        OnyxMatch matchTask = new OnyxMatch(mContext, OnyxPlugin.this);
        matchTask.execute(ftRef, matProbe, pyramidScales);
    }

    @Override
    public void onMatchFinished(boolean match, float score) {
        JSONObject result = new JSONObject();
        String errorMessage = null;
        try {
            result.put("isVerified", match);
            result.put("matchScore", score);
        } catch (JSONException e) {
            errorMessage = "Failed to set JSON key value pair: " + e.toString();
        }
        if (null != errorMessage) {
            Log.e(TAG, errorMessage);
            onError(errorMessage);
        } else {
            onFinished(Activity.RESULT_OK, result);
        }
    }
}
