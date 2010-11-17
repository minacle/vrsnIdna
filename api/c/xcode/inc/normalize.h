/*************************************************************************/
/*                                                                       */
/* normalize                                                              */
/*                                                                       */
/* Routines which handle normalize                                       */
/*                                                                       */
/* (c) Verisign Inc., 2000-2003, All rights reserved                     */
/*                                                                       */
/*************************************************************************/

#ifndef __normalize_h__
#define __normalize_h__

#include "xcode.h"

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

/*************************************************************************/
/*                                                                       */
/* <Error Codes>                                                         */
/*                                                                       */
/*  Library return constants. Return codes are within the interval       */
/*  0 - 999. Error values returned indicate the type of logic associated */
/*  with a particular error. Each component of the library has it's own  */
/*  block of error constants defined in the following header files:      */
/*                                                                       */
/*  Errors <= 20 - Indicates common library error.                       */
/*  Errors > 20  - Indicates a module specific error.                    */
/*                                                                       */
/*************************************************************************/

/* normalize specific */

#define XCODE_NORMALIZE_EXPANSIONERROR              0+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_PROHIBITEDCHAR              1+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_NULL_CHARACTER_PRESENT      2+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_FIRSTLAST_BIDIERROR         3+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_MIXED_BIDIERROR             4+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_BAD_ARGUMENT_ERROR          5+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_MEMORY_ALLOCATION_ERROR     6+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_BUFFER_OVERFLOW_ERROR       7+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_MAPPEDOUT                   8+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_OUTOFRANGEERROR             9+NORMALIZE_SPECIFIC
#define XCODE_NORMALIZE_NOT_IN_NFC_FORM             10+NORMALIZE_SPECIFIC

/*************************************************************************/
/*                                                                       */
/* <Function>                                                            */
/*                                                                       */
/*  Xcode_normalizeString                                                */
/*                                                                       */
/* <Description>                                                         */
/*                                                                       */
/*  Applies Normalization Form KC (NFKC) to an input string.             */
/*                                                                       */
/*  Returns XCODE_SUCCESS if successful, or an XCODE error constant      */
/*  on failure.                                                          */
/*                                                                       */
/* <Parameters>                                                          */
/*                                                                       */
/*  pdwzInputString  - [in] 32-bit input string.                         */
/*                                                                       */
/*  iInputSize       - [in] Length of input string.                      */
/*                                                                       */
/*  pdwzOutputString - [in,out] 32-bit normalized output string.         */
/*                                                                       */
/*  piOutputSize     - [in,out] On input, contains length of 32 bit      */
/*                     buffer. On output. set to length of output string.*/
/*                                                                       */
XCODE_EXPORTEDAPI
int Xcode_normalizeString(DWORD * pdwzInputString, int iInputSize,
		DWORD * pdwzOutputString, int * piOutputSize);
/*                                                                       */
/*************************************************************************/

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif	/* __normalize_h__ */

