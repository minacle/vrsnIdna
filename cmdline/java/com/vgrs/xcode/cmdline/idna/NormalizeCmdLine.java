/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.idna;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.idna.Normalize;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Normalize the input sequence using the NFKC algorithm.
 * <p>
 * <tt>Usage:</tt> com.vgrs.xcode.cmdline.idna.NormalizeCmdLine [-c] file=[file
 * name]
 * <ul>
 * <li>-c => test for conformance
 * </ul>
 * <p>
 * When the -c option is specified, the input should be 5 fields delimited by a
 * ';'. The conformance test is applied as follows:
 * <p>
 * c4 == NFKC(c1) == NFKC(c2) == NFKC(c3) == NFKC(c4) == NFKC(c5)
 * <p>
 * where c1 is the first field, c2 is the second field, etc. A sample line is
 * shown below:
 * <p>
 * 1E0A;1E0A;0044 0307;1E0A;0044 0307;
 * 
 * @author nchigurupati
 * @version 1.0 Aug 16, 2010
 */
public class NormalizeCmdLine extends CmdLine {

	/**
	 * @param args
	 *        the command line arguments.
	 */
	public NormalizeCmdLine ( String[] args ) {
		super( args );
	}


	/**
	 * If the -c option has been specified the normalized input is checked for
	 * conformance.
	 * 
	 * @param args
	 *        the command line arguments.
	 */
	static public void main ( String args[] ) {
		final NormalizeCmdLine cmd = new NormalizeCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( !options.has( SWITCH_FILE ) ) {
			cmd.showUsage();
			return;
		}
		final File input = new File( options.get( SWITCH_FILE ) );

		if ( options.has( "c" ) ) {
			testConformance( input );
		}
		else {
			testExecute( input );
		}
	}


	/**
	 * Normalize the data in the given input file.
	 * 
	 * @param input
	 *        file containing the data to be normalized.
	 */
	static public void testExecute ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testExecute( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Normalize the Unicode code points given in the input
	 * 
	 * @param input
	 *        the input to be normalized.
	 * @throws XcodeException
	 */
	static public void testExecute ( final String input ) throws XcodeException {
		int[] output = null;

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
			output = Normalize.execute( Hex.decodeInts( inputString ) );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		Debug.pass( Hex.encode( output ) );
	}


	/**
	 * File containing data in the specified format to test for conformance.
	 * 
	 * @param input
	 *        the file containing the input to be normalized and tested for
	 *        conformance.
	 */
	static public void testConformance ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testConformance( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Test the given input for conformance
	 * 
	 * @param input
	 *        Input to be tested for conformance
	 */
	static public void testConformance ( final String input ) {

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		String inputString = input.trim();
		final int hashIndex = inputString.indexOf( '#' );
		if ( hashIndex > 0 ) {
			inputString = inputString.substring( 0, hashIndex );
			inputString = inputString.trim();
		}

		if ( inputString.length() == 0 || inputString.charAt( 0 ) == '#'
				|| inputString.charAt( 0 ) == '@' ) {
			// Debug.pass(input);
			return;
		}

		//
		// parse the data into columns
		//
		final StringTokenizer st = new StringTokenizer( inputString, ";" );
		final int tokenCount = st.countTokens();
		if ( tokenCount < 5 ) {
			Debug.fail( inputString + "	ERROR: Invalid number of columns" );
			return;
		}
		int index = 0;
		final String[] column = new String[tokenCount];
		while ( st.hasMoreTokens() ) {
			column[ index++ ] = st.nextToken();
		}

		//
		// parse first 5 columns from hex format
		//
		final int[][] c = new int[5][];
		try {
			for ( index = 0; index < 5; index++ ) {
				c[ index ] = Hex.decodeInts( column[ index ] );
			}
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:[" + column[ index ] + "]"
					+ x.getCode() + "	" + x.getMessage() );
			return;
		}

		try {
			//
			// ensure conformance for NFKC
			// c4 == NFKC(c1) == NFKC(c2) == NFKC(c3) == NFKC(c4) == NFKC(c5)
			//
			for ( int i = 0; i < 5; i++ ) {
				final int[] nfkc = Normalize.execute( getCopy( c[ i ] ) );
				if ( !Arrays.equals( c[ 3 ], nfkc ) ) {
					Debug.fail( inputString
							+ "	ERROR: conformance test failed: col[4] = '"
							+ Hex.encode( c[ 3 ] ) + "': col[" + (i + 1) + "] = '"
							+ Hex.encode( c[ i ] ) + "': NFKC[" + (i + 1) + "] = '"
							+ Hex.encode( nfkc ) + "'" );
				}
			}

		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

	}


	/**
	 * Utility method to make a copy of an array of ints.
	 * 
	 * @param arr
	 *        the source array
	 * @return a copy of the source array.
	 */
	static private int[] getCopy ( int[] arr ) {
		final int[] copy = new int[arr.length];
		System.arraycopy( arr, 0, copy, 0, arr.length );
		return copy;
	}


	/*
	 * (non-Javadoc)
	 * @see com.vgrs.xcode.cmdline.CmdLine#getUsageOptions()
	 */
	@Override
	public String getUsageOptions () {
		return "[-c] file=<file>";
	}

}