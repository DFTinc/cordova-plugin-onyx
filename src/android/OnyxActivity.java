package com.dft.cordova.plugin.onyx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.NfiqMetrics;
import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxConfigurationBuilder;
import com.dft.onyxcamera.config.OnyxError;
import com.dft.onyxcamera.config.OnyxResult;
import com.dft.onyxcamera.ui.CaptureMetrics;
import com.dft.onyxcamera.ui.reticles.Reticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.dft.onyxcamera.config.OnyxConfiguration.ErrorCallback.Error.AUTOFOCUS_FAILURE;

public class OnyxActivity extends Activity {
  private static final String TAG = OnyxActivity.class.getSimpleName();
  private static final String ONYX_BLUE_HEX_STRING = "#3698D3";
  private Activity mActivity;
  private Context mContext;
  private Onyx mOnyx;
  private boolean mUseManualCapture = false;
  private boolean mIsProcessingManualCapture = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = this;
    mContext = this;
    requestWindowFeature(Window.FEATURE_NO_TITLE);

//    boolean showBackButton = true;
//    String backButtonText = "Back";
//    boolean showManualCaptureText = true;
//    String manualCaptureText = "Touch screen to capture.";

    int backgroundColor = Color.parseColor(ONYX_BLUE_HEX_STRING);
    try {
      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.BACKGROUND_COLOR_HEX_STRING.getKey())) {
        String backgroundColorHexString = OnyxPlugin.mArgs.getString(
          OnyxPlugin.OnyxConfig.BACKGROUND_COLOR_HEX_STRING.getKey());
        if (!backgroundColorHexString.isEmpty()) {
          backgroundColor = Color.parseColor(backgroundColorHexString);
        }
      }

//      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.USE_MANUAL_CAPTURE.getKey())) {
//        mUseManualCapture = OnyxPlugin.mArgs.getBoolean(
//          OnyxPlugin.OnyxConfig.USE_MANUAL_CAPTURE.getKey());
//      }
//
//      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOW_BACK_BUTTON.getKey())) {
//        showBackButton = OnyxPlugin.mArgs.getBoolean(
//          OnyxPlugin.OnyxConfig.SHOW_BACK_BUTTON.getKey());
//      }
//
//      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.BACK_BUTTON_TEXT.getKey())) {
//        backButtonText = OnyxPlugin.mArgs.getString(
//          OnyxPlugin.OnyxConfig.BACK_BUTTON_TEXT.getKey());
//      }
//
//      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOW_MANUAL_CAPTURE_TEXT.getKey())) {
//        showManualCaptureText = OnyxPlugin.mArgs.getBoolean(
//          OnyxPlugin.OnyxConfig.SHOW_MANUAL_CAPTURE_TEXT.getKey());
//      }
//
//      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.MANUAL_CAPTURE_TEXT.getKey())) {
//        manualCaptureText = OnyxPlugin.mArgs.getString(
//          OnyxPlugin.OnyxConfig.MANUAL_CAPTURE_TEXT.getKey());
//      }
    } catch (JSONException e) {
      String errorMessage = "Failed to set JSON key value pair: " + e.getMessage();
      onError(errorMessage);
    } catch (IllegalArgumentException e) {
      String errorMessage = "Unable to parse background color: " + e.getMessage();
      onError(errorMessage);
    }

    LinearLayout rootView = new LinearLayout(mContext);
    rootView.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.MATCH_PARENT
    );
    rootView.setLayoutParams(llp);
    rootView.setBackgroundColor(backgroundColor);

//    // Space for capture screen
//    // Higher weight number has lower priority
//    LinearLayout captureSpace = new LinearLayout(mContext);
//    LinearLayout.LayoutParams captureSpaceLayoutParams = new LinearLayout.LayoutParams(
//      RelativeLayout.LayoutParams.MATCH_PARENT,
//      RelativeLayout.LayoutParams.MATCH_PARENT);
//    captureSpaceLayoutParams.weight = 2;
//    captureSpace.setLayoutParams(captureSpaceLayoutParams);
//    captureSpace.setBackgroundColor(Color.TRANSPARENT);
//
//    // Start content 1/3 height from top.
//    RelativeLayout contentRelativeLayout = new RelativeLayout(mContext);
//    LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(
//      RelativeLayout.LayoutParams.MATCH_PARENT,
//      RelativeLayout.LayoutParams.MATCH_PARENT);
//    contentLayoutParams.weight = 1;
//    contentRelativeLayout.setLayoutParams(contentLayoutParams);
//
//    contentRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
//
//    // Onyx branding
//    int onyxIconId = mContext.getResources()
//      .getIdentifier("onyx_icon", "drawable", OnyxPlugin.mPackageName);
//    Drawable onyxIcon = mContext.getResources().getDrawable(onyxIconId);
//    ImageView onyxImageView = new ImageView(mContext);
//    onyxImageView.setImageDrawable(onyxIcon);
//    RelativeLayout.LayoutParams onyxImageViewLayoutParams = new RelativeLayout.LayoutParams(
//      getPxForDp(mContext, 48),
//      getPxForDp(mContext, 48)
//    );
//    onyxImageViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//    onyxImageViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//    onyxImageViewLayoutParams.setMargins(16, 16, 16, 16);
//    onyxImageView.setLayoutParams(onyxImageViewLayoutParams);
//
//    contentRelativeLayout.addView(onyxImageView);
//
//    if (showBackButton) {
//      // Border
//      GradientDrawable backgroundDrawable = new GradientDrawable();
//      backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
//      backgroundDrawable.setColor(Color.parseColor("#25000000"));
//      backgroundDrawable.setStroke(5, Color.WHITE);
//
//      Button backButton = new Button(mContext);
//      backButton.setText(backButtonText);
//      backButton.setTextColor(Color.WHITE);
//      backButton.setBackground(backgroundDrawable);
//      RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
//        RelativeLayout.LayoutParams.WRAP_CONTENT,
//        RelativeLayout.LayoutParams.WRAP_CONTENT
//      );
//      buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//      buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//      buttonLayoutParams.setMargins(16, 16, 16, 16);
//      backButton.setLayoutParams(buttonLayoutParams);
//
//      backButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          onBackPressed();
//        }
//      });
//
//      contentRelativeLayout.addView(backButton);
//    }
//
//    if (mUseManualCapture) {
//      if (showManualCaptureText) {
//        TextView manualCaptureTextView = new TextView(mContext);
//        RelativeLayout.LayoutParams manualCaptureViewLayoutParams = new RelativeLayout.LayoutParams(
//          RelativeLayout.LayoutParams.MATCH_PARENT,
//          getPxForDp(mContext, 48)
//        );
//        manualCaptureTextView.setText(manualCaptureText);
//        manualCaptureTextView.setGravity(Gravity.CENTER);
//        manualCaptureTextView.setTextColor(Color.WHITE);
//        manualCaptureTextView.setBackgroundColor(Color.parseColor("#25000000"));
//        int padding = getPxForDp(mContext, 8);
//        manualCaptureTextView.setPadding(padding, padding, padding, padding);
//        manualCaptureTextView.setLayoutParams(manualCaptureViewLayoutParams);
//        contentRelativeLayout.addView(manualCaptureTextView);
//      }
//
//      contentRelativeLayout.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          if (!mIsProcessingManualCapture) {
//            mIsProcessingManualCapture = true;
//            if (null != mOnyx) {
//              mOnyx.capture();
//            }
//          }
//        }
//      });
//    }
//
//
//    if (null != infoText || null != base64ImageData) {
//      LinearLayout infoView = new LinearLayout(mContext);
//      infoView.setOrientation(LinearLayout.VERTICAL);
//      LinearLayout.LayoutParams infoViewLayoutParams = new LinearLayout.LayoutParams(
//        LinearLayout.LayoutParams.MATCH_PARENT,
//        LinearLayout.LayoutParams.MATCH_PARENT
//      );
//      infoView.setLayoutParams(infoViewLayoutParams);
//
//      int padding = getPxForDp(mContext, 8);
//      int toolbarPadding = getPxForDp(mContext, 56);
//      boolean isTopPaddingNeeded = (mUseManualCapture && showManualCaptureText);
//
//      if (null != base64ImageData) {
//        ImageView imageView = new ImageView(mContext);
//        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(
//          LinearLayout.LayoutParams.MATCH_PARENT,
//          LinearLayout.LayoutParams.MATCH_PARENT,
//          1
//        );
//        imageView.setLayoutParams(imageViewLayoutParams);
//        boolean isBottomPaddingNeeded = (null == infoText);
//        imageView.setPadding(
//          padding,
//          isTopPaddingNeeded ? toolbarPadding : padding,
//          padding,
//          isBottomPaddingNeeded ? toolbarPadding : 0
//        );
//
//        // Convert base64 image data to bitmap
//        byte[] decodedString = Base64.decode(base64ImageData, Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        imageView.setImageBitmap(bitmap);
//        infoView.addView(imageView);
//      }
//
//      if (null != infoText) {
//        int infoTextColor = isColorDark(backgroundColor) ? Color.WHITE : Color.BLACK;
//        if (null != infoTextColorHexString) {
//          infoTextColor = Color.parseColor(infoTextColorHexString);
//        }
//
//        TextView infoTextView = new TextView(mContext);
//        LinearLayout.LayoutParams infoTextViewLayoutParams = new LinearLayout.LayoutParams(
//          RelativeLayout.LayoutParams.MATCH_PARENT,
//          RelativeLayout.LayoutParams.MATCH_PARENT,
//          1
//        );
//        infoTextView.setText(infoText);
//        infoTextView.setGravity(Gravity.CENTER);
//        infoTextView.setTextColor(infoTextColor);
//        infoTextView.setLayoutParams(infoTextViewLayoutParams);
//        infoTextView.setPadding(
//          padding,
//          (isTopPaddingNeeded && null == base64ImageData) ? toolbarPadding : padding,
//          padding,
//          toolbarPadding);
//        infoView.addView(infoTextView);
//      }
//
//      contentRelativeLayout.addView(infoView);
//    }
//
//    rootView.addView(captureSpace);
//    rootView.addView(contentRelativeLayout);
    setContentView(rootView);
  }

  public int getPxForDp(Context context, float dp) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
  }

  public boolean isColorDark(int color) {
    double darkness = 1 - (0.299 * Color.red(color) +
      0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
    return darkness >= 0.19;
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
            Log.e(TAG, error.toString());
            if (error.error != AUTOFOCUS_FAILURE) {
              OnyxPlugin.onError(error.errorMessage);
              finish();
            } else {
              mActivity.runOnUiThread(new Runnable() {
                public void run() {
                  if (!mUseManualCapture) {
                    mOnyx.capture();
                  }
                }
              });
            }
          }
        })
        .setOnyxCallback(new OnyxConfiguration.OnyxCallback() {
          @Override
          public void onConfigured(final Onyx configuredOnyx) {
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

      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.SHOULD_CONVERT_TO_ISO_TEMPLATE.getKey())) {
        onyxConfigurationBuilder.setShouldConvertToISOTemplate(OnyxPlugin.mArgs.getBoolean(
          OnyxPlugin.OnyxConfig.SHOULD_CONVERT_TO_ISO_TEMPLATE.getKey()));
      }

      if (OnyxPlugin.mArgs.has(OnyxPlugin.OnyxConfig.COMPUTE_NFIQ_METRICS.getKey())) {
        onyxConfigurationBuilder.setComputeNfiqMetrics(OnyxPlugin.mArgs.getBoolean(
          OnyxPlugin.OnyxConfig.COMPUTE_NFIQ_METRICS.getKey()));
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
          } else if (reticleOrientationString.equalsIgnoreCase(
            OnyxPlugin.OnyxConfig.RETICLE_ORIENTATION_THUMB_PORTRAIT.getKey())) {
            reticleOrientation = Reticle.Orientation.THUMB_PORTRAIT;
          }
          onyxConfigurationBuilder.setReticleOrientation(reticleOrientation);
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
      ArrayList<JSONObject> onyxResults = new ArrayList<JSONObject>();
      ArrayList<Bitmap> rawFingerprintImages = null;
      ArrayList<Bitmap> processedFingerprintImages = null;
      ArrayList<Bitmap> enhancedFingerprintImages = null;
      ArrayList<byte[]> wsqDataArrayList = null;
      ArrayList<FingerprintTemplate> fingerprintTemplates = null;
      int numberFingersProcessed = 0;
      try {
        if (null != onyxResult.getRawFingerprintImages()) {
          rawFingerprintImages = onyxResult.getRawFingerprintImages();
          if (rawFingerprintImages.size() > numberFingersProcessed) {
            numberFingersProcessed = rawFingerprintImages.size();
          }
        }
        if (null != onyxResult.getProcessedFingerprintImages()) {
          processedFingerprintImages = onyxResult.getProcessedFingerprintImages();
          if (processedFingerprintImages.size() > numberFingersProcessed) {
            numberFingersProcessed = processedFingerprintImages.size();
          }
        }
        if (null != onyxResult.getEnhancedFingerprintImages()) {
          enhancedFingerprintImages = onyxResult.getEnhancedFingerprintImages();
          if (enhancedFingerprintImages.size() > numberFingersProcessed) {
            numberFingersProcessed = enhancedFingerprintImages.size();
          }
        }
        if (null != onyxResult.getWsqData()) {
          wsqDataArrayList = onyxResult.getWsqData();
          if (wsqDataArrayList.size() > numberFingersProcessed) {
            numberFingersProcessed = wsqDataArrayList.size();
          }
        }
        if (null != onyxResult.getFingerprintTemplates()) {
          fingerprintTemplates = onyxResult.getFingerprintTemplates();
          if (fingerprintTemplates.size() > numberFingersProcessed) {
            numberFingersProcessed = fingerprintTemplates.size();
          }
        }

        Log.v(TAG, "numberFingersProcessed: " + numberFingersProcessed);
        for (int i = 0; i < numberFingersProcessed; i++) {
          JSONObject iOnyxResult = new JSONObject();
          JSONObject captureMetrics = new JSONObject();
          String rawFingerprintDataUri = null;
          String processedFingerprintDataUri = null;
          String enhancedFingerprintDataUri = null;
          String base64EncodedWsqBytes = null;
          String base64EncodedFingerprintTemplate = null;
          if (null != rawFingerprintImages &&
            rawFingerprintImages.size() == numberFingersProcessed) {
            rawFingerprintDataUri = getDataUriFromBitmap(
              rawFingerprintImages.get(i).copy(Bitmap.Config.RGB_565, false));
            iOnyxResult.put("rawFingerprintDataUri", rawFingerprintDataUri);
          }
          if (null != processedFingerprintImages &&
            processedFingerprintImages.size() == numberFingersProcessed) {
            processedFingerprintDataUri = getDataUriFromBitmap(
              processedFingerprintImages.get(i).copy(Bitmap.Config.RGB_565, false));
            iOnyxResult.put("processedFingerprintDataUri", processedFingerprintDataUri);
          }
          if (null != enhancedFingerprintImages &&
            enhancedFingerprintImages.size() == numberFingersProcessed) {
            enhancedFingerprintDataUri = getDataUriFromBitmap(
              enhancedFingerprintImages.get(i).copy(Bitmap.Config.RGB_565, false));
            iOnyxResult.put("enhancedFingerprintDataUri", enhancedFingerprintDataUri);
          }
          if (null != wsqDataArrayList &&
            wsqDataArrayList.size() == numberFingersProcessed) {
            base64EncodedWsqBytes = Base64.encodeToString(wsqDataArrayList.get(i), 0)
              .trim();
            iOnyxResult.put("base64EncodedWsqBytes", base64EncodedWsqBytes);
          }
          if (null != fingerprintTemplates &&
            fingerprintTemplates.size() == numberFingersProcessed) {
            base64EncodedFingerprintTemplate = Base64.encodeToString(
              fingerprintTemplates.get(i).getData(), 0)
              .trim();
            iOnyxResult.put("base64EncodedFingerprintTemplate",
              base64EncodedFingerprintTemplate);
          }
          if (null != onyxResult.getMetrics()) {
            CaptureMetrics metrics = onyxResult.getMetrics();
            captureMetrics.put("focusQuality", metrics.getFocusQuality());
            captureMetrics.put("livenessConfidence", metrics.getLivenessConfidence());
            JSONObject nfiqMetrics = new JSONObject();
            Log.v(TAG, "getNfiqMetrics().size(): " + metrics.getNfiqMetrics());

            if (null != metrics.getNfiqMetrics() &&
              metrics.getNfiqMetrics().size() == numberFingersProcessed) {
              List<NfiqMetrics> nfiqMetricsList = metrics.getNfiqMetrics();
              nfiqMetrics.put("nfiqScore", nfiqMetricsList.get(i).getNfiqScore());
              nfiqMetrics.put("mlpScore", nfiqMetricsList.get(i).getMlpScore());
            }
            captureMetrics.put("nfiqMetrics", nfiqMetrics);
          }
          iOnyxResult.put("captureMetrics", captureMetrics);
          onyxResults.add(iOnyxResult);
        }
        result.put("onyxResults", new JSONArray(onyxResults.toArray()));
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
    String dataUri;
    String encodedBytes = Base64.encodeToString(imageBytes, 0).trim();
    dataUri = OnyxPlugin.IMAGE_URI_PREFIX + encodedBytes;
    return dataUri;
  }

  private void onError(String errorMessage) {
    OnyxPlugin.onError(errorMessage);
    finish();
  }
}
