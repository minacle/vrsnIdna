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

package com.vgrs.xcode.common.unicodedata;

/**
 * Enum for the derived property value of Unicode code points as specified in
 * the IDNA2008 Protocol.
 * 
 * @author nchigurupati
 * @version 1.0 Jun 10, 2010
 */
public enum UnicodeCodePointCategory {

	// The Unicode code point categories
	DISALLOWED, UNASSIGNED, PVALID, CONTEXTO, CONTEXTJ;

	/**
	 * Utility method to find a UnicodeCategory based on the given string input.
	 * 
	 * @param aCategory
	 *        The string representation of the Unicode category.
	 * @return the UnicodeCodePointCategories or null if not found.
	 */
	public static UnicodeCodePointCategory getUnicodeCodePointCategory (
			String aCategory ) {

		final UnicodeCodePointCategory[] categories =
				UnicodeCodePointCategory.values();
		for ( final UnicodeCodePointCategory category : categories ) {
			if ( category.toString().equals( aCategory ) ) {
				return category;
			}
		}
		return null;
	}
}
