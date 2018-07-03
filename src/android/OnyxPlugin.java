package com.dft.cordova.plugin.onyx;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dft.onyx.FingerprintTemplate;

public class OnyxPlugin extends CordovaPlugin implements OnyxMatch.MatchResultCallback {

    public static final String TAG = "OnyxPlugin";

    public String mPackageName;

    public static CallbackContext mCallbackContext;
    public static PluginResult mPluginResult;

    private Activity mActivity;
    private Context mContext;
    public static JSONObject mArgs;
    private static String mExecuteAction;

    public static PluginAction mPluginAction;
    public static enum PluginAction {
        CAPTURE("capture"),
        MATCH("match");
        private final String key;

        PluginAction(String key){
            this.key = key;
        }

        public String getKey(){
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
        SHOULD_SEGMENT("shouldSegment"),
        SHOULD_CONVERT_TO_ISO_TEMPLATE("shouldConvertToISOTemplate"),
        IMAGE_ROTATION("imageRotation"),
        WHOLE_FINGER_CROP("wholeFingerCrop"),
        CROP_SIZE("cropSize"),
        CROP_SIZE_WIDTH("width"),
        CROP_SIZE_HEIGHT("height"),
        CROP_FACTOR("cropFactor"),
        SHOW_LOADING_SPINNER("showLoadingSpinner"),
        LAYOUT_PREFERENCE("layoutPreference"),
        LAYOUT_PREFERENCE_UPPER_THIRD("UPPER_THIRD"),
        LAYOUT_PREFERENCE_FULL("FULL"),
        USE_MANUAL_CAPTURE("useManualCapture"),
        USE_ONYX_LIVE("useOnyxLive"),
        USE_FLASH("useFlash"),
        RETICLE_ORIENTATION("reticleOrientation"),
        RETICLE_ORIENTATION_LEFT("LEFT"),
        RETICLE_ORIENTATION_RIGHT("RIGHT"),
        RETICLE_ANGLE("reticleAngle"),
        RETICLE_SCALE("reticleScale"),
        BACKGROUND_COLOR_HEX_STRING("backgroundColorHexString"),
        SHOW_BACK_BUTTON("showBackButton"),
        FLIP("flip"),
        FLIP_HORIZONTAL("HORIZONTAL"),
        FLIP_VERTICAL("VERTICAL"),
        FLIP_BOTH("BOTH"),
        FLIP_NONE("NONE");
        private final String key;

        OnyxConfig(String key){
            this.key = key;
        }

        public String getKey(){
            return this.key;
        }
    }

    /**
     * Constructor.
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
                mContext.startActivity(onyxIntent);
            }
        });
        keepCordovaCallback();
    }

    private void doMatch() throws JSONException {
        String encodedReference = mArgs.getString("reference");
        String encodedProbe = mArgs.getString("probe");
        byte[] referenceBytes = Base64.decode(encodedReference, 0);
        byte[] probeBytes = Base64.decode(encodedProbe, 0);
        FingerprintTemplate reference = new FingerprintTemplate(referenceBytes, 0);
        FingerprintTemplate probe = new FingerprintTemplate(probeBytes, 0);
        OnyxMatch matchTask = new OnyxMatch(mContext, OnyxPlugin.this);
        matchTask.execute(reference, probe);
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