package com.dft.cordova.plugin.onyx;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OnyxPlugin extends CordovaPlugin {

    public static final String TAG = "OnyxPlugin";
    private static final int PERMISSIONS_REQUEST_STORAGE = 346437;

    public static String mPackageName;

    public static CallbackContext mCallbackContext;
    public static PluginResult mPluginResult;

    public static final String PLUGIN_ACTION_ENROLL = "enroll";
    public static final String PLUGIN_ACTION_VERIFY = "verify";
    public static final String PLUGIN_ACTION_TEMPLATE = "template";
    public static final String PLUGIN_ACTION_IMAGE = "image";
    public static enum PluginAction {
        ENROLL,
        VERIFY,
        TEMPLATE,
        IMAGE
    }
    public static PluginAction mPluginAction;

    private Activity mActivity;
    private Context mContext;
    private static String mExecuteAction;
    private static String mArgsString;
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

        final JSONObject arg_object = args.getJSONObject(0);
        if (!arg_object.has("onyxLicense") || !arg_object.has("action")) {
            mPluginResult = new PluginResult(PluginResult.Status.ERROR);
            mCallbackContext.error("Missing required parameters");
            mCallbackContext.sendPluginResult(mPluginResult);
            return true;
        }

        if (action.equalsIgnoreCase(PLUGIN_ACTION_ENROLL)) {
            mPluginAction = PluginAction.ENROLL;
        } else if (action.equalsIgnoreCase(PLUGIN_ACTION_VERIFY)) {
            mPluginAction = PluginAction.VERIFY;
        } else if (action.equalsIgnoreCase(PLUGIN_ACTION_TEMPLATE)) {
            mPluginAction = PluginAction.TEMPLATE;
        } else if (action.equalsIgnoreCase(PLUGIN_ACTION_IMAGE)) {
            mPluginAction = PluginAction.IMAGE;
        }

        if (null != mPluginAction) {
            mArgsString = arg_object.toString();
            if (cordova.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                launchOnyx();
            } else {
                cordova.requestPermission(this, PERMISSIONS_REQUEST_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            return true;
        }
        return false;
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

    public static void onError(String errorMessage) {
        mCallbackContext.error(errorMessage);
        mPluginResult = new PluginResult(PluginResult.Status.ERROR);
        mCallbackContext.sendPluginResult(mPluginResult);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchOnyx();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e(TAG, "Storage permission denied.");
                    onError("Write external storage permission denied.");
                }
                return;
            }
        }
    }

    private void launchOnyx() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("options", mArgsString);
                Intent onyxIntent = new Intent(mContext, OnyxActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                onyxIntent.putExtra("options", mArgsString);
                mContext.startActivity(onyxIntent);
            }
        });
        mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        mPluginResult.setKeepCallback(true);
        mCallbackContext.sendPluginResult(mPluginResult);
    }
}