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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class serves as a registry for all the contextual rules defined in
 * "ContextualRules.properties" file. This class on startup will instantiate
 * each of the classes specified in the properties file. It also contains a
 * method to execute the contextual rules if a contextual code point is found.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public class ContextualRulesRegistry {

	/**
	 * File name of the file containing the contextual rules names and their
	 * associated classes
	 */
	private static final String CONTEXTUAL_RULES_DATA =
			"data/idna/ContextualRules.properties";

	/**
	 * Map to hold the contextual rule name and the associated contextul rule
	 * class.
	 */
	private static final Map<String, ContextualRule> CONTEXTUAL_RULES =
			new HashMap<String, ContextualRule>();

	/**
	 * boolean to indicate that this class has finished initialzing.
	 */
	private static boolean INITIALIZED = false;

	static {
		try {
			init();
		}
		catch ( final Exception e ) {
			throw new RuntimeException( e.getMessage() );
		}
	}


	/**
	 * This method loads the contextual rules specified in
	 * "ContextualRules.properties" file.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static synchronized void init () throws XcodeException {
		if ( INITIALIZED ) {
			return;
		}
		Iterator<String> reader = null;
		reader = Datafile.getIterator( CONTEXTUAL_RULES_DATA );
		String line = null;
		while ( reader.hasNext() ) {
			line = reader.next();
			if ( line.startsWith( "#" ) || line.trim().isEmpty() ) {
				continue;
			}
			final String[] fields = line.split( "=" );
			if ( fields.length != 2 ) {
				continue;
			}
			final String ruleName = fields[ 0 ].trim();
			final String className = fields[ 1 ].trim();
			ContextualRule rule = null;
			Class<? extends ContextualRule> ruleClass = null;
			try {
				ruleClass = (Class<? extends ContextualRule>) Class.forName( className );
				rule = ruleClass.newInstance();
				rule.setRuleName( ruleName );
				CONTEXTUAL_RULES.put( ruleName, rule );
			}
			catch ( final Throwable instantiationException ) {
				System.err.println( instantiationException.getMessage() );
				throw XcodeError
						.IDNSDK_INITIALIZATION_ERROR( "Unable to instantiate class \""
								+ className + "\" specified for rule \"" + ruleName + "\": "
								+ instantiationException.getMessage() );
			}
		}
		INITIALIZED = true;
	}


	/**
	 * This method executes the contextual rules. This method is only invoked if
	 * there are contextual code points. For each rule in the contextual rules
	 * registry, if the rule can be executed, it will be executed only once.
	 * 
	 * @param aCodePoints
	 *        the int[] array containing the code points to be evaluated by the
	 *        contextual rules.
	 * @return Set<String> containing the rule names of the contextual rules
	 *         executed
	 * @throws XcodeException
	 *         if no contextual rules have been executed.
	 */
	public static Set<String> runContextualRules ( int[] aCodePoints )
			throws XcodeException {

		final int length = aCodePoints.length;
		final Collection<ContextualRule> contextualRules =
				CONTEXTUAL_RULES.values();
		final Set<String> executedRules = new HashSet<String>();

		for ( int index = 0; index < length; index++ ) {
			final int codePoint = aCodePoints[ index ];
			for ( final ContextualRule rule : contextualRules ) {
				if ( executedRules.contains( rule.getRuleName() )
						&& rule.allCodePointsEvaluated() ) {
					// All code points have been evaluated by this rule. No need to
					// execute the rule again. For example this will prevent the
					// ArabicIndicDigitsRule,ExtendedArabicIndicDigitsRule and
					// KatakanaMiddleDotRule from executing again, as these rules have
					// already evaluated all the code points.
					continue;
				}
				if ( rule.ruleAppliesToCodepoint( codePoint ) ) {
					rule.executeRule( index, aCodePoints );
					executedRules.add( rule.getRuleName() );
				}
			}
		}
		if ( executedRules.isEmpty() ) {
			throw XcodeError
					.IDNA_CONTEXTUAL_RULE_VIOLATION( "No contextual rules were executed even though "
							+ "contextual code points were present" );
		}
		return executedRules;
	}


	public static void main ( String[] args ) {
		System.out.println( CONTEXTUAL_RULES.keySet() );
	}
}