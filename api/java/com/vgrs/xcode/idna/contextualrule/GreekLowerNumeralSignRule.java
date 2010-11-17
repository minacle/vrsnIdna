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
 * <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.4">Greek Lower
 * Numeral Sign (Keraia) Contextual Rule</a>
 * <li>Code Point : U+0375
 * <li>Rule : If Script(After(cp)) .eq. Greek Then True;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class GreekLowerNumeralSignRule extends
		AbstractAllCodePointsNotEvaluated {

	/**
	 * This contextual rule only executes if the Greek Lower Number Sign code
	 * point is present in the given set of code points.
	 */
	private static final int GREEK_LOWER_NUMERAL_SIGN = 0x0375;

	/**
	 * Constant for the Greek script as defined in Scripts.txt by Unicode.
	 */
	private static final String GREEK_SCRIPT = "Greek";


	/**
	 * Constructor specifies that this rule can only execute if the Greek Lower
	 * Number Sign code point is present in the given set of code points.
	 */
	public GreekLowerNumeralSignRule () {
		super.setCodePointToWhichRuleApplies( GREEK_LOWER_NUMERAL_SIGN );
	}


	/**
	 * Checks if the script of the code point after the Greek Lower Number Sign
	 * code point is Greek.
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {
		final int length = aCodePoints.length;
		final int afterCpIndex = aCodePointIndex + 1;
		if ( afterCpIndex > length - 1 ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
							+ ": Index of code point after GREEK_LOWER_NUMERAL_SIGN 0x0375 is > length of array" );
		}

		final String script = UnicodeData.getScript( aCodePoints[ afterCpIndex ] );
		if ( !script.equals( GREEK_SCRIPT ) ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
							+ ": Code point after GREEK_LOWER_NUMERAL_SIGN 0x0375 does not belong to the "
							+ GREEK_SCRIPT
							+ " script. Offending code point is "
							+ Integer
									.toHexString( aCodePoints[ afterCpIndex ] )
									.toUpperCase() );
		}
	}
}
