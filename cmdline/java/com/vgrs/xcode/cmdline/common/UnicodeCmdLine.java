/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.common;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class converts between Unicode data and Utf-16 using the surrogate
 * arithmetic specified in the UTF-16 RFC.
 * <ul>
 * <li><tt>Usage:</tt> com.vgrs.xcode.cmdline.common.UnicodeCmdLine (--encode |
 * --decode) file=[file name]
 * <li><tt>Input type:</tt> Utf-16 for encode, Unicode for decode
 * <li><tt>Output type:</tt> Unicode for encode, Utf-16 for decode
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 10, 2010
 */
public class UnicodeCmdLine extends CmdLine {

	static private String SWITCH_ENCODE = "encode";
	static private String SWITCH_DECODE = "decode";


	/**
	 * @param args
	 *        the command line arguments.
	 */
	public UnicodeCmdLine ( String[] args ) {
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


	/**
	 * @param args
	 *        the command line arguments.
	 */
	static public void main ( String args[] ) {
		final CmdLine cmd = new UnicodeCmdLine( args );
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
	 * Encode Utf-16 input as Unicode.
	 * 
	 * @param input
	 *        the file containing Utf-16 input.
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
	 * Decode Unicode input as Utf-16.
	 * 
	 * @param input
	 *        the file containing Unicode input.
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
	 * Encode a string to an array of unicode
	 * 
	 * @param input
	 *        the String to be encoded.
	 * @throws XcodeException
	 *         if the input array is null or empty
	 */
	static public void testEncode ( final String input ) throws XcodeException {
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
			inputarray = Hex.decodeChars( inputString );
			output = Unicode.encode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = Unicode.decode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	FATAL:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( !Arrays.equals( inputarray, check ) ) {
			Debug.fail( inputString + "	MISMATCH	" + Hex.encode( check ) );
			return;
		}

		Debug.pass( Hex.encode( output ) );
	}


	/**
	 * Decode a sequence of Unicode code points to a sequence of characters.
	 * 
	 * @param input
	 *        the String to be decoded.
	 * @throws XcodeException
	 *         if the input array is null or empty
	 */
	static public void testDecode ( final String input ) throws XcodeException {
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
			output = Unicode.decode( inputarray );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			check = Unicode.encode( output );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	FATAL:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( !Arrays.equals( inputarray, check ) ) {
			Debug.fail( inputString + "	MISMATCH	" + Hex.encode( check ) );
			return;
		}

		Debug.pass( Hex.encode( output ) );
	}

}