# Device Compile Blocker - 2026-03-21

## Scenario
- Directly verify whether the connected device can execute package compilation outside the benchmark harness

## Command
- `adb shell cmd package compile -f -m speed-profile com.novel`
- `adb shell cmd package compile -f -m speed-profile com.android.settings`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- ROM Fingerprint: `OnePlus/DN2101IND/OP515BL1:13/TP1A.220905.001/R.221a094-16f10:user/release-keys`

## Expected
- If the blocker is app-specific, the command should fail only for `com.novel`.
- If the blocker is device-side, the same command should fail for unrelated packages as well.

## Actual
- Both commands failed with the same message:
  - `Error: Failed to cpmpile !`
- Because `com.android.settings` fails with the same shell command, the blocker is treated as device/ROM-side by default, not as a `com.novel` runtime regression.

## Evidence
- `docs/refactor/evidence/device-compile-blocker-2026-03-21.txt`
- `docs/refactor/evidence/baseline-profile-benchmark-testlog-2026-03-21.txt`

## Result
- `accepted-environment-blocker`

## Residual Risk
- This conclusion should be re-validated on a second device before turning compiled-mode startup/profile runs into a hard release gate.
