/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.idna;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.set.TIntSet;

import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class perform NFKC normalization
 */
public class Normalize {

	/**
	 * Set containing the compatible code points for each of the assigned Unicode
	 * code points.
	 */
	private static TIntSet COMPATIBILITY_TABLE;

	/**
	 * A map containing the assigned Unicode code point and it's canonical
	 * equivalent
	 */
	private static TIntIntMap CANONICAL_CLASS_TABLE;

	/**
	 * A map containing the assigned Unicode code point and it's composition
	 * characters used to during the composition phase of the NFC normalization
	 * process.
	 */
	private static TLongIntMap COMPOSE_TABLE;

	/**
	 * A map containing the assigned Unicode code point and it's decomposition
	 * characters used to during the decomposition phase of the NFC normalization
	 * process.
	 */
	private static TIntObjectMap<int[]> DECOMPOSE_TABLE;

	static {
		try {
			init();
		}
		catch ( final XcodeException x ) {
			throw new RuntimeException( x.getMessage() );
		}
	}


	static private void init () throws XcodeException {
		COMPATIBILITY_TABLE = UnicodeData.getCompatibilityTable();
		CANONICAL_CLASS_TABLE = UnicodeData.getCanonicalClassTable();
		COMPOSE_TABLE = UnicodeData.getComposeTable();
		DECOMPOSE_TABLE = UnicodeData.getDecomposeTable();
	}


	/**
	 * Execute the normalization algorithm
	 * 
	 * @param aInput
	 *        Unicode sequence to normalize
	 * @return Normalized Unicode sequence
	 * @throws XcodeException
	 *         if the input string is null or with length == 0 or the input
	 *         sequence cannot be normalized.
	 */
	static public int[] execute ( int[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		return kcCompose( kcDecompose( aInput ) );

		// int value = UnicodeData.isNormalizationNeeded( input );
		//
		// switch (value) {
		//
		// case UnicodeDataConstants.NORMALIZATION_NEEDED: {
		// return kcCompose( kcDecompose( input ) );
		// }
		//
		// case UnicodeDataConstants.CANNOT_BE_NORMALIZED: {
		// throw XcodeError.NORMALIZE_INVALID_CHARACTER();
		// }
		//
		// default:
		// return input;
		// }

	}


	/**
	 * Form KC decomposition
	 */
	static private int[] kcDecompose ( final int[] aInput ) throws XcodeException {

		final TIntList output = new TIntArrayList();
		int[] buf = null;
		int charI = 0;
		int charJ = 0;
		int cClass = -1;
		int cursor = -1;
		int pCanonicalItem = 0;

		for ( final int element : aInput ) {
			charI = element;

			if ( charI == 0 ) {
				throw XcodeError.NORMALIZE_NULL_CHARACTER_PRESENT();
			}

			buf = doDecomposition( charI, false );

			for ( final int element2 : buf ) {
				charJ = element2;
				if ( CANONICAL_CLASS_TABLE.containsKey( charJ ) ) {
					pCanonicalItem = CANONICAL_CLASS_TABLE.get( charJ );
					cClass = pCanonicalItem;
				}
				else {
					cClass = 0;
				}

				cursor = output.size();

				if ( cClass != 0 ) {
					for ( ; cursor > 0; --cursor ) {
						final int canonicalIndex = output.get( cursor - 1 );
						if ( !CANONICAL_CLASS_TABLE.containsKey( canonicalIndex ) ) {
							throw XcodeError.NORMALIZE_CANONICAL_LOOKUP_ERROR();
						}
						pCanonicalItem = CANONICAL_CLASS_TABLE.get( canonicalIndex );
						if ( pCanonicalItem <= cClass ) {
							break;
						}
					}
				}
				output.insert( cursor, charJ );
			}
		}
		return output.toArray();

	} // END kcDecompose()


	/**
	 * doDecomposition - recursive decomposition for one unicode character
	 */
	static private int[] doDecomposition ( final int aCodePoint,
			boolean aCanonical ) {
		final TIntList output = new TIntArrayList();
		int[] decomposeItem = null;
		boolean compatExists = false;

		decomposeItem = DECOMPOSE_TABLE.get( aCodePoint );
		compatExists = COMPATIBILITY_TABLE.contains( aCodePoint );

		if ( decomposeItem != null && !(aCanonical && compatExists) ) {
			for ( final int element : decomposeItem ) {
				output.add( doDecomposition( element, aCanonical ) );
			}
		}
		else {
			output.add( aCodePoint );
		}

		return output.toArray();

	} // END doDecomposition()


	/**
	 * Form KC recomposition
	 */
	static private int[] kcCompose ( final int[] output ) {
		int startCh = 0;
		int pCanonical = 0;
		int pCompose = 0;
		int lastClass = -1;
		int decompPos = -1;
		int startPos = 0;
		int compPos = 1;

		int[] outputArray = new int[output.length];
		System.arraycopy( output, 0, outputArray, 0, output.length );

		startCh = outputArray[ 0 ];

		if ( CANONICAL_CLASS_TABLE.containsKey( startCh ) ) {
			pCanonical = CANONICAL_CLASS_TABLE.get( startCh );
			if ( pCanonical != 0 ) {
				lastClass = 256;
			}
			else {
				lastClass = 0;
			}
		}
		else {
			lastClass = 0;
		}

		final int outputArrayLength = outputArray.length;
		for ( decompPos = 1; decompPos < outputArrayLength; decompPos++ ) {
			int chClass = -1;
			int composite = -1;
			final int ch = outputArray[ decompPos ];
			long pair = -1;
			if ( CANONICAL_CLASS_TABLE.containsKey( ch ) ) {
				pCanonical = CANONICAL_CLASS_TABLE.get( ch );
				chClass = pCanonical;
			}
			else {
				chClass = 0;
			}

			pair = (long) startCh << 32 | ch;
			if ( COMPOSE_TABLE.containsKey( pair ) ) {
				pCompose = COMPOSE_TABLE.get( pair );
				composite = pCompose;
			}
			else {
				composite = 0xffffffff;
			}

			if ( composite != 0xffffffff && (lastClass < chClass || lastClass == 0) ) {
				outputArray[ startPos ] = composite;
				startCh = composite;
			}
			else {
				if ( chClass == 0 ) {
					startPos = compPos;
					startCh = ch;
				}
				lastClass = chClass;
				outputArray[ compPos++ ] = ch;
			}
		}

		if ( compPos != outputArray.length ) {
			final int[] buf = new int[compPos];
			System.arraycopy( outputArray, 0, buf, 0, compPos );
			outputArray = buf;
		}

		return outputArray;

	} // END kcCompose()

}
