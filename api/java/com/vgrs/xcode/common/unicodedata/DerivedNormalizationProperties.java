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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * Class to load the data in "DerivedNormalizationProps.txt.gz" into GNU Trove
 * collections. The data in this class will be used to perform a quick check to
 * find out if a given set of Unicode code points are in NFC normalized form.
 * 
 * @author nchigurupati
 * @version 1.0 Jun 11, 2010
 */
public class DerivedNormalizationProperties {

	/**
	 * A collection to hold a given code point and it's associated derived
	 * normalized properties.
	 */
	private static final TIntObjectMap<Set<String>> DERIVED_NORMALIZATION_PROP_TABLE =
			new TIntObjectHashMap<Set<String>>();

	static {
		try {
			init();
		}
		catch ( final XcodeException x ) {
			throw new RuntimeException( x.getMessage() );
		}
	}


	/**
	 * @return <code>DERIVED_NORMALIZATION_PROP_TABLE</code>
	 */
	public static TIntObjectMap<Set<String>> getDerivedNormalizationPropTable () {
		return DERIVED_NORMALIZATION_PROP_TABLE;
	}


	/**
	 * Reads the "DerivedNormalizationProps.txt.gz" and stores the code point
	 * along with it's normalized form in
	 * <code>DERIVED_NORMALIZATION_PROP_TABLE</code> data structure. Example line
	 * of data from this file is below.
	 * 
	 * <pre>
	 * 0340..0341 ; NFC_QC; N # Mn [2] COMBINING GRAVE TONE MARK..COMBINING ACUTE TONE MARK
	 * 
	 * @throws XcodeException
	 */
	private static void init () throws XcodeException {
		Iterator<String> reader = null;
		String line = null;
		String codePointString = null;
		String nfcProperty = null;
		String derivedNormalizationProperty = null;
		StringTokenizer st = null;
		String property = null;

		try {
			reader =
					Datafile
							.getIterator( UnicodeDataConstants.DERIVED_NORMALIZATION_PROPS_DATA );

			while ( reader.hasNext() ) {

				line = reader.next();

				if ( line == null ) {
					break;
				}
				if ( line.length() == 0 ) {
					continue;
				}
				if ( line.charAt( 0 ) == '#' ) {
					continue;
				}

				property = null;
				st =
						new StringTokenizer( line,
								UnicodeDataConstants.SEMI_COLON_DELIMITER );

				if ( st.hasMoreTokens() ) {
					codePointString = st.nextToken().trim();
				}

				if ( st.hasMoreTokens() ) {
					nfcProperty = st.nextToken().trim();
				}

				/*
				 * We are only parsing the following derived properties NFC_Quick_Check
				 * (NFC_QC) NFKC_Quick_Check (NFKC_QC) NFKC_Casefold (NFKC_CF)
				 */
				if ( !nfcProperty.equals( UnicodeDataConstants.NFKC_QC )
						&& !nfcProperty.equals( UnicodeDataConstants.NFC_QC )
						&& !nfcProperty.equals( UnicodeDataConstants.NFKC_CF ) ) {
					continue;
				}

				if ( st.hasMoreTokens() ) {
					derivedNormalizationProperty = st.nextToken().trim();
				}

				st =
						new StringTokenizer( derivedNormalizationProperty,
								UnicodeDataConstants.TAB_DELIMITER );
				if ( st.hasMoreTokens() ) {
					property = st.nextToken();
					property = property.trim();
				}

				codePointString = codePointString.replace( "..", " " );

				final int[] codePoints = Hex.decodeInts( codePointString );
				int start, end;

				if ( codePoints.length == 2 ) {
					start = codePoints[ 0 ];
					end = codePoints[ 1 ];
				}
				else if ( codePoints.length == 1 ) {
					start = codePoints[ 0 ];
					end = start;
				}
				else {
					line = ": \"" + line + "\"";
					throw XcodeError.INVALID_FILE_FORMAT( line );
				}

				for ( int codePoint = start; codePoint <= end; codePoint++ ) {

					Set<String> set = DERIVED_NORMALIZATION_PROP_TABLE.get( codePoint );
					if ( set == null ) {
						set = new HashSet<String>();
					}
					if ( property != null
							&& (property.equals( "N" ) || property.equals( "M" )) ) {
						set.add( nfcProperty + "_" + property );
					}
					else {
						set.add( nfcProperty );
					}
					DERIVED_NORMALIZATION_PROP_TABLE.put( codePoint, set );
				}
			}
		}
		catch ( final NumberFormatException x ) {
			line = ": \"" + line + "\"";
			throw XcodeError.INVALID_FILE_FORMAT( line );
		}
		catch ( final Exception x ) {
			line = ": \"" + line + "\"";
			throw XcodeError.INVALID_FILE_FORMAT( line );
		}
	}
}
