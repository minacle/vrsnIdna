/*
 * HashUtils.c
 *
 *  Created on: Jul 24, 2010
 *      Author: prsrinivasan
 */

#include <stdlib.h>
#include <string.h>

#include <hash_utils.h>
#include <idna_utils.h>
#include <idna_data_constants.h>

/*
 * hash key comparator for guint32 keys, supplied as function pointer
 * to g_hash_table_new_full
 *
 * v1 - (void *) placeholder pointer for guint32 key1
 * v2 - (void *) placeholder pointer for guint32 key2
 *
 */
gboolean g_uint32_equal(gconstpointer v1, gconstpointer v2) {
	return *((const guint32*) v1) == *((const guint32*) v2);
}

/*
 * hash function for guint32 keys, supplied as function pointer
 * to g_hash_table_new_full
 *
 * v - (void *) placeholder pointer for guint32 key
 *
 */
guint g_uint32_hash(gconstpointer v) {
	return (guint) * (const guint32*) v;
}

/*
 * hash key comparator for guint32 keys, supplied as function pointer
 * to g_hash_table_new_full
 *
 * v1 - (void *) placeholder pointer for guint64 key1
 * v2 - (void *) placeholder pointer for guint64 key2
 *
 */
gboolean g_uint64_equal(gconstpointer v1, gconstpointer v2) {
	return *((const guint64*) v1) == *((const guint64*) v2);
}

/*
 * hash function for guint64 keys, supplied as function pointer
 * to g_hash_table_new_full
 *
 * v - (void *) placeholder pointer for guint64 key
 *
 */
guint g_uint64_hash(gconstpointer v) {
	return (guint) * (const guint64*) v;
}

/*
 * allocate space for an int, and initialize with supplied value
 *
 * intVal - int value to be copied
 *
 */
int *mallocAndSetInt(int intVal) {
	int *ptr = (int *) (malloc(sizeof(int)));
	*ptr = intVal;

	return ptr;
}

/*
 * allocate space for a guint32, and initialize with supplied value
 *
 * guint32Val - guint32 value to be copied
 *
 */
guint32 *mallocAndSetGuint32(guint32 guint32Val) {
	guint32 *ptr = (guint32 *) (malloc(sizeof(guint32)));
	*ptr = guint32Val;

	return ptr;
}

/*
 * allocate space for a guint64, and initialize with supplied value
 *
 * guint64Val - guint64 value to be copied
 *
 */
guint64 *mallocAndSetGuint64(guint64 guint64Val) {
	guint64 *ptr = (guint64 *) (malloc(sizeof(guint64)));
	*ptr = guint64Val;

	return ptr;
}

/*
 * allocate space for a string, and initialize with supplied value
 *
 * strVal - string value to be copied
 *
 */
char *mallocAndSetString(char *strVal) {
	char *ptr = malloc(sizeof(char) * (strlen(strVal)
			+ SIZEOF_STRING_TERMINATOR));
	strcpy(ptr, strVal);

	return ptr;
}

/*
 * free allocated int location
 *
 * ptr - pointer to int location
 *
 */
void freeInt(int *ptr) {
	free(ptr);
	ptr = NULL;
}

/*
 * free allocated guint32 location
 *
 * ptr - pointer to guint32 location
 *
 */
void freeGuint32(guint32 *ptr) {
	free(ptr);
	ptr = NULL;
}

/*
 * free allocated guint64 location
 *
 * ptr - pointer to guint64 location
 *
 */
void freeGuint64(guint64 *ptr) {
	free(ptr);
	ptr = NULL;
}

/*
 * free allocated string
 *
 * ptr - pointer to char
 *
 */
void freeString(char *ptr) {
	free(ptr);
	ptr = NULL;
}

/*
 * allocate space for a guint32 array, and initialize with supplied null-terminated array of
 * string token values
 *
 * guint32ValArr - guint32 array
 *
 */
guint32 *mallocAndSetGuint32Arr(char **tokenArrPtr, int tokenArrSize) {

	guint32 *guint32ArrPtr = (guint32 *) (malloc(sizeof(guint32)
			* (tokenArrSize + 1)));
	int i;
	for (i = 0; i < tokenArrSize; i++) {
		guint32ArrPtr[i] = g_ascii_strtoull(tokenArrPtr[i], NULL, HEX_BASE);
	}

	//terminator value
	guint32ArrPtr[i] = 0;

	return guint32ArrPtr;
}

/*
 * free allocated guint32Array
 *
 * guint32ArrPtr - pointer to guint32 array
 *
 */
void freeGuint32Arr(guint32 *guint32ArrPtr) {
	free(guint32ArrPtr);
	guint32ArrPtr = NULL;
}
