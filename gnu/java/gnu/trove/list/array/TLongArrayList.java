///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
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
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.list.array;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

/**
 * A resizable, array-backed list of long primitives.
 */
public class TLongArrayList implements TLongList, Externalizable {
	/** TLongArrayList iterator */
	class TLongArrayIterator implements TLongIterator {

		/** Index of element to be returned by subsequent call to next. */
		private int cursor = 0;

		/**
		 * Index of element returned by most recent call to next or previous. Reset
		 * to -1 if this element is deleted by a call to remove.
		 */
		int lastRet = -1;


		TLongArrayIterator ( int index ) {
			this.cursor = index;
		}


		/** {@inheritDoc} */
		public boolean hasNext () {
			return this.cursor < size();
		}


		/** {@inheritDoc} */
		public long next () {
			try {
				final long next = get( this.cursor );
				this.lastRet = this.cursor++;
				return next;
			}
			catch ( final IndexOutOfBoundsException e ) {
				throw new NoSuchElementException();
			}
		}


		/** {@inheritDoc} */
		public void remove () {
			if ( this.lastRet == -1 ) {
				throw new IllegalStateException();
			}

			try {
				TLongArrayList.this.remove( this.lastRet, 1 );
				if ( this.lastRet < this.cursor ) {
					this.cursor--;
				}
				this.lastRet = -1;
			}
			catch ( final IndexOutOfBoundsException e ) {
				throw new ConcurrentModificationException();
			}
		}
	}

	static final long serialVersionUID = 1L;

	/** the data of the list */
	protected long[] _data;

	/** the index after the last entry in the list */
	protected int _pos;

	/** the default capacity for new lists */
	protected static final int DEFAULT_CAPACITY = Constants.DEFAULT_CAPACITY;

	/** the long value that represents null */
	protected long no_entry_value;


	/**
	 * Creates a new <code>TLongArrayList</code> instance with the default
	 * capacity.
	 */

	public TLongArrayList () {
		this( DEFAULT_CAPACITY, 0 );
	}


	/**
	 * Creates a new <code>TLongArrayList</code> instance with the specified
	 * capacity.
	 * 
	 * @param capacity
	 *        an <code>int</code> value
	 */

	public TLongArrayList ( int capacity ) {
		this( capacity, 0 );
	}


	/**
	 * Creates a new <code>TLongArrayList</code> instance with the specified
	 * capacity.
	 * 
	 * @param capacity
	 *        an <code>int</code> value
	 * @param no_entry_value
	 *        an <code>long</code> value that represents null.
	 */
	public TLongArrayList ( int capacity, long no_entry_value ) {
		this._data = new long[capacity];
		this._pos = 0;
		this.no_entry_value = no_entry_value;
	}


	/**
	 * Creates a new <code>TLongArrayList</code> instance whose capacity is the
	 * length of <tt>values</tt> array and whose initial contents are the
	 * specified values.
	 * 
	 * @param values
	 *        an <code>long[]</code> value
	 */
	public TLongArrayList ( long[] values ) {
		this( values.length );
		add( values );
	}


	/**
	 * Creates a new <code>TLongArrayList</code> instance that contains a copy of
	 * the collection passed to us.
	 * 
	 * @param collection
	 *        the collection to copy
	 */
	public TLongArrayList ( TLongCollection collection ) {
		this( collection.size() );
		addAll( collection );
	}


	// sizing

	/** {@inheritDoc} */
	public boolean add ( long val ) {
		ensureCapacity( this._pos + 1 );
		this._data[ this._pos++ ] = val;
		return true;
	}


	/** {@inheritDoc} */
	public void add ( long[] vals ) {
		add( vals, 0, vals.length );
	}


	/** {@inheritDoc} */
	public void add ( long[] vals, int offset, int length ) {
		ensureCapacity( this._pos + length );
		System.arraycopy( vals, offset, this._data, this._pos, length );
		this._pos += length;
	}


	/** {@inheritDoc} */
	public boolean addAll ( Collection<? extends Long> collection ) {
		boolean changed = false;
		for ( final Long element : collection ) {
			final long e = element.longValue();
			if ( add( e ) ) {
				changed = true;
			}
		}
		return changed;
	}


	// modifying

	/** {@inheritDoc} */
	public boolean addAll ( long[] array ) {
		boolean changed = false;
		for ( final long element : array ) {
			if ( add( element ) ) {
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public boolean addAll ( TLongCollection collection ) {
		boolean changed = false;
		final TLongIterator iter = collection.iterator();
		while ( iter.hasNext() ) {
			final long element = iter.next();
			if ( add( element ) ) {
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public int binarySearch ( long value ) {
		return binarySearch( value, 0, this._pos );
	}


	/** {@inheritDoc} */
	public int binarySearch ( long value, int fromIndex, int toIndex ) {
		if ( fromIndex < 0 ) {
			throw new ArrayIndexOutOfBoundsException( fromIndex );
		}
		if ( toIndex > this._pos ) {
			throw new ArrayIndexOutOfBoundsException( toIndex );
		}

		int low = fromIndex;
		int high = toIndex - 1;

		while ( low <= high ) {
			final int mid = (low + high) >>> 1;
			final long midVal = this._data[ mid ];

			if ( midVal < value ) {
				low = mid + 1;
			}
			else if ( midVal > value ) {
				high = mid - 1;
			}
			else {
				return mid; // value found
			}
		}
		return -(low + 1); // value not found.
	}


	/** {@inheritDoc} */
	public void clear () {
		clear( DEFAULT_CAPACITY );
	}


	/**
	 * Flushes the internal state of the list, setting the capacity of the empty
	 * list to <tt>capacity</tt>.
	 */
	public void clear ( int capacity ) {
		this._data = new long[capacity];
		this._pos = 0;
	}


	/** {@inheritDoc} */
	public boolean contains ( long value ) {
		return lastIndexOf( value ) >= 0;
	}


	/** {@inheritDoc} */
	public boolean containsAll ( Collection<?> collection ) {
		for ( final Object element : collection ) {
			if ( element instanceof Long ) {
				final long c = ((Long) element).longValue();
				if ( !contains( c ) ) {
					return false;
				}
			}
			else {
				return false;
			}

		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean containsAll ( long[] array ) {
		for ( int i = array.length; i-- > 0; ) {
			if ( !contains( array[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean containsAll ( TLongCollection collection ) {
		if ( this == collection ) {
			return true;
		}
		final TLongIterator iter = collection.iterator();
		while ( iter.hasNext() ) {
			final long element = iter.next();
			if ( !contains( element ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Grow the internal array as needed to accommodate the specified number of
	 * elements. The size of the array bytes on each resize unless capacity
	 * requires more than twice the current capacity.
	 */
	public void ensureCapacity ( int capacity ) {
		if ( capacity > this._data.length ) {
			final int newCap = Math.max( this._data.length << 1, capacity );
			final long[] tmp = new long[newCap];
			System.arraycopy( this._data, 0, tmp, 0, this._data.length );
			this._data = tmp;
		}
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals ( Object other ) {
		if ( other == this ) {
			return true;
		}
		else if ( other instanceof TLongArrayList ) {
			final TLongArrayList that = (TLongArrayList) other;
			if ( that.size() != this.size() ) {
				return false;
			}
			else {
				for ( int i = this._pos; i-- > 0; ) {
					if ( this._data[ i ] != that._data[ i ] ) {
						return false;
					}
				}
				return true;
			}
		}
		else {
			return false;
		}
	}


	/** {@inheritDoc} */
	public void fill ( int fromIndex, int toIndex, long val ) {
		if ( toIndex > this._pos ) {
			ensureCapacity( toIndex );
			this._pos = toIndex;
		}
		Arrays.fill( this._data, fromIndex, toIndex, val );
	}


	/** {@inheritDoc} */
	public void fill ( long val ) {
		Arrays.fill( this._data, 0, this._pos, val );
	}


	/** {@inheritDoc} */
	public boolean forEach ( TLongProcedure procedure ) {
		for ( int i = 0; i < this._pos; i++ ) {
			if ( !procedure.execute( this._data[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean forEachDescending ( TLongProcedure procedure ) {
		for ( int i = this._pos; i-- > 0; ) {
			if ( !procedure.execute( this._data[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public long get ( int offset ) {
		if ( offset >= this._pos ) {
			throw new ArrayIndexOutOfBoundsException( offset );
		}
		return this._data[ offset ];
	}


	/** {@inheritDoc} */
	public long getNoEntryValue () {
		return this.no_entry_value;
	}


	/**
	 * Returns the value at the specified offset without doing any bounds
	 * checking.
	 */
	public long getQuick ( int offset ) {
		return this._data[ offset ];
	}


	/** {@inheritDoc} */
	public TLongList grep ( TLongProcedure condition ) {
		final TLongArrayList list = new TLongArrayList();
		for ( int i = 0; i < this._pos; i++ ) {
			if ( condition.execute( this._data[ i ] ) ) {
				list.add( this._data[ i ] );
			}
		}
		return list;
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode () {
		int h = 0;
		for ( int i = this._pos; i-- > 0; ) {
			h += HashFunctions.hash( this._data[ i ] );
		}
		return h;
	}


	/** {@inheritDoc} */
	public int indexOf ( int offset, long value ) {
		for ( int i = offset; i < this._pos; i++ ) {
			if ( this._data[ i ] == value ) {
				return i;
			}
		}
		return -1;
	}


	/** {@inheritDoc} */
	public int indexOf ( long value ) {
		return indexOf( 0, value );
	}


	/** {@inheritDoc} */
	public void insert ( int offset, long value ) {
		if ( offset == this._pos ) {
			add( value );
			return;
		}
		ensureCapacity( this._pos + 1 );
		// shift right
		System.arraycopy( this._data, offset, this._data, offset + 1, this._pos
				- offset );
		// insert
		this._data[ offset ] = value;
		this._pos++;
	}


	/** {@inheritDoc} */
	public void insert ( int offset, long[] values ) {
		insert( offset, values, 0, values.length );
	}


	/** {@inheritDoc} */
	public void insert ( int offset, long[] values, int valOffset, int len ) {
		if ( offset == this._pos ) {
			add( values, valOffset, len );
			return;
		}

		ensureCapacity( this._pos + len );
		// shift right
		System.arraycopy( this._data, offset, this._data, offset + len, this._pos
				- offset );
		// insert
		System.arraycopy( values, valOffset, this._data, offset, len );
		this._pos += len;
	}


	/** {@inheritDoc} */
	public TLongList inverseGrep ( TLongProcedure condition ) {
		final TLongArrayList list = new TLongArrayList();
		for ( int i = 0; i < this._pos; i++ ) {
			if ( !condition.execute( this._data[ i ] ) ) {
				list.add( this._data[ i ] );
			}
		}
		return list;
	}


	/** {@inheritDoc} */
	public boolean isEmpty () {
		return this._pos == 0;
	}


	/** {@inheritDoc} */
	public TLongIterator iterator () {
		return new TLongArrayIterator( 0 );
	}


	/** {@inheritDoc} */
	public int lastIndexOf ( int offset, long value ) {
		for ( int i = offset; i-- > 0; ) {
			if ( this._data[ i ] == value ) {
				return i;
			}
		}
		return -1;
	}


	/** {@inheritDoc} */
	public int lastIndexOf ( long value ) {
		return lastIndexOf( this._pos, value );
	}


	/** {@inheritDoc} */
	public long max () {
		if ( size() == 0 ) {
			throw new IllegalStateException( "cannot find maximum of an empty list" );
		}
		long max = Long.MIN_VALUE;
		for ( int i = 0; i < this._pos; i++ ) {
			if ( this._data[ i ] > max ) {
				max = this._data[ i ];
			}
		}
		return max;
	}


	/** {@inheritDoc} */
	public long min () {
		if ( size() == 0 ) {
			throw new IllegalStateException( "cannot find minimum of an empty list" );
		}
		long min = Long.MAX_VALUE;
		for ( int i = 0; i < this._pos; i++ ) {
			if ( this._data[ i ] < min ) {
				min = this._data[ i ];
			}
		}
		return min;
	}


	public void readExternal ( ObjectInput in ) throws IOException,
			ClassNotFoundException {

		// VERSION
		in.readByte();

		// POSITION
		this._pos = in.readInt();

		// NO_ENTRY_VALUE
		this.no_entry_value = in.readLong();

		// ENTRIES
		final int len = in.readInt();
		this._data = new long[len];
		for ( int i = 0; i < len; i++ ) {
			this._data[ i ] = in.readLong();
		}
	}


	/** {@inheritDoc} */
	public void remove ( int offset, int length ) {
		if ( (offset < 0) || (offset >= this._pos) ) {
			throw new ArrayIndexOutOfBoundsException( offset );
		}

		if ( offset == 0 ) {
			// data at the front
			System.arraycopy( this._data, length, this._data, 0, this._pos - length );
		}
		else if ( this._pos - length == offset ) {
			// no copy to make, decrementing pos "deletes" values at
			// the end
		}
		else {
			// data in the middle
			System.arraycopy( this._data, offset + length, this._data, offset,
					this._pos - (offset + length) );
		}
		this._pos -= length;
		// no need to clear old values beyond _pos, because this is a
		// primitive collection and 0 takes as much room as any other
		// value
	}


	/** {@inheritDoc} */
	public boolean remove ( long value ) {
		for ( int index = 0; index < this._pos; index++ ) {
			if ( value == this._data[ index ] ) {
				remove( index, 1 );
				return true;
			}
		}
		return false;
	}


	/** {@inheritDoc} */
	public boolean removeAll ( Collection<?> collection ) {
		boolean changed = false;
		for ( final Object element : collection ) {
			if ( element instanceof Long ) {
				final long c = ((Long) element).longValue();
				if ( remove( c ) ) {
					changed = true;
				}
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public boolean removeAll ( long[] array ) {
		boolean changed = false;
		for ( int i = array.length; i-- > 0; ) {
			if ( remove( array[ i ] ) ) {
				changed = true;
			}
		}
		return changed;
	}


	// copying

	/** {@inheritDoc} */
	public boolean removeAll ( TLongCollection collection ) {
		if ( collection == this ) {
			clear();
			return true;
		}
		boolean changed = false;
		final TLongIterator iter = collection.iterator();
		while ( iter.hasNext() ) {
			final long element = iter.next();
			if ( remove( element ) ) {
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public long removeAt ( int offset ) {
		final long old = get( offset );
		remove( offset, 1 );
		return old;
	}


	/** {@inheritDoc} */
	public long replace ( int offset, long val ) {
		if ( offset >= this._pos ) {
			throw new ArrayIndexOutOfBoundsException( offset );
		}
		final long old = this._data[ offset ];
		this._data[ offset ] = val;
		return old;
	}


	/**
	 * Sets the size of the list to 0, but does not change its capacity. This
	 * method can be used as an alternative to the {@link #clear()} method if you
	 * want to recycle a list without allocating new backing arrays.
	 */
	public void reset () {
		this._pos = 0;
		Arrays.fill( this._data, this.no_entry_value );
	}


	/**
	 * Sets the size of the list to 0, but does not change its capacity. This
	 * method can be used as an alternative to the {@link #clear()} method if you
	 * want to recycle a list without allocating new backing arrays. This method
	 * differs from {@link #reset()} in that it does not clear the old values in
	 * the backing array. Thus, it is possible for getQuick to return stale data
	 * if this method is used and the caller is careless about bounds checking.
	 */
	public void resetQuick () {
		this._pos = 0;
	}


	/** {@inheritDoc} */

	public boolean retainAll ( Collection<?> collection ) {
		boolean modified = false;
		final TLongIterator iter = iterator();
		while ( iter.hasNext() ) {
			if ( !collection.contains( Long.valueOf( iter.next() ) ) ) {
				iter.remove();
				modified = true;
			}
		}
		return modified;
	}


	// comparing

	/** {@inheritDoc} */
	public boolean retainAll ( long[] array ) {
		boolean changed = false;
		Arrays.sort( array );
		final long[] data = this._data;

		for ( int i = data.length; i-- > 0; ) {
			if ( Arrays.binarySearch( array, data[ i ] ) < 0 ) {
				remove( i, 1 );
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public boolean retainAll ( TLongCollection collection ) {
		if ( this == collection ) {
			return false;
		}
		boolean modified = false;
		final TLongIterator iter = iterator();
		while ( iter.hasNext() ) {
			if ( !collection.contains( iter.next() ) ) {
				iter.remove();
				modified = true;
			}
		}
		return modified;
	}


	// procedures

	/** {@inheritDoc} */
	public void reverse () {
		reverse( 0, this._pos );
	}


	/** {@inheritDoc} */
	public void reverse ( int from, int to ) {
		if ( from == to ) {
			return; // nothing to do
		}
		if ( from > to ) {
			throw new IllegalArgumentException( "from cannot be greater than to" );
		}
		for ( int i = from, j = to - 1; i < j; i++, j-- ) {
			swap( i, j );
		}
	}


	// sorting

	/** {@inheritDoc} */
	public long set ( int offset, long val ) {
		if ( offset >= this._pos ) {
			throw new ArrayIndexOutOfBoundsException( offset );
		}

		final long prev_val = this._data[ offset ];
		this._data[ offset ] = val;
		return prev_val;
	}


	/** {@inheritDoc} */
	public void set ( int offset, long[] values ) {
		set( offset, values, 0, values.length );
	}


	// filling

	/** {@inheritDoc} */
	public void set ( int offset, long[] values, int valOffset, int length ) {
		if ( (offset < 0) || (offset + length > this._pos) ) {
			throw new ArrayIndexOutOfBoundsException( offset );
		}
		System.arraycopy( values, valOffset, this._data, offset, length );
	}


	/**
	 * Sets the value at the specified offset without doing any bounds checking.
	 */
	public void setQuick ( int offset, long val ) {
		this._data[ offset ] = val;
	}


	// searching

	/** {@inheritDoc} */
	public void shuffle ( Random rand ) {
		for ( int i = this._pos; i-- > 1; ) {
			swap( i, rand.nextInt( i ) );
		}
	}


	/** {@inheritDoc} */
	public int size () {
		return this._pos;
	}


	/** {@inheritDoc} */
	public void sort () {
		Arrays.sort( this._data, 0, this._pos );
	}


	/** {@inheritDoc} */
	public void sort ( int fromIndex, int toIndex ) {
		Arrays.sort( this._data, fromIndex, toIndex );
	}


	/** {@inheritDoc} */
	public TLongList subList ( int begin, int end ) {
		if ( end < begin ) {
			throw new IllegalArgumentException( "end index " + end
					+ " greater than begin index " + begin );
		}
		if ( begin < 0 ) {
			throw new IndexOutOfBoundsException( "begin index can not be < 0" );
		}
		if ( end > this._data.length ) {
			throw new IndexOutOfBoundsException( "end index < " + this._data.length );
		}
		final TLongArrayList list = new TLongArrayList( end - begin );
		for ( int i = begin; i < end; i++ ) {
			list.add( this._data[ i ] );
		}
		return list;
	}


	/**
	 * Swap the values at offsets <tt>i</tt> and <tt>j</tt>.
	 * 
	 * @param i
	 *        an offset into the data array
	 * @param j
	 *        an offset into the data array
	 */
	private void swap ( int i, int j ) {
		final long tmp = this._data[ i ];
		this._data[ i ] = this._data[ j ];
		this._data[ j ] = tmp;
	}


	/** {@inheritDoc} */
	public long[] toArray () {
		return toArray( 0, this._pos );
	}


	/** {@inheritDoc} */
	public long[] toArray ( int offset, int len ) {
		final long[] rv = new long[len];
		toArray( rv, offset, len );
		return rv;
	}


	/** {@inheritDoc} */
	public long[] toArray ( long[] dest ) {
		int len = dest.length;
		if ( dest.length > this._pos ) {
			len = this._pos;
			dest[ len ] = this.no_entry_value;
		}
		toArray( dest, 0, len );
		return dest;
	}


	/** {@inheritDoc} */
	public long[] toArray ( long[] dest, int offset, int len ) {
		if ( len == 0 ) {
			return dest; // nothing to copy
		}
		if ( (offset < 0) || (offset >= this._pos) ) {
			throw new ArrayIndexOutOfBoundsException( offset );
		}
		System.arraycopy( this._data, offset, dest, 0, len );
		return dest;
	}


	/** {@inheritDoc} */
	public long[] toArray ( long[] dest, int source_pos, int dest_pos, int len ) {
		if ( len == 0 ) {
			return dest; // nothing to copy
		}
		if ( (source_pos < 0) || (source_pos >= this._pos) ) {
			throw new ArrayIndexOutOfBoundsException( source_pos );
		}
		System.arraycopy( this._data, source_pos, dest, dest_pos, len );
		return dest;
	}


	// stringification

	/** {@inheritDoc} */
	@Override
	public String toString () {
		final StringBuilder buf = new StringBuilder( "{" );
		for ( int i = 0, end = this._pos - 1; i < end; i++ ) {
			buf.append( this._data[ i ] );
			buf.append( ", " );
		}
		if ( size() > 0 ) {
			buf.append( this._data[ this._pos - 1 ] );
		}
		buf.append( "}" );
		return buf.toString();
	}


	/** {@inheritDoc} */
	public void transformValues ( TLongFunction function ) {
		for ( int i = this._pos; i-- > 0; ) {
			this._data[ i ] = function.execute( this._data[ i ] );
		}
	}


	/**
	 * Sheds any excess capacity above and beyond the current size of the list.
	 */
	public void trimToSize () {
		if ( this._data.length > size() ) {
			final long[] tmp = new long[size()];
			toArray( tmp, 0, tmp.length );
			this._data = tmp;
		}
	}


	public void writeExternal ( ObjectOutput out ) throws IOException {
		// VERSION
		out.writeByte( 0 );

		// POSITION
		out.writeInt( this._pos );

		// NO_ENTRY_VALUE
		out.writeLong( this.no_entry_value );

		// ENTRIES
		final int len = this._data.length;
		out.writeInt( len );
		for ( int i = 0; i < len; i++ ) {
			out.writeLong( this._data[ i ] );
		}
	}
} // TLongArrayList
