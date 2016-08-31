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
import com.dft.onyx.enroll.util.Consts;
import com.dft.onyx.enroll.util.EnrolledFingerprintDetails;
import com.dft.onyx.enroll.util.EnrollmentMetric;
import com.dft.onyx.fingerwizard.FingerWizardIntentHelper;
import com.dft.onyx.verify.VerifyIntentHelper;
import com.dft.onyx.wizardroid.enrollwizard.EnrollWizardBuilder;
import com.dft.onyxcamera.ui.CaptureConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by mjwheatley on 4/19/16.
 */
public class OnyxActivity extends Activity {
    private static final String TAG = OnyxActivity.class.getSimpleName();
    public static final int RC_ONYX_ENROLL = 367655;
    public static final int RC_ONYX_IMAGE = 46843;
    public static final int RC_ONYX_VERIFY = 837439;
    public static final int RC_ONYX_TEMPLATE = 83675283;
    private static final String ONYX_IMAGE_TYPE_RAW = "raw";
    private static final String ONYX_IMAGE_TYPE_PREPROCESSED = "preprocessed";
    private static final String ONYX_IMAGE_TYPE_ENHANCED = "enhanced";
    private Context mContext;
    private File mFingerprintImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String argString = getIntent().getStringExtra("options");
        JSONObject arg_object = null;
        String onyxLicense = null;
        String action = null;
        String imageTypeParam = null;
        try {
            arg_object = new JSONObject(argString);
            onyxLicense = arg_object.getString("onyxLicense");
            action = arg_object.getString("action");
            imageTypeParam = arg_object.getString("imageType");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (action.equals("image")) {
            String imageType = "preprocessed";
            if (imageTypeParam != null) {
                imageType = imageTypeParam;
            }
            // Create a file to hold the fingerprint image bitmap
            mFingerprintImageFile = new File(Environment.getExternalStorageDirectory(),
                    "FingerImage.jpg");
            File rawImageFile = null;
            File preprocessedImageFile = null;
            File enhancedImageFile = null;

            if (imageType.equalsIgnoreCase(ONYX_IMAGE_TYPE_RAW)) {
                rawImageFile = mFingerprintImageFile;
            } else if (imageType.equalsIgnoreCase(ONYX_IMAGE_TYPE_PREPROCESSED)) {
                preprocessedImageFile = mFingerprintImageFile;
            } else if (imageType.equalsIgnoreCase(ONYX_IMAGE_TYPE_ENHANCED)) {
                enhancedImageFile = mFingerprintImageFile;
            }

            Intent fingerImageIntent = FingerWizardIntentHelper.getFingerWizardIntent(
                    mContext, onyxLicense, false, false, CaptureConfiguration.Flip.NONE,
                    rawImageFile, enhancedImageFile, preprocessedImageFile, Bitmap.CompressFormat.JPEG);
            startActivityForResult(fingerImageIntent, RC_ONYX_IMAGE);
        } else if (action.equals("enroll")) {
            EnrollWizardBuilder ewb = new EnrollWizardBuilder();
            ewb.setLicenseKey(onyxLicense);
            ewb.setUseSelfEnroll(true);
            ewb.setUseOnyxGuide(true, true, false);
            Intent enrollWizardIntent = ewb.build(mContext);
            startActivityForResult(enrollWizardIntent, RC_ONYX_ENROLL);
        } else if (action.equals("template")) {
            Intent fingerImageIntent = FingerWizardIntentHelper.getFingerWizardIntent(
                    mContext, onyxLicense, false, false, CaptureConfiguration.Flip.NONE,
                    null, null, null, null);
            startActivityForResult(fingerImageIntent, RC_ONYX_TEMPLATE);
        } else if (action.equals("verify")) {
            if (fingerprintExists()) {
                Intent verifyIntent = VerifyIntentHelper.getVerifyActivityIntent(
                        mContext, onyxLicense);
                startActivityForResult(verifyIntent, RC_ONYX_VERIFY);
            } else {
                Toast.makeText(mContext, "No fingerprints enrolled.",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JSONObject result = new JSONObject();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_ONYX_IMAGE) {
                byte[] bytes = fileToArrayOfBytes(mFingerprintImageFile);
                String encodedBytes = Base64.encodeToString(bytes, 0);
                String imageUri = "data:image/jpeg;base64," + encodedBytes;
                try {
                    result.put("imageUri", imageUri);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to set JSON key value pair: " + e.getMessage());
                }
            } else if (requestCode == RC_ONYX_ENROLL || requestCode == RC_ONYX_TEMPLATE) {
                // Get the EnrollmentMetric
                EnrollmentMetric em = null;
                if (data != null && data.hasExtra(Consts.EXTRA_ENROLLMENT_METRIC)) {
                    em = (EnrollmentMetric) data.getSerializableExtra(
                            Consts.EXTRA_ENROLLMENT_METRIC);
                } else {
                    EnrolledFingerprintDetails fingerprintDetails = EnrolledFingerprintDetails
                            .getInstance();
                    if (fingerprintDetails.enrolledEnrollmentMetricExists(mContext)) {
                        em = fingerprintDetails.getEnrolledEnrollmentMetric(mContext);
                    }
                }
                // Get the finger location
                if (em != null) {
                    // If you want a fingerprint template for enrollment, and can be
                    // matched using Onyx, get it in the following manner
                    FingerprintTemplate ft = em.getFingerprintTemplateArray()[0];
                    if (null != ft) {
                        String bytesString = Base64.encodeToString(
                                ft.getData(),
                                Base64.URL_SAFE | Base64.NO_WRAP
                        ).trim();
                        int nfiqScore = ft.getNfiqScore();
                        try {
                            result.put("template", bytesString);
                            result.put("nfiqScore", nfiqScore);
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to set JSON key value pair: " + e.getMessage());
                        }
                    }
                }
            } else if (requestCode == RC_ONYX_VERIFY) {
                try {
                    result.put("isVerified", true);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to set JSON key value pair: " + e.getMessage());
                }
            }
        }
        OnyxPlugin.onFinished(resultCode, result);
        finish();
    }

    private byte[]  fileToArrayOfBytes (File file) {
        FileInputStream fileInputStream = null;

        byte[] bytes = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
    static File mEnrolledFile = null;

    private boolean fingerprintExists() {
        mEnrolledFile = getFileStreamPath(Consts.ENROLLED_ENROLLMENT_METRIC_FILENAME);
        if (mEnrolledFile.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
