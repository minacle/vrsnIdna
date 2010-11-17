/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.idna;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.vgrs.xcode.common.Utf16;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class implements the ASCII-Compatible Encoding (ACE) algorithm Punycode:
 * A Bootstring encoding of Unicode for Internationalized Domain Names in
 * Applications (IDNA). Reference: RFC 3492 Punycode is a simple and efficient
 * transfer encoding syntax designed for use with Internationalized Domain Names
 * in Applications (IDNA). It uniquely and reversibly transforms a Unicode
 * string into an ASCII string. ASCII characters in the Unicode string are
 * represented literally, and non-ASCII characters are represented by ASCII
 * characters that are allowed in host name labels (letters, digits, and
 * hyphens). This document defines a general algorithm called Bootstring that
 * allows a string of basic code points to uniquely represent any string of code
 * points drawn from a larger set. Punycode is an instance of Bootstring that
 * uses particular parameter values specified by this document, appropriate for
 * IDNA. The code below is almost a direct port of the sample implementation in
 * C provided by the author in RFC 3492. NOTE: Punycode makes no effort to
 * convert valid surrogate pairs into an appropriate codepoint outside the BMP.
 * This is by design. However, the implication is that these data can result in
 * different encoded sequences, despite the notion that in some context they are
 * identical.
 * 
 * @author nchigurupati
 * @version 1.0 Aug 3, 2010
 */
public final class Punycode extends Ace {

	/*
	 * This prefix indicates that a domain label has been encoded using Punycode.
	 */
	static public final String DEFAULT_PREFIX = "xn--";

	/*
	 * Bootstring parameters for Punycode algorithm
	 */
	private static final int BASE = 36;

	private static final int T_MIN = 1;

	private static final int T_MAX = 26;

	private static final int SKEW = 38;

	private static final int DAMP = 700;

	private static final int INITIAL_BIAS = 72;

	private static final int INITIAL_N = 0x80;

	private static final int DELIMITER = 0x2D;

	private static final int MAX_INT = Integer.MAX_VALUE;

	private static final int PUNYCODE_MAX_LENGTH = 256;

	private static final int LOBASE = BASE - T_MIN;

	private static final int CUTOFF = LOBASE * T_MAX / 2;

	private static final int MAX_UNICODE = 0x10FFFF;


	/**
	 * Construct Punycode with default values.
	 */
	public Punycode () {
		super( DEFAULT_PREFIX, DEFAULT_USE_STD_3_ASCII_RULES );
	}


	/**
	 * @param aPrefix
	 *        the prefix for Punycode
	 */
	public Punycode ( String aPrefix ) {
		super( aPrefix, DEFAULT_USE_STD_3_ASCII_RULES );
	}


	/**
	 * @param aUseStd3AsciiRules
	 *        A flag indicating whether the validation algorithm will use the STD
	 *        3 ASCII Rules.
	 */
	public Punycode ( boolean aUseStd3AsciiRules ) {
		super( DEFAULT_PREFIX, aUseStd3AsciiRules );
	}


	/**
	 * @param aPrefix
	 *        the prefix for Punycode
	 * @param aUseStd3AsciiRules
	 *        A flag indicating whether the validation algorithm will use the STD
	 *        3 ASCII Rules.
	 */
	public Punycode ( String aPrefix, boolean aUseStd3AsciiRules ) {
		super( aPrefix, aUseStd3AsciiRules );
	}


	/*
	 * By contract with Ace
	 */
	@Override
	protected char[] internalEncode ( int[] aInput ) throws XcodeException {
		return encode( aInput, null );
	}


	/*
	 * By contract with Ace
	 */
	@Override
	protected int[] internalDecode ( char[] input ) throws XcodeException {
		return decode( input, null );
	}


	/**
	 * Checks if the give code is a DELIMITER (0x2D)
	 * 
	 * @param aCodePoint
	 *        the code point to check
	 * @return boolean indicating if the given code point is a DELIMITER
	 */
	private static boolean isDelimiter ( int aCodePoint ) {
		return aCodePoint == DELIMITER;
	}


	private static boolean isFlagged ( int aCodePoint ) {
		return aCodePoint - 65 < 26;
	}


	private static int decodeDigit ( char aDigit ) throws XcodeException {
		if ( aDigit >= 0x30 && aDigit <= 0x39 ) {
			return aDigit - 0x16;
		}
		else if ( aDigit >= 0x41 && aDigit <= 0x5A ) {
			return aDigit - 0x41;
		}
		else if ( aDigit >= 0x61 && aDigit <= 0x7A ) {
			return aDigit - 0x61;
		}
		else {
			throw XcodeError.PUNYCODE_BAD_OUTPUT();
		}
	}


	private static int encodeDigit ( final int aDigit, boolean aUcFlag ) {
		int outDigit = aDigit;

		outDigit += 22;

		if ( aUcFlag ) {
			outDigit -= 32;
		}
		if ( outDigit < 48 ) {
			outDigit += 75;
		}
		return outDigit;
	}


	private static int encodeBasic ( final int bcp, boolean ucFlag ) {

		int outBcp = bcp;

		if ( outBcp - 97 < 26 ) {
			outBcp -= 32;
		}
		if ( !ucFlag && outBcp - 65 < 26 ) {
			outBcp += 32;
		}

		return outBcp;
	}


	private static int adapt ( final int aDelta, final int aCodePointCount,
			boolean aFirstTime ) {
		int outDelta = aDelta;

		outDelta = aFirstTime ? outDelta / DAMP : outDelta >> 1;
		outDelta += outDelta / aCodePointCount;

		int i = 0;
		for ( ; outDelta > CUTOFF; i += BASE ) {
			outDelta /= LOBASE;
		}

		return i + (LOBASE + 1) * outDelta / (outDelta + SKEW);
	}


	private static char[] encode ( int[] aInput, List<Boolean> aUcFlags )
			throws XcodeException {
		int currentLargestCP = 0;
		int delta = 0;
		int cpsHandled = 0;
		int bias = 0;
		int nextLargerCP = 0;
		int currentDelta = 0;
		int currentBase = 0;
		int threshold = 0;
		int basicCPsCount = 0;

		final StringBuffer encodedString = new StringBuffer();

		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		//
		// initialize the state
		//
		currentLargestCP = INITIAL_N;
		bias = INITIAL_BIAS;

		//
		// add all the basic code points to the output string
		//
		for ( int i = 0; i < aInput.length; i++ ) {
			int inputChar = aInput[ i ];
			if ( Utf16.isAscii( inputChar ) ) {
				if ( aUcFlags != null ) {
					final boolean ucFlag = aUcFlags.get( i ).booleanValue();
					inputChar = encodeBasic( inputChar, ucFlag );
				}
				encodedString.append( (char) inputChar );
			}
		}

		basicCPsCount = encodedString.length();
		cpsHandled = basicCPsCount;

		if ( basicCPsCount > 0 ) {
			encodedString.append( (char) DELIMITER );
		}

		//
		// Main encoding loop
		//
		while ( cpsHandled < aInput.length ) {

			//
			// All non-basic code points < n have been
			// handled already. Find the next larger one:
			//
			nextLargerCP = MAX_INT;
			for ( int j = 0; j < aInput.length; ++j ) {
				final int inputChar = aInput[ j ];
				if ( inputChar >= currentLargestCP && inputChar < nextLargerCP ) {
					nextLargerCP = inputChar;
				}
			}

			//
			// Increase delta enough to advance the decoder's
			// <currentLargestCP, i> state to <nextLargerCP, 0>,
			// but guard against overflow:
			//
			if ( nextLargerCP - currentLargestCP > (MAX_INT - delta)
					/ (cpsHandled + 1) ) {
				throw XcodeError.PUNYCODE_OVERFLOW();
			}
			delta += (nextLargerCP - currentLargestCP) * (cpsHandled + 1);
			currentLargestCP = nextLargerCP;

			for ( int j = 0; j < aInput.length; ++j ) {
				final int inputChar = aInput[ j ];

				if ( inputChar < currentLargestCP && ++delta == 0 ) {
					throw XcodeError.PUNYCODE_BIG_OUTPUT();
				}

				if ( inputChar == currentLargestCP ) {

					for ( currentDelta = delta, currentBase = BASE;; currentBase += BASE ) {
						if ( encodedString.length() >= PUNYCODE_MAX_LENGTH ) {
							throw XcodeError.PUNYCODE_BIG_OUTPUT();
						}

						//
						// calculate the threshold
						//
						if ( currentBase <= bias ) {
							threshold = T_MIN;
						}
						else {
							if ( currentBase >= bias + T_MAX ) {
								threshold = T_MAX;
							}
							else {
								threshold = currentBase - bias;
							}
						}

						if ( currentDelta < threshold ) {
							final int outputDelta = currentDelta;

							//
							// determine the current uppercase flag
							//
							boolean ucFlag = false;
							if ( aUcFlags != null ) {
								ucFlag = aUcFlags.get( j ).booleanValue();
							}

							//
							// encode the delta
							//
							final char encodedDelta =
									(char) encodeDigit( outputDelta, ucFlag );

							//
							// append the encoded delta to output
							//
							encodedString.append( encodedDelta );

							break;
						}
						else {
							final int outputDelta =
									threshold + (currentDelta - threshold) % (BASE - threshold);
							final boolean ucFlag = false;

							//
							// encode the delta
							//
							final char encodedDelta =
									(char) encodeDigit( outputDelta, ucFlag );

							//
							// append the encoded delta to output
							//
							encodedString.append( encodedDelta );

							//
							// adjust the delta value for next iteration
							//
							currentDelta = (currentDelta - threshold) / (BASE - threshold);
						}
					}

					//
					// Adapt the bias:
					//
					bias = adapt( delta, cpsHandled + 1, cpsHandled == basicCPsCount );

					delta = 0;
					++cpsHandled;
				}
			}

			++delta;
			++currentLargestCP;
		}

		if ( encodedString.length() > PUNYCODE_MAX_LENGTH ) {
			throw XcodeError.PUNYCODE_BIG_OUTPUT();
		}

		return encodedString.toString().toCharArray();

	} // encode()


	private static int[] decode ( char[] aInput, List<Boolean> aUcFlags )
			throws XcodeException {

		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		if ( isDelimiter( aInput[ aInput.length - 1 ] ) ) {
			throw XcodeError.PUNYCODE_DECODE_DNS_COMPATIBLE();
		}

		final TIntList decoded = new TIntArrayList();

		int decodedVal = 0;
		// int out = 0;
		int opIndx = 0;
		int bias = 0;
		int basicCPsCount = 0;
		int j = 0;
		int indx = 0;
		int oldIndx = 0;
		int weight = 0;
		int currentBase = 0;
		int delta = 0;
		int digit = 0;
		int threshold = 0;

		//
		// Initialize the state:
		//

		decodedVal = INITIAL_N;
		opIndx = 0;
		bias = INITIAL_BIAS;

		//
		// Handle the basic code points: Let b be the number of input code
		// points before the last delimiter, or 0 if there is none, then
		// copy the first b code points to the output.
		//

		for ( basicCPsCount = 0, j = 0; j < aInput.length; j++ ) {
			final char inputChar = aInput[ j ];
			if ( isDelimiter( inputChar ) ) {
				basicCPsCount = j;
			}
		}

		if ( basicCPsCount > PUNYCODE_MAX_LENGTH ) {
			throw XcodeError.PUNYCODE_BIG_OUTPUT();
		}

		for ( j = 0; j < basicCPsCount; ++j ) {
			final char inputChar = aInput[ j ];
			if ( aUcFlags != null ) {
				aUcFlags.add( new Boolean( isFlagged( inputChar ) ) );
			}
			if ( !Utf16.isAscii( inputChar ) ) {
				throw XcodeError.PUNYCODE_BAD_OUTPUT();
			}
			decoded.add( inputChar );
		}

		//
		// Main decoding loop: Start just after the last delimiter if any
		// basic code points were copied; start at the beginning otherwise.
		//

		if ( basicCPsCount > 0 ) {
			indx = basicCPsCount + 1;
		}
		else {
			indx = 0;
		}
		while ( indx < aInput.length ) {

			//
			// indx is the index of the next character to be consumed, and
			// out is the number of code points in the output array.
			//

			//
			// Decode a generalized variable-length integer into delta,
			// which gets added to i. The overflow checking is easier
			// if we increase i as we go, then subtract off its starting
			// value at the end to obtain delta.
			//

			oldIndx = opIndx;
			weight = 1;
			currentBase = BASE;
			for ( ;; currentBase += BASE ) {

				if ( indx >= aInput.length ) {
					throw XcodeError.PUNYCODE_BAD_OUTPUT();
				}

				digit = decodeDigit( aInput[ indx++ ] );
				if ( digit >= BASE ) {
					throw XcodeError.PUNYCODE_BAD_OUTPUT();
				}
				if ( digit > (MAX_INT - opIndx) / weight ) {
					throw XcodeError.PUNYCODE_OVERFLOW();
				}
				opIndx += digit * weight;

				//
				// calculate the threshold
				//
				if ( currentBase <= bias ) {
					threshold = T_MIN;
				}
				else {
					if ( currentBase - bias >= T_MAX ) {
						threshold = T_MAX;
					}
					else {
						threshold = currentBase - bias;
					}
				}

				if ( digit < threshold ) {
					break;
				}
				if ( weight > MAX_INT / (BASE - threshold) ) {
					throw XcodeError.PUNYCODE_OVERFLOW();
				}
				weight *= BASE - threshold;

			}

			//
			// Adapt the bias:
			//
			delta = oldIndx == 0 ? opIndx / DAMP : opIndx - oldIndx >>> 1;
			delta += delta / (decoded.size() + 1);
			for ( bias = 0; delta > CUTOFF; bias += BASE ) {
				delta /= LOBASE;
			}
			bias += (LOBASE + 1) * delta / (delta + SKEW);

			//
			// opIndx was supposed to wrap around from decoded.length()+1 to 0,
			// incrementing n each time, so we'll fix that now:
			//

			if ( opIndx / (decoded.size() + 1) > MAX_INT - decodedVal ) {
				throw XcodeError.PUNYCODE_OVERFLOW();
			}
			decodedVal += opIndx / (decoded.size() + 1);
			opIndx %= decoded.size() + 1;

			if ( decoded.size() >= PUNYCODE_MAX_LENGTH ) {
				throw XcodeError.PUNYCODE_BIG_OUTPUT();
			}

			if ( aUcFlags != null ) {
				//
				// Case of last character determines uppercase flag:
				//
				aUcFlags.add( opIndx, new Boolean( isFlagged( aInput[ indx - 1 ] ) ) );
			}

			//
			// check if the number corresponds to a valid unicode character
			//
			if ( decodedVal > MAX_UNICODE ) {
				throw XcodeError.PUNYCODE_BIG_OUTPUT();
			}

			//
			// Insert decodedVal at position i of the output:
			//
			if ( Idna.isDelimiter( decodedVal ) ) {
				throw XcodeError.PUNYCODE_DECODE_INTERNAL_DELIMITER_FOUND( " "
						+ Integer.toString( decodedVal, 16 ) );
			}
			decoded.insert( opIndx++, decodedVal );
		}

		return decoded.toArray();

	} // decode()
}