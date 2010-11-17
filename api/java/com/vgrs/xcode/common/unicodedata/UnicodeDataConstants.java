/**************************************************************************
 *                                                                        *
 * The information in this document is proprietary to VeriSign, Inc.      *
 * It may not be used, reproduced or disclosed without the written        *
 * approval of VeriSign.                                                  *
 *                                                                        *
 * VERISIGN PROPRIETARY & CONFIDENTIAL INFORMATION                        *
 *                                                                        *
 *                                                                        *
 * Copyright (c) 2010 VeriSign, Inc.  All rights reserved.                *
 *                                                                        *
 *************************************************************************/

package com.vgrs.xcode.common.unicodedata;

/**
 * File containing constants used throughout the IDNSDK code base
 * 
 * @author nchigurupati
 * @version 1.0 Jun 10, 2010
 */
public class UnicodeDataConstants {
	/*
	 * Delimiters in data files
	 */

	public static final String SEMI_COLON_DELIMITER = ";";

	public static final String TAB_DELIMITER = " #\t";

	public static final String[] SEMI_COLON_TAB_DELIMITERS = new String[] {
			SEMI_COLON_DELIMITER, TAB_DELIMITER
	};

	public static final String DOT_DOT_DELIMITER = "..";

	/**
	 * Derived Normalization properties
	 */
	public static final String NFKC_CF = "NFKC_CF";

	public static final String NFC_QC = "NFC_QC";
	public static final String NFC_QC_N = "NFC_QC_N";

	public static final String NFC_QC_M = "NFC_QC_M";

	public static final String NFKC_QC = "NFKC_QC";
	public static final String NFKC_QC_N = "NFKC_QC_N";

	/**
	 * Normalized input or not
	 */
	public static final int CANNOT_BE_NORMALIZED = 0;
	public static final int NORMALIZED = 1;
	public static final int NORMALIZATION_NEEDED = 2;

	/**
	 * Joining type
	 */
	public static final char UNDEFINED_JOINING_TYPE = 'U';

	/*
	 * Commingle Filter variables
	 */
	public static final String COMMON_SCRIPT = "Common";
	public static final String UNKNOWN_SCRIPT = "Unknown";
	public static final String INHERITED_SCRIPT = "Inherited";
	public static final String GENERAL_CATEGORY_MC = "Mc";
	public static final String GENERAL_CATEGORY_ME = "Me";
	public static final String GENERAL_CATEGORY_MN = "Mn";

	/**
	 * Unicode Data Files
	 */
	public static final String UNICODE_DATA = "data/unicode/UnicodeData.txt.gz";

	public static final String COMPOSITION_EXCLUSIONS_DATA =
			"data/unicode/CompositionExclusions.txt.gz";

	public static final String SCRIPTS_DATA = "data/unicode/Scripts.txt.gz";

	public static final String PROP_LIST_DATA = "data/unicode/PropList.txt.gz";

	public static final String DERIVED_JOINING_TYPE_DATA =
			"data/unicode/DerivedJoiningType.txt.gz";

	public static final String DERIVED_NORMALIZATION_PROPS_DATA =
			"data/unicode/DerivedNormalizationProps.txt.gz";

	public static final String DERIVED_CORE_PROPERTIES_DATA =
			"data/unicode/DerivedCoreProperties.txt.gz";

	public static final String HANGUL_SYLLABLE_TYPE_DATA =
			"data/unicode/HangulSyllableType.txt.gz";

	public static final String BLOCKS_DATA = "data/unicode/Blocks.txt.gz";

	/**
	 * Constants used for while parsing Unicode data files.
	 */
	public static final String NONCHARACTER_CODE_POINT =
			"Noncharacter_Code_Point";

	public static final String WHITE_SPACE = "White_Space";

	public static final String DEFAULT_IGNORABLE_CODE_POINT =
			"Default_Ignorable_Code_Point";

	public static final String ANCIENT_GREEK_MUSICAL_NOTATION =
			"Ancient Greek Musical Notation";

	public static final String MUSICAL_SYMBOLS = "Musical Symbols";

	public static final String COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS =
			"Combining Diacritical Marks for Symbols";

	/**
	 * Hangul composition constants
	 */
	public static final int S_BASE = 0xAC00;

	public static final int L_BASE = 0x1100;

	public static final int V_BASE = 0x1161;

	public static final int T_BASE = 0x11A7;

	public static final int L_COUNT = 19;

	public static final int V_COUNT = 21;

	public static final int T_COUNT = 28;
}
