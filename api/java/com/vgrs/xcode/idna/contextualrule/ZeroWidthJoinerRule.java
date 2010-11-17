
package com.vgrs.xcode.idna.contextualrule;

import com.vgrs.xcode.idna.CanonicalCombiningClass;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * <ul>
 * <li><a href="http://tools.ietf.org/html/rfc5892#appendix-A.2"> Zero Width
 * Joiner Contextual Rule</a>
 * <li>Code Point : U+200D
 * <li>Rule : If Canonical_Combining_Class(Before(cp)) .eq. Virama Then True;
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class ZeroWidthJoinerRule extends AbstractJoinerCodePointRule {

	/**
	 * Zero width joiner code point
	 */
	private static final int ZERO_WIDTH_JOINER = 0x200D;


	/**
	 * Constructor specifies that this rule can only execute if the Zero width
	 * joiner code point is present in the given set of code points.
	 */
	public ZeroWidthJoinerRule () {
		super.setCodePointToWhichRuleApplies( ZERO_WIDTH_JOINER );
	}


	/**
	 * Checks if the canonical combining class of the code point before the Zero
	 * width joiner code point is Virama.
	 */
	@Override
	public void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException {
		// If Canonical_Combining_Class(Before(cp)) .eq. Virama Then True;
		if ( !super.checkIfCpBeforeIsVirama( aCodePointIndex, aCodePoints ) ) {
			throw XcodeError.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
					+ ": Canonical Combining Class of code point before "
					+ Integer
							.toHexString( super.getCodePointToWhichRuleApplies() )
							.toUpperCase() + " is not "
					+ CanonicalCombiningClass.VIRAMA.toString() );
		}
	}
}
