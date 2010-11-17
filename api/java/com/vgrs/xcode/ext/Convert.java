/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.ext;

import java.util.Properties;

import com.vgrs.xcode.common.Native;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.common.UnicodeFilter;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.idna.Race;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This routine converts directly between Race, Punycode and Native forms,
 * abstracting any intermediate steps. For instance, it is possible for calling
 * applications to convert a domain name from Race to Punycode and UTF8 using a
 * single call. <br>
 * <br>
 * <b>Note: </b> <br>
 * Data conversions involving "Race" and "Punycode" rely on the underlying Idna
 * object. Encoding to "Race" or "Punycode" always applies the Nameprep
 * algorithm. Decoding from "Race" or "Punycode" may optionally apply Nameprep
 * dependant on the toUnicodeRoundTripCheckFlag of the Idna object. Conversions
 * to and from all other data types do not include the Nameprep algorithm, and
 * are allowed to contain data not valid for IDN registration.
 */
public class Convert {

	/**
	 * A string refering to the Row-based ASCII Compatible Encoding. <br>
	 * This variable is hard coded to the value "RACE".
	 */
	public static final String RACE_ENCODING = "RACE";

	/**
	 * A string refering to the Punycode algorithm referenced in RFC 3492. <br>
	 * This variable is hard coded to the value "PUNYCODE".
	 */
	public static final String PUNYCODE_ENCODING = "PUNYCODE";

	/**
	 * Private attributes
	 */
	private Idna iRace = null;

	private Idna iPunycode = null;

	private UnicodeFilter unicodeFilter = null;


	/**
	 * Construct a new Convert object to transform a single input sequence into
	 * one or more encoding forms.
	 */
	public Convert () throws XcodeException {
		this( new Idna( new Race() ), new Idna( new Punycode() ), null );
	}


	/**
	 * Construct a new Convert object to transform a single input sequence into
	 * one or more encoding forms.
	 * 
	 * @param aPunycode
	 *        An Ace object for converting to and from Punycode.
	 * @param aRace
	 *        An Ace object for converting to and from Race.
	 * @throws XcodeException
	 *         If the parameters are null or invalid.
	 */
	public Convert ( Punycode aPunycode, Race aRace ) throws XcodeException {
		this( new Idna( aRace ), new Idna( aPunycode ), null );
	}


	/**
	 * Construct a new Convert object to transform a single input sequence into
	 * one or more encoding forms.
	 * 
	 * @param aPunycode
	 *        An Ace object for converting to and from Punycode.
	 * @param aRace
	 *        An Ace object for converting to and from Race.
	 * @param aToUnicodeExceptionFlag
	 *        (see the Idna object for details)
	 * @throws XcodeException
	 *         If the parameters are null or invalid.
	 */
	public Convert ( Punycode aPunycode, Race aRace,
			boolean aToUnicodeExceptionFlag ) throws XcodeException {
		this( new Idna( aRace, aToUnicodeExceptionFlag ), new Idna( aPunycode,
				aToUnicodeExceptionFlag ), null );
	}


	/**
	 * Construct a new Convert object to transform a single input sequence into
	 * one or more encoding forms.
	 * 
	 * @param aPunycode
	 *        An Ace object for converting to and from Punycode.
	 * @param aRace
	 *        An Ace object for converting to and from Race.
	 * @param aToUnicodeExceptionFlag
	 *        (see the Idna object for details)
	 * @throws XcodeException
	 *         If the parameters are null or invalid.
	 */
	public Convert ( Punycode aPunycode, Race aRace,
			boolean aToUnicodeExceptionFlag, boolean aIsRegistrationProtocol )
			throws XcodeException {
		this(
				new Idna( aRace, aToUnicodeExceptionFlag, aIsRegistrationProtocol ),
				new Idna( aPunycode, aToUnicodeExceptionFlag, aIsRegistrationProtocol ),
				null );
	}


	/**
	 * Construct a new Convert object to transform a single input sequence into
	 * one or more encoding forms.
	 * 
	 * @param aRace
	 *        An Idna object for converting to and from Race.
	 * @param aPunycode
	 *        An Idna object for converting to and from Punycode.
	 * @param aUnicodeFilter
	 *        A UnicodeFilter object to applied to the input sequence before
	 *        encoding.
	 * @throws XcodeException
	 *         If the parameters are null or invalid.
	 */
	public Convert ( Idna aRace, Idna aPunycode, UnicodeFilter aUnicodeFilter ) {
		this.iRace = aRace;
		this.iPunycode = aPunycode;
		this.unicodeFilter = aUnicodeFilter;
	}


	/**
	 * Construct a new Convert object to transform a single input sequence into
	 * one or more encoding forms.
	 * 
	 * @param aRace
	 *        An Idna object for converting to and from Race.
	 * @param aPunycode
	 *        An Idna object for converting to and from Punycode.
	 * @throws XcodeException
	 *         If the parameters are null or invalid.
	 */
	public Convert ( Idna aRace, Idna aPunycode ) {
		this.iRace = aRace;
		this.iPunycode = aPunycode;
	}


	/**
	 * Convert the input sequence into a new encoding form.
	 * 
	 * @param aInput
	 *        the string to be converted
	 * @param aInputType
	 *        the encoding type of the input string
	 * @param aOutputType
	 *        the desired output encoding
	 * @return A Properties object containing the results of all transformations.
	 *         Each key matches a String from otype and each value is the
	 *         associated encoded form.
	 * @throws XcodeException
	 *         if either the input string is invalid or the itype/otype is not
	 *         supported
	 */
	public String execute ( String aInput, String aInputType, String aOutputType )
			throws XcodeException {

		if ( aInput == null || aInputType == null || aOutputType == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}

		if ( aInput.length() == 0 || aInputType.length() == 0
				|| aOutputType.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final int[] unicode = decode( aInput, aInputType );
		if ( this.unicodeFilter != null ) {
			this.unicodeFilter.assertNone( unicode );
		}
		return encode( unicode, aOutputType );
	}


	/**
	 * Convert the input sequence into one or more encoding forms. <br>
	 * <br>
	 * <b>Note: </b> <br>
	 * The Convert logic ignores XcodeExceptions when attempting to convert to
	 * multiple output encodings. Output encodings for which the conversion fails
	 * are not included in the result set. This implies that XcodeExceptions such
	 * as UNSUPPORTED_ENCODING are hidden from the user unless the call asks
	 * specifically for a conversion to exactly one encoding. This logic improves
	 * the efficiency of the algorithm, and is not generally problematic.
	 * 
	 * @param aInput
	 *        the string to be converted
	 * @param aInputType
	 *        the encoding type of the input string
	 * @param aOutputTypes
	 *        the String array of desired output encodings
	 * @return A Properties object containing the results of all transformations.
	 *         Each key matches a String from otype and each value is the
	 *         associated encoded form.
	 * @throws XcodeException
	 *         On invalid input parameters
	 */
	public Properties execute ( String aInput, String aInputType,
			String[] aOutputTypes ) throws XcodeException {

		if ( aInput == null || aInputType == null || aOutputTypes == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}

		if ( aInput.length() == 0 || aInputType.length() == 0
				|| aOutputTypes.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final int[] unicode = decode( aInput, aInputType );
		if ( this.unicodeFilter != null ) {
			this.unicodeFilter.assertNone( unicode );
		}
		return encode( unicode, aOutputTypes );
	}


	/**
	 * Decodes the input string to Unicode
	 * 
	 * @param aInput
	 *        the string to be converted
	 * @param aInputType
	 *        the encoding type of the input string
	 * @throw XcodeException if the itype is not supported or the input string is
	 *        invalid
	 */
	private int[] decode ( String aInput, String aInputType )
			throws XcodeException {

		if ( aInputType.equalsIgnoreCase( RACE_ENCODING ) ) {
			return this.iRace.domainToUnicode( aInput.toCharArray() );
		}
		else if ( aInputType.equalsIgnoreCase( PUNYCODE_ENCODING ) ) {
			return this.iPunycode.domainToUnicode( aInput.toCharArray() );
		}
		else {
			final String utf16 =
					Native.decode( Native.getEncoding( aInput ), aInputType );
			return Unicode.encode( utf16.toCharArray() );
		}
	}


	/**
	 * Encode the input Unicode using the indicated encoding type
	 * 
	 * @param aUnicodeCodePoints
	 *        the Unicode sequence to be converted
	 * @param aOutputType
	 *        the encoding type
	 * @throw XcodeException if the otype is not supported or the utf16 string is
	 *        invalid
	 */
	private String encode ( int[] aUnicodeCodePoints, String aOutputType )
			throws XcodeException {

		if ( aOutputType.equalsIgnoreCase( RACE_ENCODING ) ) {
			return new String( this.iRace.domainToAscii( aUnicodeCodePoints ) );
		}
		else if ( aOutputType.equalsIgnoreCase( PUNYCODE_ENCODING ) ) {
			return new String( this.iPunycode.domainToAscii( aUnicodeCodePoints ) );
		}
		else {
			return Native.encode( new String( Unicode.decode( aUnicodeCodePoints ) ),
					aOutputType );
		}
	}


	/**
	 * Encode the input Unicode using all indicated encoding types
	 * 
	 * @param aUnicodeCodePoints
	 *        the Unicode sequence to be converted
	 * @param aOutputTypes
	 *        the encoding types
	 * @throw XcodeException Currently no XcodeException can be thrown
	 */
	private Properties encode ( int[] aUnicodeCodePoints, String[] aOutputTypes ) {
		final Properties results = new Properties();
		for ( final String aOutputType : aOutputTypes ) {
			try {
				results.put( aOutputType, encode( aUnicodeCodePoints, aOutputType ) );
			}
			catch ( final XcodeException x ) {
				// Currently no XcodeException can be thrown
			}
		}
		return results;
	}

}