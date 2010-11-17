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
import gnu.trove.set.hash.TIntHashSet;

/**
 * Class containing the Arabic Indic digits and Extended Arabic Indic digits.
 * When this rule executes, all code points are evaluated.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public abstract class AbstractArabicIndicDigitsCodePoints extends
		AbstractAllCodePointsEvaluated {

	/**
	 * Set of Arabic Indic digits for quick comparision.
	 */
	private static final TIntSet ARABIC_INDIC_DIGITS = new TIntHashSet();

	/**
	 * Set of Extended Arabic Indic digits for quick comparision.
	 */
	private static final TIntSet EXTENDED_ARABIC_INDIC_DIGITS = new TIntHashSet();

	static {
		init();
	}


	/**
	 * Initialize the arabic indic digits and extended arabic indic digits
	 * collections.
	 */
	private static void init () {
		// List of Arabic Indic Digits
		final int[] ARABIC_INDIC_DIGITS_ARR = {
				0x660, 0x661, 0x662, 0x663, 0x664, 0x665, 0x666, 0x667, 0x668, 0x669
		};
		for ( final int cps : ARABIC_INDIC_DIGITS_ARR ) {
			AbstractArabicIndicDigitsCodePoints.ARABIC_INDIC_DIGITS.add( cps );
		}

		// List of Arabic Indic Digits
		final int[] EXTENDED_ARABIC_INDIC_DIGITS_ARR = {
				0x6F0, 0x6F1, 0x6F2, 0x6F3, 0x6F4, 0x6F5, 0x6F6, 0x6F7, 0x6F8, 0x6F9
		};
		for ( final int cps : EXTENDED_ARABIC_INDIC_DIGITS_ARR ) {
			AbstractArabicIndicDigitsCodePoints.EXTENDED_ARABIC_INDIC_DIGITS
					.add( cps );
		}
	}


	/**
	 * @return Set of Arabic Indic digits
	 */
	protected TIntSet getArabicIndicDigits () {
		return AbstractArabicIndicDigitsCodePoints.ARABIC_INDIC_DIGITS;
	}


	/**
	 * @return Set of Extended Arabic Indic digits
	 */
	protected TIntSet getExtendedArabicIndicDigits () {
		return AbstractArabicIndicDigitsCodePoints.EXTENDED_ARABIC_INDIC_DIGITS;
	}

}
