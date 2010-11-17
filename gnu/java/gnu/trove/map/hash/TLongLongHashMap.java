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

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

/**
 * An open addressed Map implementation for long keys and long values.
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_HashMap.template,v 1.1.2.13 2009/11/16 21:25:13 robeden
 *          Exp $
 */
public class TLongLongHashMap extends TLongLongHash implements TLongLongMap,
		Externalizable {
	/** a view onto the keys of the map. */
	protected class TKeyView implements TLongSet {

		private static final long serialVersionUID = 2587821669699160951L;


		/**
		 * Unsupported when operating upon a Key Set view of a TLongLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean add ( long entry ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TLongLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( Collection<? extends Long> collection ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TLongLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( long[] array ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TLongLongMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( TLongCollection collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public void clear () {
			TLongLongHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( long entry ) {
			return TLongLongHashMap.this.contains( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Long ) {
					final long ele = ((Long) element).longValue();
					if ( !TLongLongHashMap.this.containsKey( ele ) ) {
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
				if ( !TLongLongHashMap.this.contains( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TLongCollection collection ) {
			final TLongIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TLongLongHashMap.this.containsKey( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		@Override
		public boolean equals ( Object other ) {
			if ( !(other instanceof TLongSet) ) {
				return false;
			}
			final TLongSet that = (TLongSet) other;
			if ( that.size() != this.size() ) {
				return false;
			}
			for ( int i = TLongLongHashMap.this._states.length; i-- > 0; ) {
				if ( TLongLongHashMap.this._states[ i ] == FULL ) {
					if ( !that.contains( TLongLongHashMap.this._set[ i ] ) ) {
						return false;
					}
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TLongProcedure procedure ) {
			return TLongLongHashMap.this.forEachKey( procedure );
		}


		/** {@inheritDoc} */
		public long getNoEntryValue () {
			return TLongLongHashMap.this.no_entry_key;
		}


		@Override
		public int hashCode () {
			int hashcode = 0;
			for ( int i = TLongLongHashMap.this._states.length; i-- > 0; ) {
				if ( TLongLongHashMap.this._states[ i ] == FULL ) {
					hashcode += HashFunctions.hash( TLongLongHashMap.this._set[ i ] );
				}
			}
			return hashcode;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TLongLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TLongIterator iterator () {
			return new TLongLongKeyHashIterator( TLongLongHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( long entry ) {
			return TLongLongHashMap.this.no_entry_value != TLongLongHashMap.this
					.remove( entry );
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
			final long[] set = TLongLongHashMap.this._set;
			final byte[] states = TLongLongHashMap.this._states;

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
			return TLongLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public long[] toArray () {
			return TLongLongHashMap.this.keys();
		}


		/** {@inheritDoc} */
		public long[] toArray ( long[] dest ) {
			return TLongLongHashMap.this.keys( dest );
		}


		@Override
		public String toString () {
			final StringBuilder buf = new StringBuilder( "{" );
			forEachKey( new TLongProcedure()
			{
				private boolean first = true;


				public boolean execute ( long key ) {
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

	class TLongLongHashIterator extends THashPrimitiveIterator implements
			TLongLongIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param map
		 *        the <tt>TLongLongHashMap</tt> we will be iterating over.
		 */
		TLongLongHashIterator ( TLongLongHashMap map ) {
			super( map );
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		public long key () {
			return TLongLongHashMap.this._set[ this._index ];
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
				TLongLongHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}
			this._expectedSize--;
		}


		/** {@inheritDoc} */
		public long setValue ( long val ) {
			final long old = value();
			TLongLongHashMap.this._values[ this._index ] = val;
			return old;
		}


		/** {@inheritDoc} */
		public long value () {
			return TLongLongHashMap.this._values[ this._index ];
		}
	}

	class TLongLongKeyHashIterator extends THashPrimitiveIterator implements
			TLongIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TLongLongKeyHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public long next () {
			moveToNextIndex();
			return TLongLongHashMap.this._set[ this._index ];
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
				TLongLongHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	class TLongLongValueHashIterator extends THashPrimitiveIterator implements
			TLongIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TLongLongValueHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public long next () {
			moveToNextIndex();
			return TLongLongHashMap.this._values[ this._index ];
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
				TLongLongHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	/** a view onto the values of the map. */
	protected class TValueView implements TLongCollection {

		private static final long serialVersionUID = -7978858398155661217L;


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
			TLongLongHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( long entry ) {
			return TLongLongHashMap.this.containsValue( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Long ) {
					final long ele = ((Long) element).longValue();
					if ( !TLongLongHashMap.this.containsValue( ele ) ) {
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
				if ( !TLongLongHashMap.this.containsValue( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TLongCollection collection ) {
			final TLongIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TLongLongHashMap.this.containsValue( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TLongProcedure procedure ) {
			return TLongLongHashMap.this.forEachValue( procedure );
		}


		/** {@inheritDoc} */
		public long getNoEntryValue () {
			return TLongLongHashMap.this.no_entry_value;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TLongLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TLongIterator iterator () {
			return new TLongLongValueHashIterator( TLongLongHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( long entry ) {
			final long[] values = TLongLongHashMap.this._values;
			final long[] set = TLongLongHashMap.this._set;

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
			final long[] values = TLongLongHashMap.this._values;
			final byte[] states = TLongLongHashMap.this._states;

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
			return TLongLongHashMap.this._size;
		}


		/** {@inheritDoc} */
		public long[] toArray () {
			return TLongLongHashMap.this.values();
		}


		/** {@inheritDoc} */
		public long[] toArray ( long[] dest ) {
			return TLongLongHashMap.this.values( dest );
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
	 * Creates a new <code>TLongLongHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TLongLongHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TLongLongHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TLongLongHashMap ( int initialCapacity ) {
		super( initialCapacity );
	}


	/**
	 * Creates a new <code>TLongLongHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TLongLongHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
	}


	/**
	 * Creates a new <code>TLongLongHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 * @param noEntryKey
	 *        a <code>long</code> value that represents <tt>null</tt> for the Key
	 *        set.
	 * @param noEntryValue
	 *        a <code>long</code> value that represents <tt>null</tt> for the
	 *        Value set.
	 */
	public TLongLongHashMap ( int initialCapacity, float loadFactor,
			long noEntryKey, long noEntryValue ) {
		super( initialCapacity, loadFactor, noEntryKey, noEntryValue );
	}


	/**
	 * Creates a new <code>TLongLongHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param keys
	 *        a <tt>long</tt> array containing the keys for the matching values.
	 * @param values
	 *        a <tt>long</tt> array containing the values.
	 */
	public TLongLongHashMap ( long[] keys, long[] values ) {
		super( Math.max( keys.length, values.length ) );

		final int size = Math.min( keys.length, values.length );
		for ( int i = 0; i < size; i++ ) {
			this.put( keys[ i ], values[ i ] );
		}
	}


	/**
	 * Creates a new <code>TLongLongHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param map
	 *        a <tt>TLongLongMap</tt> that will be duplicated.
	 */
	public TLongLongHashMap ( TLongLongMap map ) {
		super( map.size() );
		if ( map instanceof TLongLongHashMap ) {
			final TLongLongHashMap hashmap = (TLongLongHashMap) map;
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
	public long adjustOrPutValue ( long key, long adjust_amount, long put_amount ) {
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
	public boolean adjustValue ( long key, long amount ) {
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
	public boolean containsKey ( long key ) {
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


	private long doPut ( long key, long value, int aIndex ) {
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
		if ( !(other instanceof TLongLongMap) ) {
			return false;
		}
		final TLongLongMap that = (TLongLongMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		final long[] values = this._values;
		final byte[] states = this._states;
		final long this_no_entry_value = getNoEntryValue();
		final long that_no_entry_value = that.getNoEntryValue();
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				final long key = this._set[ i ];
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
	public boolean forEachEntry ( TLongLongProcedure procedure ) {
		final byte[] states = this._states;
		final long[] keys = this._set;
		final long[] values = this._values;
		for ( int i = keys.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && !procedure.execute( keys[ i ], values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean forEachKey ( TLongProcedure procedure ) {
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
	public long get ( long key ) {
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
	public boolean increment ( long key ) {
		return adjustValue( key, 1 );
	}


	/** {@inheritDoc} */
	@Override
	public boolean isEmpty () {
		return 0 == this._size;
	}


	/** {@inheritDoc} */
	public TLongLongIterator iterator () {
		return new TLongLongHashIterator( this );
	}


	/** {@inheritDoc} */
	public long[] keys () {
		final long[] keys = new long[size()];
		final long[] k = this._set;
		final byte[] states = this._states;

		for ( int i = k.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				keys[ j++ ] = k[ i ];
			}
		}
		return keys;
	}


	/** {@inheritDoc} */
	public long[] keys ( long[] aArray ) {
		final int size = size();
		long[] array = aArray;
		if ( array.length < size ) {
			array = new long[size];
		}

		final long[] keys = this._set;
		final byte[] states = this._states;

		for ( int i = keys.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				array[ j++ ] = keys[ i ];
			}
		}
		return array;
	}


	/** {@inheritDoc} */
	public TLongSet keySet () {
		return new TKeyView();
	}


	/** {@inheritDoc} */
	public long put ( long key, long value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends Long, ? extends Long> map ) {
		ensureCapacity( map.size() );
		// could optimize this for cases when map instanceof THashMap
		for ( final Map.Entry<? extends Long, ? extends Long> entry : map
				.entrySet() ) {
			this.put( entry.getKey().longValue(), entry.getValue().longValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TLongLongMap map ) {
		ensureCapacity( map.size() );
		final TLongLongIterator iter = map.iterator();
		while ( iter.hasNext() ) {
			iter.advance();
			this.put( iter.key(), iter.value() );
		}
	}


	/** {@inheritDoc} */
	public long putIfAbsent ( long key, long value ) {
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
			final long key = in.readLong();
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

		final long oldKeys[] = this._set;
		final long oldVals[] = this._values;
		final byte oldStates[] = this._states;

		this._set = new long[newCapacity];
		this._values = new long[newCapacity];
		this._states = new byte[newCapacity];

		for ( int i = oldCapacity; i-- > 0; ) {
			if ( oldStates[ i ] == FULL ) {
				final long o = oldKeys[ i ];
				final int index = insertionIndex( o );
				this._set[ index ] = o;
				this._values[ index ] = oldVals[ i ];
				this._states[ index ] = FULL;
			}
		}
	}


	/** {@inheritDoc} */
	public long remove ( long key ) {
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
	public boolean retainEntries ( TLongLongProcedure procedure ) {
		boolean modified = false;
		final byte[] states = this._states;
		final long[] keys = this._set;
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
		forEachEntry( new TLongLongProcedure()
		{
			private boolean first = true;


			public boolean execute ( long key, long value ) {
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
				out.writeLong( this._set[ i ] );
				out.writeLong( this._values[ i ] );
			}
		}
	}
} // TLongLongHashMap
