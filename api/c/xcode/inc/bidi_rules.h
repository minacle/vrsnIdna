/*
 * bidi_rules.h
 *
 *  Created on: July 30, 2010
 *      Author: prsrinivasan
 */

#ifndef BIDI_RULES_H_
#define BIDI_RULES_H_

#include <idna_types.h>
#include <xcode.h>

typedef enum {
	BIDI_RULE_ONE,
	BIDI_RULE_TWO,
	BIDI_RULE_THREE,
	BIDI_RULE_FOUR,
	BIDI_RULE_FIVE,
	BIDI_RULE_SIX,
	BIDI_RULE_LAST
} bidi_rule;

/*
 * <Error Codes>
 */
#define		XCODE_BIDIRULE_FAIL				0+BIDI_RULE_SPECIFIC
#define		XCODE_BIDIRULE_ONE_FAIL			1+BIDI_RULE_SPECIFIC
#define		XCODE_BIDIRULE_TWO_FAIL			2+BIDI_RULE_SPECIFIC
#define		XCODE_BIDIRULE_THREE_FAIL		3+BIDI_RULE_SPECIFIC
#define		XCODE_BIDIRULE_FOUR_FAIL		4+BIDI_RULE_SPECIFIC
#define		XCODE_BIDIRULE_FIVE_FAIL		5+BIDI_RULE_SPECIFIC
#define		XCODE_BIDIRULE_SIX_FAIL			6+BIDI_RULE_SPECIFIC

/*
 * <Error Messages>
 */
#define		XCODE_BIDIRULE_FAIL_MSG				"BIDI Rules evaluation failed"
#define		XCODE_BIDIRULE_ONE_FAIL_MSG			"BIDI Rule 1 evaluation failed : The first character must be a character with Bidi property L, R, or AL. If it has the R or AL property, it is an RTL label; if it has the L property, it is an LTR label."
#define		XCODE_BIDIRULE_TWO_FAIL_MSG			"BIDI Rule 2 evaluation failed : In an RTL label, only characters with the Bidi properties R, AL, AN, EN, ES, CS, ET, ON, BN, or NSM are allowed."
#define		XCODE_BIDIRULE_THREE_FAIL_MSG		"BIDI Rule 3 evaluation failed : In an RTL label, the end of the label must be a character with Bidi property R, AL, EN, or AN, followed by zero or more characters with Bidi property NSM."
#define		XCODE_BIDIRULE_FOUR_FAIL_MSG		"BIDI Rule 4 evaluation failed : In an RTL label, if an EN is present, no AN may be present, and vice versa."
#define		XCODE_BIDIRULE_FIVE_FAIL_MSG		"BIDI Rule 5 evaluation failed : In an LTR label, only characters with the Bidi properties L, EN, ES, CS, ET, ON, BN, or NSM are allowed."
#define		XCODE_BIDIRULE_SIX_FAIL_MSG			"BIDI Rule 6 evaluation failed : In an LTR label, the end of the label must be a character with Bidi property L or EN, followed by zero or more characters with Bidi property NSM."

bool isBidiDomain(guint32 codePoints[], int cpArrSize);

bool isRTLLabel(guint32 codePoints[], int cpArrSize);

bool isLTRLabel(guint32 codePoints[], int cpArrSize);

bool execBidiRuleOne(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);

bool execBidiRuleTwo(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);

bool execBidiRuleThree(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);

bool execBidiRuleFour(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);

bool execBidiRuleFive(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);

bool execBidiRuleSix(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr);

bool execBidiRules(guint32 codePoints[], int cpArrSize, int *ruleExecStatusPtr);

bool assertBidiCompliance(guint32 codePoints[], int cpArrSize);

#endif /* BIDI_RULES_H_ */
