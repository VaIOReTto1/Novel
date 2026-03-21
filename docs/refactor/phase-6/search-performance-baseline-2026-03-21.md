# Search Performance Baseline - 2026-03-21

## Scenario
- Cold-start into search results via debug route
- Route: `search_result?query=novel`

## Command
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "search_result?query=novel"`
- `adb logcat -d | Select-String "ComposeMainActivity|NavigationSetup|SearchResultPage|SearchResultViewModel|SearchBooksUseCase|SearchService|SearchRepository"`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build Variant: `debug`

## Expected
- The route must land on `SearchResultPage`.
- Search must execute without runtime error.
- The first sample can be a log-based sample; it does not need to be a full benchmark.

## Actual
- `ComposeMainActivity` cold-started successfully.
- `NavigationSetup` jumped to `search_result?query=novel` at `21:30:57.936`.
- `SearchResultViewModel` initialized at `21:30:58.043`.
- Search preparation logged at `21:30:58.335`.
- Actual search execution logged at `21:30:58.836`.
- Search success logged at `21:30:58.881`.
- Current log sample therefore shows:
  - route jump -> search execution: about `900 ms`
  - route jump -> first success log: about `945 ms`
- Current sample returned `0` results, but the route and fetch path stayed healthy.

## Evidence
- `docs/refactor/evidence/search-result-performance-logcat-2026-03-21.txt`

## Result
- `pass-log-sample`

## Residual Risk
- Current evidence is a log sample, not a dedicated search benchmark.
- Search result paging still lacks a dedicated macrobenchmark or smoke-style regression suite.
