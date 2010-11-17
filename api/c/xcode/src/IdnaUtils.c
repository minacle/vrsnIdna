/*
 * IdnaUtils.c
 *
 *  Created on: Jul 12, 2010
 *      Author: prsrinivasan
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#include <glib.h>

#include <idna_data_constants.h>
#include <idna_utils.h>

void printDWORDArr(DWORD *dwPtr, int dwLength) {
	int i = 0;

	if (dwLength == -1) {
		while (dwPtr[i] != 0) {
			fprintf(stderr, "%x ", dwPtr[i]);
			i++;
		}
	} else {
		for (i = 0; i < dwLength; i++) {
			fprintf(stderr, "%x ", dwPtr[i]);
		}
	}

	fprintf(stderr, "\n");
}

void printWORDArr(guint16 *dwPtr, int dwLength) {
	int i = 0;

	if (dwLength == -1) {
		while (dwPtr[i] != 0) {
			fprintf(stderr, "%x ", dwPtr[i]);
			i++;
		}
	} else {
		for (i = 0; i < dwLength; i++) {
			fprintf(stderr, "%x ", dwPtr[i]);
		}
	}

	fprintf(stderr, "\n");
}

/*
 * function tests if a string contains only whitespace
 *
 * char *str	- imput string
 * returns bool
 *
 */
bool isEmpty(char *str) {
	int i;
	int c;

	for (i = 0; i < strlen(str); i++) {
		c = str[i];

		if (!isspace(c))
			return false;
	}

	return true;
}

bool canBeProcessed(char *line, char **lineTokenArr) {
	return !(isEmpty(line) || (line[0] == '#') || (strlen(line) == 0)
			|| hasNoTokens(lineTokenArr));

}

bool hasNoTokens(char **lineTokenArr) {
	return (lineTokenArr == NULL);
}

bool isUnicodeRangeBegin(char *line, char **lineTokenArr) {

	if (!(isEmpty(line)) && (strlen(lineTokenArr[1]) != 0)) {
		if (g_str_has_suffix(lineTokenArr[1], UNICODE_RANGE_BEGIN_SUFFIX)) {
			return true;
		} else {
			return false;
		}
	}

	return false;
}

bool isUnicodeRangeEnd(char *line, char **lineTokenArr) {

	if (!(isEmpty(line)) && (strlen(line) != 0)) {
		if (g_str_has_suffix(lineTokenArr[1], UNICODE_RANGE_END_SUFFIX)) {
			return true;
		} else {
			return false;
		}
	}

	return false;
}

bool isValidCodePoint(char *token) {
	return (!(isEmpty(token)) && (strlen(token) != 0) && (g_ascii_strtoull(
			token, NULL, HEX_BASE) != 0));
}

/*
 * function tokenizes a line on the given delimiter string. It accommodates tokens beyond
 * the default, by dynamically allocating additional chunks of DEFAULT_MALLOC_SIZE.
 *
 *
 *  char *line        	- input line string
 *  char *delimiter   	- delimiter string
 *  returns char **		- NULL-terminated array of tokens, excluding empty tokens
 *
 */
char **tokenizeLine(char *line, char *delimiter) {

	char **ptr = NULL;
	char *token = NULL;
	char *lastPtr = NULL;
	char *copyOfLine;
	int tokenCount = 0;
	bool parsedAllTokens = false;

	while (!parsedAllTokens) {
		if (tokenCount == 0) {

			copyOfLine = strdup(line);

			token = strtok_r(copyOfLine, delimiter, &lastPtr);

			if (token == NULL) {
				free(copyOfLine);
				return NULL;
			}

			ptr = (char **) malloc(sizeof(char *) * DEFAULT_MALLOC_SIZE);
		} else {
			token = strtok_r(NULL, delimiter, &lastPtr);

			if (token == NULL)
				parsedAllTokens = true;
		}

		++tokenCount;

		if (((tokenCount - 1) % DEFAULT_MALLOC_SIZE == 0) && (tokenCount != 1))
			ptr = (char **) realloc(ptr, sizeof(char *) * ((tokenCount - 1)
					+ DEFAULT_MALLOC_SIZE));

		ptr[tokenCount - 1] = token;

		free(copyOfLine);
		copyOfLine = NULL;
	}

	return ptr;
}

void freeTokenArr(char **tokenArrPtr) {
	int i = 0;
	while (tokenArrPtr[i] != NULL) {
		free(tokenArrPtr[i]);
		i++;
	}

	//free(tokenArrPtr[i]);
	free(tokenArrPtr);

}

bool isDelimiter(guint32 key, guint32 delimiters[], int delimArrSize) {
	int i;

	for (i = 0; i < delimArrSize; i++) {
		if (key == delimiters[i])
			return true;
	}

	return false;
}

int *tokenizeUnicodeArr(guint32 unicodeArr[], int unicodeArrSize,
		guint32 delimiters[], int delimArrSize) {

	int i;
	int j = 0;
	int *delimiterPositionArr = (int *) malloc(sizeof(int) * unicodeArrSize);

	bool delimFound = false;
	for (i = 0; i < unicodeArrSize; i++) {

		if (isDelimiter(unicodeArr[i], delimiters, delimArrSize)) {
			delimiterPositionArr[j] = i;
			j++;
			delimFound = true;
		}
	}

	if (!delimFound) {
		delimiterPositionArr[j] = unicodeArrSize;
		delimiterPositionArr[++j] = -1;
	} else {
		delimiterPositionArr[j] = -1;
	}

	return delimiterPositionArr;
}

/*
 * function returns an absolute file path rooted at the supplied environment variable
 *
 * char *relFilePath 	- relative file path
 * char *envVariable 	- environment variable holding root dir path
 * returns char *		- absolute file path
 *
 */
char *getAbsFilePath(char *relFilePath, char *envVariable) {
	const char *basePath = getenv(envVariable);

	if (basePath == NULL) {
		fprintf(stderr, "The environment variable %s is undefined\n",
				envVariable);
		return NULL;
	}

	char *resultStr = (char *) malloc(strlen(basePath) + SIZEOF_FILE_SEP
			+ strlen(relFilePath) + SIZEOF_STRING_TERMINATOR);

	strcpy(resultStr, basePath);
	strcat(resultStr, "/");
	strcat(resultStr, relFilePath);

	return resultStr;
}

/*
 * function returns String representation of a guint32 val
 *
 * long guint32Val		- input guint32Val value
 * returns char *		- String representing the guint32Val val
 *
 */
char *guint32ToStr(guint32 guint32Val) {
	char *buf = malloc(sizeof(char) * 16);

	sprintf(buf, "%u", guint32Val);
	return buf;
}

bool hasRestrictedHyphens(const DWORD codePoints[], int arrSize) {
	if ((codePoints[0] == HYPHEN) || (codePoints[arrSize - 1] == HYPHEN))
		return true;

	if (arrSize > 3)
		if ((codePoints[2] == HYPHEN) && (codePoints[3] == HYPHEN))
			return true;

	return false;
}

bool areArraysEqual(guint32 *arrA, int arrASize, guint32 *arrB, int arrBSize) {

	if (arrASize != arrBSize)
		return false;

	int i;

	for (i = 0; i < arrASize; i++) {
		if (arrA[i] != arrB[i])
			return false;
	}

	return true;

}

bool areEqualUTF16Arrays(UTF16CHAR *arrA, int arrASize, UTF16CHAR *arrB,
		int arrBSize) {

	if (arrASize != arrBSize)
		return false;

	int i;

	for (i = 0; i < arrASize; i++) {
		if (arrA[i] != arrB[i])
			return false;
	}

	return true;

}

void logErr(int xcode_err) {

	fprintf(stderr, "Error Code : %d\n", xcode_err);

}
