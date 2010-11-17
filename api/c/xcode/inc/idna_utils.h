/*
 * idna_utils.h
 *
 *  Created on: Jul 12, 2010
 *      Author: prsrinivasan
 */

#ifndef IDNA_UTILS_H_
#define IDNA_UTILS_H_

#define SIZEOF_FILE_SEP				sizeof(char)
#define SIZEOF_STRING_TERMINATOR	sizeof(char)

#include <idna_types.h>
#include <xcode_config.h>

void printDWORDArr(DWORD *dwPtr, int dwLength);

void printWORDArr(guint16 *dwPtr, int dwLength);

/*
 * function tests if a string contains only whitespace
 */
bool isEmpty(char *str);

bool canBeProcessed(char *line, char **lineTokenArr);

bool hasNoTokens(char **lineTokenArr);

bool isUnicodeRangeBegin(char *line, char **lineTokenArr);

bool isUnicodeRangeEnd(char *line, char **lineTokenArr);

bool isValidCodePoint(char *token);

/*
 * function tokenizes line, returning a NULL-terminated array of strings, minus
 * empty tokens
 */
char **tokenizeLine(char *line, char *delimiter);

void freeTokenArr(char **tokenArrPtr);

int *tokenizeUnicodeArr(guint32 unicodeArr[], int unicodeArrSize,
		guint32 delimiters[], int delimArrSize);

/*
 * function returns an absolute file path rooted at the supplied environment variable
 */
char *getAbsFilePath(char *relFilePath, char *envVariable);

bool hasRestrictedHyphens(const DWORD codePoints[], int arrSize);

bool areArraysEqual(guint32 *arrA, int arrASize, guint32 *arrB, int arrBSize);

void logErr(int xcode_err);

#endif /* IDNA_UTILS_H_ */
