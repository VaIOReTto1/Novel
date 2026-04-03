const signoffCheck = require('../../scripts/stage7-signoff-check.js');

describe('stage7 signoff check', () => {
  test('fails while signoff parties are still pending', () => {
    const model = signoffCheck.readSignoffModel({
      repoRoot: require('path').resolve(__dirname, '..', '..'),
    });

    const result = signoffCheck.validateSignoffModel(model);

    expect(result.ok).toBe(false);
    expect(result.message).toContain('Pending signoff parties');
  });

  test('passes when all parties approve and final gate is validated', () => {
    const result = signoffCheck.validateSignoffModel({
      results: [
        {
          key: 'design',
          label: 'Design',
          reviewer: 'Designer A',
          date: '2026-04-04',
          decision: 'approved',
        },
        {
          key: 'product',
          label: 'Product',
          reviewer: 'PM A',
          date: '2026-04-04',
          decision: 'approved-with-notes',
        },
        {
          key: 'qa',
          label: 'QA',
          reviewer: 'QA A',
          date: '2026-04-04',
          decision: 'approved',
        },
      ],
      finalDecision: 'Stage 7 = validated',
      decisionDate: '2026-04-04',
    });

    expect(result).toEqual({
      ok: true,
      message: 'Stage 7 signoff record is complete and validated.',
    });
  });
});
