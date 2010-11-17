/**************************************************************************
 *                                                                        *
 * The information in this document is proprietary to VeriSign, Inc.      *
 * It may not be used, reproduced or disclosed without the written        *
 * approval of VeriSign.                                                  *
 *                                                                        *
 * VERISIGN PROPRIETARY & CONFIDENTIAL INFORMATION                        *
 *                                                                        *
 *                                                                        *
 * Copyright (c) 2010 VeriSign, Inc.  All rights reserved.                *
 *                                                                        *
 *************************************************************************/

package com.vgrs.xcode.cmdline.unicodedata;

import java.io.File;
import java.util.Iterator;

import com.vgrs.xcode.cmdline.CmdLine;
import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.cmdline.CommandLineArgs;
import com.vgrs.xcode.util.Datafile;
import com.vgrs.xcode.util.Debug;
import com.vgrs.xcode.util.XcodeException;

/**
 * Prints the properties of Unicode code points specified in the input file. The
 * Unicode code points can be separated by spaces and will be treated as
 * hexadecimal values. For each code point, the following properties are
 * printed:
 * <ul>
 * <li>The script to which the code point belongs to.
 * <li>Bidi class
 * <li>Joining Type
 * <li>Canonical Combining Class
 * <li>A boolean indicating if the code point is a combining mark character.
 * <li>A boolean indicating if the code point is prohibited as per IDNA2008
 * specification.
 * <li>A boolean indicating if the code point is allowed in NFKC normalization.
 * </ul>
 * 
 * @author nchigurupati
 * @version 1.0 Jul 7, 2010
 */
public class UnicodeDataCmdLine extends CmdLine {

	/**
	 * @param args
	 */
	public UnicodeDataCmdLine ( String[] args ) {
		super( args );
	}


	@Override
	public String getUsageOptions () {
		return "file=<file>";
	}


	static public void main ( String args[] ) {
		final UnicodeDataCmdLine cmd = new UnicodeDataCmdLine( args );
		final CommandLineArgs options = cmd.getOptions();

		if ( !options.has( SWITCH_FILE ) ) {
			cmd.showUsage();
			return;
		}
		final File inputFile = new File( options.get( SWITCH_FILE ) );

		int[] input_decoded = null;
		String derivedProperty = null;
		String scriptProperty = null;
		String bidiProperty = null;
		char joiningTypeProperty = 0;
		int canonicalCombiningClass = -1;
		boolean prohibitedCodePointFlag = false;
		boolean normalizedNFCCodePointFlag = false;
		boolean combiningMarkFlag = false;

		try {
			final Iterator<String> data = Datafile.getIterator( inputFile );

			while ( data.hasNext() ) {
				String input = data.next();

				if ( input == null ) {
					continue;
				}

				input = input.trim();

				if ( input.length() == 0 || input.charAt( 0 ) == '#' ) {
					continue;
				}

				derivedProperty = null;
				scriptProperty = null;
				bidiProperty = null;
				joiningTypeProperty = 0;

				try {
					input_decoded = Hex.decodeInts( input );
				}
				catch ( final XcodeException x ) {
					input_decoded = Unicode.encode( input.toCharArray() );
				}

				if ( input_decoded.length > 1 ) {

					for ( int j = input_decoded[ 0 ]; j <= input_decoded[ 1 ]; j++ ) {
						derivedProperty =
								UnicodeData.getCodePointDerivedProperty( j ).toString();
						scriptProperty = UnicodeData.getScript( j );
						bidiProperty = UnicodeData.getBidiClass( j );
						joiningTypeProperty = UnicodeData.getJoiningType( j );
						canonicalCombiningClass = UnicodeData.getCanonicalClass( j );
						prohibitedCodePointFlag =
								UnicodeData.isDisallowedOrUnassignedCodePoint( j );
						normalizedNFCCodePointFlag =
								UnicodeData.isNormalized( input_decoded );
						combiningMarkFlag = UnicodeData.isCombiningMark( j );

						Debug.log( toHexString( j ) + " ; " + "DerivedProperty <"
								+ derivedProperty + "> ; " + "Script <" + scriptProperty + ">"
								+ " ; " + "BidiClass <" + bidiProperty + ">" + " ; "
								+ "JoiningType <" + joiningTypeProperty + ">" + " ; "
								+ "CanonicalCombiningClass <" + canonicalCombiningClass + ">"
								+ " ; " + "isCombiningMark <"
								+ Boolean.toString( combiningMarkFlag ) + ">" + " ; "
								+ "isProhibitedCodePoint <"
								+ Boolean.toString( prohibitedCodePointFlag ) + ">" + " ; "
								+ "isNormalized <"
								+ Boolean.toString( normalizedNFCCodePointFlag ) + ">" );

					}
				}
				else {
					derivedProperty =
							UnicodeData
									.getCodePointDerivedProperty( input_decoded[ 0 ] )
									.toString();
					scriptProperty = UnicodeData.getScript( input_decoded[ 0 ] );
					bidiProperty = UnicodeData.getBidiClass( input_decoded[ 0 ] );
					joiningTypeProperty = UnicodeData.getJoiningType( input_decoded[ 0 ] );
					canonicalCombiningClass =
							UnicodeData.getCanonicalClass( input_decoded[ 0 ] );
					prohibitedCodePointFlag =
							UnicodeData
									.isDisallowedOrUnassignedCodePoint( input_decoded[ 0 ] );
					normalizedNFCCodePointFlag = UnicodeData.isNormalized( input_decoded );
					combiningMarkFlag = UnicodeData.isCombiningMark( input_decoded[ 0 ] );

					Debug.log( toHexString( input_decoded[ 0 ] ) + " ; "
							+ "DerivedProperty <" + derivedProperty + ">" + " ; "
							+ "Script <" + scriptProperty + ">" + " ; " + "BidiClass <"
							+ bidiProperty + ">" + " ; " + "JoiningType <"
							+ joiningTypeProperty + ">" + " ; " + "CanonicalCombiningClass <"
							+ canonicalCombiningClass + ">" + " ; " + "isCombiningMark <"
							+ Boolean.toString( combiningMarkFlag ) + ">" + " ; "
							+ "isProhibitedCodePoints <"
							+ Boolean.toString( prohibitedCodePointFlag ) + ">" + " ; "
							+ "isNormalized <"
							+ Boolean.toString( normalizedNFCCodePointFlag ) + ">" );

				}

			} // END of while(data.hasNext())
		}
		catch ( final Exception x ) {
			x.printStackTrace();
		}

	}


	public static String leftPad ( String s ) {
		String result = s;
		if ( result.length() < 4 ) {
			for ( int i = result.length(); i < 4; i++ ) {
				result = "0" + result;
			}
		}
		return result;
	}


	private static String toHexString ( int input ) {
		if ( input < 0 ) {
			return null;
		}
		final String output = Integer.toString( input, 16 );
		return leftPad( output.toUpperCase() );
	}

}