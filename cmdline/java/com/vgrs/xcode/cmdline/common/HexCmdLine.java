/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.common;

import java.io.File;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Encode an input file using Hexidecimal codes, or interpret those codes and
 * decode to ASCII.
 * <ul>
 * <li><tt>Usage:</tt> com.vgrs.xcode.cmdline.common.HexCmdLine (--encode |
 * --decode) file=[file name]
 * <li><tt>Input type:</tt> Raw binary for encode, Hexadecimal for decode
 * <li><tt>Output type:</tt> Hexadecimal for encode, Raw binary for decode
 * </ul>
 * 
 * @author jcolosi
 * @version 1.0 Jul 1, 2010
 */
public class HexCmdLine extends CmdLine {

	static private String SWITCH_ENCODE = "encode";
	static private String SWITCH_DECODE = "decode";


	/**
	 * Construct a class to perform command line actions. Input args must match
	 * the specified switches.
	 * 
	 * @param args
	 */
	public HexCmdLine ( String[] args ) {
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
	 * Entry point, integrates with the OS. Processes input arguments and executes
	 * command line logic.
	 * 
	 * @param args
	 *        the command line arguments
	 */
	static public void main ( String args[] ) {
		final CmdLine cmd = new HexCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( options.getCount() == 2 && options.has( SWITCH_FILE ) ) {
			final File inputFile = new File( options.get( SWITCH_FILE ) );
			if ( options.has( SWITCH_ENCODE ) ) {
				encode( inputFile );
				return;
			}
			if ( options.has( SWITCH_DECODE ) ) {
				decode( inputFile );
				return;
			}
		}
		cmd.showUsage();
	}


	/**
	 * Encode the data in the input File using hexadecimal values.
	 * 
	 * @param input
	 *        the file containing the integer values to be encoded as hexadecimal
	 *        string.
	 */
	static public void encode ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				encode( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Interpret the input File as hexadecimal values and decode to ASCII.
	 * 
	 * @param input
	 *        the file containing the hexadecimal values to be decoded to ASCII.
	 */
	static public void decode ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				decode( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Encode the input String using hexadecimal values.
	 * 
	 * @param input
	 *        the string to be encoded using hexadecimal values.
	 */
	static public void encode ( String input ) {
		String output = null;

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		// input = input.trim();

		if ( input.length() == 0 || input.charAt( 0 ) == '#' ) {
			Debug.pass( input );
			return;
		}

		try {
			output = Hex.encode( input.toCharArray() );
		}
		catch ( final XcodeException x ) {
			Debug.fail( input + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		Debug.pass( output );
	}


	/**
	 * Interpret the input String as hexadecimal values and decode to ASCII.
	 * 
	 * @param input
	 *        the string to be decoded to ASCII by interpreting the hexadecimal
	 *        values.
	 */
	static public void decode ( final String input ) {
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
			System.out.println( new String( Hex.decodeBytes( inputString ) ) );
		}
		catch ( final XcodeException x1 ) {
			try {
				System.out.println( new String( Hex.decodeChars( inputString ) ) );
			}
			catch ( final XcodeException x2 ) {
				try {
					System.out.println( new String( Unicode.decode( Hex
							.decodeInts( inputString ) ) ) );
				}
				catch ( final XcodeException x3 ) {
					Debug.fail( inputString + "	ERROR:" + x3.getCode() + "	"
							+ x3.getMessage() );
					return;
				}
			}
		}
	}

}