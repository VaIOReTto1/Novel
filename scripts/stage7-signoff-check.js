#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const DEFAULT_SIGNOFF_PATH = path.join(
  'docs',
  'refactor',
  'stage-7-signoff-record.md',
);

const readText = (repoRoot, relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const capture = (text, pattern, fallback = 'unknown') => {
  const match = text.match(pattern);
  return match ? match[1].trim() : fallback;
};

const captureSectionField = (text, sectionTitle, label, fallback = 'unknown') => {
  const escapedTitle = sectionTitle.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const escapedLabel = label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const pattern = new RegExp(
    `## ${escapedTitle}[\\s\\S]*?- ${escapedLabel}:\\s*` + '`?([^\\n`]+)`?',
  );
  return capture(text, pattern, fallback);
};

const readSignoffModel = ({
  repoRoot = path.resolve(__dirname, '..'),
  signoffPath = DEFAULT_SIGNOFF_PATH,
} = {}) => {
  const signoffRecord = readText(repoRoot, signoffPath);
  const parties = [
    {
      key: 'design',
      title: 'Design Signoff',
      label: 'Design',
    },
    {
      key: 'product',
      title: 'Product Signoff',
      label: 'Product',
    },
    {
      key: 'qa',
      title: 'QA Signoff',
      label: 'QA',
    },
  ];

  const results = parties.map((party) => ({
    key: party.key,
    label: party.label,
    reviewer: captureSectionField(signoffRecord, party.title, 'Reviewer', 'pending'),
    date: captureSectionField(signoffRecord, party.title, 'Date', 'pending'),
    decision: captureSectionField(signoffRecord, party.title, 'Decision', 'pending'),
  }));

  const finalDecision = captureSectionField(
    signoffRecord,
    'Final Gate',
    'Closeout decision',
    'pending',
  );
  const decisionDate = captureSectionField(
    signoffRecord,
    'Final Gate',
    'Decision date',
    'pending',
  );

  return {
    results,
    finalDecision,
    decisionDate,
  };
};

const validateSignoffModel = (model) => {
  const pendingParties = model.results.filter(
    (item) =>
      item.reviewer === 'pending' ||
      item.date === 'pending' ||
      item.decision === 'pending',
  );
  const blockedParties = model.results.filter((item) => item.decision === 'blocked');
  const invalidParties = model.results.filter(
    (item) =>
      !['approved', 'approved-with-notes', 'blocked', 'pending'].includes(item.decision),
  );

  if (invalidParties.length > 0) {
    return {
      ok: false,
      message: `Invalid signoff decision values: ${invalidParties
        .map((item) => `${item.label}=${item.decision}`)
        .join(', ')}`,
    };
  }

  if (blockedParties.length > 0) {
    return {
      ok: false,
      message: `Blocked signoff parties: ${blockedParties
        .map((item) => item.label)
        .join(', ')}`,
    };
  }

  if (pendingParties.length > 0) {
    return {
      ok: false,
      message: `Pending signoff parties: ${pendingParties
        .map((item) => item.label)
        .join(', ')}`,
    };
  }

  if (model.finalDecision !== 'Stage 7 = validated') {
    return {
      ok: false,
      message: `Final closeout decision is not validated: ${model.finalDecision}`,
    };
  }

  if (model.decisionDate === 'pending') {
    return {
      ok: false,
      message: 'Final closeout decision date is pending.',
    };
  }

  return {
    ok: true,
    message: 'Stage 7 signoff record is complete and validated.',
  };
};

const main = () => {
  const model = readSignoffModel();
  const result = validateSignoffModel(model);
  if (!result.ok) {
    console.error(result.message);
    process.exit(1);
  }
  console.log(result.message);
};

module.exports = {
  readSignoffModel,
  validateSignoffModel,
};

if (require.main === module) {
  main();
}
