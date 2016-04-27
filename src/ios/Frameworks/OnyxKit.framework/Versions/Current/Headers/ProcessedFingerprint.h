//
//  ProcessedFingerprint.h
//  OnyxDemo
//
//  Created by Devan Buggay on 6/12/14.
//  Copyright (c) 2014 Devan Buggay. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


/*!
 @class ProcessedFingerprint
 @abstract Object returned from OnyxViewController that holds all data on fingerprint.
 */
@interface ProcessedFingerprint : NSObject
/*!
 * @brief The source image.
 */
@property UIImage *sourceImage;
/*!
 * @brief The processed image.
 */
@property UIImage *processedImage;
/*!
 * @brief The enhanced image.
 */
@property UIImage *enhancedImage;
/*!
 * @brief The inverted and y-axis flipped processed image.
 */
@property UIImage *invertedMirroredProcessedImage;
/*!
 * @brief The fingerprint template, to be used with Onyx matchers.
 */
@property NSData *fingerprintTemplate;
/*!
 * @brief The WSQ data.
 */
@property NSData *WSQ;
/*!
 * @brief The inverted and mirrored WSQ data.
 */
@property NSData *invertedMirroredWSQ;
/*!
 * @brief Black and white image of the processed image.
 */
@property UIImage *blackWhiteProcessed;
/*!
 * @brief The quality score. Any score over 15 is acceptable.
 */
@property float quality;
/*!
 * @brief The focus measure score. [0, 1] 0.1 is acceptable.
 */
@property float focusMeasure;
/*!
 * @brief The nfiqscore of the WSQ file.
 */
@property int nfiqscore;
/*!
 * @brief The mlscore of the WSQ file.
 */
@property float mlpscore;
/*!
 * @brief The direction of the finger.
 * Images will be flipped upright beforehand.
 */
@property NSInteger fingerDirection;
/*!
 * @brief The finger number.
 */
@property NSInteger finger;
/*!
 * @brief image size for all returned images.
 */
@property CGSize size;

@end
