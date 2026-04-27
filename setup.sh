#!/bin/bash
set -e

echo "=== BigType AI Setup ==="
echo "Creating full directory structure..."

# Create full directory structure
mkdir -p bigtype-ai/.github/workflows
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/ime
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/keyboard
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/llm
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/bridge
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/clipboard
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/db
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/di
mkdir -p bigtype-ai/app/src/main/java/com/aetheria/bigtype/ui/theme
mkdir -p bigtype-ai/app/src/main/res/xml
mkdir -p bigtype-ai/gradle/wrapper

cd bigtype-ai

echo "Initializing git repository..."
git init
git branch -m master main 2>/dev/null || git branch -m main

echo "Adding remote origin..."
git remote add origin https://github.com/tonygrischke-art/bigtype-ai.git 2>/dev/null || echo "Remote already exists"

echo "Creating .gitignore..."
cat > .gitignore << 'EOF'
*.apk
*.ap_
*.dex
*.class
*.log
.gradle/
local.properties
*.iml
.idea/
build/
captures/
external/
EOF

echo "Adding all files..."
git add .

echo "Creating initial commit..."
git commit -m "feat: Initial BigType AI commit with Phase 1 & 2"

echo "Pushing to main..."
git push -u origin main

echo "=== Setup complete! ==="
echo "Repository: https://github.com/tonygrischke-art/bigtype-ai"
