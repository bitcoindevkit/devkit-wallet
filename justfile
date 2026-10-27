default:
  just --list --unsorted

@devices:
  adb devices

build:
  ./kotlin build

# Run the app in an emulator or on a device (list your connected devices using `just devices`)
run DEVICE="":
  ./kotlin run {{ if DEVICE == "" { "" } else { "--device-id=" + DEVICE } }}

# Build the docs website
docs-build:
  uv run zensical build

# Serve the docs locally
docs-serve:
  uv run zensical serve

docs-clean:
  rm -rf .venv/
  rm -rf .cache/
  rm -rf site/

[group("Format")]
[doc("Format all Kotlin source files with ktfmt.")]
format: check-ktfmt-version
  ktfmt --kotlinlang-style --enable-editorconfig src/

[group("Format")]
[doc("Check that all Kotlin source files are formatted, without changing them.")]
format-check: check-ktfmt-version
  #!/usr/bin/env bash
  set -euo pipefail
  if ! ktfmt --kotlinlang-style --enable-editorconfig --dry-run --set-exit-if-changed src/; then
    echo -e "\nError: the files above need formatting. Run 'just format' to fix them." >&2
    exit 1
  fi

# The version of ktfmt must be 0.65
[private]
check-ktfmt-version:
  #!/usr/bin/env bash
  set -euo pipefail
  version=$(ktfmt --version | grep -oE '[0-9]+\.[0-9]+')
  if [ "$version" != "0.65" ]; then
    echo "Error: ktfmt 0.65 is required, but found $version" >&2
    exit 1
  fi