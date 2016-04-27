//
//  StepIndicatorView.h
//  OnyxDemo
//
//  Created by Devan Buggay on 5/16/14.
//  Copyright (c) 2014 Devan Buggay. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface StepIndicatorView : UIView

@property (nonatomic,strong) UIColor *color;

@property bool complete;
@property float amount;

@end
