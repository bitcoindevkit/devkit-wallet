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
