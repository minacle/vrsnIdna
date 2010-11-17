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

package gnu.trove.set.hash;

import gnu.trove.TIntCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

/**
 * An open addressed set implementation for int primitives.
 * 
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */

public class TIntHashSet extends TIntHash implements TIntSet, Externalizable {
	class TIntHashIterator extends THashPrimitiveIterator implements TIntIterator {

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

	static final long serialVersionUID = 1L;


	/**
	 * Creates a new <code>TIntHashSet</code> instance with the default capacity
	 * and load factor.
	 */
	public TIntHashSet () {
		super();
	}


	/**
	 * Creates a new <code>TIntHashSet</code> instance that is a copy of the
	 * existing Collection.
	 * 
	 * @param collection
	 *        a <tt>Collection</tt> that will be duplicated.
	 */
	public TIntHashSet ( Collection<? extends Integer> collection ) {
		this( Math.max( collection.size(), DEFAULT_CAPACITY ) );
		addAll( collection );
	}


	/**
	 * Creates a new <code>TIntHashSet</code> instance with a prime capacity equal
	 * to or greater than <tt>initialCapacity</tt> and with the default load
	 * factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public TIntHashSet ( int initialCapacity ) {
		super( initialCapacity );
	}


	/**
	 * Creates a new <code>TIntHash</code> instance with a prime value at or near
	 * the specified capacity and load factor.
	 * 
	 * @param initialCapacity
	 *        used to find a prime capacity for the table.
	 * @param load_factor
	 *        used to calculate the threshold over which rehashing takes place.
	 */
	public TIntHashSet ( int initialCapacity, float load_factor ) {
		super( initialCapacity, load_factor );
	}


	/**
	 * Creates a new <code>TIntHashSet</code> instance with a prime capacity equal
	 * to or greater than <tt>initial_capacity</tt> and with the specified load
	 * factor.
	 * 
	 * @param initial_capacity
	 *        an <code>int</code> value
	 * @param load_factor
	 *        a <code>float</code> value
	 * @param no_entry_value
	 *        a <code>int</code> value that represents null.
	 */
	public TIntHashSet ( int initial_capacity, float load_factor,
			int no_entry_value ) {
		super( initial_capacity, load_factor, no_entry_value );
		// noinspection RedundantCast
		if ( no_entry_value != 0 ) {
			Arrays.fill( this._set, no_entry_value );
		}
	}


	/**
	 * Creates a new <code>TIntHashSet</code> instance containing the elements of
	 * <tt>array</tt>.
	 * 
	 * @param array
	 *        an array of <code>int</code> primitives
	 */
	public TIntHashSet ( int[] array ) {
		this( Math.max( array.length, DEFAULT_CAPACITY ) );
		addAll( array );
	}


	/**
	 * Creates a new <code>TIntHashSet</code> instance that is a copy of the
	 * existing set.
	 * 
	 * @param collection
	 *        a <tt>TIntSet</tt> that will be duplicated.
	 */
	public TIntHashSet ( TIntCollection collection ) {
		this( Math.max( collection.size(), DEFAULT_CAPACITY ) );
		if ( collection instanceof TIntHashSet ) {
			final TIntHashSet hashset = (TIntHashSet) collection;
			this._loadFactor = hashset._loadFactor;
			this.no_entry_value = hashset.no_entry_value;
			// noinspection RedundantCast
			if ( this.no_entry_value != 0 ) {
				Arrays.fill( this._set, this.no_entry_value );
			}
			setUp( (int) Math.ceil( DEFAULT_CAPACITY / this._loadFactor ) );
		}
		addAll( collection );
	}


	/** {@inheritDoc} */
	public boolean add ( int val ) {
		final int index = insertionIndex( val );

		if ( index < 0 ) {
			return false; // already present in set, nothing to add
		}

		final byte previousState = this._states[ index ];
		this._set[ index ] = val;
		this._states[ index ] = FULL;
		postInsertHook( previousState == FREE );

		return true; // yes, we added something
	}


	/** {@inheritDoc} */
	public boolean addAll ( Collection<? extends Integer> collection ) {
		boolean changed = false;
		for ( final Integer element : collection ) {
			final int e = element.intValue();
			if ( add( e ) ) {
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public boolean addAll ( int[] array ) {
		boolean changed = false;
		for ( int i = array.length; i-- > 0; ) {
			if ( add( array[ i ] ) ) {
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	public boolean addAll ( TIntCollection collection ) {
		boolean changed = false;
		final TIntIterator iter = collection.iterator();
		while ( iter.hasNext() ) {
			final int element = iter.next();
			if ( add( element ) ) {
				changed = true;
			}
		}
		return changed;
	}


	/** {@inheritDoc} */
	@Override
	public void clear () {
		super.clear();
		final int[] set = this._set;
		final byte[] states = this._states;

		for ( int i = set.length; i-- > 0; ) {
			set[ i ] = this.no_entry_value;
			states[ i ] = FREE;
		}
	}


	/** {@inheritDoc} */
	public boolean containsAll ( Collection<?> collection ) {
		for ( final Object element : collection ) {
			if ( element instanceof Integer ) {
				final int c = ((Integer) element).intValue();
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
	public boolean containsAll ( int[] array ) {
		for ( int i = array.length; i-- > 0; ) {
			if ( !contains( array[ i ] ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	public boolean containsAll ( TIntCollection collection ) {
		final TIntIterator iter = collection.iterator();
		while ( iter.hasNext() ) {
			final int element = iter.next();
			if ( !contains( element ) ) {
				return false;
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals ( Object other ) {
		if ( !(other instanceof TIntSet) ) {
			return false;
		}
		final TIntSet that = (TIntSet) other;
		if ( that.size() != this.size() ) {
			return false;
		}
		for ( int i = this._states.length; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				if ( !that.contains( this._set[ i ] ) ) {
					return false;
				}
			}
		}
		return true;
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode () {
		int hashcode = 0;
		for ( int i = this._states.length; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				hashcode += HashFunctions.hash( this._set[ i ] );
			}
		}
		return hashcode;
	}


	/** {@inheritDoc} */
	public TIntIterator iterator () {
		return new TIntHashIterator( this );
	}


	/** {@inheritDoc} */
	@Override
	public void readExternal ( ObjectInput in ) throws IOException,
			ClassNotFoundException {

		// VERSION
		final int version = in.readByte();

		// SUPER
		super.readExternal( in );

		// NUMBER OF ENTRIES
		int size = in.readInt();

		if ( version >= 1 ) {
			// LOAD FACTOR
			this._loadFactor = in.readFloat();

			// NO ENTRY VALUE
			this.no_entry_value = in.readInt();
			// noinspection RedundantCast
			if ( this.no_entry_value != 0 ) {
				Arrays.fill( this._set, this.no_entry_value );
			}
		}

		// ENTRIES
		setUp( size );
		while ( size-- > 0 ) {
			final int val = in.readInt();
			add( val );
		}
	}


	/** {@inheritDoc} */
	@Override
	protected void rehash ( int newCapacity ) {
		final int oldCapacity = this._set.length;
		if ( oldCapacity == newCapacity ) {
			return;
		}

		final int oldSet[] = this._set;
		final byte oldStates[] = this._states;

		this._set = new int[newCapacity];
		this._states = new byte[newCapacity];

		for ( int i = oldCapacity; i-- > 0; ) {
			if ( oldStates[ i ] == FULL ) {
				final int o = oldSet[ i ];
				final int index = insertionIndex( o );
				this._set[ index ] = o;
				this._states[ index ] = FULL;
			}
		}
	}


	/** {@inheritDoc} */
	public boolean remove ( int val ) {
		final int index = index( val );
		if ( index >= 0 ) {
			removeAt( index );
			return true;
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
		final int[] set = this._set;
		final byte[] states = this._states;

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
	public int[] toArray () {
		final int[] result = new int[size()];
		final int[] set = this._set;
		final byte[] states = this._states;

		for ( int i = states.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				result[ j++ ] = set[ i ];
			}
		}
		return result;
	}


	/** {@inheritDoc} */
	public int[] toArray ( int[] dest ) {
		final int[] set = this._set;
		final byte[] states = this._states;

		for ( int i = states.length, j = 0; i-- > 0; ) {
			if ( states[ i ] == FULL ) {
				dest[ j++ ] = set[ i ];
			}
		}

		if ( dest.length > this._size ) {
			dest[ this._size ] = this.no_entry_value;
		}
		return dest;
	}


	/** {@inheritDoc} */
	@Override
	public String toString () {
		final StringBuilder buffy = new StringBuilder( this._size * 2 + 2 );
		buffy.append( "{" );
		for ( int i = this._states.length, j = 1; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				buffy.append( this._set[ i ] );
				if ( j++ < this._size ) {
					buffy.append( "," );
				}
			}
		}
		buffy.append( "}" );
		return buffy.toString();
	}


	/** {@inheritDoc} */
	@Override
	public void writeExternal ( ObjectOutput out ) throws IOException {

		// VERSION
		out.writeByte( 1 );

		// SUPER
		super.writeExternal( out );

		// NUMBER OF ENTRIES
		out.writeInt( this._size );

		// LOAD FACTOR -- Added version 1
		out.writeFloat( this._loadFactor );

		// NO ENTRY VALUE -- Added version 1
		out.writeInt( this.no_entry_value );

		// ENTRIES
		for ( int i = this._states.length; i-- > 0; ) {
			if ( this._states[ i ] == FULL ) {
				out.writeInt( this._set[ i ] );
			}
		}
	}
} // TIntHashSet
