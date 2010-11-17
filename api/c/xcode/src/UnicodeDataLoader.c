/*
 * UnicodeDataLoader.c
 *
 *  Created on: Jul 14, 2010
 *      Author: prsrinivasan
 */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <limits.h>

/*
 * GLIB is a utility library - part of the GNOME/GTK+ open-source project. More info
 * can be found at http://library.gnome.org/devel/glib/stable/
 *
 */
#include <glib.h>

#include <idna_utils.h>
#include <unicode_datafiles.h>
#include <hash_utils.h>

/*
 * define UNICODE_DATALOADER_IMPL_ to exclude "extern" qualifier from
 * included unicode_dataloader.h
 */
#define UNICODE_DATALOADER_IMPL_
#include <unicode_dataloader.h>

/*
 * initializes unicodeData container.
 * unicodeData is a global var
 *
 */
static void constructUnicodeData() {
	unicodeData = (UnicodeData *) malloc(sizeof(UnicodeData));

	unicodeData->UNICODE_DATA_POINTS_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, NULL);

	unicodeData->UNICODE_GENERAL_CATEGORY_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, freeString);

	unicodeData->UNICODE_CANONICAL_CLASS_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, freeGuint32);

	unicodeData->UNICODE_BIDI_CLASS_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, freeString);

	unicodeData->UNICODE_COMPATIBILITY_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, freeGuint32);

	unicodeData->UNICODE_COMP_EXCLUSIONS_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, NULL);

	unicodeData->UNICODE_DECOMPOSE_TABLE = g_hash_table_new_full(g_uint32_hash,
			g_uint32_equal, freeGuint32, freeGuint32Arr);

	unicodeData->UNICODE_COMPOSE_TABLE = g_hash_table_new_full(g_uint64_hash,
			g_uint64_equal, freeGuint64, freeGuint32);

	unicodeData->UNICODE_SCRIPTS_TABLE = g_hash_table_new_full(g_uint32_hash,
			g_uint32_equal, freeGuint32, freeString);

	unicodeData->UNICODE_DERIVED_JOINING_TYPE_TABLE = g_hash_table_new_full(
			g_uint32_hash, g_uint32_equal, freeGuint32, freeString);

	unicodeData->COMBINING_MARK_TABLE = g_hash_table_new_full(g_uint32_hash,
			g_uint32_equal, freeGuint32, NULL);

	unicodeData->CONTEXTO_TABLE = g_hash_table_new_full(g_uint32_hash,
			g_uint32_equal, freeGuint32, NULL);

	unicodeData->CONTEXTJ_TABLE = g_hash_table_new_full(g_uint32_hash,
			g_uint32_equal, freeGuint32, NULL);

	unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE = g_hash_table_new_full(
			g_str_hash, g_str_equal, freeString, NULL);

	unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE = g_hash_table_new_full(
			g_str_hash, g_str_equal, freeString, NULL);

}

/*
 * read a line from a given file.
 *
 * file - file pointer
 * line - read output buffer
 *
 * return char * - string
 */
static char *readLine(FILE *file, char line[]) {
	return fgets(line, LINE_MAX_SIZE / 2, file);
}

/*
 * process a range of lines from the "UnicodeData.txt" file
 *
 * beginRangeLine - start line
 * endRangeLine - end line
 *
 */
static void loadUnicodeDataRange(char *beginRangeLine, char *endRangeLine) {

	guint32 codePoint;
	guint32 *codePointPtr;
	gchar **tokenArr;
	guint32 canonicalClass;
	guint32 beginCodePoint, endCodePoint;
	char *currToken;

	//decomposition vars
	bool fCompat = false;
	gchar **decomposeTokenArr;
	int decomposeTokenArrLength;

	//composition vars
	//guint32 & guint64 - typedefs in GLIB, guaranteed to be 32 & 64 bits respectively,
	//on all platforms - see "http://library.gnome.org/devel/glib/stable/glib-Basic-Types.html#guint64"
	guint32 first, second;
	guint64 pair;
	guint64 *pairPtr;

	char **beginTokenArr = g_strsplit(beginRangeLine, SEMI_COLON_DELIMITER,
			MAX_TOKENS);
	char **endTokenArr = g_strsplit(endRangeLine, SEMI_COLON_DELIMITER,
			MAX_TOKENS);

	if ((isValidCodePoint(beginTokenArr[0])) && (isValidCodePoint(
			endTokenArr[0]))) {
		beginCodePoint = g_ascii_strtoull(beginTokenArr[0], NULL, HEX_BASE);
		endCodePoint = g_ascii_strtoull(endTokenArr[0], NULL, HEX_BASE);
	} else {

		g_strfreev(beginTokenArr);
		g_strfreev(endTokenArr);

		return;
	}

	//all other tokens, apart from token[0] (codePoint) and token[1] (Range),
	//are assumed to be identical for a range-pair
	tokenArr = beginTokenArr;

	for (codePoint = beginCodePoint; codePoint <= endCodePoint; codePoint++) {
		//process token 0: codepoint
		codePointPtr = mallocAndSetGuint32(codePoint);

		g_hash_table_insert(unicodeData->UNICODE_DATA_POINTS_TABLE,
				codePointPtr, codePointPtr);

		//process token 2: General_Category
		currToken = tokenArr[2];
		if (!(isEmpty(currToken)) && (strlen(currToken) != 0))
			g_hash_table_insert(unicodeData->UNICODE_GENERAL_CATEGORY_TABLE,
					codePointPtr, mallocAndSetString(currToken));

		//process token 3: canonical combining class
		currToken = tokenArr[3];
		if (!(isEmpty(currToken)) && (strlen(currToken) != 0)) {
			canonicalClass = g_ascii_strtoull(currToken, NULL, DECIMAL_BASE);

			//canonical class must be within 0-255
			if (canonicalClass != (canonicalClass & 0xFF))
				//throw exception - bad unicode file
				return;

			g_hash_table_insert(unicodeData->UNICODE_CANONICAL_CLASS_TABLE,
					codePointPtr, mallocAndSetGuint32(canonicalClass));
		}

		//process token 4: bidirectional BIDI_CLASS
		currToken = tokenArr[4];
		if (!(isEmpty(currToken)) && (strlen(currToken) != 0)) {
			g_hash_table_insert(unicodeData->UNICODE_BIDI_CLASS_TABLE,
					codePointPtr, mallocAndSetString(currToken));
		}

		//process token 5: decomposition, Decomposition_Type in angle brackets and
		// Decomposition_Mapping
		currToken = tokenArr[5];
		if (!(isEmpty(currToken)) && (strlen(currToken) != 0)) {

			//check if char has compatible codepoint
			if (currToken[0] == '<') {
				g_hash_table_insert(unicodeData->UNICODE_COMPATIBILITY_TABLE,
						codePointPtr, codePointPtr);
				if (strrchr(currToken, '>') == NULL) {
					//throw exception - invalid unicode file
					return;
				}//--

				currToken = strrchr(currToken, '>');
				fCompat = true;

				//set currToken pointer 2 locations ahead
				currToken++;
				currToken++;
			}

			//decompose
			{
				decomposeTokenArr = g_strsplit(currToken, SPACE_DELIMITER,
						MAX_TOKENS);

				decomposeTokenArrLength = g_strv_length(decomposeTokenArr);

				if ((decomposeTokenArrLength < 1) || ((decomposeTokenArrLength
						> 2) && !fCompat)) {
					//throw exception
					return;
				}

				//store the decomposition sequence as a guint32 array
				g_hash_table_insert(unicodeData->UNICODE_DECOMPOSE_TABLE,
						codePointPtr, mallocAndSetGuint32Arr(decomposeTokenArr,
								decomposeTokenArrLength));

			}//-- decompose

			//store composition pairs
			if (!fCompat && (g_hash_table_lookup(
					unicodeData->UNICODE_COMP_EXCLUSIONS_TABLE, &codePoint)
					== NULL)) {

				if (decomposeTokenArrLength > 1) {
					first = g_ascii_strtoull(decomposeTokenArr[0], NULL,
							HEX_BASE);
					second = g_ascii_strtoull(decomposeTokenArr[1], NULL,
							HEX_BASE);
				} else {
					first = 0;
					second = g_ascii_strtoull(decomposeTokenArr[0], NULL,
							HEX_BASE);
				}

				//pair is unsigned 64-bit  - guint64
				pair = first;
				pair = (pair << 32) | second;

				pairPtr = mallocAndSetGuint64(pair);
				g_hash_table_insert(unicodeData->UNICODE_COMPOSE_TABLE,
						pairPtr, mallocAndSetGuint32(codePoint));
			}//-- store composition pairs

			g_strfreev(decomposeTokenArr);
			fCompat = false;

		}//-- process token 5 end

	}// -- end for

	//cleanup
	g_strfreev(beginTokenArr);
	g_strfreev(endTokenArr);

}

static void loadUnicodeData() {
	//open unicode data file
	char *fileName = getAbsFilePath(UNICODE_DATA_FILE, "UNICODE_DATA_HOME");

	FILE *unicodeDataFile = fopen(fileName, "r");

	if (unicodeDataFile == NULL) {
		perror(fileName);
		free(fileName);

		//throw exception
		return;
	}

	//setup file read buffers
	char line[LINE_MAX_SIZE / 2];
	char nextLine[LINE_MAX_SIZE / 2];

	//line token arrays
	char **lineTokenArr, **nextLineTokenArr = NULL;

	while (readLine(unicodeDataFile, line) != NULL) {

		lineTokenArr = g_strsplit(line, SEMI_COLON_DELIMITER, MAX_TOKENS);

		if (!canBeProcessed(line, lineTokenArr)) {
			g_strfreev(lineTokenArr);
			continue;
		}

		if (isUnicodeRangeBegin(line, lineTokenArr)) {

			if (readLine(unicodeDataFile, nextLine) == NULL) {
				perror(fileName);
				free(fileName);

				g_strfreev(lineTokenArr);

				fclose(unicodeDataFile);

				//throw exception
				return;
			}

			nextLineTokenArr = g_strsplit(nextLine, SEMI_COLON_DELIMITER,
					MAX_TOKENS);

			if (canBeProcessed(nextLine, nextLineTokenArr)
					&& isUnicodeRangeEnd(nextLine, nextLineTokenArr)) {

				loadUnicodeDataRange(line, nextLine);
			}

			g_strfreev(lineTokenArr);
			g_strfreev(nextLineTokenArr);

		} else {
			loadUnicodeDataRange(line, line);
			g_strfreev(lineTokenArr);
		}

	}//end while


	fclose(unicodeDataFile);

	free(fileName);

}

static void loadCompExclusionChars() {
	char *fileName = getAbsFilePath(COMPOSITION_EXCLUSIONS_FILE,
			"UNICODE_DATA_HOME");

	FILE *compExclusionsFile = fopen(fileName, "r");

	if (compExclusionsFile == NULL) {
		perror(fileName);
		free(fileName);

		//throw exception
		return;
	}

	char line[LINE_MAX_SIZE / 2];
	char **tokenArr;

	guint32 codePoint;
	guint32 *codePointPtr;

	while (readLine(compExclusionsFile, line) != NULL) {
		if (isEmpty(line) || line[0] == '#')
			continue;

		tokenArr = g_strsplit(line, TAB_DELIMITER, MAX_TOKENS);

		if (tokenArr != NULL) {
			codePoint = g_ascii_strtoull(tokenArr[0], NULL, HEX_BASE);

			if (codePoint != 0) {
				codePointPtr = mallocAndSetGuint32(codePoint);

				g_hash_table_insert(unicodeData->UNICODE_COMP_EXCLUSIONS_TABLE,
						codePointPtr, codePointPtr);
			}
		}

		g_strfreev(tokenArr);
	}

	fclose(compExclusionsFile);
	free(fileName);
}

static void loadDisallowedAndUnassigned() {
	char *fileName = getAbsFilePath(DISALLOWED_AND_UNASSIGNED_FILE,
			"UNICODE_DATA_HOME");
	FILE *disallowedAndUnassignedFile = fopen(fileName, "r");

	if (disallowedAndUnassignedFile == NULL) {
		perror(fileName);
		//throw exception
		return;
	}

	char line[LINE_MAX_SIZE / 16];
	char **tokenArr;

	CodePointRange tempCodePointRange;

	int i = 0;
	while (readLine(disallowedAndUnassignedFile, line) != NULL) {
		if (isEmpty(line) || line[0] == '#')
			continue;

		tokenArr = g_strsplit(line, HYPHEN_DELIMITER, MAX_TOKENS);

		if (tokenArr != NULL) {
			tempCodePointRange.beginCodePoint = g_ascii_strtoull(tokenArr[0],
					NULL, HEX_BASE);

			if (tokenArr[1] != NULL) {
				tempCodePointRange.endCodePoint = g_ascii_strtoull(tokenArr[1],
						NULL, HEX_BASE);
			} else {
				tempCodePointRange.endCodePoint = g_ascii_strtoull(tokenArr[0],
						NULL, HEX_BASE);
			}

			g_strfreev(tokenArr);
		} else {
			g_strfreev(tokenArr);
			continue;
		}

		disallowedUnassignedRangesTable[i++] = tempCodePointRange;
	}

	disallowedUnassignedTableSize = i - 1;

	fclose(disallowedAndUnassignedFile);
	free(fileName);

}

static void loadPropertyFile(char *relFileName, GHashTable *hashTable) {
	char *fileName = getAbsFilePath(relFileName, "UNICODE_DATA_HOME");
	FILE *loadFile = fopen(fileName, "r");

	if (loadFile == NULL) {
		perror(fileName);
		//throw exception
		return;
	}

	char line[LINE_MAX_SIZE / 2];
	char **tokenArr, **codePointsTokenArr, **propertyTokenArr;
	char *codePointsToken, *propertyToken;
	char *property, *propertyPtr;

	guint32 beginCodePoint, endCodePoint;
	guint32 i;
	guint32 *codePointPtr;

	while (readLine(loadFile, line) != NULL) {
		if (isEmpty(line) || line[0] == '#')
			continue;

		tokenArr = g_strsplit(line, SEMI_COLON_DELIMITER, MAX_TOKENS);

		if (tokenArr != NULL) {
			codePointsToken = tokenArr[0];
			codePointsTokenArr = g_strsplit(g_strstrip(codePointsToken),
					DOT_DOT_DELIMITER, MAX_TOKENS);

			if (g_strv_length(codePointsTokenArr) == 1) {
				beginCodePoint = endCodePoint = g_ascii_strtoull(
						codePointsTokenArr[0], NULL, HEX_BASE);
			} else {
				beginCodePoint = g_ascii_strtoull(codePointsTokenArr[0], NULL,
						HEX_BASE);
				endCodePoint = g_ascii_strtoull(codePointsTokenArr[1], NULL,
						HEX_BASE);
			}

			propertyToken = tokenArr[1];
			propertyTokenArr = g_strsplit_set(g_strstrip(propertyToken),
					TAB_DELIMITER, MAX_TOKENS);
			property = propertyTokenArr[0];

			if ((beginCodePoint != 0) && (endCodePoint != 0)) {
				for (i = beginCodePoint; i <= endCodePoint; i++) {
					codePointPtr = mallocAndSetGuint32(i);
					propertyPtr = mallocAndSetString(property);

					g_hash_table_insert(hashTable, codePointPtr, propertyPtr);
				}
			} else
				continue;

			g_strfreev(tokenArr);
			g_strfreev(codePointsTokenArr);
			g_strfreev(propertyTokenArr);
		}
	}//end-while

	fclose(loadFile);

	free(fileName);
	fileName = NULL;
}

static void loadScripts() {
	loadPropertyFile(SCRIPTS_FILE, unicodeData->UNICODE_SCRIPTS_TABLE);
}

static void loadDerivedJoiningTypeData() {
	loadPropertyFile(DERIVED_JOINING_TYPE_FILE,
			unicodeData->UNICODE_DERIVED_JOINING_TYPE_TABLE);
}

/*
 * populate combining marks table
 */
static void loadCombiningMarks() {
	guint32 lookupCodePoint;
	guint32 *codePointPtr;

	for (lookupCodePoint = UNICODE_MIN; lookupCodePoint < UNICODE_MAX; lookupCodePoint++) {
		char *genCategory = (char *) g_hash_table_lookup(
				unicodeData->UNICODE_GENERAL_CATEGORY_TABLE, &lookupCodePoint);

		if (genCategory != NULL) {
			if ((strcmp(genCategory, GENERAL_CATEGORY_ME) == 0) || (strcmp(
					genCategory, GENERAL_CATEGORY_MC) == 0) || (strcmp(
					genCategory, GENERAL_CATEGORY_MN) == 0)) {

				codePointPtr = mallocAndSetGuint32(lookupCodePoint);

				g_hash_table_insert(unicodeData->COMBINING_MARK_TABLE,
						codePointPtr, codePointPtr);
			}
		}
	}
}

static void loadContextualCodePoints() {

	int i;
	guint32 *codePointPtr;

	/*
	 * CONTEXTO -- Would otherwise have been DISALLOWED
	 */
	guint32 CONTEXTO_ARR_1[] = { 0x00B7, 0x0375, 0x05F3, 0x05F4, 0x030FB,
			G_MAXUINT32 };

	i = 0;
	while (CONTEXTO_ARR_1[i] != G_MAXUINT32) {
		codePointPtr = mallocAndSetGuint32(CONTEXTO_ARR_1[i]);

		g_hash_table_insert(unicodeData->CONTEXTO_TABLE, codePointPtr,
				codePointPtr);
		i++;
	}

	/*
	 * CONTEXTO -- Would otherwise have been PVALID
	 */
	guint32
			CONTEXTO_ARR_2[] = { 0x0660, 0x0661, 0x0662, 0x0663, 0x0664,
					0x0665, 0x0666, 0x0667, 0x0668, 0x0669, 0x06F0, 0x06F1,
					0x06F2, 0x06F3, 0x06F4, 0x06F5, 0x06F6, 0x06F7, 0x06F8,
					0x06F9, G_MAXUINT32 };

	i = 0;
	while (CONTEXTO_ARR_2[i] != G_MAXUINT32) {
		codePointPtr = mallocAndSetGuint32(CONTEXTO_ARR_2[i]);

		g_hash_table_insert(unicodeData->CONTEXTO_TABLE, codePointPtr,
				codePointPtr);
		i++;
	}

	/*
	 * CONTEXTJ
	 */
	guint32 CONTEXTO_ARR_J[] = { 0x200C, 0x200D, G_MAXUINT32 };

	i = 0;
	while (CONTEXTO_ARR_J[i] != G_MAXUINT32) {
		codePointPtr = mallocAndSetGuint32(CONTEXTO_ARR_J[i]);

		g_hash_table_insert(unicodeData->CONTEXTJ_TABLE, codePointPtr,
				codePointPtr);
		i++;
	}
}

static void loadRTLLabelBidiProperties() {

	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "R", "R");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "AL",
			"AL");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "AN",
			"AN");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "EN",
			"EN");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "ES",
			"ES");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "CS",
			"CS");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "ET",
			"ET");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "ON",
			"ON");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "BN",
			"BN");
	g_hash_table_insert(unicodeData->RTL_LABEL_BIDI_PROPERTIES_TABLE, "NSM",
			"NSM");

}

static void loadLTRLabelBidiProperties() {

	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "L", "L");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "EN",
			"EN");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "ES",
			"ES");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "CS",
			"CS");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "ET",
			"ET");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "ON",
			"ON");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "BN",
			"BN");
	g_hash_table_insert(unicodeData->LTR_LABEL_BIDI_PROPERTIES_TABLE, "NSM",
			"NSM");

}

/*
 * Initialization routine to load all data files and lookup data
 */
void initUnicodeData() {

	constructUnicodeData();

	loadCompExclusionChars();
	loadUnicodeData();
	loadDisallowedAndUnassigned();
	loadScripts();
	loadDerivedJoiningTypeData();
	loadCombiningMarks();
	loadContextualCodePoints();
	loadRTLLabelBidiProperties();
	loadLTRLabelBidiProperties();

}
