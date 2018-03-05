//
//  OnyxLicense.h
//  onyx
//
//  Created by Devan Buggay on 3/28/14.
//  Copyright (c) 2014 Devan Buggay. All rights reserved.
//

#import <Foundation/Foundation.h>
//#import "NetworkReachability.h"
#import "NetworkUtil.h"

#import "DeviceUID.h"

//#include <onyx-core/License.h>

@interface OnyxLicense : NSObject {
    NSString *baseurl;
    NSString *deviceKey;
    NSString *deviceUDID;
    NSInteger licenseType;
    
    //Type 0 and 1 license
    NSInteger validateThreshold;
    
    //Type 0 license
    NSDate *lastValidateDate;
    
    void (^_validateCompletionHandler)(NSError* error);
}

@property NSInteger usageCount;
@property bool isKeySet;
@property bool isLicenseTypeSet;
@property bool hasRecievedResponse;

@property NetworkReachability* internetReachable;
@property NetworkReachability* hostReachable;

@property BOOL internetActive;
@property BOOL hostActive;
@property (nonatomic) BOOL isValid;

+(OnyxLicense *)sharedInstance;

-(void)setKey:(NSString *)key;
-(void)loadDefaults;
-(void)saveDefaults;
-(void)increaseUsageCount;
-(void)validate:(void(^)(NSError *))handler;
-(void)validateWithKey:(NSString *) key;
-(void)recievedValidateJSON:(NSData *) data;
-(void)recievedValidateJSONError:(NSError *) error;
-(BOOL)isInternetAvailable;

@end
