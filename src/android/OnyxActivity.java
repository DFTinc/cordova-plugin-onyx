package com.dft.cordova.plugin.onyx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.NfiqMetrics;
import com.dft.onyx.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mjwheatley on 4/19/16.
 */
public class OnyxActivity extends Activity {
    private static final String TAG = OnyxActivity.class.getSimpleName();
//    public static final int RC_ONYX_ENROLL = 367655;
//    public static final int RC_ONYX_IMAGE = 46843;
//    public static final int RC_ONYX_VERIFY = 837439;
//    public static final int RC_ONYX_TEMPLATE = 83675283;
//    private static final String ONYX_IMAGE_TYPE_RAW = "raw";
//    private static final String ONYX_IMAGE_TYPE_PREPROCESSED = "preprocessed";
//    private static final String ONYX_IMAGE_TYPE_ENHANCED = "enhanced";
//    private static final String ONYX_IMAGE_TYPE_WSQ = "wsq";
    private Activity mActivity;
    private Context mContext;
//    private File mRawFingerprintImageFile;
//    private File mPreprocessedFingerprintImageFile;
//    private File mEnhancedFingerprintImageFile;
//    private JSONObject mImageTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        OnyxPlugin.mOnyx.create(mActivity);
        OnyxPlugin.mOnyx.capture();
//        String argString = getIntent().getStringExtra("options");
//        JSONObject arg_object = null;
//        String onyxLicense = null;
//        try {
//            arg_object = new JSONObject(argString);
//            onyxLicense = arg_object.getString("onyxLicense");
//
//            switch (OnyxPlugin.mPluginAction) {
//                case IMAGE:
//                    JSONArray imageTypes = arg_object.getJSONArray("imageTypes");
//                    mImageTypes = imageTypes.toJSONObject(imageTypes);
//
//                    // Always return at least the preprocessed image
//                    if (null == imageTypes ||
//                            imageTypes.length() == 0 ||
//                            mImageTypes.has(ONYX_IMAGE_TYPE_PREPROCESSED) ||
//                            mImageTypes.has(ONYX_IMAGE_TYPE_WSQ)) {
//                        mPreprocessedFingerprintImageFile = new File(
//                                Environment.getExternalStorageDirectory(), "PreprocessedFingerprintImage.jpg");
//                    }
//
//                    if (mImageTypes.has(ONYX_IMAGE_TYPE_RAW)) {
//                        mRawFingerprintImageFile = new File(
//                                Environment.getExternalStorageDirectory(), "RawFingerprintImage.jpg");
//                    }
//                    if (mImageTypes.has(ONYX_IMAGE_TYPE_ENHANCED)) {
//                        mEnhancedFingerprintImageFile = new File(
//                                Environment.getExternalStorageDirectory(), "EnhancedFingerprintImage.jpg");
//                    }
//
//                    Intent fingerImageIntent = FingerWizardIntentHelper.getFingerWizardIntent(
//                            mContext, onyxLicense, false, false, false, CaptureConfiguration.Flip.NONE,
//                            mRawFingerprintImageFile, mEnhancedFingerprintImageFile,
//                            mPreprocessedFingerprintImageFile, Bitmap.CompressFormat.JPEG);
//                    startActivityForResult(fingerImageIntent, RC_ONYX_IMAGE);
//                    break;
//                case ENROLL:
//                    EnrollWizardBuilder ewb = new EnrollWizardBuilder();
//                    ewb.setLicenseKey(onyxLicense);
//                    ewb.setUseSelfEnroll(true);
//                    ewb.setUseOnyxGuide(true, true, false);
//                    ewb.setNumEnrollCapturesPerScale(1);
//                    ewb.setNumEnrollScales(1);
//                    ewb.setMinReticleScale(1.0f);
//                    Intent enrollWizardIntent = ewb.build(mContext);
//                    startActivityForResult(enrollWizardIntent, RC_ONYX_ENROLL);
//                    break;
//                case TEMPLATE:
//                    Intent fingerprintTemplateIntent = FingerWizardIntentHelper.getFingerWizardIntent(
//                            mContext, onyxLicense, false, false, false, CaptureConfiguration.Flip.NONE,
//                            null, null, null, null);
//                    startActivityForResult(fingerprintTemplateIntent, RC_ONYX_TEMPLATE);
//                    break;
//                case VERIFY:
//                    if (fingerprintExists()) {
//                        Intent verifyIntent = VerifyIntentHelper.getVerifyActivityIntent(
//                                mContext, onyxLicense);
//                        startActivityForResult(verifyIntent, RC_ONYX_VERIFY);
//                    } else {
//                        Toast.makeText(mContext, "No fingerprints enrolled.",
//                                Toast.LENGTH_LONG).show();
//                        String errorMessage = "No fingerprints enrolled";
//                        OnyxPlugin.onError(errorMessage);
//                        finish();
//                    }
//                    break;
//            }
//        } catch (JSONException e) {
//            String errorMessage = "Error parsing JSON Object: " + e.toString();
//            Log.e(TAG, errorMessage);
//            OnyxPlugin.onError(errorMessage);
//            finish();
//        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        JSONObject result = new JSONObject();
//        String errorMessage = null;
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == RC_ONYX_IMAGE) {
//                String preprocessedImageUri = null;
//                String rawImageUri = null;
//                String enhancedImageUri = null;
//                JSONObject wsqImageJson = null;
//
//                if (null != mPreprocessedFingerprintImageFile &&
//                        mImageTypes.has(ONYX_IMAGE_TYPE_PREPROCESSED)) {
//                    preprocessedImageUri = createImageUriFromFile(mPreprocessedFingerprintImageFile);
//                }
//                if (null != mRawFingerprintImageFile) {
//                    rawImageUri = createImageUriFromFile(mRawFingerprintImageFile);
//                }
//                if (null != mEnhancedFingerprintImageFile) {
//                    enhancedImageUri = createImageUriFromFile(mEnhancedFingerprintImageFile);
//                }
//
//                JSONObject images = new JSONObject();
//                try {
//
//                    if (mImageTypes.has(ONYX_IMAGE_TYPE_WSQ)) {
//                        wsqImageJson = getWsqImageJson();
//                    }
//
//                    images.put(ONYX_IMAGE_TYPE_PREPROCESSED, preprocessedImageUri);
//                    images.put(ONYX_IMAGE_TYPE_RAW, rawImageUri);
//                    images.put(ONYX_IMAGE_TYPE_ENHANCED, enhancedImageUri);
//                    images.put(ONYX_IMAGE_TYPE_WSQ, wsqImageJson);
//                    result.put("images", images);
//                } catch (JSONException e) {
//                    errorMessage = "Failed to set JSON key value pair: " + e.toString();
//                }
//            } else if (requestCode == RC_ONYX_ENROLL || requestCode == RC_ONYX_TEMPLATE) {
//                // Get the EnrollmentMetric
//                EnrollmentMetric em = null;
//                if (data != null && data.hasExtra(Consts.EXTRA_ENROLLMENT_METRIC)) {
//                    em = (EnrollmentMetric) data.getSerializableExtra(
//                            Consts.EXTRA_ENROLLMENT_METRIC);
//                } else {
//                    EnrolledFingerprintDetails fingerprintDetails = EnrolledFingerprintDetails
//                            .getInstance();
//                    if (fingerprintDetails.enrolledEnrollmentMetricExists(mContext)) {
//                        em = fingerprintDetails.getEnrolledEnrollmentMetric(mContext);
//                    }
//                }
//
//                if (em != null) {
//                    // Get best fingerprint template from array
//                    FingerprintTemplate[] fpTemplateArray = em.getFingerprintTemplateArray();
//                    List<FingerprintTemplate> fpTemplateList = Arrays.asList(fpTemplateArray);
//                    FingerprintTemplate bestTemplate = null;
//                    for (FingerprintTemplate fpt : fpTemplateList) {
//                        if (null != fpt) {
//                            if (null == bestTemplate) {
//                                bestTemplate = fpt;
//                            } else if (fpt.getNfiqScore() > bestTemplate.getNfiqScore()) {
//                                bestTemplate = fpt;
//                            }
//                        }
//                    }
//
//                    if (null != bestTemplate) {
//                        Log.d(TAG, "nfiqScore: " + bestTemplate.getNfiqScore());
//                        if (bestTemplate.getNfiqScore() < 5) {
//                            try {
//                                float score = core.verify(bestTemplate, bestTemplate);
//                                if (score >= 34.0f) {
//                                    String bytesString = Base64.encodeToString(
//                                            bestTemplate.getData(),
//                                            Base64.URL_SAFE | Base64.NO_WRAP
//                                    ).trim();
//                                    int nfiqScore = bestTemplate.getNfiqScore();
//
//                                    result.put("template", bytesString);
//                                    result.put("nfiqScore", nfiqScore);
//                                } else {
//                                    errorMessage = "Fingerprint template failed self validation.";
//                                }
//                            } catch (JSONException e) {
//                                errorMessage = "Failed to set JSON key value pair: " + e.toString();
//                            } catch (Exception e) {
//                                errorMessage = "Exception verifying templates: " + e.toString();
//                            }
//                        } else {
//                            errorMessage = "Insufficent NFIQ Score";
//                        }
//                    } else {
//                        errorMessage = "Fingerprint template not found.";
//                    }
//                } else {
//                    errorMessage = "Unable to retrieve enrollment metric.";
//                }
//            } else if (requestCode == RC_ONYX_VERIFY) {
//                try {
//                    result.put("isVerified", true);
//                } catch (JSONException e) {
//                    errorMessage = "Failed to set JSON key value pair: " + e.toString();
//                }
//            }
//        }
//
//        if (null != errorMessage) {
//            Log.e(TAG, errorMessage);
//            OnyxPlugin.onError(errorMessage);
//        } else {
//            OnyxPlugin.onFinished(resultCode, result);
//        }
//        finish();
//    }
//
//    private String createImageUriFromFile(File file) {
//        String imageUri = null;
//        byte[] imageBytes = fileToArrayOfBytes(file);
//        if (null != imageBytes) {
//            imageUri = createImageUriFromBytes(imageBytes);
//        }
//        return imageUri;
//    }
//
//    private String createImageUriFromBytes(byte[] imageBytes) {
//        String encodedBytes = null;
//        String imageUri = null;
//        String imageUriPrefix = "data:image/jpeg;base64,";
//        if (null != imageBytes) {
//            encodedBytes = Base64.encodeToString(imageBytes, 0);
//            imageUri = imageUriPrefix + encodedBytes;
//        }
//        return imageUri;
//    }
//
//    private JSONObject getWsqImageJson() throws JSONException {
//        JSONObject wsqImageJson = null;
//        byte[] wsqBytes = null;
//        String encodedBytes = null;
//        int wsqImageNfiqScore = -1;
//        RestoreBitmap restoreBitmap = new RestoreBitmap();
//        Bitmap processedBitmap = restoreBitmap.restoreProcessedBitmapFromFile(
//                mPreprocessedFingerprintImageFile);
//
//        if (null != processedBitmap) {
//            wsqImageJson = new JSONObject();
//            Mat mat = new Mat();
//            Utils.bitmapToMat(processedBitmap, mat);
//            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY); // ensure image is grayscale
//            wsqBytes = core.matToWsq(mat);
//            if (null != wsqBytes) {
//                encodedBytes = Base64.encodeToString(wsqBytes, 0);
//            }
//            NfiqMetrics nfiqMetrics = core.computeNfiq(mat);
//            wsqImageNfiqScore = nfiqMetrics.getNfiqScore();
//        }
//
//        wsqImageJson.put("bytes", encodedBytes);
//        wsqImageJson.put("nfiqScore", wsqImageNfiqScore);
//
//        return wsqImageJson;
//    }
//
//    private byte[] fileToArrayOfBytes(File file) {
//        FileInputStream fileInputStream = null;
//
//        byte[] bytes = new byte[(int) file.length()];
//
//        try {
//            //convert file into array of bytes
//            fileInputStream = new FileInputStream(file);
//            fileInputStream.read(bytes);
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            String errorMessage = "Error getting file bytes: " + e.toString();
//            Log.e(TAG, errorMessage);
//        }
//        return bytes;
//    }
//
//    static File mEnrolledFile = null;
//
//    private boolean fingerprintExists() {
//        mEnrolledFile = getFileStreamPath(Consts.ENROLLED_ENROLLMENT_METRIC_FILENAME);
//        if (mEnrolledFile.exists()) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
