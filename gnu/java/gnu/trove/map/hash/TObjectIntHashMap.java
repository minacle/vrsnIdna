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

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

/**
 * An open addressed Map implementation for Object keys and int values. Created:
 * Sun Nov 4 08:52:45 2001
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */
public class TObjectIntHashMap<K> extends TObjectHash<K> implements
		TObjectIntMap<K>, Externalizable {

	/** a view onto the keys of the map. */
	protected class KeyView extends MapBackedView<K> {

		@Override
		public boolean containsElement ( K key ) {
			return TObjectIntHashMap.this.contains( key );
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public Iterator<K> iterator () {
			return new TObjectHashIterator( TObjectIntHashMap.this );
		}


		@Override
		public boolean removeElement ( K key ) {
			return TObjectIntHashMap.this.no_entry_value != TObjectIntHashMap.this
					.remove( key );
		}
	}

	private abstract class MapBackedView<E> extends AbstractSet<E> implements
			Set<E>, Iterable<E> {

		@Override
		public boolean add ( E obj ) {
			throw new UnsupportedOperationException();
		}


		@Override
		public boolean addAll ( Collection<? extends E> collection ) {
			throw new UnsupportedOperationException();
		}


		@Override
		public void clear () {
			TObjectIntHashMap.this.clear();
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public boolean contains ( Object key ) {
			return containsElement( (E) key );
		}


		public abstract boolean containsElement ( E key );


		@Override
		public boolean isEmpty () {
			return TObjectIntHashMap.this.isEmpty();
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public boolean remove ( Object o ) {
			return removeElement( (E) o );
		}


		public abstract boolean removeElement ( E key );


		@Override
		public boolean retainAll ( Collection<?> collection ) {
			boolean changed = false;
			final Iterator<E> i = iterator();
			while ( i.hasNext() ) {
				if ( !collection.contains( i.next() ) ) {
					i.remove();
					changed = true;
				}
			}
			return changed;
		}


		@Override
		public int size () {
			return TObjectIntHashMap.this.size();
		}


		@Override
		public Object[] toArray () {
			final Object[] result = new Object[size()];
			final Iterator<E> e = iterator();
			for ( int i = 0; e.hasNext(); i++ ) {
				result[ i ] = e.next();
			}
			return result;
		}


		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray ( T[] aArray ) {
			final int size = size();
			T[] a = aArray;
			if ( a.length < size ) {
				// noinspection unchecked
				a =
						(T[]) java.lang.reflect.Array.newInstance( a
								.getClass()
								.getComponentType(), size );
			}

			final Iterator<E> it = iterator();
			final Object[] result = a;
			for ( int i = 0; i < size; i++ ) {
				result[ i ] = it.next();
			}

			if ( a.length > size ) {
				a[ size ] = null;
			}

			return a;
		}
	}

	class TIntValueCollection implements TIntCollection {

		private static final long serialVersionUID = 6499037526377731396L;

		class TObjectIntValueHashIterator implements TIntIterator {

			protected THash _hash = TObjectIntHashMap.this;

			/**
			 * the number of elements this iterator believes are in the data structure
			 * it accesses.
			 */
			protected int _expectedSize;

			/** the index used for iteration. */
			protected int _index;


			/** Creates an iterator over the specified map */
			TObjectIntValueHashIterator () {
				this._expectedSize = this._hash.size();
				this._index = this._hash.capacity();
			}


			/** {@inheritDoc} */
			public boolean hasNext () {
				return nextIndex() >= 0;
			}


			/**
			 * Sets the internal <tt>index</tt> so that the `next' object can be
			 * returned.
			 */
			protected final void moveToNextIndex () {
				// doing the assignment && < 0 in one line shaves
				// 3 opcodes...
				if ( (this._index = nextIndex()) < 0 ) {
					throw new NoSuchElementException();
				}
			}


			/** {@inheritDoc} */
			public int next () {
				moveToNextIndex();
				return TObjectIntHashMap.this._values[ this._index ];
			}


			/**
			 * Returns the index of the next value in the data structure or a negative
			 * value if the iterator is exhausted.
			 * 
			 * @return an <code>int</code> value
			 * @throws ConcurrentModificationException
			 *         if the underlying collection's size has been modified since the
			 *         iterator was created.
			 */
			protected final int nextIndex () {
				if ( this._expectedSize != this._hash.size() ) {
					throw new ConcurrentModificationException();
				}

				final Object[] set = TObjectIntHashMap.this._set;
				int i = this._index;
				while ( (i-- > 0)
						&& ((set[ i ] == TObjectHash.FREE) || (set[ i ] == TObjectHash.REMOVED)) ) {
					;
				}
				return i;
			}


			/** @{inheritDoc */
			public void remove () {
				if ( this._expectedSize != this._hash.size() ) {
					throw new ConcurrentModificationException();
				}

				// Disable auto compaction during the remove. This is a workaround for
				// bug 1642768.
				try {
					this._hash.tempDisableAutoCompaction();
					TObjectIntHashMap.this.removeAt( this._index );
				}
				finally {
					this._hash.reenableAutoCompaction( false );
				}

				this._expectedSize--;
			}
		}


		public boolean add ( int entry ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public boolean addAll ( Collection<? extends Integer> collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public boolean addAll ( int[] array ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public boolean addAll ( TIntCollection collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public void clear () {
			TObjectIntHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( int entry ) {
			return TObjectIntHashMap.this.containsValue( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Integer ) {
					final int ele = ((Integer) element).intValue();
					if ( !TObjectIntHashMap.this.containsValue( ele ) ) {
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
				if ( !TObjectIntHashMap.this.containsValue( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TIntCollection collection ) {
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TObjectIntHashMap.this.containsValue( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TIntProcedure procedure ) {
			return TObjectIntHashMap.this.forEachValue( procedure );
		}


		/** {@inheritDoc} */
		public int getNoEntryValue () {
			return TObjectIntHashMap.this.no_entry_value;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TObjectIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TIntIterator iterator () {
			return new TObjectIntValueHashIterator();
		}


		/** {@inheritDoc} */
		public boolean remove ( int entry ) {
			final int[] values = TObjectIntHashMap.this._values;
			final Object[] set = TObjectIntHashMap.this._set;

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
			final int[] values = TObjectIntHashMap.this._values;

			final Object[] set = TObjectIntHashMap.this._set;
			for ( int i = set.length; i-- > 0; ) {
				if ( (set[ i ] != FREE) && (set[ i ] != REMOVED)
						&& (Arrays.binarySearch( array, values[ i ] ) < 0) ) {
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
			return TObjectIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public int[] toArray () {
			return TObjectIntHashMap.this.values();
		}


		/** {@inheritDoc} */
		public int[] toArray ( int[] dest ) {
			return TObjectIntHashMap.this.values( dest );
		}


		@Override
		public String toString () {
			final StringBuilder buf = new StringBuilder( "{" );
			forEachValue( new TIntProcedure()
			{
				private boolean first = true;


				public boolean execute ( int value ) {
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

	@SuppressWarnings("hiding")
	class TObjectIntHashIterator<K> extends TObjectHashIterator<K> implements
			TObjectIntIterator<K> {

		/** the collection being iterated over */
		private final TObjectIntHashMap<K> _map;


		public TObjectIntHashIterator ( TObjectIntHashMap<K> map ) {
			super( map );
			this._map = map;
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		@SuppressWarnings( {
			"unchecked"
		})
		public K key () {
			return (K) this._map._set[ this._index ];
		}


		/** {@inheritDoc} */
		public int setValue ( int val ) {
			final int old = value();
			this._map._values[ this._index ] = val;
			return old;
		}


		/** {@inheritDoc} */
		public int value () {
			return this._map._values[ this._index ];
		}
	}

	static final long serialVersionUID = 1L;

	private final TObjectIntProcedure<K> PUT_ALL_PROC =
			new TObjectIntProcedure<K>()
			{
				public boolean execute ( K key, int value ) {
					put( key, value );
					return true;
				}
			};

	/** the values of the map */
	protected transient int[] _values;

	/** the value that represents null */
	protected int no_entry_value;


	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TObjectIntHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TObjectIntHashMap ( int initialCapacity ) {
		super( initialCapacity );
		this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
	}


	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TObjectIntHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
		this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
	}


	// Query Operations

	/**
	 * Creates a new <code>TObjectIntHashMap</code> instance with a prime value at
	 * or near the specified capacity and load factor.
	 * 
	 * @param initialCapacity
	 *        used to find a prime capacity for the table.
	 * @param loadFactor
	 *        used to calculate the threshold over which rehashing takes place.
	 * @param noEntryValue
	 *        the value used to represent null.
	 */
	public TObjectIntHashMap ( int initialCapacity, float loadFactor,
			int noEntryValue ) {
		super( initialCapacity, loadFactor );
		this.no_entry_value = noEntryValue;
		// noinspection RedundantCast
		if ( this.no_entry_value != 0 ) {
			Arrays.fill( this._values, this.no_entry_value );
		}
	}


	/**
	 * Creates a new <code>TObjectIntHashMap</code> that contains the entries in
	 * the map passed to it.
	 * 
	 * @param map
	 *        the <tt>TObjectIntMap</tt> to be copied.
	 */
	@SuppressWarnings("unchecked")
	public TObjectIntHashMap ( TObjectIntMap<K> map ) {
		this( map.size(), 0.5f, map.getNoEntryValue() );
		if ( map instanceof TObjectIntHashMap ) {
			final TObjectIntHashMap hashmap = (TObjectIntHashMap) map;
			this._loadFactor = hashmap._loadFactor;
			this.no_entry_value = hashmap.no_entry_value;
			// noinspection RedundantCast
			if ( this.no_entry_value != 0 ) {
				Arrays.fill( this._values, this.no_entry_value );
			}
			setUp( (int) Math.ceil( DEFAULT_CAPACITY / this._loadFactor ) );
		}
		putAll( map );
	}


	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public int adjustOrPutValue ( final K key, final int adjust_amount,
			final int put_amount ) {
		int index = insertionIndex( key );
		final boolean isNewMapping;
		final int newValue;
		if ( index < 0 ) {
			index = -index - 1;
			newValue = (this._values[ index ] += adjust_amount);
			isNewMapping = false;
		}
		else {
			newValue = (this._values[ index ] = put_amount);
			isNewMapping = true;
		}

		// noinspection unchecked
		final K oldKey = (K) this._set[ index ];
		this._set[ index ] = key;

		if ( isNewMapping ) {
			postInsertHook( oldKey == FREE );
		}

		return newValue;
	}


	/** {@inheritDoc} */
	public boolean adjustValue ( K key, int amount ) {
		final int index = index( key );
		if ( index < 0 ) {
			return false;
		}
		else {
			this._values[ index ] += amount;
			return true;
		}
	}


	// Modification Operations

	/** {@inheritDoc} */
	@Override
	public void clear () {
		super.clear();
		Arrays.fill( this._set, 0, this._set.length, FREE );
		Arrays.fill( this._values, 0, this._values.length, this.no_entry_value );
	}


	/** {@inheritDoc} */
	public boolean containsKey ( Object key ) {
		return contains( key );
	}


	/** {@inheritDoc} */
	public boolean containsValue ( int val ) {
		final Object[] keys = this._set;
		final int[] vals = this._values;

		for ( int i = vals.length; i-- > 0; ) {
			if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED) && (val == vals[ i ]) ) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	private int doPut ( K key, int value, int aIndex ) {
		int previous = this.no_entry_value;
		boolean isNewMapping = true;
		int index = aIndex;
		if ( index < 0 ) {
			index = -index - 1;
			previous = this._values[ index ];
			isNewMapping = false;
		}
		// noinspection unchecked
		final K oldKey = (K) this._set[ index ];
		this._set[ index ] = key;
		this._values[ index ] = value;

		if ( isNewMapping ) {
			postInsertHook( oldKey == FREE );
		}
		return previous;
	}


	/**
	 * Compares this map with another map for equality of their stored entries.
	 * 
	 * @param other
	 *        an <code>Object</code> value
	 * @return a <code>boolean</code> value
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals ( Object other ) {
		if ( !(other instanceof TObjectIntMap) ) {
			return false;
		}
		final TObjectIntMap that = (TObjectIntMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		try {
			final TObjectIntIterator iter = this.iterator();
			while ( iter.hasNext() ) {
				iter.advance();
				final Object key = iter.key();
				final int value = iter.value();
				if ( value == this.no_entry_value ) {
					if ( !((that.get( key ) == that.getNoEntryValue()) && that
							.containsKey( key )) ) {
						return false;
					}
				}
				else {
					if ( value != that.get( key ) ) {
						return false;
					}
				}
			}
		}
		catch ( final ClassCastException ex ) {
			// unused.
		}
		return true;
	}


	// Bulk Operations

	/**
	 * Executes <tt>procedure</tt> for each key/value entry in the map.
	 * 
	 * @param procedure
	 *        a <code>TOObjectIntProcedure</code> value
	 * @return false if the loop over the entries terminated because the procedure
	 *         returned false for some entry.
	 */
	@SuppressWarnings( {
		"unchecked"
	})
	public boolean forEachEntry ( TObjectIntProcedure<K> procedure ) {
		final Object[] keys = this._set;
		final int[] values = this._values;
		for ( int i = keys.length; i-- > 0; ) {
			if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED)
					&& !procedure.execute( (K) keys[ i ], values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Executes <tt>procedure</tt> for each key in the map.
	 * 
	 * @param procedure
	 *        a <code>TObjectProcedure</code> value
	 * @return false if the loop over the keys terminated because the procedure
	 *         returned false for some key.
	 */
	public boolean forEachKey ( TObjectProcedure<K> procedure ) {
		return forEach( procedure );
	}


	/**
	 * Executes <tt>procedure</tt> for each value in the map.
	 * 
	 * @param procedure
	 *        a <code>TIntProcedure</code> value
	 * @return false if the loop over the values terminated because the procedure
	 *         returned false for some value.
	 */
	public boolean forEachValue ( TIntProcedure procedure ) {
		final Object[] keys = this._set;
		final int[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED)
					&& !procedure.execute( values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	// Views

	/** {@inheritDoc} */
	public int get ( Object key ) {
		final int index = index( key );
		return index < 0 ? this.no_entry_value : this._values[ index ];
	}


	/** {@inheritDoc} */
	public int getNoEntryValue () {
		return this.no_entry_value;
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode () {
		int hashcode = 0;
		final Object[] keys = this._set;
		final int[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED) ) {
				hashcode +=
						HashFunctions.hash( values[ i ] )
								^ (keys[ i ] == null ? 0 : keys[ i ].hashCode());
			}
		}
		return hashcode;
	}


	/** {@inheritDoc} */
	public boolean increment ( K key ) {
		// noinspection RedundantCast
		return adjustValue( key, 1 );
	}


	/**
	 * @return an iterator over the entries in this map
	 */
	public TObjectIntIterator<K> iterator () {
		return new TObjectIntHashIterator<K>( this );
	}


	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Object[] keys () {
		// noinspection unchecked
		final K[] keys = (K[]) new Object[size()];
		final Object[] k = this._set;

		for ( int i = k.length, j = 0; i-- > 0; ) {
			if ( (k[ i ] != FREE) && (k[ i ] != REMOVED) ) {
				// noinspection unchecked
				keys[ j++ ] = (K) k[ i ];
			}
		}
		return keys;
	}


	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public K[] keys ( K[] aArray ) {
		final int size = size();
		K[] a = aArray;
		if ( a.length < size ) {
			// noinspection unchecked
			a =
					(K[]) java.lang.reflect.Array.newInstance( a
							.getClass()
							.getComponentType(), size );
		}

		final Object[] k = this._set;

		for ( int i = k.length, j = 0; i-- > 0; ) {
			if ( (k[ i ] != FREE) && (k[ i ] != REMOVED) ) {
				// noinspection unchecked
				a[ j++ ] = (K) k[ i ];
			}
		}
		return a;
	}


	/** {@inheritDoc} */
	public Set<K> keySet () {
		return new KeyView();
	}


	/** {@inheritDoc} */
	public int put ( K key, int value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends K, ? extends Integer> map ) {
		final Set<? extends Map.Entry<? extends K, ? extends Integer>> set =
				map.entrySet();
		for ( final Map.Entry<? extends K, ? extends Integer> entry : set ) {
			put( entry.getKey(), entry.getValue().intValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TObjectIntMap<K> map ) {
		map.forEachEntry( this.PUT_ALL_PROC );
	}


	/** {@inheritDoc} */
	public int putIfAbsent ( K key, int value ) {
		final int index = insertionIndex( key );
		if ( index < 0 ) {
			return this._values[ -index - 1 ];
		}
		return doPut( key, value, index );
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readExternal ( ObjectInput in ) throws IOException,
			ClassNotFoundException {

		// VERSION
		in.readByte();

		// SUPER
		super.readExternal( in );

		// NO_ENTRY_VALUE
		this.no_entry_value = in.readInt();

		// NUMBER OF ENTRIES
		int size = in.readInt();
		setUp( size );

		// ENTRIES
		while ( size-- > 0 ) {
			// noinspection unchecked
			final K key = (K) in.readObject();
			final int val = in.readInt();
			put( key, val );
		}
	}


	/**
	 * rehashes the map to the new capacity.
	 * 
	 * @param newCapacity
	 *        an <code>int</code> value
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void rehash ( int newCapacity ) {
		final int oldCapacity = this._set.length;
		if ( oldCapacity == newCapacity ) {
			return;
		}

		// noinspection unchecked
		final K oldKeys[] = (K[]) this._set;
		final int oldVals[] = this._values;

		this._set = new Object[newCapacity];
		Arrays.fill( this._set, FREE );
		this._values = new int[newCapacity];
		Arrays.fill( this._values, this.no_entry_value );

		for ( int i = oldCapacity; i-- > 0; ) {
			if ( (oldKeys[ i ] != FREE) && (oldKeys[ i ] != REMOVED) ) {
				final K o = oldKeys[ i ];
				final int index = insertionIndex( o );
				if ( index < 0 ) {
					throwObjectContractViolation( this._set[ (-index - 1) ], o );
				}
				this._set[ index ] = o;
				this._values[ index ] = oldVals[ i ];
			}
		}
	}


	/** {@inheritDoc} */
	public int remove ( Object key ) {
		int prev = this.no_entry_value;
		final int index = index( key );
		if ( index >= 0 ) {
			prev = this._values[ index ];
			removeAt( index ); // clear key,state; adjust size
		}
		return prev;
	}


	// Comparison and hashing

	/**
	 * Removes the mapping at <tt>index</tt> from the map. This method is used
	 * internally and public mainly because of packaging reasons. Caveat
	 * Programmer.
	 * 
	 * @param index
	 *        an <code>int</code> value
	 */
	@Override
	protected void removeAt ( int index ) {
		this._values[ index ] = this.no_entry_value;
		super.removeAt( index ); // clear key, state; adjust size
	}


	/**
	 * Retains only those entries in the map for which the procedure returns a
	 * true value.
	 * 
	 * @param procedure
	 *        determines which entries to keep
	 * @return true if the map was modified.
	 */
	@SuppressWarnings("unchecked")
	public boolean retainEntries ( TObjectIntProcedure<K> procedure ) {
		boolean modified = false;
		// noinspection unchecked
		final K[] keys = (K[]) this._set;
		final int[] values = this._values;

		// Temporarily disable compaction. This is a fix for bug #1738760
		tempDisableAutoCompaction();
		try {
			for ( int i = keys.length; i-- > 0; ) {
				if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED)
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
	public int setUp ( int initialCapacity ) {
		int capacity;

		capacity = super.setUp( initialCapacity );
		this._values = new int[capacity];
		return capacity;
	}


	/** {@inheritDoc} */
	@Override
	public String toString () {
		final StringBuilder buf = new StringBuilder( "{" );
		forEachEntry( new TObjectIntProcedure<K>()
		{
			private boolean first = true;


			public boolean execute ( K key, int value ) {
				if ( this.first ) {
					this.first = false;
				}
				else {
					buf.append( "," );
				}

				buf.append( key ).append( "=" ).append( value );
				return true;
			}
		} );
		buf.append( "}" );
		return buf.toString();
	}


	/**
	 * Transform the values in this map using <tt>function</tt>.
	 * 
	 * @param function
	 *        a <code>TIntFunction</code> value
	 */
	public void transformValues ( TIntFunction function ) {
		final Object[] keys = this._set;
		final int[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( (keys[ i ] != null) && (keys[ i ] != REMOVED) ) {
				values[ i ] = function.execute( values[ i ] );
			}
		}
	}


	/** {@inheritDoc} */
	public TIntCollection valueCollection () {
		return new TIntValueCollection();
	}


	// Externalization

	/** {@inheritDoc} */
	public int[] values () {
		final int[] vals = new int[size()];
		final int[] v = this._values;
		final Object[] keys = this._set;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED) ) {
				vals[ j++ ] = v[ i ];
			}
		}
		return vals;
	}


	/** {@inheritDoc} */
	public int[] values ( int[] aArray ) {
		final int size = size();
		int[] array = aArray;
		if ( array.length < size ) {
			array = new int[size];
		}

		final int[] v = this._values;
		final Object[] keys = this._set;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED) ) {
				array[ j++ ] = v[ i ];
			}
		}
		if ( array.length > size ) {
			array[ size ] = this.no_entry_value;
		}
		return array;
	}


	@Override
	public void writeExternal ( ObjectOutput out ) throws IOException {
		// VERSION
		out.writeByte( 0 );

		// SUPER
		super.writeExternal( out );

		// NO_ENTRY_VALUE
		out.writeInt( this.no_entry_value );

		// NUMBER OF ENTRIES
		out.writeInt( this._size );

		// ENTRIES
		for ( int i = this._set.length; i-- > 0; ) {
			if ( (this._set[ i ] != REMOVED) && (this._set[ i ] != FREE) ) {
				out.writeObject( this._set[ i ] );
				out.writeInt( this._values[ i ] );
			}
		}
	}
} // TObjectIntHashMap
