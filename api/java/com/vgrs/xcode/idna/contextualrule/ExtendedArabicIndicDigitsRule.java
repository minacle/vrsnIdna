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

import gnu.trove.set.TIntSet;

import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * <ul>
 * <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.9">Extended Arabic
 * Indic Digits Contextual Rule</a>
 * <li>Code Point : U+06F0..U+06F9
 * <li>Rule : If cp .in. U+0660..U+0669 Then False;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class ExtendedArabicIndicDigitsRule extends
		AbstractArabicIndicDigitsCodePoints {

	/**
	 * Tests if the given code point is one of the Extended Arabic Indic digits
	 * code point.
	 */
	@Override
	public boolean ruleAppliesToCodepoint ( int aCodePoint ) {
		final TIntSet extendedArabicIndicDigits =
				super.getExtendedArabicIndicDigits();
		return extendedArabicIndicDigits.contains( aCodePoint );
	}


	/**
	 * If an Extended Arabic Indic digit is found, then no Arabic Indic Digits may
	 * be present.
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {

		final TIntSet arabicIndicDigits = super.getArabicIndicDigits();
		for ( final int codePoint : aCodePoints ) {
			if ( arabicIndicDigits.contains( codePoint ) ) {
				throw XcodeError
						.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
								+ ": Arabic Indic digits exist when Extended Arabic Indic digits are present. Offending code point is "
								+ Integer.toHexString( codePoint ).toUpperCase() );
			}
		}
	}
}
