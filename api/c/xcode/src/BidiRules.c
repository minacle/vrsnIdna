/*
 * BidiRules.c
 *
 *  Created on: July 30, 2010
 *      Author: prsrinivasan
 */

#include <idna_utils.h>
#include <bidi_rules.h>
#include <data_lookups.h>

/*
 * A "Bidi domain name" is a domain name that contains at least one RTL
 * label.
 */
bool isBidiDomain(guint32 codePoints[], int cpArrSize) {

	int i;
	char *bidiClass;

	for (i = 0; i < cpArrSize; i++) {
		bidiClass = getBidiClass(codePoints[i]);

		if ((strcmp(bidiClass, "R") == 0) || (strcmp(bidiClass, "AL") == 0)
				|| (strcmp(bidiClass, "AN") == 0))
			return true;
	}

	return false;
}

/*
 * An RTL label is a label that contains at least one character of type
 * R, AL, or AN.
 *
 */
bool isRTLLabel(guint32 codePoints[], int cpArrSize) {

	char *bidiClass;

	bidiClass = getBidiClass(codePoints[0]);

	if ((strcmp(bidiClass, "R") == 0) || (strcmp(bidiClass, "AL") == 0)
			|| (strcmp(bidiClass, "AN") == 0))
		return true;

	return false;
}

/*
 * An LTR label is any label that is not an RTL label.
 *
 */
bool isLTRLabel(guint32 codePoints[], int cpArrSize) {
	return !isRTLLabel(codePoints, cpArrSize);
}

/*
 * The first character must be a character with Bidi property L, R,
 * or AL. If it has the R or AL property, it is an RTL label; if it
 * has the L property, it is an LTR label.
 */
bool execBidiRuleOne(guint32 codePoints[], int cpArrSizeint,
		int *ruleExecStatusPtr) {
	char *bidiClass = getBidiClass(codePoints[0]);

	if ((strcmp(bidiClass, "L") == 0) || (strcmp(bidiClass, "R") == 0)
			|| (strcmp(bidiClass, "AL") == 0)) {
		*ruleExecStatusPtr = XCODE_SUCCESS;
		return true;
	}

	*ruleExecStatusPtr = XCODE_BIDIRULE_ONE_FAIL;
	return false;
}

/*
 * Rule applicable only for RTL labels
 * Rule 2 - In an RTL label, only characters with the Bidi properties R, AL,
 * AN, EN, ES, CS, ET, ON, BN, or NSM are allowed.
 *
 */
bool execBidiRuleTwo(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr) {

	int i;
	char *bidiClass;

	for (i = 0; i < cpArrSize; i++) {
		bidiClass = getBidiClass(codePoints[i]);
		if (!isRTLBidiProperty(bidiClass)) {
			*ruleExecStatusPtr = XCODE_BIDIRULE_TWO_FAIL;
			return false;
		}
	}

	*ruleExecStatusPtr = XCODE_SUCCESS;
	return true;
}

/*
 * Rule applicable only for RTL labels
 * Rule 3 - In an RTL label, the end of the label must be a character with
 * Bidi property R, AL, EN, or AN, followed by zero or more
 * characters with Bidi property NSM.
 *
 * In terms of BIDI properties - the rule requires the label to
 * satisfy the regex  .*(R|AL|EN|AN)(NSM)*
 */
bool execBidiRuleThree(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr) {
	int i;
	char *bidiClass;
	bool rulePassed = true;

	for (i = cpArrSize - 1; i >= 0; i--) {
		bidiClass = getBidiClass(codePoints[i]);

		if (!(strcmp(bidiClass, "NSM") == 0)) {
			rulePassed = ((strcmp(bidiClass, "R") == 0) || (strcmp(bidiClass,
					"AL") == 0) || (strcmp(bidiClass, "EN") == 0) || (strcmp(
					bidiClass, "AN") == 0));

			if (rulePassed)
				*ruleExecStatusPtr = XCODE_SUCCESS;
			else
				*ruleExecStatusPtr = XCODE_BIDIRULE_THREE_FAIL;

			return rulePassed;
		}
	}

	*ruleExecStatusPtr = XCODE_BIDIRULE_THREE_FAIL;
	return false;
}

/*
 * Rule applicable only for RTL labels
 * Rule 4 - In an RTL label, if an EN is present, no AN may be present, and
 * vice versa.
 *
 */
bool execBidiRuleFour(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr) {
	int i;
	char *bidiClass;

	bool enExists = false;
	bool anExists = false;

	for (i = 0; i < cpArrSize; i++) {
		bidiClass = getBidiClass(codePoints[i]);

		if (!enExists)
			enExists = (strcmp(bidiClass, "EN") == 0);
		if (!anExists)
			anExists = (strcmp(bidiClass, "AN") == 0);

		if (enExists && anExists) {
			*ruleExecStatusPtr = XCODE_BIDIRULE_FOUR_FAIL;
			return false;
		}
	}

	*ruleExecStatusPtr = XCODE_SUCCESS;
	return true;
}

/*
 * Rule applicable only for LTR labels
 * Rule 5 - In an LTR label, only characters with the Bidi properties L, EN,
 * ES, CS, ET, ON, BN, or NSM are allowed.
 *
 */
bool execBidiRuleFive(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr) {
	int i;
	char *bidiClass;

	for (i = 0; i < cpArrSize; i++) {
		bidiClass = getBidiClass(codePoints[i]);

		if (!isLTRBidiProperty(bidiClass)) {
			*ruleExecStatusPtr = XCODE_BIDIRULE_FIVE_FAIL;
			return false;
		}
	}

	return true;
}

/*
 * Rule applicable only for LTR labels
 * Rule 6 - In an LTR label, the end of the label must be a character with
 * Bidi property L or EN, followed by zero or more characters with
 * Bidi property NSM.
 *
 */
bool execBidiRuleSix(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr) {
	int i;
	char *bidiClass;

	bool rulePassed = true;

	for (i = cpArrSize - 1; i >= 0; i--) {
		bidiClass = getBidiClass(codePoints[i]);

		if (!(strcmp(bidiClass, "NSM") == 0)) {
			rulePassed = (strcmp(bidiClass, "L") == 0) || (strcmp(bidiClass,
					"EN") == 0);

			if (rulePassed)
				*ruleExecStatusPtr = XCODE_SUCCESS;
			else
				*ruleExecStatusPtr = XCODE_BIDIRULE_SIX_FAIL;

			return rulePassed;
		}
	}

	*ruleExecStatusPtr = XCODE_BIDIRULE_SIX_FAIL;
	return false;
}

bool execBidiRules(guint32 codePoints[], int cpArrSize, int *ruleExecStatusPtr) {
	bool allRulesPassed = true;

	allRulesPassed = execBidiRuleOne(codePoints, cpArrSize, ruleExecStatusPtr);

	//return and don't evaluate the rest, if BidiRuleOne evaluated to false
	if (!allRulesPassed)
		return allRulesPassed;

	if (isRTLLabel(codePoints, cpArrSize)) {
		allRulesPassed = allRulesPassed && execBidiRuleTwo(codePoints,
				cpArrSize, ruleExecStatusPtr) && execBidiRuleThree(codePoints,
				cpArrSize, ruleExecStatusPtr) && execBidiRuleFour(codePoints,
				cpArrSize, ruleExecStatusPtr);

		return allRulesPassed;
	}

	if (isLTRLabel(codePoints, cpArrSize)) {
		allRulesPassed = allRulesPassed && execBidiRuleFive(codePoints,
				cpArrSize, ruleExecStatusPtr) && execBidiRuleSix(codePoints,
				cpArrSize, ruleExecStatusPtr);

		return allRulesPassed;
	}

	return allRulesPassed;
}

bool assertBidiCompliance(guint32 codePoints[], int cpArrSize) {
	int ruleExecStatusPtr;

	ruleExecStatusPtr = XCODE_SUCCESS;

	bool allRulesPassed = execBidiRules(codePoints, cpArrSize,
			&ruleExecStatusPtr);

	if (!allRulesPassed) {
		logErr(ruleExecStatusPtr);
	}

	return allRulesPassed;

}
