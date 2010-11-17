
package com.vgrs.xcode.idna.contextualrule;

import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.idna.CanonicalCombiningClass;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * Parent class to be inherited by the contextual rules that deal with zero
 * width joiner/non-joiner contextual rules.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public abstract class AbstractJoinerCodePointRule extends
		AbstractAllCodePointsNotEvaluated {

	/**
	 * Checks if the canonical combining class of the code point before the zero
	 * width joiner/non-joiner code point is "Virama".
	 * 
	 * @param aCodePointIndex
	 *        the index into the code points int array containing the zero width
	 *        joiner/non-joiner character(s).
	 * @param aCodePoints
	 *        the code points int[] array
	 * @throws XcodeException
	 */
	protected boolean checkIfCpBeforeIsVirama ( int aCodePointIndex,
			int[] aCodePoints ) throws XcodeException {

		// Index of the code point before the zero width joiner/non-joiner code
		// point
		final int beforeCpIndex = aCodePointIndex - 1;
		if ( beforeCpIndex < 0 ) {
			throw XcodeError.IDNA_CONTEXTUAL_RULE_VIOLATION( super.getRuleName()
					+ ": Index of code point before "
					+ Integer
							.toHexString( super.getCodePointToWhichRuleApplies() )
							.toUpperCase() + " is < 0" );
		}

		final int ccCodePoint =
				UnicodeData.getCanonicalClass( aCodePoints[ beforeCpIndex ] );

		// If the canonical combining class is VIRAMA, then return true;
		if ( ccCodePoint == CanonicalCombiningClass.VIRAMA
				.getCanonicalCombiningClass() ) {
			return true;
		}

		return false;
	}
}
