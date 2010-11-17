/*
 * DataLookups.c
 *
 *  Created on: Jul 24, 2010
 *      Author: prsrinivasan
 */

#include <stdlib.h>
#include <stdio.h>

#include <xcode_config.h>
#include <glib.h>
#include <unicode_dataloader.h>

guint32 *getDecomposeSeq(guint32 lookupCodePoint) {
	return (guint32 *) g_hash_table_lookup(
			unicodeData->UNICODE_DECOMPOSE_TABLE, &lookupCodePoint);
}

/*
 * wrapper function to support existing library lookup interface
 *  - used by normalize
 * DWORD has been redefined as guint32
 */
int lookup_decompose(DWORD dwChar, const DWORD ** pdwzChar) {
	*pdwzChar = getDecomposeSeq(dwChar);

	if (*pdwzChar == NULL)
		return 0;

	int i = 0;
	while ((*pdwzChar)[i] != 0) {
		i++;
	}

	return i;
}

bool isCompatible(guint32 lookupCodePoint) {
	return (g_hash_table_lookup(unicodeData->UNICODE_COMPATIBILITY_TABLE,
			&lookupCodePoint) != NULL);
}

/*
 * wrapper function to support existing library lookup interface
 *  - used by normalize
 * DWORD has been redefined as guint32
 */
int lookup_compatible(DWORD dwCodepoint) {
	if (isCompatible(dwCodepoint))
		return 1;
	else
		return 0;
}

guint32 getCanonicalClass(guint32 lookupCodePoint) {
	guint32 *ptr = (guint32 *) g_hash_table_lookup(
			unicodeData->UNICODE_CANONICAL_CLASS_TABLE, &lookupCodePoint);

	if (ptr == NULL)
		return 0;

	return *ptr;
}

/*
 * wrapper function to support existing library lookup interface
 * - used by normalize
 * DWORD has been redefined as guint32
 */
int lookup_canonical(DWORD dwCodepoint) {
	return getCanonicalClass(dwCodepoint);
}

guint32 getCompositeCodePoint(guint64 compositePair) {
	guint32 *ptr = (guint32 *) g_hash_table_lookup(
			unicodeData->UNICODE_COMPOSE_TABLE, &compositePair);

	if (ptr == NULL)
		return 0;

	return *ptr;
}

int lookup_composite(QWORD qwPair, DWORD * dwCodepoint) {
	DWORD compositeCodePoint = getCompositeCodePoint(qwPair);
	if (compositeCodePoint != 0) {
		*dwCodepoint = compositeCodePoint;
		return 1;
	}

	return 0;
}

/*
 * This function is used as a comparator in binary searches.
 *
 * The function compares two code point ranges - an input keyNode, and a compareNode, which
 * is the current node of the binary search tree.
 *
 * The input keyNode has identical begin and end code point values - it represents a
 * single code point being searched for. The function determines if the single code
 * point represented by keyNode, falls within the code point range represented by
 * the compareNode.
 *
 *
 */
static int compareCodePointRange(const void *aKeyNode, const void *aCompareNode) {

	CodePointRange *keyNode = ((CodePointRange *) aKeyNode);
	CodePointRange *compareNode = ((CodePointRange *) aCompareNode);

	if ((keyNode->beginCodePoint >= compareNode->beginCodePoint)
			&& (keyNode->beginCodePoint <= compareNode->endCodePoint))
		return 0;

	if (keyNode->beginCodePoint < compareNode->beginCodePoint)
		return -1;

	if (keyNode->beginCodePoint > compareNode->endCodePoint)
		return 1;
}

/*
 * This function does a binary search lookup of an input code point range
 * against a table of code point ranges.
 *
 */
static bool isFoundInTable(CodePointRange inputCodePointRange,
		CodePointRange codePointRangesTable[], int tableSize) {

	CodePointRange *matchedNodePtr;

	matchedNodePtr = (CodePointRange *) bsearch(
			(void *) (&inputCodePointRange), (void *) codePointRangesTable,
			tableSize, sizeof(CodePointRange), compareCodePointRange);

	return (matchedNodePtr != NULL);

}

bool hasDisallowedOrUnassigned(const guint32 codePoints[], int arrSize) {
	int i;
	CodePointRange lookupCodePointRange;

	for (i = 0; i < arrSize; i++) {

		lookupCodePointRange.beginCodePoint = codePoints[i];
		lookupCodePointRange.endCodePoint = codePoints[i];

		if (isFoundInTable(lookupCodePointRange,
				disallowedUnassignedRangesTable, disallowedUnassignedTableSize))
			return true;
	}

	return false;
}

bool isDisallowedOrUnassigned(const guint32 codePoint) {
	guint32 lookupCodePoint[] = { codePoint };

	return hasDisallowedOrUnassigned(lookupCodePoint, 1);
}

bool isCombiningMark(const guint32 codePoint) {
	guint32 *ptr = (guint32 *) g_hash_table_lookup(
			unicodeData->COMBINING_MARK_TABLE, &codePoint);

	if (ptr == NULL)
		return false;

	return true;

}

char *getDerivedJoiningType(const guint32 codePoint) {
	char *ptr = (char *) g_hash_table_lookup(
			unicodeData->UNICODE_DERIVED_JOINING_TYPE_TABLE, &codePoint);

	if (ptr == NULL)
		return "U";

	//derived joining type is a single character
	return ptr;
}

char *getScript(const guint32 codePoint) {
	return (char *) g_hash_table_lookup(unicodeData->UNICODE_SCRIPTS_TABLE,
			&codePoint);
}

char *getBidiClass(const guint32 codePoint) {
	return (char *) g_hash_table_lookup(unicodeData->UNICODE_BIDI_CLASS_TABLE,
			&codePoint);
}

bool isContextO(const guint32 codePoint) {
	return ((char *) g_hash_table_lookup(unicodeData->CONTEXTO_TABLE,
			&codePoint) != NULL);
}

bool isContextJ(const guint32 codePoint) {
	return ((char *) g_hash_table_lookup(unicodeData->CONTEXTJ_TABLE,
			&codePoint) != NULL);
}

bool isContextualCodePoint(guint32 codePoint) {
	return (isContextO(codePoint) || isContextJ(codePoint));
}

bool hasContextualCodePoints(guint32 codePoints[], int cpArrSize) {
	int i;
	bool hasContextualCodePoints = false;

	for (i = 0; i < cpArrSize; i++) {
		hasContextualCodePoints = hasContextualCodePoints
				|| isContextualCodePoint(codePoints[i]);
	}

	return hasContextualCodePoints;
}

bool isRTLBidiProperty(const char *bidiClass) {
	return ((char *) g_hash_table_lookup(
			unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, bidiClass) != NULL);
}

bool isLTRBidiProperty(const char *bidiClass) {
	return ((char *) g_hash_table_lookup(
			unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, bidiClass) != NULL);
}
