/*
 * unicode_dataloader.h
 *
 *  Created on: Jul 12, 2010
 *      Author: prsrinivasan
 */
#include <idna_types.h>
#include <idna_data_constants.h>

#ifndef UNICODE_DATALOADER_H_
#define UNICODE_DATALOADER_H_

#ifndef UNICODE_DATALOADER_IMPL_
#define UNICODE_DATALOADER_EXTERN_ extern
#else
#define UNICODE_DATALOADER_EXTERN_
#endif

//functions
void initUnicodeData();
//globals
UNICODE_DATALOADER_EXTERN_ guint32 disallowedUnassignedTableSize;
UNICODE_DATALOADER_EXTERN_ CodePointRange
		disallowedUnassignedRangesTable[DISALLOWED_UNASSIGNED_MAX_SIZE];
UNICODE_DATALOADER_EXTERN_ UnicodeData *unicodeData;

#endif /* UNICODE_DATALOADER_H_ */
