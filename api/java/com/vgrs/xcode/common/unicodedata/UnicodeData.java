/**************************************************************************
 *                                                                        *
 * The information in this document is proprietary to VeriSign, Inc.      *
 * It may not be used, reproduced or disclosed without the written        *
 * approval of VeriSign.                                                  *
 *                                                                        *
 * VERISIGN PROPRIETARY & CONFIDENTIAL INFORMATION                        *
 *                                                                        *
 *                                                                        *
 * Copyright (c) 2010 VeriSign, Inc.  All rights reserved.                *
 *                                                                        *
 *************************************************************************/

package com.vgrs.xcode.common.unicodedata;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TIntCharHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vgrs.xcode.common.Range;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.common.UnicodeFilter;
import com.vgrs.xcode.idna.Bidi;
import com.vgrs.xcode.idna.Normalize;
import com.vgrs.xcode.util.RangeCreator;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * This class will read and store all the relevant Unicode data files into
 * memory. It also implements the algorithm defined in IDNA2008 Tables document
 * to assign a Unicode category for each code point. This class has all the data
 * needed to implement the IDNA 2008 protocol.
 * 
 * @author hjarada
 * @version 1.0 May 29, 2010
 * @author nchigurupati
 * @version 1.1 June 10, 2010
 *          <p>
 *          Cleaned up the code to use GNU Trove collections, refactored the
 *          common code and comments to all the variables and methods.
 */
public class UnicodeData {

	/**
	 * Collection to hold all the code points specified in "UnicodeData.txt.gz"
	 */
	private static TIntSet UNICODE_DATA_POINTS;

	/**
	 * Collection to hold the contextual (CONTEXTO/CONTEXTJ) code points
	 */
	private static final TIntSet CONTEXTUAL_CODE_POINTS = new TIntHashSet();

	/**
	 * Collection to hold the code point and it's properties read from the
	 * "PropList.txt.gz"
	 */
	private static TIntObjectMap<Set<String>> PROP_LIST_TABLE =
			new TIntObjectHashMap<Set<String>>();

	/**
	 * Collection to hold the code point and it's properties read from the
	 * "DerivedCoreProperties.txt.gz"
	 */
	private static TIntObjectMap<Set<String>> DERIVED_CORE_PROP_TABLE =
			new TIntObjectHashMap<Set<String>>();

	/**
	 * Collection to hold the code point and it's properties read from the
	 * "DerivedCoreProperties.txt.gz"
	 */
	private static TIntObjectMap<Set<String>> DERIVED_NORMALIZATION_PROP_TABLE =
			null;
	/**
	 * Collection to hold the code point and it's properties read from the
	 * "Blocks.txt.gz"
	 */
	private static TIntObjectMap<String> BLOCKS_TABLE =
			new TIntObjectHashMap<String>();

	/**
	 * Collection to hold the code point and it's properties read from the
	 * "HangulSylableType.txt.gz"
	 */
	private static TIntObjectMap<String> HANGUL_SYLLABLE_TYPE_TABLE =
			new TIntObjectHashMap<String>();

	/**
	 * Collection to hold the code point and it's properties read from the
	 * "Scripts.txt.gz"
	 */
	private static final TIntObjectMap<String> SCRIPTS_TABLE =
			new TIntObjectHashMap<String>();

	/**
	 * Collection to hold the code point and it's properties read from the
	 * "DerivedJoiningType.txt.gz"
	 */
	private static final TIntCharMap DERIVED_JOINING_TYPE_TABLE =
			new TIntCharHashMap();

	/**
	 * Collection to hold the code point and it's associated BIDI class specified
	 * in "UnicodeData.txt.gz" field 5.
	 */
	private static TIntObjectMap<String> BIDI_CLASS_TABLE;

	/**
	 * Collection to hold the code point and it's associated general category
	 * specified in "UnicodeData.txt.gz" field 3.
	 */
	private static TIntObjectMap<String> GENERAL_CATEGORY_TABLE;

	/**
	 * The following four collections are used by {@link Normalize} class to
	 * perform NFKC normalization. These variables are initialized by data read
	 * from "UnicodeData.txt.gz" by the {@link UnicodeDataLoader} class.
	 */
	private static TIntSet COMPATIBILITY_TABLE;

	private static TIntIntMap CANONICAL_CLASS_TABLE;

	private static TLongIntMap COMPOSE_TABLE;

	private static TIntObjectMap<int[]> DECOMPOSE_TABLE;

	/**
	 * {@link UnicodeFilter} that contains all the DISALLOWED/UNASSIGNED Unicode
	 * code points.
	 */
	private static UnicodeFilter DISALLOWED_AND_UNASSIGNED_FILTER =
			new UnicodeFilter( "Disallowed/Unassigned" );

	/**
	 * A collection to hold the code points of letters, digits and hyphen.
	 */
	private static final TIntSet LETTERS_DIGITS_HYPHEN = new TIntHashSet();

	/**
	 * Collection to hold the code points of combining mark characters. Code
	 * points that have the general category of {"Mn", "Mc", "Me"} are combining
	 * mark characters.
	 */
	private static final TIntSet COMBINING_MARK = new TIntHashSet();

	/**
	 * This category includes the code points that property values in versions of
	 * Unicode after 5.2 have changed in such a way that the derived property
	 * value would no longer be PVALID or DISALLOWED. If changes are made to
	 * future versions of Unicode so that code points might change property value
	 * from PVALID or DISALLOWED, then this table can be updated and keep special
	 * exception values so that the property values for code points stay stable.
	 * As of IDNA2008 Protocol writing, this set is currently empty.
	 */
	private static final TIntSet BACKWARD_COMPATIBLE_CODE_POINTS =
			new TIntHashSet();

	/**
	 * boolean to indicate that this class has finished initialzing.
	 */
	private static boolean INITIALIZED = false;

	static {
		try {
			init();
		}
		catch ( final XcodeException x ) {
			throw new RuntimeException( x.getMessage() );
		}
	}


	/**
	 * Initialize all the data structures in this class with data read and parsed
	 * from Unicode data files. This method can be invoked explicitly on startup
	 * to ensure that there is no delay at runtime when any of the IDNSDK classes
	 * are invoked to perform IDNA processing.
	 * 
	 * @throws XcodeException
	 */
	static public synchronized void init () throws XcodeException {
		if ( !INITIALIZED ) {
			try {
				buildLookupTables();
			}
			catch ( Throwable ex ) {
				throw XcodeError.IDNSDK_INITIALIZATION_ERROR( ex.getMessage() );
			}
		}
		INITIALIZED = true;
	}


	/**
	 * @param aCodePoint
	 * @return boolean specifying if the given code point is a combining mark.
	 */
	public static boolean isCombiningMark ( final int aCodePoint ) {
		return COMBINING_MARK.contains( aCodePoint );
	}


	/**
	 * @param aCodePoint
	 * @return the script for the given code point, or value of "Unknown" if not
	 *         found
	 */
	public static String getScript ( final int aCodePoint ) {
		final String script = SCRIPTS_TABLE.get( aCodePoint );
		if ( script != null ) {
			return script;
		}
		return UnicodeDataConstants.UNKNOWN_SCRIPT;
	}


	/**
	 * @param aCodePoint
	 * @return the BIDI class for the given code point, or "L" if not found
	 */
	public static String getBidiClass ( final int aCodePoint ) {

		/*
		 * Currently we are reading Bidi_Class value from UCD UnicodeData.txt field
		 * 4. Since UCD UnicodeData.txt does not list Unassigned code points, which
		 * we reject per IDNA2008 registration spec validation, then no need to
		 * extract these unassigned code points their derived properties like
		 * Bidi_Class or Canonical_Combining_Class The Bidi_Class value for any code
		 * point including unassigned code points is list in
		 * http://www.unicode.org/Public/UNIDATA/extracted/DerivedBidiClass.txt
		 */
		final String bidiClass = BIDI_CLASS_TABLE.get( aCodePoint );
		if ( bidiClass == null ) {
			return Bidi.BIDI_L;
		}
		return bidiClass;
	}


	/**
	 * @param aCodePoint
	 * @return the joining type for the given code point or "U" if not found
	 */
	public static char getJoiningType ( final int aCodePoint ) {
		char joiningType = UnicodeDataConstants.UNDEFINED_JOINING_TYPE;

		if ( DERIVED_JOINING_TYPE_TABLE.containsKey( aCodePoint ) ) {
			joiningType = DERIVED_JOINING_TYPE_TABLE.get( aCodePoint );
		}
		return joiningType;
	}


	/**
	 * Assert that none of the code points are either UNASSIGNED or DISALLOWED.
	 * 
	 * @param aCodePoints
	 *        an int[] array of Unicode code points.
	 * @throws XcodeException
	 *         if any of the Unicode code points are either UNASSIGNED or
	 *         DISALLOWED
	 */
	public static void assertNoDisallowedOrUnassignedCodePoints (
			final int[] aCodePoints ) throws XcodeException {
		DISALLOWED_AND_UNASSIGNED_FILTER.assertNone( aCodePoints );
	}


	public static boolean isDisallowedOrUnassignedCodePoint ( int aCodePoint ) {
		return DISALLOWED_AND_UNASSIGNED_FILTER.has( aCodePoint );
	}


	/**
	 * @param aCodePoints
	 *        the Unicode code points
	 * @return boolean indicating if the given set of code points are in
	 *         Normalization Form C (NFC [Unicode-UAX15])
	 * @throws XcodeException
	 */
	public static boolean isNormalized ( final int[] aCodePoints ) {
		try {
			Normalize.execute( aCodePoints );
		}
		catch ( final Exception ex ) {
			return false;
		}
		return true;
	}


	/**
	 * Assert that the given set of code points are in NFC normalized form.
	 * 
	 * @param aCodePoints
	 *        the Unicode code points
	 * @throws XcodeException
	 *         if the given set of code points are not in Normalization Form C
	 *         (NFC [Unicode-UAX15])
	 */
	public static void assertNormalized ( final int[] aCodePoints )
			throws XcodeException {
		final int[] normalizedCodePoints = Normalize.execute( aCodePoints );
		if ( !Arrays.equals( normalizedCodePoints, aCodePoints ) ) {
			throw XcodeError.NORMALIZE_NOT_IN_NFC_FORM();
		}
	}


	/**
	 * This method performs a quick check to see if the given code points can be
	 * normalized or not.
	 * <p>
	 * If the value of a code point has a derived core property of "NFC_QC_N" then
	 * the code points cannot be normalized.
	 * <p>
	 * If the value of a code point has a derived core property of "NFC_QC_M" then
	 * the code points have to be normalized.
	 * <p>
	 * Otherwise, the code points are already in NFC normalized form.
	 * 
	 * @param aCodePoints
	 *        the Unicode code points
	 * @return int indicating if the given set of code points are in Normalization
	 *         Form C (NFC [Unicode-UAX15]) as follows:
	 *         <p>
	 *         0 - means given set contains code points with NFC_QC with value “N”
	 *         <p>
	 *         1 - means given set contains code points with NFC_QC with value “Y"
	 *         <p>
	 *         2 - means given set contains code points with NFC_QC with value “M"
	 * @throws XcodeException
	 */
	public static int isNormalizationNeeded ( final int[] aCodePoints ) {

		/*
		 * Per http://www.unicode.org/Public/UNIDATA/DerivedNormalizationProps.txt
		 * NFC_QC with value “N” means “Characters that cannot ever occur in the
		 * respective normalization form.” NFC_QC with value “M” means “Characters
		 * that may occur in the respective normalization, depending on the
		 * context.” NFC_QC with value “Y” “All other characters. This is the
		 * default value for Quick_Check properties”
		 */
		for ( final int codePoint : aCodePoints ) {
			final Set<String> value =
					DERIVED_NORMALIZATION_PROP_TABLE.get( codePoint );

			/*
			 * First check: return 0 If we found a code point in the given set that
			 * cannot occur in that NFC Normalization Form. the checking is done via
			 * NFC_Quick_Check (NFC_QC) with value "N" from UCD
			 * DerivedNormalizationProps.txt
			 */

			if ( value != null && value.contains( UnicodeDataConstants.NFC_QC_N ) ) {
				return UnicodeDataConstants.CANNOT_BE_NORMALIZED;
			}

			/*
			 * Second check: If we found a code point in the given set that have
			 * derived normalization property NFC_QC with value "M", then Return 2 to
			 * apply normalization algorithm "NFKC" since Normalization Forms NFC and
			 * NFKC are identical, NFKC form is always the same as the NFC form and
			 * the NFKD form is always the same as the NFD form.
			 */

			if ( value != null && value.contains( UnicodeDataConstants.NFC_QC_M ) ) {
				return UnicodeDataConstants.NORMALIZATION_NEEDED;
			}
		}
		return UnicodeDataConstants.NORMALIZED;
	}


	/**
	 * The canonical combining class value of the code point or 0 if not found
	 * 
	 * @param aCodePoint
	 * @return the canonical combining class value of the code point or 0 if not
	 *         found
	 */
	public static int getCanonicalClass ( final int aCodePoint ) {
		if ( CANONICAL_CLASS_TABLE.containsKey( aCodePoint ) ) {
			return CANONICAL_CLASS_TABLE.get( aCodePoint );
		}
		else {
			return 0;
		}
	}


	/**
	 * Checks to see if there are any contextual (CONTEXTO/CONTEXTJ) code points
	 * present
	 * 
	 * @param aCodePoints
	 * @return boolean indicating if there are any contextual (CONTEXTO/CONTEXTJ)
	 *         code points present
	 */
	public static boolean hasContextualCodePoints ( int[] aCodePoints ) {
		for ( final int codePoint : aCodePoints ) {
			if ( CONTEXTUAL_CODE_POINTS.contains( codePoint ) ) {
				return true;
			}
		}
		return false;
	}


	public static UnicodeCodePointCategory getCodePointDerivedProperty (
			final int aCodePoint ) {

		if ( aCodePoint == 0x200C || aCodePoint == 0x200D ) {
			return UnicodeCodePointCategory.CONTEXTJ;
		}
		else if ( CONTEXTUAL_CODE_POINTS.contains( aCodePoint ) ) {
			return UnicodeCodePointCategory.CONTEXTO;
		}
		else if ( DISALLOWED_AND_UNASSIGNED_FILTER.has( aCodePoint ) ) {
			return UnicodeCodePointCategory.DISALLOWED;
		}
		else {
			return UnicodeCodePointCategory.PVALID;
		}

	}


	/**
	 * The algorithm implemented in this method is defined in IDNA2008 Tables
	 * document. A number of checks are applied sequentially to determine the
	 * category of a Unicode code point.
	 * 
	 * @param aCodePoint
	 *        the code point for which the Unicode category (PVALID, DISALLOWED,
	 *        UNASSIGNED, CONTEXTJ or CONTEXTO) that needs to be determined
	 * @return {@link UnicodeCodePointCategory} of the code point
	 */
	private static UnicodeCodePointCategory internalGetCodePointDerivedProperty (
			final int aCodePoint ) {

		try {
			if ( inExceptions( aCodePoint ) ) {
				return exceptions( aCodePoint );
			}
			else if ( inBackwardCompatible( aCodePoint ) ) {
				return backwardCompatible( aCodePoint );
			}
			else if ( inUnassigned( aCodePoint ) ) {
				return UnicodeCodePointCategory.UNASSIGNED;
			}
			else if ( inLDH( aCodePoint ) ) {
				return UnicodeCodePointCategory.PVALID;
			}
			else if ( inJoinControl( aCodePoint ) ) {
				return UnicodeCodePointCategory.CONTEXTJ;
			}
			else if ( inUnstable( aCodePoint ) ) {
				return UnicodeCodePointCategory.DISALLOWED;
			}
			else if ( inIgnorableProperties( aCodePoint ) ) {
				return UnicodeCodePointCategory.DISALLOWED;
			}
			else if ( inIgnorableBlocks( aCodePoint ) ) {
				return UnicodeCodePointCategory.DISALLOWED;
			}
			else if ( inOldHangulJamo( aCodePoint ) ) {
				return UnicodeCodePointCategory.DISALLOWED;
			}
			else if ( inLetterDigits( aCodePoint ) ) {
				return UnicodeCodePointCategory.PVALID;
			}
			else {
				return UnicodeCodePointCategory.DISALLOWED;
			}

		}
		catch ( final Exception ex ) {
			return null;
		}

	}


	/**
	 *This category includes the code points that property values in versions of
	 * Unicode after 5.2 have changed in such a way that the derived property
	 * value would no longer be PVALID or DISALLOWED. If changes are made to
	 * future versions of Unicode so that code points might change property value
	 * from PVALID or DISALLOWED, then this table can be updated and keep special
	 * exception values so that the property values for code points stay stable.
	 * As of IDNA2008 Protocol writing, this set is currently empty. By default,
	 * currently this returns DISALLOWED.
	 * 
	 * @param aCodePoint
	 * @return {@link UnicodeCodePointCategory} of the code point
	 */
	private static UnicodeCodePointCategory backwardCompatible ( int aCodePoint ) {
		return UnicodeCodePointCategory.DISALLOWED;
	}


	/**
	 * Returns the category assigned to the code point as defined in
	 * {@link ExceptionCodePoints} class.
	 * 
	 * @param aCodePoint
	 * @return {@link UnicodeCodePointCategory} of the code point
	 */
	private static UnicodeCodePointCategory exceptions ( int aCodePoint ) {
		final UnicodeCodePointCategory category =
				ExceptionCodePoints.getCategory( aCodePoint );
		return category;

	}


	/**
	 * Specifies if the code points is defined {@link ExceptionCodePoints} class
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is present in the
	 *         {@link ExceptionCodePoints} tables.
	 */
	private static boolean inExceptions ( int aCodePoint ) {
		if ( ExceptionCodePoints.getCategory( aCodePoint ) != null ) {
			return true;
		}
		return false;
	}


	/**
	 * Specifies if the code point is present in the Backwards compatible list of
	 * code points.
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is present in the Backwards
	 *         compatible list of code points.
	 */
	private static boolean inBackwardCompatible ( int aCodePoint ) {
		return BACKWARD_COMPATIBLE_CODE_POINTS.contains( aCodePoint );
	}


	/**
	 * This category consists of code points in the Unicode character set that are
	 * not (yet) assigned. It should be noted that Unicode distinguishes between
	 * 'unassigned code points' and 'unassigned characters'. The unassigned code
	 * points are all but (Cn - Noncharacters), while the unassigned *characters*
	 * are all but (Cn + Cs). It is defined as follows:
	 * <p>
	 * General_Category(cp) is in {Cn} and Noncharacter_Code_Point(cp) = False
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is unassigned.
	 */
	private static boolean inUnassigned ( int aCodePoint ) {

		boolean nonCharacterCodePoint = false;

		final Set<String> set = PROP_LIST_TABLE.get( aCodePoint );
		if ( set != null
				&& set.contains( UnicodeDataConstants.NONCHARACTER_CODE_POINT ) ) {
			nonCharacterCodePoint = true;
		}

		if ( !UNICODE_DATA_POINTS.contains( aCodePoint ) && !nonCharacterCodePoint ) {
			return true;
		}
		else {
			return false;
		}
	}


	/**
	 * This category is used in the second step to preserve the traditional
	 * "hostname" (LDH) characters ('-', 0-9 and a-z). In general, these code
	 * points are suitable for use for IDN.
	 * <p>
	 * cp is in {002D, 0030..0039, 0061..007A}
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is a letter, digit or hyphen
	 *         character
	 */
	private static boolean inLDH ( int aCodePoint ) {
		return LETTERS_DIGITS_HYPHEN.contains( aCodePoint );
	}


	/**
	 * Load the letters, digits and hyphen into TIntSet
	 */
	private static void loadLDH () {
		LETTERS_DIGITS_HYPHEN.add( 0x002D );
		for ( int i = 0x0030; i <= 0x0039; i++ ) {
			LETTERS_DIGITS_HYPHEN.add( i );
		}
		for ( int i = 0x0061; i <= 0x007A; i++ ) {
			LETTERS_DIGITS_HYPHEN.add( i );
		}
	}


	/**
	 * Specifies if the given code point is a CONTEXTJ (Join Control) character.
	 * Currently there are two characters in this category (0x200C, 0x200D)
	 * 
	 * @param aCodePoint
	 * @return
	 */
	private static boolean inJoinControl ( int aCodePoint ) {
		final Set<String> set = PROP_LIST_TABLE.get( aCodePoint );
		if ( set != null && set.contains( "Join_Control" ) ) {
			return true;
		}
		return false;
	}


	/**
	 * The Unicode Character Database supplies properties that allow
	 * implementations to quickly determine whether a string x is in a particular
	 * Normalization Form—for example, isNFC(x). This is, in general, many times
	 * faster than normalizing and then comparing. For each Normalization Form,
	 * the properties provide three possible values for each Unicode code point,
	 * as shown here: Values Abbr Description NO N The code point cannot occur in
	 * that Normalization Form. YES Y The code point is a starter and can occur in
	 * the Normalization Form. In addition, for NFKC and NFC, the character may
	 * compose with a following character, but it never composes with a previous
	 * character. MAYBE M The code point can occur, subject to canonical ordering,
	 * but with constraints. In particular, the text may not be in the specified
	 * Normalization Form depending on the context in which the character occurs.
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the given code point is unstable.
	 */
	private static boolean inUnstable ( int aCodePoint ) {

		/*
		 * NFKC_Quick_Check (NFKC_QC): with value "N" - Characters that cannot ever
		 * occur in the respective normalization form NFKC_Casefold (NFKC_CF):
		 * Mapping from a character to the string produced by casefolding it,
		 * removing any Default_Ignorable_Code_Point=T characters, and converting to
		 * NFKC form. if ((value.contains("NFKC_QC_N")) ||
		 * (value.contains("NFKC_CF"))) return true;
		 */

		final Set<String> value = DERIVED_NORMALIZATION_PROP_TABLE.get( aCodePoint );
		if ( value != null
				&& (value.contains( UnicodeDataConstants.NFKC_QC_N ) || value
						.contains( UnicodeDataConstants.NFKC_CF )) ) {
			return true;
		}

		return false;
	}


	/**
	 * This category is used to group code points that are not recommended for use
	 * in identifiers. In general, these code points are not suitable for use for
	 * IDN.
	 * <p>
	 * Default_Ignorable_Code_Point(cp) = True or White_Space(cp) = True or
	 * Noncharacter_Code_Point(cp) = True
	 * 
	 * @param aCodePoint
	 * @return boolean specifying if the code point belongs one of the ignorable
	 *         properties.
	 */
	private static boolean inIgnorableProperties ( int aCodePoint ) {

		Set<String> value = null;

		value = DERIVED_CORE_PROP_TABLE.get( aCodePoint );
		if ( value != null
				&& value.contains( UnicodeDataConstants.DEFAULT_IGNORABLE_CODE_POINT ) ) {
			return true;
		}

		value = PROP_LIST_TABLE.get( aCodePoint );
		if ( value != null
				&& (value.contains( UnicodeDataConstants.WHITE_SPACE ) || value
						.contains( UnicodeDataConstants.NONCHARACTER_CODE_POINT )) ) {
			return true;
		}

		return false;
	}


	/**
	 * This category is used to identifying code points that are not useful in
	 * mnemonics or that are otherwise impractical for IDN use. In general, these
	 * code points are not suitable for use for IDN.
	 * <p>
	 * Block(cp) is in {Combining Diacritical Marks for Symbols, Musical Symbols,
	 * Ancient Greek Musical Notation}
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is one of the ignorable blocks
	 *         as mentioned above
	 */
	private static boolean inIgnorableBlocks ( int aCodePoint ) {
		return BLOCKS_TABLE.containsKey( aCodePoint );
	}


	/**
	 * This category consists of all conjoining Hangul Jamo (Leading Jamo, Vowel
	 * Jamo, and Trailing Jamo). Elimination of conjoining Hangul Jamos from the
	 * set of PVALID characters results in restricting the set of Korean PVALID
	 * characters just to preformed, modern Hangul syllable characters. Old Hangul
	 * syllables, which must be spelled with sequences of conjoining Hangul Jamos,
	 * are not PVALID for IDNs.
	 * <p>
	 * Hangul_Syllable_Type(cp) is in {L, V, T}
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is a Hangul Syllable type with
	 *         {L,V or T} property
	 */
	private static boolean inOldHangulJamo ( int aCodePoint ) {
		return HANGUL_SYLLABLE_TYPE_TABLE.containsKey( aCodePoint );
	}


	/**
	 * These rules identifies characters commonly used in mnemonics and often
	 * informally described as "language characters". In general, only code points
	 * assigned to this category are suitable for use in IDN.
	 * <p>
	 * General_Category(cp) is in {Ll, Lu, Lo, Nd, Lm, Mn, Mc}
	 * <p>
	 * The categories used in this rule are:
	 * <ul>
	 * <li>Ll - Lowercase_Letter
	 * <li>Lu - Uppercase_Letter
	 * <li>Lo - Other_Letter
	 * <li>Nd - Decimal_Number
	 * <li>Lm - Modifier_Letter
	 * <li>Mn - Nonspacing_Mark o
	 * <li>Mc - Spacing_Mark
	 * </ul>
	 * 
	 * @param aCodePoint
	 * @return boolean indicating if the code point is a letter or a digit
	 */
	private static boolean inLetterDigits ( int aCodePoint ) {
		final String propertyValue = GENERAL_CATEGORY_TABLE.get( aCodePoint );
		if ( propertyValue != null ) {
			if ( propertyValue.equals( "Ll" ) || propertyValue.equals( "Lu" )
					|| propertyValue.equals( "Lo" ) || propertyValue.equals( "Nd" )
					|| propertyValue.equals( "Lm" ) || propertyValue.equals( "Mn" )
					|| propertyValue.equals( "Mc" ) ) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Loads all the necessary Unicode data files required for implementing the
	 * IDNA2008 Protocol
	 */
	static private void buildLookupTables () throws XcodeException {
		initializeUnicodeDataVariables();
		loadLDH();
		loadPropListData();
		loadDerivedNormalizationPropertiesData();
		loadBlocksData();
		loadHangulSyllableTypeData();
		loadDerivedJoiningTypeData();
		loadScriptsData();
		loadDerivedNormalizationPropertiesData();
		loadDerivedCorePropertiesData();

		collapseUnicodeData();

	} // END buildLookupTables()


	/**
	 * Data read from "data/unicode/UnicodeData.txt.gz" file by
	 * {@link UnicodeDataLoader} class is stored in the variables below. This data
	 * is then used by other classes to implement the IDNA2008 Protocol.
	 */
	private static void initializeUnicodeDataVariables () {
		UNICODE_DATA_POINTS = UnicodeDataLoader.getUnicodeDataPoints();
		BIDI_CLASS_TABLE = UnicodeDataLoader.getBidiClassTable();
		CANONICAL_CLASS_TABLE = UnicodeDataLoader.getCanonicalClassTable();
		COMPATIBILITY_TABLE = UnicodeDataLoader.getCompatibilityTable();
		COMPOSE_TABLE = UnicodeDataLoader.getComposeTable();
		DECOMPOSE_TABLE = UnicodeDataLoader.getDecomposeTable();
		GENERAL_CATEGORY_TABLE = UnicodeDataLoader.getGeneralCategoryTable();
	}


	/**
	 * After all the Unicode data files have been read, the data is spread across
	 * multiple data structures. This method then collapses all the DISALLOWED and
	 * UNASSIGNED code points into one UnicodeFilter for efficient storage and
	 * faster performance. All the contextual characters are stored into a
	 * {@link TIntSet} for fast lookup. Similarly, the combining mark characters
	 * are stored into a {@link TIntSet} for fast lookup.
	 * 
	 * @throws XcodeException
	 */
	private static void collapseUnicodeData () throws XcodeException {
		// List to hold DISALLOWED/UNASSIGNED code points
		final TIntList disallowedAndUnassignedPoints = new TIntArrayList();
		UnicodeCodePointCategory category = null;
		String property = null;
		for ( int unicodeCodePoint = Unicode.MIN; unicodeCodePoint <= Unicode.MAX; unicodeCodePoint++ ) {
			category = internalGetCodePointDerivedProperty( unicodeCodePoint );
			switch (category) {
			case PVALID:
				break;
			case CONTEXTO:
				CONTEXTUAL_CODE_POINTS.add( unicodeCodePoint );
				break;
			case CONTEXTJ:
				CONTEXTUAL_CODE_POINTS.add( unicodeCodePoint );
				break;
			default:
				disallowedAndUnassignedPoints.add( unicodeCodePoint );
			}
			property = GENERAL_CATEGORY_TABLE.get( unicodeCodePoint );
			if ( property != null ) {
				if ( property.equals( UnicodeDataConstants.GENERAL_CATEGORY_ME )
						|| property.equals( UnicodeDataConstants.GENERAL_CATEGORY_MC )
						|| property.equals( UnicodeDataConstants.GENERAL_CATEGORY_MN ) ) {
					COMBINING_MARK.add( unicodeCodePoint );
				}
			}

		}
		// Store all the DISALLOWED/UNASSIGNED code points into one Unicode Filter.
		final Collection<Range> prohibitedRanges =
				RangeCreator.createRanges( disallowedAndUnassignedPoints );
		DISALLOWED_AND_UNASSIGNED_FILTER.add( prohibitedRanges );

		// nullify unneeded data strucutres to conserve memory
		UnicodeDataLoader.nullifyUnicodeDataPointsTable();
		UNICODE_DATA_POINTS = null;
		BLOCKS_TABLE = null;
		HANGUL_SYLLABLE_TYPE_TABLE = null;
		DERIVED_CORE_PROP_TABLE = null;
		PROP_LIST_TABLE = null;
	}


	/**
	 * Utility method to load the data in "data/unicode/HangulSyllableType.txt.gz"
	 * file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadHangulSyllableTypeData () throws XcodeException {

		// Only load code points with property of (L,V or T)
		final Set<String> hangulJamoType = new HashSet<String>();
		hangulJamoType.add( "L" );
		hangulJamoType.add( "V" );
		hangulJamoType.add( "T" );

		UnicodeDataFileUtil.loadCodePointAndCategoryMap(
				UnicodeDataConstants.HANGUL_SYLLABLE_TYPE_DATA,
				HANGUL_SYLLABLE_TYPE_TABLE,
				UnicodeDataConstants.SEMI_COLON_TAB_DELIMITERS, hangulJamoType );
	}


	/**
	 * Utility method to load data from "data/unicode/Blocks.txt.gz" file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadBlocksData () throws XcodeException {

		// Only load blocks data for blocks in {Combining Diacritical Marks for
		// Symbols, Musical Symbols, Ancient Greek Musical Notation}

		final Set<String> blocksToload = new HashSet<String>();
		blocksToload
				.add( UnicodeDataConstants.COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS );
		blocksToload.add( UnicodeDataConstants.MUSICAL_SYMBOLS );
		blocksToload.add( UnicodeDataConstants.ANCIENT_GREEK_MUSICAL_NOTATION );

		UnicodeDataFileUtil.loadCodePointAndCategoryMap(
				UnicodeDataConstants.BLOCKS_DATA, BLOCKS_TABLE,
				UnicodeDataConstants.SEMI_COLON_DELIMITER, blocksToload );

	}


	/**
	 * Utility method to load data from "data/unicode/Scripts.txt.gz" file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadScriptsData () throws XcodeException {
		UnicodeDataFileUtil.loadCodePointAndCategoryMap(
				UnicodeDataConstants.SCRIPTS_DATA, SCRIPTS_TABLE,
				UnicodeDataConstants.SEMI_COLON_TAB_DELIMITERS, null );
	}


	/**
	 * Utility method to load data from "data/unicode/DerivedJoiningType.txt.gz"
	 * file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadDerivedJoiningTypeData () throws XcodeException {
		UnicodeDataFileUtil.loadCodePointAndCategoryCharMap(
				UnicodeDataConstants.DERIVED_JOINING_TYPE_DATA,
				DERIVED_JOINING_TYPE_TABLE,
				UnicodeDataConstants.SEMI_COLON_TAB_DELIMITERS, null );

	}


	/**
	 * Initialize with data read from
	 * "data/unicode/DerivedNormalizationProps.txt.gz" file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadDerivedNormalizationPropertiesData ()
			throws XcodeException {
		DERIVED_NORMALIZATION_PROP_TABLE =
				DerivedNormalizationProperties.getDerivedNormalizationPropTable();
	}


	/**
	 * Utility method to load data from
	 * "data/unicode/DerivedCoreProperties.txt.gz" file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadDerivedCorePropertiesData () throws XcodeException {
		UnicodeDataFileUtil
				.loadCodePointAndCategorySet(
						UnicodeDataConstants.DERIVED_CORE_PROPERTIES_DATA,
						DERIVED_CORE_PROP_TABLE,
						UnicodeDataConstants.SEMI_COLON_TAB_DELIMITERS );
	}


	/**
	 * Utility method to load data from "data/unicode/PropList.txt.gz" file.
	 * 
	 * @throws XcodeException
	 */
	private static void loadPropListData () throws XcodeException {
		UnicodeDataFileUtil.loadCodePointAndCategorySet(
				UnicodeDataConstants.PROP_LIST_DATA, PROP_LIST_TABLE,
				UnicodeDataConstants.SEMI_COLON_TAB_DELIMITERS );
	}


	/**
	 * Returns the canonical combining class value for the given code point
	 * 
	 * @param aCodePoint
	 * @return the canonical combining class value
	 */
	public static final int getCanonicalCombiningClass ( int aCodePoint ) {
		return CANONICAL_CLASS_TABLE.get( aCodePoint );
	}


	/**
	 * Returns the compatibilityTable
	 * 
	 * @return the compatibilityTable
	 */
	public static final TIntSet getCompatibilityTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return COMPATIBILITY_TABLE;
	}


	/**
	 * Returns the canonicalClassTable
	 * 
	 * @return the canonicalClassTable
	 */
	public static final TIntIntMap getCanonicalClassTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return CANONICAL_CLASS_TABLE;
	}


	/**
	 * Returns the composeTable
	 * 
	 * @return the composeTable
	 */
	public static final TLongIntMap getComposeTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return COMPOSE_TABLE;
	}


	/**
	 * Returns the decomposeTable
	 * 
	 * @return the decomposeTable
	 */
	public static final TIntObjectMap<int[]> getDecomposeTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return DECOMPOSE_TABLE;
	}


	/**
	 * @param aCodePoint
	 * @return the general category of the code point as specified in
	 *         UnicodeData.txt
	 */
	public static final String getGeneralCategory ( final int aCodePoint ) {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return GENERAL_CATEGORY_TABLE.get( aCodePoint );
	}


	/**
	 * Returns the contextualCodePoints
	 * 
	 * @return the contextualCodePoints
	 */
	public static TIntSet getContextualCodePointsTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return new TIntHashSet( CONTEXTUAL_CODE_POINTS );
	}


	/**
	 * Returns the scriptsTable
	 * 
	 * @return the scriptsTable
	 */
	public static TIntObjectMap<String> getScriptsTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		final TIntObjectMap<String> scriptsTable =
				new TIntObjectHashMap<String>( SCRIPTS_TABLE );
		return scriptsTable;
	}


	/**
	 * Returns the derivedJoiningTypeTable
	 * 
	 * @return the derivedJoiningTypeTable
	 */
	public static TIntCharMap getDerivedJoiningTypeTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		final TIntCharMap derivedJoiningType =
				new TIntCharHashMap( DERIVED_JOINING_TYPE_TABLE );
		return derivedJoiningType;
	}


	/**
	 * Returns the Bidi Class Table
	 * 
	 * @return the Bidi Class Table
	 */
	public static TIntObjectMap<String> getBidiClassTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		final TIntObjectMap<String> bidiClassTable =
				new TIntObjectHashMap<String>( BIDI_CLASS_TABLE );
		return bidiClassTable;
	}


	/**
	 * Returns the General Category table
	 * 
	 * @return the General Category table
	 */
	public static TIntObjectMap<String> getGeneralCategoryTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		final TIntObjectMap<String> generalCategoryTable =
				new TIntObjectHashMap<String>( GENERAL_CATEGORY_TABLE );
		return generalCategoryTable;
	}


	/**
	 * Returns the combiningMark table
	 * 
	 * @return the combiningMark table
	 */
	public static TIntSet getCombiningMarkTable () {
		if ( !INITIALIZED ) {
			throw new IllegalStateException(
					"UnicodeData has not initialized properly." );
		}
		return new TIntHashSet( COMBINING_MARK );
	}
}
