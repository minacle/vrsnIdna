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

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * This class explicitly lists code points for which the category cannot be
 * assigned using only the core property values that exist in the Unicode
 * standard. The values are according to the tables below. These code points are
 * stored in the appropriate GNU Trove collection classes.
 * 
 * @author nchigurupati
 * @version 1.0 Jun 10, 2010
 */
public class ExceptionCodePoints {

	private static final TIntSet PVALID = new TIntHashSet();
	private static final TIntSet DISALLOWED = new TIntHashSet();
	private static final TIntSet CONTEXTO = new TIntHashSet();

	static {
		init();
	}


	/**
	 * Return the category for the given code point based on it's existence in one
	 * of the GNU Trove collections.
	 * 
	 * @param aCodePoint
	 * @return {@link UnicodeCodePointCategory} of the given code point.
	 */
	public static UnicodeCodePointCategory getCategory ( int aCodePoint ) {
		if ( PVALID.contains( aCodePoint ) ) {
			return UnicodeCodePointCategory.PVALID;
		}
		else if ( DISALLOWED.contains( aCodePoint ) ) {
			return UnicodeCodePointCategory.DISALLOWED;
		}
		else if ( CONTEXTO.contains( aCodePoint ) ) {
			return UnicodeCodePointCategory.CONTEXTO;
		}
		else {
			return null;
		}
	}


	private static void init () {

		/*
		 * PVALID -- Would otherwise have been DISALLOWED
		 */
		PVALID.add( 0x00DF );
		PVALID.add( 0x03C2 );
		PVALID.add( 0x06FD );
		PVALID.add( 0x06FE );
		PVALID.add( 0x0F0B );
		PVALID.add( 0x3007 );

		/*
		 * CONTEXTO -- Would otherwise have been DISALLOWED
		 */
		CONTEXTO.add( 0x00B7 );
		CONTEXTO.add( 0x0375 );
		CONTEXTO.add( 0x05F3 );
		CONTEXTO.add( 0x05F4 );
		CONTEXTO.add( 0x030FB );

		/*
		 * CONTEXTO -- Would otherwise have been PVALID
		 */
		CONTEXTO.add( 0x0660 );
		CONTEXTO.add( 0x0661 );
		CONTEXTO.add( 0x0662 );
		CONTEXTO.add( 0x0663 );
		CONTEXTO.add( 0x0664 );
		CONTEXTO.add( 0x0665 );
		CONTEXTO.add( 0x0666 );
		CONTEXTO.add( 0x0667 );
		CONTEXTO.add( 0x0668 );
		CONTEXTO.add( 0x0669 );
		CONTEXTO.add( 0x06F0 );
		CONTEXTO.add( 0x06F1 );
		CONTEXTO.add( 0x06F2 );
		CONTEXTO.add( 0x06F3 );
		CONTEXTO.add( 0x06F4 );
		CONTEXTO.add( 0x06F5 );
		CONTEXTO.add( 0x06F6 );
		CONTEXTO.add( 0x06F7 );
		CONTEXTO.add( 0x06F8 );
		CONTEXTO.add( 0x06F9 );

		/*
		 * DISALLOWED -- Would otherwise have been PVALID
		 */
		DISALLOWED.add( 0x0640 );
		DISALLOWED.add( 0x07FA );
		DISALLOWED.add( 0x302E );
		DISALLOWED.add( 0x302F );
		DISALLOWED.add( 0x3031 );
		DISALLOWED.add( 0x3032 );
		DISALLOWED.add( 0x3033 );
		DISALLOWED.add( 0x3034 );
		DISALLOWED.add( 0x3035 );
		DISALLOWED.add( 0x303B );
	}

}
