/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

import java.util.StringTokenizer;

import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * Base 16 or Hexadecimal is often represented using the digits 0-9 as well as
 * the letters a-f. The decode methods in this class interpret this Hexadecimal
 * notation, converting into usable data structures. The encode methods perform
 * the opposite function, representing internal data using Hexadecimal notation.
 */
public final class Hex {

	/**
	 * Return the Hexadecimal representation of the input sequence.
	 * 
	 * @param aInput
	 *        Sequence of 8 bit bytes
	 * @return Sets of two characters separated by a space. Each group represents
	 *         a single byte from the input stream using hex notation.
	 * @throws XcodeException
	 *         if the input is null or with length == 0
	 */
	static public String encode ( byte[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringBuilder sb = new StringBuilder();
		for ( final byte element : aInput ) {
			sb.append( Integer.toString( (char) element & 0xff, 16 ) );
			sb.append( " " );
		}
		return sb.toString();
	}


	/**
	 * Return the Hexadecimal representation of the input sequence.
	 * 
	 * @param aInput
	 *        Sequence of 16 bit characters in UTF16 format
	 * @return Sets of four characters separated by a space. Each group represents
	 *         a single character from the input stream using hex notation.
	 * @throws XcodeException
	 *         if the input is null or with length == 0
	 */
	static public String encode ( char[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringBuilder sb = new StringBuilder();
		for ( final char element : aInput ) {
			sb.append( Integer.toString( element, 16 ) );
			sb.append( " " );
		}
		return sb.toString();
	}


	/**
	 * Return the Hexadecimal representation of the input sequence.
	 * 
	 * @param aInput
	 *        Sequence of integers representing Unicode characters.
	 * @return Grouped characters separated by a space. Each group represents a
	 *         single Unicode character from the input stream using hex notation.
	 * @throws XcodeException
	 *         if the input is null or with length == 0
	 */
	static public String encode ( int[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringBuilder sb = new StringBuilder();
		for ( final int element : aInput ) {
			sb.append( Integer.toString( element, 16 ) );
			sb.append( " " );
		}
		return sb.toString();
	}


	/**
	 * Return the data structure represented by the Hexadecimal input sequence.
	 * 
	 * @param aInput
	 *        Grouped characters separated by a space. Each group represents a
	 *        single byte using hex notation.
	 * @return Each group of bytes in hex notation is interpreted to create a new
	 *         8 bit Java byte primitive.
	 * @throws XcodeException
	 *         if the input is null or with length == 0 or if length of the input
	 *         indicates the hex value is greater than 0xff
	 */
	static public byte[] decodeBytes ( String aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringTokenizer st = new StringTokenizer( aInput );
		final byte[] output = new byte[st.countTokens()];
		int output_offset = 0;
		String token = null;

		while ( st.hasMoreTokens() ) {
			token = st.nextToken().toUpperCase();
			if ( token.startsWith( "\\U" ) || token.startsWith( "0X" ) ) {
				token = token.substring( 2 );
			}
			try {
				if ( token.length() > 2 ) {
					throw XcodeError.HEX_DECODE_ONE_BYTE_EXCEEDED( aInput );
				}
				output[ output_offset++ ] = (byte) Integer.parseInt( token, 16 );
			}
			catch ( final NumberFormatException x ) {
				throw XcodeError.HEX_DECODE_INVALID_FORMAT( aInput );
			}
		}
		return output;
	}


	/**
	 * Return the data structure represented by the Hexadecimal input sequence.
	 * 
	 * @param aInput
	 *        Grouped characters separated by a space. Each group represents a
	 *        single Utf16 character using hex notation.
	 * @return Each group of characters in hex notation is interpreted to create a
	 *         new 16 bit Java character primitive.
	 * @throws XcodeException
	 *         if the input is null or with length == 0 or if length of the input
	 *         indicates the hex value is greater than 0xff
	 */
	static public char[] decodeChars ( String aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringTokenizer st = new StringTokenizer( aInput );
		final char[] output = new char[st.countTokens()];
		int output_offset = 0;
		String token = null;

		while ( st.hasMoreTokens() ) {
			token = st.nextToken().toUpperCase();
			if ( token.startsWith( "\\U" ) || token.startsWith( "0X" ) ) {
				token = token.substring( 2 );
			}
			try {
				if ( token.length() > 4 ) {
					throw XcodeError.HEX_DECODE_TWO_BYTES_EXCEEDED( aInput );
				}
				output[ output_offset++ ] = (char) Integer.parseInt( token, 16 );
			}
			catch ( final NumberFormatException x ) {
				throw XcodeError.HEX_DECODE_INVALID_FORMAT( aInput );
			}
		}
		return output;
	}


	/**
	 * Return the data structure represented by the Hexadecimal input sequence.
	 * 
	 * @param aInput
	 *        Grouped characters separated by a space. Each group represents a
	 *        single Unicode character using hex notation.
	 * @return Each group of characters in hex notation is interpreted as a
	 *         Unicode value resulting in a 21 bit Java int primitive.
	 * @throws XcodeException
	 *         if the input is null or with length == 0 or if length of the input
	 *         indicates the hex value is greater than 0xffffffff
	 */
	static public int[] decodeInts ( String aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringTokenizer st = new StringTokenizer( aInput );
		final int[] output = new int[st.countTokens()];
		int output_offset = 0;
		String token = null;

		while ( st.hasMoreTokens() ) {
			token = st.nextToken().toUpperCase();
			if ( token.startsWith( "\\U" ) || token.startsWith( "0X" ) ) {
				token = token.substring( 2 );
			}
			try {
				if ( token.length() > 8 ) {
					throw XcodeError.HEX_DECODE_FOUR_BYTES_EXCEEDED( aInput );
				}
				output[ output_offset++ ] = Integer.parseInt( token, 16 );
			}
			catch ( final NumberFormatException x ) {
				throw XcodeError.HEX_DECODE_INVALID_FORMAT( aInput );
			}
		}

		return Unicode.encode( Unicode.decode( output ) );
	}

}