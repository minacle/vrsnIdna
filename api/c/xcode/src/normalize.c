/*************************************************************************/
/*                                                                       */
/* normalize                                                              */
/*                                                                       */
/* Routines which handle normalization                                     */
/*                                                                       */
/* (c) Verisign Inc., 2000-2003, All rights reserved                     */
/*                                                                       */
/*************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <xcode.h>
#include <normalize.h>
#include <util.h>
#include <data_lookups.h>

/* Hangul composition and decomposition constants. */

static const int SBase = 0xAC00;
static const int LBase = 0x1100;
static const int VBase = 0x1161;
static const int TBase = 0x11A7;
static const int LCount = 19;
static const int VCount = 21;
static const int TCount = 28;
static const int NCount = 588; /* VCount * TCount */
static const int SCount = 11172; /* LCount * NCount */

/********************************************************************************
 *                             C O M M O N                                      *
 ********************************************************************************/

/*********************************************************************************
 *
 * void insert( DWORD * target, size_t * length, size_t offset, DWORD ch )
 *
 *  Perform string insert.
 *
 *  target - The string into which the character will be inserted
 *  length - The length in characters of the target string
 *  offset - The index at which the new character goes
 *  ch     - The character to insert
 *
 **********************************************************************************/

static void insert(DWORD * target, size_t * length, size_t offset, DWORD ch) {
	int i;
	if (offset < 0 || offset > *length) {
		return;
	}

	for (i = (*length); i > offset; i--) {
		target[i] = target[i - 1];
	}

	target[offset] = ch;

	(*length)++;
}

/*********************************************************************************
 *
 * int dwstrlen( const DWORD * pdwBuf )
 *
 *  32 bit character version of strlen.
 *
 *  Returns length of the string.
 *
 *  pdwBuf - Pointer to the 32-bit string.
 *
 **********************************************************************************/

static int dwstrlen(const DWORD * pdwBuf) {
	int index = 0;
	while (*(pdwBuf + index) != 0) {
		index++;
		if (index > 10000)
			return 0;
	}
	return index;
}

/*********************************************************************************
 *
 * int dwstrncat( DWORD * pdwBuf, DWORD * pdwAdd, int len )
 *
 *  32 bit character version of strcat.
 *
 *  Returns length of the string.
 *
 *  pdwBuf - Pointer to the 32-bit string.
 *  pdwAdd - Pointer to the 32-bit string to concatinate.
 *  len    - Length of pdwAdd.
 *
 **********************************************************************************/

static int dwstrncat(DWORD * pdwBuf, DWORD * pdwAdd, int len) {
	int index = 0;
	while (*(pdwBuf + index) != 0) {
		index++;
		if (index > 10000)
			return 0;
	}

	memcpy(pdwBuf + index, pdwAdd, len * sizeof(DWORD));

	return index;
}

/*********************************************************************************
 *                           N O R M A L I Z E                                   *
 *********************************************************************************/

/*********************************************************************************
 *
 * int composeHangul( DWORD lastCh, DWORD ch, DWORD * out )
 *
 *  Perform hangul composition on hangul characters found in a string and return
 *  the result.
 *
 *  Returns 1 if a hangul LV or LVT set was found and converted, 0 otherwise.
 *
 *  dwLastCh - The previous character in the string.
 *  ch       - The target character.
 *  pdwOut   - Pointer to resulting LV or LVT compose set.
 *
 **********************************************************************************/

static int composeHangul(DWORD dwLastCh, DWORD ch, DWORD * pdwOut) {
	int SIndex;
	int LIndex;

	/* 1. check to see if two current characters are L and V */

	LIndex = dwLastCh - LBase;

	if (0 <= LIndex && LIndex < LCount) {
		int VIndex = ch - VBase;
		if (0 <= VIndex && VIndex < VCount) {

			/* make syllable of form LV */

			*pdwOut = (DWORD)(SBase + (LIndex * VCount + VIndex) * TCount);

			return 1;
		}
	}

	/* 2. check to see if two current characters are LV and T */

	SIndex = dwLastCh - SBase;

	if (0 <= SIndex && SIndex < SCount && (SIndex % TCount) == 0) {
		int TIndex = ch - TBase;
		if (0 <= TIndex && TIndex <= TCount) {

			/* make syllable of form LVT */

			*pdwOut = dwLastCh + TIndex;
			return 1;
		}
	}

	return 0;
}

/*********************************************************************************
 *
 * int decomposeHangul( DWORD ch, DWORD * pdwOut )
 *
 *  Perform hangul decmposition on a character if it applies.
 *
 *  Returns 1 if a hangul LV or LVT set was found and converted, 0 otherwise.
 *
 *  ch -     The unicode character to be decomposed.
 *  pdwOut - The resulting string buffer if decomposition was performed. Incoming
 *           buffer should be of length 5 characters or more.
 *
 **********************************************************************************/

static int decomposeHangul(DWORD ch, DWORD * pdwOut) {
	int SIndex = ch - SBase;
	int L;
	int V;
	int T;

	if (SIndex < 0 || SIndex >= SCount) {
		return 0;
	}

	L = LBase + SIndex / NCount;
	V = VBase + (SIndex % NCount) / TCount;
	T = TBase + SIndex % TCount;

	*pdwOut = (DWORD) L;
	pdwOut++;
	*pdwOut = (DWORD) V;
	pdwOut++;

	*pdwOut = 0;

	if (T != TBase) {
		*pdwOut = (DWORD) T;
		pdwOut++;
	}

	*pdwOut = 0;

	return 1;
}

/*********************************************************************************
 *
 * void doDecomposition( int iRecursionCheck, int fCanonical,
 *                       DWORD ch, DWORD* outbuf )
 *
 *  Recursive decomposition for one character.
 *
 *  fCanonical - Boolean to indicate if only doing NFC (always 0)
 *  ch         - The unicode character to be decomposed
 *  outbuf     - The decomposed string
 *
 **********************************************************************************/

static void doDecomposition(int iRecursionCheck, int fCanonical, DWORD ch,
		DWORD * outbuf) {
	const DWORD* pDecomposeString;
	int i, len;
	int iDecompResult;
	DWORD iCompatValue, iCompatResult;

	if (iRecursionCheck > 20)
		return;

	{
		/* Q: there will never be a compatible entry for hangul characters?
		 Q: Is recursion of resulting hangul string is needed?? We recurse to be sure anyway. */
		DWORD hangubuf[5];

		if (decomposeHangul(ch, hangubuf)) {

			len = dwstrlen(hangubuf);

			for (i = 0; i < len; i++) {

				doDecomposition(iRecursionCheck + 1, fCanonical, hangubuf[i],
						outbuf);
			}
			return;
		}
	}

	iDecompResult = lookup_decompose(ch, &pDecomposeString);

	iCompatResult = lookup_compatible(ch);

	iCompatValue = ch;

	if (iDecompResult && !(fCanonical && iCompatResult)) {

		len = dwstrlen(pDecomposeString);

		for (i = 0; i < len; i++) {
			doDecomposition(iRecursionCheck + 1, fCanonical,
					pDecomposeString[i], outbuf);
		}

	} else {
		dwstrncat(outbuf, &ch, 1);
	}

}

/*********************************************************************************
 *
 * int doKCDecompose( const DWORD* input, size_t input_size,
 *                    DWORD* output, size_t* output_size )
 *
 *  Applies form KC decompositionon a string.
 *
 *  input               - Input utf16 to be decomposed
 *  input_size          - allocated size for input
 *  output              - Already decomposed utf16 to be recomposed
 *  output_size         - allocated size for output
 *
 **********************************************************************************/

static int doKCDecompose(const DWORD * input, int input_size, DWORD * output,
		int * output_size) {

	int i, j;
	size_t cursor;
	DWORD buf[80];
	int buflen;
	DWORD ch;
	int cClass;
	size_t output_offset = 0;
	int nCanonicalItem;

	for (i = 0; i < input_size; i++) {

		if (input[i] == 0x0000) {
			return XCODE_NORMALIZE_NULL_CHARACTER_PRESENT;
		}

		memset(buf, '\0', sizeof(buf));

		doDecomposition(0, 0, input[i], buf);

		buflen = dwstrlen(buf);

		for (j = 0; j < buflen; j++) {
			ch = buf[j];
			cClass = lookup_canonical(ch);
			cursor = output_offset;

			if (cClass != 0) {
				for (; cursor > 0; --cursor) {

					nCanonicalItem = lookup_canonical(output[cursor - 1]);
					if (nCanonicalItem <= cClass) {
						break;
					}
				}
			}

			insert(output, &output_offset, cursor, ch);
		}
	}

	if (output_offset > *output_size) {
		return XCODE_NORMALIZE_BUFFER_OVERFLOW_ERROR;
	}

	*output_size = output_offset;

	return XCODE_SUCCESS;
}

/*********************************************************************************
 *
 * int doKCCompose( DWORD* output, size_t* output_size )
 *
 *  Applies form KC recomposition on a string.
 *
 *  output              - Already decomposed utf16 to be recomposed
 *  output_size         - allocated size for output
 *
 **********************************************************************************/

static int doKCCompose(DWORD * output, int * output_size) {
	DWORD startCh;
	QWORD qwPair;
	int lastClass;
	int decompPos;
	int startPos = 0;
	int compPos = 1;
	DWORD nComposeResult, nComposeItem;

	startCh = output[0];

	lastClass = lookup_canonical(startCh);

	if (lastClass != 0) {
		lastClass = 256;
	}

	/* walk across the string */

	for (decompPos = 1; decompPos < *output_size; decompPos++) {
		int chClass;
		int composite;
		DWORD ch = output[decompPos];

		chClass = lookup_canonical(ch);

		nComposeResult = composeHangul(startCh, ch, &nComposeItem);

		/* if not Hangul, check our lookup table */
		if (!nComposeResult) {
			qwPair = startCh;
			qwPair = qwPair << 32;
			qwPair = qwPair | ch;
			nComposeResult = lookup_composite(qwPair, &nComposeItem);
		}

		if (!nComposeResult) {
			composite = 0xffff; /* 65535 */
		} else {
			composite = nComposeItem;
		}

		if (composite != 0xffff && (lastClass < chClass || lastClass == 0)) {

			output[startPos] = (DWORD) composite;
			startCh = (DWORD) composite;

		} else {

			if (chClass == 0) {
				startPos = compPos;
				startCh = ch;
			}

			lastClass = chClass;
			output[compPos++] = ch;
		}
	}

	if (compPos > *output_size) {
		return XCODE_NORMALIZE_BUFFER_OVERFLOW_ERROR;
	}

	*output_size = compPos;

	return XCODE_SUCCESS;
}

/*********************************************************************************
 *
 * int Xcode_normalizeString( DWORD *  pdwInputString, int iInputSize,
 *                            DWORD *  pdwOutputString, int * piOutputSize )
 *
 *  Applies Normalization Form KC (NFKC) to an input string. Called by
 *  Xcode_nameprepString below.
 *
 *  Returns XCODE_SUCCESS if successful, or an XCODE error constant on failure.
 *
 *  pdwInputString  - 32-bit input string
 *  iInputSize      - length of input string
 *  pdwOutputString - 32 bit character mapped output string
 *  piOutputSize    - length of output string
 *
 **********************************************************************************/

int Xcode_normalizeString(DWORD * pdwInputString, int iInputSize,
		DWORD * pdwOutputString, int * piOutputSize) {
	int return_code;
	int i;
	DWORD dwzTemp[256];
	int iTempSize = 256;

	if (iInputSize < 1)
		return XCODE_NORMALIZE_BAD_ARGUMENT_ERROR;

	/* decompose */

	return_code
			= doKCDecompose(pdwInputString, iInputSize, dwzTemp, &iTempSize);

	if (return_code != XCODE_SUCCESS)
		return return_code;

	/* compose */

	return_code = doKCCompose(dwzTemp, &iTempSize);

	if (return_code != XCODE_SUCCESS)
		return return_code;

	/* copy output */

	for (i = 0; i < iTempSize; i++) {
		pdwOutputString[i] = dwzTemp[i];
	}

	/* terminate the string */

	pdwOutputString[i] = 0;
	*piOutputSize = i;

	return XCODE_SUCCESS;
}

