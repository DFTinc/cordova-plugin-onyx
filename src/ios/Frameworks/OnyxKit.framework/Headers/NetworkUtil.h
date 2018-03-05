//
//  NetworkUtil.h
//  OnyxKit
//
//  Created by Matthew Wheatley on 8/20/15.
//  Copyright (c) 2015 Diamond Fortress. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NetworkReachability.h"

@interface NetworkUtil : NSObject

/*!
 * Returns boolean for internet connectivity
 * @auther Matthew Wheatley
 */
+ (BOOL) isInternetAvailable;

@end
