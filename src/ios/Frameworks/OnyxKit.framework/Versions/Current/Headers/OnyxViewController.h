//
//  OnyxViewController.h
//  OnyxKit
//
//  Created by Devan Buggay on 6/16/14.
//  Copyright (c) 2014 dft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>

#define OnyxBlue [UIColor colorWithRed:(float)(54.0/255.0) green:(float)(152.0/255.0) blue:(float)(211.0/255.0) alpha:1.0]
#define fingerStepSize 15
#define fingerStepPadding 10

#define QUALITY_THRESHOLD 6

#define __deprecated    __attribute__((deprecated))

/*!
 * @typedef onyxStates
 * @brief List of states OnyxViewController can be in
 */
typedef enum onyxStates {
    ONYX_UNKNOWN,
    ONYX_SINGLE,
    ONYX_ENROLL
} OnyxState;

/*!
 * @typedef onyxDirections
 * @brief List of directions guide can be in
 */
typedef enum onyxDirections {
    ONYX_CENTER,
    ONYX_LEFT,
    ONYX_RIGHT
} OnyxDirection;

/*!
 * @typedef onyxFingers
 * @brief List of fingers. 1 - Thumb, 2 - Pointer, 5 - Pinky
 */
typedef enum onyxFingers {
    left_1,
    left_2,
    left_3,
    left_4,
    left_5,
    right_1,
    right_2,
    right_3,
    right_4,
    right_5
} OnyxFinger;

/*!
 * @typedef onyxInternalState
 * @brief List of fingers. 1 - Thumb, 2 - Pointer, 5 - Pinky
 */
typedef enum onyxInternalState {
    FINGER_SELECTION,
    TUTORIAL,
    CATPURE_SCREEN
} OnyxInternalState;


@class ProcessedFingerprint;
@class FingerGuideView;
@class StepIndicatorView;


/*!
 @class OnyxViewController
 @abstract View controller housing the Onyx camera tech
 */
@interface OnyxViewController : UIViewController <AVCaptureVideoDataOutputSampleBufferDelegate, UIGestureRecognizerDelegate> {

    // AVFoundation
    AVCaptureDevice *inputDevice;
    AVCaptureSession *captureSession;
    AVCaptureVideoPreviewLayer *captureVideoPreviewLayer;
    AVCaptureStillImageOutput *stillImageOutput;
    AVCaptureVideoDataOutput *captureOutput;
    dispatch_queue_t captureQueue;
    
    ProcessedFingerprint *pf;
    NSMutableArray *prints;
    
    CGRect camFrame;
    NSInteger stableFrames;
    NSInteger neededStableFrames;
    NSInteger stage;
    NSInteger failures;
    
    
    BOOL complete;
    BOOL matched;
    BOOL capturePreview;
    bool fingerSelected;
    bool cameraBool;
    
    NSArray *steps;
    NSMutableArray *stepIndicatorViews;
    
    FingerGuideView *fingerGuideView;
    UIView *fingertipView;
    UIView *imagePreview;
    UIView *fingerSelect;
    UIImage *fingerImage;
    UIImage *gifImage;
    UIImage *brandImage;
    UIImage *leftInfo;
    UIImage *rightInfo;
    UIImageView *infoView;
    UIImageView *sidebarView;
    
    
    
    UIPageViewController *pageViewController;
    NSArray *pageTitles;
    NSArray *pageImages;
    
    UIView *flashBackground;
    UIView *flash;
    UIActivityIndicatorView *activityIndicatorView;
}

/*!
 * @brief The OnyxViewController's delegate
 */
//@property (weak, nonatomic) id delegate;
@property (strong, nonatomic) id delegate;

/*!
 * @brief The OnyxViewController's internal state
 */
@property int state;

/*!
 * @brief The number of enroll captures to take [default: 3]
 */
@property int enrollCount;

/*!
 * @brief The OnyxViewController's brand image (Not implemented)
 */
@property UIImage *brand;

/*!
 * @brief The OnyxViewController's license key
 */
@property NSString *license;

/*!
 * @brief Option to hide hand selection [default: false]
 */
@property bool hideHandSelect;

/*!
 * @brief The internal finger direction
 */
@property NSInteger fingerDirection;

/*!
 * @brief The internal finger
 */
@property NSInteger selectedFinger;

/*!
 * @brief Option to reverse hand selection
 */
@property bool reverseHand;

/*!
 * @brief The onyx-core's version number
 */
@property (readonly) NSString *onyxcoreversion;

/*!
 * @brief Option to show tutorial page [default: false]
 */
@property bool showTutorial;

/*!
 * @brief Option to show match score at end of enrollment [default: false]
 */
@property bool showMatchScore;

/*!
 * @brief Option to change LED brightness for camera
 */
@property float LEDBrightness; // (0.0, 1.0]

/*!
 * @brief Option 
 */
@property bool useAutoFocus;

/*!
 * @brief Boolean to toggle on/off flashing during capture.
 */
@property bool useFlash;

@property NSArray *scaleFactors;

@property UILabel *infoLabel1;
@property UILabel *infoLabel2;

/*!
 * @brief Text for a custom label in the info view.
 */
@property NSString *infoText;

/*!
 * @brief Boolean for showing debug info on screen
 */
@property bool showDebug;

@property float focusMeasurementRequirement;

@property int frameCount;

@property float thresholdValue;

/*!
 * Show tutorial modal
 * @author Devan Buggay
 *
 * @return void
 */
- (void)showTutorialModal;

/*!
 * Set up left hand guide view
 * @author Devan Buggay
 *
 * @param sender
 * @return void
 */
- (void)leftHandSelect:(id) sender;

/*!
 * Set up right hand guide view
 * @author Devan Buggay
 *
 * @param sender
 * @return void
 */
- (void)rightHandSelect:(id) sender;

/*!
 * Set capturePreview to true
 * @author Devan Buggay
 *
 * @return void
 */
- (void)capture;

/*!
 * Set up AVFoundation if inputDevice exists
 * @author Devan Buggay
 *
 * @return void
 */
- (void)setupAVFoundation;

/*!
 * Capture a still image from input device
 * @author Devan Buggay
 *
 * @return void
 */
- (void)captureStill;

/*!
 * Reset the focus and exposure of the camera. If iOS 8+, lock focus.
 * @author Devan Buggay
 *
 * @return void
 */
- (void)resetFocusAndExposure;
/*!
 * Start the process indicator
 * @author Devan Buggay
 *
 * @return void
 */
- (void)startProcessIndicator;
/*!
 * Stop the process indicator
 * @author Devan Buggay
 *
 * @return void
 */
- (void)stopProcessIndicator;

@end

#pragma mark - OnyxViewControllerDelegate

// OnyxViewController Delegate methods
@protocol OnyxViewControllerDelegate

@optional


/*!
 * Output processed fingerprint
 * @author Devan Buggay
 *
 * @param UIImage
 */
- (void) Onyx:(OnyxViewController *)controller didOutputProcessedFingerprint:(ProcessedFingerprint *)fingerprint fromSet:(NSArray *)fingerprints;

/*!
 * Output raw image on completion.
 * @author Devan Buggay
 *
 * @param UIImage
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputRaw:(UIImage *)image __deprecated;
/*!
 * Output processed image on completion.
 * @author Devan Buggay
 *
 * @param UIImage
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputProcessed:(UIImage *)image __deprecated;
/*!
 * Output enhanced image on completion.
 * @author Devan Buggay
 *
 * @param UIImage
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputEnhanced:(UIImage *)image __deprecated;
/*!
 * Output WSQ on completion.
 * @author Devan Buggay
 *
 * @param UIImage
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputWSQ:(NSData *)data __deprecated;
/*!
 * Output mirrored and flipped WSQ on completion.
 * @author Devan Buggay
 *
 * @param data
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputMirroredInvertedWSQ:(NSData *)data __deprecated;
/*!
 * Output mirrored and flipped image on completion.
 * @author Devan Buggay
 *
 * @param image
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputMirroredInverted:(UIImage *)image __deprecated;
/*!
 * Output raw image on completion.
 * @author Devan Buggay
 *
 * @param data
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputTemplate:(NSData *)data __deprecated;
/*!
 * Output finger enum on completion.
 * @author Devan Buggay
 *
 * @param finger
 * @deprecated This delegate method is deprecated
 */
- (void) Onyx:(OnyxViewController *)controller didOutputFinger:(NSInteger)finger __deprecated;
/*!
 * View controller did complete.
 * @author Devan Buggay
 *
 */
- (void) OnyxDidComplete:(OnyxViewController *)controller;


@end
