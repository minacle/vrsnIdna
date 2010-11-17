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

package gnu.trove.map.hash;

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

/**
 * An open addressed Map implementation for int keys and long values.
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_HashMap.template,v 1.1.2.13 2009/11/16 21:25:13 robeden
 *          Exp $
 */
public class TIntLongHashMap extends TIntLongHash implements TIntLongMap,
		Externalizable {
	class TIntLongHashIterator extends THashPrimitiveIterator implements
			TIntLongIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param map
		 *        the <tt>TIntLongHashMap</tt> we will be iterating over.
		 */
		TIntLongHashIterator ( TIntLongHashMap map ) {
			super( map );
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		public int key () {
			return TIntLongHashMap.this._set[ this._index ];
		}


		/** @{inheritDoc */
		@Override
		public void remove () {
			if ( this._expectedSize != this._hash.size() ) {
				throw new ConcurrentModificationException();
			}
			// Disable auto compaction during the remove. This is a workaround for bug
			// 1642768.
			try {
				this._hash.tempDisableAutoCompaction();
				TIntLongHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}
			this._expectedSize--;
		}


		/** {@inheritDoc} */
		public long setValue ( long val ) {
			final long old = value();
			TIntLongHashMap.this._values[ this._index ] = val;
			return old;
		}


		/** {@inheritDoc} */
		public long value () {
			return TIntLongHashMap.this._values[ this._index ];
		}
	}

	class TIntLongKeyHashIterator extends THashPrimitiveIterator implements
			TIntIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TIntLongKeyHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public int next () {
			moveToNextIndex();
			return TIntLongHashMap.this._set[ this._index ];
		}


		/** @{inheritDoc */
		@Override
		public void remove () {
			if ( this._expectedSize != this._hash.size() ) {
				throw new ConcurrentModificationException();
			}

			// Disable auto compaction during the remove. This is a workaround for bug
			// 1642768.
			try {
				this._hash.tempDisableAutoCompaction();
				TIntLongHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	class TIntLongValueHashIterator extends THashPrimitiveIterator implements
			TLongIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TIntLongValueHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public long next () {
			moveToNextIndex();
			return TIntLongHashMap.this._values[ this._index ];
		}


		/** @{inheritDoc */
		@Override
		public void remove () {
			if ( this._expectedSize != this._hash.size() ) {
				throw new ConcurrentModificationException();
			}

			// Disable auto compaction during the remove. This is a workaround for bug
			// 1642768.
			try {
				this._hash.tempDisableAutoCompaction();
				TIntLongHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	/** a view onto the keys of the map. */
	protected class TKeyView implements TIntSet {

		private static final long serialVersionUID = 2486654005049596692L;


		/**
		 * Unsupported when operating upon a Key Set view of a TIntLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean add ( int entry ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TIntLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( Collection<? extends Integer> collection ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TIntLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( int[] array ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TIntLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( TIntCollection collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public void clear () {
			TIntLongHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( int entry ) {
			return TIntLongHashMap.this.contains( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Integer ) {
					final int ele = ((Integer) element).intValue();
					if ( !TIntLongHashMap.this.containsKey( ele ) ) {
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
		public boolean containsAll ( int[] array ) {
			for ( final int element : array ) {
				if ( !TIntLongHashMap.this.contains( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TIntCollection collection ) {
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TIntLongHashMap.this.containsKey( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		@Override
		public boolean equals ( Object other ) {
			if ( !(other instanceof TIntSet) ) {
				return false;
			}
			final TIntSet that = (TIntSet) other;
			if ( that.size() != this.size() ) {
				return false;
			}
			for ( int i = TIntLongHashMap.this._states.length; i-- > 0; ) {
				if ( TIntLongHashMap.this._states[ i ] == FULL ) {
					if ( !that.contains( TIntLongHashMap.this._set[ i ] ) ) {
						return false;
					}
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TIntProcedure procedure ) {
			return TIntLongHashMap.this.forEachKey( procedure );
		}


		/** {@inheritDoc} */
		public int getNoEntryValue () {
			return TIntLongHashMap.this.no_entry_key;
		}


		@Override
		public int hashCode () {
			int hashcode = 0;
			for ( int i = TIntLongHashMap.this._states.length; i-- > 0; ) {
				if ( TIntLongHashMap.this._states[ i ] == FULL ) {
					hashcode += HashFunctions.hash( TIntLongHashMap.this._set[ i ] );
				}
			}
			return hashcode;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TIntLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TIntIterator iterator () {
			return new TIntLongKeyHashIterator( TIntLongHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( int entry ) {
			return TIntLongHashMap.this.no_entry_value != TIntLongHashMap.this
					.remove( entry );
		}


		/** {@inheritDoc} */
		public boolean removeAll ( Collection<?> collection ) {
			boolean changed = false;
			for ( final Object element : collection ) {
				if ( element instanceof Integer ) {
					final int c = ((Integer) element).intValue();
					if ( remove( c ) ) {
						changed = true;
					}
				}
			}
			return changed;
		}


		/** {@inheritDoc} */
		public boolean removeAll ( int[] array ) {
			boolean changed = false;
			for ( int i = array.length; i-- > 0; ) {
				if ( remove( array[ i ] ) ) {
					changed = true;
				}
			}
			return changed;
		}


		/** {@inheritDoc} */
		public boolean removeAll ( TIntCollection collection ) {
			if ( this == collection ) {
				clear();
				return true;
			}
			boolean changed = false;
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				final int element = iter.next();
				if ( remove( element ) ) {
					changed = true;
				}
			}
			return changed;
		}


		/** {@inheritDoc} */
		public boolean retainAll ( Collection<?> collection ) {
			boolean modified = false;
			final TIntIterator iter = iterator();
			while ( iter.hasNext() ) {
				if ( !collection.contains( Integer.valueOf( iter.next() ) ) ) {
					iter.remove();
					modified = true;
				}
			}
			return modified;
		}


		/** {@inheritDoc} */
		public boolean retainAll ( int[] array ) {
			boolean changed = false;
			Arrays.sort( array );
			final int[] set = TIntLongHashMap.this._set;
			final byte[] states = TIntLongHashMap.this._states;

			for ( int i = set.length; i-- > 0; ) {
				if ( (states[ i ] == FULL)
						&& (Arrays.binarySearch( array, set[ i ] ) < 0) ) {
					removeAt( i );
					changed = true;
				}
			}
			return changed;
		}


		/** {@inheritDoc} */
		public boolean retainAll ( TIntCollection collection ) {
			if ( this == collection ) {
				return false;
			}
			boolean modified = false;
			final TIntIterator iter = iterator();
			while ( iter.hasNext() ) {
				if ( !collection.contains( iter.next() ) ) {
					iter.remove();
					modified = true;
				}
			}
			return modified;
		}


		/** {@inheritDoc} */
		public int size () {
			return TIntLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public int[] toArray () {
			return TIntLongHashMap.this.keys();
		}


		/** {@inheritDoc} */
		public int[] toArray ( int[] dest ) {
			return TIntLongHashMap.this.keys( dest );
		}


		@Override
		public String toString () {
			final StringBuilder buf = new StringBuilder( "{" );
			forEachKey( new TIntProcedure()
			{
				private boolean first = true;


				public boolean execute ( int key ) {
					if ( this.first ) {
						this.first = false;
					}
					else {
						buf.append( "," );
					}

					buf.append( key );
					return true;
				}
			} );
			buf.append( "}" );
			return buf.toString();
		}
	}

	/** a view onto the values of the map. */
	protected class TValueView implements TLongCollection {

		private static final long serialVersionUID = 3174653860626649018L;


		public boolean add ( long entry ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public boolean addAll ( Collection<? extends Long> collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public boolean addAll ( long[] array ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public boolean addAll ( TLongCollection collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public void clear () {
			TIntLongHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( long entry ) {
			return TIntLongHashMap.this.containsValue( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Long ) {
					final long ele = ((Long) element).longValue();
					if ( !TIntLongHashMap.this.containsValue( ele ) ) {
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
			for ( final long element : array ) {
				if ( !TIntLongHashMap.this.containsValue( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TLongCollection collection ) {
			final TLongIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TIntLongHashMap.this.containsValue( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TLongProcedure procedure ) {
			return TIntLongHashMap.this.forEachValue( procedure );
		}


		/** {@inheritDoc} */
		public long getNoEntryValue () {
			return TIntLongHashMap.this.no_entry_value;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TIntLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TLongIterator iterator () {
			return new TIntLongValueHashIterator( TIntLongHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( long entry ) {
			final long[] values = TIntLongHashMap.this._values;
			final int[] set = TIntLongHashMap.this._set;

			for ( int i = values.length; i-- > 0; ) {
				if ( ((set[ i ] != FREE) && (set[ i ] != REMOVED))
						&& (entry == values[ i ]) ) {
					removeAt( i );
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


		/** {@inheritDoc} */
		public boolean removeAll ( TLongCollection collection ) {
			if ( this == collection ) {
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


		/** {@inheritDoc} */
		public boolean retainAll ( long[] array ) {
			boolean changed = false;
			Arrays.sort( array );
			final long[] values = TIntLongHashMap.this._values;
			final byte[] states = TIntLongHashMap.this._states;

			for ( int i = values.length; i-- > 0; ) {
				if ( (states[ i ] == FULL)
						&& (Arrays.binarySearch( array, values[ i ] ) < 0) ) {
					removeAt( i );
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


		/** {@inheritDoc} */
		public int size () {
			return TIntLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public long[] toArray () {
			return TIntLongHashMap.this.values();
		}


		/** {@inheritDoc} */
		public long[] toArray ( long[] dest ) {
			return TIntLongHashMap.this.values( dest );
		}


		/** {@inheritDoc} */
		@Override
		public String toString () {
			final StringBuilder buf = new StringBuilder( "{" );
			forEachValue( new TLongProcedure()
			{
				private boolean first = true;


				public boolean execute ( long value ) {
					if ( this.first ) {
						this.first = false;
					}
					else {
						buf.append( "," );
					}

					buf.append( value );
					return true;
				}
			} );
			buf.append( "}" );
			return buf.toString();
		}
	}

	static final long serialVersionUID = 1L;

	/** the values of the map */
	protected transient long[] _values;


	/**
	 * Creates a new <code>TIntLongHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TIntLongHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TIntLongHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TIntLongHashMap ( int initialCapacity ) {
		super( initialCapacity );
	}


	/**
	 * Creates a new <code>TIntLongHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TIntLongHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
	}


	/**
	 * Creates a new <code>TIntLongHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 * @param noEntryKey
	 *        a <code>int</code> value that represents <tt>null</tt> for the Key
	 *        set.
	 * @param noEntryValue
	 *        a <code>long</code> value that represents <tt>null</tt> for the
	 *        Value set.
	 */
	public TIntLongHashMap ( int initialCapacity, float loadFactor,
			int noEntryKey, long noEntryValue ) {
		super( initialCapacity, loadFactor, noEntryKey, noEntryValue );
	}


	/**
	 * Creates a new <code>TIntLongHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param keys
	 *        a <tt>int</tt> array containing the keys for the matching values.
	 * @param values
	 *        a <tt>long</tt> array containing the values.
	 */
	public TIntLongHashMap ( int[] keys, long[] values ) {
		super( Math.max( keys.length, values.length ) );

		final int size = Math.min( keys.length, values.length );
		for ( int i = 0; i < size; i++ ) {
			this.put( keys[ i ], values[ i ] );
		}
	}


	/**
	 * Creates a new <code>TIntLongHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param map
	 *        a <tt>TIntLongMap</tt> that will be duplicated.
	 */
	public TIntLongHashMap ( TIntLongMap map ) {
		super( map.size() );
		if ( map instanceof TIntLongHashMap ) {
			final TIntLongHashMap hashmap = (TIntLongHashMap) map;
			this._loadFactor = hashmap._loadFactor;
			this.no_entry_key = hashmap.no_entry_key;
			this.no_entry_value = hashmap.no_entry_value;
			// noinspection RedundantCast
			if ( this.no_entry_key != 0 ) {
				Arrays.fill( this._set, this.no_entry_key );
			}
			// noinspection RedundantCast
			if ( this.no_entry_value != 0 ) {
				Arrays.fill( this._values, this.no_entry_value );
			}
			setUp( (int) Math.ceil( DEFAULT_CAPACITY / this._loadFactor ) );
		}
		putAll( map );
	}


	/** {@inheritDoc} */
	public long adjustOrPutValue ( int key, long adjust_amount, long put_amount ) {
		int index = insertionIndex( key );
		final boolean isNewMapping;
		final long newValue;
		if ( index < 0 ) {
			index = -index - 1;
			newValue = (this._values[ index ] += adjust_amount);
			isNewMapping = false;
		}
		else {
			newValue = (this._values[ index ] = put_amount);
			isNewMapping = true;
		}

		final byte previousState = this._states[ index ];
		this._set[ index ] = key;
		this._states[ index ] = FULL;

		if ( isNewMapping ) {
			postInsertHook( previousState == FREE );
		}

		return newValue;
	}


	/** {@inheritDoc} */
	public boolean adjustValue ( int key, long amount ) {
		final int index = index( key );
		if ( index < 0 ) {
			return false;
		}
		else {
			this._values[ index ] += amount;
			return true;
		}
	}


	/** {@inheritDoc} */
	@Override
	public void clear () {
		super.clear();
		Arrays.fill( this._set, 0, this._set.length, this.no_entry_key );
		Arrays.fill( this._values, 0, this._values.length, this.no_entry_value );
		Arrays.fill( this._states, 0, this._states.length, FREE );
	}


	/** {@inheritDoc} */
	public boolean containsKey ( int key ) {
		return contains( key );
	}


	/** {@inheritDoc} */
	public boolean containsValue ( long val ) {
		final byte[] states = this._states;
		final long[] vals = this._values;

		for ( int i = vals.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && (val == vals[ i ]) ) {
				return true;
			}
		}
		return false;
	}


	private long doPut ( int key, long value, int aIndex ) {
		byte previousState;
		long previous = this.no_entry_value;
		boolean isNewMapping = true;
		int index = aIndex;
		if ( index < 0 ) {
			index = -index - 1;
			previous = this._values[ index ];
			isNewMapping = false;
		}
		previousState = this._states[ index ];
		this._set[ index ] = key;
		this._states[ index ] = FULL;
		this._values[ index ] = value;
		if ( isNewMapping ) {
			postInsertHook( previousState == FREE );
		}

		return previous;
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals ( Object other ) {
		if ( !(other instanceof TIntLongMap) ) {
			return false;
		}
		final TIntLongMap that = (TIntLongMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		final long[] values = this._values;
		final byte[] states = this._states;
		final long this_no_entry_value = getNoEntryValue();
		final long that_no_entry_value = that.getNoEntryValue();
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				final int key = this._set[ i ];
				final long that_value = that.get( key );
				final long this_value = values[ i ];
				if ( (this_value != that_value) && (this_value != this_no_entry_value)
						&& (that_value != that_no_entry_value) ) {
					return false;
				}
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean forEachEntry ( TIntLongProcedure procedure ) {
		final byte[] states = this._states;
		final int[] keys = this._set;
		final long[] values = this._values;
		for ( int i = keys.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && !procedure.execute( keys[ i ], values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean forEachKey ( TIntProcedure procedure ) {
		return forEach( procedure );
	}


	/** {@inheritDoc} */
	public boolean forEachValue ( TLongProcedure procedure ) {
		final byte[] states = this._states;
		final long[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && !procedure.execute( values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public long get ( int key ) {
		final int index = index( key );
		return index < 0 ? this.no_entry_value : this._values[ index ];
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode () {
		int hashcode = 0;
		final byte[] states = this._states;
		for ( int i = this._values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				hashcode +=
						HashFunctions.hash( this._set[ i ] )
								^ HashFunctions.hash( this._values[ i ] );
			}
		}
		return hashcode;
	}


	/** {@inheritDoc} */
	public boolean increment ( int key ) {
		return adjustValue( key, 1 );
	}


	/** {@inheritDoc} */
	@Override
	public boolean isEmpty () {
		return 0 == this._size;
	}


	/** {@inheritDoc} */
	public TIntLongIterator iterator () {
		return new TIntLongHashIterator( this );
	}


	/** {@inheritDoc} */
	public int[] keys () {
		final int[] keys = new int[size()];
		final int[] k = this._set;
		final byte[] states = this._states;

		for ( int i = k.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				keys[ j++ ] = k[ i ];
			}
		}
		return keys;
	}


	/** {@inheritDoc} */
	public int[] keys ( int[] aArray ) {
		final int size = size();
		int[] array = aArray;
		if ( array.length < size ) {
			array = new int[size];
		}

		final int[] keys = this._set;
		final byte[] states = this._states;

		for ( int i = keys.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				array[ j++ ] = keys[ i ];
			}
		}
		return array;
	}


	/** {@inheritDoc} */
	public TIntSet keySet () {
		return new TKeyView();
	}


	/** {@inheritDoc} */
	public long put ( int key, long value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends Integer, ? extends Long> map ) {
		ensureCapacity( map.size() );
		// could optimize this for cases when map instanceof THashMap
		for ( final Map.Entry<? extends Integer, ? extends Long> entry : map
				.entrySet() ) {
			this.put( entry.getKey().intValue(), entry.getValue().longValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TIntLongMap map ) {
		ensureCapacity( map.size() );
		final TIntLongIterator iter = map.iterator();
		while ( iter.hasNext() ) {
			iter.advance();
			this.put( iter.key(), iter.value() );
		}
	}


	/** {@inheritDoc} */
	public long putIfAbsent ( int key, long value ) {
		final int index = insertionIndex( key );
		if ( index < 0 ) {
			return this._values[ -index - 1 ];
		}
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	@Override
	public void readExternal ( ObjectInput in ) throws IOException,
			ClassNotFoundException {
		// VERSION
		in.readByte();

		// SUPER
		super.readExternal( in );

		// NUMBER OF ENTRIES
		int size = in.readInt();
		setUp( size );

		// ENTRIES
		while ( size-- > 0 ) {
			final int key = in.readInt();
			final long val = in.readLong();
			put( key, val );
		}
	}


	/**
	 * rehashes the map to the new capacity.
	 * 
	 * @param newCapacity
	 *        an <code>int</code> value
	 */
	/** {@inheritDoc} */
	@Override
	protected void rehash ( int newCapacity ) {
		final int oldCapacity = this._set.length;
		if ( oldCapacity == newCapacity ) {
			return;
		}

		final int oldKeys[] = this._set;
		final long oldVals[] = this._values;
		final byte oldStates[] = this._states;

		this._set = new int[newCapacity];
		this._values = new long[newCapacity];
		this._states = new byte[newCapacity];

		for ( int i = oldCapacity; i-- > 0; ) {
			if ( oldStates[ i ] == FULL ) {
				final int o = oldKeys[ i ];
				final int index = insertionIndex( o );
				this._set[ index ] = o;
				this._values[ index ] = oldVals[ i ];
				this._states[ index ] = FULL;
			}
		}
	}


	/** {@inheritDoc} */
	public long remove ( int key ) {
		long prev = this.no_entry_value;
		final int index = index( key );
		if ( index >= 0 ) {
			prev = this._values[ index ];
			removeAt( index ); // clear key,state; adjust size
		}
		return prev;
	}


	/** {@inheritDoc} */
	@Override
	protected void removeAt ( int index ) {
		this._values[ index ] = this.no_entry_value;
		super.removeAt( index ); // clear key, state; adjust size
	}


	/** {@inheritDoc} */
	public boolean retainEntries ( TIntLongProcedure procedure ) {
		boolean modified = false;
		final byte[] states = this._states;
		final int[] keys = this._set;
		final long[] values = this._values;

		// Temporarily disable compaction. This is a fix for bug #1738760
		tempDisableAutoCompaction();
		try {
			for ( int i = keys.length; i-- > 0; ) {
				if ( (states[ i ] == FULL)
						&& !procedure.execute( keys[ i ], values[ i ] ) ) {
					removeAt( i );
					modified = true;
				}
			}
		}
		finally {
			reenableAutoCompaction( true );
		}

		return modified;
	}


	/**
	 * initializes the hashtable to a prime capacity which is at least
	 * <tt>initialCapacity + 1</tt>.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @return the actual capacity chosen
	 */
	@Override
	protected int setUp ( int initialCapacity ) {
		int capacity;

		capacity = super.setUp( initialCapacity );
		this._values = new long[capacity];
		return capacity;
	}


	/** {@inheritDoc} */
	@Override
	public String toString () {
		final StringBuilder buf = new StringBuilder( "{" );
		forEachEntry( new TIntLongProcedure()
		{
			private boolean first = true;


			public boolean execute ( int key, long value ) {
				if ( this.first ) {
					this.first = false;
				}
				else {
					buf.append( "," );
				}

				buf.append( key );
				buf.append( "=" );
				buf.append( value );
				return true;
			}
		} );
		buf.append( "}" );
		return buf.toString();
	}


	/** {@inheritDoc} */
	public void transformValues ( TLongFunction function ) {
		final byte[] states = this._states;
		final long[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				values[ i ] = function.execute( values[ i ] );
			}
		}
	}


	/** {@inheritDoc} */
	public TLongCollection valueCollection () {
		return new TValueView();
	}


	/** {@inheritDoc} */
	public long[] values () {
		final long[] vals = new long[size()];
		final long[] v = this._values;
		final byte[] states = this._states;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				vals[ j++ ] = v[ i ];
			}
		}
		return vals;
	}


	/** {@inheritDoc} */
	public long[] values ( long[] aArray ) {
		final int size = size();
		long[] array = aArray;
		if ( array.length < size ) {
			array = new long[size];
		}

		final long[] v = this._values;
		final byte[] states = this._states;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				array[ j++ ] = v[ i ];
			}
		}
		return array;
	}


	/** {@inheritDoc} */
	@Override
	public void writeExternal ( ObjectOutput out ) throws IOException {
		// VERSION
		out.writeByte( 0 );

		// SUPER
		super.writeExternal( out );

		// NUMBER OF ENTRIES
		out.writeInt( this._size );

		// ENTRIES
		for ( int i = this._states.length; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				out.writeInt( this._set[ i ] );
				out.writeLong( this._values[ i ] );
			}
		}
	}
} // TIntLongHashMap
