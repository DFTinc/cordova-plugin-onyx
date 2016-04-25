package com.dft.cordova.plugin.onyx;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class OnyxPlugin extends CordovaPlugin {

	public static final String TAG = "OnyxPlugin";
	public static String mPackageName;

	public static CallbackContext mCallbackContext;
	public static PluginResult mPluginResult;

	private Context mContext;
	private static String mAction;

	/** Alias for our key in the Android Key Store */
	private static String mClientId;
	/** Used to encrypt token */
	private static String mClientSecret;

	/**
	 * Constructor.
	 */
	public OnyxPlugin() {
	}

	/**
	 * Sets the context of the Command. This can then be used to do things like
	 * get file paths associated with the Activity.
	 *
	 * @param cordova
	 *            The context of the main Activity.
	 * @param webView
	 *            The CordovaWebView Cordova is running in.
	 */

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.v(TAG, "Init Onyx");
		mPackageName = cordova.getActivity().getApplicationContext().getPackageName();
		mPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		mContext = cordova.getActivity().getApplicationContext();
	}

	/**
	 * Executes the request and returns PluginResult.
	 *
	 * @param action            The action to execute.
	 * @param args              JSONArry of arguments for the plugin.
	 * @param callbackContext   The callback id used when calling back into JavaScript.
	 * @return                  A PluginResult object with a status and message.
	 */
	public boolean execute(final String action,
						   JSONArray args,
						   CallbackContext callbackContext) throws JSONException {
		mCallbackContext = callbackContext;
		Log.v(TAG, "OnyxPlugin action: " + action);
		mAction = action;

		final JSONObject arg_object = args.getJSONObject(0);
		if (!arg_object.has("onyxLicense") || !arg_object.has("action")) {
			mPluginResult = new PluginResult(PluginResult.Status.ERROR);
			mCallbackContext.error("Missing required parameters");
			mCallbackContext.sendPluginResult(mPluginResult);
			return true;
		}

		if (mAction.equals("enroll") || mAction.equals("verify") ||
				mAction.equals("template") || mAction.equals("image")) {

			cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Bundle bundle = new Bundle();
					bundle.putString("options", arg_object.toString());
					Intent onyxIntent = new Intent(mContext, OnyxActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					onyxIntent.putExtra("options", arg_object.toString());
					mContext.startActivity(onyxIntent);
				}
			});
			mPluginResult.setKeepCallback(true);
			mCallbackContext.sendPluginResult(mPluginResult);
			return true;
		}
		return false;
	}

	public static void onFinished(JSONObject result) {
		mPluginResult = new PluginResult(PluginResult.Status.OK);
		try {
			result.put("action", mAction);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to set JSON key value pair: " + e.getMessage());
		}
		mCallbackContext.success(result);
		mCallbackContext.sendPluginResult(mPluginResult);
	}
}