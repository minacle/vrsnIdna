/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

/**
 * Implements a single Unicode Plane as a matrix of boolean flags 8K in size.
 * 
 * @author jcolosi
 */
public class UnicodeMatrix {

	static private final int LENGTH = 4096;

	private final int floor;

	private final int ceiling;

	private char[] data = null;


	/**
	 * Construct a UnicodeMatrix to store 65,535 flags
	 * 
	 * @param aPlane
	 *        A number on the range [0-16] indicating the active plane.
	 */
	public UnicodeMatrix ( int aPlane ) {
		this.floor = aPlane * 0x10000;
		this.ceiling = this.floor + 0xffff;
		this.data = new char[LENGTH];
	}


	/**
	 * Store a range in the UnicodeMatrix
	 * 
	 * @param aRange
	 *        An integer array of length two. The first element is the lower bound
	 *        and the second element is the upper bound.
	 */
	public void insert ( int[] aRange ) {
		insert( aRange[ 0 ], aRange[ 1 ] );
	}


	/**
	 * Store a range in the UnicodeMatrix
	 * 
	 * @param aRange
	 *        A Range object.
	 */
	public void insert ( Range aRange ) {
		insert( aRange.first, aRange.last );
	}


	/**
	 * Store a range in the UnicodeMatrix
	 * 
	 * @param aFirst
	 *        The lower bound (inclusive)
	 * @param aLast
	 *        The upper bound (inclusive)
	 */
	public void insert ( final int aFirst, final int aLast ) {
		int first = aFirst;
		int last = aLast;
		// Out of bound
		if ( last < this.floor || first > this.ceiling ) {
			return;
		}

		// Assert first element is 16 bit clean
		if ( first < this.floor ) {
			first = 0x0000;
		}
		else {
			first &= 0x0000ffff;
		}

		// Assert last element is 16 bit clean
		if ( last > this.ceiling ) {
			last = 0xffff;
		}
		else {
			last &= 0x0000ffff;
		}

		// Insert values
		for ( int i = first; i <= last; i++ ) {
			insert( i );
		}
	}


	/**
	 * Store an integer in the UnicodeMatrix
	 * 
	 * @param aCodePoint
	 *        An integer to be inserted in matrix
	 */
	public void insert ( int aCodePoint ) {
		this.data[ (aCodePoint >> 4) ] |= 1 << (aCodePoint & 0xf);
	}


	/**
	 * Return true if the given range fits completely inside the matrix.
	 * 
	 * @param aRange
	 *        An integer array of length two. The first element is the lower bound
	 *        and the second element is the upper bound.
	 */
	public boolean spans ( int[] aRange ) {
		return aRange[ 0 ] >= this.floor && aRange[ 1 ] <= this.ceiling;
	}


	/**
	 * Return true if the given range fits completely inside the matrix.
	 * 
	 * @param aRange
	 *        A Range object.
	 */
	public boolean spans ( Range aRange ) {
		return aRange.first >= this.floor && aRange.last <= this.ceiling;
	}


	/**
	 * Return true if the given range fits completely inside the matrix.
	 * 
	 * @param aFirst
	 *        The lower bound (inclusive)
	 * @param aLast
	 *        The upper bound (inclusive)
	 */
	public boolean spans ( int aFirst, int aLast ) {
		return aFirst >= this.floor && aLast <= this.ceiling;
	}


	/**
	 * Test for intersection between a single integer and the UnicodeMatrix
	 * 
	 * @param aInput
	 *        An integer
	 */
	public boolean test ( int aInput ) {
		return (1 << (aInput & 0x0000000f) & this.data[ (aInput & 0x0000fff0) >> 4 ]) > 0;
	}


	/**
	 * Display the matrix data to standard output.
	 */
	@Override
	public String toString () {
		final StringBuilder out = new StringBuilder();
		for ( int i = 0; i < this.data.length; i++ ) {
			out.append( Integer.toString( this.data[ i ], 16 ) + ", " );
			if ( i % 8 == 7 ) {
				out.append( '\n' );
			}
		}
		return out.toString();
	}

}