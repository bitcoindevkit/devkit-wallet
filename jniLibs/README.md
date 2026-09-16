# README

This directory vendors JNA's Android native library, `libjnidispatch.so`, for the ABIs the app ships. **Do not delete these files** — the app builds without them, but crashes at runtime on the first call into bdk.

## Where the files come from

They are copied verbatim out of the JNA aar — same bytes, no patching:

```sh
curl -sLO https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.14.0/jna-5.14.0.aar
unzip -j jna-5.14.0.aar 'jni/arm64-v8a/libjnidispatch.so' -d jniLibs/arm64-v8a
unzip -j jna-5.14.0.aar 'jni/x86_64/libjnidispatch.so'    -d jniLibs/x86_64
```

Keep the version in sync with whatever JNA version `bdk-android` depends on. Check it with:

```sh
grep -A3 'net.java.dev.jna' ~/Library/Caches/JetBrains/Kotlin/.m2.cache/org/bitcoindevkit/bdk-android/<version>/bdk-android-<version>.pom
```
