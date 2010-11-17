// ////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
// ////////////////////////////////////////////////////////////////////////////

package gnu.trove.impl;

/**
 * Central location for constants needed by various implementations.
 */
public class Constants {

	private static final boolean VERBOSE =
			System.getProperty( "gnu.trove.verbose", null ) != null;

	/** the default capacity for new collections */
	public static final int DEFAULT_CAPACITY = 10;

	/** the load above which rehashing occurs. */
	public static final float DEFAULT_LOAD_FACTOR = 0.5f;

	/** the default value that represents for <tt>int</tt> types. */
	public static final int DEFAULT_INT_NO_ENTRY_VALUE;
	static {
		int value;
		final String property = System.getProperty( "gnu.trove.no_entry.int", "0" );
		if ( "MAX_VALUE".equalsIgnoreCase( property ) ) {
			value = Integer.MAX_VALUE;
		}
		else if ( "MIN_VALUE".equalsIgnoreCase( property ) ) {
			value = Integer.MIN_VALUE;
		}
		else {
			value = Integer.valueOf( property ).intValue();
		}
		DEFAULT_INT_NO_ENTRY_VALUE = value;
		if ( VERBOSE ) {
			System.out.println( "DEFAULT_INT_NO_ENTRY_VALUE: "
					+ DEFAULT_INT_NO_ENTRY_VALUE );
		}
	}

	/** the default value that represents for <tt>long</tt> types. */
	public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
	static {
		long value;
		final String property = System.getProperty( "gnu.trove.no_entry.long", "0" );
		if ( "MAX_VALUE".equalsIgnoreCase( property ) ) {
			value = Long.MAX_VALUE;
		}
		else if ( "MIN_VALUE".equalsIgnoreCase( property ) ) {
			value = Long.MIN_VALUE;
		}
		else {
			value = Long.valueOf( property ).longValue();
		}
		DEFAULT_LONG_NO_ENTRY_VALUE = value;
		if ( VERBOSE ) {
			System.out.println( "DEFAULT_LONG_NO_ENTRY_VALUE: "
					+ DEFAULT_LONG_NO_ENTRY_VALUE );
		}
	}

}
