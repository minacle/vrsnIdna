
/********************************************************************************
 *                              Unicode                                         *
 ********************************************************************************/

/*
Purpose: This tool converts between Unicode data and Utf-16 using the surrogate arithmetic specified in
the UTF-16 RFC.  (see the References section in Appendices)

Usage:   unicode (encode|decode) <file>

Input type:   Utf-16 for encode, Unicode for decode
Output type:  Unicode for encode, Utf-16 for decode
 */

#include "xcode.h"
#include "../utility/utility.h"
#include <stdio.h>

#include <bidi_rules.h>

#ifdef WIN32
#ifdef _DEBUG
#pragma comment( lib, "../../../../../lib/win32/xcodelibdbg.lib" )
#else
#pragma comment( lib, "../../../../../lib/win32/xcodelib.lib" )
#endif
#endif

int main(int argc, char* argv[]) {
    FILE * fpin;
    /*FILE * fpout;*/
    char szIn[1024];
    DWORD dwInput[1024];
    DWORD dwOutput[1024];
    UTF16CHAR uData[1024];
    int iInputSize = 0;
    int counter = 0;

    char *inputFile;


    /* Arg check */
    if (argc < 2) {
        printf("usage: %s file=<file>\n", argv[0]);
        return 1;
    }

    inputFile = (char *) (strchr(argv[1], '=') + 1);

    /* Get file */
    fpin = fopen(inputFile, "r");
    if (fpin == NULL) {
        printf("Cannot open %s\n", inputFile);
        return 1;
    }

    initLib();

    while (!feof(fpin)) {
        counter++;
        
        memset(szIn, 0, sizeof (szIn));
        memset(dwInput, 0, sizeof (dwInput));
        memset(uData, 0, sizeof (uData));
        memset(dwOutput, 0, sizeof (dwOutput));

        fgets(szIn, sizeof (szIn), fpin);
        if (szIn[0] == ' ' || szIn[0] == '#' || strlen(szIn) < 2) continue;

        /* Clip off \n */
        if (szIn[strlen(szIn) - 1] == '\n'){
            szIn[strlen(szIn) - 1] = 0;
        }        

        Read32BitLine(szIn, dwInput, &iInputSize);        

        if (applyIdna2008BidiRules(dwInput, iInputSize) != 0) {
            printf("Failed BIDI compliance: Line %d \n", counter);
        }else
        {
            printf("Passed BIDI compliance: Line %d \n", counter);
        }

        printf("\n");

    }

    fclose(fpin);
#ifdef WIN32
    getchar();
#endif
    return 0;

}

