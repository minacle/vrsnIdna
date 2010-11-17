/*
 * idna_data_constants.h
 *
 *  Created on: Jul 12, 2010
 *      Author: prsrinivasan
 */

#ifndef IDNA_DATA_CONSTANTS_H_
#define IDNA_DATA_CONSTANTS_H_

/*
 * base(radix) defs
 */
#define DECIMAL_BASE	10
#define HEX_BASE	16

/*
 * Limits
 */
#define DEFAULT_MALLOC_SIZE				16
#define MAX_TOKENS						64
#define DISALLOWED_UNASSIGNED_MAX_SIZE	1024
#define	LINE_MAX_SIZE						4096

/*
 * Unicode Limits
 */
#define UNICODE_MIN						0
#define UNICODE_MAX						0x10ffff

/*
 * Delimiters, prefixes and suffixes
 */
#define SPACE_DELIMITER  		" "
#define SEMI_COLON_DELIMITER  	";"
#define TAB_DELIMITER  			" #\t"
#define DOT_DOT_DELIMITER  		".."
#define PIPE_DELIMITER  		"|"
#define HYPHEN_DELIMITER		"-"

/*
 * Special chars
 */
#define HYPHEN		'-'

#define UNICODE_RANGE_BEGIN_SUFFIX	", First>"
#define UNICODE_RANGE_END_SUFFIX	", Last>"

/**
 * Derived Normalization properties
 */
#define NFKC_CF  	"NFKC_CF"
#define NFC_QC  	"NFC_QC"
#define NFC_QC_N  	"NFC_QC_N"
#define NFC_QC_M  	"NFC_QC_M"
#define NFKC_QC  	"NFKC_QC"
#define NFKC_QC_N  	"NFKC_QC_N"

#define UNDEFINED_JOINING_TYPE  'U'

/*
 * Commingle Filter variables
 */
#define COMMON_SCRIPT  		"Common"
#define UNKNOWN_SCRIPT  	"Unknown"
#define INHERITED_SCRIPT  	"Inherited"
#define GENERAL_CATEGORY_MC "Mc"
#define GENERAL_CATEGORY_ME "Me"
#define GENERAL_CATEGORY_MN "Mn"

/**
 * Constants used for while parsing Unicode data files.
 */
#define NONCHARACTER_CODE_POINT						"Noncharacter_Code_Point"
#define WHITE_SPACE  								"White_Space"
#define DEFAULT_IGNORABLE_CODE_POINT				"Default_Ignorable_Code_Point"
#define ANCIENT_GREEK_MUSICAL_NOTATION				"Ancient Greek Musical Notation"
#define MUSICAL_SYMBOLS  							"Musical Symbols"
#define COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS		"Combining Diacritical Marks for Symbols"

/**
 * Hangul composition constants
 */
#define S_BASE  0xAC00
#define L_BASE  0x1100
#define V_BASE  0x1161
#define T_BASE  0x11A7
#define L_COUNT  19
#define V_COUNT  21
#define T_COUNT  28

#endif /* IDNA_DATA_CONSTANTS_H_ */
