
package com.vgrs.xcode.idna.contextualrule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.idna.CanonicalCombiningClass;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * <ul>
 * <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.1"> Zero Width Non
 * Joiner Contextual Rule</a>
 * <li>Code Point : U+200C
 * <li>Rule :
 * <p>
 * If Canonical_Combining_Class(Before(cp)) .eq. Virama Then True;
 * <p>
 * If RegExpMatch((Joining_Type:{L,D})(Joining_Type:T)*\u200C
 * (Joining_Type:T)*(Joining_Type:{R,D})) Then True;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class ZeroWidthNonJoinerRule extends AbstractJoinerCodePointRule {

	/**
	 * Zero width non joiner code point
	 */
	private static final int ZERO_WIDTH_NON_JOINER = 0x200C;

	/**
	 * String representation Zero width non joiner code point for regex matching
	 */
	private static final String ZERO_WIDTH_NON_JOINER_UNICODE_CODE_POINT =
			Integer.toHexString( ZERO_WIDTH_NON_JOINER ).toUpperCase();

	/**
	 * Regex pattern to match if Zero width non joiner code point is present
	 */
	private static final Pattern JOINING_TYPE =
			Pattern.compile( "(L|D)(T)*200C(T)*(R|D)" );


	public ZeroWidthNonJoinerRule () {
		super.setCodePointToWhichRuleApplies( ZERO_WIDTH_NON_JOINER );
	}


	/**
	 * This method first checks to see if the canonical combining class of the
	 * code point before the ZERO WIDTH NON JOINER code point is VIRAMA. If it is,
	 * then the rule returns. Otherwise a further check is done to ensure that the
	 * code points satisfy the regular expression specified by this contextual
	 * rule.
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {

		// If canonical combining class before code point is VIRAMA, then return.
		if ( super.checkIfCpBeforeIsVirama( aCodePointIndex, aCodePoints ) ) {
			return;
		}

		// Canonical combining class before code point is not VIRAMA, so we need to
		// run the regex rule.
		// If RegExpMatch((Joining_Type:{L,D})(Joining_Type:T)*\u200C
		// (Joining_Type:T)*(Joining_Type:{R,D})) Then True;

		char joiningTypeOfCodePoint;

		final StringBuilder sb = new StringBuilder();
		for ( final int codePoint : aCodePoints ) {
			if ( codePoint == ZERO_WIDTH_NON_JOINER ) {
				sb.append( ZERO_WIDTH_NON_JOINER_UNICODE_CODE_POINT );
			}
			else {
				joiningTypeOfCodePoint = UnicodeData.getJoiningType( codePoint );
				sb.append( joiningTypeOfCodePoint );
			}
		}

		final Matcher matcher = JOINING_TYPE.matcher( sb.toString() );
		if ( !matcher.matches() ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
							+ ": Either Canonical Combining Class of code point before "
							+ Integer
									.toHexString( super.getCodePointToWhichRuleApplies() )
									.toUpperCase()
							+ " must be "
							+ CanonicalCombiningClass.VIRAMA.toString()
							+ " OR should match the regex "
							+ "((Joining_Type:{L,D})(Joining_Type:T)*200C(Joining_Type:T)*(Joining_Type:{R,D})" );
		}
	}
}
