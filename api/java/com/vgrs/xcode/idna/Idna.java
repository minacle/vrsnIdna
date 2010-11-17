/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.idna;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.StringTokenizer;

import com.vgrs.xcode.common.UnicodeTokenizer;
import com.vgrs.xcode.common.Utf16;
import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.idna.contextualrule.ContextualRulesRegistry;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * Implementation of IDNA2008 Protocol
 * <p>
 * Ultimately the following will be true: <br>
 * The Race object uses Utf16 as input and output <br>
 * The Punycode object uses Unicode as input and output <br>
 * <p>
 * toAscii <br>
 * 1. Application has Unicode data U32 <br>
 * 2. Pass the Unicode data to Idna U32 <br>
 * 3. Idna tokenizes the data. *U32 <br>
 * 4. Idna runs Unicode through IDNA2008 Protocol if the registration protocol
 * is specified. U32 <br>
 * 5. Idna encodes the data using an ACE algorithm U32->U16 <br>
 * 6. Idna assembles the labels. U16 <br>
 * 7. Idna passes the Utf16 back to the application. U16 <br>
 * <p>
 * - It is not trivial to tokenize the labels as Unicode. We have written <br>
 * a special object to do so, which allows us to avoid decoding to and <br>
 * from Unicode just for tokenization. <br>
 * <p>
 * toUnicode <br>
 * 1. Application has Utf16 (probably Ascii) string U16 <br>
 * 2. Pass the Utf16 data to Idna. U16 <br>
 * 3. Idna tokenizes the data. U16 <br>
 * 4. Idna encodes the data in Unicode. U32 <-U16 <br>
 * 5. Idna runs Unicode through IDNA2008 Protocol if the registration protocol
 * is specified.<br>
 * 6. Idna decodes the data to Utf-16. U32->U16 <br>
 * 7. Idna decodes the data using an ACE algorithm U32 <-U16 <br>
 * 8. Idna assembles the labels *U32 <br>
 * 9. Idna asserts IDNA2008 Bidi rules if an RTL label is present in the domain
 * if the registration protocol is specified <br>
 * 10. Idna passes the Unicode back to the application. U32 <br>
 * 
 * @author nchigurupati
 * @version 1.0 Jun 17, 2010
 */
public final class Idna {

	/**
	 * Stores the list of IDNA compliant delimiters. <br>
	 * This variable is hard coded to the value <i>{0x2e,0x3002,0xff0e,0xff61}
	 * </i>
	 */
	static public final int[] INT_DELIMITERS = {
			0x2e, 0x3002, 0xff0e, 0xff61
	};

	/**
	 * Stores the list of IDNA compliant delimiters. <br>
	 * This variable is hard coded to the value <i>"\ u 0 0 2 e \ u 3 0 0 2 \ u f
	 * f 0 e \ u f f 6 1" </i>
	 */
	static public final String DELIMITERS = "\u002e\u3002\uff0e\uff61";

	/**
	 * Stores the Unicode character used to delimit domain labels in an ASCII
	 * Compatible Encoding sequence. <br>
	 * This variable is hard coded to the value <i>0x002e </i>.
	 */
	static public final char ACE_DELIMITER = 0x002e;

	/**
	 * RFC 1034 imposes a length restriction of 0 to 63 octets per domain label. <br>
	 * This variable is hard coded to the value <i>63 </i>.
	 */
	static public final int MAX_DOMAIN_LABEL_LENGTH_IN_OCTETS = 63;

	/**
	 * Indicates whether or not to throw XcodeExceptions from the toUnicode
	 * method. Any error condition in the execution of the toUnicode operation
	 * results in the return of the input. This prevents applications from knowing
	 * whether an error occured during the process. The Idna object uses an
	 * internal flag to indicate whether an exception should be thrown if an error
	 * condition occurs. Setting the internal flag to <i>true </i> will allow an
	 * error condition to throw an exception to the calling application. The
	 * application can then decide whether or not to continue execution. <br>
	 * This variable takes the value <i>false </i> by default.
	 */
	static public final boolean DEFAULT_TO_UNICODE_EXCEPTION_FLAG = false;

	/**
	 * Indicates whether or not IDNA2008 Protocol rules are applied. By default
	 * this value is true.
	 */
	static public final boolean DEFAULT_REGISTRATION_PROTOCOL = true;

	/**
	 * Class that peforms ASCII compatible encoding of Unicode code
	 * points/charaters.
	 */
	private final Ace ace;

	/**
	 * Indicates whether or not an exception should be thrown if an exception is
	 * encountered in the toUnicode() method.
	 */
	private final boolean toUnicodeExceptionFlag;

	/**
	 * Indicates whether or not IDNA2008 Protocol rules are applied
	 */
	private final boolean isRegistrationProtocol;


	/**
	 * Construct an Idna object for converting data to and from an ASCII
	 * compatible form.
	 * 
	 * @param ace
	 *        An Ace object to use during conversion.
	 * @throws XcodeException
	 */
	public Idna ( Ace ace ) throws XcodeException {
		this( ace, DEFAULT_TO_UNICODE_EXCEPTION_FLAG, DEFAULT_REGISTRATION_PROTOCOL );
	}


	/**
	 * Construct an Idna object for converting data to and from an ASCII
	 * compatible form.
	 * 
	 * @param ace
	 *        An Ace object to use during conversion.
	 * @param aToUnicodeExceptionFlag
	 *        See the description for the <b>DEFAULT_TO_UNICODE_EXCEPTION_FLAG
	 *        </b> attibute which stores the default value for this parameter.
	 */
	public Idna ( Ace ace, boolean aToUnicodeExceptionFlag )
			throws XcodeException {
		this( ace, aToUnicodeExceptionFlag, DEFAULT_REGISTRATION_PROTOCOL );
	}


	/**
	 * Construct an Idna object for converting data to and from an ASCII
	 * compatible form.
	 * 
	 * @param ace
	 *        An Ace object to use during conversion.
	 * @param aToUnicodeExceptionFlag
	 *        See the description for the <b>DEFAULT_TO_UNICODE_EXCEPTION_FLAG
	 *        </b> attibute which stores the default value for this parameter.
	 * @param aIsRegistrationProtocol
	 *        Indicates whether or not IDNA2008 Protocol rules are applied
	 */
	public Idna ( Ace ace, boolean aToUnicodeExceptionFlag,
			boolean aIsRegistrationProtocol ) throws XcodeException {

		if ( ace == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		this.ace = ace;
		this.toUnicodeExceptionFlag = aToUnicodeExceptionFlag;
		this.isRegistrationProtocol = aIsRegistrationProtocol;
	}


	/**
	 * Checks if the input string is a delimiter
	 * 
	 * @param s
	 *        a string to check
	 * @return true if the s represents one of the DELIMITERS, false if not
	 */
	static public boolean isDelimiter ( String s ) {
		return DELIMITERS.indexOf( s ) >= 0;
	}


	/**
	 * Checks if the input int is a delimiter
	 * 
	 * @param c
	 *        an int to check
	 * @return true if the s represents one of the DELIMITERS, false if not
	 */
	static public boolean isDelimiter ( int c ) {
		return DELIMITERS.indexOf( c ) >= 0;
	}


	/**
	 * Converts an array of integers to a character array
	 * 
	 * @param input
	 *        an int array
	 * @return a character array
	 * @throws XcodeException
	 *         if the input is null or empty
	 */
	public char[] domainToAscii ( int[] input ) throws XcodeException {
		if ( input == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( input.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		StringBuilder output = new StringBuilder();

		// Array list to hold the normalized Unicode code points from toAscii()
		// method call.
		TIntList normalizedUnicode = new TIntArrayList();

		// Container returned from toAscii() method call.
		UnicodeAsciiContainer container = null;

		final UnicodeTokenizer tokens =
				new UnicodeTokenizer( input, INT_DELIMITERS, true );

		for ( final int[] token : tokens ) {
			if ( token.length == 1 && isDelimiter( token[ 0 ] ) ) {
				output.append( ACE_DELIMITER );
				normalizedUnicode.add( token[ 0 ] );
			}
			else {
				// Once the UnicodeAsciiContainer is returned from the toAscii() method
				// call, save it's contents into the output to be returned. Also, the
				// normalized code points will be saved to the array list.
				container = toAscii( token );
				output.append( container.getAscii() );
				normalizedUnicode.add( container.getUnicode() );
			}
		}

		if ( this.isRegistrationProtocol ) {
			// Apply the Bidi rules on the normalized Unicode code points output from
			// the toAscii() method call.
			applyIdna2008BidiRules( normalizedUnicode.toArray() );
		}

		return output.toString().toCharArray();
	}


	/**
	 * Converts a character array to an integer array
	 * 
	 * @param input
	 *        a character array
	 * @return an int array
	 * @throws XcodeException
	 *         if the input is null or empty
	 */
	public int[] domainToUnicode ( char[] input ) throws XcodeException {
		if ( input == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( input.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final TIntList output = new TIntArrayList();
		final StringTokenizer tokens =
				new StringTokenizer( new String( input ), DELIMITERS, true );
		String token;
		while ( tokens.hasMoreTokens() ) {
			token = tokens.nextToken();
			if ( isDelimiter( token ) ) {
				output.add( token.charAt( 0 ) );
			}
			else {
				output.add( toUnicode( token.toCharArray() ) );
			}
		}

		final int[] unicode = output.toArray();

		// Apply Bidi rules
		if ( this.isRegistrationProtocol ) {
			applyIdna2008BidiRules( unicode );
		}
		return unicode;
	}


	/**
	 * toAscii - IETF Idna draft documents a method of converting unicode data
	 * into a DNS compliant domain. <br>
	 * 
	 * @param input
	 *        an int array to be converted to characters
	 * @return converted character array
	 */
	private UnicodeAsciiContainer toAscii ( final int[] anArray )
			throws XcodeException {

		UnicodeAsciiContainer container = new UnicodeAsciiContainer();
		// This encoding is intended for DNS Registration
		if ( this.isRegistrationProtocol ) {
			int[] input = anArray;

			// Step 1. Normalize
			if ( !Utf16.isAscii( input ) ) {
				input = Normalize.execute( input );
			}

			container.setUnicode( input );

			// Step 2. Assert Idna compliance (MUST not alter input)
			idna2008Protocol( input );

			// Step 3. Encode in Ace
			final char[] output = this.ace.encode( input );

			// Step 4. Assert Dns compatibility
			if ( output.length > MAX_DOMAIN_LABEL_LENGTH_IN_OCTETS ) {
				throw XcodeError.IDNA_LABEL_LENGTH_RESTRICTION();
			}

			container.setAscii( output );
		}

		// This encoding is intended for Resolution only
		else {
			container.setAscii( this.ace.encode( anArray ) );
			container.setUnicode( anArray );
		}
		return container;
	}


	/**
	 * @param input
	 *        an array of characters contained in the domain name
	 * @return an int[] containing the Unicode code points as decoded by the
	 *         Punycode algorithm. If <tt>isRegistrationProtocol</tt> is true,
	 *         then the IDNA2008 Protocol checks are applied to ensure
	 *         conformance.
	 * @throws XcodeException
	 */
	private int[] toUnicode ( final char[] input ) throws XcodeException {

		try {

			// This encoding is intended for DNS Registration
			if ( this.isRegistrationProtocol ) {

				// Step 4. Assert Dns compatibility
				if ( input.length > MAX_DOMAIN_LABEL_LENGTH_IN_OCTETS ) {
					throw XcodeError.IDNA_LABEL_LENGTH_RESTRICTION();
				}

				// Step 3. Decode from Ace
				final int[] output = this.ace.decode( input );

				// Step 2. Assert Idna compliance (MUST not alter input)
				idna2008Protocol( output );

				// Step 1. Assert normalization
				UnicodeData.assertNormalized( output );

				return output;
			}

			// This encoding is intended for Resolution only
			else {
				return this.ace.decode( input );
			}
		}

		catch ( final XcodeException x ) {
			if ( this.toUnicodeExceptionFlag ) {
				throw x;
			}
			return Utf16.expand( input );
		}
	}


	/**
	 * Method to apply IDNA2008 Protocol rules on the given set of Unicode code
	 * points. The BIDI rules are not applied in this method. They are applied in
	 * <code>applyIdna2008BidiRules</code> method.
	 * 
	 * @param codePoints
	 *        an int[] containing the Unicode code points as decoded by the
	 *        Punycode algorithm
	 * @throws XcodeException
	 */
	private void idna2008Protocol ( final int[] codePoints )
			throws XcodeException {

		if ( codePoints == null || codePoints.length == 0 ) {
			throw XcodeError.NULL_ARGUMENT();
		}

		// Assert all Unicode points are allowed
		UnicodeData.assertNoDisallowedOrUnassignedCodePoints( codePoints );

		// Assert no hyphen restrictions
		if ( Utf16.hasHyphenRestrictions( codePoints ) ) {
			throw XcodeError.IDNA_IDNA_HYPHEN_RESTRICTION();
		}

		// Assert no leading combining marks
		if ( UnicodeData.isCombiningMark( codePoints[ 0 ] ) ) {
			throw XcodeError.IDNA_LEADING_COMBINING_MARK();
		}

		// Assert contextual rules compliance (if CONTEXTO/CONTEXTJ chars present)
		if ( UnicodeData.hasContextualCodePoints( codePoints ) ) {
			ContextualRulesRegistry.runContextualRules( codePoints );
		}

	}


	/**
	 * Bidi rules are for BIDI domains which contain an R, AL or AN code point. If
	 * not this method returns immediately. Otherwise each label, is then
	 * processed for BIDI rules compliance.
	 * 
	 * @param aUnicode
	 * @throws XcodeException
	 */
	private void applyIdna2008BidiRules ( int[] aUnicode ) throws XcodeException {

		// If it is not a BIDI domain i.e. no R, AL or AN code points, then return.
		if ( !Bidi.isBidiDomain( aUnicode ) ) {
			return;
		}

		final UnicodeTokenizer tokens =
				new UnicodeTokenizer( aUnicode, INT_DELIMITERS, false );

		// Process each label for BIDI rules compliance.
		for ( final int[] unicodeLabel : tokens ) {
			Bidi.assertCompliance( unicodeLabel );

		}
	}

	/**
	 * Class to serve as a container for the Unicode code points as well as the
	 * ASCII representation in Punycode.
	 * 
	 * @author nchigurupati
	 * @version 1.0 Sep 29, 2010
	 */
	private static class UnicodeAsciiContainer {

		/**
		 * Array to hold the normalized Unicode code points
		 */
		private int[] unicode;

		/**
		 * Array to hold the ASCII representation in Punycode.
		 */
		private char[] ascii;


		/**
		 * Returns the unicode
		 * 
		 * @return the unicode
		 */
		public int[] getUnicode () {
			return this.unicode;
		}


		/**
		 * Sets unicode value to unicode
		 * 
		 * @param unicode
		 *        the unicode to set
		 */
		public void setUnicode ( int[] unicode ) {
			this.unicode = unicode;
		}


		/**
		 * Returns the ascii
		 * 
		 * @return the ascii
		 */
		public char[] getAscii () {
			return this.ascii;
		}


		/**
		 * Sets ascii value to ascii
		 * 
		 * @param ascii
		 *        the ascii to set
		 */
		public void setAscii ( char[] ascii ) {
			this.ascii = ascii;
		}

	}
}