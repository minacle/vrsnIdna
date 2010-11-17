/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

/**
 * Statically implements various operations surrounding UTF-16 codepoints.
 */
public class Utf16 {

	/**
	 * Constants
	 */
	static public final char HYPHEN = 0x002d;


	/**
	 * Contract a 16 bit sequence into 8 bits. <br>
	 * Used when retrieving a native encoding from a String object. <br>
	 * example: abcd, efgh, -> cd, gh
	 * 
	 * @param aInput
	 *        A char array
	 * @return output A byte array matching low bytes of the input.
	 */
	static public byte[] contract ( char[] aInput ) {
		final byte[] output = new byte[aInput.length];
		for ( int i = 0; i < aInput.length; i++ ) {
			output[ i ] = (byte) (aInput[ i ] & 0xff);
		}
		return output;
	}


	/**
	 * Contract a 32 bit sequence into 16 bits. <br>
	 * Used when the input to Ace.encode() does not require encoding. <br>
	 * example: abcdefgh, jklmnopq -> efgh, nopq
	 * 
	 * @param aInput
	 *        An integer array
	 * @return output A char array matching low chars of the input
	 */
	static public char[] contract ( int[] aInput ) {
		final char[] output = new char[aInput.length];
		for ( int i = 0; i < aInput.length; i++ ) {
			output[ i ] = (char) (aInput[ i ] & 0xffff);
		}
		return output;
	}


	/**
	 * Expand a 8 bit sequence into 16 bits. <br>
	 * Used to create a String object from a native encoding. <br>
	 * example: ab, cd, -> 00ab, 00cd
	 * 
	 * @param aInput
	 *        A byte array
	 * @return output A char array matching low bytes of the input
	 */
	static public char[] expand ( byte[] aInput ) {
		final char[] output = new char[aInput.length];
		for ( int i = 0; i < aInput.length; i++ ) {
			output[ i ] = (char) (aInput[ i ] & 0xff);
		}
		return output;
	}


	/**
	 * Expand a 16 bit sequence into 32 bits. <br>
	 * Used when the input to Ace.decode does not have a prefix. <br>
	 * Also used in Idna.toUnicode() on any Exception, to return the input. <br>
	 * example: abcd, efgh -> 0000abcd, 0000efgh
	 * 
	 * @param aInput
	 *        A char array
	 * @return output An integer array matching low chars of the input
	 */
	static public int[] expand ( char[] aInput ) {
		final int[] output = new int[aInput.length];
		for ( int i = 0; i < aInput.length; i++ ) {
			output[ i ] = aInput[ i ];
		}
		return output;
	}


	/**
	 * Get high 8 bits of a character
	 * 
	 * @param aInput
	 *        a character
	 */
	static public byte getHighByte ( char aInput ) {
		return (byte) (aInput >> 8);
	}


	/**
	 * Get low 8 bits of a character
	 * 
	 * @param aInput
	 *        a character
	 */
	static public byte getLowByte ( char aInput ) {
		return (byte) aInput;
	}


	/**
	 * Check if a character an ASCII character
	 * 
	 * @param aChar
	 *        a character
	 * @return true if the character is an ASCII character, otherwise false
	 */
	static public boolean isAscii ( char aChar ) {
		return 0x00 <= aChar && aChar < 0x80;
	}


	/**
	 * Check if a character array is an ASCII array
	 * 
	 * @param aCharArray
	 *        a character array
	 * @return true if all characters in the array are ASCII character, otherwise
	 *         false.
	 */
	static public boolean isAscii ( char[] aCharArray ) {
		final int length = aCharArray.length;
		for ( int i = 0; i < length; i++ ) {
			if ( !isAscii( aCharArray[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if an int is an ASCII character
	 * 
	 * @param aCodePoint
	 *        an int
	 * @return true if the int is a ASCII character, otherwise false.
	 */
	static public boolean isAscii ( int aCodePoint ) {
		return 0x00 <= aCodePoint && aCodePoint < 0x80;
	}


	/**
	 * Check if an int array is an ASCII array
	 * 
	 * @param aCodePoints
	 *        an int array
	 * @return true if all ints in the array are ASCII character, otherwise false.
	 */
	static public boolean isAscii ( int[] aCodePoints ) {
		final int length = aCodePoints.length;
		for ( int i = 0; i < length; i++ ) {
			if ( !isAscii( aCodePoints[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if the input character is DNS compatible. This method returns true if
	 * the input character is a letter, digit, or hyphen.
	 * 
	 * @param aChar
	 *        a character
	 * @return true if the character is DNS compatible, otherwise false
	 */
	static public boolean isDnsCompatible ( char aChar ) {
		return aChar == 0x002D || aChar >= 0x0030 && aChar <= 0x0039
				|| aChar >= 0x0041 && aChar <= 0x005A || aChar >= 0x0061
				&& aChar <= 0x007A;
	}


	/**
	 * Check if all characters in an input array are all DNS compatible. Ace uses
	 * this to determine if a label must be encoded
	 * 
	 * @param aCharArray
	 *        character array
	 * @return true if and only if all characters in the array are DNS compatible
	 */
	static public boolean isDnsCompatible ( char[] aCharArray ) {
		final int length = aCharArray.length;
		if ( length <= 0 ) {
			return true;
		}
		if ( aCharArray[ 0 ] == HYPHEN || aCharArray[ length - 1 ] == HYPHEN ) {
			return false;
		}
		for ( int i = 0; i < length; i++ ) {
			if ( !isDnsCompatible( aCharArray[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if the input codepoint is DNS compatible. This method returns true if
	 * the input character is a letter, digit, or hyphen.
	 * 
	 * @param aCodePoint
	 *        an integer
	 * @return true if the codepoint is DNS compatible, otherwise false
	 */
	static public boolean isDnsCompatible ( int aCodePoint ) {
		return aCodePoint == 0x002D || aCodePoint >= 0x0030 && aCodePoint <= 0x0039
				|| aCodePoint >= 0x0041 && aCodePoint <= 0x005A || aCodePoint >= 0x0061
				&& aCodePoint <= 0x007A;
	}


	/**
	 * Check if all ints in an input array are all DNS compatible. Ace uses this
	 * to determine if a label must be encoded
	 * 
	 * @param aCodePoints
	 *        a int array
	 * @return true if all ints in the array are DNS compatible, otherwise false
	 */
	static public boolean isDnsCompatible ( int[] aCodePoints ) {
		final int length = aCodePoints.length;
		if ( length <= 0 ) {
			return true;
		}
		if ( aCodePoints[ 0 ] == HYPHEN || aCodePoints[ length - 1 ] == HYPHEN ) {
			return false;
		}
		for ( int i = 0; i < length; i++ ) {
			if ( !isDnsCompatible( aCodePoints[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check a character is a high surrogate character
	 * 
	 * @param aInput
	 *        a character to be checked
	 */
	static public boolean isHighSurrogate ( char aInput ) {
		return aInput >= 0xd800 && aInput < 0xdc00;
	}


	/**
	 * Check a character is a low surrogate character
	 * 
	 * @param aInput
	 *        a character to be checked
	 */
	static public boolean isLowSurrogate ( char aInput ) {
		return aInput >= 0xdc00 && aInput < 0xe000;
	}


	/**
	 * Check if a character could be part of a native encoding.
	 * 
	 * @param aChar
	 *        a character
	 * @return true if the high byte is 0x00, false otherwise
	 */
	static public boolean isNative ( char aChar ) {
		return 0x00 <= aChar && aChar < 0x100;
	}


	/**
	 * Check if a character array could be a native encoding.
	 * 
	 * @param aCharArray
	 *        a character array
	 * @return true if all high bytes are 0x00, false otherwise
	 */
	static public boolean isNative ( char[] aCharArray ) {
		final int length = aCharArray.length;
		for ( int i = 0; i < length; i++ ) {
			if ( !isNative( aCharArray[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if an input character is printable (c > 0x1f && c < 0x7f) || (c >
	 * 0xA0 && c < 0x100)
	 * 
	 * @param aChar
	 *        a character
	 * @return true if the character is printable, false otherwise
	 */
	static public boolean isPrintable ( char aChar ) {
		return aChar > 0x1f && aChar < 0x7f || aChar > 0xA0 && aChar < 0x100;
	}


	/**
	 * Check if all characters in an input array are printable.
	 * 
	 * @param aCharArray
	 *        character array
	 * @return true if all characters in the array are printable, false otherwise
	 */
	static public boolean isPrintable ( char[] aCharArray ) {
		for ( int i = 0; i < aCharArray.length; i++ ) {
			if ( !isPrintable( aCharArray[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if an input integer is printable (c > 0x1f && c < 0x7f) || (c > 0xA0
	 * && c < 0x100)
	 * 
	 * @param aCodePoint
	 *        an integer
	 * @return true if the integer is printable, false otherwise
	 */
	static public boolean isPrintable ( int aCodePoint ) {
		return aCodePoint > 0x1f && aCodePoint < 0x7f || aCodePoint > 0xA0
				&& aCodePoint < 0x100;
	}


	/**
	 * Check if all integers in an input array are printable.
	 * 
	 * @param aCodePoints
	 *        integer array
	 * @return true if all integers in the array are printable, false otherwise
	 */
	static public boolean isPrintable ( int[] aCodePoints ) {
		for ( int i = 0; i < aCodePoints.length; i++ ) {
			if ( !isPrintable( aCodePoints[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if an input character is standard 3 ASCII character, required by
	 * IDNA.toAscii step #3
	 * 
	 * @param aCharArray
	 *        a character array
	 * @return true if the characters are all standard 3 ASCII character,
	 *         otherwise false
	 */
	static public boolean isStd3Ascii ( char[] aCharArray ) {
		if ( aCharArray.length <= 0 ) {
			return true;
		}
		if ( aCharArray[ 0 ] == HYPHEN
				|| aCharArray[ aCharArray.length - 1 ] == HYPHEN ) {
			return false;
		}
		for ( int i = 0; i < aCharArray.length; i++ ) {
			if ( isAscii( aCharArray[ i ] ) && !isDnsCompatible( aCharArray[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Check if an input int is standard 3 ASCII int, required by IDNA.toAscii
	 * step #3
	 * 
	 * @param aCodePoints
	 *        an int
	 * @return true if the int is standard 3 ASCII character, otherwise false
	 */
	static public boolean isStd3Ascii ( int[] aCodePoints ) {
		if ( aCodePoints.length <= 0 ) {
			return true;
		}
		if ( aCodePoints[ 0 ] == HYPHEN
				|| aCodePoints[ aCodePoints.length - 1 ] == HYPHEN ) {
			return false;
		}
		for ( int i = 0; i < aCodePoints.length; i++ ) {
			if ( isAscii( aCodePoints[ i ] ) && !isDnsCompatible( aCodePoints[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * The Unicode string MUST NOT contain "--" (two consecutive hyphens) in the
	 * third and fourth character positions and MUST NOT start or end with a "-"
	 * (hyphen).
	 * 
	 * @param aCodePoints
	 *        int array to check for hyphen restrictions
	 * @return <tt>true</tt> if the code points contain hyphens in first/last or
	 *         3rd/4th position.
	 */
	static public boolean hasHyphenRestrictions ( int[] aCodePoints ) {
		if ( aCodePoints.length <= 0 ) {
			return false;
		}
		if ( aCodePoints[ 0 ] == HYPHEN
				|| aCodePoints[ aCodePoints.length - 1 ] == HYPHEN ) {
			return true;
		}
		if ( aCodePoints.length > 3 ) {
			if ( aCodePoints[ 2 ] == HYPHEN && aCodePoints[ 3 ] == HYPHEN ) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Check a character is a surrogate character
	 * 
	 * @param aInput
	 *        a character to be checked
	 */
	static public boolean isSurrogate ( char aInput ) {
		return isHighSurrogate( aInput ) || isLowSurrogate( aInput );
	}

}