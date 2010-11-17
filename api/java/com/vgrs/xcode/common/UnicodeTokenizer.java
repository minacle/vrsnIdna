/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Allows tokenization of an array of integer primitives. Functionality emulates
 * the StringTokenizer object.
 */
public class UnicodeTokenizer implements Iterable<int[]> {

	private final Collection<int[]> results;


	/**
	 * Construct a unicode tokenizer for the specified unicode.
	 * 
	 * @param aInput
	 *        a unicode to be parsed
	 * @param aDelimiters
	 *        delimiters
	 * @param aReturnDelims
	 *        flag to indicate if to return delimiter as token
	 */
	public UnicodeTokenizer ( int[] aInput, int[] aDelimiters,
			boolean aReturnDelims ) {
		this.results = new ArrayList<int[]>();

		int len = 0;
		int[] token;
		for ( int i = 0; i < aInput.length; i++ ) {
			if ( exists( aInput[ i ], aDelimiters ) ) {
				if ( len > 0 ) {
					token = new int[len];
					System.arraycopy( aInput, i - len, token, 0, len );
					this.results.add( token );
				}
				if ( aReturnDelims ) {
					token = new int[1];
					token[ 0 ] = aInput[ i ];
					this.results.add( token );
				}
				len = 0;
			}
			else {
				len++;
			}
		}
		if ( len > 0 ) {
			token = new int[len];
			System.arraycopy( aInput, aInput.length - len, token, 0, len );
			this.results.add( token );
		}
	}


	static private boolean exists ( int aCodePoint, int[] aDelimiters ) {
		for ( final int delimiter : aDelimiters ) {
			if ( delimiter == aCodePoint ) {
				return true;
			}
		}
		return false;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<int[]> iterator () {
		return this.results.iterator();
	}

}