
package com.vgrs.xcode.cmdline.idna;

import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.idna.Ace;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.util.XcodeException;

/**
 * Demo class to show how Idna.domainToUnicode() and domainToAscii() methods
 * could be used.
 * 
 * @author jcolosi
 * @version 1.0 Aug 10, 2010
 */
public class DemoCmdLine {

	static private boolean useStd3AsciiRules = true;
	static private boolean throwExceptions = true;
	static private boolean idnaRegistrationProtocol = true;


	static public void main ( String[] args ) throws XcodeException {
		final Ace ace = new Punycode( useStd3AsciiRules );
		final Idna idna = new Idna( ace, throwExceptions, idnaRegistrationProtocol );

		final String utf16 = "résumé";
		final int[] unicode = Unicode.encode( utf16.toCharArray() );
		final String punycode = new String( idna.domainToAscii( unicode ) );
		final int[] check = idna.domainToUnicode( punycode.toCharArray() );

		System.out.println( "utf16: " + utf16 );
		System.out.println( "unicode: " + Hex.encode( unicode ) );
		System.out.println( "punycode: " + punycode );
		System.out.println( "check: " + Hex.encode( check ) );
	}
}
