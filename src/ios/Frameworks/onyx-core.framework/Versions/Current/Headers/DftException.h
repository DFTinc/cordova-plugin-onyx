/// \file DftException.h
/// \copyright Copyright 2014 Diamond Fortress Technologies, Inc. All rights reserved.

#ifndef DFTEXCEPTION_H_
#define DFTEXCEPTION_H_

#ifdef _MSC_VER
#pragma warning(push)
#pragma warning(disable:4275)
#endif

#include <stdexcept>
#include <string>

namespace dft
{

/// \brief This class is the base exception class for the onyx-core SDK.
class DftException : public std::runtime_error
{
public:
	/// \brief This contructs a new DftException.
	/// \see [std::runtime_error](http://www.cplusplus.com/reference/stdexcept/runtime_error/) for inherited methods.
	DftException(const std::string& msg = "") : runtime_error(msg) {}
};

}

#ifdef _MSC_VER
#pragma warning(pop)
#endif

#endif
