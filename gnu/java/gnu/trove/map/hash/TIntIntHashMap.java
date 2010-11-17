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
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
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
 * An open addressed Map implementation for int keys and int values.
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_HashMap.template,v 1.1.2.13 2009/11/16 21:25:13 robeden
 *          Exp $
 */
public class TIntIntHashMap extends TIntIntHash implements TIntIntMap,
		Externalizable {
	class TIntIntHashIterator extends THashPrimitiveIterator implements
			TIntIntIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param map
		 *        the <tt>TIntIntHashMap</tt> we will be iterating over.
		 */
		TIntIntHashIterator ( TIntIntHashMap map ) {
			super( map );
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		public int key () {
			return TIntIntHashMap.this._set[ this._index ];
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
				TIntIntHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}
			this._expectedSize--;
		}


		/** {@inheritDoc} */
		public int setValue ( int val ) {
			final int old = value();
			TIntIntHashMap.this._values[ this._index ] = val;
			return old;
		}


		/** {@inheritDoc} */
		public int value () {
			return TIntIntHashMap.this._values[ this._index ];
		}
	}

	class TIntIntKeyHashIterator extends THashPrimitiveIterator implements
			TIntIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TIntIntKeyHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public int next () {
			moveToNextIndex();
			return TIntIntHashMap.this._set[ this._index ];
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
				TIntIntHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	class TIntIntValueHashIterator extends THashPrimitiveIterator implements
			TIntIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TIntIntValueHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public int next () {
			moveToNextIndex();
			return TIntIntHashMap.this._values[ this._index ];
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
				TIntIntHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	/** a view onto the keys of the map. */
	protected class TKeyView implements TIntSet {

		/**
		 * 
		 */
		private static final long serialVersionUID = -704152874267345615L;


		/**
		 * Unsupported when operating upon a Key Set view of a TIntIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean add ( int entry ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TIntIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( Collection<? extends Integer> collection ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TIntIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( int[] array ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TIntIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( TIntCollection collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public void clear () {
			TIntIntHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( int entry ) {
			return TIntIntHashMap.this.contains( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Integer ) {
					final int ele = ((Integer) element).intValue();
					if ( !TIntIntHashMap.this.containsKey( ele ) ) {
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
				if ( !TIntIntHashMap.this.contains( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TIntCollection collection ) {
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TIntIntHashMap.this.containsKey( iter.next() ) ) {
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
			for ( int i = TIntIntHashMap.this._states.length; i-- > 0; ) {
				if ( TIntIntHashMap.this._states[ i ] == FULL ) {
					if ( !that.contains( TIntIntHashMap.this._set[ i ] ) ) {
						return false;
					}
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TIntProcedure procedure ) {
			return TIntIntHashMap.this.forEachKey( procedure );
		}


		/** {@inheritDoc} */
		public int getNoEntryValue () {
			return TIntIntHashMap.this.no_entry_key;
		}


		@Override
		public int hashCode () {
			int hashcode = 0;
			for ( int i = TIntIntHashMap.this._states.length; i-- > 0; ) {
				if ( TIntIntHashMap.this._states[ i ] == FULL ) {
					hashcode += HashFunctions.hash( TIntIntHashMap.this._set[ i ] );
				}
			}
			return hashcode;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TIntIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TIntIterator iterator () {
			return new TIntIntKeyHashIterator( TIntIntHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( int entry ) {
			return TIntIntHashMap.this.no_entry_value != TIntIntHashMap.this
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
			final int[] set = TIntIntHashMap.this._set;
			final byte[] states = TIntIntHashMap.this._states;

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
			return TIntIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public int[] toArray () {
			return TIntIntHashMap.this.keys();
		}


		/** {@inheritDoc} */
		public int[] toArray ( int[] dest ) {
			return TIntIntHashMap.this.keys( dest );
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
	protected class TValueView implements TIntCollection {

		private static final long serialVersionUID = 5757518007351459773L;


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
			TIntIntHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( int entry ) {
			return TIntIntHashMap.this.containsValue( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Integer ) {
					final int ele = ((Integer) element).intValue();
					if ( !TIntIntHashMap.this.containsValue( ele ) ) {
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
				if ( !TIntIntHashMap.this.containsValue( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TIntCollection collection ) {
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TIntIntHashMap.this.containsValue( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TIntProcedure procedure ) {
			return TIntIntHashMap.this.forEachValue( procedure );
		}


		/** {@inheritDoc} */
		public int getNoEntryValue () {
			return TIntIntHashMap.this.no_entry_value;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TIntIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TIntIterator iterator () {
			return new TIntIntValueHashIterator( TIntIntHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( int entry ) {
			final int[] values = TIntIntHashMap.this._values;
			final int[] set = TIntIntHashMap.this._set;

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
			final int[] values = TIntIntHashMap.this._values;
			final byte[] states = TIntIntHashMap.this._states;

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
			return TIntIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public int[] toArray () {
			return TIntIntHashMap.this.values();
		}


		/** {@inheritDoc} */
		public int[] toArray ( int[] dest ) {
			return TIntIntHashMap.this.values( dest );
		}


		/** {@inheritDoc} */
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

	static final long serialVersionUID = 1L;

	/** the values of the map */
	protected transient int[] _values;


	/**
	 * Creates a new <code>TIntIntHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TIntIntHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TIntIntHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TIntIntHashMap ( int initialCapacity ) {
		super( initialCapacity );
	}


	/**
	 * Creates a new <code>TIntIntHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TIntIntHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
	}


	/**
	 * Creates a new <code>TIntIntHashMap</code> instance with a prime capacity
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
	 *        a <code>int</code> value that represents <tt>null</tt> for the Value
	 *        set.
	 */
	public TIntIntHashMap ( int initialCapacity, float loadFactor,
			int noEntryKey, int noEntryValue ) {
		super( initialCapacity, loadFactor, noEntryKey, noEntryValue );
	}


	/**
	 * Creates a new <code>TIntIntHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param keys
	 *        a <tt>int</tt> array containing the keys for the matching values.
	 * @param values
	 *        a <tt>int</tt> array containing the values.
	 */
	public TIntIntHashMap ( int[] keys, int[] values ) {
		super( Math.max( keys.length, values.length ) );

		final int size = Math.min( keys.length, values.length );
		for ( int i = 0; i < size; i++ ) {
			this.put( keys[ i ], values[ i ] );
		}
	}


	/**
	 * Creates a new <code>TIntIntHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param map
	 *        a <tt>TIntIntMap</tt> that will be duplicated.
	 */
	public TIntIntHashMap ( TIntIntMap map ) {
		super( map.size() );
		if ( map instanceof TIntIntHashMap ) {
			final TIntIntHashMap hashmap = (TIntIntHashMap) map;
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
	public int adjustOrPutValue ( int key, int adjust_amount, int put_amount ) {
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

		final byte previousState = this._states[ index ];
		this._set[ index ] = key;
		this._states[ index ] = FULL;

		if ( isNewMapping ) {
			postInsertHook( previousState == FREE );
		}

		return newValue;
	}


	/** {@inheritDoc} */
	public boolean adjustValue ( int key, int amount ) {
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
	public boolean containsValue ( int val ) {
		final byte[] states = this._states;
		final int[] vals = this._values;

		for ( int i = vals.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && (val == vals[ i ]) ) {
				return true;
			}
		}
		return false;
	}


	private int doPut ( int key, int value, int aIndex ) {
		byte previousState;
		int previous = this.no_entry_value;
		int index = aIndex;
		boolean isNewMapping = true;
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
		if ( !(other instanceof TIntIntMap) ) {
			return false;
		}
		final TIntIntMap that = (TIntIntMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		final int[] values = this._values;
		final byte[] states = this._states;
		final int this_no_entry_value = getNoEntryValue();
		final int that_no_entry_value = that.getNoEntryValue();
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				final int key = this._set[ i ];
				final int that_value = that.get( key );
				final int this_value = values[ i ];
				if ( (this_value != that_value) && (this_value != this_no_entry_value)
						&& (that_value != that_no_entry_value) ) {
					return false;
				}
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean forEachEntry ( TIntIntProcedure procedure ) {
		final byte[] states = this._states;
		final int[] keys = this._set;
		final int[] values = this._values;
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
	public boolean forEachValue ( TIntProcedure procedure ) {
		final byte[] states = this._states;
		final int[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( (states[ i ] == FULL) && !procedure.execute( values[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public int get ( int key ) {
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
	public TIntIntIterator iterator () {
		return new TIntIntHashIterator( this );
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
	public int put ( int key, int value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends Integer, ? extends Integer> map ) {
		ensureCapacity( map.size() );
		// could optimize this for cases when map instanceof THashMap
		for ( final Map.Entry<? extends Integer, ? extends Integer> entry : map
				.entrySet() ) {
			this.put( entry.getKey().intValue(), entry.getValue().intValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TIntIntMap map ) {
		ensureCapacity( map.size() );
		final TIntIntIterator iter = map.iterator();
		while ( iter.hasNext() ) {
			iter.advance();
			this.put( iter.key(), iter.value() );
		}
	}


	/** {@inheritDoc} */
	public int putIfAbsent ( int key, int value ) {
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
	/** {@inheritDoc} */
	@Override
	protected void rehash ( int newCapacity ) {
		final int oldCapacity = this._set.length;
		if ( oldCapacity == newCapacity ) {
			return;
		}

		final int oldKeys[] = this._set;
		final int oldVals[] = this._values;
		final byte oldStates[] = this._states;

		this._set = new int[newCapacity];
		this._values = new int[newCapacity];
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
	public int remove ( int key ) {
		int prev = this.no_entry_value;
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
	public boolean retainEntries ( TIntIntProcedure procedure ) {
		boolean modified = false;
		final byte[] states = this._states;
		final int[] keys = this._set;
		final int[] values = this._values;

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
		this._values = new int[capacity];
		return capacity;
	}


	/** {@inheritDoc} */
	@Override
	public String toString () {
		final StringBuilder buf = new StringBuilder( "{" );
		forEachEntry( new TIntIntProcedure()
		{
			private boolean first = true;


			public boolean execute ( int key, int value ) {
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
	public void transformValues ( TIntFunction function ) {
		final byte[] states = this._states;
		final int[] values = this._values;
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				values[ i ] = function.execute( values[ i ] );
			}
		}
	}


	/** {@inheritDoc} */
	public TIntCollection valueCollection () {
		return new TValueView();
	}


	/** {@inheritDoc} */
	public int[] values () {
		final int[] vals = new int[size()];
		final int[] v = this._values;
		final byte[] states = this._states;

		for ( int i = v.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
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
				out.writeInt( this._values[ i ] );
			}
		}
	}
} // TIntIntHashMap
