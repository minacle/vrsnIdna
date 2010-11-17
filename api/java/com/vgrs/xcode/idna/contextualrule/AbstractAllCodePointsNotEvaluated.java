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
 * Abstract class to indicate that all code points have been evaluated. Most
 * contextual rules apply to only specific code points. There are some rules
 * which evaluate all the code points. This class identifies rules which iterate
 * and process all the code points when a specific contextual code point is
 * found in the input. This will prevent the rule from being executed multiple
 * times as the <code>ContextualRulesRegistry.runContextualRules</code> method
 * iterates over all the code points in the input.
 * 
 * @author nchigurupati
 * @version 1.0 Aug 4, 2010
 */
public abstract class AbstractAllCodePointsNotEvaluated extends
		AbstractContextualRule {

	/**
	 * By default this method return <tt>false</tt>.
	 */
	@Override
	public boolean allCodePointsEvaluated () {
		return ContextualRule.ALL_CODE_POINTS_EVALUATED_FALSE;
	}

}
