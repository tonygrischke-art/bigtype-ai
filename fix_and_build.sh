#!/bin/bash
set -e

cd /root/bigtype-ai

echo "=== BigType AI Build Script ==="

build() {
    echo "Running: ./gradlew assembleDebug..."
    ./gradlew assembleDebug --no-daemon --stacktrace 2>&1
}

echo "Attempting build..."
if build; then
    echo "=== Build successful! ==="
    exit 0
fi

echo "Build failed. Attempting fixes..."

# Fix 1: Ensure ksp.incremental=false
echo "Checking gradle.properties..."
if ! grep -q "ksp.incremental=false" gradle.properties; then
    echo "ksp.incremental=false" >> gradle.properties
    echo "Added ksp.incremental=false to gradle.properties"
fi

# Fix 2: Clear .gradle cache if FileAlreadyExistsException
if grep -q "FileAlreadyExistsException" build.log 2>/dev/null; then
    echo "Clearing .gradle cache..."
    rm -rf ~/.gradle/caches/
fi

echo "Retrying build..."
if build; then
    echo "=== Build successful after fixes! ==="
    exit 0
fi

echo "=== Build failed. Check errors above. ==="
exit 1
