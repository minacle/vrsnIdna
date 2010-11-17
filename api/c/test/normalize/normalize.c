/*
 * idna.c
 *
 *  Created on: Aug 31, 2010
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
#include <data_lookups.h>

#include <unicode_dataloader.h>
#include <normalize.h>

#include <init_lib.h>

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

void normalize(char *codePointsToken, guint32 *codePointsArr,
        int *codePointsArrSizePtr, const char *codePointsArrName,
        guint32 *normalizedArr, int *normalizedArrSizePtr,
        const char *normalizedArrName) {

    char **codePointsTokenArr;

    int retCode, i;

    codePointsTokenArr = g_strsplit(codePointsToken, SPACE_DELIMITER,
            MAX_TOKENS);

    *codePointsArrSizePtr = g_strv_length(codePointsTokenArr);

    for (i = 0; i < *codePointsArrSizePtr; i++) {
        codePointsArr[i] = g_ascii_strtoull(codePointsTokenArr[i], NULL,
                HEX_BASE);
    }

    g_strfreev(codePointsTokenArr);

    //normalize
    retCode = Xcode_normalizeString(codePointsArr, *codePointsArrSizePtr,
            normalizedArr, normalizedArrSizePtr);

}

void normalizeLine(char *line) {
    char **tokenArr;
    char *codePointsToken;

    guint32 c1[64], c2[64], c3[64], c4[64], c5[64];
    int c1Size = 0, c2Size = 0, c3Size = 0, c4Size = 0, c5Size = 0;

    guint32 nc1[1024], nc2[1024], nc3[1024], nc4[1024], nc5[1024];
    int nc1Size = 0, nc2Size = 0, nc3Size = 0, nc4Size = 0, nc5Size = 0;

    tokenArr = g_strsplit(line, SEMI_COLON_DELIMITER, MAX_TOKENS);

    /*******************************************************************/
    //c1
    /*******************************************************************/
    codePointsToken = tokenArr[0];
    normalize(codePointsToken, c1, &c1Size, "c1", nc1, &nc1Size, "nc1");

    /*******************************************************************/
    //c2
    /*******************************************************************/
    codePointsToken = tokenArr[1];
    normalize(codePointsToken, c2, &c2Size, "c2", nc2, &nc2Size, "nc2");

    /*******************************************************************/
    //c3
    /*******************************************************************/
    codePointsToken = tokenArr[2];
    normalize(codePointsToken, c3, &c3Size, "c3", nc3, &nc3Size, "nc3");

    /*******************************************************************/
    //c4
    /*******************************************************************/
    codePointsToken = tokenArr[3];
    normalize(codePointsToken, c4, &c4Size, "c4", nc4, &nc4Size, "nc4");

    /*******************************************************************/
    //c5
    /*******************************************************************/
    codePointsToken = tokenArr[4];
    normalize(codePointsToken, c5, &c5Size, "c5", nc5, &nc5Size, "nc5");

    //free tokenArr
    g_strfreev(tokenArr);

    //fprintf(stderr, "c4Size is : %d and nc1Size is : %d\n", c4Size, nc1Size);

    if (!areArraysEqual(c4, c4Size, nc1, nc1Size))
        fprintf(stderr,
            "********* FAILURE ********* c4 and nc1 are NOT equal\n");

    if (!areArraysEqual(c4, c4Size, nc2, nc2Size))
        fprintf(stderr,
            "********* FAILURE ********* c4 and nc2 are NOT equal\n");

    if (!areArraysEqual(c4, c4Size, nc3, nc3Size))
        fprintf(stderr,
            "********* FAILURE ********* c4 and nc3 are NOT equal\n");

    if (!areArraysEqual(c4, c4Size, nc4, nc4Size))
        fprintf(stderr,
            "********* FAILURE ********* c4 and nc4 are NOT equal\n");

    if (!areArraysEqual(c4, c4Size, nc5, nc5Size))
        fprintf(stderr,
            "********* FAILURE ********* c4 and nc5 are NOT equal\n");

    nc1Size = nc2Size = nc3Size = nc4Size = nc5Size = 0;

}

void normalizeFile(char *fileName) {

    FILE *normalizationTestFile = fopen(fileName, "r");

    if (normalizationTestFile == NULL) {
        perror(fileName);
        free(fileName);

        //throw exception
        return;
    }

    char line[LINE_MAX_SIZE / 2];

    int lineCount = 0;

    while (readLine(normalizationTestFile, line) != NULL) {
        g_strstrip(line);

/*
        fprintf(stderr, "---------------- line number %i ----------------\n",
                ++lineCount);
*/

        if (isEmpty(line) || line[0] == '#')
            continue;

        normalizeLine(line);
    }

    fclose(normalizationTestFile);
/*
    free(fileName);
*/

}

int main(int argc, char *argv[]) {

    if (argc < 2) {
        printf("usage: %s [-c c1;c2;c3;c4;c5] file=<file>\n", argv[0]);
        return 1;
    }

    char cmdStr[1024];
    bool isFileInput = false;

    memset(cmdStr, 0, sizeof (cmdStr));

    //better command option processing required

    if ((strcmp(argv[1], "-c") == 0) && (argc == 3)) {

        if (strncmp("file=", argv[2], strlen("file=")) == 0) {
            printf("usage: %s [-c c1;c2;c3;c4;c5] file=<file>\n", argv[0]);
            exit(1);
        }

        strncpy(cmdStr, argv[2], 1024);
        isFileInput = false;
       
    } else if (strncmp("file=", argv[1], strlen("file=")) == 0) {
        strcpy(cmdStr, (char *) (strchr(argv[1], '=') + 1));
        isFileInput = true;
    } else {
        printf("usage: %s [-c c1;c2;c3;c4;c5] file=<file>\n", argv[0]);
        exit(1);
    }


    initLib();

    if (isFileInput) {
        normalizeFile(cmdStr);
    } else {
        normalizeLine(cmdStr);
    }

    printf("DONE\n");

    return 0;
}
