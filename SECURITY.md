# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability, please report it privately:

1. **Do NOT** open a public issue
2. Email the maintainers directly or use GitHub's private vulnerability reporting
3. Include detailed description and reproduction steps
4. Allow reasonable time for a fix before public disclosure

We will acknowledge receipt within 48 hours and provide a timeline for the fix.

## Dependency Security

### JSON Library

This library uses Android's built-in `org.json` package (part of the Android SDK framework) for JSON serialization. This is **NOT** the `org.json:json` Maven artifact that has known vulnerabilities (CVE-2022-45688, CVE-2023-5072).

The Android framework's `org.json` implementation:
- Is maintained separately by Google as part of AOSP
- Receives security updates through Android OS updates
- Is not affected by vulnerabilities in the Maven artifact
- Is available since Android API level 1

For more information on Android's JSON implementation, see:
https://developer.android.com/reference/org/json/JSONObject

### Native Library Security

The native PDFium library (libpdfium.so) is used for PDF operations:
- Version: As specified in pdfium-android-arm64/VERSION
- Source: https://pdfium.googlesource.com/pdfium/
- Memory safety: All JNI operations include pointer validation
- Resource management: Automatic cleanup via Closeable interface
