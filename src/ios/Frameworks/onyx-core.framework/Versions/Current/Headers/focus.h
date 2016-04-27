#ifndef FOCUS_H_
#define FOCUS_H_

#include "dft/Finger.h"
#include <opencv2/core/core.hpp>

namespace dft
{

double focusMeasureImpl(const cv::Mat& src, const Finger& finger);

}

#endif
