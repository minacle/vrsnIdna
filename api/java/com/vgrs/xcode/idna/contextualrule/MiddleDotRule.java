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

import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * <ul>
 ** <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.3"> Middle Dot
 * Contextual Rule</a>
 * <li>Code Point : U+00B7
 * <li>Rule : If Before(cp) .eq. U+006C And After(cp) .eq. U+006C Then True;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class MiddleDotRule extends AbstractAllCodePointsNotEvaluated {

	/**
	 * Middle dot code point
	 */
	private static final int MIDDLE_DOT = 0x00B7;

	/**
	 * Latin small letter 'L' code point
	 */
	private static final int LATIN_SMALL_LETTER_L = 0x006C;


	/**
	 * Constructor specifies that this rule can only execute if the Middle dot
	 * code point is present in the given set of code points.
	 */
	public MiddleDotRule () {
		super.setCodePointToWhichRuleApplies( MIDDLE_DOT );
	}


	/**
	 * Checks to see if the code points before and after the middle dot (U+00B7)
	 * are Latin small letter 'L' (U+006C)
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {

		final int length = aCodePoints.length;
		final int beforeCpIndex = aCodePointIndex - 1;

		if ( beforeCpIndex < 0 ) {
			throw XcodeError.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
					+ ": Index of code point before MIDDLE_DOT 0x00B7 is < 0" );
		}

		final int afterCpIndex = aCodePointIndex + 1;
		if ( afterCpIndex > length - 1 ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
							+ ": Index of code point after MIDDLE_DOT 0x00B7 is > length of array" );
		}

		if ( aCodePoints[ beforeCpIndex ] != LATIN_SMALL_LETTER_L
				|| aCodePoints[ afterCpIndex ] != LATIN_SMALL_LETTER_L ) {
			throw XcodeError.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
					+ ": Code point before/after MIDDLE DOT (0x00B7) "
					+ "!= LATIN SMALL LETTER L (0x006C)" );
		}
	}
}
