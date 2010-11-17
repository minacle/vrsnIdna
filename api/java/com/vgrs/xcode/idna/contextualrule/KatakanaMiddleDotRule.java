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

import java.util.HashSet;

import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * <ul>
 * <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.7">Katakana Middle
 * Dot Contextual Rule</a>
 * <li>Appendix A.7: KATAKANA MIDDLE DOT
 * <li>Code Point : U+30FB
 * <li>Rule : If Script(cp) .in. {Hiragana, Katakana, Han} Then True;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class KatakanaMiddleDotRule extends AbstractAllCodePointsEvaluated {

	/**
	 * Katakana Middle Dot code point
	 */
	private static final int KATAKANA_MIDDLE_DOT = 0x30FB;
	/**
	 * Constant for the Han script as defined in Scripts.txt by Unicode.
	 */
	private static final String HAN_SCRIPT = "Han";

	/**
	 * Constant for the Hiragana script as defined in Scripts.txt by Unicode.
	 */
	private static final String HIRAGANA_SCRIPT = "Hiragana";

	/**
	 * Constant for the Katakana script as defined in Scripts.txt by Unicode.
	 */
	private static final String KATAKANA_SCRIPT = "Katakana";

	/**
	 * Set contanining the allowable scripts when Katakana Middle Dot is present.
	 */
	private static final HashSet<String> scriptsRequiredWhenKatakanaMiddleDotPresent =
			new HashSet<String>();

	static {
		scriptsRequiredWhenKatakanaMiddleDotPresent.add( HAN_SCRIPT );
		scriptsRequiredWhenKatakanaMiddleDotPresent.add( HIRAGANA_SCRIPT );
		scriptsRequiredWhenKatakanaMiddleDotPresent.add( KATAKANA_SCRIPT );
	}


	/**
	 * Constructor specifies that this rule can only execute if the Katakana
	 * Middle Dot code point is present in the given set of code points.
	 */
	public KatakanaMiddleDotRule () {
		super.setCodePointToWhichRuleApplies( KATAKANA_MIDDLE_DOT );
	}


	/**
	 * If the Katakana Middle Dot code point is present, then at least one code
	 * point must belong to one of {Hiragana, Katakana, or Han} script.
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {

		// This rule requires at least one character in the label to be in one
		// of the {Hiragana, Katakana or Han} scripts.
		boolean foundHiraganaKatanaOrHanCharacter = false;

		for ( final int codePoint : aCodePoints ) {

			if ( codePoint == KATAKANA_MIDDLE_DOT ) {
				continue;
			}

			final String script = UnicodeData.getScript( codePoint );
			if ( scriptsRequiredWhenKatakanaMiddleDotPresent.contains( script ) ) {
				foundHiraganaKatanaOrHanCharacter = true;
				break;
			}
		}

		// No Hiragana, Katakana, or Han characters found
		if ( !foundHiraganaKatanaOrHanCharacter ) {
			throw XcodeError.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
					+ ": At least one character in a label that has a Katakana Middle Dot (0x30FB) "
					+ "must belong to any of the "
					+ scriptsRequiredWhenKatakanaMiddleDotPresent + " scripts." );
		}
	}

}
