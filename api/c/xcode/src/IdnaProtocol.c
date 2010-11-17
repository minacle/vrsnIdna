/*
 * IdnaProtocol.c
 *
 *  Created on: July 30, 2010
 *      Author: prsrinivasan
 */

#include <idna_types.h>
#include <idna_utils.h>
#include <data_lookups.h>
#include <unicode_dataloader.h>
#include <xcode_config.h>
#include <xcode.h>
#include <contextual_rules.h>
#include <bidi_rules.h>

#include <stdlib.h>
#include <stdio.h>

#include <idna_protocol.h>
#include <error_handler.h>

int idna2008Protocol(guint32 codePoints[], int arrSize) {
	int ruleExecStatus;

	if ((codePoints == NULL) || (arrSize == 0))
		return XCODE_BAD_ARGUMENT_ERROR;

	if (hasDisallowedOrUnassigned(codePoints, arrSize)) {
		logError(HAS_DISALLOWED_OR_UNASSIGNED);
		return HAS_DISALLOWED_OR_UNASSIGNED;
	}

	if (hasRestrictedHyphens(codePoints, arrSize)) {
		logError(HAS_RESTRICTED_HYPHENS);
		return HAS_RESTRICTED_HYPHENS;
	}

	if (isCombiningMark(codePoints[0])) {
		logError(IS_COMBINING_MARK);
		return IS_COMBINING_MARK;
	}

	if (hasContextualCodePoints(codePoints, arrSize)) {
		//Contextual Rules
		if (!executeContextualRules(codePoints, arrSize, &ruleExecStatus)) {
			logError(XCODE_CTXRULE_FAIL);
			return XCODE_CTXRULE_FAIL;
		}
	}

	return XCODE_SUCCESS;
}

int applyIdna2008BidiRules(guint32 domain[], int domainSize) {

	static guint32 DOMAIN_DELIMITERS[] = { 0x2E, 0x3002, 0xFF0E, 0xFF61 };

	if (!isBidiDomain(domain, domainSize))
		return XCODE_SUCCESS;

	//Get array of delimiter positions within domain array of code points.
	//Choice of delimiter positions vs. actual tokens was made to simplify
	//memory management
	int *delimiterPositionArr = tokenizeUnicodeArr(domain, domainSize,
			DOMAIN_DELIMITERS, 4);

	int i = 0;

	int domainLabelStartPos = 0;
	int domainLabelEndPos;
	int domainLabelSize;

	while (delimiterPositionArr[i] != -1) {
		domainLabelEndPos = delimiterPositionArr[i] - 1;
		domainLabelSize = domainLabelEndPos - domainLabelStartPos + 1;

		if (!assertBidiCompliance(&domain[domainLabelStartPos], domainLabelSize)) {
			free(delimiterPositionArr);
			logError(XCODE_BIDIRULE_FAIL);
			return XCODE_BIDIRULE_FAIL;
		}

		domainLabelStartPos = delimiterPositionArr[i] + 1;
		i++;
	}

	free(delimiterPositionArr);
	delimiterPositionArr = NULL;

	return XCODE_SUCCESS;
}

