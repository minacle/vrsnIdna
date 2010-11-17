/*
 * idna_protocol.h
 *
 *  Created on: July 30, 2010
 *      Author: prsrinivasan
 */

#ifndef IDNA_PROTOCOL_H_
#define IDNA_PROTOCOL_H_

#define		IDNA_PROTOCOL_FAIL				0+IDNA_PROTOCOL_SPECIFIC
#define		HAS_DISALLOWED_OR_UNASSIGNED	1+IDNA_PROTOCOL_SPECIFIC
#define		HAS_RESTRICTED_HYPHENS			2+IDNA_PROTOCOL_SPECIFIC
#define		IS_COMBINING_MARK				3+IDNA_PROTOCOL_SPECIFIC

int idna2008Protocol(guint32 codePoints[], int arrSize);

int applyIdna2008BidiRules(guint32 domain[], int domainSize);

#endif /* IDNA_PROTOCOL_H_ */
