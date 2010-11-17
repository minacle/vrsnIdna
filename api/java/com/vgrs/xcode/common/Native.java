/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * A class that provides algorithms to encode/decode a UTF 16 to/from native
 * characters. <BR>
 * Note: when using JVM 1.4 or greater, the String.getBytes() method can return
 * a java.nio.BufferOverflowException. <BR>
 * (e.g. byte[] b = new String("\u678F").getBytes("ISO2022KR"); <BR>
 * It is not possible to catch the java.nio.BufferOverflowException specifically
 * because this won't compile on an older JVM. Instead, the internalDecode()
 * method now catches any Exception and covers it with a
 * XcodeError.NATIVE_INVALID_ENCODING.
 */
public final class Native {

	/**
	 * Java UTF8 encoding type, used to abstract the Java internals.
	 */
	public static final String UTF8 = "UTF8";

	/**
	 * DOUBLE_UTF8 is not a Java supported encoding, and so requires special
	 * processing.
	 */
	public static final String DOUBLE_UTF8 = "DOUBLE_UTF8";


	/**
	 * Retrieve the encoding stored in a Java String. In Java, a native encoding
	 * is stored as an array of bytes. Unfortunately convenience sometimes
	 * dictates that we must use a String to store this data. When creating a
	 * String from an encoding, Java places each encoding byte in the low octet of
	 * a two byte char, leaving the high byte empty. In order to retrieve an
	 * encoding from a String, we will reverse this process, returning only the
	 * low bytes from each character in the String. We must also throw an
	 * Exception if any high byte is not empty to avoid inaccurate results.
	 * 
	 * @param aInput
	 *        the String holding the encoded data
	 * @return an array of bytes
	 * @throws XcodeException
	 *         if the input is null/empty or if any high byte is non-zero
	 */
	public static byte[] getEncoding ( String aInput ) throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}
		final char[] c = aInput.toCharArray();
		if ( !Utf16.isNative( c ) ) {
			throw XcodeError.NATIVE_INVALID_ENCODING();
		}
		return Utf16.contract( c );
	}


	/**
	 * Encode a UTF16 sequence using the specified native encoding. This method
	 * does not perform round-trip checking and does not support DOUBLE_UTF8. It
	 * is provided only for internal use. The encode(String, String[]) method
	 * should be used for applications.
	 * 
	 * @param aInput
	 *        The input UTF16 sequence to be encoded using the specified native
	 *        encoding.
	 * @param aEncoding
	 *        string to indicate the encoding type of the output
	 * @return byte[] array containing the encoded sequence
	 * @throws XcodeException
	 */
	private static byte[] internalEncode ( String aInput, String aEncoding )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		try {
			return aInput.getBytes( aEncoding );
		}
		catch ( final UnsupportedEncodingException x ) {
			throw XcodeError.UNSUPPORTED_ENCODING( ": " + aEncoding );
		}
		catch ( final Exception x ) {
			throw XcodeError.NATIVE_INVALID_ENCODING( ": " + aEncoding );
		}
	}


	/**
	 * Encode the input using each of the Java supported encoding types A
	 * round-trip check is used to ensure that the encoded data is valid.
	 * 
	 * @param aInput
	 *        the string to be encoded
	 * @return a HashMap keyed on all encoding types
	 * @throws XcodeException
	 *         if input is null or empty string.
	 */
	public static Map<String, String> encode ( String aInput )
			throws XcodeException {
		return encode( aInput, ENCODINGS );
	}


	/**
	 * Encode the input using the indicated encoding types. A round-trip check is
	 * used to ensure that the encoded data is valid.
	 * 
	 * @param aInput
	 *        string to be encoded
	 * @param aEncoding
	 *        string to indicate the encoding type of the output
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 * @return the encoded string
	 */
	public static String encode ( String aInput, String aEncoding )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		byte[] output = null;
		String check = null;

		// Special DOUBLE_UTF8 processing
		if ( aEncoding.equals( DOUBLE_UTF8 ) ) {
			output =
					internalEncode( new String( internalEncode( aInput, UTF8 ) ), UTF8 );
			check =
					internalDecode( getEncoding( internalDecode( output, UTF8 ) ), UTF8 );
		}
		else {
			output = internalEncode( aInput, aEncoding );
			check = internalDecode( output, aEncoding );
		}
		if ( !aInput.equals( check ) ) {
			throw XcodeError.NATIVE_INVALID_ENCODING( ": " + aEncoding );
		}

		/*
		 * We used to essentially do this: return new
		 * String(internalEncode(input,encoding)); But the problem is that new
		 * String() is actually trying to decode the input with respect to the
		 * default encoding. The crux of the internalDecode routine is actually a
		 * call to new String(). Here, we want to return a String object with
		 * exactly the data in the byte[], no decoding. We do it the hard way now.
		 */
		return new String( Utf16.expand( output ) );
	}


	/**
	 * Encode the input using the indicated encoding types. A round-trip check is
	 * used to ensure that the encoded data is valid.
	 * 
	 * @param aInput
	 *        the string to be encoded
	 * @param aEncodings
	 *        indicate the encoding types of the output string
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 * @return a HashMap with keyed on the encoding type
	 */
	public static Map<String, String> encode ( String aInput, String[] aEncodings )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final Map<String, String> results = new HashMap<String, String>();

		for ( final String aEncoding : aEncodings ) {
			try {
				results.put( aEncoding, encode( aInput, aEncoding ) );
			}
			catch ( final XcodeException x ) {
				//
			}
		}
		return results;
	}


	/**
	 * Encode the input using each of the encoding types to a HashSet
	 * 
	 * @param aInput
	 *        the string to be encoded
	 * @return a HashSet of the encoded string
	 * @throws XcodeException
	 *         if input is null or empty string.
	 */
	public static Set<String> encodeToSet ( String aInput ) throws XcodeException {
		return encodeToSet( aInput, ENCODINGS );
	}


	/**
	 * Encode the input using indicated encoding types to a HashSet
	 * 
	 * @param aInput
	 *        the string to be encoded
	 * @param aEncodings
	 *        encoding types of the decoded string
	 * @return a HashSet of the encoded string
	 * @throws XcodeException
	 *         if input is null or empty string.
	 */
	public static Set<String> encodeToSet ( String aInput, String[] aEncodings )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final Set<String> results = new HashSet<String>();

		for ( final String aEncoding : aEncodings ) {
			try {
				results.add( encode( aInput, aEncoding ) );
			}
			catch ( final XcodeException x ) {
				//
			}
		}
		return results;
	}


	/**
	 * Use the specified native encoding to return the input sequence in UTF16
	 * format. This method does not perform round-trip checking and does not
	 * support DOUBLE_UTF8. It is provided only for internal use. The decode
	 * method should be used for applications.
	 * 
	 * @param aInput
	 *        the bytes to be decoded into characters
	 * @param aEncoding
	 *        the name of a supported charset native encoding
	 * @throws XcodeException
	 *         If the named charset is not supported
	 */
	private static String internalDecode ( byte[] aInput, String aEncoding )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		try {
			return new String( aInput, aEncoding );
		}
		catch ( final UnsupportedEncodingException x ) {
			throw XcodeError.UNSUPPORTED_ENCODING( ": " + aEncoding );
		}
	}


	/**
	 * Evaluate the given input against the list of encodings to determine how the
	 * input may have been encoded. First decode and then re-encode the input
	 * using each of the encoding types. Any type for which the round-trip is
	 * symmetric is considered a viable encoding, and added to the output list.
	 * 
	 * @param aInput
	 *        the bytes to be decoded into characters
	 * @return a HashMap with keyed on the encoding type
	 * @throws XcodeException
	 *         If the named charset is not supported
	 */
	public static Map<String, String> decode ( byte[] aInput )
			throws XcodeException {
		return decode( aInput, ENCODINGS );
	}


	/**
	 * Use the specified native encoding to return the input sequence in UTF16
	 * format. This method performs round-trip checking and supports DOUBLE_UTF8.
	 * 
	 * @param aInput
	 *        the bytes to be decoded
	 * @param aEncoding
	 *        the encoding type to be used in decoding
	 * @return a string of the indicated encoding type
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 */
	public static String decode ( byte[] aInput, String aEncoding )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		String output = null;
		byte[] check = null;

		// Special DOUBLE_UTF8 processing
		if ( aEncoding.equals( DOUBLE_UTF8 ) ) {
			output =
					internalDecode( getEncoding( internalDecode( aInput, UTF8 ) ), UTF8 );
			check =
					internalEncode( new String( internalEncode( output, UTF8 ) ), UTF8 );
		}
		else {
			output = internalDecode( aInput, aEncoding );
			check = internalEncode( output, aEncoding );
		}
		if ( !Arrays.equals( check, aInput ) ) {
			throw XcodeError.NATIVE_INVALID_ENCODING( ": " + aEncoding );
		}
		return output;
	}


	/**
	 * Decode the input of the indicated encoding types. A round-trip check is
	 * used to ensure that the encoded data is valid.
	 * 
	 * @param aInput
	 *        the native string to be decode
	 * @param aEncodings
	 *        indicate the encoding types of the input string
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 * @return the encoded string
	 */
	public static Map<String, String> decode ( byte[] aInput, String[] aEncodings )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final Map<String, String> results = new HashMap<String, String>();

		for ( final String aEncoding : aEncodings ) {
			try {
				results.put( aEncoding, decode( aInput, aEncoding ) );
			}
			catch ( final XcodeException x ) {
				//
			}
		}
		return results;
	}


	/**
	 * Decode the native string of all encoding types to a HashSet
	 * 
	 * @param aInput
	 *        the string to be decoded
	 * @return a HashSet of the decoded string
	 * @throws XcodeException
	 *         if input is null or empty string.
	 */
	public static Set<String> decodeToSet ( byte[] aInput ) throws XcodeException {
		return decodeToSet( aInput, ENCODINGS );
	}


	/**
	 * Decode the native string of indicated encoding types to a HashSet
	 * 
	 * @param aInput
	 *        the string to be decoded
	 * @param aEncodings
	 *        encoding types of the decoded string
	 * @return a HashSet of the decoded string
	 * @throws XcodeException
	 *         if input is null or empty string.
	 */
	public static Set<String> decodeToSet ( byte[] aInput, String[] aEncodings )
			throws XcodeException {
		if ( aInput == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aInput.length == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}

		final Set<String> results = new HashSet<String>();

		for ( final String aEncoding : aEncodings ) {
			try {
				results.add( decode( aInput, aEncoding ) );
			}
			catch ( final XcodeException x ) {
				//
			}
		}
		return results;
	}

	/**
	 * Array to stort all Java-Supported native encodings
	 */
	public static final String[] ENCODINGS =
			{
					"ASCII", "Big5", "Big5_HKSCS", "Big5_Solaris", "Cp037", "Cp1006",
					"Cp1025", "Cp1026", "Cp1046", "Cp1047", "Cp1097", "Cp1098", "Cp1112",
					"Cp1122", "Cp1123", "Cp1124", "Cp1140", "Cp1141", "Cp1142", "Cp1143",
					"Cp1144", "Cp1145", "Cp1146", "Cp1147", "Cp1148", "Cp1149", "Cp1250",
					"Cp1251", "Cp1252", "Cp1253", "Cp1254", "Cp1255", "Cp1256", "Cp1257",
					"Cp1258", "Cp1381", "Cp1383", "Cp273", "Cp277", "Cp278", "Cp280",
					"Cp284", "Cp285", "Cp297", "Cp33722", "Cp420", "Cp424", "Cp437",
					"Cp500", "Cp737", "Cp775", "Cp838", "Cp850", "Cp852", "Cp855",
					"Cp856", "Cp857", "Cp858", "Cp860", "Cp861", "Cp862", "Cp863",
					"Cp864", "Cp865", "Cp866", "Cp868", "Cp869", "Cp870", "Cp871",
					"Cp874", "Cp875", "Cp918", "Cp921", "Cp922", "Cp930", "Cp933",
					"Cp935", "Cp937", "Cp939", "Cp942", "Cp942C", "Cp943", "Cp943C",
					"Cp948", "Cp949", "Cp949C", "Cp950", "Cp964", "Cp970", "EUC_CN",
					"EUC_JP", "EUC_JP_LINUX", "EUC_KR", "EUC_TW", "GB18030", "GBK",
					"ISCII91", "ISO2022_CN_CNS", "ISO2022_CN_GB", "ISO2022JP",
					"ISO2022KR", "ISO8859_1", "ISO8859_13", "ISO8859_15", "ISO8859_2",
					"ISO8859_3", "ISO8859_4", "ISO8859_5", "ISO8859_6", "ISO8859_7",
					"ISO8859_8", "ISO8859_9", "JISAutoDetect", "KOI8_R", "MacArabic",
					"MacCentralEurope", "MacCroatian", "MacCyrillic", "MacDingbat",
					"MacGreek", "MacHebrew", "MacIceland", "MacRoman", "MacRomania",
					"MacSymbol", "MacThai", "MacTurkish", "MacUkraine", "MS874", "MS932",
					"MS936", "MS949", "MS950", "MS950_HKSCS", "SJIS", "TIS620",
					"UnicodeBig", "UnicodeBigUnmarked", "UnicodeLittle",
					"UnicodeLittleUnmarked", "UTF-16", "UTF8",
			};

}