/*
 * (c) VeriSign Inc., 2010, All rights reserved
 */

package com.vgrs.xcode.cmdline.ext;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.cmdline.common.Base32CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.ext.DCE;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Makes a sequence of bytes compatible with the Domain Naming System. The
 * algorithm uses the Base 32 encoding to create data on the range [a-z, 2-7].
 * Then data sequences longer than 63 characters are delimited using a FULLSTOP
 * character.
 * <ul>
 * <li><tt>Usage:</tt> com.vgrs.xcode.cmdline.ext.DCECmdLine (--encode |
 * --decode) file=[file name]
 * <li><tt>Input type:</tt> Native for encode, ASCII for decode
 * <li><tt>Output type:</tt> ASCII for encode, Native for decode
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 19, 2010
 */
public class DCECmdLine extends CmdLine {

	static private String SWITCH_ENCODE = "encode";
	static private String SWITCH_DECODE = "decode";


	/**
	 * @param args
	 *        the command line arguments.
	 */
	public DCECmdLine ( String[] args ) {
		super( args );
	}


	static public void main ( String args[] ) {
		final CmdLine cmd = new Base32CmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( options.getCount() == 2 && options.has( SWITCH_FILE ) ) {
			final File inputFile = new File( options.get( SWITCH_FILE ) );
			if ( options.has( SWITCH_ENCODE ) ) {
				testEncode( inputFile );
				return;
			}
			if ( options.has( SWITCH_DECODE ) ) {
				testDecode( inputFile );
				return;
			}
		}
		cmd.showUsage();
	}


	/**
	 * Convert the data in the file into a dns-compatible string
	 * 
	 * @param input
	 *        the file containing the data to be coverted into a dns-compatible
	 *        string.
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
	 * Convert the data in the file which contains dns-compatible string(s) into
	 * array of bytes.
	 * 
	 * @param input
	 *        the input file with dns-compatible string(s).
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
	 * Convert the input into a dns-compatible string
	 * 
	 * @param input
	 *        the data to be converted into a dns-compatible string.
	 */
	static public void testEncode ( String input ) throws XcodeException {
		byte[] inputarray = null;
		char[] output = null;
		byte[] check = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		input = input.trim();

		if ( input.length() == 0 || input.charAt( 0 ) == '#' ) {
			Debug.pass( input );
			return;
		}

		try {
			inputarray = Hex.decodeBytes( input );
			output = DCE.encode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( input + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = DCE.decode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( input + " FATAL:" + x.getCode() + " " + x.getMessage() );
			return;
		}

		if ( !Arrays.equals( inputarray, check ) ) {
			Debug.fail( input + " MISMATCH " + Hex.encode( check ) );
			return;
		}

		Debug.pass( new String( output ) );
	}


	/**
	 * Convert the dns-compatible string into array of bytes
	 * 
	 * @param input
	 *        a dns-compatible string.
	 */
	static public void testDecode ( String input ) throws XcodeException {
		char[] inputarray = null;
		byte[] output = null;
		char[] check = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		input = input.trim();

		if ( input.length() == 0 || input.charAt( 0 ) == '#' ) {
			Debug.pass( input );
			return;
		}

		try {
			inputarray = input.toCharArray();
			output = DCE.decode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( input + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = DCE.encode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( input + " FATAL:" + x.getCode() + " " + x.getMessage() );
			return;
		}

		if ( !input.equalsIgnoreCase( new String( check ) ) ) {
			Debug.fail( input + "	MISMATCH	" + new String( check ) );
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
		return "(--encode | --decode) file=<file>";
	}

}