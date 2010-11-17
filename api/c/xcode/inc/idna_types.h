/*
 * idna_types.h
 *
 *  Created on: Jul 12, 2010
 *      Author: prsrinivasan
 */
#include <glib.h>

#ifndef IDNA_TYPES_H_
#define IDNA_TYPES_H_

/*
 * GLIB is a utility library - part of the GNOME/GTK+ open-source project. More info
 * can be found at http://library.gnome.org/devel/glib/stable/
 */

typedef enum {
	false, true
} bool;

typedef struct {
	GHashTable *UNICODE_DATA_POINTS_TABLE;
	GHashTable *UNICODE_GENERAL_CATEGORY_TABLE;
	GHashTable *UNICODE_CANONICAL_CLASS_TABLE;
	GHashTable *UNICODE_BIDI_CLASS_TABLE;
	GHashTable *UNICODE_COMPATIBILITY_TABLE;
	GHashTable *UNICODE_COMP_EXCLUSIONS_TABLE;
	GHashTable *UNICODE_DECOMPOSE_TABLE;
	GHashTable *UNICODE_COMPOSE_TABLE;
	GHashTable *UNICODE_SCRIPTS_TABLE;
	GHashTable *UNICODE_DERIVED_JOINING_TYPE_TABLE;
	GHashTable *COMBINING_MARK_TABLE;
	GHashTable *CONTEXTO_TABLE;
	GHashTable *CONTEXTJ_TABLE;
	GHashTable *RTL_LABEL_BIDI_PROPERTIES_TABLE;
	GHashTable *LTR_LABEL_BIDI_PROPERTIES_TABLE;
        GHashTable *IDNA_ERROR_CODES_TABLE;
} UnicodeData;

//not used currently
typedef struct {
	GHashTable *hashTable;
} Set;

typedef Set IntegerSet;

typedef struct {
	guint32 beginCodePoint;
	guint32 endCodePoint;
} CodePointRange;

#endif /* IDNA_TYPES_H_ */
