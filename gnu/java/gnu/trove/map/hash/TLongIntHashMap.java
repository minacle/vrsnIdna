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
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
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
 * An open addressed Map implementation for long keys and int values.
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_HashMap.template,v 1.1.2.13 2009/11/16 21:25:13 robeden
 *          Exp $
 */
public class TLongIntHashMap extends TLongIntHash implements TLongIntMap,
		Externalizable {
	/** a view onto the keys of the map. */
	protected class TKeyView implements TLongSet {

		private static final long serialVersionUID = -947097421612939533L;


		/**
		 * Unsupported when operating upon a Key Set view of a TLongIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean add ( long entry ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TLongIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( Collection<? extends Long> collection ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TLongIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( long[] array ) {
			throw new UnsupportedOperationException();
		}


		/**
		 * Unsupported when operating upon a Key Set view of a TLongIntMap
		 * <p/>
		 * {@inheritDoc}
		 */
		public boolean addAll ( TLongCollection collection ) {
			throw new UnsupportedOperationException();
		}


		/** {@inheritDoc} */
		public void clear () {
			TLongIntHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( long entry ) {
			return TLongIntHashMap.this.contains( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Long ) {
					final long ele = ((Long) element).longValue();
					if ( !TLongIntHashMap.this.containsKey( ele ) ) {
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
				if ( !TLongIntHashMap.this.contains( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TLongCollection collection ) {
			final TLongIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TLongIntHashMap.this.containsKey( iter.next() ) ) {
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
			for ( int i = TLongIntHashMap.this._states.length; i-- > 0; ) {
				if ( TLongIntHashMap.this._states[ i ] == FULL ) {
					if ( !that.contains( TLongIntHashMap.this._set[ i ] ) ) {
						return false;
					}
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TLongProcedure procedure ) {
			return TLongIntHashMap.this.forEachKey( procedure );
		}


		/** {@inheritDoc} */
		public long getNoEntryValue () {
			return TLongIntHashMap.this.no_entry_key;
		}


		@Override
		public int hashCode () {
			int hashcode = 0;
			for ( int i = TLongIntHashMap.this._states.length; i-- > 0; ) {
				if ( TLongIntHashMap.this._states[ i ] == FULL ) {
					hashcode += HashFunctions.hash( TLongIntHashMap.this._set[ i ] );
				}
			}
			return hashcode;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TLongIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TLongIterator iterator () {
			return new TLongIntKeyHashIterator( TLongIntHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( long entry ) {
			return TLongIntHashMap.this.no_entry_value != TLongIntHashMap.this
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
			final long[] set = TLongIntHashMap.this._set;
			final byte[] states = TLongIntHashMap.this._states;

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
			return TLongIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public long[] toArray () {
			return TLongIntHashMap.this.keys();
		}


		/** {@inheritDoc} */
		public long[] toArray ( long[] dest ) {
			return TLongIntHashMap.this.keys( dest );
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

	class TLongIntHashIterator extends THashPrimitiveIterator implements
			TLongIntIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param map
		 *        the <tt>TLongIntHashMap</tt> we will be iterating over.
		 */
		TLongIntHashIterator ( TLongIntHashMap map ) {
			super( map );
		}


		/** {@inheritDoc} */
		public void advance () {
			moveToNextIndex();
		}


		/** {@inheritDoc} */
		public long key () {
			return TLongIntHashMap.this._set[ this._index ];
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
				TLongIntHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}
			this._expectedSize--;
		}


		/** {@inheritDoc} */
		public int setValue ( int val ) {
			final int old = value();
			TLongIntHashMap.this._values[ this._index ] = val;
			return old;
		}


		/** {@inheritDoc} */
		public int value () {
			return TLongIntHashMap.this._values[ this._index ];
		}
	}

	class TLongIntKeyHashIterator extends THashPrimitiveIterator implements
			TLongIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TLongIntKeyHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public long next () {
			moveToNextIndex();
			return TLongIntHashMap.this._set[ this._index ];
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
				TLongIntHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	class TLongIntValueHashIterator extends THashPrimitiveIterator implements
			TIntIterator {

		/**
		 * Creates an iterator over the specified map
		 * 
		 * @param hash
		 *        the <tt>TPrimitiveHash</tt> we will be iterating over.
		 */
		TLongIntValueHashIterator ( TPrimitiveHash hash ) {
			super( hash );
		}


		/** {@inheritDoc} */
		public int next () {
			moveToNextIndex();
			return TLongIntHashMap.this._values[ this._index ];
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
				TLongIntHashMap.this.removeAt( this._index );
			}
			finally {
				this._hash.reenableAutoCompaction( false );
			}

			this._expectedSize--;
		}
	}

	/** a view onto the values of the map. */
	protected class TValueView implements TIntCollection {

		private static final long serialVersionUID = 3767381278385666314L;


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
			TLongIntHashMap.this.clear();
		}


		/** {@inheritDoc} */
		public boolean contains ( int entry ) {
			return TLongIntHashMap.this.containsValue( entry );
		}


		/** {@inheritDoc} */
		public boolean containsAll ( Collection<?> collection ) {
			for ( final Object element : collection ) {
				if ( element instanceof Integer ) {
					final int ele = ((Integer) element).intValue();
					if ( !TLongIntHashMap.this.containsValue( ele ) ) {
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
				if ( !TLongIntHashMap.this.containsValue( element ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean containsAll ( TIntCollection collection ) {
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() ) {
				if ( !TLongIntHashMap.this.containsValue( iter.next() ) ) {
					return false;
				}
			}
			return true;
		}


		/** {@inheritDoc} */
		public boolean forEach ( TIntProcedure procedure ) {
			return TLongIntHashMap.this.forEachValue( procedure );
		}


		/** {@inheritDoc} */
		public int getNoEntryValue () {
			return TLongIntHashMap.this.no_entry_value;
		}


		/** {@inheritDoc} */
		public boolean isEmpty () {
			return 0 == TLongIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public TIntIterator iterator () {
			return new TLongIntValueHashIterator( TLongIntHashMap.this );
		}


		/** {@inheritDoc} */
		public boolean remove ( int entry ) {
			final int[] values = TLongIntHashMap.this._values;
			final long[] set = TLongIntHashMap.this._set;

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
			final int[] values = TLongIntHashMap.this._values;
			final byte[] states = TLongIntHashMap.this._states;

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
			return TLongIntHashMap.this._size;
		}


		/** {@inheritDoc} */
		public int[] toArray () {
			return TLongIntHashMap.this.values();
		}


		/** {@inheritDoc} */
		public int[] toArray ( int[] dest ) {
			return TLongIntHashMap.this.values( dest );
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
	 * Creates a new <code>TLongIntHashMap</code> instance with the default
	 * capacity and load factor.
	 */
	public TLongIntHashMap () {
		super();
	}


	/**
	 * Creates a new <code>TLongIntHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TLongIntHashMap ( int initialCapacity ) {
		super( initialCapacity );
	}


	/**
	 * Creates a new <code>TLongIntHashMap</code> instance with a prime capacity
	 * equal to or greater than <tt>initialCapacity</tt> and with the specified
	 * load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public TLongIntHashMap ( int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
	}


	/**
	 * Creates a new <code>TLongIntHashMap</code> instance with a prime capacity
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
	 *        a <code>int</code> value that represents <tt>null</tt> for the Value
	 *        set.
	 */
	public TLongIntHashMap ( int initialCapacity, float loadFactor,
			long noEntryKey, int noEntryValue ) {
		super( initialCapacity, loadFactor, noEntryKey, noEntryValue );
	}


	/**
	 * Creates a new <code>TLongIntHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param keys
	 *        a <tt>long</tt> array containing the keys for the matching values.
	 * @param values
	 *        a <tt>int</tt> array containing the values.
	 */
	public TLongIntHashMap ( long[] keys, int[] values ) {
		super( Math.max( keys.length, values.length ) );

		final int size = Math.min( keys.length, values.length );
		for ( int i = 0; i < size; i++ ) {
			this.put( keys[ i ], values[ i ] );
		}
	}


	/**
	 * Creates a new <code>TLongIntHashMap</code> instance containing all of the
	 * entries in the map passed in.
	 * 
	 * @param map
	 *        a <tt>TLongIntMap</tt> that will be duplicated.
	 */
	public TLongIntHashMap ( TLongIntMap map ) {
		super( map.size() );
		if ( map instanceof TLongIntHashMap ) {
			final TLongIntHashMap hashmap = (TLongIntHashMap) map;
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
	public int adjustOrPutValue ( long key, int adjust_amount, int put_amount ) {
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
	public boolean adjustValue ( long key, int amount ) {
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


	private int doPut ( long key, int value, int aIndex ) {
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
		if ( !(other instanceof TLongIntMap) ) {
			return false;
		}
		final TLongIntMap that = (TLongIntMap) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		final int[] values = this._values;
		final byte[] states = this._states;
		final int this_no_entry_value = getNoEntryValue();
		final int that_no_entry_value = that.getNoEntryValue();
		for ( int i = values.length; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				final long key = this._set[ i ];
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
	public boolean forEachEntry ( TLongIntProcedure procedure ) {
		final byte[] states = this._states;
		final long[] keys = this._set;
		final int[] values = this._values;
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
	public int get ( long key ) {
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
	public TLongIntIterator iterator () {
		return new TLongIntHashIterator( this );
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
		long[] array = aArray;
		final int size = size();
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
	public int put ( long key, int value ) {
		final int index = insertionIndex( key );
		return doPut( key, value, index );
	}


	/** {@inheritDoc} */
	public void putAll ( Map<? extends Long, ? extends Integer> map ) {
		ensureCapacity( map.size() );
		// could optimize this for cases when map instanceof THashMap
		for ( final Map.Entry<? extends Long, ? extends Integer> entry : map
				.entrySet() ) {
			this.put( entry.getKey().longValue(), entry.getValue().intValue() );
		}
	}


	/** {@inheritDoc} */
	public void putAll ( TLongIntMap map ) {
		ensureCapacity( map.size() );
		final TLongIntIterator iter = map.iterator();
		while ( iter.hasNext() ) {
			iter.advance();
			this.put( iter.key(), iter.value() );
		}
	}


	/** {@inheritDoc} */
	public int putIfAbsent ( long key, int value ) {
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

		final long oldKeys[] = this._set;
		final int oldVals[] = this._values;
		final byte oldStates[] = this._states;

		this._set = new long[newCapacity];
		this._values = new int[newCapacity];
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
	public int remove ( long key ) {
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
	public boolean retainEntries ( TLongIntProcedure procedure ) {
		boolean modified = false;
		final byte[] states = this._states;
		final long[] keys = this._set;
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
		forEachEntry( new TLongIntProcedure()
		{
			private boolean first = true;


			public boolean execute ( long key, int value ) {
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
				out.writeLong( this._set[ i ] );
				out.writeInt( this._values[ i ] );
			}
		}
	}
} // TLongIntHashMap
