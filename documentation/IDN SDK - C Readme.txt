******************************************************************
******************************************************************
VeriSign IDNA XCode (encode/decode) ANSI c Library

A library for encoding / decoding of IDN domains and labels.

(c) VeriSign Inc., 2002-2011, All rights reserved
******************************************************************
******************************************************************

  Contents:

  I.    License
  II.   Introduction
  III.  General Overview
  IV.   Functional Entry Points
  V.    Standard Types
  VI.   Error and Success Constants
  VII.  Constants
  VIII. Compile Configuration
  IX.   Normalization and static data
  X.    Example Use


I. License


The VeriSign XCode library is licensed under the BSD open source
license, and as such is suitable for use in commercial applications.
In the Windows environment, the Idn Sdk C language library requires
the Cygwin runtime which is distributed under the GPL license.
Other dependencies, as described in the User's Guide, come with
their own license agreements.


II. Introduction


Xcode is an IDNA compliant encoding and decoding library used to 
support the International Domain Names (IDNA) standard in applications. 
XCode is primarily targeted at client application development - the 
library is small, compact, and includes make targets for building both 
static and dynmaic libraries on a number of platforms.

For in-depth information on the current IDNA standard, and for
information on how IDN's should be handled at the application
level, see IDNA RFCs 5890,5891,5892,5893 and 5894. 


III. General Overview


The library has a single set of IDNA compliant routines used
in encoding and decoding domains and domain labels (ToXXX). 
Direct access to each nameprep and encode/decode step is also
made available.
  
In most cases The ToXXX domain label routines ToASCII and ToUnicode, 
or the domain processing routines DomainToASCII and DomainToUnicode 
are the only routines application developers will need to implement 
IDNA in their application. 

The following is a breakdown of the different areas of the library:

  ToXXX (toxxx.c/.h) - IDNA spec domain label & domain processing 
  routines.

  Punycode (puny.c/.h) - Routines which handle Punycode encoding and 
  decoding of domain lables.

  Samples & Conformance - Various command line based applications 
  and sample code showing how to use this library.

The distribution directory strucuture is as follows:

 api
   c
     xcode            - core library source base.
       src               - .c source files.
       inc               - .h include directory.
     test             - testing and conformance applications.    


IV. Functional Entry Points


In general application developers should use the primary entry
points in processing domain labels or domains. Using primary 
entry points insures IDNA conformance in the application. Auxillary 
entry points are primarily used in testing. Each entry point
is documented in it's header.

The following entry points are provided by the library. See the 
header files for specific information on each.  


Primary Entry Points:

--------------------------------------------------------------------
int Xcode_ToASCII( const UTF16CHAR *  puzInputString, 
                   int                iInputSize,
                   UCHAR8 *           pzOutputString, 
                   int *              piOutputSize );

Routine for encoding a domain label. Input is UTF16 and output is 
8-bit ASCII.

Example Input:  enténial
Example Output: xn--entnial-dya
--------------------------------------------------------------------


--------------------------------------------------------------------
int Xcode_DomainToASCII( const UTF16CHAR *  puzInputString, 
                         int                iInputSize,
                         UCHAR8 *           pzOutputString, 
                         int *              piOutputSize );

Routine for encoding a domain which may contain unicode codepoints. 
Breaks up a domain by known domain label delimiters and subsequently 
hands each label to ToASCII. Reassembles encoded labels into a domain 
using '.' as the delimiter between each encoded label. Input is UTF16 
and output is 8-bit ASCII. Valid ASCII domains which do not contain 
unicode codepoints are mapped to the output.

Example Input:  www.enténial.com
Example Output: www.xn--entnial-dya.com
--------------------------------------------------------------------


--------------------------------------------------------------------
int Xcode_ToUnicode8( const UCHAR8 *    pzInputString, 
                      int                iInputSize,
                      UTF16CHAR *        puzOutputString, 
                      int *              piOutputSize );

int Xcode_ToUnicode16( const UTF16CHAR * puzInputString, 
                       int               iInputSize,
                       UTF16CHAR *       puzOutputString, 
                       int *             piOutputSize );

Routines which decode encoded domain labels. ToUnicode8 takes
8-bit character string as input, ToUnicode16 takes 16 bit UTF16
character string as input. Output is returned in UTF16.

Example Input:  xn--entnial-dya
Example Output: enténial
--------------------------------------------------------------------


----------------------------------------------------------------------
int Xcode_DomainToUnicode8( const UCHAR8 *     pzInputString, 
                            int                iInputSize,
                            UTF16CHAR *        puzOutputString, 
                            int *              piOutputSize );

int Xcode_DomainToUnicode16( const UTF16CHAR *  puzInputString, 
                             int                iInputSize,
                             UTF16CHAR *        puzOutputString, 
                             int *              piOutputSize );

Routines which decode encoded domains.

Example Input:  www.xn--entnial-dya.com
Example Output: www.enténial.com

Example Input:  www\u3002xn--entnial-dya\uFF0Ecom
Example Output: www.enténial.com

Example 8-bit Input: www.xn--j1aimx.com
Example UTF16 Input: www\u3002xn--\uFF4A1aimx\uFF0Ecom
Example Output:      www.043a\u043e\u0448\u0442.com
----------------------------------------------------------------------


----------------------------------------------------------------------
int Xcode_convertUTF16To32Bit( const UTF16CHAR *  puInput, 
                               int                iInputLength, 
                               DWORD *            pdwResult, 
                               int *              piResultLength );

int Xcode_convert32BitToUTF16( const DWORD *  pdwzInput, 
                               int            iInputLength, 
                               UTF16CHAR *    puzResult, 
                               int *          piResultLength );

int Xcode_convertUTF16ToUTF8( const UTF16CHAR * puzInput,
                              int               iInputLength,
                              UCHAR8 *          pszResult,
                              int *             piResultLength );

int Xcode_convertUTF8ToUTF16( const UCHAR8 *    pszInput,
                              int               iInputLength,
                              UTF16CHAR *       puzResult,
                              int *             piResultLength );


Conversion rotuines used in converting 32-bit/UTF16/UTF8.
----------------------------------------------------------------------


V. Standard Types


The library defines a set of standard types which are used in 
entry point declarations. Standard types are typedef'd in the
xcode_config.h header file. The following types are defined:

UCHAR8    - 8-bit unsigned character (unsigned char *)
UTF16CHAR - UTF16 (16-bit) encoded character (unsigned short int)
DWORD     - 32-bit unsigned character or codepoint (unsigned long *)
QWORD     - (Internal use only) 64-bit unsigned codepoint pair (unsigned int64)
XcodeBool - Boolean flag (1/0)


VI. Error and Success Constants


All entry points return an integer value indicating success or 
failure. Success is always indicated by a return value of 0, or
developers can also use the XCode constant XCODE_SUCCESS. Error
constants are defined in the following headers within the 
library:

xcode.h     		- [1 - 99] General error constants.

puny.h      		- [100-199] Ace specific error constants.

normalize.h      	- [200-299] Normalize specific error constants.

toxxx.h     		- [300-399] Toxxx routine specific constants.

util.h      		- [400-499] Utility specific error constants.

idna_protocol.h		- [500-599] IDNA 2008 Protocol specific error constants.

contextual_rules.h	- [600-699] Contextual Rules specific error constants.

bidi_rules.h		- [700-799] Bidirectional Rules specific error constants.

When an entry point returns a value greater than 0, one of these 
headers will describe the error.


VII. Constants


XCode supports various different configurations and constants. All 
configuration options are specified through a common configuration 
include file named "xcode_config.h". The following constants are 
defined:

  ACE_PREFIX

  The IDNA Ace label prefix.

  MAX_LABEL_SIZE_XX, MAX_DOMAIN_SIZE_XX

  The maximum size of 32, 16, & 8 bit strings to be passed into the 
  library's routines. Used internally to define input / output 
  buffer  sizes as well. Input string lengths are checked against 
  these constants prior to processing. All incoming result buffers 
  must meet these minimum widths as well or an XCODE_BUFFER_OVERFLOW_ERROR
  error will be returned.

  static const UTF16CHAR ULABEL_DELIMITER_LIST[4]

  The IDNA approved domain delimiters. See xcode_config.h for more 
  information.


VIII. Compile Configuration


  UseSTD3ASCIIRules   (default on) 

  Apply STD3 domain format rules in ToASCII and ToUnicode as specified 
  in IDNA.


  AllowUnassigned     (default on) 

  Optionally allow unassigned unicode codepoints per IDNA in Query     
  string processing. Client applications predominately deal with       
  "stored strings", therefore this compile switch is turned off by     
  default.                                                             
                                                                      

  IDNA_DEBUG_ON (default off)

  Turn debug output on for more detailed error messages.



IX. Normalization and static data


Before encoding a domain label, normalization must be 
applied, as described in http://www.unicode.org/unicode/reports/tr15/. 
Static data required for this and other IDNA processes is stored in 
lookup tables. Various raw data files which contain this data 
are specified in the RFC's associated with IDNA. XCode accesses this 
data through a set of hash-tables loaded upon initialization of the library. 



X. Example use

1) Encoding a domain:

  int res;

  /* unicode or UTF16 input string */

  /* ex: www.enténial.com */

  UTF16CHAR uInput[] = { 0x0077, 0x0077, 0x0077, 0x002E, 0x0066, 
                         0x00FC, 0x006E, 0x0066, 0x0064, 0x3002, 
                         0x006E, 0x0065, 0x0074 };
  int iInputSize = 13;

  /* ASCII output buffer */

  UCHAR8 szOutput[MAX_DOMAIN_SIZE_8];

  int iOutputSize = sizeof(szOutput);

  res = Xcode_DomainToASCII( uInput, iInputSize, szOutput, &iOutputSize );

  if ( res != XCODE_SUCCESS ) 
  {
    /* Error */
  }

  printf( szOutput );

2) Decoding a domain:

  int res;

  /* UTF16 output buffer */

  UTF16CHAR uOutput[MAX_DOMAIN_SIZE_16];

  char * szIn = "www.xn--weingut-schnberger-n3b.net";

  int iInputSize = strlen(szIn);
  int iOutputSize = sizeof(uOutput);

  res = Xcode_DomainToUnicode8( szIn, iInputSize, uOutput, &iOutputSize );

  if ( res != XCODE_SUCCESS ) 
  {
    /* Error */
  }


Application developers may wish to break a domain up into labels
manually outside of this library. According to IDNA, applications
must recognize all domain delimiters defined in xcode_config.h.

1) Encoding a label:

  int res;

  UTF16CHAR uInput[] = { 0x0070, 0x00E4, 0x00E4, 0x006F, 0x006D, 0x0061 };

  int iInputSize = 6;

  UCHAR8 szOutput[1204];

  int iOutputSize = sizeof(szOutput);

  /* ex: enténial */

  res = Xcode_ToASCII( uInput, iInputSize, szOutput, &iOutputSize );

  if ( res != XCODE_SUCCESS ) 
  {
    /* Error */
  }

  printf( szOutput );

2) Decoding a label:

  int res;

  /* UTF16 output buffer */

  UTF16CHAR uOutput[MAX_LABEL_SIZE_16];

  char * szIn = "xn--weingut-schnberger-n3b";

  int iInputSize = strlen(szIn);
  int iOutputSize = sizeof(uOutput);

  res = Xcode_ToUnicode8( szIn, iInputSize, uOutput, &iOutputSize );

  if ( res != XCODE_SUCCESS ) 
  {
    /* Error */
  }
