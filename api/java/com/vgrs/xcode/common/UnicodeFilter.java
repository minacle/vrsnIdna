/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * Implements a set of Unicode codepoints and operations to determine whether
 * certain codepoints fall inside or outside the set. Two different data
 * structures are used to store codepoints internally.
 */
public class UnicodeFilter implements Filter {

	/**
	 * This is the maximum number of ranges to allow on a plane before converting
	 * the data to a matrix. If there are only a handful of ranges, then the
	 * expense of a matrix is not justified. If the number of ranges exceeds
	 * RANGE_THRESHOLD, then a matrix of size 8K is generated and the range data
	 * is stored. Ranges contained entirely on the matrix are removed from the
	 * list.
	 */
	public static final int RANGE_THRESHOLD = 30;

	/**
	 * The set of Unicode points is defined on the values 0 - 0x10ffff. This is
	 * exactly 17 planes of size 0x10000. The UNICODE_PLANES variable is
	 * hard-coded with the value 17.
	 */
	public static final int UNICODE_PLANES = 17;

	/**
	 * The range of Unicode code points to be applied in this filter
	 */
	private TreeSet<Range> ranges = null;

	/**
	 * The number of ranges in each Unicode plane
	 */
	private final int[] rangeCount = new int[UNICODE_PLANES];

	/**
	 * @{link UnicodeMatrix} array to hold the Unicode code points in each of the
	 *        Unicode plane(s).
	 */
	private final UnicodeMatrix[] matrix = new UnicodeMatrix[UNICODE_PLANES];

	/**
	 * String prefix for this filter
	 */
	private String prefix = " ";


	/**
	 * Construct a UnicodeFilter
	 */
	public UnicodeFilter () {
		this.ranges = new TreeSet<Range>();
		for ( int i = 0; i < UNICODE_PLANES; i++ ) {
			this.rangeCount[ i ] = 0;
		}
	}


	/**
	 * @param aPrefix
	 *        A string prefix associated with this filter
	 */
	public UnicodeFilter ( String aPrefix ) {
		this();
		setPrefix( aPrefix );
	}


	@Override
	public void add ( Collection<Range> aRanges ) throws XcodeException {
		for ( final Range range : aRanges ) {
			Unicode.assertValid( range );

			final int firstPlane = getPlane( range.first );
			final int lastPlane = getPlane( range.last );
			for ( int i = firstPlane; i <= lastPlane; i++ ) {
				this.rangeCount[ i ]++;
			}
		}
		this.ranges.addAll( aRanges );

		compile();
	}


	@Override
	public void add ( int aPoint ) throws XcodeException {
		Unicode.isValid( aPoint );

		final int plane = getPlane( aPoint );
		this.rangeCount[ plane ]++;
		this.ranges.add( new Range( aPoint, aPoint ) );

		compile();
	}


	/**
	 * Store a range of integers in the UnicodeFilter.
	 * 
	 * @param aRange
	 *        An integer array of length two. The first element is the lower bound
	 *        and the second element is the upper bound.
	 * @throws XcodeException
	 *         If range is not valid Unicode
	 */
	@Override
	public void add ( Range aRange ) throws XcodeException {
		Unicode.assertValid( aRange );

		final int firstPlane = getPlane( aRange.first );
		final int lastPlane = getPlane( aRange.last );
		for ( int i = firstPlane; i <= lastPlane; i++ ) {
			this.rangeCount[ i ]++;
		}

		this.ranges.add( aRange );

		compile();
	}


	@Override
	public void add ( Range[] aRanges ) throws XcodeException {
		for ( final Range range : aRanges ) {
			Unicode.assertValid( range );

			final int firstPlane = getPlane( range.first );
			final int lastPlane = getPlane( range.last );
			for ( int i = firstPlane; i <= lastPlane; i++ ) {
				this.rangeCount[ i ]++;
			}

			this.ranges.add( range );
		}

		compile();
	}


	@Override
	public void assertAll ( int[] aPoints ) throws XcodeException {
		for ( final int point : aPoints ) {
			if ( !has( point ) ) {
				throwUnicodeFilterDoesNotPass( point );
			}
		}
	}


	/**
	 * We know the exact offending codepoint at test time. Use this to throw a
	 * proper exception.
	 */
	@Override
	public void assertNone ( int[] aPoints ) throws XcodeException {
		for ( final int point : aPoints ) {
			if ( has( point ) ) {
				throwUnicodeFilterDoesNotPass( point );
			}
		}
	}


	/**
	 * @return the prefix identifying this UnicodeFilter
	 */
	public String getPrefix () {
		return this.prefix;
	}


	/**
	 * Test for intersection between a single integer and the UnicodeFilter
	 * 
	 * @param aPoint
	 *        An integer
	 * @throws XcodeException
	 *         If an intersection is found
	 */

	@Override
	public boolean has ( int aPoint ) {
		if ( !Unicode.isValid( aPoint ) ) {
			return false;
		}
		final int plane = getPlane( aPoint );
		if ( this.hasMatrix( plane ) ) {
			return this.matrix[ plane ].test( aPoint );
		}
		else {
			final Range match = this.ranges.floor( new Range( aPoint ) );
			return match != null && match.has( aPoint );
		}
	}


	/**
	 * Test for a non-zero intersection between an integer array and the
	 * UnicodeFilter
	 * 
	 * @param aPoints
	 *        An integer array
	 * @throws XcodeException
	 *         If code points are not valid Unicode
	 */
	@Override
	public boolean hasAll ( int[] aPoints ) {
		for ( final int point : aPoints ) {
			if ( !has( point ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Test for a zero intersection between an integer array and the UnicodeFilter
	 * 
	 * @param aPoints
	 *        An integer array
	 * @throws XcodeException
	 *         If code points are not valid Unicode
	 */
	@Override
	public boolean hasNone ( int[] aPoints ) {
		for ( final int point : aPoints ) {
			if ( has( point ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * @param aPrefix
	 *        the prefix identifying this UnicodeFilter
	 */
	public void setPrefix ( String aPrefix ) {
		if ( aPrefix == null ) {
			this.prefix = "";
		}
		else {
			this.prefix = aPrefix;
		}
	}


	/**
	 * @return String representation of the code points in this filter
	 */
	public String toFullString () {
		final StringBuilder out = new StringBuilder();
		for ( int plane = 0; plane < UNICODE_PLANES; plane++ ) {
			if ( this.hasMatrix( plane ) ) {
				out.append( "Plane: " + plane + "\n" + this.matrix[ plane ] );
			}
		}
		for ( final Range range : this.ranges ) {
			out.append( range );
		}
		return out.toString();
	}


	@Override
	public String toString () {
		final StringBuilder out = new StringBuilder();
		for ( int plane = 0; plane < UNICODE_PLANES; plane++ ) {
			out.append( "Matrices:" );
			if ( this.hasMatrix( plane ) ) {
				out.append( " " + plane );
			}
		}
		for ( final Range range : this.ranges ) {
			out.append( range );
		}
		return out.toString();
	}


	/**
	 * If the code points in a given range already exist in a UnicodeMatrix
	 * object, then the range is removed from the list of ranges.
	 */
	private void clipRanges () {
		Range range = null;
		final Iterator<Range> iterator = this.ranges.iterator();
		while ( iterator.hasNext() ) {
			range = iterator.next();
			final int firstPlane = getPlane( range.first );
			if ( hasMatrix( firstPlane ) ) {
				range.first = getLastInPlane( firstPlane ) + 1;
				if ( range.first > range.last ) {
					iterator.remove();
					continue;
				}
			}
			final int lastPlane = getPlane( range.last );
			if ( hasMatrix( lastPlane ) ) {
				range.last = getFirstInPlane( lastPlane ) - 1;
				if ( range.last < range.first ) {
					iterator.remove();
				}
			}
		}
	}


	/**
	 * This method invokes other methods which will populate UnicodeMatrices for
	 * planes with too many ranges and also consolidate existing ranges contained
	 * completely within a UnicodeMatrix
	 */
	private void compile () {
		populateRanges();
		mergeRanges();
		clipRanges();
	}


	/**
	 * Specifies if the given plane has UnicodeMatrix object allocated
	 * 
	 * @param aPlane
	 *        the plane for which to check if an UnicodeMatrix object is allocated
	 * @return <tt>true</tt> if an UnicodeMatrix is already allocated for the
	 *         given plane
	 */
	private boolean hasMatrix ( int aPlane ) {
		return aPlane >= 0 && aPlane < UNICODE_PLANES
				&& this.matrix[ aPlane ] != null;
	}


	/**
	 * Overlapping ranges i.e. the end point of one range is past the start point
	 * of the next range. For example, Range1(1,8) and Range2(6,16) will be
	 * collapsed into one range of Range(1,16).
	 */
	private void mergeRanges () {
		Range previous = null;
		Range current = null;
		final Iterator<Range> iterator = this.ranges.iterator();
		while ( iterator.hasNext() ) {
			current = iterator.next();
			if ( previous != null ) {
				if ( current.first - 1 <= previous.last ) {
					if ( current.last > previous.last ) {
						previous.last = current.last;
					}
					iterator.remove();
				}
				else {
					previous = current;
				}
			}
			else {
				previous = current;
			}
		}
	}


	/**
	 * Populate a UnicodeMatrix for the given plane. This method also removes
	 * ranges which are contained completely within the given matrix. Ranges that
	 * fall even partially outside the matrix are ignored.
	 * 
	 * @param aPlane
	 *        The number of a Unicode Plane for the matrix
	 */
	private void populateRange ( int aPlane ) {
		if ( this.matrix[ aPlane ] == null ) {
			this.matrix[ aPlane ] = new UnicodeMatrix( aPlane );
		}

		Range range = null;
		final Iterator<Range> iterator = this.ranges.iterator();
		while ( iterator.hasNext() ) {
			range = iterator.next();
			this.matrix[ aPlane ].insert( range );
			if ( this.matrix[ aPlane ].spans( range ) ) {
				iterator.remove();
			}
		}
		this.rangeCount[ aPlane ] = 0;
	}


	/**
	 * Create a UnicodeMatrix for planes with too many ranges.
	 */
	private void populateRanges () {
		for ( int plane = 0; plane < UNICODE_PLANES; plane++ ) {
			if ( this.rangeCount[ plane ] > RANGE_THRESHOLD
					|| this.matrix[ plane ] != null ) {
				populateRange( plane );
			}
		}
	}


	/**
	 * Utility method to throw an exception for a given code point.
	 * 
	 * @param aCodePoint
	 *        the code point to check for existence/non-existence in the filter.
	 * @throws XcodeException
	 */
	private void throwUnicodeFilterDoesNotPass ( int aCodePoint )
			throws XcodeException {
		final String msg =
				this.prefix + " " + Integer.toString( aCodePoint, 16 ).toUpperCase();
		throw XcodeError.UNICODEFILTER_DOES_NOT_PASS( msg );
	}


	/**
	 * @param aPlane
	 *        a Unicode plane ( 0 - 16)
	 * @return the first code point in the specified plane
	 */
	static private int getFirstInPlane ( int aPlane ) {
		return (aPlane & 0x0000001f) << 16;
	}


	/**
	 * @param aPlane
	 * @return
	 */
	static private int getLastInPlane ( int aPlane ) {
		return getFirstInPlane( aPlane ) + 0x0000ffff;
	}


	/**
	 * Return the number of the Unicode Plane to which this codepoint belongs.
	 * 
	 * @param input
	 *        An integer
	 */
	static private int getPlane ( int aCodePoint ) {
		return (aCodePoint & 0x001f0000) >>> 16;
	}
}