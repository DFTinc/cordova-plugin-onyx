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

#import </Users/chatcher/Documents/xcode projects/onyx-onyx-kit-ios/OnyxKit/Licensing/DeviceUID.h>

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
    

    
}

@property NSInteger usageCount;
@property bool isKeySet;
@property bool isLicenseTypeSet;
@property bool hasRecievedResponse;

@property NetworkReachability* internetReachable;
@property NetworkReachability* hostReachable;

@property BOOL internetActive;
@property BOOL hostActive;

+(OnyxLicense *)sharedInstance;

-(void)setKey:(NSString *)key;

-(void)loadDefaults;
-(void)saveDefaults;

-(void)increaseUsageCount;

-(void)validate;
-(bool)validateWithKey:(NSString *) key;
-(bool)recievedValidateJSON:(NSData *) data;
-(bool)recievedValidateJSONError:(NSError *) error;
-(bool) checkValidity;

//-(BOOL) checkNetworkStatus;

@property (nonatomic) BOOL isValid;

@end
