/*
 * contextual_rules.h
 *
 *  Created on: Aug 12, 2010
 *      Author: prsrinivasan
 */

#ifndef CONTEXTUAL_RULES_H_
#define CONTEXTUAL_RULES_H_

#include <xcode.h>
#include <idna_types.h>

/*
 * rule type
 */
typedef enum {
	ZERO_WIDTH_NON_JOINER_RULE,
	ZERO_WIDTH_JOINER_RULE,
	MIDDLE_DOT_RULE,
	GREEK_LOWER_NUMERAL_SIGN_RULE,
	HEBREW_PUNCTUATION_GERESH_RULE,
	HEBREW_PUNCTUATION_GERSHAYIM_RULE,
	KATAKANA_MIDDLE_DOT_RULE,
	ARABIC_INDIC_DIGITS_RULE,
	EXT_ARABIC_INDIC_DIGITS_RULE,
	LAST_RULE
} rule;

/*
 * <Error Codes>
 */
#define		XCODE_CTXRULE_FAIL									0+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_NOT_APPLICABLE_FAIL					1+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_ZERO_WIDTH_NON_JOINER_FAIL			2+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_ZERO_WIDTH_JOINER_FAIL				3+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_MIDDLE_DOT_FAIL						4+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_GREEK_LOWER_NUMERAL_FAIL				5+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_HEBREW_PUNCUTATION_GERESH_FAIL		6+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_HEBREW_PUNCUTATION_GERSHAYIM_FAIL		7+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_KATAKANA_MIDDLE_DOT_FAIL				8+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_ARABIC_INDIC_DIGITS_FAIL				9+CONTEXTUAL_RULE_SPECIFIC
#define		XCODE_CTXRULE_EXT_ARABIC_INDIC_DIGITS_FAIL			10+CONTEXTUAL_RULE_SPECIFIC

/*
 * <Error Messages>
 */
#define		XCODE_CTXRULE_FAIL_MSG									"Contextual Rules evaluation failed"
#define		XCODE_CTXRULE_NOT_APPLICABLE_FAIL_MSG					"Contextual Rule not applicable to code point"
#define		XCODE_CTXRULE_ZERO_WIDTH_NON_JOINER_FAIL_MSG			"Contextual Rule ZERO_WIDTH_NON_JOINER failed : Code point:U+200C --> If Canonical_Combining_Class(Before(cp)) .eq.  Virama Then True; If RegExpMatch((Joining_Type:{L,D})(Joining_Type:T)*\u200C(Joining_Type:T)*(Joining_Type:{R,D})) Then True"
#define		XCODE_CTXRULE_ZERO_WIDTH_JOINER_FAIL_MSG				"Contextual Rule ZERO_WIDTH_JOINER failed : Code point:U+200D --> If Canonical_Combining_Class(Before(cp)) .eq.  Virama Then True;"
#define		XCODE_CTXRULE_MIDDLE_DOT_FAIL_MSG						"Contextual Rule MIDDLE_DOT failed : Code point:U+00B7 --> If Before(cp) .eq.  U+006C And After(cp) .eq.  U+006C Then True;"
#define		XCODE_CTXRULE_GREEK_LOWER_NUMERAL_FAIL_MSG				"Contextual Rule GREEK_LOWER_NUMERAL failed : Code point:U+0375 --> If Script(After(cp)) .eq.  Greek Then True;"
#define		XCODE_CTXRULE_HEBREW_PUNCUTATION_GERESH_FAIL_MSG		"Contextual Rule HEBREW_PUNCUTATION_GERESH failed : Code point:U+05F3 --> If Script(Before(cp)) .eq.  Hebrew Then True;"
#define		XCODE_CTXRULE_HEBREW_PUNCUTATION_GERSHAYIM_FAIL_MSG		"Contextual Rule HEBREW_PUNCUTATION_GERSHAYIM failed : Code point: U+05F4 --> If Script(Before(cp)) .eq.  Hebrew Then True;"
#define		XCODE_CTXRULE_KATAKANA_MIDDLE_DOT_FAIL_MSG				"Contextual Rule KATAKANA_MIDDLE_DOT failed : Code point: U+30FB  --> If Script(cp) .in. {Hiragana, Katakana, Han} Then True;"
#define		XCODE_CTXRULE_ARABIC_INDIC_DIGITS_FAIL_MSG				"Contextual Rule ARABIC_INDIC_DIGITS failed : Code point:0660..0669 --> For All Characters: If cp .in. 06F0..06F9 Then False; End For;"
#define		XCODE_CTXRULE_EXT_ARABIC_INDIC_DIGITS_FAIL_MSG			"Contextual Rule EXT_ARABIC_INDIC_DIGITS failed :  Code point:06F0..06F9 --> For All Characters: If cp .in. 0660..0669 Then False; End For;"

/*
 * codepoint constants for contextual rules
 */
#define		ZERO_WIDTH_NON_JOINER			0x200C
#define		ZERO_WIDTH_JOINER				0x200D
#define		MIDDLE_DOT						0x00B7
#define		GREEK_LOWER_NUMERAL_SIGN		0x0375
#define		HEBREW_PUNCTUATION_GERESH		0x05F3
#define		HEBREW_PUNCTUATION_GERSHAYIM	0x05F4
#define		KATAKANA_MIDDLE_DOT				0x30FB
#define		FIRST_ARABIC_INDIC_DIGIT		0x0660
#define		LAST_ARABIC_INDIC_DIGIT			0x0669
#define		FIRST_EXT_ARABIC_INDIC_DIGIT	0x06F0
#define		LAST_EXT_ARABIC_INDIC_DIGIT		0x06F9

/*
 *
 */
#define		VIRAMA		9
#define		GREEK		"Greek"
#define		HEBREW		"Hebrew"
#define		HIRAGANA	"Hiragana"
#define		KATAKANA	"Katakana"
#define		HAN			"Han"

/*
 * regex for ZERO_WIDTH_NON_JOINER_REGEX_RULE
 */
#define		ZERO_WIDTH_NON_JOINER_REGEX		"^(L|D)(T)*200C(T)*(R|D)$"

#define		UNDEFINED_CODEPOINT				G_MAXUINT32

#define		before(cp)	((cpIndex - 1) < 0 ? UNDEFINED_CODEPOINT : codePoints[cpIndex - 1] )

#define		after(cp)	( (cpIndex + 1) > cpArrSize ? UNDEFINED_CODEPOINT : codePoints[cpIndex + 1] )

bool executeContextualRules(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);
#endif /* CONTEXTUAL_RULES_H_ */
