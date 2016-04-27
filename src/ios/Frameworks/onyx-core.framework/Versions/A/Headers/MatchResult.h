/*
 * MatchResult.h
 *
 *  Created on: Jul 22, 2014
 *      Author: wlucas
 */

#ifndef MATCHRESULT_H_
#define MATCHRESULT_H_

namespace dft
{

/// This structure defines the data captured during an identification.
struct MatchResult
{
	int index; //!< Stores the top matching index
	float score; //!< Stores the top matching score

	MatchResult()
		: index(-1), score(0.0f)
	{}

	MatchResult(int index, float score)
		: index(index), score(score)
	{}
};

}

#endif /* MATCHRESULT_H_ */
