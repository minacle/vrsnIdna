/*
 * hash_utils.h
 *
 *  Created on: Jul 30, 2010
 *      Author: prsrinivasan
 */

#ifndef HASH_UTILS_H_
#define HASH_UTILS_H_

#include <glib.h>

gboolean g_uint32_equal(gconstpointer v1, gconstpointer v2);
guint g_uint32_hash(gconstpointer v);

gboolean g_uint64_equal(gconstpointer v1, gconstpointer v2);
guint g_uint64_hash(gconstpointer v);

int *mallocAndSetInt(int intVal);

guint32 *mallocAndSetGuint32(guint32 guint32Val);

guint64 *mallocAndSetGuint64(guint64 guint64Val);

char *mallocAndSetString(char *strVal);

void freeInt(int *ptr);

void freeGuint32(guint32 *ptr);

void freeGuint64(guint64 *ptr);

void freeString(char *ptr);

guint32 *mallocAndSetGuint32Arr(char **tokenArrPtr, int tokenArrSize);

void freeGuint32Arr(guint32 *guint32ArrPtr);

#endif /* HASH_UTILS_H_ */
