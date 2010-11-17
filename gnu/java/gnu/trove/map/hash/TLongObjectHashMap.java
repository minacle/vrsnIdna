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

import gnu.trove.TLongCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

/**
 * An open addressed Map implementation for long keys and Object values.
 * Created: Sun Nov 4 08:52:45 2001
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */
public class TLongObjectHashMap<V> extends TLongHash implements
		TLongObjectMap<V>, Externalizable {

	class KeyView implements TLongSet {

		private static final long serialVersionUID = -4940294449157068415L;

		class TLongHashIterator extends THashPrimitiveIterator implements
				TLongIterator {

			/** the collection on which the iterator operates */
			private final TLongHash _hash;


			/** {@inheritDoc} */
			public TLongHashIterator ( TLongHash hash ) {
				super( hash );
				this._hash = hash;
			}


			/** {@inheritDoc} */
			public long next () {
				moveToNextIndex();
				return this._hash._set[ this._index ];
			}
		}


		/** {@inheritDoc} */
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
			TLongObjectHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( long entry ) {
			return TLongObjectHashMap.this.containsKey( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( !TLongObjectHashMap.this
						.containsKey( ((Long) element).longValue() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( long[] array ) {
			for ( final long element : array ) {
				if ( !TLongObjectHashMap.this.containsKey( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TLongCollection collection ) {
			if ( collection == this ) {
				return true;
			}
			final TLongIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TLongObjectHashMap.this.containsKey( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc)  */
		@Override
		public boolean equals ( Object other ) {
			if ( !(other instanceof TLongSet) ) {
				return false;
			}
			final TLongSet that = (TLongSet) other;
			if ( that.size() != this.size() ) {
				return false;
			}
			for ( int i = TLongObjectHashMap.this._states.length; i-- > 0; ) {
				if ( TLongObjectHashMap.this._states[ i ] == FULL ) {
					if ( !that.contains( TLongObjectHashMap.this._set[ i ] ) ) {
						return false;
					}
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TLongProcedure procedure ) {
			return TLongObjectHashMap.this.forEachKey( procedure );
		}


		/** {@inheritDoc} */
		public long getNoEntryValue () {
			return TLongObjectHashMap.this.no_entry_key;
		}


		/** {@inheritDoc} */
		@Override
		public int hashCode () {
			int hashcode = 0;
			for ( int i = TLongObjectHashMap.this._states.length; i-- > 0; ) {
				if ( TLongObjectHashMap.this._states[ i ] == FULL ) {
					hashcode += HashFunctions.hash( TLongObjectHashMap.this._set[ i ] );
				}
			}
			return hashcode;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return TLongObjectHashMap.this._size == 0;
		}


		/** {@inheritDoc} */
		public TLongIterator iterator () {
			return new TLongHashIterator( TLongObjectHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( long entry ) {
			return null != TLongObjectHashMap.this.remove( entry );
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
		public boolean retainAll ( Collection<?> collection ) {
			boolean modified = false;
			final TLongIterator iter = iterator();
			while ( iter.hasNext() ) {
				// noinspection SuspiciousMethodCalls
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
			final long[] set = TLongObjectHashMap.this._set;
			final byte[] states = TLongObjectHashMap.this._states;

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
			return TLongObjectHashMap.this._size;
		}


		/** {@inheritDoc} */
		public long[] toArray () {
			return keys();
		}


		/** {@inheritDoc} */
		public long[] toArray ( long[] dest ) {
			return keys( dest );
		}


		/** {@inheritDoc} */
		@Override
		public String toString () {
			final StringBuilder buf = new StringBuilder( "{" );
			boolean first = true;
			for ( int i = TLongObjectHashMap.this._states.length; i-- > 0; ) {
				if ( TLongObjectHashMap.this._states[ i ] == FULL ) {
					if ( first ) {
						first = false;
					}
					else {
						buf.append( "," );
					}
					buf.append( TLongObjectHashMap.this._set[ i ] );
				}
			}
			return buf.toString();
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
			TLongObjectHashMap.this.clear();
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
			return TLongObjectHashMap.this.isEmpty();
		}


		@Override
		public abstract Iterator<E> iterator ();


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
			return TLongObjectHashMap.this.size();
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


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public <T> T[] toArray ( T[] aArray ) {
			final int size = size();
			T[] a = aArray;
			if ( a.length < size ) {
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

	@SuppressWarnings("hiding")
	class TLongObjectHashIterator<V> extends THashPrimitiveIterator implements
			TLongObjectIterator<V> {

		/** the collection being iterated over */
		private final TLongObjectHashMap<V> _map;


		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param map
		 *        map to iterate over.
		 */
		public TLongObjectHashIterator ( TLongObjectHashMap<V> map ) {
			super( map );
			this._map = map;
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		public long key () {
			return this._map._set[ this._index ];
		}


		/** {@inheritDoc} */
		public V setValue ( V val ) {
			final V old = value();
			this._map._values[ this._index ] = val;
			return old;
		}


		/** {@inheritDoc} */
		public V value () {
			return this._map._values[ this._index ];
		}
	}

	/** a view onto the values of the map. */
	protected class ValueView extends MapBackedView<V> {

		class TLongObjectValueHashIterator extends THashPrimitiveIterator implements
				Iterator<V> {

			@SuppressWarnings("unchecked")
			protected final TLongObjectHashMap _map;


			@SuppressWarnings("unchecked")
			public TLongObjectValueHashIterator ( TLongObjectHashMap map ) {
				super( map );
				this._map = map;
			}


			/** {@inheritDoc} */
			@SuppressWarnings("unchecked")
			public V next () {
				moveToNextIndex();
				return (V) this._map._values[ this._index ];
			}


			@SuppressWarnings("unchecked")
			protected V objectAtIndex ( int index ) {
				final byte[] states = TLongObjectHashMap.this._states;
				final Object value = this._map._values[ index ];
				if ( states[ index ] != FULL ) {
					return null;
				}
				return (V) value;
			}
		}


		@Override
		public boolean containsElement ( V value ) {
			return containsValue( value );
		}


		@Override
		public Iterator<V> iterator () {
			return new TLongObjectValueHashIterator( TLongObjectHashMap.this )
			{
				@Override
				protected V objectAtIndex ( int index ) {
					return TLongObjectHashMap.this._values[ index ];
				}
			};
		}


		@Override
		public boolean removeElement ( V value ) {
			final V[] values = TLongObjectHashMap.this._values;
			final byte[] states = TLongObjectHashMap.this._states;

			for ( int i = values.length; i-- > 0; ) {
				if ( states[ i ] == FULL ) {
					if ( (value == values[ i ])
							|| ((null != values[ i ]) && values[ i ].equals( value )) ) {
						removeAt( i );
						return true;
					}
				}
			}
			return false;
		}
	}

	static final long serialVersionUID = 1L;

	private final TLongObjectProcedure<V> PUT_ALL_PROC =
			new TLongObjectProcedure<V>()
			{
				public boolean execute ( long key, V value ) {
					put( key, value );
					return true;
				}
			};

	/** the values of the map */
	protected transient V[] _values;

	/** the value that represents null in the key set. */
	protected long no_entry_key;


	/**
	 * Creates a new <code>TLongObjectHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TLongObjectHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TLongObjectHashMap</code> instance with a prime
	 * capacity equal to or greater than <tt>initialCapacity</tt> and with the
	 * default load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TLongObjectHashMap ( int initialCapacity ) {
		super( initialCapacity );
		this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
	}


	/**
	 * Creates a new <code>TLongObjectHashMap</code> instance with a prime
	 * capacity equal to or greater than <tt>initialCapacity</tt> and with the
	 * specified load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TLongObjectHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
		this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
	}


	// Query Operations

	/**
	 * Creates a new <code>TLongObjectHashMap</code> instance with a prime value
	 * at or near the specified capacity and load factor.
	 * 
	 * @param initialCapacity
	 *        used to find a prime capacity for the table.
	 * @param loadFactor
	 *        used to calculate the threshold over which rehashing takes place.
	 * @param noEntryKey
	 *        the value used to represent null in the key set.
	 */
	public TLongObjectHashMap ( int initialCapacity, float loadFactor,
			long noEntryKey ) {
		super( initialCapacity, loadFactor );
		this.no_entry_value = noEntryKey;
	}


	/**
	 * Creates a new <code>TLongObjectHashMap</code> that contains the entries in
	 * the map passed to it.
	 * 
	 * @param map
	 *        the <tt>TLongObjectMap</tt> to be copied.
	 */
	public TLongObjectHashMap ( TLongObjectMap<V> map ) {
		this( map.size(), 0.5f, map.getNoEntryKey() );
		putAll( map );
	}


	/** {@inheritDoc} */
	@Override
	public void clear () {
		super.clear();
		Arrays.fill( this._set, 0, this._set.length, this.no_entry_key );
		Arrays.fill( this._states, 0, this._states.length, FREE );
		Arrays.fill( this._values, 0, this._values.length, null );
	}


	/** {@inheritDoc} */
	public boolean containsKey ( long key ) {
		return contains( key );
	}


	// Modification Operations

	/** {@inheritDoc} */
	public boolean containsValue ( Object val ) {
		final byte[] states = this._states;
		final V[] vals = this._values;

		// special case null values so that we don't have to
		// perform null checks before every call to equals()
		if ( null == val ) {
			for ( int i = vals.length; i-- > 0; ) {
				if ( (states[ i ] == FULL) && (val == vals[ i ]) ) {
					return true;
				}
			}
		}
		else {
			for ( int i = vals.length; i-- > 0; ) {
				if ( (states[ i ] == FULL)
						&& ((val == vals[ i ]) || val.equals( vals[ i ] )) ) {
					return true;
				}
			}
		} // end of else
		return false;
	}


	private V doPut ( long key, V value, int aIndex ) {
		byte previousState;
		V previous = null;
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
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals ( Object other ) {
		if ( !(other instanceof TLongObjectMap) ) {
			return false;
		}
		final TLongObjectMap that = (TLongObjectMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		try {
			final TLongObjectIterator iter = this.iterator();
			while ( iter.hasNext() ) {
				iter.advance();
				final long key = iter.key();
				final Object value = iter.value();
				if ( value == null ) {
					if ( !((that.get( key ) == null) && that.containsKey( key )) ) {
						return false;
					}
				}
				else {
					if ( !value.equals( that.get( key ) ) ) {
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


	/** {@inheritDoc} */
	public boolean forEachEntry ( TLongObjectProcedure<V> procedure ) {
		final byte[] states = this._states;
		final long[] keys = this._set;
		final V[] values = this._values;
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


	// Bulk Operations

	/** {@inheritDoc} */
	public boolean forEachValue ( TObjectProcedure<V> procedure ) {
		final byte[] states = this._states;
		final V[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && !procedure.execute( values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public V get ( long key ) {
		final int index = index( key );
		return index < 0 ? null : this._values[ index ];
	}


	/** {@inheritDoc} */
	public long getNoEntryKey () {
		return this.no_entry_key;
	}


	// Views

	/** {@inheritDoc} */
	@Override
	public int hashCode () {
		int hashcode = 0;
		final V[] values = this._values;
		final byte[] states = this._states;
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				hashcode +=
						HashFunctions.hash( this._set[ i ] )
								^ (values[ i ] == null ? 0 : values[ i ].hashCode());
			}
		}
		return hashcode;
	}


	/** {@inheritDoc} */
	public TLongObjectIterator<V> iterator () {
		return new TLongObjectHashIterator<V>( this );
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
	public long[] keys ( long[] aDest ) {
		long[] dest = aDest;
		if ( dest.length < this._size ) {
			dest = new long[this._size];
		}

		final long[] k = this._set;
		final byte[] states = this._states;

		for ( int i = k.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				dest[ j++ ] = k[ i ];
			}
		}
		return dest;
	}


	/** {@inheritDoc} */
	public TLongSet keySet () {
		return new KeyView();
	}


	/** {@inheritDoc} */
	public V put ( long key, V value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends Long, ? extends V> map ) {
		final Set<? extends Map.Entry<? extends Long, ? extends V>> set =
				map.entrySet();
		for ( final Map.Entry<? extends Long, ? extends V> entry : set ) {
			put( entry.getKey().longValue(), entry.getValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TLongObjectMap<V> map ) {
		map.forEachEntry( this.PUT_ALL_PROC );
	}


	/** {@inheritDoc} */
	public V putIfAbsent ( long key, V value ) {
		final int index = insertionIndex( key );
		if ( index < 0 ) {
			return this._values[ -index - 1 ];
		}
		return doPut( key, value, index );
	}


	@Override
	@SuppressWarnings( {
		"unchecked"
	})
	public void readExternal ( ObjectInput in ) throws IOException,
			ClassNotFoundException {

		// VERSION
		in.readByte();

		// SUPER
		super.readExternal( in );

		// NO_ENTRY_KEY
		this.no_entry_key = in.readLong();

		// NUMBER OF ENTRIES
		int size = in.readInt();
		setUp( size );

		// ENTRIES
		while ( size-- > 0 ) {
			final long key = in.readLong();
			final V val = (V) in.readObject();
			put( key, val );
		}
	}


	/** {@inheritDoc} */
	@Override
	@SuppressWarnings( {
		"unchecked"
	})
	protected void rehash ( int newCapacity ) {
		final int oldCapacity = this._set.length;
		if ( oldCapacity == newCapacity ) {
			return;
		}

		final long oldKeys[] = this._set;
		final V oldVals[] = this._values;
		final byte oldStates[] = this._states;

		this._set = new long[newCapacity];
		this._values = (V[]) new Object[newCapacity];
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
	public V remove ( long key ) {
		V prev = null;
		final int index = index( key );
		if ( index >= 0 ) {
			prev = this._values[ index ];
			removeAt( index ); // clear key,state; adjust size
		}
		return prev;
	}


	// Comparison and hashing

	/** {@inheritDoc} */
	@Override
	protected void removeAt ( int index ) {
		this._values[ index ] = null;
		super.removeAt( index ); // clear key, state; adjust size
	}


	/** {@inheritDoc} */
	public boolean retainEntries ( TLongObjectProcedure<V> procedure ) {
		boolean modified = false;
		final byte[] states = this._states;
		final long[] keys = this._set;
		final V[] values = this._values;

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


	/** {@inheritDoc} */
	@Override
	@SuppressWarnings( {
		"unchecked"
	})
	protected int setUp ( int initialCapacity ) {
		int capacity;

		capacity = super.setUp( initialCapacity );
		this._values = (V[]) new Object[capacity];
		return capacity;
	}


	@Override
	public String toString () {
		final StringBuilder buf = new StringBuilder( "{" );
		forEachEntry( new TLongObjectProcedure<V>()
		{
			private boolean first = true;


			public boolean execute ( long key, Object value ) {
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
	public void transformValues ( TObjectFunction<V, V> function ) {
		final byte[] states = this._states;
		final V[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				values[ i ] = function.execute( values[ i ] );
			}
		}
	}


	/** {@inheritDoc} */
	public Collection<V> valueCollection () {
		return new ValueView();
	}


	/** {@inheritDoc} */
	@SuppressWarnings( {
		"unchecked"
	})
	public V[] values () {
		final V[] vals = (V[]) new Object[size()];
		final V[] v = this._values;
		final byte[] states = this._states;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				vals[ j++ ] = v[ i ];
			}
		}
		return vals;
	}


	/** {@inheritDoc} */
	@SuppressWarnings( {
		"unchecked"
	})
	public <T> T[] values ( T[] aDest ) {
		T[] dest = aDest;
		if ( dest.length < this._size ) {
			dest =
					(T[]) java.lang.reflect.Array.newInstance( dest
							.getClass()
							.getComponentType(), this._size );
		}

		final V[] v = this._values;
		final byte[] states = this._states;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				dest[ j++ ] = (T) v[ i ];
			}
		}
		return dest;
	}


	@Override
	public void writeExternal ( ObjectOutput out ) throws IOException {
		// VERSION
		out.writeByte( 0 );

		// SUPER
		super.writeExternal( out );

		// NO_ENTRY_KEY
		out.writeLong( this.no_entry_key );

		// NUMBER OF ENTRIES
		out.writeInt( this._size );

		// ENTRIES
		for ( int i = this._states.length; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				out.writeLong( this._set[ i ] );
				out.writeObject( this._values[ i ] );
			}
		}
	}
} // TLongObjectHashMap
