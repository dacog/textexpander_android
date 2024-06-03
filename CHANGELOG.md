# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2024-06-04

### Added
- Support for traversing directories within the `match` folder to search for YAML files. This allows the app to handle configurations organized in subdirectories, improving the manageability of larger sets of expansion rules.
- Support for multiple triggers per single replacement as documented in https://espanso.org/docs/matches/basics/#multiple-triggers. Users can now specify multiple trigger phrases that result in the same output, enhancing flexibility in text expansions in the same way is in the desktop app.

### Changed
- Updated the file parsing logic to recursively search through directories and subdirectories for YAML files, ensuring that all potential configurations are loaded.
- Enhanced the text expansion functionality to check each trigger against the current text input, allowing for multiple triggers to be efficiently recognized and processed. In the background this means duplicated data (the same value in many rows) but this approach makes it less complex and easier to maintain.

### Fixed
- Minor bug fixes related to YAML file parsing that could cause errors during app initialization under certain conditions.

## [1.0] - 2024-06-02

### Added
- Initial release of the app with basic text expansion functionality.
- Support for loading and parsing YAML configuration files from the `match` folder.
- Basic text replacement based on configured triggers and corresponding outputs.
- Basic UI showing all the yaml files the app found inside the `match` folder and their trigger-replace pairs. The user can touch a pair and its replace value will be copied to the clipboard.
