default:
  just --list --unsorted

# Run the app in an emulator
run:
  ./kotlin run

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

# Format app using ktfmt 0.64
format:
  #!/usr/bin/env bash
  set -euo pipefail
  version=$(ktfmt --version | grep -oE '[0-9]+\.[0-9]+')
  if [ "$version" != "0.64" ]; then
    echo "Error: ktfmt 0.64 is required, but found $version" >&2
    exit 1
  fi
  ktfmt --kotlinlang-style --enable-editorconfig src/

# Check formatting using ktfmt 0.64 without modifying files
format-check:
  #!/usr/bin/env bash
  set -euo pipefail
  version=$(ktfmt --version | grep -oE '[0-9]+\.[0-9]+')
  if [ "$version" != "0.64" ]; then
    echo "Error: ktfmt 0.64 is required, but found $version" >&2
    exit 1
  fi
  if ! ktfmt --kotlinlang-style --enable-editorconfig --dry-run --set-exit-if-changed src/ > /dev/null; then
    echo "Error: formatting issues found. Run 'just format' to fix them." >&2
    exit 1
  fi
