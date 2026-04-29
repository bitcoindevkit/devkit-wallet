default:
  just --list --unsorted

check:
  ./gradlew ktlintCheck

format:
  ./gradlew ktlintFormat

# Build the docs website
build-docs:
  uv run zensical build

# Serve the docs locally
serve-docs:
  uv run zensical serve

clean-docs:
  rm -rf .venv/
  rm -rf .cache/
  rm -rf site/
