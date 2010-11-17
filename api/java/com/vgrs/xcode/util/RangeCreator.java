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

package com.vgrs.xcode.util;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.Collection;

import com.vgrs.xcode.common.Range;

/**
 * This class contains utility methods to create a Collection of {@link Range}
 * objects from either a {@link TIntSet} or a {@link TIntList}.
 * 
 * @author nchigurupati
 * @version 1.0 Jun 11, 2010
 */
public class RangeCreator {

	/**
	 * Create a Collection<{@link Range}> from code points in a {@link TIntSet}.
	 * The code points are added to a {@link TIntList} so that they can be sorted
	 * prior to creating Collection<Range> objects.
	 * 
	 * @param aSet
	 * @return Collection<{@link Range}> from code points in a {@link TIntSet}
	 */
	public static Collection<Range> createRanges ( TIntSet aSet ) {
		final TIntList list = new TIntArrayList( aSet );
		return createRanges( list );
	}


	/**
	 * Create a Collection<{@link Range}> from code points in a {@link TIntList}.
	 * 
	 * @param aList
	 *        a list containing int values that need to be converted into
	 *        {@link Range} objects.
	 * @return Collection<{@link Range}> from code points in a {@link TIntList}
	 */
	public static Collection<Range> createRanges ( TIntList aList ) {
		Collection<Range> ranges = null;

		if ( aList == null || aList.isEmpty() ) {
			return ranges;
		}

		ranges = new ArrayList<Range>();
		// Sort the list of code points, so that ranges can be created correctly.
		aList.sort();
		final int[] inputArray = aList.toArray();

		int first = 0;
		int prev = 0;
		boolean rangeStarted = false;
		final int length = inputArray.length;

		// All consecutive code points (i.e. the current code point == prev code
		// point + 1) are put into one Range object. When
		// non-consecutive code points are found, a new Range object is created.
		for ( int i = 0; i < length; ) {
			if ( !rangeStarted ) {
				first = inputArray[ i ];
				prev = first;
				rangeStarted = true;
			}
			int next = 0;
			if ( i + 1 >= length - 1 ) {
				next = inputArray[ i + 1 ];
				if ( next == prev + 1 ) {
					ranges.add( new Range( first, next ) );
				}
				else {
					ranges.add( new Range( first, prev ) );
					ranges.add( new Range( next, next ) );
				}
				break;
			}
			else {
				next = inputArray[ ++i ];
				if ( next > prev + 1 ) {
					ranges.add( new Range( first, prev ) );
					rangeStarted = false;
				}
			}
			prev = next;
		}
		return ranges;
	}
}
