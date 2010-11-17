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

package com.vgrs.xcode.idna;

import java.util.HashSet;
import java.util.Set;

import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class implements the IDNA2008 BIDI rules. The BIDI class associated with
 * each of the Unicode code points is retrieved from {@link UnicodeData}
 * <code>getBidiClass()</code> method to ensure compliance with the IDNA 2008
 * BIDI rules.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class Bidi {

	/*
	 * Various BIDI class constants
	 */
	public static final String BIDI_L = "L";
	public static final String BIDI_R = "R";
	public static final String BIDI_AL = "AL";

	public static final String BIDI_AN = "AN";
	public static final String BIDI_EN = "EN";
	public static final String BIDI_ES = "ES";
	public static final String BIDI_CS = "CS";
	public static final String BIDI_ET = "ET";
	public static final String BIDI_ON = "ON";
	public static final String BIDI_BN = "BN";
	public static final String BIDI_NSM = "NSM";

	private static final Set<String> RTL_LABEL = new HashSet<String>();
	static {
		RTL_LABEL.add( BIDI_R );
		RTL_LABEL.add( BIDI_AL );
		RTL_LABEL.add( BIDI_AN );
	}

	private static final Set<String> RTL_BIDI_PROPERTIES = new HashSet<String>();
	static {
		RTL_BIDI_PROPERTIES.add( BIDI_R );
		RTL_BIDI_PROPERTIES.add( BIDI_AL );
		RTL_BIDI_PROPERTIES.add( BIDI_AN );
		RTL_BIDI_PROPERTIES.add( BIDI_EN );
		RTL_BIDI_PROPERTIES.add( BIDI_ES );
		RTL_BIDI_PROPERTIES.add( BIDI_CS );
		RTL_BIDI_PROPERTIES.add( BIDI_ET );
		RTL_BIDI_PROPERTIES.add( BIDI_ON );
		RTL_BIDI_PROPERTIES.add( BIDI_BN );
		RTL_BIDI_PROPERTIES.add( BIDI_NSM );
	}

	private static final Set<String> LTR_BIDI_PROPERTIES = new HashSet<String>();
	static {
		LTR_BIDI_PROPERTIES.add( BIDI_L );
		LTR_BIDI_PROPERTIES.add( BIDI_EN );
		LTR_BIDI_PROPERTIES.add( BIDI_ES );
		LTR_BIDI_PROPERTIES.add( BIDI_CS );
		LTR_BIDI_PROPERTIES.add( BIDI_ET );
		LTR_BIDI_PROPERTIES.add( BIDI_ON );
		LTR_BIDI_PROPERTIES.add( BIDI_BN );
		LTR_BIDI_PROPERTIES.add( BIDI_NSM );
	}


	/**
	 * Asserts that the given Unicode code points are in compliance with the
	 * IDNA2008 BIDI rules.
	 * 
	 * @param aCodePoints
	 *        The Unicode code points to assert the IDNA2008 BIDI rules.
	 * @throws XcodeException
	 */
	public static void assertCompliance ( int[] aCodePoints )
			throws XcodeException {

		final boolean isRTLLabel = isRTLLabel( aCodePoints );

		if ( isRTLLabel ) {
			handleRTLLabel( aCodePoints );
		}
		else {
			handleLTRLabel( aCodePoints );
		}
	}


	/**
	 * Method to determine if the "domain" contains any BIDI code points i.e. R,
	 * AL or AN.
	 * 
	 * @param aCodePoints
	 *        the code points to check if there are BIDI code points
	 * @return boolean indicating whether or not BIDI code points are present
	 */
	public static boolean isBidiDomain ( int[] aCodePoints ) {
		boolean isBidiDomain = false;
		for ( final int codePoint : aCodePoints ) {
			final String bidiClass = UnicodeData.getBidiClass( codePoint );
			if ( RTL_LABEL.contains( bidiClass ) ) {
				isBidiDomain = true;
				break;
			}
		}
		return isBidiDomain;
	}


	/**
	 * Method to handle domain labels containing RTL (Right-to-Left) characters
	 * 
	 * @param aCodePoints
	 *        the RTL Unicode code points.
	 * @throws XcodeException
	 */
	private static void handleRTLLabel ( int[] aCodePoints )
			throws XcodeException {

		boolean arabicNumberPresent = false;
		boolean europeanNumberPresent = false;

		for ( final int codePoint : aCodePoints ) {
			final String cat = UnicodeData.getBidiClass( codePoint );
			if ( !RTL_BIDI_PROPERTIES.contains( cat ) ) {
				throw XcodeError.BIDI_RULE_2_VIOLATION();
			}
			if ( cat.equals( BIDI_AN ) ) {
				arabicNumberPresent = true;
			}
			if ( cat.equals( BIDI_EN ) ) {
				europeanNumberPresent = true;
			}
			if ( arabicNumberPresent && europeanNumberPresent ) {
				throw XcodeError.BIDI_RULE_4_VIOLATION();
			}

		}
		// Rule#3: In an RTL label, the end of the label must be a character
		// with BIDI property R, AL, EN or AN, followed by zero or more
		// characters with BIDI property NSM.

		// start at the end of the label
		boolean rtlLabelPropertyFound = false;
		for ( int i = aCodePoints.length - 1; i >= 0; i-- ) {
			final String cat = UnicodeData.getBidiClass( aCodePoints[ i ] );
			if ( !cat.equals( BIDI_NSM ) ) {
				rtlLabelPropertyFound =
						cat.equals( BIDI_R ) || cat.equals( BIDI_AL )
								|| cat.equals( BIDI_EN ) || cat.equals( BIDI_AN );
				break;
			}
		}
		if ( !rtlLabelPropertyFound ) {
			throw XcodeError.BIDI_RULE_3_VIOLATION();
		}
	}


	/**
	 * Method to handle domain labels containing LTR (Left-to-Right) characters
	 * 
	 * @param aCodePoints
	 *        the LTR Unicode code points.
	 * @throws XcodeException
	 */
	private static void handleLTRLabel ( int[] aCodePoints )
			throws XcodeException {

		for ( final int codePoint : aCodePoints ) {
			final String cat = UnicodeData.getBidiClass( codePoint );
			if ( !LTR_BIDI_PROPERTIES.contains( cat ) ) {
				throw XcodeError.BIDI_RULE_5_VIOLATION();
			}
		}
		// Rule#6: In an LTR label, the end of the label must be a character
		// with BIDI property L or EN, followed by zero or more characters with
		// BIDI property NSM.

		// start at the end of the label
		boolean ltrLabelPropertyFound = false;
		for ( int i = aCodePoints.length - 1; i >= 0; i-- ) {
			final String cat = UnicodeData.getBidiClass( aCodePoints[ i ] );
			if ( !cat.equals( BIDI_NSM ) ) {
				ltrLabelPropertyFound = cat.equals( BIDI_L ) || cat.equals( BIDI_EN );
				break;
			}
		}
		if ( !ltrLabelPropertyFound ) {
			throw XcodeError.BIDI_RULE_6_VIOLATION();
		}

	}


	/**
	 * Determines if the given set of code points contain RTL or LTR characters.
	 * 
	 * @param aCodePoints
	 *        The input Unicode code points.
	 * @return <tt>true</tt> if the given set of code points contain RTL
	 *         (Right-to-Left) code points.
	 * @throws XcodeException
	 */
	private static boolean isRTLLabel ( int[] aCodePoints ) throws XcodeException {
		if ( aCodePoints == null || aCodePoints.length == 0 ) {
			throw XcodeError.NULL_ARGUMENT();
		}

		boolean isRTLLabel = true;
		final String firstCpBidiCat = UnicodeData.getBidiClass( aCodePoints[ 0 ] );
		if ( firstCpBidiCat.equals( BIDI_L ) ) {
			isRTLLabel = false;
		}
		else if ( firstCpBidiCat.equals( BIDI_R )
				|| firstCpBidiCat.equals( BIDI_AL ) ) {
			isRTLLabel = true;
		}
		else {
			throw XcodeError.BIDI_RULE_1_VIOLATION();
		}
		return isRTLLabel;
	}

}
