/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.idna;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.StringTokenizer;

import com.vgrs.xcode.common.UnicodeTokenizer;
import com.vgrs.xcode.common.Utf16;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * An abstract class implementing logic common to all ASCII Compatible
 * Encodings.
 */
public abstract class Ace {

	static public final boolean DEFAULT_USE_STD_3_ASCII_RULES = true;

	/**
	 * Prefix for this Ace
	 */
	private final String prefix;

	/**
	 * A flag indicating whether the validation algorithm will use the STD 3 ASCII
	 * Rules. These rules assert that an IDN contain only letter, digits and
	 * hyphens. They also assert that IDNs must not begin or end with a hyphen
	 * character. If the value of USE_STD_3_ASCII_RULES is <i>false </i>, then
	 * these rules are ignored. <br>
	 */
	private final boolean useStd3AsciiRules;


	/**
	 * @param aPrefix
	 *        Prefix for this Ace
	 * @param aUseStd3AsciiRules
	 *        boolean indicating if only letters, digits and hyphens are allowed
	 *        in the IDN.
	 */
	public Ace ( String aPrefix, boolean aUseStd3AsciiRules ) {
		this.prefix = aPrefix;
		this.useStd3AsciiRules = aUseStd3AsciiRules;
	}


	/**
	 * Encode an entire domain using the Ace algorithm.
	 * 
	 * @param aInput
	 *        An int array representing a domain name
	 * @return A char array with Ace encoded domain name
	 * @throws XcodeException
	 *         when the input is null or empty or the input contains non-standard
	 *         3 ASCII character or the encoded domain name is empty or longer
	 *         than 63.
	 */
	public char[] domainEncode ( int[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final StringBuilder output = new StringBuilder();
		final UnicodeTokenizer tokens =
				new UnicodeTokenizer( aInput, Idna.INT_DELIMITERS, true );

		for ( final int[] token : tokens ) {
			if ( token.length == 1 && Idna.isDelimiter( token[ 0 ] ) ) {
				output.append( Idna.ACE_DELIMITER );
			}
			else {
				output.append( encode( token ) );
			}
		}

		return output.toString().toCharArray();
	}


	/**
	 * Decode an entire domain using the Ace algorithm.
	 * 
	 * @param aInput
	 *        A char array representing an encoded domain name
	 * @return An int array with unicode codepooints
	 * @throws XcodeException
	 *         when the input is null or empty
	 */
	public int[] domainDecode ( char[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final TIntList output = new TIntArrayList();
		final StringTokenizer tokens =
				new StringTokenizer( new String( aInput ), Idna.DELIMITERS, true );
		String token;
		while ( tokens.hasMoreTokens() ) {
			token = tokens.nextToken();
			if ( Idna.isDelimiter( token ) ) {
				output.add( token.charAt( 0 ) );
			}
			else {
				output.add( decode( token.toCharArray() ) );
			}
		}

		return output.toArray();
	}


	/**
	 * Encode a single domain label using the Ace algorithm.
	 * 
	 * @param aInput
	 *        An int array representing a domain name
	 * @return A char array with Ace encoded domain name
	 * @throws XcodeException
	 *         when the input is null or empty or the input contains non-standard
	 *         3 ASCII character or the encoded domain name is empty or longer
	 *         than 63.
	 */
	public char[] encode ( int[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		// idna draft: ToAscii Step #3
		if ( this.useStd3AsciiRules && !Utf16.isStd3Ascii( aInput ) ) {
			throw XcodeError.ACE_ENCODE_NOT_STD3ASCII();
		}

		// idna draft: ToAscii Step #4
		if ( Utf16.isAscii( aInput ) ) {
			return Utf16.contract( aInput );
		}

		// idna draft: ToAscii Step #5 <updated=2003.09.25/>
		if ( hasPrefix( aInput ) ) {
			throw XcodeError.ACE_ENCODE_PREFIX_FOUND();
		}

		// idna draft: ToAscii Step #6,7
		return new String( this.prefix + new String( internalEncode( aInput ) ) )
				.toCharArray();
	}


	abstract protected char[] internalEncode ( int[] input )
			throws XcodeException;


	/**
	 * Decode a single domain label using the Ace algorithm.
	 * 
	 * @param aInput
	 *        A char array representing an encoded domain name
	 * @return An int array with unicode codepooints
	 * @throws XcodeException
	 *         when the input is null or empty
	 */
	public int[] decode ( final char[] aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		// Decarations
		int[] output = null;
		final String inputString = new String( aInput );

		// idna draft: ToUnicode Step #3-5
		if ( aInput.length > this.prefix.length()
				&& inputString.substring( 0, this.prefix.length() ).equalsIgnoreCase(
						this.prefix ) ) {
			final char[] inputArray =
					inputString.substring( this.prefix.length() ).toCharArray();
			output = internalDecode( inputArray );
		}
		else {
			output = Utf16.expand( aInput );
		}
		if ( this.useStd3AsciiRules && !Utf16.isStd3Ascii( output ) ) {
			throw XcodeError.ACE_DECODE_NOT_STD3ASCII();
		}

		return output;
	}


	/**
	 * Ace specific decoding to be performed on the given input.
	 * 
	 * @param aInput
	 *        a UTF16 character array to be decoded into Unicode code points.
	 * @return decoded Unicode code points
	 * @throws XcodeException
	 */
	abstract protected int[] internalDecode ( char[] aInput )
			throws XcodeException;


	/**
	 * Checks if the given input contains the prefix for this Ace.
	 * 
	 * @param aInput
	 *        an array of Unicode code points.
	 * @return true if the given array of Unicode code points contain the prefix.
	 */
	private boolean hasPrefix ( int[] aInput ) {
		final int length = this.prefix.length();
		if ( aInput.length < length ) {
			return false;
		}
		final int[] input32 = new int[length];
		System.arraycopy( aInput, 0, input32, 0, length );
		final String input16 = new String( Utf16.contract( input32 ) );
		return this.prefix.equalsIgnoreCase( input16 );
	}

}