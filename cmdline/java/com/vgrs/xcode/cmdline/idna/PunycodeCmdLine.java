/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.idna;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Utf16;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * This tool compresses and converts Unicode data into an ASCII compatible
 * sequence. This algorithm was designed for use with IDNA. No other ACE
 * encoding is supported by the IETF. The IDNA RFC gives applications permission
 * to choose whether or not to exclude ASCII characters which are not a letter,
 * digit, or hyphen. If the –3 switch is given, these codepoints are allowed to
 * be encoded by Punycode.
 * <p>
 * <tt>Usage:</tt> com.vgrs.xcode.cmdline.idna.PunycodeCmdLine [-3] (--encode |
 * --decode) file=[file name]
 * <ul>
 * <li>-3 => do NOT enforce Std 3 Ascii Rules
 * </ul>
 * <p>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 16, 2010
 */
public class PunycodeCmdLine extends CmdLine {

	/**
	 * @param args
	 *        the command line arguments.
	 */
	public PunycodeCmdLine ( String[] args ) {
		super( args );
	}

	/**
	 * An instance of {@link Punycode}
	 */
	static private Punycode punycode;


	static public void main ( String args[] ) {
		final PunycodeCmdLine cmd = new PunycodeCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( !options.has( SWITCH_FILE ) ) {
			cmd.showUsage();
			return;
		}

		final File input = new File( options.get( SWITCH_FILE ) );
		boolean useStd3AsciiRules = true;

		if ( options.has( "3" ) ) {
			useStd3AsciiRules = false;
		}
		punycode = new Punycode( useStd3AsciiRules );

		if ( options.has( "encode" ) ) {
			testEncode( input );
		}
		else if ( options.has( "decode" ) ) {
			testDecode( input );
		}
		else {
			cmd.showUsage();
		}
	}


	/**
	 * Encode the Unicode code points in the input file into Punycode string.
	 * 
	 * @param input
	 *        file containing Unicode code points to be encoded.
	 */
	static public void testEncode ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testEncode( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Decode Punycode strings in the input file into Unicode code points
	 * 
	 * @param input
	 *        file containing punycode strings to be decoded.
	 */
	static public void testDecode ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testDecode( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Encode the Unicode code points in the input string into Punycode string.
	 * 
	 * @param input
	 *        string containing Unicode code points to be encoded.
	 */
	static public void testEncode ( final String input ) throws XcodeException {
		int[] inputarray = null;
		char[] output = null;
		int[] check = null;

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
			inputarray = Hex.decodeInts( inputString );
			output = punycode.domainEncode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = punycode.domainDecode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	FATAL:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		/*
		 * During ace encode, all delimiters will be converted to the ascii
		 * fullstop. This same replacement must be done before applying the
		 * round-trip check to avoid false MISMATCH based on a valid delimiter
		 * replacement.
		 */
		for ( int i = 0; i < inputarray.length; i++ ) {
			for ( int j = 1; j < Idna.INT_DELIMITERS.length; j++ ) {
				if ( inputarray[ i ] == Idna.INT_DELIMITERS[ j ] ) {
					inputarray[ i ] = Idna.ACE_DELIMITER;
				}
			}
		}

		if ( !Arrays.equals( inputarray, check ) ) {
			Debug.fail( inputString + "	MISMATCH	" + Hex.encode( check ) );
			return;
		}

		Debug.pass( new String( output ) );
	}


	/**
	 * Decode the Punycode string into Unicode code points
	 * 
	 * @param input
	 *        punycode string to be decoded.
	 */
	static public void testDecode ( final String input ) throws XcodeException {
		char[] inputarray = null;
		int[] output = null;
		char[] check = null;

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
			inputarray = inputString.toCharArray();
			output = punycode.domainDecode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = punycode.domainEncode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	FATAL:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( Utf16.isDnsCompatible( inputarray )
				&& !inputString.equalsIgnoreCase( new String( check ) ) ) {
			Debug.fail( inputString + " MISMATCH " + new String( check ) );
			return;
		}

		Debug.pass( Hex.encode( output ) );
	}


	/*
	 * (non-Javadoc)
	 * @see com.vgrs.xcode.cmdline.CmdLine#getUsageOptions()
	 */
	@Override
	public String getUsageOptions () {
		return " [-3] (--encode | --decode) file=<file>\n"
				+ "-3 => do NOT enforce Std 3 Ascii Rules";
	}

}