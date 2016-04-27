//
//  Onyx.h
//  OnyxDemo
//
//  Created by Devan Buggay on 6/11/14.
//  Copyright (c) 2014 Devan Buggay. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface OnyxMatch : NSObject

/*!
 * This function performs a 1:1 match on two fingerprint templates.
 * @author Will Lucas
 *
 * @param ft1 the first fingerprint template to verify with.
 * @param ft2 the first fingerprint template to verify with.
 * @return a value greater than or equal to 0.1 means the verification was successful.
 */
//+ (double) verify:(const FingerprintTemplate&) ft1 with:(const FingerprintTemplate&) ft2;

/*!
 * This function will attempt to match two templates and return a score.
 * @author Devan Buggay
 *
 * @param d1 first template data
 * @param d2 second template data
 * @return a match score [0, 1] 0.1 is acceptable. 
 */
+ (double) match:(NSData *)d1 with:(NSData *)d2;

/*!
 * Get FingerprintTemplate from NSData
 * @author Devan Buggay
 *
 * @param d NSData of template
 * @return a pointer to a FingerprintTemplate.
 */
//+ (FingerprintTemplate) fingerprintTemplateForData:(NSData *) d;

/*!
 * This NSData from FingerprintTemplate
 * @author Devan Buggay
 *
 * @param t a pointer to a FingerprintTemplate
 * @return a pointer to a NSData object
 */
//+ (NSData *) dataForFingerprintTemplate:(FingerprintTemplate) t;

/*!
 * Get onyx-core version
 * @author Devan Buggay
 *
 */
+ (NSString *) onyxcoreversion;

@end
