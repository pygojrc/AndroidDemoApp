# 现代 Android 示例

这是 `android-modern-app` Skill 的可编译 Demo 和项目模板。

## 技术版本

- Android 13+，`minSdk = 33`
- `compileSdk = 37`、`targetSdk = 37`
- Android Gradle Plugin 9.3.1
- Gradle 9.7.0
- AGP 内置 Kotlin 2.2.10
- Compose BOM 2026.06.01
- Activity Compose 1.13.0
- Navigation 3 1.1.5
- Java 17

## 构建

```bash
JAVA_HOME=/home/ms/.jdks/jdk-17.0.19 ./gradlew :app:assembleDebug
JAVA_HOME=/home/ms/.jdks/jdk-17.0.19 ./gradlew :app:lintDebug
```

Debug APK：`app/build/outputs/apk/debug/app-debug.apk`。

Demo 只使用 XML 定义 Android 资源和 Manifest，不包含传统 XML 页面布局。
