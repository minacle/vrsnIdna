/*
 * InitLib.c
 *
 *  Created on: Oct 8, 2010
 *      Author: prsrinivasan
 */

#include <pthread.h>
#include <stdio.h>
#include <unicode_dataloader.h>
#include <error_handler.h>

static pthread_once_t ctxInitialized = PTHREAD_ONCE_INIT;

/*
 * Initialization routine to load all data files, lookup data and error table
 */
void initLibContext() {	
	initUnicodeData();
        initErrorCodes();
}

void *initLib() {
	pthread_once(&ctxInitialized, initLibContext);
}
