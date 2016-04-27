#ifndef STFT_H
#define STFT_H

#include <vector>
#include <opencv2/core/core.hpp>
#include <stdint.h>

namespace dft
{

cv::Mat raisedCosine2d(uint32_t blockSize, uint32_t overlap);
cv::Mat raisedCosine1d(uint32_t blockSize, uint32_t overlap);

float computeMeanFrequency(const cv::Mat& energy, const cv::Mat& rho);
float computeMeanAngle(const cv::Mat& energy, const cv::Mat& theta);

std::vector<cv::Mat> createAngularFilterBank(float bandwidth, int fftSize, int angularSteps);
cv::Mat computeAngularLobe(const cv::Mat& theta, float angle, float bandwidth);
cv::Mat computeAngularDistance(const cv::Mat& theta, float angle);

cv::Mat smoothOrientationMap(const cv::Mat& theta, double sigma);
cv::Mat computeCoherenceMap(const cv::Mat& orientationMap, int ksize);
float computeCoherence(const cv::Mat& block, float theta);

cv::Mat computeBandwidthMap(const cv::Mat& coherenceMap);
cv::Mat lookupAngularFilter(float theta, float bandwidth, const std::vector<cv::Mat>& bankPi4, const std::vector<cv::Mat>& bankPi2);

void stftEnhance(const cv::Mat& src, cv::Mat& orientationMap, cv::Mat& frequencyMap, cv::Mat& energyMap, cv::Mat& enhancedImage);

}

#endif
