#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const DEFAULT_SIGNOFF_PATH = path.join(
  'docs',
  'refactor',
  'stage-7-signoff-record.md',
);

const PARTY_TITLES = {
  design: 'Design Signoff',
  product: 'Product Signoff',
  qa: 'QA Signoff',
};

const VALID_DECISIONS = new Set(['approved', 'approved-with-notes', 'blocked', 'pending']);

const readText = (repoRoot, relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const writeText = (repoRoot, relativePath, content) => {
  fs.writeFileSync(path.join(repoRoot, relativePath), content, 'utf8');
};

const replaceWithinSection = (text, sectionTitle, transform) => {
  const startMarker = `## ${sectionTitle}`;
  const start = text.indexOf(startMarker);
  if (start === -1) {
    throw new Error(`Missing section: ${sectionTitle}`);
  }
  const nextHeading = text.indexOf('\n## ', start + startMarker.length);
  const end = nextHeading === -1 ? text.length : nextHeading + 1;
  const section = text.slice(start, end);
  const updated = transform(section);
  return `${text.slice(0, start)}${updated}${text.slice(end)}`;
};

const replaceLine = (section, label, value) => {
  const linePattern = new RegExp(`- ${label}:\\s*\`[^\\n]*\``);
  if (!linePattern.test(section)) {
    throw new Error(`Missing line for ${label}`);
  }
  return section.replace(linePattern, `- ${label}: \`${value}\``);
};

const replaceNotes = (section, notes) => {
  const notePattern = /- Notes:\n(?:  - .*?\n)*/;
  const match = section.match(notePattern);
  if (!match) {
    throw new Error('Missing Notes block');
  }
  const notesBlock = `- Notes:\n${notes.map((note) => `  - ${note}`).join('\n')}`;
  return section.replace(notePattern, `${notesBlock}\n`);
};

const parseArgs = (argv) => {
  const result = {};
  for (let i = 2; i < argv.length; i += 1) {
    const arg = argv[i];
    if (!arg.startsWith('--')) continue;
    const key = arg.slice(2);
    const next = argv[i + 1];
    if (!next || next.startsWith('--')) {
      result[key] = true;
      continue;
    }
    result[key] = next;
    i += 1;
  }
  return result;
};

const normalizeNotes = (notesValue) => {
  if (!notesValue) return ['pending'];
  return notesValue
    .split('||')
    .map((item) => item.trim())
    .filter(Boolean);
};

const updateSignoffRecord = ({
  repoRoot = path.resolve(__dirname, '..'),
  signoffPath = DEFAULT_SIGNOFF_PATH,
  party,
  reviewer,
  date,
  decision,
  notes,
  finalDecision,
  finalDecisionDate,
} = {}) => {
  if (!PARTY_TITLES[party]) {
    throw new Error(`Invalid party: ${party}`);
  }
  if (!VALID_DECISIONS.has(decision)) {
    throw new Error(`Invalid decision: ${decision}`);
  }

  let content = readText(repoRoot, signoffPath);
  content = replaceWithinSection(content, PARTY_TITLES[party], (section) => {
    let next = section;
    next = replaceLine(next, 'Reviewer', reviewer);
    next = replaceLine(next, 'Date', date);
    next = replaceLine(next, 'Decision', decision);
    next = replaceNotes(next, notes);
    return next;
  });

  if (finalDecision || finalDecisionDate) {
    content = replaceWithinSection(content, 'Final Gate', (section) => {
      let next = section;
      if (finalDecision) {
        next = replaceLine(next, 'Closeout decision', finalDecision);
      }
      if (finalDecisionDate) {
        next = replaceLine(next, 'Decision date', finalDecisionDate);
      }
      return next;
    });
  }

  writeText(repoRoot, signoffPath, content);
  return { signoffPath: path.join(repoRoot, signoffPath) };
};

const main = () => {
  const args = parseArgs(process.argv);
  const party = args.party;
  const reviewer = args.reviewer;
  const date = args.date;
  const decision = args.decision;

  if (!party || !reviewer || !date || !decision) {
    console.error(
      'Usage: node scripts/stage7-signoff-update.js --party <design|product|qa> --reviewer <name> --date <YYYY-MM-DD> --decision <approved|approved-with-notes|blocked|pending> [--notes "line1 || line2"] [--final-decision "Stage 7 = validated"] [--final-decision-date <YYYY-MM-DD>]',
    );
    process.exit(1);
  }

  const result = updateSignoffRecord({
    party,
    reviewer,
    date,
    decision,
    notes: normalizeNotes(args.notes),
    finalDecision: args['final-decision'],
    finalDecisionDate: args['final-decision-date'],
  });

  console.log(`Updated Stage 7 signoff record: ${result.signoffPath}`);
};

module.exports = {
  normalizeNotes,
  updateSignoffRecord,
};

if (require.main === module) {
  main();
}
