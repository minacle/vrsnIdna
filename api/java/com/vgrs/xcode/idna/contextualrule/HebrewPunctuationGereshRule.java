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

package com.vgrs.xcode.idna.contextualrule;

import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * <ul>
 * <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.5">Hebrew
 * Punctuation Geresh Contextual Rule</a>
 * <li>Code Point : U+05F3
 * <li>Rule : If Script(Before(cp)) .eq. Hebrew Then True;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class HebrewPunctuationGereshRule extends
		AbstractAllCodePointsNotEvaluated {

	/**
	 * This contextual rule only executes if the Hebrew punctuation Geresh code
	 * point is present in the given set of code points.
	 */
	private static final int HEBREW_PUNCTUATION_GERESH = 0x05F3;

	/**
	 * Constant for the Hebrew script as defined in Scripts.txt by Unicode.
	 */
	private static final String HEBREW_SCRIPT = "Hebrew";


	/**
	 * Constructor specifies that this rule can only execute if the Hebrew
	 * punctuation Geresh code point is present in the given set of code points.
	 */
	public HebrewPunctuationGereshRule () {
		super.setCodePointToWhichRuleApplies( HEBREW_PUNCTUATION_GERESH );
	}


	/**
	 * Checks if the script before the Hebrew punctuation Geresh code point is
	 * Hebrew.
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {
		final int beforeCpIndex = aCodePointIndex - 1;
		if ( beforeCpIndex < 0 ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
							+ ": Index of code point before HEBREW_PUNCTUATION_GERESH 0x05F3 is < 0" );
		}

		final String script = UnicodeData.getScript( aCodePoints[ beforeCpIndex ] );
		if ( !script.equals( HEBREW_SCRIPT ) ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
							+ ": Code point before HEBREW_PUNCTUATION_GERESH 0x05F3 does not belong to the "
							+ HEBREW_SCRIPT
							+ " script. Offending code point is "
							+ Integer
									.toHexString( aCodePoints[ beforeCpIndex ] )
									.toUpperCase() );
		}

	}

}
