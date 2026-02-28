# This script will build the website and push it to the gh-pages branch,
# publishing it automatically to https://bitcoindevkit.github.io/devkit-wallet/.

set -euo pipefail

just clean
just build
cd ./site/
git init .
git switch --create gh-pages
git add .
git commit --message "Deploy $(date +"%Y-%m-%d")"
git remote add upstream git@github.com:bitcoindevkit/devkit-wallet.git
git push upstream gh-pages --force
cd ..
