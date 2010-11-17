/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.ext;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.cmdline.common.Base32CmdLine;
import com.vgrs.xcode.ext.EncodingVariants;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.idna.Race;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Uses the Native object to generate a list of encoding variants given an ACE
 * encoded sequence. All valid encoding variants are ACE encoded and returned.
 * <ul>
 * <li><tt>Usage:</tt> com.vgrs.xcode.cmdline.ext.EncodingVariantsCmdLine [-r]
 * file=[file name] [encoding list]
 * <li>-r => use Race for ACE encoding (Punycode by default)
 * <li><tt>Input type:</tt> ASCII
 * <li><tt>Output type:</tt> ASCII
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 19, 2010
 */
public class EncodingVariantsCmdLine extends CmdLine {

	static public EncodingVariants ev;


	/**
	 * @param args
	 *        the command line arguments.
	 */
	public EncodingVariantsCmdLine ( String[] args ) {
		super( args );
	}


	static public void main ( String args[] ) {

		final CmdLine cmd = new Base32CmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( options.has( "r" ) ) {
			ev = new EncodingVariants( new Race() );
		}
		else {
			ev = new EncodingVariants( new Punycode() );
		}

		if ( !options.has( SWITCH_FILE ) ) {
			cmd.showUsage();
			return;
		}

		final File inputFile = new File( options.get( SWITCH_FILE ) );
		final List<String> encodingList = options.getUnassigned();
		String[] encodings = null;
		if ( encodingList != null && encodingList.size() > 1 ) {
			encodings = encodingList.toArray( new String[encodingList.size()] );
		}

		testExecute( inputFile, encodings );
	}


	/**
	 * Convert the ACE encoded strings in the file into a list of native
	 * encodings.
	 * 
	 * @param input
	 *        the file containing a list of ACE encoded strings.
	 * @param encodings
	 *        A list of native encodings to convert into.
	 */
	static public void testExecute ( File input, String[] encodings ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testExecute( data.next(), encodings );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Convert the ACE encoded string into a list of native encodings.
	 * 
	 * @param input
	 *        ACE encoded string
	 * @param encodings
	 *        A list of native encodings to convert into.
	 */
	static public void testExecute ( String input, String[] encodings ) {
		String[] output = null;

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
			output = ev.execute( input, encodings );
		}
		catch ( final XcodeException x ) {
			Debug.fail( input + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( output == null || output.length == 0 ) {
			Debug.pass( input + " -> " );
		}
		else if ( output.length == 1 ) {
			Debug.pass( input + " -> " + output[ 0 ] );
		}
		else {
			System.out.println( "\n" + input );
			for ( final String element : output ) {
				Debug.pass( "-> " + element );
			}
			System.out.println();
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.vgrs.xcode.cmdline.CmdLine#getUsageOptions()
	 */
	@Override
	public String getUsageOptions () {
		return "[-r] file=<file> <encoding list>\n"
				+ " -r => use Race for ACE encoding (Punycode by default)";
	}

}