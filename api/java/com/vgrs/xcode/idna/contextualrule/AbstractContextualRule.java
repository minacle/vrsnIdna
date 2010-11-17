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

/**
 * Parent class to be inherited by all class that implement the
 * {@link ContextualRule} interface. This class has the ruleName of the
 * contextual rule.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public abstract class AbstractContextualRule implements ContextualRule {

	/**
	 * The contextual rule name
	 */
	private String ruleName;

	/**
	 * The specific contextual code point associated with this contextual rule
	 */
	private int codePointToWhichRuleApplies;


	@Override
	public String getRuleName () {
		return this.ruleName;
	}


	@Override
	public void setRuleName ( String aRuleName ) {
		this.ruleName = aRuleName;
	}


	@Override
	public boolean ruleAppliesToCodepoint ( int aCodePoint ) {
		return aCodePoint == getCodePointToWhichRuleApplies();
	}


	/**
	 * @return the code point associated with this contextual rule
	 */
	public int getCodePointToWhichRuleApplies () {
		return this.codePointToWhichRuleApplies;
	}


	/**
	 * @param aCodePoint
	 *        set the code point for this contextual rule
	 */
	public void setCodePointToWhichRuleApplies ( int aCodePoint ) {
		this.codePointToWhichRuleApplies = aCodePoint;
	}

}
