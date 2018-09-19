package com.dft.cordova.plugin.onyx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxConfigurationBuilder;
import com.dft.onyxcamera.config.OnyxError;
import com.dft.onyxcamera.config.OnyxResult;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.reticles.Reticle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class OnyxActivity extends Activity {
    private static final String TAG = OnyxActivity.class.getSimpleName();
    private static final String ONYX_BLUE_HEX_STRING = "#3698D3";
    private Activity mActivity;
    private Context mContext;
    private Onyx mOnyx;
    private boolean mUseManualCapture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        boolean showBackButton = true;

        int backgroundColor = Color.parseColor(ONYX_BLUE_HEX_STRING);
        try {
            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.BACKGROUND_COLOR_HEX_STRING.getKey())) {
                String backgroundColorHexString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.BACKGROUND_COLOR_HEX_STRING.getKey());
                if (!backgroundColorHexString.isEmpty()) {
                    backgroundColor = Color.parseColor(backgroundColorHexString);
                }
                if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.USE_MANUAL_CAPTURE.getKey())) {
                    mUseManualCapture = OnyxPlugin.mArgs.getBoolean(
                            OnyxPlugin.OnyxConfig.USE_MANUAL_CAPTURE.getKey());
                }
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOW_BACK_BUTTON.getKey())) {
                showBackButton = OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.SHOW_BACK_BUTTON.getKey());
            }
        } catch (JSONException e) {
            String errorMessage = "Failed to set JSON key value pair: " + e.getMessage();
            onError(errorMessage);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Unable to parse background color: " + e.getMessage();
            onError(errorMessage);
        }

        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.setBackgroundColor(backgroundColor);

        if (showBackButton) {
            Button backButton = new Button(mContext);
            backButton.setText("Back");
            backButton.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_LEFT);
            backButton.setLayoutParams(buttonLayoutParams);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            relativeLayout.addView(backButton);
        }

        if (mUseManualCapture) {
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnyx) {
                        mOnyx.capture();
                    }
                }
            });
        }

        setContentView(relativeLayout, rlp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupOnyx();
    }

    private void setupUI() {

    }

    private void setupOnyx() {
        try {
            // Create an OnyxConfigurationBuilder and configure it with desired options
            String onyxLicense = OnyxPlugin.mArgs.getString(
                    OnyxPlugin.OnyxConfig.ONYX_LICENSE.getKey());

            OnyxConfigurationBuilder onyxConfigurationBuilder = new OnyxConfigurationBuilder()
                    .setActivity(mActivity)
                    .setLicenseKey(onyxLicense)
                    .setSuccessCallback(onyxSuccessCallback)
                    .setErrorCallback(new OnyxConfiguration.ErrorCallback() {
                        @Override
                        public void onError(OnyxError error) {
                            Log.e(TAG, error.errorMessage);
                            OnyxPlugin.onError(error.errorMessage);
                            finish();
                        }
                    })
                    .setOnyxCallback(new OnyxConfiguration.OnyxCallback() {
                        @Override
                        public void onConfigured(Onyx configuredOnyx) {
                            mActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    mOnyx = configuredOnyx;
                                    mOnyx.create(mActivity);
                                    if (!mUseManualCapture) {
                                        mOnyx.capture();
                                    }
                                }
                            });
                        }
                    });

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETURN_RAW_IMAGE.getKey())) {
                onyxConfigurationBuilder.setReturnRawImage(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.RETURN_RAW_IMAGE.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETURN_PROCESSED_IMAGE.getKey())) {
                onyxConfigurationBuilder.setReturnProcessedImage(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.RETURN_PROCESSED_IMAGE.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETURN_ENHANCED_IMAGE.getKey())) {
                onyxConfigurationBuilder.setReturnEnhancedImage(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.RETURN_ENHANCED_IMAGE.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETURN_WSQ.getKey())) {
                onyxConfigurationBuilder.setReturnWSQ(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.RETURN_WSQ.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETURN_FINGERPRINT_TEMPLATE.getKey())) {
                onyxConfigurationBuilder.setReturnFingerprintTemplate(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.RETURN_FINGERPRINT_TEMPLATE.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOULD_SEGMENT.getKey())) {
                onyxConfigurationBuilder.setShouldSegment(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.SHOULD_SEGMENT.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOULD_CONVERT_TO_ISO_TEMPLATE.getKey())) {
                onyxConfigurationBuilder.setShouldConvertToISOTemplate(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.SHOULD_CONVERT_TO_ISO_TEMPLATE.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.IMAGE_ROTATION.getKey())) {
                String imageRotationString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.IMAGE_ROTATION.getKey());
                if (!imageRotationString.isEmpty()) {
                    onyxConfigurationBuilder.setImageRotation(Integer.parseInt(imageRotationString));
                }
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.WHOLE_FINGER_CROP.getKey())) {
                onyxConfigurationBuilder.setWholeFingerCrop(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.WHOLE_FINGER_CROP.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.CROP_SIZE.getKey())) {
                JSONObject cropSize = OnyxPlugin.mArgs.getJSONObject(
                        OnyxPlugin.OnyxConfig.CROP_SIZE.getKey());
                int width = 512;
                int height = 300;
                if (cropSize.has(OnyxPlugin.OnyxConfig.CROP_SIZE_WIDTH.getKey())) {
                    String cropSizeWidthString = cropSize.getString(
                            OnyxPlugin.OnyxConfig.CROP_SIZE_WIDTH.getKey());
                    if (!cropSizeWidthString.isEmpty()) {
                        width = Integer.parseInt(cropSizeWidthString);
                    }
                }
                if (cropSize.has(OnyxPlugin.OnyxConfig.CROP_SIZE_HEIGHT.getKey())) {
                    String cropSizeHeightString = cropSize.getString(
                            OnyxPlugin.OnyxConfig.CROP_SIZE_HEIGHT.getKey());
                    if (!cropSizeHeightString.isEmpty()) {
                        height = Integer.parseInt(cropSizeHeightString);
                    }
                }
                onyxConfigurationBuilder.setCropSize(width, height);
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.CROP_FACTOR.getKey())) {
                String cropFactorString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.CROP_FACTOR.getKey());
                if (!cropFactorString.isEmpty()) {
                    onyxConfigurationBuilder.setCropFactor(Double.parseDouble(cropFactorString));
                }
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOW_LOADING_SPINNER.getKey())) {
                onyxConfigurationBuilder.setShowLoadingSpinner(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.SHOW_LOADING_SPINNER.getKey()));
            }

            OnyxConfiguration.LayoutPreference layoutPreference = OnyxConfiguration.LayoutPreference.UPPER_THIRD;
            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.LAYOUT_PREFERENCE.getKey())) {
                String layoutPreferenceString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.LAYOUT_PREFERENCE.getKey());
                if (!layoutPreferenceString.isEmpty()) {
                    if (layoutPreferenceString.equalsIgnoreCase(
                            OnyxPlugin.OnyxConfig.LAYOUT_PREFERENCE_UPPER_THIRD.getKey())) {
                        layoutPreference = OnyxConfiguration.LayoutPreference.UPPER_THIRD;
                    } else if (layoutPreferenceString.equalsIgnoreCase(
                            OnyxPlugin.OnyxConfig.LAYOUT_PREFERENCE_FULL.getKey())) {
                        layoutPreference = OnyxConfiguration.LayoutPreference.FULL;
                    }
                    onyxConfigurationBuilder.setLayoutPreference(layoutPreference);
                }
            }

            if (mUseManualCapture) {
                onyxConfigurationBuilder.setUseManualCapture(mUseManualCapture);
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.USE_ONYX_LIVE.getKey())) {
                onyxConfigurationBuilder.setUseOnyxLive(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.USE_ONYX_LIVE.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.USE_FLASH.getKey())) {
                onyxConfigurationBuilder.setUseFlash(OnyxPlugin.mArgs.getBoolean(
                        OnyxPlugin.OnyxConfig.USE_FLASH.getKey()));
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETICLE_ORIENTATION.getKey())) {
                String reticleOrientationString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.RETICLE_ORIENTATION.getKey());
                if (!reticleOrientationString.isEmpty()) {
                    Reticle.Orientation reticleOrientation = Reticle.Orientation.LEFT;
                    if (reticleOrientationString.equalsIgnoreCase(
                            OnyxPlugin.OnyxConfig.RETICLE_ORIENTATION_LEFT.getKey())) {
                        reticleOrientation = Reticle.Orientation.LEFT;
                    } else if (reticleOrientationString.equalsIgnoreCase(
                            OnyxPlugin.OnyxConfig.RETICLE_ORIENTATION_RIGHT.getKey())) {
                        reticleOrientation = Reticle.Orientation.RIGHT;
                    }
                    onyxConfigurationBuilder.setReticleOrientation(reticleOrientation);
                }
            }

            if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETICLE_SCALE.getKey())) {
                String reticleScaleString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.RETICLE_SCALE.getKey());
                if (!reticleScaleString.isEmpty()) {
                    onyxConfigurationBuilder.setReticleScale(Float.valueOf(reticleScaleString));
                }
            }

            if (layoutPreference == OnyxConfiguration.LayoutPreference.FULL
                    && OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.RETICLE_ANGLE.getKey())) {
                String reticleAngleString = OnyxPlugin.mArgs.getString(
                        OnyxPlugin.OnyxConfig.RETICLE_ANGLE.getKey());
                if (!reticleAngleString.isEmpty()) {
                    onyxConfigurationBuilder.setReticleAngle(Integer.parseInt(reticleAngleString));
                }
            }


            // Finally, build the OnyxConfiguration
            onyxConfigurationBuilder.buildOnyxConfiguration();
        } catch (JSONException e) {
            onError("JSONException: " + e.getMessage());
        }
    }

    private OnyxConfiguration.SuccessCallback onyxSuccessCallback = new OnyxConfiguration.SuccessCallback() {
        @Override
        public void onSuccess(OnyxResult onyxResult) {
            JSONObject result = new JSONObject();
            JSONObject captureMetrics = new JSONObject();
            String rawFingerprintDataUri = null;
            String processedFingerprintDataUri = null;
            String enhancedFingerprintDataUri = null;
            String base64EncodedWsqBytes = null;
            String base64EncodedFingerprintTemplate = null;
            try {
                if (null != onyxResult.getRawFingerprintImage()) {
                    rawFingerprintDataUri = getDataUriFromBitmap(
                            onyxResult.getRawFingerprintImage());
                    result.put("rawFingerprintDataUri", rawFingerprintDataUri);
                }
                if (null != onyxResult.getProcessedFingerprintImage()) {
                    processedFingerprintDataUri = getDataUriFromBitmap(
                            onyxResult.getProcessedFingerprintImage());
                    result.put("processedFingerprintDataUri", processedFingerprintDataUri);
                }
                if (null != onyxResult.getEnhancedFingerprintImage()) {
                    enhancedFingerprintDataUri = getDataUriFromBitmap(
                            onyxResult.getEnhancedFingerprintImage());
                    result.put("enhancedFingerprintDataUri", enhancedFingerprintDataUri);
                }
                if (null != onyxResult.getWsqData()) {
                    base64EncodedWsqBytes = Base64.encodeToString(onyxResult.getWsqData(), 0)
                            .trim();
                    result.put("base64EncodedWsqBytes", base64EncodedWsqBytes);
                }
                if (null != onyxResult.getFingerprintTemplate()) {
                    base64EncodedFingerprintTemplate = Base64.encodeToString(
                            onyxResult.getFingerprintTemplate().getData(), 0)
                            .trim();
                    result.put("base64EncodedFingerprintTemplate", base64EncodedFingerprintTemplate);
                }
                if (null != onyxResult.getMetrics()) {
                    CaptureMetrics metrics = onyxResult.getMetrics();
                    captureMetrics.put("focusQuality", metrics.getFocusQuality());
                    captureMetrics.put("livenessConfidence", metrics.getLivenessConfidence());
                    captureMetrics.put("distanceToCenter", metrics.getDistanceToCenter());
                    JSONObject nfiqMetrics = new JSONObject();
                    if (null != metrics.getNfiqMetrics()) {
                        nfiqMetrics.put("nfiqScore", metrics.getNfiqMetrics().getNfiqScore());
                        nfiqMetrics.put("mlpScore", metrics.getNfiqMetrics().getMlpScore());
                    }
                    captureMetrics.put("nfiqMetrics", nfiqMetrics);
                    JSONObject fillProperties = new JSONObject();
                    if (null != metrics.getFillProperties()) {
                        fillProperties.put("heightRatio", metrics.getFillProperties().getHeightRatio());
                        fillProperties.put("overlapRatio", metrics.getFillProperties().getOverlapRatio());
                    }
                    captureMetrics.put("fillProperties", fillProperties);
                }
                result.put("captureMetrics", captureMetrics);
            } catch (JSONException e) {
                String errorMessage = "Failed to set JSON key value pair: " + e.getMessage();
                onError(errorMessage);
            }

            OnyxPlugin.onFinished(Activity.RESULT_OK, result);
            finish();
        }
    };

    private String getDataUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        bitmap.recycle();
        String dataUri = null;
        if (null != imageBytes) {
            String dataUriPrefix = "data:image/jpeg;base64,";
            String encodedBytes = Base64.encodeToString(imageBytes, 0).trim();
            dataUri = dataUriPrefix + encodedBytes;
        }
        return dataUri;
    }

    private void onError(String errorMessage) {
        OnyxPlugin.onError(errorMessage);
        finish();
    }
}
