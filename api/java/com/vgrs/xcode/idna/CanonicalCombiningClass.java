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

package com.vgrs.xcode.idna;

/**
 * Each assigned Unicode code point has a canonical combining class property.
 * This enum currently defines one canonical combining class i.e. VIRAMA which
 * has a canonical combining class value of 9. This is used in some of the
 * contextual rules.
 * 
 * @author nchigurupati
 * @version 1.0 May 5, 2010
 */
public enum CanonicalCombiningClass {
	/**
	 * VIRAMA has a canonical combining class value of 9.
	 */
	VIRAMA(9);

	/**
	 * the canonical combining class value.
	 */
	private final int canonicalCombiningClass;


	private CanonicalCombiningClass ( int aCononicalCombiningClass ) {
		this.canonicalCombiningClass = aCononicalCombiningClass;
	}


	/**
	 * @return the canonical combining class value.
	 */
	public int getCanonicalCombiningClass () {
		return this.canonicalCombiningClass;
	}


	/**
	 * Utility method to find the canonical combining class for a given int value.
	 * 
	 * @param aCanonicalCombiningClass
	 * @return the {@link CanonicalCombiningClass} for the given int value.
	 */
	public static CanonicalCombiningClass findCanonicalCombiningClass (
			int aCanonicalCombiningClass ) {

		for ( final CanonicalCombiningClass cc : CanonicalCombiningClass.values() ) {
			if ( cc.getCanonicalCombiningClass() == aCanonicalCombiningClass ) {
				return cc;
			}
		}
		return null;

	}
}
