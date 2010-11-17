
package com.vgrs.xcode.cmdline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collect command line arguments into a set of data structures which are easy
 * to interrogate.
 * <p>
 * Usage:
 * <ul>
 * <li>Key, value pairs are separated by an equals sign. You can have hyphens
 * prior to the argument.
 * <ul>
 * <li>runreport date=6/4/1975 --version=1.5</li>
 * </ul>
 * </li>
 * <li>The single hyphen indicates a set of switches. Each character in the
 * argument is its own switch. Commonly used in the unix tar command.
 * <ul>
 * <li>tar -cvf inputfile</li>
 * </ul>
 * </li>
 * <li>The 'f' switch is special. It can be followed by a file name which will
 * be associated with the switch. (see above)</li>
 * <li>The double hyphen indicates a standalone flag.
 * <ul>
 * <li>format --force</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author jcolosi
 */
public class CommandLineArgs {
	/**
	 * Flag to indicate that the expression should ignore case.
	 */
	static private final int IC = Pattern.CASE_INSENSITIVE;

	/**
	 * Regular expression for a key value pair
	 */
	static private final String RX_KEY_VALUE = "^-*(.+)=(.+)$";

	/**
	 * Regular expression for a double flag
	 */
	static private final String RX_DOUBLE_FLAG = "^--(.+)$";

	/**
	 * Regular expression for a single switch
	 */
	static private final String RX_SINGLE_SWITCH = "^-(.+)";

	/**
	 * The pre-compiled pattern for a key value pair
	 */
	static private final Pattern PT_KEY_VALUE =
			Pattern.compile( RX_KEY_VALUE, IC );

	/**
	 * The pre-compiled pattern for a double flag
	 */
	static private final Pattern PT_DOUBLE_FLAG =
			Pattern.compile( RX_DOUBLE_FLAG, IC );

	/**
	 * The pre-compiled pattern for a single switch
	 */
	static private final Pattern PT_SINGLE_SWITCH =
			Pattern.compile( RX_SINGLE_SWITCH, IC );

	/**
	 * A common identifier typically used to indicate a file input follows
	 */
	static private final char ID_FILE = 'f';

	private HashMap<String, String> options;
	private ArrayList<String> unassigned;
	private int count;


	public CommandLineArgs () {
		reset();
	}


	public CommandLineArgs ( String[] aArgs ) {
		this();
		parse( aArgs );
	}


	public String get ( String aOption ) {
		return this.options.get( aOption );
	}


	public List<String> getUnassigned () {
		return this.unassigned;
	}


	public boolean has ( String aOption ) {
		return this.options.containsKey( aOption );
	}


	public void parse ( String[] aArgs ) {
		if ( aArgs == null || aArgs.length == 0 ) {
			return;
		}
		this.count = aArgs.length;
		Matcher matcher;
		char last = 0x0000;
		for ( final String arg : aArgs ) {
			if ( (matcher = PT_KEY_VALUE.matcher( arg )).matches() ) {
				this.options.put( matcher.group( 1 ), matcher.group( 2 ) );
			}
			else if ( (matcher = PT_DOUBLE_FLAG.matcher( arg )).matches() ) {
				this.options.put( matcher.group( 1 ), null );
			}
			else if ( (matcher = PT_SINGLE_SWITCH.matcher( arg )).matches() ) {
				final char[] flags = matcher.group( 1 ).toCharArray();
				for ( final char flag : flags ) {
					this.options.put( "" + flag, null );
					last = flag;
				}
			}
			else {
				if ( last == ID_FILE ) {
					this.options.put( "" + ID_FILE, arg );
					last = 0x0000;
				}
				else {
					this.unassigned.add( arg );
				}
			}
		}
	}


	public void reset () {
		this.options = new HashMap<String, String>();
		this.unassigned = new ArrayList<String>();
	}


	@Override
	public String toString () {
		final StringBuilder out = new StringBuilder();
		out.append( "Options: " + this.options );
		out.append( "\nUnassigned: " + this.unassigned );
		return out.toString();
	}


	public int getCount () {
		return this.count;
	}


	public Set<String> getOptionSet () {
		return this.options.keySet();
	}

}
