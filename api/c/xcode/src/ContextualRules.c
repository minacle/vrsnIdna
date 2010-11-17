/*
 * ContextualRulesRegistry.c
 *
 *  Created on: July 28, 2010
 *      Author: prsrinivasan
 */

#include <idna_utils.h>
#include <contextual_rules.h>
#include <data_lookups.h>
#include <error_handler.h>

/*Appendix A.1. ZERO WIDTH NON-JOINER


 Code point:
 U+200C

 Overview:
 This may occur in a formally cursive script (such as Arabic) in a
 context where it breaks a cursive connection as required for
 orthographic rules, as in the Persian language, for example.  It
 also may occur in Indic scripts in a consonant-conjunct context
 (immediately following a virama), to control required display of
 such conjuncts.

 Lookup:
 True

 Rule Set:

 False;

 If Canonical_Combining_Class(Before(cp)) .eq.  Virama Then True;

 If RegExpMatch((Joining_Type:{L,D})(Joining_Type:T)*\u200C

 (Joining_Type:T)*(Joining_Type:{R,D})) Then True;
 */
bool execZeroWidthNonJoinerRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if (codePoint == ZERO_WIDTH_NON_JOINER) {

		ruleExecutionMatrix[cpIndex][ZERO_WIDTH_NON_JOINER_RULE] = true;

		if (before(codePoint) == UNDEFINED_CODEPOINT) {
			*ruleExecStatusPtr = XCODE_CTXRULE_ZERO_WIDTH_NON_JOINER_FAIL;
			return false;
		}

		if (getCanonicalClass(before(codePoint)) == VIRAMA) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		//the canonical class of cp before the ZERO_WIDTH_NON_JOINER is not VIRAMA
		//check regex compliance of label
		char derivedLabel[512] = "";

		int i;
		for (i = 0; i < cpArrSize; i++) {
			codePoint = codePoints[i];

			if (codePoint == ZERO_WIDTH_NON_JOINER) {
				strcat(derivedLabel, "200C");
				continue;
			}

			strcat(derivedLabel, getDerivedJoiningType(codePoint));
		}

		if (g_regex_match_simple(ZERO_WIDTH_NON_JOINER_REGEX, derivedLabel, 0,
				0)) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		*ruleExecStatusPtr = XCODE_CTXRULE_ZERO_WIDTH_NON_JOINER_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;
}

/*
 Appendix A.2. ZERO WIDTH JOINER


 Code point:
 U+200D

 Overview:
 This may occur in Indic scripts in a consonant-conjunct context
 (immediately following a virama), to control required display of
 such conjuncts.

 Lookup:
 True

 Rule Set:

 False;

 If Canonical_Combining_Class(Before(cp)) .eq.  Virama Then True;
 */
bool execZeroWidthJoinerRule(guint32 codePoints[], int cpArrSize, int cpIndex,
		int *ruleExecStatusPtr, bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if (codePoint == ZERO_WIDTH_JOINER) {

		ruleExecutionMatrix[cpIndex][ZERO_WIDTH_JOINER_RULE] = true;

		if (before(codePoint) == UNDEFINED_CODEPOINT) {
			*ruleExecStatusPtr = XCODE_CTXRULE_FAIL;
			return false;
		}

		if (getCanonicalClass(before(codePoint)) == VIRAMA) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		*ruleExecStatusPtr = XCODE_CTXRULE_ZERO_WIDTH_JOINER_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;
}

/*
 Appendix A.3. MIDDLE DOT


 Code point:
 U+00B7

 Overview:
 Between 'l' (U+006C) characters only, used to permit the Catalan
 character ela geminada to be expressed.

 Lookup:
 False

 Rule Set:

 False;

 If Before(cp) .eq.  U+006C And

 After(cp) .eq.  U+006C Then True;
 */
bool execMiddleDotRule(guint32 codePoints[], int cpArrSize, int cpIndex,
		int *ruleExecStatusPtr, bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if (codePoint == MIDDLE_DOT) {

		ruleExecutionMatrix[cpIndex][MIDDLE_DOT_RULE] = true;

		if ((before(codePoint) == UNDEFINED_CODEPOINT) || (after(codePoint)
				== UNDEFINED_CODEPOINT)) {

			*ruleExecStatusPtr = XCODE_CTXRULE_FAIL;
			return false;
		}

		if ((before(codePoint) == 0x006C) && (after(codePoint) == 0x006C)) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		*ruleExecStatusPtr = XCODE_CTXRULE_MIDDLE_DOT_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;
}

/*
 Appendix A.4. GREEK LOWER NUMERAL SIGN (KERAIA)


 Code point:
 U+0375

 Overview:
 The script of the following character MUST be Greek.

 Lookup:
 False

 Rule Set:

 False;

 If Script(After(cp)) .eq.  Greek Then True;
 */
bool execGreekLowerNumeralSignRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if (codePoint == GREEK_LOWER_NUMERAL_SIGN) {

		ruleExecutionMatrix[cpIndex][GREEK_LOWER_NUMERAL_SIGN_RULE] = true;

		if (after(codePoint) == UNDEFINED_CODEPOINT) {

			*ruleExecStatusPtr = XCODE_CTXRULE_FAIL;
			return false;
		}

		if (strcmp(getScript(after(codePoint)), GREEK) == 0) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		*ruleExecStatusPtr = XCODE_CTXRULE_GREEK_LOWER_NUMERAL_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;

}

/*
 Appendix A.5. HEBREW PUNCTUATION GERESH


 Code point:
 U+05F3

 Overview:
 The script of the preceding character MUST be Hebrew.

 Lookup:
 False

 Rule Set:

 False;

 If Script(Before(cp)) .eq.  Hebrew Then True;
 */
bool execHebrewPunctuationGereshRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if (codePoint == HEBREW_PUNCTUATION_GERESH) {

		ruleExecutionMatrix[cpIndex][HEBREW_PUNCTUATION_GERESH_RULE] = true;

		if (before(codePoint) == UNDEFINED_CODEPOINT) {

			*ruleExecStatusPtr = XCODE_CTXRULE_FAIL;
			return false;
		}

		if (strcmp(getScript(before(codePoint)), HEBREW) == 0) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		*ruleExecStatusPtr = XCODE_CTXRULE_HEBREW_PUNCUTATION_GERESH_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;

}

/*
 Appendix A.6. HEBREW PUNCTUATION GERSHAYIM


 Code point:
 U+05F4

 Overview:
 The script of the preceding character MUST be Hebrew.

 Lookup:
 False

 Rule Set:

 False;

 If Script(Before(cp)) .eq.  Hebrew Then True;
 */
bool execHebrewPunctuationGershayimRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if (codePoint == HEBREW_PUNCTUATION_GERSHAYIM) {

		ruleExecutionMatrix[cpIndex][HEBREW_PUNCTUATION_GERSHAYIM_RULE] = true;

		if (before(codePoint) == UNDEFINED_CODEPOINT) {

			*ruleExecStatusPtr = XCODE_CTXRULE_FAIL;
			return false;
		}

		if (strcmp(getScript(before(codePoint)), HEBREW) == 0) {
			*ruleExecStatusPtr = XCODE_SUCCESS;
			return true;
		}

		*ruleExecStatusPtr = XCODE_CTXRULE_HEBREW_PUNCUTATION_GERSHAYIM_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;

}

/*
 Appendix A.7. KATAKANA MIDDLE DOT


 Code point:
 U+30FB

 Overview:
 Note that the Script of Katakana Middle Dot is not any of
 "Hiragana", "Katakana", or "Han".  The effect of this rule is to
 require at least one character in the label to be in one of those
 scripts.

 Lookup:
 False

 Rule Set:

 False;

 For All Characters:

 If Script(cp) .in. {Hiragana, Katakana, Han} Then True;

 End For;
 */
bool execKatakanaMiddleDotRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];
	char *script;

	if (codePoint == KATAKANA_MIDDLE_DOT) {

		int i;
		// if a KATAKANA_MIDDLE_DOT and a HIRAGANA|KATAKANA|HAN code point co-exsit
		// in the label, or if all the code points have been iterated through,
		// then the rule can be considered to have been evaluated for all code points
		for (i = 0; i < cpArrSize; i++) {
			ruleExecutionMatrix[i][KATAKANA_MIDDLE_DOT_RULE] = true;
		}

		for (i = 0; i < cpArrSize; i++) {
			if (codePoints[i] == KATAKANA_MIDDLE_DOT) {
				continue;
			}

			script = getScript(codePoints[i]);

			if ((strcmp(script, HIRAGANA) == 0) || (strcmp(script, KATAKANA)
					== 0) || (strcmp(script, HAN) == 0)) {
				*ruleExecStatusPtr = XCODE_SUCCESS;
                                return true;
			}
		}

                //none of the code points are HIRAGANA|KATAKANA|HAN
                *ruleExecStatusPtr = XCODE_CTXRULE_KATAKANA_MIDDLE_DOT_FAIL;
		return false;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;

}

/*
 Appendix A.8. ARABIC-INDIC DIGITS


 Code point:
 0660..0669

 Overview:
 Can not be mixed with Extended Arabic-Indic Digits.

 Lookup:
 False

 Rule Set:

 True;

 For All Characters:

 If cp .in. 06F0..06F9 Then False;

 End For;
 */
bool execArabicIndicDigitsRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if ((codePoint >= FIRST_ARABIC_INDIC_DIGIT) && (codePoint
			<= LAST_ARABIC_INDIC_DIGIT)) {

		int i;
		// if an Extended Arabic Indic and Arabic Indic digit co-exist
		// in the label, or if all the code points have been iterated through,
		// then the rule can be considered to have been evaluated for all code points
		for (i = 0; i < cpArrSize; i++) {
			ruleExecutionMatrix[i][ARABIC_INDIC_DIGITS_RULE] = true;
		}

		for (i = 0; i < cpArrSize; i++) {
			if ((codePoints[i] >= FIRST_EXT_ARABIC_INDIC_DIGIT)
					&& (codePoints[i] <= LAST_EXT_ARABIC_INDIC_DIGIT)) {

				*ruleExecStatusPtr = XCODE_CTXRULE_ARABIC_INDIC_DIGITS_FAIL;
				return false;

			}
		}

		return true;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;
}

/*
 Appendix A.9. EXTENDED ARABIC-INDIC DIGITS


 Code point:
 06F0..06F9

 Overview:
 Can not be mixed with Arabic-Indic Digits.

 Lookup:
 False

 Rule Set:

 True;

 For All Characters:

 If cp .in. 0660..0669 Then False;

 End For;
 */
bool execExtArabicIndicDigitsRule(guint32 codePoints[], int cpArrSize,
		int cpIndex, int *ruleExecStatusPtr,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	guint32 codePoint = codePoints[cpIndex];

	if ((codePoint >= FIRST_EXT_ARABIC_INDIC_DIGIT) && (codePoint
			<= LAST_EXT_ARABIC_INDIC_DIGIT)) {

		int i;
		// if an Arabic Indic and Extended Arabic Indic digit co-exist
		// in the label, or if all the code points have been iterated through,
		// then the rule can be considered to have been evaluated for all code points
		for (i = 0; i < cpArrSize; i++) {
			ruleExecutionMatrix[i][EXT_ARABIC_INDIC_DIGITS_RULE] = true;
		}

		for (i = 0; i < cpArrSize; i++) {
			if ((codePoints[i] >= FIRST_ARABIC_INDIC_DIGIT) && (codePoints[i]
					<= LAST_ARABIC_INDIC_DIGIT)) {

				*ruleExecStatusPtr = XCODE_CTXRULE_EXT_ARABIC_INDIC_DIGITS_FAIL;
				return false;

			}
		}

		return true;
	}

	//the rule is not applicable at this code point
	*ruleExecStatusPtr = XCODE_CTXRULE_NOT_APPLICABLE_FAIL;

	return false;
}

bool isRuleApplicableToCodePoint(rule currRule, guint32 codePoint) {
	if ((codePoint == ZERO_WIDTH_NON_JOINER) && (currRule
			== ZERO_WIDTH_NON_JOINER_RULE))
		return true;

	if ((codePoint == ZERO_WIDTH_JOINER)
			&& (currRule == ZERO_WIDTH_JOINER_RULE))
		return true;

	if ((codePoint == MIDDLE_DOT) && (currRule == MIDDLE_DOT_RULE))
		return true;

	if ((codePoint == GREEK_LOWER_NUMERAL_SIGN) && (currRule
			== GREEK_LOWER_NUMERAL_SIGN_RULE))
		return true;

	if ((codePoint == HEBREW_PUNCTUATION_GERESH) && (currRule
			== HEBREW_PUNCTUATION_GERESH_RULE))
		return true;

	if ((codePoint == HEBREW_PUNCTUATION_GERSHAYIM) && (currRule
			== HEBREW_PUNCTUATION_GERSHAYIM_RULE))
		return true;

	if ((codePoint == KATAKANA_MIDDLE_DOT) && (currRule
			== KATAKANA_MIDDLE_DOT_RULE))
		return true;

	if ((currRule == ARABIC_INDIC_DIGITS_RULE) && (codePoint
			>= FIRST_ARABIC_INDIC_DIGIT) && (codePoint
			<= LAST_ARABIC_INDIC_DIGIT))
		return true;

	if ((currRule == EXT_ARABIC_INDIC_DIGITS_RULE) && (codePoint
			>= FIRST_EXT_ARABIC_INDIC_DIGIT) && (codePoint
			<= LAST_EXT_ARABIC_INDIC_DIGIT))
		return true;

	return false;
}

bool hasBeenAppliedForAllCodePoints(rule currRule, int cpArrSize,
		bool ruleExecutionMatrix[][LAST_RULE]) {

	bool appliedForAllCodePoints = true;

	int i;
	for (i = 0; i < cpArrSize; i++) {
		appliedForAllCodePoints = (appliedForAllCodePoints
				&& ruleExecutionMatrix[i][currRule]);
	}

	return appliedForAllCodePoints;
}

bool executeRule(rule currRule, bool ruleExecutionMatrix[][LAST_RULE],
		guint32 codePoints[], int cpArrSize, int cpIndex,
		int *ruleExecStatusPtr) {
	switch (currRule) {
	case ZERO_WIDTH_NON_JOINER_RULE:
		return execZeroWidthNonJoinerRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case ZERO_WIDTH_JOINER_RULE:
		return execZeroWidthJoinerRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case MIDDLE_DOT_RULE:
		return execMiddleDotRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case GREEK_LOWER_NUMERAL_SIGN_RULE:
		return execGreekLowerNumeralSignRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case HEBREW_PUNCTUATION_GERESH_RULE:
		return execHebrewPunctuationGereshRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case HEBREW_PUNCTUATION_GERSHAYIM_RULE:
		return execHebrewPunctuationGershayimRule(codePoints, cpArrSize,
				cpIndex, ruleExecStatusPtr, ruleExecutionMatrix);

	case KATAKANA_MIDDLE_DOT_RULE:
		return execKatakanaMiddleDotRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case ARABIC_INDIC_DIGITS_RULE:
		return execArabicIndicDigitsRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);

	case EXT_ARABIC_INDIC_DIGITS_RULE:
		return execExtArabicIndicDigitsRule(codePoints, cpArrSize, cpIndex,
				ruleExecStatusPtr, ruleExecutionMatrix);
	default:
		break;
	}

	return false;
}

/*
 * This function executes rules using the following psuedo-coded algorithm
 *
 * for each codePoint in codePoints
 * do
 * 	next_rule:
 * 	for each rule in rules
 * 	do
 * 		if (isRuleApplicable(codePoint, rule))
 * 		then
 * 			if (hasBeenAppliedForAllCodePoints(rule))
 * 			then
 * 				continue next_rule;
 * 			else
 * 				executeRule(rule, codePoint, codePointIdx, codePoints, arrSize);
 * 			end-if
 *		end-if
 *
 * 	done
 * done
 */
bool executeContextualRules(guint32 codePoints[], int cpArrSize,
		int *ruleExecStatusPtr) {

	//ruleExecutionMatrix - 2-dimensional array captures rule execution status for a codePoint.
	//Redundant rule executions can be avoided by recording this status.
	//LAST_RULE is a marker rule that gives the number of rules in the rule enum
	bool ruleExecutionMatrix[cpArrSize][LAST_RULE];

	int cpIndex;
	rule currRule;
	bool passedAllRules = true;

	for (cpIndex = 0; cpIndex < cpArrSize; cpIndex++) {
		for (currRule = ZERO_WIDTH_NON_JOINER_RULE; currRule < LAST_RULE; currRule++) {

			if (isRuleApplicableToCodePoint(currRule, codePoints[cpIndex])) {

				/*if (hasBeenAppliedForAllCodePoints(currRule, cpArrSize,
				 ruleExecutionMatrix))
				 continue;*/

				passedAllRules = passedAllRules && executeRule(currRule,
						ruleExecutionMatrix, codePoints, cpArrSize, cpIndex,
						ruleExecStatusPtr);

				if (!passedAllRules) {
					logError(*ruleExecStatusPtr);
					return false;
				}
			}

		}
	}

	return true;
}

