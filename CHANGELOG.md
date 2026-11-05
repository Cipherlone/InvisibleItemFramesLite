# Changelog

All notable changes to this project are documented here.

## Unreleased — Paper 26.3 compatibility

### Changed
- Updated the Paper API dependency to 26.3.
- Updated MockBukkit to the matching 26.3 test artifact.
- Declared `api-version: '26.3'` in plugin metadata.
- Kept Java 25 as the project compilation target.
- Consolidated the JUnit version in Maven properties.
- Removed the legacy Sonatype OSS repository; dependencies are resolved from Maven Central and the PaperMC repository.
- Isolated the relocated bStats classes under the plugin's `lib.bstats` package.

### Documentation
- Updated the README compatibility statement and Maven verification instructions.
- Added class and method documentation to explain plugin lifecycle, persistent-data markers, item creation, configuration parsing, and event handling.

### Validation
- Repository metadata and source changes have been reviewed.
- A Maven build and in-game Paper 26.3 test have not yet been run as part of this change.
