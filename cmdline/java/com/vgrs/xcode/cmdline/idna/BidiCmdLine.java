/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.idna;

import java.io.File;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.idna.Bidi;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class asserts that the given set of Unicode code points are in
 * compliance with IDNA2008 Bidi rules.
 * <p>
 * <tt>Usage:</tt> com.vgrs.xcode.cmdline.idna.BidiCmdLine file=[file name]
 * 
 * @author nchigurupati
 * @version 1.0 Aug 10, 2010
 */
public class BidiCmdLine extends CmdLine {

	/**
	 * @param args
	 *        the command line arguments.
	 */
	public BidiCmdLine ( String[] args ) {
		super( args );
	}


	static public void main ( String args[] ) {
		final BidiCmdLine cmd = new BidiCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();
		if ( !options.has( SWITCH_FILE ) ) {
			cmd.showUsage();
			return;
		}

		testExecute( new File( options.get( SWITCH_FILE ) ) );
	}


	/**
	 * Asserts that the given Unicode code points in the file are in compliance
	 * with IDNA2008 Bidi rules.
	 * 
	 * @param input
	 *        the file containing code points separated by spaces and prefixed
	 *        with either a \U or 0x
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
	 * Asserts that the sequence of Unicode code points are in compliance with
	 * IDNA2008 Bidi rules.
	 * 
	 * @param input
	 *        code points separated by spaces and prefixed with either a \U or 0x
	 */
	static public void testExecute ( final String input ) {
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
			Bidi.assertCompliance( Hex.decodeInts( inputString ) );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		Debug.pass( inputString );
	}


	/*
	 * (non-Javadoc)
	 * @see com.vgrs.xcode.cmdline.CmdLine#getUsageOptions()
	 */
	@Override
	public String getUsageOptions () {
		return "file=<file>";
	}

}