/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * A class that provides algorithms to encode/decode a UTF 16 to/from Unicode.
 */

public class Unicode {

	/**
	 * This is the maximum value a Unicode codepoint can assume. <br>
	 * This variable is hard-coded with the value 0x10ffff.
	 */
	static public final int MAX = 0x10ffff;

	/**
	 * This is the minumum value a Unicode codepoint can assume. <br>
	 * This variable is hard-coded with the value 0.
	 */
	static public final int MIN = 0;


	/**
	 * Assert that the specified value is a Unicode codepoint
	 * 
	 * @param aInput
	 *        A potential Unicode value
	 * @throws XcodeException
	 *         if the input is not a Unicode codepoint.
	 */
	static public void assertValid ( int aInput ) throws XcodeException {
		if ( !isValid( aInput ) ) {
			throw XcodeError.UNICODE_INVALID_VALUE( " "
					+ Integer.toString( aInput, 16 ) );
		}
	}


	/**
	 * Assert that the specified values are Unicode codepoints
	 * 
	 * @param aInput
	 *        An array of potential Unicode values
	 * @throws XcodeException
	 *         if any input value is not a Unicode codepoint.
	 */
	static public void assertValid ( int[] aInput ) throws XcodeException {
		for ( final int point : aInput ) {
			assertValid( point );
		}
	}


	/**
	 * Assert that the specified values are Unicode codepoints
	 * 
	 * @param aRange
	 *        An array of potential Unicode values contained within the given
	 *        range.
	 * @throws XcodeException
	 *         if any input value is not a Unicode codepoint.
	 */
	static public void assertValid ( Range aRange ) throws XcodeException {
		assertValid( aRange.first );
		assertValid( aRange.last );
	}


	/**
	 * Convert one unicode to two characters
	 * 
	 * @param aInput
	 *        a unicode value
	 * @return A surrogate pair comprised of two 16-bit surrogate values.
	 */
	static public char[] decode ( int aInput ) throws XcodeException {
		if ( aInput < 0x10000 || aInput > 0x10ffff ) {
			throw XcodeError.UNICODE_DECODE_INVALID_VALUE( " "
					+ Integer.toString( aInput, 16 ) );
		}
		final char[] surrogate = new char[2];
		surrogate[ 0 ] = (char) ((aInput - 0x10000 >> 10) + 0xd800);
		surrogate[ 1 ] = (char) ((aInput - 0x10000 & 0x3ff) + 0xdc00);
		return surrogate;
	}


	/**
	 * Decode a sequence of unicode to a character sequence
	 * 
	 * @param aInput
	 *        array of unicode
	 * @return a character sequence
	 * @throws XcodeException
	 *         if the array of unicode is null or empty
	 */

	static public char[] decode ( int[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final char[] tmp = new char[aInput.length * 2];
		char[] output;
		char[] surrogate;
		int output_offset = 0;

		for ( final int element : aInput ) {
			if ( element > 0xFFFF ) {
				surrogate = decode( element );
				tmp[ output_offset++ ] = surrogate[ 0 ];
				tmp[ output_offset++ ] = surrogate[ 1 ];
			}
			else {

				/*
				 * I am removing this check for a valid surrogate pair. In theory,
				 * passing a decomposed surrogate pair into a method that is decomposing
				 * surrogate pairs is a bit cheeky. In practice however, it may be
				 * common to have some data in an int[] that you're not sure about. I'll
				 * allow this to improve user experience, even though it does break the
				 * round trip check of which I do not approve.
				 */

				/*
				 * if (i+1 < input.length && Utf16.isHighSurrogate((char)input[i]) &&
				 * Utf16.isLowSurrogate((char)input[i+1])) { throw
				 * XcodeError.UNICODE_SURROGATE_DECODE_ATTEMPTED(); }
				 */

				tmp[ output_offset++ ] = (char) element;
			}
		}

		if ( output_offset != tmp.length ) {
			output = new char[output_offset];
			System.arraycopy( tmp, 0, output, 0, output_offset );
			// Debug.log(" copied ["+tmp.length+"] to ["+output_offset+"]");
		}
		else {
			output = tmp;
			// Debug.log(" no copy ["+tmp.length+"] = ["+output_offset+"]");
		}

		return output;
	}


	/**
	 * Convert two characters to one unicode.
	 * 
	 * @param aHigh
	 *        The character serves as high 8 bits of a unicode
	 * @param aLow
	 *        The character serves as low 8 bits of a unicode
	 * @return a unicode
	 */
	static public int encode ( char aHigh, char aLow ) {
		return (aHigh - 0xd800 << 10) + aLow - 0xdc00 + 0x10000;
	}


	/**
	 * Encode a character sequence to an array of unicode
	 * 
	 * @param aInput
	 *        a character sequence
	 * @return array of unicode
	 * @throws XcodeException
	 *         if the input array is null or empty
	 */
	static public int[] encode ( char[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final int[] tmp = new int[aInput.length];
		int[] output;
		int output_offset = 0;

		for ( int i = 0; i < aInput.length; i++ ) {
			if ( i + 1 < aInput.length && Utf16.isHighSurrogate( aInput[ i ] )
					&& Utf16.isLowSurrogate( aInput[ i + 1 ] ) ) {
				tmp[ output_offset++ ] = encode( aInput[ i ], aInput[ i + 1 ] );
				i++;
			}
			else {
				tmp[ output_offset++ ] = aInput[ i ];
			}
		}

		if ( output_offset != tmp.length ) {
			output = new int[output_offset];
			System.arraycopy( tmp, 0, output, 0, output_offset );
			// Debug.log(" copied ["+tmp.length+"] to ["+output_offset+"]");
		}
		else {
			output = tmp;
			// Debug.log(" no copy ["+tmp.length+"] = ["+output_offset+"]");
		}

		return output;
	}


	/**
	 * Assert that the specified value is a Unicode codepoint
	 * 
	 * @param aInput
	 *        A potential Unicode value
	 */
	static public boolean isValid ( int aInput ) {
		return aInput >= MIN && aInput <= MAX;
	}

}