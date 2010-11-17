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
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;

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
 * An open addressed Map implementation for int keys and Object values. Created:
 * Sun Nov 4 08:52:45 2001
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */
public class TIntObjectHashMap<V> extends TIntHash implements TIntObjectMap<V>,
		Externalizable {

	class KeyView implements TIntSet {

		private static final long serialVersionUID = 5785251292507343750L;

		class TIntHashIterator extends THashPrimitiveIterator implements
				TIntIterator {

			/** the collection on which the iterator operates */
			private final TIntHash _hash;


			/** {@inheritDoc} */
			public TIntHashIterator ( TIntHash hash ) {
				super( hash );
				this._hash = hash;
			}


			/** {@inheritDoc} */
			public int next () {
				moveToNextIndex();
				return this._hash._set[ this._index ];
			}
		}


		/** {@inheritDoc} */
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
			TIntObjectHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( int entry ) {
			return TIntObjectHashMap.this.containsKey( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( !TIntObjectHashMap.this.containsKey( ((Integer) element)
						.intValue() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( int[] array ) {
			for ( final int element : array ) {
				if ( !TIntObjectHashMap.this.containsKey( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TIntCollection collection ) {
			if ( collection == this ) {
				return true;
			}
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TIntObjectHashMap.this.containsKey( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc)  */
		@Override
		public boolean equals ( Object other ) {
			if ( !(other instanceof TIntSet) ) {
				return false;
			}
			final TIntSet that = (TIntSet) other;
			if ( that.size() != this.size() ) {
				return false;
			}
			for ( int i = TIntObjectHashMap.this._states.length; i-- > 0; ) {
				if ( TIntObjectHashMap.this._states[ i ] == FULL ) {
					if ( !that.contains( TIntObjectHashMap.this._set[ i ] ) ) {
						return false;
					}
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TIntProcedure procedure ) {
			return TIntObjectHashMap.this.forEachKey( procedure );
		}


		/** {@inheritDoc} */
		public int getNoEntryValue () {
			return TIntObjectHashMap.this.no_entry_key;
		}


		/** {@inheritDoc} */
		@Override
		public int hashCode () {
			int hashcode = 0;
			for ( int i = TIntObjectHashMap.this._states.length; i-- > 0; ) {
				if ( TIntObjectHashMap.this._states[ i ] == FULL ) {
					hashcode += HashFunctions.hash( TIntObjectHashMap.this._set[ i ] );
				}
			}
			return hashcode;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return TIntObjectHashMap.this._size == 0;
		}


		/** {@inheritDoc} */
		public TIntIterator iterator () {
			return new TIntHashIterator( TIntObjectHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( int entry ) {
			return null != TIntObjectHashMap.this.remove( entry );
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
			if ( collection == this ) {
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
				// noinspection SuspiciousMethodCalls
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
			final int[] set = TIntObjectHashMap.this._set;
			final byte[] states = TIntObjectHashMap.this._states;

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
			return TIntObjectHashMap.this._size;
		}


		/** {@inheritDoc} */
		public int[] toArray () {
			return keys();
		}


		/** {@inheritDoc} */
		public int[] toArray ( int[] dest ) {
			return keys( dest );
		}


		/** {@inheritDoc} */
		@Override
		public String toString () {
			final StringBuilder buf = new StringBuilder( "{" );
			boolean first = true;
			for ( int i = TIntObjectHashMap.this._states.length; i-- > 0; ) {
				if ( TIntObjectHashMap.this._states[ i ] == FULL ) {
					if ( first ) {
						first = false;
					}
					else {
						buf.append( "," );
					}
					buf.append( TIntObjectHashMap.this._set[ i ] );
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
			TIntObjectHashMap.this.clear();
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
			return TIntObjectHashMap.this.isEmpty();
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
			return TIntObjectHashMap.this.size();
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
	class TIntObjectHashIterator<V> extends THashPrimitiveIterator implements
			TIntObjectIterator<V> {

		/** the collection being iterated over */
		private final TIntObjectHashMap<V> _map;


		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param map
		 *        map to iterate over.
		 */
		public TIntObjectHashIterator ( TIntObjectHashMap<V> map ) {
			super( map );
			this._map = map;
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		public int key () {
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

		class TIntObjectValueHashIterator extends THashPrimitiveIterator implements
				Iterator<V> {

			@SuppressWarnings("unchecked")
			protected final TIntObjectHashMap _map;


			@SuppressWarnings("unchecked")
			public TIntObjectValueHashIterator ( TIntObjectHashMap map ) {
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
				final byte[] states = TIntObjectHashMap.this._states;
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
			return new TIntObjectValueHashIterator( TIntObjectHashMap.this )
			{
				@Override
				protected V objectAtIndex ( int index ) {
					return TIntObjectHashMap.this._values[ index ];
				}
			};
		}


		@Override
		public boolean removeElement ( V value ) {
			final V[] values = TIntObjectHashMap.this._values;
			final byte[] states = TIntObjectHashMap.this._states;

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

	private final TIntObjectProcedure<V> PUT_ALL_PROC =
			new TIntObjectProcedure<V>()
			{
				public boolean execute ( int key, V value ) {
					put( key, value );
					return true;
				}
			};

	/** the values of the map */
	protected transient V[] _values;

	/** the value that represents null in the key set. */
	protected int no_entry_key;


	/**
	 * Creates a new <code>TIntObjectHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TIntObjectHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TIntObjectHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TIntObjectHashMap ( int initialCapacity ) {
		super( initialCapacity );
		this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
	}


	/**
	 * Creates a new <code>TIntObjectHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TIntObjectHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
		this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
	}


	// Query Operations

	/**
	 * Creates a new <code>TIntObjectHashMap</code> instance with a prime value at
	 * or near the specified capacity and load factor.
	 * 
	 * @param initialCapacity
	 *        used to find a prime capacity for the table.
	 * @param loadFactor
	 *        used to calculate the threshold over which rehashing takes place.
	 * @param noEntryKey
	 *        the value used to represent null in the key set.
	 */
	public TIntObjectHashMap ( int initialCapacity, float loadFactor,
			int noEntryKey ) {
		super( initialCapacity, loadFactor );
		this.no_entry_value = noEntryKey;
	}


	/**
	 * Creates a new <code>TIntObjectHashMap</code> that contains the entries in
	 * the map passed to it.
	 * 
	 * @param map
	 *        the <tt>TIntObjectMap</tt> to be copied.
	 */
	public TIntObjectHashMap ( TIntObjectMap<V> map ) {
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
	public boolean containsKey ( int key ) {
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


	private V doPut ( int key, V value, int aIndex ) {
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
		if ( !(other instanceof TIntObjectMap) ) {
			return false;
		}
		final TIntObjectMap that = (TIntObjectMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		try {
			final TIntObjectIterator iter = this.iterator();
			while ( iter.hasNext() ) {
				iter.advance();
				final int key = iter.key();
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
	public boolean forEachEntry ( TIntObjectProcedure<V> procedure ) {
		final byte[] states = this._states;
		final int[] keys = this._set;
		final V[] values = this._values;
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
	public V get ( int key ) {
		final int index = index( key );
		return index < 0 ? null : this._values[ index ];
	}


	/** {@inheritDoc} */
	public int getNoEntryKey () {
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
	public TIntObjectIterator<V> iterator () {
		return new TIntObjectHashIterator<V>( this );
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
	public int[] keys ( int[] aDest ) {
		int[] dest = aDest;
		if ( dest.length < this._size ) {
			dest = new int[this._size];
		}

		final int[] k = this._set;
		final byte[] states = this._states;

		for ( int i = k.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				dest[ j++ ] = k[ i ];
			}
		}
		return dest;
	}


	/** {@inheritDoc} */
	public TIntSet keySet () {
		return new KeyView();
	}


	/** {@inheritDoc} */
	public V put ( int key, V value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends Integer, ? extends V> map ) {
		final Set<? extends Map.Entry<? extends Integer, ? extends V>> set =
				map.entrySet();
		for ( final Map.Entry<? extends Integer, ? extends V> entry : set ) {
			put( entry.getKey().intValue(), entry.getValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TIntObjectMap<V> map ) {
		map.forEachEntry( this.PUT_ALL_PROC );
	}


	/** {@inheritDoc} */
	public V putIfAbsent ( int key, V value ) {
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
		this.no_entry_key = in.readInt();

		// NUMBER OF ENTRIES
		int size = in.readInt();
		setUp( size );

		// ENTRIES
		while ( size-- > 0 ) {
			final int key = in.readInt();
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

		final int oldKeys[] = this._set;
		final V oldVals[] = this._values;
		final byte oldStates[] = this._states;

		this._set = new int[newCapacity];
		this._values = (V[]) new Object[newCapacity];
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
	public V remove ( int key ) {
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
	public boolean retainEntries ( TIntObjectProcedure<V> procedure ) {
		boolean modified = false;
		final byte[] states = this._states;
		final int[] keys = this._set;
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
		forEachEntry( new TIntObjectProcedure<V>()
		{
			private boolean first = true;


			public boolean execute ( int key, Object value ) {
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
		out.writeInt( this.no_entry_key );

		// NUMBER OF ENTRIES
		out.writeInt( this._size );

		// ENTRIES
		for ( int i = this._states.length; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				out.writeInt( this._set[ i ] );
				out.writeObject( this._values[ i ] );
			}
		}
	}
} // TIntObjectHashMap
