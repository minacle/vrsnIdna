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

import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Range;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * Unicode data files come in a variety of different formats. Rather than
 * clutter and/or repeat the code to read data from Unicode data files, the
 * utility methods to read from a variety of different Unicode data file formats
 * are present in this class.
 * 
 * @author nchigurupati
 * @version 1.0 Jun 10, 2010
 */
public class UnicodeDataFileUtil {

	/**
	 * Loads a code point and it's corresponding property into a
	 * <code>TIntObjectMap<String></code>. Data read from the file is tokenized
	 * using the given delimiter parameter. Properties that are present in the
	 * <code>Set<String> aPropertiesToLoad</code> are loaded into the collection.
	 * 
	 * @param aFileName
	 *        The file from which to load the code points and the corresponding
	 *        property into a TIntObjectMap<String> object.
	 * @param aMap
	 *        TIntObjectMap<String> object
	 * @param aDelimiter
	 *        The delimiter to tokenize the data read from the data file.
	 * @param aPropertiesToLoad
	 *        Load only properties specified in this set.
	 * @throws XcodeException
	 */
	public static void loadCodePointAndCategoryMap ( String aFileName,
			TIntObjectMap<String> aMap, String aDelimiter,
			Set<String> aPropertiesToLoad ) throws XcodeException {

		if ( aPropertiesToLoad == null || aPropertiesToLoad.isEmpty() ) {
			return;
		}

		Iterator<String> reader = null;
		String line = null;
		String token = null;

		try {
			reader = Datafile.getIterator( aFileName );
			StringTokenizer st = null;
			String property = null;

			while ( reader.hasNext() ) {
				line = reader.next().trim();

				if ( line.isEmpty() || line.charAt( 0 ) == '#' ) {
					continue;
				}

				st = new StringTokenizer( line, aDelimiter );
				token =
						st.nextToken().trim().replace(
								UnicodeDataConstants.DOT_DOT_DELIMITER, " " );
				property = st.nextToken().trim();

				if ( !aPropertiesToLoad.contains( property ) ) {
					continue;
				}

				final int[] codePoints = Hex.decodeInts( token );
				if ( codePoints.length > 1 ) {
					for ( int i = codePoints[ 0 ]; i <= codePoints[ 1 ]; i++ ) {
						aMap.put( i, property );
					}
				}
				else {
					aMap.put( codePoints[ 0 ], property );
				}

			} // END while()
		}
		catch ( final Throwable x ) {
			handleException( x, line );
		}
	}


	/**
	 * Loads a code point and it's corresponding property into a
	 * <code>TIntObjectMap<Set<String>></code>. Data read from the file is
	 * tokenized using the given delimiters parameter.
	 * 
	 * @param aFileName
	 *        The file from which to load the code points and the corresponding
	 *        property into a TIntObjectMap<String> object.
	 * @param aMap
	 *        TIntObjectMap<Set<String>> object
	 * @param delimiters
	 *        The delimiters to tokenize the data read from the data file in
	 *        order.
	 * @throws XcodeException
	 */
	public static void loadCodePointAndCategorySet ( String aFileName,
			TIntObjectMap<Set<String>> aMap, String[] delimiters )
			throws XcodeException {

		Iterator<String> reader = null;
		String line = null;
		String codePointsToken = null;
		String propertyToken = null;

		try {
			reader = Datafile.getIterator( aFileName );
			StringTokenizer st = null;
			String property = null;

			while ( reader.hasNext() ) {

				line = reader.next().trim();
				if ( line.isEmpty() || line.charAt( 0 ) == '#' ) {
					continue;
				}

				st = new StringTokenizer( line, delimiters[ 0 ] );
				codePointsToken = st.nextToken();
				codePointsToken = codePointsToken.trim();

				propertyToken = st.nextToken();
				propertyToken = propertyToken.trim();

				st = new StringTokenizer( propertyToken, delimiters[ 1 ] );
				property = st.nextToken();
				property = property.trim();

				codePointsToken =
						codePointsToken.replace( UnicodeDataConstants.DOT_DOT_DELIMITER,
								" " );

				final int[] codePoints = Hex.decodeInts( codePointsToken );

				if ( codePoints.length > 1 ) {
					for ( int j = codePoints[ 0 ]; j <= codePoints[ 1 ]; j++ ) {
						Set<String> set = aMap.get( j );
						if ( set == null ) {
							set = new HashSet<String>();
						}
						set.add( property );
						aMap.put( j, set );
					}
				}
				else {
					Set<String> set = aMap.get( codePoints[ 0 ] );
					if ( set == null ) {
						set = new HashSet<String>();
					}
					set.add( property );
					aMap.put( codePoints[ 0 ], set );
				}

			} // END while()
		}
		catch ( final Throwable x ) {
			handleException( x, line );
		}

	}


	/**
	 * Loads a code point and it's corresponding property into a
	 * <code>TIntObjectMap<String></code>. Data read from the file is tokenized
	 * using the given delimiters parameter. Properties that are present in the
	 * <code>Set<String> aPropertiesToLoad</code> are loaded into the collection.
	 * 
	 * @param aFileName
	 *        The file from which to load the code points and the corresponding
	 *        property into a TIntObjectMap<String> object.
	 * @param aMap
	 *        TIntObjectMap<String> object
	 * @param delimiters
	 *        The delimiter to tokenize the data read from the data file.
	 * @param aPropertiesToLoad
	 *        Load only properties specified in this set.
	 * @throws XcodeException
	 */

	public static void loadCodePointAndCategoryMap ( String aFileName,
			TIntObjectMap<String> aMap, String[] delimiters,
			Set<String> aPropertiesToLoad ) throws XcodeException {

		Iterator<String> reader = null;
		String line = null;
		String codePointToken = null;
		String propertyToken = null;
		try {
			reader = Datafile.getIterator( aFileName );
			StringTokenizer st = null;
			String property = null;

			while ( reader.hasNext() ) {

				line = reader.next().trim();
				if ( line.isEmpty() || line.charAt( 0 ) == '#' ) {
					continue;
				}

				st = new StringTokenizer( line, delimiters[ 0 ] );
				codePointToken =
						st.nextToken().trim().replace(
								UnicodeDataConstants.DOT_DOT_DELIMITER, " " );

				propertyToken = st.nextToken().trim();
				st = new StringTokenizer( propertyToken, delimiters[ 1 ] );
				property = st.nextToken().trim();
				if ( aPropertiesToLoad != null
						&& !aPropertiesToLoad.contains( property ) ) {
					continue;
				}

				final int[] codePoints = Hex.decodeInts( codePointToken );

				if ( codePoints.length > 1 ) {
					for ( int j = codePoints[ 0 ]; j <= codePoints[ 1 ]; j++ ) {
						aMap.put( j, property );
					}
				}
				else {
					aMap.put( codePoints[ 0 ], property );
				}

			} // END while()
		}
		catch ( final Throwable x ) {
			handleException( x, line );
		}
	}


	/**
	 * Loads a code point and it's corresponding property into a
	 * <code>TIntCharMap</code>. Data read from the file is tokenized using the
	 * given delimiters parameter. Properties that are present in the
	 * <code>Set<String> aPropertiesToLoad</code> are loaded into the collection.
	 * 
	 * @param aFileName
	 *        The file from which to load the code points and the corresponding
	 *        property into a TIntObjectMap<String> object.
	 * @param aMap
	 *        TIntCharMap object
	 * @param delimiters
	 *        The delimiters to tokenize the data read from the data file.
	 * @param aPropertiesToLoad
	 *        Load only properties specified in this set.
	 * @throws XcodeException
	 */

	public static void loadCodePointAndCategoryCharMap ( String aFileName,
			TIntCharMap aMap, String[] delimiters, Set<String> aPropertiesToLoad )
			throws XcodeException {

		Iterator<String> reader = null;
		String line = null;
		String codePointsToken = null;
		String propertyToken = null;
		try {
			reader = Datafile.getIterator( aFileName );
			StringTokenizer st = null;
			String property = null;

			while ( reader.hasNext() ) {

				line = reader.next().trim();
				if ( line.isEmpty() || line.charAt( 0 ) == '#' ) {
					continue;
				}

				st = new StringTokenizer( line, delimiters[ 0 ] );
				codePointsToken =
						st.nextToken().trim().replace(
								UnicodeDataConstants.DOT_DOT_DELIMITER, " " );

				propertyToken = st.nextToken().trim();
				st = new StringTokenizer( propertyToken, delimiters[ 1 ] );
				property = st.nextToken().trim();
				if ( aPropertiesToLoad != null
						&& !aPropertiesToLoad.contains( property ) ) {
					continue;
				}

				final int[] codePoints = Hex.decodeInts( codePointsToken );

				if ( codePoints.length > 1 ) {
					for ( int j = codePoints[ 0 ]; j <= codePoints[ 1 ]; j++ ) {
						aMap.put( j, property.toCharArray()[ 0 ] );
					}
				}
				else {
					aMap.put( codePoints[ 0 ], property.toCharArray()[ 0 ] );
				}

			} // END while()
		}
		catch ( final Throwable x ) {
			handleException( x, line );
		}
	}


	/**
	 * Loads a code point and it's corresponding property into a
	 * <code>TIntSet</code>. Data read from the file is tokenized using the given
	 * delimiter parameter.
	 * 
	 * @param aFileName
	 *        The file from which to load the code points and the corresponding
	 *        property into a TIntSet object.
	 * @param aSet
	 *        TIntSet object into which the code points read should be added.
	 * @param aDelimiter
	 *        The delimiter to tokenize the data read from the data file.
	 * @throws XcodeException
	 */

	public static void loadCodePointSet ( String aFileName, TIntSet aSet,
			String aDelimiter ) throws XcodeException {
		Iterator<String> reader = null;
		String line = null;
		StringTokenizer st = null;
		String token = null;
		int codePoint = -1;

		try {
			reader = Datafile.getIterator( aFileName );

			while ( reader.hasNext() ) {
				line = reader.next().trim();
				if ( line.isEmpty() || line.charAt( 0 ) == '#' ) {
					continue;
				}

				st = new StringTokenizer( line, aDelimiter );
				if ( st.hasMoreTokens() ) {
					token = st.nextToken();
					codePoint = Integer.parseInt( token, 16 );
					aSet.add( codePoint );
				}
				else {
					continue; // skip blank line
				}
			}
		}
		catch ( final Throwable x ) {
			handleException( x, line );
		}

	}


	/**
	 * Read Unicode data from a file, return a List of Range objects.
	 * 
	 * @param resource
	 *        The resource containing Unicode data.
	 * @throws XcodeException
	 *         If the file does not exist or has invalid format.
	 */
	static public List<Range> getRanges ( String resource ) throws XcodeException {
		final List<Range> out = new ArrayList<Range>();

		Iterator<String> reader = null;
		String line = null;
		StringTokenizer st = null;
		int first;
		int last;

		reader = Datafile.getIterator( resource );
		try {

			while ( reader.hasNext() ) {
				line = reader.next();

				if ( line == null ) {
					break;
				}
				line = line.trim();
				if ( line.length() == 0 ) {
					continue;
				}
				if ( line.charAt( 0 ) == '#' ) {
					continue;
				}

				st = new StringTokenizer( line, " \t-;" );

				// Assign first
				if ( st.hasMoreTokens() ) {
					first = Integer.parseInt( st.nextToken(), 16 );
				}
				else {
					continue;
				}

				// Assign last
				if ( st.hasMoreTokens() ) {
					last = Integer.parseInt( st.nextToken(), 16 );
				}
				else {
					last = first;
				}

				out.add( new Range( first, last ) );
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

		return out;
	}


	/**
	 * Utility method to throw an exception encountered while reading and parsing
	 * data files.
	 * 
	 * @param aException
	 *        The exception caught
	 * @param line
	 * @throws XcodeException
	 */
	private static void handleException ( Throwable aException, String line )
			throws XcodeException {
		final String formattedLine = ": \"" + line + "\"";
		throw XcodeError.INVALID_FILE_FORMAT( aException.getMessage()
				+ formattedLine );
	}
}
