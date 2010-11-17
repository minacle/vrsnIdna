/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.common;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Base32;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class converts a string of binary bytes into an ASCII compatible
 * sequence using only the characters [a-z, 2-7]. The input sequence is read 5
 * bits at-a-time, and each 5 bits is converted into one of the 32 allowed
 * characters.
 * <ul>
 * <li><tt>Usage:</tt> com.vgrs.xcode.cmdline.common.Base32CmdLine (--encode |
 * --decode) file=[file name]
 * <li><tt>Input type:</tt> Native for encode, ASCII for decode
 * <li><tt>Output type:</tt> ASCII for encode, Native for decode
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 10, 2010
 */
public class Base32CmdLine extends CmdLine {

	static private String SWITCH_ENCODE = "encode";
	static private String SWITCH_DECODE = "decode";


	/**
	 * @param args the command line arguments.
	 */
	public Base32CmdLine ( String[] args ) {
		super( args );
	}


	/**
	 * Return only the options for this command. The usage and class name are
	 * populated by the super class.
	 */
	@Override
	public String getUsageOptions () {
		return "(--encode | --decode) file=<file>";
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
	 * Base32 encode the input in the file.
	 * 
	 * @param input
	 *        the file containing the input to be base32 encoded.
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
	 * Base32 decode the input in the file.
	 * 
	 * @param input
	 *        the file containing the input to be base32 decoded.
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
	 * Base32 encode the given input
	 * 
	 * @param input
	 *        Code points prefixed with \U or 0x.
	 * @throws XcodeException
	 */
	static public void testEncode ( final String input ) throws XcodeException {
		byte[] inputarray = null;
		char[] output = null;
		byte[] check = null;

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
			output = Base32.encode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = Base32.decode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	FATAL:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( !Arrays.equals( inputarray, check ) ) {
			Debug.fail( inputString + "	MISMATCH	" + Hex.encode( check ) );
			return;
		}

		Debug.pass( new String( output ) );
	}


	/**
	 * Base32 decode the given input
	 * 
	 * @param input
	 *        a base32 encoded string to be decoded.
	 * @throws XcodeException
	 */
	static public void testDecode ( final String input ) throws XcodeException {
		char[] inputarray = null;
		byte[] output = null;
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

		inputarray = inputString.toCharArray();

		try {
			output = Base32.decode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = Base32.encode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	FATAL:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( !input.equalsIgnoreCase( new String( check ) ) ) {
			Debug.fail( inputString + "	MISMATCH	" + new String( check ) );
			return;
		}

		Debug.pass( Hex.encode( output ) );
	}

}