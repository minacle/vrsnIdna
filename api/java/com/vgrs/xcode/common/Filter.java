/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.common;

import java.util.Collection;

import com.vgrs.xcode.util.XcodeException;

/**
 * Interface to be implemented by the {@link UnicodeFilter} class.
 * 
 * @author jcolosi
 */
public interface Filter {

	/**
	 * Adds the given code point to the filter
	 * 
	 * @param aPoint
	 *        the code point to add to the filter
	 * @throws XcodeException
	 */
	public void add ( int aPoint ) throws XcodeException;


	/**
	 * Stores the collection of Range objects in the filter
	 * 
	 * @param aRanges
	 *        collection of Range objects to add to the filter
	 * @throws XcodeException
	 */
	public void add ( Collection<Range> aRanges ) throws XcodeException;


	/**
	 * Adds the given range to the filter
	 * 
	 * @param aRange
	 *        the range to add to the filter
	 * @throws XcodeException
	 */
	public void add ( Range aRange ) throws XcodeException;


	/**
	 * Adds an array of Range objects to the filter
	 * 
	 * @param aRanges
	 *        Array of Range objects to add to the filter
	 * @throws XcodeException
	 */
	public void add ( Range[] aRanges ) throws XcodeException;


	/**
	 * Asserts that the input array of code points exist in the filter. Even if a
	 * single code point does not exist in the filter, an XcodeException is
	 * thrown.
	 * 
	 * @param aPoints
	 *        input array of code points to check if they are in the filter
	 * @throws XcodeException
	 */
	public void assertAll ( int[] aPoints ) throws XcodeException;


	/**
	 * Asserts that none of the input array of code points exist in the filter.
	 * Even if a single code point exists in the filter, an XcodeException is
	 * thrown.
	 * 
	 * @param aPoints
	 *        input array of code points to check if they are not present in the
	 *        filter
	 * @throws XcodeException
	 */
	public void assertNone ( int[] aPoints ) throws XcodeException;


	/**
	 * Check that the input code point exists in the filter. If the code point
	 * does not exist in the filter, a value of <tt>
	 * 
	 * @param aPoint
	 *        code point to check for existence in the filter
	 * @return If the code point does not exist in the filter, a value of
	 *         <tt>false</tt> is returned. Otherwise, a value of
	 *         <tt>true</true> is returned.
	 */
	public boolean has ( int aPoint );


	/**
	 * Check that the input array of code points exist in the filter.
	 * 
	 * @param aPoints
	 *        code point to check for existence in the filter
	 * @return If a code point does not exist in the filter, a value of
	 *         <tt>false</tt> is returned. Otherwise, a value of
	 *         <tt>true</true> is returned.
	 */
	public boolean hasAll ( int[] aPoints );


	/**
	 * Check that the input array of code points do not exist in the filter.
	 * 
	 * @param aPoints
	 *        code point to check for non existence in the filter
	 * @return If a code point exists in the filter, a value of <tt>false</tt> is
	 *         returned. Otherwise, a value of <tt>true</true> is returned.
	 */
	public boolean hasNone ( int[] aPoints );

}