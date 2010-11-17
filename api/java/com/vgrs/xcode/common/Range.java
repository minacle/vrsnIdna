
package com.vgrs.xcode.common;

/**
 * This class stores the start and end integer values of a range of integers. It
 * also provides methods to specify if a given integer value falls within the
 * start and end range of integers stored in this class.
 * 
 * @author jcolosi
 * @version 1.0 Jun 17, 2010
 */
public class Range implements Comparable<Range> {

	/**
	 * The integer value of the start of the range
	 */
	public int first;

	/**
	 * The integer value of the end of the range
	 */
	public int last;

	/**
	 * Boolean to indicate that start and end are the same value
	 */
	public boolean isPoint;


	/**
	 * @param aFirst
	 *        the start of the range of integers
	 * @param aLast
	 *        the end of the range of integers
	 */
	public Range ( int aFirst, int aLast ) {
		if ( aFirst <= aLast ) {
			this.first = aFirst;
			this.last = aLast;
		}
		else {
			this.first = aLast;
			this.last = aFirst;
		}
		this.isPoint = this.first == this.last;
	}


	/**
	 * @param aFirst
	 *        Start and end are the same
	 */
	public Range ( int aFirst ) {
		this( aFirst, aFirst );
	}


	/**
	 * Comparator to efficiently sort while storing this class in
	 * java.util.Collections.
	 */
	@Override
	public int compareTo ( Range aOtherRange ) {
		if ( this.first != aOtherRange.first || this.isPoint || aOtherRange.isPoint ) {
			return this.first - aOtherRange.first;
		}
		else {
			return this.last - aOtherRange.last;
		}
	}


	/**
	 * @param aPoint
	 *        the int value to check if it is within the range of integers held in
	 *        this class.
	 * @return
	 *         <tt>true</true> if the int value is within the range of integers held in
	 *        this class.
	 */
	public boolean has ( int aPoint ) {
		return aPoint >= this.first && aPoint <= this.last;
	}


	/**
	 * Specifies if any of the int values specified in the input array are present
	 * in the range of integers stored in this class.
	 * 
	 * @param aPoints
	 *        an array containing the int values to be tested
	 * @return
	 *         <tt>true</true> if the any of the int values are within the range of integers held in
	 *        this class.
	 */
	public boolean hasAny ( int[] aPoints ) {
		if ( aPoints != null ) {
			for ( final int point : aPoints ) {
				if ( has( point ) ) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Specifies if all the code points specified in the input array are present
	 * in the range of integers stored in this class.
	 * 
	 * @param aPoints
	 *        an array containing the int values to be tested
	 * @return
	 *         <tt>true</true> if the all of the int values are within the range of integers held in
	 *        this class.
	 */
	public boolean hasAll ( int[] aPoints ) {
		if ( aPoints != null ) {
			for ( final int point : aPoints ) {
				if ( !has( point ) ) {
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public String toString () {
		final StringBuilder out = new StringBuilder();
		out.append( "(" );
		out.append( Integer.toString( this.first, 16 ) );
		if ( !this.isPoint ) {
			out.append( "," + Integer.toString( this.last, 16 ) );
		}
		out.append( ")" );
		return out.toString();
	}

}