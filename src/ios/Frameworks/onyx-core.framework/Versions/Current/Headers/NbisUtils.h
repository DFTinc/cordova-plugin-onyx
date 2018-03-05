#ifndef NBISUTILS_H_
#define NBISUTILS_H_

#include <opencv2/core/core.hpp>
#include <vector>

namespace dft
{

std::vector<unsigned char> matToWsq(
		const cv::Mat& image,
		const std::string& comment = "Diamond Fortress Technologies, Inc.",
		int ppi = 500,
		float bitrate = 2.25f
);

cv::Mat wsqToMat(const std::vector<unsigned char>& wsqData, int& ppi);

std::vector<cv::Mat> pyramidImage(const cv::Mat& image, const std::vector<double>& scales);

struct NfiqMetrics
{
	int nfiqScore;
	float mlpScore;

	NfiqMetrics() : nfiqScore(-1), mlpScore(-1.0f) {}
};

NfiqMetrics computeNfiq(const cv::Mat& image, int ppi = 500, int opts = 0);

}

#endif
