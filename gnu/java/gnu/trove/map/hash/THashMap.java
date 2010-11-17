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

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectObjectProcedure;
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
import java.util.Set;

/**
 * An implementation of the Map interface which uses an open addressed hash
 * table to store its contents.
 * <p/>
 * Created: Sun Nov 4 08:52:45 2001
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: THashMap.java,v 1.1.2.4 2009/11/16 21:25:13 robeden Exp $
 */

public class THashMap<K, V> extends TObjectHash<K> implements Map<K, V>,
		Externalizable {

	final class Entry implements Map.Entry<K, V> {

		private final K key;
		private V val;
		private final int index;


		Entry ( final K key, V value, final int index ) {
			this.key = key;
			this.val = value;
			this.index = index;
		}


		@SuppressWarnings("unchecked")
		@Override
		public boolean equals ( Object o ) {
			if ( o instanceof Map.Entry ) {
				final Map.Entry<K, V> e1 = this;
				final Map.Entry e2 = (Map.Entry) o;
				return (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(
						e2.getKey() ))
						&& (e1.getValue() == null ? e2.getValue() == null : e1
								.getValue()
								.equals( e2.getValue() ));
			}
			return false;
		}


		public K getKey () {
			return this.key;
		}


		public V getValue () {
			return this.val;
		}


		@Override
		public int hashCode () {
			return (getKey() == null ? 0 : getKey().hashCode())
					^ (getValue() == null ? 0 : getValue().hashCode());
		}


		public V setValue ( V o ) {
			if ( THashMap.this._values[ this.index ] != this.val ) {
				throw new ConcurrentModificationException();
			}
			// need to return previous value
			final V retval = this.val;
			// update this entry's value, in case setValue is called again
			THashMap.this._values[ this.index ] = o;
			this.val = o;
			return retval;
		}


		@Override
		public String toString () {
			return this.key + "=" + this.val;
		}
	}

	/** a view onto the entries of the map. */
	protected class EntryView extends MapBackedView<Map.Entry<K, V>> {

		@SuppressWarnings("unchecked")
		private final class EntryIterator extends TObjectHashIterator {

			EntryIterator ( THashMap<K, V> map ) {
				super( map );
			}


			@Override
			public Entry objectAtIndex ( final int index ) {
				return new Entry( (K) THashMap.this._set[ index ],
						THashMap.this._values[ index ], index );
			}
		}


		@Override
		public boolean containsElement ( Map.Entry<K, V> entry ) {
			final Object val = get( keyForEntry( entry ) );
			final Object entryValue = entry.getValue();
			return (entryValue == val) || ((null != val) && val.equals( entryValue ));
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public Iterator<Map.Entry<K, V>> iterator () {
			return new EntryIterator( THashMap.this );
		}


		protected K keyForEntry ( Map.Entry<K, V> entry ) {
			return entry.getKey();
		}


		@Override
		public boolean removeElement ( Map.Entry<K, V> entry ) {
			// have to effectively reimplement Map.remove here
			// because we need to return true/false depending on
			// whether the removal took place. Since the Entry's
			// value can be null, this means that we can't rely
			// on the value of the object returned by Map.remove()
			// to determine whether a deletion actually happened.
			//
			// Note also that the deletion is only legal if
			// both the key and the value match.
			Object val;
			int index;

			final K key = keyForEntry( entry );
			index = index( key );
			if ( index >= 0 ) {
				val = valueForEntry( entry );
				if ( (val == THashMap.this._values[ index ])
						|| ((null != val) && val.equals( THashMap.this._values[ index ] )) ) {
					removeAt( index ); // clear key,state; adjust size
					return true;
				}
			}
			return false;
		}


		protected V valueForEntry ( Map.Entry<K, V> entry ) {
			return entry.getValue();
		}
	}

	private static final class EqProcedure<K, V> implements
			TObjectObjectProcedure<K, V> {

		private final Map<K, V> _otherMap;


		EqProcedure ( Map<K, V> otherMap ) {
			this._otherMap = otherMap;
		}


		public final boolean execute ( K key, V value ) {
			// Check to make sure the key is there. This avoids problems that come up
			// with
			// null values. Since it is only caused in that cause, only do this when
			// the
			// value is null (to avoid extra work).
			if ( (value == null) && !this._otherMap.containsKey( key ) ) {
				return false;
			}

			final V oValue = this._otherMap.get( key );
			return (oValue == value) || ((oValue != null) && oValue.equals( value ));
		}
	}

	private final class HashProcedure implements TObjectObjectProcedure<K, V> {

		private int h = 0;


		public final boolean execute ( K key, V value ) {
			this.h +=
					HashFunctions.hash( key ) ^ (value == null ? 0 : value.hashCode());
			return true;
		}


		public int getHashCode () {
			return this.h;
		}
	}

	/** a view onto the keys of the map. */
	protected class KeyView extends MapBackedView<K> {

		@Override
		public boolean containsElement ( K key ) {
			return THashMap.this.contains( key );
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public Iterator<K> iterator () {
			return new TObjectHashIterator( THashMap.this );
		}


		@Override
		public boolean removeElement ( K key ) {
			return null != THashMap.this.remove( key );
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
			THashMap.this.clear();
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public boolean contains ( Object key ) {
			return containsElement( (E) key );
		}


		public abstract boolean containsElement ( E key );


		// public boolean containsAll( Collection<?> collection ) {
		// for ( Object element : collection ) {
		// if ( !contains( element ) ) {
		// return false;
		// }
		// }
		// return true;
		// }

		@Override
		public boolean isEmpty () {
			return THashMap.this.isEmpty();
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
			return THashMap.this.size();
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
		public <T> T[] toArray ( T[] aObject ) {
			final int size = size();
			T[] a = aObject;
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

	/** a view onto the values of the map. */
	protected class ValueView extends MapBackedView<V> {

		@Override
		public boolean containsElement ( V value ) {
			return containsValue( value );
		}


		@Override
		@SuppressWarnings( {
			"unchecked"
		})
		public Iterator<V> iterator () {
			return new TObjectHashIterator( THashMap.this )
			{
				@Override
				protected V objectAtIndex ( int index ) {
					return THashMap.this._values[ index ];
				}
			};
		}


		@Override
		public boolean removeElement ( V value ) {
			final Object[] values = THashMap.this._values;
			final Object[] set = THashMap.this._set;

			for ( int i = values.length; i-- > 0; ) {
				if ( (((set[ i ] != FREE) && (set[ i ] != REMOVED)) && (value == values[ i ]))
						|| ((null != values[ i ]) && values[ i ].equals( value )) ) {

					removeAt( i );
					return true;
				}
			}

			return false;
		}
	}

	static final long serialVersionUID = 1L;

	/** the values of the map */
	protected transient V[] _values;


	/**
	 * Creates a new <code>THashMap</code> instance with the default capacity and
	 * load factor.
	 */
	public THashMap () {
		super();
	}


	/**
	 * Creates a new <code>THashMap</code> instance with a prime capacity equal to
	 * or greater than <tt>initialCapacity</tt> and with the default load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public THashMap ( int initialCapacity ) {
		super( initialCapacity );
	}


	/**
	 * Creates a new <code>THashMap</code> instance with a prime capacity equal to
	 * or greater than <tt>initialCapacity</tt> and with the specified load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public THashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
	}


	/**
	 * Creates a new <code>THashMap</code> instance which contains the key/value
	 * pairs in <tt>map</tt>.
	 * 
	 * @param map
	 *        a <code>Map</code> value
	 */
	public THashMap ( Map<K, V> map ) {
		this( map.size() );
		putAll( map );
	}


	/**
	 * Creates a new <code>THashMap</code> instance which contains the key/value
	 * pairs in <tt>map</tt>.
	 * 
	 * @param map
	 *        a <code>Map</code> value
	 */
	public THashMap ( THashMap<K, V> map ) {
		this( map.size() );
		putAll( map );
	}


	/** Empties the map. */
	@Override
	public void clear () {
		if ( size() == 0 ) {
			return; // optimization
		}

		super.clear();

		Arrays.fill( this._set, 0, this._set.length, FREE );
		Arrays.fill( this._values, 0, this._values.length, null );
	}


	/**
	 * checks for the present of <tt>key</tt> in the keys of the map.
	 * 
	 * @param key
	 *        an <code>Object</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean containsKey ( Object key ) {
		return contains( key );
	}


	/**
	 * checks for the presence of <tt>val</tt> in the values of the map.
	 * 
	 * @param val
	 *        an <code>Object</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean containsValue ( Object val ) {
		final Object[] set = this._set;
		final V[] vals = this._values;

		// special case null values so that we don't have to
		// perform null checks before every call to equals()
		if ( null == val ) {
			for ( int i = vals.length; i-- > 0; ) {
				if ( ((set[ i ] != FREE) && (set[ i ] != REMOVED))
						&& (val == vals[ i ]) ) {
					return true;
				}
			}
		}
		else {
			for ( int i = vals.length; i-- > 0; ) {
				if ( ((set[ i ] != FREE) && (set[ i ] != REMOVED))
						&& ((val == vals[ i ]) || val.equals( vals[ i ] )) ) {
					return true;
				}
			}
		} // end of else
		return false;
	}


	private V doPut ( K key, V value, int aIndex ) {
		V previous = null;
		Object oldKey;
		boolean isNewMapping = true;
		int index = aIndex;
		if ( index < 0 ) {
			index = -index - 1;
			previous = this._values[ index ];
			isNewMapping = false;
		}
		oldKey = this._set[ index ];
		this._set[ index ] = key;
		this._values[ index ] = value;
		if ( isNewMapping ) {
			postInsertHook( oldKey == FREE );
		}

		return previous;
	}


	/**
	 * Returns a Set view on the entries of the map.
	 * 
	 * @return a <code>Set</code> value
	 */
	public Set<Map.Entry<K, V>> entrySet () {
		return new EntryView();
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
		if ( !(other instanceof Map) ) {
			return false;
		}
		final Map<K, V> that = (Map<K, V>) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		return forEachEntry( new EqProcedure<K, V>( that ) );
	}


	/**
	 * Executes <tt>procedure</tt> for each key/value entry in the map.
	 * 
	 * @param procedure
	 *        a <code>TObjectObjectProcedure</code> value
	 * @return false if the loop over the entries terminated because the procedure
	 *         returned false for some entry.
	 */
	@SuppressWarnings( {
		"unchecked"
	})
	public boolean forEachEntry ( TObjectObjectProcedure<K, V> procedure ) {
		final Object[] keys = this._set;
		final V[] values = this._values;
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
	 *        a <code>TObjectProcedure</code> value
	 * @return false if the loop over the values terminated because the procedure
	 *         returned false for some value.
	 */
	public boolean forEachValue ( TObjectProcedure<V> procedure ) {
		final V[] values = this._values;
		final Object[] set = this._set;
		for ( int i = values.length; i-- > 0; ) {
			if ( (set[ i ] != FREE) && (set[ i ] != REMOVED)
					&& !procedure.execute( values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/**
	 * retrieves the value for <tt>key</tt>
	 * 
	 * @param key
	 *        an <code>Object</code> value
	 * @return the value of <tt>key</tt> or null if no such mapping exists.
	 */
	public V get ( Object key ) {
		final int index = index( key );
		if ( (index < 0) || !this._set[ index ].equals( key ) ) {
			return null;
		}
		return this._values[ index ];
	}


	@Override
	public int hashCode () {
		final HashProcedure p = new HashProcedure();
		forEachEntry( p );
		return p.getHashCode();
	}


	/**
	 * returns a Set view on the keys of the map.
	 * 
	 * @return a <code>Set</code> value
	 */
	public Set<K> keySet () {
		return new KeyView();
	}


	/**
	 * Inserts a key/value pair into the map.
	 * 
	 * @param key
	 *        an <code>Object</code> value
	 * @param value
	 *        an <code>Object</code> value
	 * @return the previous value associated with <tt>key</tt>, or {@code null} if
	 *         none was found.
	 */
	public V put ( K key, V value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/**
	 * copies the key/value mappings in <tt>map</tt> into this map.
	 * 
	 * @param map
	 *        a <code>Map</code> value
	 */
	public void putAll ( Map<? extends K, ? extends V> map ) {
		ensureCapacity( map.size() );
		// could optimize this for cases when map instanceof THashMap
		for ( final Map.Entry<? extends K, ? extends V> e : map.entrySet() ) {
			put( e.getKey(), e.getValue() );
		}
	}


	/**
	 * Inserts a key/value pair into the map if the specified key is not already
	 * associated with a value.
	 * 
	 * @param key
	 *        an <code>Object</code> value
	 * @param value
	 *        an <code>Object</code> value
	 * @return the previous value associated with <tt>key</tt>, or {@code null} if
	 *         none was found.
	 */
	public V putIfAbsent ( K key, V value ) {
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
		final byte version = in.readByte();

		// NOTE: super was not written in version 0
		if ( version != 0 ) {
			super.readExternal( in );
		}

		// NUMBER OF ENTRIES
		int size = in.readInt();
		setUp( size );

		// ENTRIES
		while ( size-- > 0 ) {
			// noinspection unchecked
			final K key = (K) in.readObject();
			// noinspection unchecked
			final V val = (V) in.readObject();
			put( key, val );
		}
	}


	/**
	 * rehashes the map to the new capacity.
	 * 
	 * @param newCapacity
	 *        an <code>int</code> value
	 */
	@Override
	@SuppressWarnings( {
		"unchecked"
	})
	protected void rehash ( int newCapacity ) {
		final int oldCapacity = this._set.length;
		if ( oldCapacity == newCapacity ) {
			return;
		}

		final Object oldKeys[] = this._set;
		final V oldVals[] = this._values;

		this._set = new Object[newCapacity];
		Arrays.fill( this._set, FREE );
		this._values = (V[]) new Object[newCapacity];

		for ( int i = oldCapacity; i-- > 0; ) {
			if ( (oldKeys[ i ] != FREE) && (oldKeys[ i ] != REMOVED) ) {
				final Object o = oldKeys[ i ];
				final int index = insertionIndex( (K) o );
				if ( index < 0 ) {
					throwObjectContractViolation( this._set[ (-index - 1) ], o );
				}
				this._set[ index ] = o;
				this._values[ index ] = oldVals[ i ];
			}
		}
	}


	/**
	 * Deletes a key/value pair from the map.
	 * 
	 * @param key
	 *        an <code>Object</code> value
	 * @return an <code>Object</code> value
	 */
	public V remove ( Object key ) {
		V prev = null;
		final int index = index( key );
		if ( index >= 0 ) {
			prev = this._values[ index ];
			removeAt( index ); // clear key,state; adjust size
		}
		return prev;
	}


	/**
	 * removes the mapping at <tt>index</tt> from the map.
	 * 
	 * @param index
	 *        an <code>int</code> value
	 */
	@Override
	public void removeAt ( int index ) {
		this._values[ index ] = null;
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
	@SuppressWarnings( {
		"unchecked"
	})
	public boolean retainEntries ( TObjectObjectProcedure<K, V> procedure ) {
		boolean modified = false;
		final Object[] keys = this._set;
		final V[] values = this._values;

		// Temporarily disable compaction. This is a fix for bug #1738760
		tempDisableAutoCompaction();
		try {
			for ( int i = keys.length; i-- > 0; ) {
				if ( (keys[ i ] != FREE) && (keys[ i ] != REMOVED)
						&& !procedure.execute( (K) keys[ i ], values[ i ] ) ) {
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
	 * initialize the value array of the map.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @return an <code>int</code> value
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int setUp ( int initialCapacity ) {
		int capacity;

		capacity = super.setUp( initialCapacity );
		// noinspection unchecked
		this._values = (V[]) new Object[capacity];
		return capacity;
	}


	@Override
	public String toString () {
		final StringBuilder buf = new StringBuilder( "{" );
		forEachEntry( new TObjectObjectProcedure<K, V>()
		{
			private boolean first = true;


			public boolean execute ( K key, V value ) {
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


	/**
	 * Transform the values in this map using <tt>function</tt>.
	 * 
	 * @param function
	 *        a <code>TObjectFunction</code> value
	 */
	public void transformValues ( TObjectFunction<V, V> function ) {
		final V[] values = this._values;
		final Object[] set = this._set;
		for ( int i = values.length; i-- > 0; ) {
			if ( (set[ i ] != FREE) && (set[ i ] != REMOVED) ) {
				values[ i ] = function.execute( values[ i ] );
			}
		}
	}


	/**
	 * Returns a view on the values of the map.
	 * 
	 * @return a <code>Collection</code> value
	 */
	public Collection<V> values () {
		return new ValueView();
	}


	@Override
	public void writeExternal ( ObjectOutput out ) throws IOException {
		// VERSION
		out.writeByte( 1 );

		// NOTE: Super was not written in version 0
		super.writeExternal( out );

		// NUMBER OF ENTRIES
		out.writeInt( this._size );

		// ENTRIES
		for ( int i = this._set.length; i-- > 0; ) {
			if ( (this._set[ i ] != REMOVED) && (this._set[ i ] != FREE) ) {
				out.writeObject( this._set[ i ] );
				out.writeObject( this._values[ i ] );
			}
		}
	}
} // THashMap
