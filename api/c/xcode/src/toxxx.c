/*************************************************************************/
/*                                                                       */
/* toxxx                                                                 */
/*                                                                       */
/* IDNA spec entry points ToUnicode & ToASCII along with some IDNA spec  */
/* domain label splitting utility routines.                              */
/*                                                                       */
/* (c) Verisign Inc., 2000-2002, All rights reserved                     */
/*                                                                       */
/*************************************************************************/
#include <idna_types.h>
#include <toxxx.h>
#include <normalize.h>
#include <puny.h>
#include <util.h>
#include <idna_protocol.h>
#include <init_lib.h>

XcodeBool Xcode_IsIDNADomainDelimiter(const UTF16CHAR * wp) {
    if (wp == 0)
        return 0;
    if (*wp == ULABEL_DELIMITER_LIST[0] || *wp == ULABEL_DELIMITER_LIST[1]
            || *wp == ULABEL_DELIMITER_LIST[2] || *wp
            == ULABEL_DELIMITER_LIST[3])
        return 1;
    return 0;
}

int Xcode_ToASCII_WithNormalizedOutput(const UTF16CHAR * puzInputString,
        int iInputSize, UCHAR8 * pzOutputString, int * piOutputSize,
        DWORD *dwzNormalizedString, int *iNormalizedSizePtr) {

    int i;
    int en;

    DWORD dwzOutputString[MAX_LABEL_SIZE_32];
    int iOutputSize = MAX_LABEL_SIZE_32;

    DWORD dwzConvertedString[MAX_LABEL_SIZE_32];
    int iConvertedSize = MAX_LABEL_SIZE_32;

    int iNormalizedSize;

    /* Basic input validity checks and buffer length checks */
    if (puzInputString == 0 || iInputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (pzOutputString == 0 || *piOutputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (iInputSize > MAX_LABEL_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    if (*piOutputSize < MAX_LABEL_SIZE_8)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    for (i = 0; i < iInputSize; i++) {
        dwzOutputString[i] = (DWORD) * (puzInputString + i);
    }
    iOutputSize = iInputSize;

    /* 1. If the UseSTD3ASCIIRules flag is set, then perform these checks:
     (a) Verify the absence of non-LDH ASCII code points; that is, the
     absence of 0..2C, 2E..2F, 3A..40, 5B..60, and 7B..7F.

     (b) Verify the absence of leading and trailing hyphen-minus; that
     is, the absence of U+002D at the beginning and end of the
     sequence. */
    /* UseSTD3ASCIIRules is a compile switch and is defined via xcode_config.h */

#ifdef UseSTD3ASCIIRules
    for (i = 0; i < iOutputSize; i++) {
        if (is_ldh_character32(*(dwzOutputString + i)) == XCODE_TRUE) {
            return XCODE_TOXXX_STD3_NONLDH;
        }
        if ((i == 0 && *(dwzOutputString + i) == LABEL_HYPHEN) ||
                (i == (iOutputSize - 1) && *(dwzOutputString + i) == LABEL_HYPHEN)) {
            return XCODE_TOXXX_STD3_HYPHENERROR;
        }
    }
#endif
    /* 2. If the sequence contains only characters in the ASCII range
     (0..7F), set a flag  to skip normalization. */
    en = 0;

    bool isNonASCII = false;
    for (i = 0; i < iOutputSize; i++) {

        if (*(dwzOutputString + i) < 0 || *(dwzOutputString + i) > 0x7F) {
            isNonASCII = true;

            /* 3. Verify that the sequence does NOT begin with the ACE prefix. */
            if ((ACE_PREFIX32[0] == *(dwzOutputString + 0)) && (ACE_PREFIX32[1]
                    == *(dwzOutputString + 1)) && (ACE_PREFIX32[2]
                    == *(dwzOutputString + 2)) && (ACE_PREFIX32[3]
                    == *(dwzOutputString + 3))) {
                return XCODE_TOXXX_ALREADYENCODED;
            }
        }
    }

    int retVal;

    /* 4. Get 32-bit representation of code points. */
    Xcode_convertUTF16To32Bit(puzInputString, iInputSize, dwzConvertedString,
            &iConvertedSize);

    /* 5. Normalize converted output of step 4. */
    if (isNonASCII) {
        retVal = Xcode_normalizeString(dwzConvertedString, iConvertedSize,
                dwzNormalizedString, iNormalizedSizePtr);

        if (retVal != XCODE_SUCCESS) {
            return retVal;
        }
    } else {
        memcpy(dwzNormalizedString, dwzConvertedString, iConvertedSize * 4);
        *iNormalizedSizePtr = iConvertedSize;
    }

    iNormalizedSize = *iNormalizedSizePtr;

    /* 6. Apply the IDNA2008 Protocol on normalized output of step 5. */
    retVal = idna2008Protocol(dwzNormalizedString, iNormalizedSize);

    if (retVal != XCODE_SUCCESS) {
        return retVal;
    }

    /* 7. Encode the sequence using the encoding algorithm in [PUNYCODE] and
     fail if there is an error. */
    /* 8. Prepend the ACE prefix. */
    retVal = Xcode_puny_encodeString(dwzNormalizedString, iNormalizedSize,
            pzOutputString, piOutputSize);

    if (retVal != XCODE_SUCCESS) {
        return retVal;
    }

    en = 1;

    if (en == 0) {
        for (i = 0; i < iOutputSize; i++) {
            pzOutputString[i] = (UCHAR8) * (dwzOutputString + i);
        }

        *piOutputSize = iOutputSize;
    }

    /* 9. Verify that the number of code points is in the range 1 to 63
     inclusive. */
    if (*piOutputSize < 1 || *piOutputSize > 63)
        return XCODE_TOXXX_INVALIDDNSLEN;

    return XCODE_SUCCESS;
}

/*********************************************************************************
 From: RFC 3490 - Internationalizing Domain Names in Applications (IDNA)
 4. Conversion operations
 An application converts a domain name put into an IDN-unaware slot or
 displayed to a user.  This section specifies the steps to perform in
 the conversion, and the ToASCII and ToUnicode operations.
 The input to ToASCII or ToUnicode is a single label that is a
 sequence of Unicode code points (remember that all ASCII code points
 are also Unicode code points).  If a domain name is represented using
 a character set other than Unicode or US-ASCII, it will first need to
 be transcoded to Unicode.
 Starting from a whole domain name, the steps that an application
 takes to do the conversions are:
 1) Decide whether the domain name is a "stored string" or a "query
 string" as described in [STRINGPREP].  If this conversion follows
 the "queries" rule from [STRINGPREP], set the flag called
 "AllowUnassigned".

 * This c library supports both through a compile switch. See xcode_config.h
 for more information.

 2) Split the domain name into individual labels as described in
 section 3.1.  The labels do not include the separator.
 3) For each label, decide whether or not to enforce the restrictions
 on ASCII characters in host names [STD3].  (Applications already
 faced this choice before the introduction of IDNA, and can
 continue to make the decision the same way they always have; IDNA
 makes no new recommendations regarding this choice.)  If the
 restrictions are to be enforced, set the flag called
 "UseSTD3ASCIIRules" for that label.
 4) Process each label with either the ToASCII or the ToUnicode
 operation as appropriate.  Typically, you use the ToASCII
 operation if you are about to put the name into an IDN-unaware
 slot, and you use the ToUnicode operation if you are displaying
 the name to a user; section 3.1 gives greater detail on the
 applicable requirements.
 5) If ToASCII was applied in step 4 and dots are used as label
 separators, change all the label separators to U+002E (full stop).
 **********************************************************************************/
/*********************************************************************************
 *
 * int ToASCII( const UTF16CHAR * puzInputString, int iInputSize,
 *              UCHAR8 * pzOutputString, int * piOutputSize )
 *
 *  Applies IDNA spec ToASCII operation on a domain label.
 *
 *  Returns XCODE_SUCCESS if successful, or an XCODE error constant on failure.
 *
 *  puzInputString  - [in] UTF16 input string.
 *  iInputSize      - [in] Length of incoming UTF16 string.
 *  pzOutputString  - [in,out] 8-bit output character string buffer.
 *  piOutputSize    - [in,out] Length of incoming 8-bit buffer, and contains
 *                    length of resulting encoded string on exit.
 *
 **********************************************************************************/

/*********************************************************************************
 The ToASCII operation takes a sequence of Unicode code points that
 make up one label and transforms it into a sequence of code points in
 the ASCII range (0..7F).  If ToASCII succeeds, the original sequence
 and the resulting sequence are equivalent labels.
 It is important to note that the ToASCII operation can fail.  ToASCII
 fails if any step of it fails.  If any step of the ToASCII operation
 fails on any label in a domain name, that domain name MUST NOT be
 used as an internationalized domain name.  The method for dealing
 with this failure is application-specific.
 The inputs to ToASCII are a sequence of code points, the
 AllowUnassigned flag, and the UseSTD3ASCIIRules flag.  The output of
 ToASCII is either a sequence of ASCII code points or a failure
 condition.
 ToASCII never alters a sequence of code points that are all in the
 ASCII range to begin with (although it could fail).  Applying the
 ToASCII operation multiple times has exactly the same effect as
 applying it just once.

 ToASCII consists of the following steps:

 1. If the UseSTD3ASCIIRules flag is set, then perform these checks:
 (a) Verify the absence of non-LDH ASCII code points; that is, the
 absence of 0..2C, 2E..2F, 3A..40, 5B..60, and 7B..7F.
 (b) Verify the absence of leading and trailing hyphen-minus; that
 is, the absence of U+002D at the beginning and end of the
 sequence.
 2. If the sequence contains any code points outside the ASCII range
 (0..7F) then proceed to step 3, otherwise skip to step 9.
 3. Verify that the sequence does NOT begin with the ACE prefix.
 4. Get 32-bit representation of code points.
 5. Normalize converted output of step 4.
 6. Apply the IDNA2008 Protocol on normalized output of step 5.
 7. Encode the sequence using the encoding algorithm in [PUNYCODE] and
 fail if there is an error.
 8. Prepend the ACE prefix.
 9. Verify that the number of code points is in the range 1 to 63
 inclusive.
 **********************************************************************************/
int Xcode_ToASCII(const UTF16CHAR * puzInputString, int iInputSize,
        UCHAR8 * pzOutputString, int * piOutputSize) {

    //initialize library - this function is an entry point
    initLib();

    DWORD dwzNormalizedLabel[MAX_LABEL_SIZE_32];
    int iNormalizedLabelSize = MAX_LABEL_SIZE_32;

    Xcode_ToASCII_WithNormalizedOutput(puzInputString, iInputSize,
            pzOutputString, piOutputSize, dwzNormalizedLabel,
            &iNormalizedLabelSize);

    return XCODE_SUCCESS;
}

/*********************************************************************************
 *
 * int ToUnicode( const UCHAR8 * pzInputString, int iInputSize,
 *                UTF16CHAR * puzOutputString, int * piOutputSize )
 *
 *  Applies IDNA spec ToUnicode operation on a domain label. Includes a
 *  good amount of commenting detail in .c on operation.
 *
 *  Returns XCODE_SUCCESS if call was successful. Sets piOutputSize to the width
 *  of the result.
 *
 *  pzInputString   - [in] 8-bit input string.
 *  iInputSize      - [in] Length of incoming 8-bit string.
 *  puzOutputString - [in,out] UTF16 output character string buffer.
 *  piOutputSize    - [in,out] Length of incoming UTF16 buffer, and contains
 *                    length of resulting decoded string on exit.
 *
 **********************************************************************************/

/*********************************************************************************
 The ToUnicode operation takes a sequence of Unicode code points that
 make up one label and returns a sequence of Unicode code points.  If
 the input sequence is a label in ACE form, then the result is an
 equivalent internationalized label that is not in ACE form, otherwise
 the original sequence is returned unaltered.
 ToUnicode never fails.  If any step fails, then the original input
 sequence is returned immediately in that step.
 The ToUnicode output never contains more code points than its input.
 Note that the number of octets needed to represent a sequence of code
 points depends on the particular character encoding used.
 The inputs to ToUnicode are a sequence of code points, the
 AllowUnassigned flag, and the UseSTD3ASCIIRules flag.  The output of
 ToUnicode is always a sequence of Unicode code points.

 1. Verify that the sequence begins with the ACE prefix, and save a
 copy of the sequence.
 2. Remove the ACE prefix.
 3. Decode the sequence using the decoding algorithm in [PUNYCODE]
 and fail if there is an error. Save a copy of the result of
 this step.
 4. Apply ToAscii.
 5. Verify that the result of step 4 matches the saved copy from
 step 1, using a case-insensitive ASCII comparison.
 6. Return the saved copy from step 3.
 7. Get 32-bit representation of decoded code points.
 8. Apply the IDNA2008 Protocol.
 9. Assert Normalization.
 **********************************************************************************/
int Xcode_ToUnicode8(const UCHAR8 * pzInputString, int iInputSize,
        UTF16CHAR * puzOutputString, int * piOutputSize) {

    //initialize library - this function is an entry point
    initLib();

    int retVal, i;
    XcodeBool bHigh = 0;

    UCHAR8 pzCopyString[MAX_LABEL_SIZE_8];
    int iCopySize;

    UTF16CHAR suzDecoded[MAX_LABEL_SIZE_16];
    int iDecodedSize = MAX_LABEL_SIZE_16;

    DWORD dwzConvertedString[MAX_LABEL_SIZE_32];
    int iConvertedSize = MAX_LABEL_SIZE_32;

    DWORD dwzNormalizedString[MAX_LABEL_SIZE_32];
    int iNormalizedSize = MAX_LABEL_SIZE_32;

    /* Basic input validity checks and buffer length checks */
    if (pzInputString == 0 || iInputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (puzOutputString == 0 || *piOutputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (iInputSize > MAX_LABEL_SIZE_8)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    if (*piOutputSize < MAX_LABEL_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    /* ToUnicode never fails.  If any step fails, then the original input
     sequence is returned immediately in that step. */

    for (i = 0; i < iInputSize; i++) {
        *(puzOutputString + i) = (UTF16CHAR) * (pzInputString + i);

        if (*(pzInputString + i) > 0x7F) {
            bHigh = 1;
        }
    }

    *piOutputSize = iInputSize;

    /*
        memcpy(pzCopyString, pzInputString, iInputSize);
        iCopySize = iInputSize;
     */

    if (!bHigh) {

        for (i = 0; i < iInputSize; i++) {
            pzCopyString[i] = (UCHAR8) * (pzInputString + i);
        }

        iCopySize = iInputSize;

        /* 1. Verify that the sequence begins with the ACE prefix, and save a
         copy of the sequence. */

        /* 2. Remove the ACE prefix. */
        /* 3. Decode the sequence using the decoding algorithm in [PUNYCODE]
         and fail if there is an error. Save a copy of the result of
         this step. */
        retVal = Xcode_puny_decodeString(pzCopyString, iCopySize, suzDecoded,
                &iDecodedSize);

        if (retVal != XCODE_SUCCESS) {
            return retVal;
        }
    } else {
        iDecodedSize = MAX_LABEL_SIZE_16;

        Xcode_convertUTF8ToUTF16(pzInputString,
                iInputSize,
                suzDecoded, iDecodedSize);
    }

    /* 6. Return the saved copy */
    memcpy(puzOutputString, suzDecoded, iDecodedSize * 2);
    *piOutputSize = iDecodedSize;

    /* 7. Get 32-bit representation of decoded code points. */
    Xcode_convertUTF16To32Bit(suzDecoded, iDecodedSize, dwzConvertedString,
            &iConvertedSize);

    /* 8. Apply the IDNA2008 Protocol. */
    retVal = idna2008Protocol(dwzConvertedString, iConvertedSize);

    if (retVal != XCODE_SUCCESS) {
        return retVal;
    }

    /* 9. Assert Normalization. */
    retVal = Xcode_normalizeString(dwzConvertedString, iConvertedSize,
            dwzNormalizedString, &iNormalizedSize);

    if (retVal != XCODE_SUCCESS) {
        return retVal;
    }

    if (memcmp(dwzConvertedString, dwzNormalizedString, iNormalizedSize) != 0) {
        return XCODE_NORMALIZE_NOT_IN_NFC_FORM;
    }

    return XCODE_SUCCESS;

}

int Xcode_ToUnicode16(const UTF16CHAR * puzInputString, int iInputSize,
        UTF16CHAR * puzOutputString, int * piOutputSize) {

    //initialize library - this function is an entry point
    initLib();

    int retVal, i;
    XcodeBool bHigh = 0;

    UCHAR8 pzCopyString[MAX_LABEL_SIZE_8];
    int iCopySize;

    UTF16CHAR suzDecoded[MAX_LABEL_SIZE_16];
    int iDecodedSize = MAX_LABEL_SIZE_16;

    DWORD dwzConvertedString[MAX_LABEL_SIZE_32];
    int iConvertedSize = MAX_LABEL_SIZE_32;

    DWORD dwzNormalizedString[MAX_LABEL_SIZE_32];
    int iNormalizedSize = MAX_LABEL_SIZE_32;

    /* Basic input validity checks and buffer length checks */
    if (puzInputString == 0 || iInputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (puzOutputString == 0 || *piOutputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (iInputSize > MAX_LABEL_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    if (*piOutputSize < MAX_LABEL_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    /* ToUnicode never fails.  If any step fails, then the original input
     sequence is returned immediately in that step. */
    for (i = 0; i < iInputSize; i++) {
        *(puzOutputString + i) = *(puzInputString + i);

        if (*(puzInputString + i) > 0x7F) {
            bHigh = 1;
        }
    }

    *piOutputSize = iInputSize;
    if (!bHigh) {

        for (i = 0; i < iInputSize; i++) {
            pzCopyString[i] = (UCHAR8) * (puzInputString + i);
        }

        iCopySize = iInputSize;

        /* 1. Verify that the sequence begins with the ACE prefix, and save a
         copy of the sequence. */

        /* 2. Remove the ACE prefix. */
        /* 3. Decode the sequence using the decoding algorithm in [PUNYCODE]
         and fail if there is an error. Save a copy of the result of
         this step. */
        retVal = Xcode_puny_decodeString(pzCopyString, iCopySize, suzDecoded,
                &iDecodedSize);

        if (retVal != XCODE_SUCCESS) {
            return retVal;
        }
    } else {
        iDecodedSize = iInputSize;
        memcpy(suzDecoded, puzInputString, iDecodedSize * 2);
    }

    /* 6. Return the saved copy */
    memcpy(puzOutputString, suzDecoded, iDecodedSize * 2);
    *piOutputSize = iDecodedSize;

    /* 7. Get 32-bit representation of decoded code points. */
    Xcode_convertUTF16To32Bit(suzDecoded, iDecodedSize, dwzConvertedString,
            &iConvertedSize);

    /* 8. Apply the IDNA2008 Protocol. */
    retVal = idna2008Protocol(dwzConvertedString, iConvertedSize);

    if (retVal != XCODE_SUCCESS) {
        return retVal;
    }

    /* 9. Assert Normalization. */
    retVal = Xcode_normalizeString(dwzConvertedString, iConvertedSize,
            dwzNormalizedString, &iNormalizedSize);

    if (retVal != XCODE_SUCCESS) {
        return retVal;
    }

    if (memcmp(dwzConvertedString, dwzNormalizedString, iNormalizedSize) != 0) {
        return XCODE_NORMALIZE_NOT_IN_NFC_FORM;
    }

    return XCODE_SUCCESS;
}

/*********************************************************************************
 *
 * int Xcode_DomainToASCII( const UTF16CHAR * puzInputString, int iInputSize,
 *              UCHAR8 * pzOutputString, int * piOutputSize )
 *
 *  Applies IDNA spec ToASCII operation on a domain.
 *
 *  Returns XCODE_SUCCESS if successful, or an XCODE error constant on failure.
 *
 *  puzInputString  - [in] UTF16 input string.
 *  iInputSize      - [in] Length of incoming UTF16 string.
 *  pzOutputString  - [in,out] 8-bit output character string buffer.
 *  piOutputSize    - [in,out] Length of incoming 8-bit buffer, and contains
 *                    length of resulting encoded string on exit.
 *
 **********************************************************************************/
int Xcode_DomainToASCII(const UTF16CHAR * puzInputString, int iInputSize,
        UCHAR8 * pzOutputString, int * piOutputSize) {

    //initialize library - this function is an entry point
    initLib();

    int i, j;
    UTF16CHAR suzLabel[MAX_LABEL_SIZE_16];
    UCHAR8 szDomain[MAX_DOMAIN_SIZE_8];
    UCHAR8 szLabel[MAX_LABEL_SIZE_8];

    DWORD dwzNormalizedLabel[MAX_LABEL_SIZE_32];
    int iNormalizedLabelSize = MAX_LABEL_SIZE_32;

    DWORD dwzNormalizedDomain[MAX_DOMAIN_SIZE_32];
    int iNormalizedDomainSize = MAX_DOMAIN_SIZE_32;

    int lindex = 0;
    int dindex = 0;
    int ndIndex = 0; //normalized domain running index
    int iOutputSize;
    int delimiterPresent;
    int retVal;

    /* Basic input validity checks */
    if (puzInputString == 0 || iInputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (pzOutputString == 0 || *piOutputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (iInputSize > MAX_DOMAIN_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    if (*piOutputSize < MAX_DOMAIN_SIZE_8)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    for (i = 0; i < iInputSize; i++) {
        delimiterPresent = 0;

        /* www.ml.ml.com */

        if (Xcode_IsIDNADomainDelimiter(puzInputString + i)) {
            delimiterPresent = 1;
        } else {
            if (lindex >= MAX_LABEL_SIZE_16) {
                return XCODE_BUFFER_OVERFLOW_ERROR;
            }

            suzLabel[lindex] = *(puzInputString + i);
            lindex++;
        }

        if (i == iInputSize - 1 || delimiterPresent == 1) {
            /* encode the label and save the result in domain */
            suzLabel[lindex] = 0;

            if (lindex == 0)
                goto skip;

            iOutputSize = MAX_LABEL_SIZE_8;

            if ((retVal = Xcode_ToASCII_WithNormalizedOutput(suzLabel, lindex,
                    szLabel, &iOutputSize, dwzNormalizedLabel,
                    &iNormalizedLabelSize)) != XCODE_SUCCESS)
                return retVal;

            if (dindex + iOutputSize > MAX_DOMAIN_SIZE_8)
                return XCODE_BUFFER_OVERFLOW_ERROR;

            memcpy(&szDomain[dindex], szLabel, iOutputSize);
            dindex = dindex + iOutputSize;
            lindex = 0;

            for (j = 0; j < iNormalizedLabelSize; j++) {
                dwzNormalizedDomain[ndIndex] = dwzNormalizedLabel[j];
                ndIndex++;
            }

skip:
            if (delimiterPresent == 1) {
                szDomain[dindex] = LABEL_DELIMITER;
                dindex++;

                dwzNormalizedDomain[ndIndex] = LABEL_DELIMITER;
                ndIndex++;
            }

            continue;
        }
    }

    memcpy(pzOutputString, szDomain, dindex);
    *piOutputSize = dindex;
    //return XCODE_SUCCESS;

    iNormalizedDomainSize = ndIndex;

    retVal = applyIdna2008BidiRules(dwzNormalizedDomain, ndIndex);

    return retVal;

}

/*********************************************************************************
 *
 * int DomainToUnicode( const UCHAR8 * pzInputString, int iInputSize,
 *                UTF16CHAR * puzOutputString, int * piOutputSize )
 *
 *  Applies IDNA 2008 protocol spec in ToUnicode operation on a domain label.
 *
 *  Returns XCODE_SUCCESS if call was successful. Sets piOutputSize to the width
 *  of the result.
 *
 *  pzInputString   - [in] 8-bit input string.
 *  iInputSize      - [in] Length of incoming 8-bit string.
 *  puzOutputString - [in,out] UTF16 output character string buffer.
 *  piOutputSize    - [in,out] Length of incoming UTF16 buffer, and contains
 *                    length of resulting decoded string on exit.
 *
 **********************************************************************************/
int Xcode_DomainToUnicode8(const UCHAR8 * pzInputString, int iInputSize,
        UTF16CHAR * puzOutputString, int * piOutputSize) {

    //initialize library - this function is an entry point
    initLib();

    int i;

    UTF16CHAR suzDomain[MAX_DOMAIN_SIZE_16];
    UCHAR8 szInLabel[MAX_LABEL_SIZE_8];
    UTF16CHAR suzOutLabel[MAX_LABEL_SIZE_16];

    DWORD dwzConvertedString[MAX_LABEL_SIZE_32];
    int iConvertedSize = MAX_LABEL_SIZE_32;

    int lindex = 0;
    int dindex = 0;
    int iOutputSize;
    int delimiterPresent;

    int retVal;

    /* Basic input validity checks and buffer length checks */
    if (pzInputString == 0 || iInputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (puzOutputString == 0 || *piOutputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (iInputSize > MAX_DOMAIN_SIZE_8)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    if (*piOutputSize < MAX_DOMAIN_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    for (i = 0; i < iInputSize; i++) {
        delimiterPresent = 0;

        /* www.encoded.com */
        if (*(pzInputString + i) == LABEL_DELIMITER) {
            delimiterPresent = 1;
        } else {
            if (lindex >= MAX_LABEL_SIZE_8)
                return XCODE_BUFFER_OVERFLOW_ERROR;
            szInLabel[lindex] = *(pzInputString + i);
            lindex++;
        }

        if (i == iInputSize - 1 || delimiterPresent == 1) {
            /* encode the label and save the result in domain */
            szInLabel[lindex] = 0;

            iOutputSize = MAX_LABEL_SIZE_16;
            retVal = Xcode_ToUnicode8(szInLabel, lindex, suzOutLabel, &iOutputSize);
            if (retVal == XCODE_SUCCESS) {
                if (dindex + iOutputSize > MAX_DOMAIN_SIZE_16)
                    return XCODE_BUFFER_OVERFLOW_ERROR;

                memcpy(&suzDomain[dindex], suzOutLabel, iOutputSize * 2);
                dindex = dindex + iOutputSize;
            } else {
                return retVal;
            }

            lindex = 0;

            if (delimiterPresent == 1) {
                suzDomain[dindex] = LABEL_DELIMITER;
                dindex++;
            }
            continue;
        }
    }

    memcpy(puzOutputString, suzDomain, dindex * 2);
    *piOutputSize = dindex;
    puzOutputString[dindex] = 0;

    if ((suzDomain != NULL) && (strcmp(suzDomain, puzOutputString) == 0)) {
        // apply bidi rules
        Xcode_convertUTF16To32Bit(puzOutputString, *piOutputSize,
                dwzConvertedString, &iConvertedSize);

        retVal = applyIdna2008BidiRules(dwzConvertedString, iConvertedSize);
    }

    return retVal;
}

int Xcode_DomainToUnicode16(const UTF16CHAR * puzInputString, int iInputSize,
        UTF16CHAR * puzOutputString, int * piOutputSize) {

    //initialize library - this function is an entry point
    initLib();

    int i;
    UTF16CHAR suzDomain[MAX_DOMAIN_SIZE_16];
    UTF16CHAR suzInLabel[MAX_LABEL_SIZE_8];
    UTF16CHAR suzOutLabel[MAX_LABEL_SIZE_16];

    DWORD dwzConvertedString[MAX_LABEL_SIZE_32];
    int iConvertedSize = MAX_LABEL_SIZE_32;

    int lindex = 0;
    int dindex = 0;
    int iOutputSize;
    int delimiterPresent;

    int retVal;

    /* Basic input validity checks and buffer length checks */
    if (puzInputString == 0 || iInputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (puzOutputString == 0 || *piOutputSize <= 0)
        return XCODE_BAD_ARGUMENT_ERROR;

    if (iInputSize > MAX_DOMAIN_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    if (*piOutputSize < MAX_DOMAIN_SIZE_16)
        return XCODE_BUFFER_OVERFLOW_ERROR;

    for (i = 0; i < iInputSize; i++) {
        delimiterPresent = 0;

        if (Xcode_IsIDNADomainDelimiter(puzInputString + i)) {
            delimiterPresent = 1;
        } else {
            if (lindex > MAX_LABEL_SIZE_8)
                return XCODE_BUFFER_OVERFLOW_ERROR;

            suzInLabel[lindex] = *(puzInputString + i);
            lindex++;
        }

        if (i == iInputSize - 1 || delimiterPresent == 1) {
            /* encode the label and save the result in domain */
            suzInLabel[lindex] = 0;

            iOutputSize = MAX_LABEL_SIZE_16;

            retVal = Xcode_ToUnicode16(suzInLabel, lindex, suzOutLabel,
                    &iOutputSize);
            if (retVal == XCODE_SUCCESS) {
                if (dindex + iOutputSize > MAX_DOMAIN_SIZE_16)
                    return XCODE_BUFFER_OVERFLOW_ERROR;

                memcpy(&suzDomain[dindex], suzOutLabel, iOutputSize * 2);
                dindex = dindex + iOutputSize;
            } else {
                return retVal;
            }

            lindex = 0;

            if (delimiterPresent == 1) {
                suzDomain[dindex] = LABEL_DELIMITER;
                dindex++;
            }
            continue;
        }
    }

    memcpy(puzOutputString, suzDomain, dindex * 2);
    *piOutputSize = dindex;
    puzOutputString[dindex] = 0;

    if ((suzDomain != NULL) && (strcmp(suzDomain, puzOutputString) == 0)) {
        // apply bidi rules
        Xcode_convertUTF16To32Bit(puzOutputString, *piOutputSize,
                dwzConvertedString, &iConvertedSize);

        retVal = applyIdna2008BidiRules(dwzConvertedString, iConvertedSize);
    }

    return retVal;
}
