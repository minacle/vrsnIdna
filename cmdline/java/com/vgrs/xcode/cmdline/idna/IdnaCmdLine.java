
package com.vgrs.xcode.cmdline.idna;

import java.io.File;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.common.Utf16;
import com.vgrs.xcode.idna.Ace;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.idna.Race;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Internationalized Domain Names in Applications. A set of algorithms, which
 * define a way to encode and decode Unicode data making it compatible with the
 * Domain Naming System.
 *<ul>
 * <li><tt>Usage:</tt>com.vgrs.xcode.cmdline.idna.IdnaCmdLine [-3crx] (--toAscii
 * | --toUnicode) file=[file name]
 * <li>-3 => do NOT enforce Std 3 Ascii Rules
 * <li>-c => do NOT perform IDNA2008 Protocol validation
 * <li>-r => use Race for ACE encoding (Punycode by default)
 * <li>-x => allow eXceptions during toUnicode
 * <li><tt>Input type:</tt> Unicode for toAscii, ASCII for toUnicode
 * <li><tt>Output type:</tt> ASCII for toAscii, Unicode for toUnicode
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Aug 10, 2010
 */
public class IdnaCmdLine extends CmdLine {

	/**
	 * An instance of {@link Idna}
	 */
	private Idna idna = null;


	/**
	 * @param args
	 *        the command line arguments
	 */
	public IdnaCmdLine ( String[] args ) {
		super( args );
	}


	/**
	 * Sets idna value to idna
	 * 
	 * @param idna
	 *        the idna to set
	 */
	public void setIdna ( Idna idna ) {
		this.idna = idna;
	}


	/**
	 * @param args
	 *        the command line arguments
	 * @throws XcodeException
	 */
	static public void main ( String args[] ) throws XcodeException {
		final IdnaCmdLine cmd = new IdnaCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		Ace ace = null;

		if ( !options.has( SWITCH_FILE ) ) {
			cmd.showUsage();
			return;
		}
		boolean useStd3AsciiRules = true;
		boolean idna2008Protocol = true;
		boolean toUnicodeExceptionFlag = false;
		boolean isPunycode = true;

		if ( options.has( "3" ) ) {
			useStd3AsciiRules = false;
		}

		if ( options.has( "c" ) ) {
			idna2008Protocol = false;
		}

		if ( options.has( "r" ) ) {
			isPunycode = false;
		}

		if ( options.has( "x" ) ) {
			toUnicodeExceptionFlag = true;
		}

		final File input = new File( options.get( SWITCH_FILE ) );

		if ( isPunycode ) {
			ace = new Punycode( useStd3AsciiRules );
		}
		else {
			ace = new Race( useStd3AsciiRules );
		}

		final Idna idna = new Idna( ace, toUnicodeExceptionFlag, idna2008Protocol );
		cmd.setIdna( idna );
		if ( options.has( "toAscii" ) ) {
			cmd.testToAscii( input );
		}
		else if ( options.has( "toUnicode" ) ) {
			cmd.testToUnicode( input );
		}
		else {
			cmd.showUsage();
			return;
		}

	}


	/**
	 * Converts the code points in the given file to ASCII using the specified ACE
	 * algorithm.
	 * 
	 * @param input
	 *        file containing code points prefixed with \U or 0x.
	 */
	public void testToAscii ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testToAscii( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Converts the given ASCII to a set of Unicode code points using the
	 * specified ACE algorithm.
	 * 
	 * @param input
	 *        file containing ASCII data
	 */
	public void testToUnicode ( File input ) {
		try {
			final Iterator<String> data = Datafile.getIterator( input );
			while ( data.hasNext() ) {
				testToUnicode( data.next() );
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Converts the given set of Unicode code points to ASCII using the specified
	 * ACE algorithm.
	 * 
	 * @param input
	 *        convert the given set of code points to ASCII
	 */
	public void testToAscii ( final String input ) {
		int[] input_decoded = null;
		char[] output = null;

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
			try {
				input_decoded = Hex.decodeInts( inputString );
			}
			catch ( final XcodeException x ) {
				input_decoded = Unicode.encode( inputString.toCharArray() );
			}
			output = this.idna.domainToAscii( input_decoded );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		Debug.pass( new String( output ) );
	}


	/**
	 * Convert the given ASCII string to Unicode code points using the specified
	 * ACE algorithm.
	 * 
	 * @param input
	 * @throws XcodeException
	 */
	public void testToUnicode ( final String input ) throws XcodeException {
		char[] input_decoded = null;
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
			try {
				input_decoded = Hex.decodeChars( inputString );
			}
			catch ( final XcodeException x ) {
				try {
					input_decoded = Unicode.decode( Hex.decodeInts( inputString ) );
				}
				catch ( final XcodeException x2 ) {
					input_decoded = inputString.toCharArray();
				}
			}
			output = this.idna.domainToUnicode( input_decoded );
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + "	ERROR:" + x.getCode() + "	" + x.getMessage() );
			return;
		}

		if ( Utf16.isPrintable( output ) ) {
			Debug.pass( new String( Utf16.contract( output ) ) );
		}
		else {
			Debug.pass( Hex.encode( output ) );
		}
	}


	@Override
	public String getUsageOptions () {
		return "[-3crx] file=<file> (--toAscii | --toUnicode)\n"
				+ " -3 => do NOT enforce Std 3 Ascii Rules\n"
				+ " -c => do NOT perform IDNA2008 Protocol validation\n"
				+ " -r => use Race for ACE encoding (Punycode by default)\n"
				+ " -x => allow eXceptions during toUnicode\n";
	}

}