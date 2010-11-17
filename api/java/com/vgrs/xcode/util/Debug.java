/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.util;

/**
 * Log debugging information to System.out/System.err.
 */
public class Debug {

	/**
	 * Log information to Standard Error. This method can be used to display debug
	 * information during development.
	 * 
	 * @param aInput
	 *        A message to log
	 */
	static public void log ( String aInput ) {
		System.err.println( aInput );
	}


	/**
	 * Log information to Standard Out. This method is commonly used in test
	 * routines to display output after a successful execution. Input is not
	 * modified.
	 * 
	 * @param aInput
	 *        A message to log
	 */
	static public void pass ( String aInput ) {
		System.out.println( aInput );
	}


	/**
	 * Log information to Standard Error. This method is commonly used in test
	 * routines to display output after a failed execution. A "Number Sign" (#)
	 * character is prefixed to the input. This facilitates sequenced executions
	 * on the command-line. For instance, users can use the "race" command to
	 * decode an input file, and write the contents out to an output file. This
	 * entire output file can then be fed into the "idna" routine. Records which
	 * failed the original race decode will be ignored by the idna routine because
	 * they have been prefixed with a "#" character.
	 * 
	 * @param aInput
	 *        A message to log
	 */
	static public void fail ( String aInput ) {
		System.err.println( "#	" + aInput );
	}

}