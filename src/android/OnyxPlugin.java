package com.dft.cordova.plugin.onyx;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxConfigurationBuilder;
import com.dft.onyxcamera.config.OnyxResult;
import com.dft.onyxcamera.ui.reticles.Reticle;


public class OnyxPlugin extends CordovaPlugin {

    public static final String TAG = "OnyxPlugin";
    private static final int PERMISSIONS_REQUEST_STORAGE = 346437;

    public static String mPackageName;

    public static CallbackContext mCallbackContext;
    public static PluginResult mPluginResult;

    private Activity mActivity;
    private Context mContext;
    private JSONObject mArgs;
    public static Onyx mOnyx = null;
    private static String mExecuteAction;
    private static String mArgsString;
    private boolean mCaptureAfterSetup = false;

    public static PluginAction mPluginAction;
    public static enum PluginAction {
        SETUP("setup"),
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
        CROP_SIZE_WIDTH("cropSizeWidth"),
        CROP_SIZE_HEIGHT("cropSizeHeight"),
        CROP_FACTOR("cropFactor"),
        SHOW_SPINNER("showSpinner"),
        LAYOUT_PREFERENCE("layoutPreference"),
        LAYOUT_PREFERENCE_UPPER_THIRD("upperThird"),
        LAYOUT_PREFERENCE_FULL("full"),
        MANUAL_CAPTURE("manualCapture"),
        USE_ONYX_LIVE("useOnyxLive"),
        USE_FLASH("useFlash"),
        RETICLE_ORIENTATION("reticleOrientation"),
        RETICLE_ORIENTATION_LEFT("left"),
        RETICLE_ORIENTATION_RIGHT("right"),
        RETICLE_ANGLE("reticleAngle"),
        RETICLE_SCALE("reticleScale");
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

        if (action.equalsIgnoreCase(PluginAction.SETUP.getKey())) {
            mPluginAction = PluginAction.SETUP;
        } else if (action.equalsIgnoreCase(PluginAction.MATCH.getKey())) {
            mPluginAction = PluginAction.MATCH;
        } else if (action.equalsIgnoreCase(PluginAction.CAPTURE.getKey())) {
            mPluginAction = PluginAction.CAPTURE;
        }

        if (null != mPluginAction) {
            mArgsString = mArgs.toString();
//            if (cordova.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                launchOnyx();
//            } else {
//                cordova.requestPermission(this, PERMISSIONS_REQUEST_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//            return true;
        }

        if (mPluginAction == PluginAction.SETUP) {
            setupOnyx();
        } else if (mPluginAction == PluginAction.MATCH) {

        } else if (mPluginAction == PluginAction.CAPTURE) {
            if (null == mOnyx) {
                mCaptureAfterSetup = true;
                setupOnyx();
            } else {
                launchOnyx();
            }
        }
        return true;
    }

    private void setupOnyx() {
        try {
            // Create an OnyxConfigurationBuilder and configure it with desired options
            String onyxLicense = mArgs.getString(OnyxConfig.ONYX_LICENSE.getKey());

            OnyxConfigurationBuilder onyxConfigurationBuilder = new OnyxConfigurationBuilder()
                    .setActivity(mActivity)
                    .setLicenseKey(onyxLicense)
                    .setSuccessCallback(new OnyxConfiguration.SuccessCallback() {
                        @Override
                        public void onSuccess(OnyxResult onyxResult) {
                            //displayResults(onyxResult);
                        }
                    })
                    .setErrorCallback(new OnyxConfiguration.ErrorCallback() {
                        @Override
                        public void onError(Error error, String errorMessage, Exception exception) {
                            Log.e(TAG, errorMessage);
                            mCallbackContext.error(errorMessage);
                            mPluginResult = new PluginResult(PluginResult.Status.ERROR);
                        }
                    })
                    .setOnyxCallback(new OnyxConfiguration.OnyxCallback() {
                        @Override
                        public void onConfigured(Onyx configuredOnyx) {
                            mOnyx = configuredOnyx;
                            if (!mCaptureAfterSetup) {
                                mPluginResult = new PluginResult(PluginResult.Status.OK);
                            } else {
                                launchOnyx();
                            }
                        }
                    });

            if (mArgs.has(OnyxConfig.RETURN_RAW_IMAGE.getKey())) {
                onyxConfigurationBuilder.setReturnRawImage(mArgs.getBoolean(OnyxConfig.RETURN_RAW_IMAGE.getKey()));
            }
            if (mArgs.has(OnyxConfig.RETURN_PROCESSED_IMAGE.getKey())) {
                onyxConfigurationBuilder.setReturnProcessedImage(mArgs.getBoolean(OnyxConfig.RETURN_PROCESSED_IMAGE.getKey()));
            }
            if (mArgs.has(OnyxConfig.RETURN_ENHANCED_IMAGE.getKey())) {
                onyxConfigurationBuilder.setReturnEnhancedImage(mArgs.getBoolean(OnyxConfig.RETURN_ENHANCED_IMAGE.getKey()));
            }
            if (mArgs.has(OnyxConfig.RETURN_WSQ.getKey())) {
                onyxConfigurationBuilder.setReturnWSQ(mArgs.getBoolean(OnyxConfig.RETURN_WSQ.getKey()));
            }
            if (mArgs.has(OnyxConfig.RETURN_FINGERPRINT_TEMPLATE.getKey())) {
                onyxConfigurationBuilder.setReturnFingerprintTemplate(mArgs.getBoolean(OnyxConfig.RETURN_FINGERPRINT_TEMPLATE.getKey()));
            }
            if (mArgs.has(OnyxConfig.SHOULD_SEGMENT.getKey())) {
                onyxConfigurationBuilder.setShouldSegment(mArgs.getBoolean(OnyxConfig.SHOULD_SEGMENT.getKey()));
            }
            if (mArgs.has(OnyxConfig.SHOULD_CONVERT_TO_ISO_TEMPLATE.getKey())) {
                onyxConfigurationBuilder.setShouldConvertToISOTemplate(mArgs.getBoolean(OnyxConfig.SHOULD_CONVERT_TO_ISO_TEMPLATE.getKey()));
            }
            if (mArgs.has(OnyxConfig.IMAGE_ROTATION.getKey())) {
                onyxConfigurationBuilder.setImageRotation(mArgs.getInt(OnyxConfig.IMAGE_ROTATION.getKey()));
            }
            if (mArgs.has(OnyxConfig.WHOLE_FINGER_CROP.getKey())) {
                onyxConfigurationBuilder.setWholeFingerCrop(mArgs.getBoolean(OnyxConfig.WHOLE_FINGER_CROP.getKey()));
            }
            if (mArgs.has(OnyxConfig.CROP_SIZE_WIDTH.getKey()) && mArgs.has(OnyxConfig.CROP_SIZE_HEIGHT.getKey())) {
                int width = mArgs.getInt(OnyxConfig.CROP_SIZE_WIDTH.getKey());
                int height = mArgs.getInt(OnyxConfig.CROP_SIZE_HEIGHT.getKey());
                onyxConfigurationBuilder.setCropSize(width, height);
            }
            if (mArgs.has(OnyxConfig.CROP_FACTOR.getKey())) {
                onyxConfigurationBuilder.setCropFactor(mArgs.getInt(OnyxConfig.CROP_FACTOR.getKey()));
            }
            if (mArgs.has(OnyxConfig.SHOW_SPINNER.getKey())) {
                onyxConfigurationBuilder.setShowLoadingSpinner(mArgs.getBoolean(OnyxConfig.SHOW_SPINNER.getKey()));
            }
            if (mArgs.has(OnyxConfig.LAYOUT_PREFERENCE.getKey())) {
                String layoutPreferenceString = mArgs.getString(OnyxConfig.LAYOUT_PREFERENCE.getKey());
                OnyxConfiguration.LayoutPreference layoutPreference = null;
                if (layoutPreferenceString.equalsIgnoreCase(OnyxConfig.LAYOUT_PREFERENCE_UPPER_THIRD.getKey())) {
                    layoutPreference = OnyxConfiguration.LayoutPreference.UPPER_THIRD;
                } else if (layoutPreferenceString.equalsIgnoreCase(OnyxConfig.LAYOUT_PREFERENCE_FULL.getKey())) {
                    layoutPreference = OnyxConfiguration.LayoutPreference.FULL;
                }
                onyxConfigurationBuilder.setLayoutPreference(layoutPreference);
            }
            if (mArgs.has(OnyxConfig.MANUAL_CAPTURE.getKey())) {
                onyxConfigurationBuilder.setUseManualCapture(mArgs.getBoolean(OnyxConfig.MANUAL_CAPTURE.getKey()));
            }
            if (mArgs.has(OnyxConfig.USE_ONYX_LIVE.getKey())) {
                onyxConfigurationBuilder.setUseOnyxLive(mArgs.getBoolean(OnyxConfig.USE_ONYX_LIVE.getKey()));
            }
            if (mArgs.has(OnyxConfig.USE_FLASH.getKey())) {
                onyxConfigurationBuilder.setUseFlash(mArgs.getBoolean(OnyxConfig.USE_FLASH.getKey()));
            }
            if (mArgs.has(OnyxConfig.RETICLE_ORIENTATION.getKey())) {
                String reticleOrientationString = mArgs.getString(OnyxConfig.RETICLE_ORIENTATION.getKey());
                Reticle.Orientation reticleOrientation = null;
                if (reticleOrientationString.equalsIgnoreCase(OnyxConfig.RETICLE_ORIENTATION_LEFT.getKey())) {
                    reticleOrientation = Reticle.Orientation.LEFT;
                } else if (reticleOrientationString.equalsIgnoreCase(OnyxConfig.RETICLE_ORIENTATION_RIGHT.getKey())) {
                    reticleOrientation = Reticle.Orientation.RIGHT;
                }
                onyxConfigurationBuilder.setReticleOrientation(reticleOrientation);
            }
            if (mArgs.has(OnyxConfig.RETICLE_SCALE.getKey())) {
                onyxConfigurationBuilder.setReticleScale(mArgs.getInt(OnyxConfig.RETICLE_SCALE.getKey()));
            }
            if (mArgs.has(OnyxConfig.RETICLE_ANGLE.getKey())) {
                onyxConfigurationBuilder.setReticleAngle(mArgs.getInt(OnyxConfig.RETICLE_ANGLE.getKey()));
            }
            if (mCaptureAfterSetup) {
                onyxConfigurationBuilder.setShowLoadingSpinner(mCaptureAfterSetup);
            }

            // Finally, build the OnyxConfiguration
            onyxConfigurationBuilder.buildOnyxConfiguration();

            keepCordovaCallback();
        } catch (JSONException e) {
            onError("JSONException: " + e.getMessage());
        }
    }

    public static void onFinished(int resultCode, JSONObject result) {
        if (resultCode == Activity.RESULT_OK) {
            mPluginResult = new PluginResult(PluginResult.Status.OK);
            try {
                result.put("action", mExecuteAction);
            } catch (JSONException e) {
                String errorMessage = "Failed to set JSON key value pair: " + e.getMessage();
                Log.e(TAG, errorMessage);
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
        mCallbackContext.error(errorMessage);
        mPluginResult = new PluginResult(PluginResult.Status.ERROR);
        mCallbackContext.sendPluginResult(mPluginResult);
    }

//    @Override
//    public void onRequestPermissionResult(int requestCode, String[] permissions,
//                                          int[] grantResults) throws JSONException {
//        super.onRequestPermissionResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_STORAGE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    launchOnyx();
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Log.e(TAG, "Storage permission denied.");
//                    onError("Write external storage permission denied.");
//                }
//                return;
//            }
//        }
//    }

    private void launchOnyx() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
//                Bundle bundle = new Bundle();
//                bundle.putString("options", mArgsString);
                Intent onyxIntent = new Intent(mContext, OnyxActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                onyxIntent.putExtra("options", mArgsString);
                mContext.startActivity(onyxIntent);
            }
        });
        keepCordovaCallback();
    }
}