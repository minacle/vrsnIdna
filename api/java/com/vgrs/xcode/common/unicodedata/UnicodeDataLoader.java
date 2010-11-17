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

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Iterator;

import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.idna.Normalize;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class reads and parses the information in the
 * "data/unicode/UnicodeData.txt.gz" file. This file contains all the code
 * points assigned by Unicode. A number of fields are read and parsed along with
 * the code point. This information is then used to implement the IDNA2008
 * Protocol and also by the [@link Normalize} class to perform NFKC
 * normalization as required by the IDNA2008 Protocol. The general category and
 * the BIDI class is also stored for each assigned code point.
 * 
 * @author nchigurupati
 * @version 1.0 Jun 14, 2010
 */
public final class UnicodeDataLoader {

	/**
	 * Set containing the assigned Unicode code points
	 */
	private static TIntSet UNICODE_DATA_POINTS = new TIntHashSet();

	/**
	 * Set containing code points read from
	 * "data/unicode/CompositionExclusions.txt.gz" file.
	 */
	private static final TIntSet EXCLUDED_TABLE = new TIntHashSet();

	/**
	 * Set containing the compatible code points for each of the assigned Unicode
	 * code points.
	 */
	private static final TIntSet COMPATIBILITY_TABLE = new TIntHashSet();

	/**
	 * A map containing the assigned Unicode code point and it's canonical
	 * equivalent
	 */
	private static final TIntIntMap CANONICAL_CLASS_TABLE = new TIntIntHashMap();

	/**
	 * A map containing the assigned Unicode code point and it's composition
	 * characters used during the composition phase of the NFC normalization
	 * process.
	 */
	private static final TLongIntMap COMPOSE_TABLE = new TLongIntHashMap();

	/**
	 * A map containing the assigned Unicode code point and it's decomposition
	 * characters used during the decomposition phase of the NFC normalization
	 * process.
	 */
	private static final TIntObjectMap<int[]> DECOMPOSE_TABLE =
			new TIntObjectHashMap<int[]>();

	/**
	 * A map containing the assigned Unicode code point and it's general category
	 */
	private static final TIntObjectMap<String> GENERAL_CATEGORY_TABLE =
			new TIntObjectHashMap<String>();

	/**
	 * A map containing the assigned Unicode code point and it's BIDI class
	 */
	private static final TIntObjectMap<String> BIDI_CLASS_TABLE =
			new TIntObjectHashMap<String>();

	static {
		try {
			init();
		}
		catch ( final XcodeException e ) {
			throw new RuntimeException( e.getMessage() );
		}
	}


	/**
	 * Load data from "data/unicode/CompositionExclusions.txt.gz" and
	 * "data/unicode/UnicodeData.txt.gz" files.
	 * 
	 * @throws XcodeException
	 */
	private static void init () throws XcodeException {
		loadExclusionChars();
		loadUnicodeData();
		loadHangulDecompositions();
	}


	/**
	 * Load data from "data/unicode/CompositionExclusions.txt.gz" file.
	 */
	static private void loadExclusionChars () throws XcodeException {
		UnicodeDataFileUtil.loadCodePointSet(
				UnicodeDataConstants.COMPOSITION_EXCLUSIONS_DATA, EXCLUDED_TABLE,
				UnicodeDataConstants.TAB_DELIMITER );
	}


	/**
	 * Load data from "data/unicode/UnicodeData.txt.gz" file into various data
	 * structures to be used by other classes that implement the IDNA2008
	 * Protocol.
	 * 
	 * @throws XcodeException
	 */
	private static void loadUnicodeData () throws XcodeException {

		Iterator<String> reader = null;
		String line = null;
		String token = null;
		String codePoint = null;
		int[] mapseq = null;
		int unicodeChar = -1;
		int canonicalClass = -1;
		int semicolonIndx = -1;
		int nextSemicolonIndx = -1;
		int gtIndex = -1;
		int first = 0;
		int second = 0;
		long pair = -1;

		/**
		 * A few example lines from UnicodeData.txt are shown below: This is an
		 * example of one code point
		 * 
		 * <pre> 0041;LATIN  CAPITAL LETTER A;Lu;0;L;;;;;N;;;;0061;
		 * 
		 * <pre>
		 * This is a range of code points..First --> begin of range , Last --> end of range
		 * 3400;<CJK Ideograph Extension A, First>;Lo;0;L;;;;;N;;;;;
		 * 4DB5;<CJK Ideograph Extension A, Last>;Lo;0;L;;;;;N;;;;;
		 */

		try {
			reader = Datafile.getIterator( UnicodeDataConstants.UNICODE_DATA );

			while ( reader.hasNext() ) {

				boolean fCompat = false;

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

				//
				// each line consists of strings(tokens) which are seperated by
				// semi-colons
				//

				semicolonIndx = line.indexOf( ';' );
				if ( semicolonIndx == -1 ) {
					continue; // skip blank line
				}

				//
				// token 0: codepoint
				//
				if ( semicolonIndx != 0 ) {
					codePoint = line.substring( 0, semicolonIndx );

					if ( isEmpty( codePoint ) ) {
						continue;
					}

					unicodeChar = Integer.parseInt( codePoint, 16 );
					UNICODE_DATA_POINTS.add( unicodeChar );
				}

				//
				// token 1: Name
				//

				nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
				if ( nextSemicolonIndx == -1 ) {
					continue;
				}

				if ( nextSemicolonIndx - semicolonIndx > 1 ) {
					token = line.substring( semicolonIndx + 1, nextSemicolonIndx );
					if ( !isEmpty( token ) ) {
						if ( token.indexOf( ", First" ) != -1 ) {
							// handle ranges in the file UnicodeData.txt
							handleUnicodeDataRanges( line, reader );
						}

					}
				}

				//
				// token 2: General_Category
				//
				semicolonIndx = nextSemicolonIndx;
				nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
				if ( nextSemicolonIndx == -1 ) {
					continue;
				}

				if ( nextSemicolonIndx - semicolonIndx > 1 ) {
					token = line.substring( semicolonIndx + 1, nextSemicolonIndx );
					if ( !isEmpty( token ) ) {
						GENERAL_CATEGORY_TABLE.put( unicodeChar, token );
					}

				}

				//
				// token 3: canonical combining class
				//
				semicolonIndx = nextSemicolonIndx;
				nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
				if ( nextSemicolonIndx == -1 ) {
					continue;
				}

				if ( nextSemicolonIndx - semicolonIndx > 1 ) {
					token = line.substring( semicolonIndx + 1, nextSemicolonIndx );

					if ( !isEmpty( token ) ) {

						canonicalClass = Integer.parseInt( token );

						//
						// check if canocical class is within 0 - 255
						//
						if ( canonicalClass != (canonicalClass & 0xFF) ) {
							throw XcodeError.NORMALIZE_BAD_CANONICALCLASS_ERROR();
						}
						CANONICAL_CLASS_TABLE.put( unicodeChar, canonicalClass );
					}
				}

				//
				// token 4: bidirectional "Bidi_Class"
				//
				semicolonIndx = nextSemicolonIndx;
				nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
				if ( nextSemicolonIndx == -1 ) {
					continue;
				}

				if ( nextSemicolonIndx - semicolonIndx > 1 ) {
					token = line.substring( semicolonIndx + 1, nextSemicolonIndx );

					if ( !isEmpty( token ) ) {
						BIDI_CLASS_TABLE.put( unicodeChar, token );
					}
				}

				//
				// token 5: decomposition, Decomposition_Type in angle brackets and
				// Decomposition_Mapping
				//
				semicolonIndx = nextSemicolonIndx;
				nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
				if ( nextSemicolonIndx == -1 ) {
					continue;
				}

				if ( nextSemicolonIndx - semicolonIndx > 1 ) {
					token = line.substring( semicolonIndx + 1, nextSemicolonIndx );

					if ( token.charAt( 0 ) == '<' ) {

						COMPATIBILITY_TABLE.add( unicodeChar );

						gtIndex = token.indexOf( '>' );
						if ( gtIndex == -1 ) {
							throw XcodeError.NORMALIZE_BAD_COMPATTAG_ERROR();
						}

						token = token.substring( gtIndex + 2 );
						fCompat = true;
					}

					//
					// process mapping sequence
					//
					mapseq = Hex.decodeInts( token );

					//
					// sanity check: all decomposition must be singles or pairs
					//
					if ( mapseq.length < 1 || mapseq.length > 2 && !fCompat ) {
						throw XcodeError.NORMALIZE_BAD_DECOMPSEQUENCE_ERROR();
					}

					DECOMPOSE_TABLE.put( unicodeChar, mapseq );

					//
					// store composition pairs
					//
					if ( !fCompat && !EXCLUDED_TABLE.contains( unicodeChar ) ) {
						if ( mapseq.length > 1 ) {
							first = mapseq[ 0 ];
							second = mapseq[ 1 ];
						}
						else {
							first = 0;
							second = mapseq[ 0 ];
						}
						pair = (long) first << 32 | second;

						// The key is a construction of the first two values in
						// mapseq
						COMPOSE_TABLE.put( pair, unicodeChar );

					}
				}

			} // END while()
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


	/**
	 * For backward compatibility, ranges in the file UnicodeData.txt are
	 * specified by entries for the start and end characters of the range, rather
	 * than by the form "X..Y". The start character is indicated by a range
	 * identifier, followed by a comma and the string "First", in angle brackets.
	 * This entry takes the place of a regular character name in field 1 for that
	 * line. The end character is indicated on the next line with the same range
	 * identifier, followed by a comma and the string "Last", in angle brackets
	 * (example below):
	 * <p>
	 * 4E00;<CJK Ideograph, First>;Lo;0;L;;;;;N;;;;; 9FC3;
	 * <p>
	 * <CJK Ideograph, Last>;Lo;0;L;;;;;N;;;;;
	 */
	private static void handleUnicodeDataRanges ( String aFirstTokenRangeLine,
			Iterator<String> aReader ) throws XcodeException {

		String line = null;
		String token = null;
		String codePoint = null;
		String codePointLast = null;
		int[] mapseq = null;
		final int unicodeChar = -1;
		int canonicalClass = -1;
		int semicolonIndx = -1;
		int semicolonIndxLast = -1;

		int nextSemicolonIndx = -1;
		int gtIndex = -1;
		int first = 0;
		int second = 0;
		long pair = -1;
		String input;
		boolean fCompat = false;
		int[] input_decoded = null;
		String lastTokenRangeLine = null;

		while ( aReader.hasNext() ) {

			lastTokenRangeLine = aReader.next();
			break;
		}

		if ( lastTokenRangeLine.indexOf( ", Last" ) == -1 ) {
			return;
		}

		line = aFirstTokenRangeLine;
		//
		// each line consists of strings(tokens) which are seperated by
		// semi-colons
		//

		semicolonIndx = line.indexOf( ';' );
		if ( semicolonIndx == -1 ) {
			return; // skip blank line
		}

		//
		// token 0: codepoint
		//
		if ( semicolonIndx != 0 ) {
			codePoint = line.substring( 0, semicolonIndx );

			if ( isEmpty( codePoint ) ) {
				return;
			}

			semicolonIndxLast = lastTokenRangeLine.indexOf( ';' );
			if ( semicolonIndxLast == -1 ) {
				return; // skip blank line
			}

			if ( semicolonIndxLast != 0 ) {
				codePointLast = lastTokenRangeLine.substring( 0, semicolonIndxLast );
				input = codePoint + " " + codePointLast;

				try {
					input_decoded = Hex.decodeInts( input );
				}
				catch ( final XcodeException x ) {
					input_decoded = Unicode.encode( input.toCharArray() );
				}
			}

			if ( input_decoded.length > 1 ) {
				for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
					UNICODE_DATA_POINTS.add( j );
				}
			}
		}

		//
		// token 1: Name
		//

		nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
		if ( nextSemicolonIndx == -1 ) {
			return;
		}

		//
		// token 2: General_Category
		//
		semicolonIndx = nextSemicolonIndx;
		nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
		if ( nextSemicolonIndx == -1 ) {
			return;
		}

		if ( nextSemicolonIndx - semicolonIndx > 1 ) {
			token = line.substring( semicolonIndx + 1, nextSemicolonIndx );
			if ( !isEmpty( token ) ) {
				if ( input_decoded.length > 1 ) {
					for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
						GENERAL_CATEGORY_TABLE.put( j, token );
					}
				}
			}

		}

		//
		// token 3: canonical combining class
		//
		semicolonIndx = nextSemicolonIndx;
		nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
		if ( nextSemicolonIndx == -1 ) {
			return;
		}

		if ( nextSemicolonIndx - semicolonIndx > 1 ) {
			token = line.substring( semicolonIndx + 1, nextSemicolonIndx );

			if ( !isEmpty( token ) ) {

				canonicalClass = Integer.parseInt( token );

				//
				// check if canocical class is within 0 - 255
				//
				if ( canonicalClass != (canonicalClass & 0xFF) ) {
					throw XcodeError.NORMALIZE_BAD_CANONICALCLASS_ERROR();
				}

				if ( input_decoded.length > 1 ) {
					for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
						CANONICAL_CLASS_TABLE.put( j, canonicalClass );
					}
				}
			}
		}

		//
		// token 4: bidirectional "Bidi_Class"
		//
		semicolonIndx = nextSemicolonIndx;
		nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
		if ( nextSemicolonIndx == -1 ) {
			return;
		}

		if ( nextSemicolonIndx - semicolonIndx > 1 ) {
			token = line.substring( semicolonIndx + 1, nextSemicolonIndx );

			if ( !isEmpty( token ) ) {
				if ( input_decoded.length > 1 ) {
					for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
						BIDI_CLASS_TABLE.put( j, token );
					}
				}
			}
		}

		//
		// token 5: decomposition, Decomposition_Type in angle brackets and
		// Decomposition_Mapping
		//
		semicolonIndx = nextSemicolonIndx;
		nextSemicolonIndx = line.indexOf( ';', semicolonIndx + 1 );
		if ( nextSemicolonIndx == -1 ) {
			return;
		}

		if ( nextSemicolonIndx - semicolonIndx > 1 ) {
			token = line.substring( semicolonIndx + 1, nextSemicolonIndx );

			if ( token.charAt( 0 ) == '<' ) {

				if ( input_decoded.length > 1 ) {
					for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
						COMPATIBILITY_TABLE.add( j );
					}
				}

				gtIndex = token.indexOf( '>' );
				if ( gtIndex == -1 ) {
					throw XcodeError.NORMALIZE_BAD_COMPATTAG_ERROR();
				}

				token = token.substring( gtIndex + 2 );
				fCompat = true;
			}

			//
			// process mapping sequence
			//
			mapseq = Hex.decodeInts( token );

			//
			// sanity check: all decomposition must be singles or pairs
			//
			if ( mapseq.length < 1 || mapseq.length > 2 && !fCompat ) {
				throw XcodeError.NORMALIZE_BAD_DECOMPSEQUENCE_ERROR();
			}

			if ( input_decoded.length > 1 ) {
				for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
					DECOMPOSE_TABLE.put( j, mapseq );
				}
			}

			//
			// store composition pairs
			//
			if ( !fCompat && !EXCLUDED_TABLE.contains( unicodeChar ) ) {
				if ( mapseq.length > 1 ) {
					first = mapseq[ 0 ];
					second = mapseq[ 1 ];
				}
				else {
					first = 0;
					second = mapseq[ 0 ];
				}
				pair = (long) first << 32 | second;

				// The key is a construction of the first two values in
				// mapseq

				if ( input_decoded.length > 1 ) {
					for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
						COMPOSE_TABLE.put( pair, j );
					}
				}

			}
		}

	}


	/**
	 * Load Hangul decompositions to be used by {@link Normalize} class
	 */
	private static void loadHangulDecompositions () {

		int[] mapseq = null;
		int unicodeChar = -1;
		int first = 0;
		int second = 0;
		long pair = -1;
		int sIndex = -1;
		int tIndex = -1;
		int nCount = -1;
		int sCount = -1;

		//
		// Hangul decompositions
		//
		nCount = UnicodeDataConstants.V_COUNT * UnicodeDataConstants.T_COUNT; // 588
		sCount = UnicodeDataConstants.L_COUNT * nCount; // 11172
		for ( sIndex = 0; sIndex < sCount; ++sIndex ) {
			tIndex = sIndex % UnicodeDataConstants.T_COUNT;

			if ( tIndex != 0 ) { // triple
				first = UnicodeDataConstants.S_BASE + sIndex - tIndex;
				second = UnicodeDataConstants.T_BASE + tIndex;
			}
			else {
				first = UnicodeDataConstants.L_BASE + sIndex / nCount;
				second =
						UnicodeDataConstants.V_BASE + sIndex % nCount
								/ UnicodeDataConstants.T_COUNT;
			}

			mapseq = new int[2];
			mapseq[ 0 ] = first;
			mapseq[ 1 ] = second;

			pair = (long) first << 32 | second;

			unicodeChar = sIndex + UnicodeDataConstants.S_BASE;

			DECOMPOSE_TABLE.put( unicodeChar, mapseq );
			COMPOSE_TABLE.put( pair, unicodeChar );

		} // END for()

	}


	private static boolean isEmpty ( String aToken ) {
		if ( null == aToken || aToken.trim().isEmpty() ) {
			return true;
		}
		return false;
	}


	/**
	 * Returns the unicodeDataPoints
	 * 
	 * @return the unicodeDataPoints
	 */
	protected static TIntSet getUnicodeDataPoints () {
		return UNICODE_DATA_POINTS;
	}


	/**
	 * Returns the excludedTable
	 * 
	 * @return the excludedTable
	 */
	protected static TIntSet getExcludedTable () {
		return EXCLUDED_TABLE;
	}


	/**
	 * Returns the compatibilityTable
	 * 
	 * @return the compatibilityTable
	 */
	protected static TIntSet getCompatibilityTable () {
		return COMPATIBILITY_TABLE;
	}


	/**
	 * Returns the canonicalClassTable
	 * 
	 * @return the canonicalClassTable
	 */
	protected static TIntIntMap getCanonicalClassTable () {
		return CANONICAL_CLASS_TABLE;
	}


	/**
	 * Returns the composeTable
	 * 
	 * @return the composeTable
	 */
	protected static TLongIntMap getComposeTable () {
		return COMPOSE_TABLE;
	}


	/**
	 * Returns the decomposeTable
	 * 
	 * @return the decomposeTable
	 */
	protected static TIntObjectMap<int[]> getDecomposeTable () {
		return DECOMPOSE_TABLE;
	}


	/**
	 * Returns the generalCategoryTable
	 * 
	 * @return the generalCategoryTable
	 */
	protected static TIntObjectMap<String> getGeneralCategoryTable () {
		return GENERAL_CATEGORY_TABLE;
	}


	/**
	 * Returns the bidiClassTable
	 * 
	 * @return the bidiClassTable
	 */
	protected static TIntObjectMap<String> getBidiClassTable () {
		return BIDI_CLASS_TABLE;
	}


	/**
	 * Once the derived property of each Unicode code point is calculated, the
	 * UNICODE_DATA_POINTS data structure is no longer needed. Null out this data
	 * structure to free up memory.
	 */
	protected static void nullifyUnicodeDataPointsTable () {
		UNICODE_DATA_POINTS = null;
	}
}
