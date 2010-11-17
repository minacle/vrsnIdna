/*
 * data_lookups.h
 *
 *  Created on: Aug 5, 2010
 *      Author: prsrinivasan
 */

#ifndef DATA_LOOKUPS_H_
#define DATA_LOOKUPS_H_

#include <idna_types.h>

guint32 *getDecomposeSeq(guint32 lookupCodePoint);

int lookup_decompose(DWORD dwChar, const DWORD ** pdwzChar);

bool isCompatible(guint32 lookupCodePoint);

int lookup_compatible(DWORD dwCodepoint);

guint32 getCanonicalClass(guint32 lookupCodePoint);

int lookup_canonical(DWORD dwCodepoint);

guint32 getCompositeCodePoint(guint64 compositePair);

int lookup_composite(QWORD qwPair, DWORD * dwCodepoint);

int compareCodePointRange(const void *aKeyNode, const void *aCompareNode);

bool isFoundInTable(CodePointRange inputCodePointRange,
		CodePointRange codePointRangesTable[], int tableSize);

bool hasDisallowedOrUnassigned(const guint32 codePoints[], int arrSize);

bool isDisallowedOrUnassigned(const guint32 codePoint);

bool isCombiningMark(const guint32 codePoint);

char *getDerivedJoiningType(const guint32 codePoint);

char *getScript(const guint32 codePoint);

char *getBidiClass(const guint32 codePoint);

bool isContextO(const guint32 codePoint);

bool isContextJ(const guint32 codePoint);

bool isContextualCodePoint(guint32 codePoint);

bool hasContextualCodePoints(guint32 codePoints[], int cpArrSize);

bool isRTLBidiProperty(const char *bidiClass);

bool isLTRBidiProperty(const char *bidiClass);

#endif /* DATA_LOOKUPS_H_ */
