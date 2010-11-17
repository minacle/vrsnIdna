/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline.ext;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Native;
import com.vgrs.xcode.ext.Convert;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.idna.Race;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Test driver for Convert
 */

/**
 * This class converts input directly between Race, Punycode, and any Java
 * supported native encoding, bypassing intermediate steps. This routine is
 * useful in applications migrating Race encoded domains to Punycode. This class
 * also serves to prepare natively encoded data for IDN registration. See the
 * [ENCODINGS] reference for a complete list of supported native encodings.
 * <p>
 * <tt>Usage : </tt> com.vgrs.xcode.cmdline.common.ConvertCmdLine [-3acsx]
 * file=[file name] [input type] [output types]
 * <ul>
 * <li>-3 => do NOT enforce Std 3 Ascii Rules
 * <li>-c => do NOT perform IDNA2008 Protocol validation
 * <li>-x => allow eXceptions during toUnicode
 * <li>--list => Output a list of supported encoding type
 * <li>  <tt>Input type: </tt> ASCII for race and punycode, Native for all others
 * <li>  <tt>Output type : </tt> ASCII for race and punycode, Native for all
 * others
 * 
 * @author nchigurupati
 * @version 1.0 Aug 10, 2010
 */
public class ConvertCmdLine extends CmdLine {

	/**
	 * The {@link Convert} instance
	 */
	private Convert convert = null;


	/**
	 * Return only the options for this command. The usage and class name are
	 * populated by the super class.
	 */
	@Override
	public String getUsageOptions () {
		return "[-3cx] [--list] file=<file> <input type> <output types>\n"
				+ " -3 => do NOT enforce Std 3 Ascii Rules\n"
				+ " -c => do NOT perform IDNA2008 Protocol validation\n"
				+ " -x => allow eXceptions during toUnicode\n"
				+ " --list => Output a list of supported encoding types\n";
	}


	/**
	 * @param args
	 *        the command line arguments.
	 */
	public ConvertCmdLine ( String[] args ) {
		super( args );
	}


	/**
	 * Sets convert value to convert
	 * 
	 * @param convert
	 *        the {@link Convert} instance to set
	 */
	public void setConvert ( Convert convert ) {
		this.convert = convert;
	}


	static public void main ( String args[] ) throws XcodeException {

		final ConvertCmdLine cmd = new ConvertCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( options.has( "list" ) ) {
			listEncodings();
			return;
		}

		if ( !options.has( SWITCH_FILE ) || options.getUnassigned().size() < 2 ) {
			cmd.showUsage();
			return;
		}

		boolean useStd3AsciiRules = true;
		boolean idna2008Protocol = true;
		boolean toUnicodeExceptionFlag = false;

		if ( options.has( "3" ) ) {
			useStd3AsciiRules = false;
		}
		if ( options.has( "c" ) ) {
			idna2008Protocol = false;
		}
		if ( options.has( "x" ) ) {
			toUnicodeExceptionFlag = true;
		}
		final File input = new File( options.get( SWITCH_FILE ) );
		final String itype = options.getUnassigned().get( 0 );
		final Race race = new Race( useStd3AsciiRules );
		final Punycode punycode = new Punycode( useStd3AsciiRules );
		final Idna iRace =
				new Idna( race, toUnicodeExceptionFlag, idna2008Protocol );
		final Idna iPunycode =
				new Idna( punycode, toUnicodeExceptionFlag, idna2008Protocol );

		cmd.setConvert( new Convert( iRace, iPunycode ) );

		final String[] otype = new String[options.getUnassigned().size() - 1];
		for ( int i = 1; i < options.getUnassigned().size(); i++ ) {
			otype[ i - 1 ] = options.getUnassigned().get( i );
		}

		cmd.testExecute( input, itype, otype );
	}


	/**
	 * List the supported native encodings.
	 */
	static private void listEncodings () {
		for ( final String element : Native.ENCODINGS ) {
			Debug.pass( element );
		}
	}


	/**
	 * Converts the input in the file which is in the specified input type to the
	 * specified output types.
	 * 
	 * @param file
	 *        file name of the file containing the input in the specified input
	 *        type
	 * @param itype
	 *        the input type of the data in the given file
	 * @param otype
	 *        an array of output types to be converted into.
	 */
	private void testExecute ( File file, String itype, String[] otype ) {
		try {
			final Iterator<String> data = Datafile.getIterator( file );
			if ( otype != null && otype.length == 1 ) {
				while ( data.hasNext() ) {
					testExecute( data.next(), itype, otype[ 0 ] );
				}
			}
			else {
				while ( data.hasNext() ) {
					testExecute( data.next(), itype, otype );
				}
			}
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}
	}


	/**
	 * Converts the input in the file which is in the specified input type to the
	 * specified output type.
	 * 
	 * @param input
	 *        data in the specified input type to be converted into the specified
	 *        output type
	 * @param itype
	 *        the input type of the data in the given file
	 * @param otype
	 *        output type to be converted into.
	 */
	private void testExecute ( final String input, final String itype,
			final String otype ) throws XcodeException {

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

		final boolean asciiInput =
				itype.equalsIgnoreCase( Convert.RACE_ENCODING )
						|| itype.equalsIgnoreCase( Convert.PUNYCODE_ENCODING );
		final boolean asciiOutput =
				otype.equalsIgnoreCase( Convert.RACE_ENCODING )
						|| otype.equalsIgnoreCase( Convert.PUNYCODE_ENCODING );

		try {
			if ( asciiInput ) {
				output = this.convert.execute( inputString, itype, otype );
			}
			else {
				output =
						this.convert.execute( new String( Hex.decodeBytes( inputString ) ),
								itype, otype );
			}
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + " ERROR:" + x.getCode() + " " + x.getMessage() );
			return;
		}

		if ( asciiOutput ) {
			Debug.pass( output );
		}
		else {
			Debug.pass( Hex.encode( Native.getEncoding( output ) ) );
		}
	}


	/**
	 * Converts the input in the file which is in the specified input type to the
	 * specified output types.
	 * 
	 * @param input
	 *        data in the specified input type to be converted into the specified
	 *        output type
	 * @param itype
	 *        the input type of the data in the given file
	 * @param otype
	 *        an array of output types to be converted into.
	 */
	private void testExecute ( final String input, final String itype,
			final String[] otype ) throws XcodeException {

		if ( input == null ) {
			Debug.pass( "" );
			return;
		}
		final String inputString = input.trim();

		if ( inputString.length() == 0 || inputString.charAt( 0 ) == '#' ) {
			Debug.pass( inputString );
			return;
		}

		final boolean asciiInput =
				itype.equalsIgnoreCase( Convert.RACE_ENCODING )
						|| itype.equalsIgnoreCase( Convert.PUNYCODE_ENCODING );

		Properties output;
		try {
			if ( asciiInput ) {
				output = this.convert.execute( inputString, itype, otype );
			}
			else {
				output =
						this.convert.execute( new String( Hex.decodeBytes( inputString ) ),
								itype, otype );
			}
		}
		catch ( final XcodeException x ) {
			Debug.fail( inputString + " ERROR:" + x.getCode() + " " + x.getMessage() );
			return;
		}

		final Iterator<?> iterator = output.keySet().iterator();
		boolean asciiOutput;
		String key;
		String value;

		System.out.println( "\n" + inputString );
		while ( iterator.hasNext() ) {
			key = (String) iterator.next();
			value = output.getProperty( key );
			asciiOutput =
					key.equalsIgnoreCase( Convert.RACE_ENCODING )
							|| key.equalsIgnoreCase( Convert.PUNYCODE_ENCODING );
			System.out.print( "	" + key + "	" );
			if ( asciiOutput ) {
				System.out.println( value );
			}
			else {
				System.out.println( Hex.encode( Native.getEncoding( value ) ) );
			}
		}
	}

}