/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.common;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Native;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class converts between native language encodings like Big5, S-JIS, or
 * UTF- 8. Input data can be converted between one or more native encodings
 * given in a space-separated list. The full list of supported encodings can be
 * found in the Appendices section of the IDNSDK User's guide. Because of the
 * nature of Java's support for Native Encodings, the round trip checking must
 * be done deep inside the code. It does not, therefore need to take place here
 * in the test driver. The encode and decode methods here just make one call,
 * because the answer is guaranteed to be reliable.
 * <ul>
 * <li><tt>Usage:</tt> com.vgrs.xcode.cmdline.common.NativeCmdLine (--encode |
 * --decode) file=[file name] [space separated encoding list]
 * <li><tt>Input type:</tt> Utf-16 for encode, Native for decode
 * <li><tt>Output type:</tt> Native for encode, Utf-16 for decode
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 10, 2010
 */
public class NativeCmdLine extends CmdLine {

	static private String SWITCH_ENCODE = "encode";
	static private String SWITCH_DECODE = "decode";


	/**
	 * @param args
	 *        the command line arguments
	 */
	public NativeCmdLine ( String[] args ) {
		super( args );
	}


	/**
	 * @param args
	 *        the command line arguments
	 */
	static public void main ( String args[] ) {

		final NativeCmdLine cmd = new NativeCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( !options.has( SWITCH_FILE ) || options.getUnassigned().size() == 0 ) {
			cmd.showUsage();
			return;
		}

		final File inputFile = new File( options.get( SWITCH_FILE ) );

		final List<String> encodingList = options.getUnassigned();
		String[] encodings = null;
		if ( encodingList != null && encodingList.size() > 0 ) {
			encodings = encodingList.toArray( new String[encodingList.size()] );
		}

		if ( options.has( SWITCH_ENCODE ) ) {
			testEncode( inputFile, encodings );
		}
		else if ( options.has( SWITCH_DECODE ) ) {
			testDecode( inputFile, encodings );
		}
		else {
			cmd.showUsage();
			return;
		}
	}


	/**
	 * Encodes the input in the file using each of the Java supported encoding
	 * types. A round-trip check is used to ensure that the encoded data is valid.
	 * 
	 * @param input
	 *        the file name of the file containing the input to be encoded.
	 * @param encodings
	 *        the encoding scheme to use
	 */
	static public void testEncode ( File input, String[] encodings ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			if ( encodings != null && encodings.length == 1 ) {
				while ( data.hasNext() ) {
					testEncode( data.next(), encodings[ 0 ] );
				}
			}
			else {
				while ( data.hasNext() ) {
					testEncode( data.next(), encodings );
				}
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Use the specified native encoding to return the input sequence in UTF16
	 * format.
	 * 
	 * @param input
	 *        the file name of the file containing the input to be encoded.
	 * @param encodings
	 *        the encoding scheme to use
	 */
	static public void testDecode ( File input, String[] encodings ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			if ( encodings != null && encodings.length == 1 ) {
				while ( data.hasNext() ) {
					testDecode( data.next(), encodings[ 0 ] );
				}
			}
			else {
				while ( data.hasNext() ) {
					testDecode( data.next(), encodings );
				}
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Encode the input using the indicated encoding type. A round-trip check is
	 * used to ensure that the encoded data is valid.
	 * 
	 * @param input
	 *        string to be encoded
	 * @param encoding
	 *        string to indicate the encoding type of the output
	 * @throws XcodeException
	 */
	static public void testEncode ( final String input, final String encoding )
			throws XcodeException {
		String inputdecoded = null;
		String output = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		final String inputString = input.trim();

		if ( inputString.length() == 0 || inputString.charAt( 0 ) == '#' ) {
			Debug.pass( inputString );
			return;
		}

		try {
			inputdecoded = new String( Hex.decodeChars( inputString ) );
			output = Native.encode( inputdecoded, encoding );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		System.out.println( Hex.encode( output.toCharArray() ) );
	}


	/**
	 * Encode the input using the indicated encoding types. A round-trip check is
	 * used to ensure that the encoded data is valid.
	 * 
	 * @param input
	 *        the string to be encoded
	 * @param encodings
	 *        indicate the encoding types of the output string
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 */
	static public void testEncode ( final String input, final String[] encodings )
			throws XcodeException {
		String inputdecoded = null;
		Map<String, String> output = null;
		String variant = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		final String inputString = input.trim();

		if ( inputString.length() == 0 || inputString.charAt( 0 ) == '#' ) {
			Debug.pass( inputString );
			return;
		}

		try {
			inputdecoded = new String( Hex.decodeChars( inputString ) );
			if ( encodings == null || encodings.length == 0 ) {
				output = Native.encode( inputdecoded );
			}
			else {
				output = Native.encode( inputdecoded, encodings );
			}
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		System.out.println( inputString );
		final Iterator<String> i = output.keySet().iterator();
		String encoding = null;
		while ( i.hasNext() ) {
			encoding = i.next();
			variant = output.get( encoding );
			System.out.println( "	" + encoding + "	"
					+ Hex.encode( variant.toCharArray() ) );
		}
		System.out.println( output.size() );
	}


	/**
	 * Use the specified native encoding to return the input sequence in UTF16
	 * format. This method performs round-trip checking and supports DOUBLE_UTF8.
	 * 
	 * @param input
	 *        the string to be decoded
	 * @param encoding
	 *        the encoding type to be used in decoding
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 */
	static public void testDecode ( final String input, final String encoding )
			throws XcodeException {
		byte[] inputarray = null;
		String output = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		final String inputString = input.trim();

		if ( inputString.length() == 0 || inputString.charAt( 0 ) == '#' ) {
			Debug.pass( inputString );
			return;
		}

		try {
			inputarray = Hex.decodeBytes( inputString );
			output = Native.decode( inputarray, encoding );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}
		System.out.println( Hex.encode( output.toCharArray() ) );
	}


	/**
	 * Use the specified native encoding to return the input sequence in UTF16
	 * format. This method performs round-trip checking and supports DOUBLE_UTF8.
	 * 
	 * @param input
	 *        the string to be decoded
	 * @param encodings
	 *        the encoding types to be used in decoding
	 * @throws XcodeException
	 *         if input is null or empty string or if the named charset is not
	 *         supported
	 */
	static public void testDecode ( final String input, final String[] encodings )
			throws XcodeException {
		byte[] inputarray = null;
		Map<String, String> output = null;
		String variant = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		final String inputString = input.trim();

		if ( inputString.length() == 0 || inputString.charAt( 0 ) == '#' ) {
			Debug.pass( inputString );
			return;
		}

		try {
			inputarray = Hex.decodeBytes( inputString );
			if ( encodings == null || encodings.length == 0 ) {
				output = Native.decode( inputarray );
			}
			else {
				output = Native.decode( inputarray, encodings );
			}
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		System.out.println( inputString );
		final Iterator<String> i = output.keySet().iterator();
		String encoding = null;
		while ( i.hasNext() ) {
			encoding = i.next();
			variant = output.get( encoding );
			System.out.println( "	" + encoding + "	"
					+ Hex.encode( variant.toCharArray() ) );
		}
		System.out.println( output.size() );
	}


	/**
	 * Return only the options for this command. The usage and class name are
	 * populated by the super class.
	 */
	@Override
	public String getUsageOptions () {
		return "(--encode | --decode) file=<file> <space separated encoding list>";
	}

}