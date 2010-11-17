
package com.vgrs.xcode.idna.contextualrule;

import com.vgrs.xcode.util.XcodeException;

/**
 * Interface for implementing the contextual rules in IDNA2008 Tables document.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public interface ContextualRule {

	/**
	 * Booleans to specify if <tt>all</tt> code points in the rule have been
	 * evaluated. This is false for most of the rules which evaluate one code
	 * point at a time. However, a few rules (eg: {@link ArabicIndicDigitsRule},
	 * {@link KatakanaMiddleDotRule}, etc.) evaluate all the code points when
	 * executed. For these rules a value of <tt>true</tt> is returned.
	 */
	boolean ALL_CODE_POINTS_EVALUATED_TRUE = true;

	boolean ALL_CODE_POINTS_EVALUATED_FALSE = false;


	/**
	 * Checks if the specified code point satisfies the condition for executing
	 * the rule
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the contextual rule needs to be executed
	 */
	boolean ruleAppliesToCodepoint ( int aCodePoint );


	/**
	 * Executes the rule as per the IDNA2008 specification's contextual rule
	 * requirements for this rule.
	 * 
	 * @param aCodePointIndex
	 *        the index of the code point that satisfied the condition of this
	 *        rule
	 * @param aCodePoints
	 *        the code points to be evaluated.
	 * @throws XcodeException
	 */
	void executeRule ( int aCodePointIndex, int[] aCodePoints )
			throws XcodeException;


	/**
	 * @return the name of the rule specified in ContextualRule.properties
	 */
	String getRuleName ();


	/**
	 * @param aRuleName
	 *        set the name of the rule as specified in ContextualRule.properties
	 */
	void setRuleName ( String aRuleName );


	/**
	 * @return a boolean indicating if all code points have been evaluated. If all
	 *         code points have been evaluated, this rule will not be executed
	 *         again.
	 */
	boolean allCodePointsEvaluated ();

}
