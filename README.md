# Android Modern App

一个用于创建现代 Android 13+ 应用的 Codex Skill。仓库内置可独立编译的 Kotlin、Jetpack Compose、Material 3 Demo，并提供安全的项目创建和应用图标处理脚本。

## 默认能力

- Android 13（API 33）及以上
- Kotlin + Jetpack Compose + Material 3
- Android 17（API 37）编译和目标版本
- Edge-to-edge 与完整 WindowInsets 适配
- 底部导航栏和稳定版 Navigation 3
- 状态栏、导航栏、刘海/挖孔屏与 IME 适配
- 浅色、深色和动态配色
- 国漫风美少女头像 adaptive icon
- Kotlin DSL、Version Catalog 和 Gradle Wrapper

## 安装为 Codex Skill

```bash
git clone git@github.com:pygojrc/AndroidDemoApp.git \
  "${CODEX_HOME:-$HOME/.codex}/skills/android-modern-app"
```

随后可使用：

```text
使用 $android-modern-app 创建一个记账 APK。
```

## 直接构建 Demo

```bash
cd assets/demo
JAVA_HOME=/home/ms/.jdks/jdk-17.0.19 ./gradlew :app:assembleDebug
```

Debug APK 输出到：

```text
assets/demo/app/build/outputs/apk/debug/app-debug.apk
```

## 从模板创建项目

```bash
uv run scripts/创建项目.py \
  -o /目标/目录 \
  -n "应用名称" \
  -p com.example.app
```

详细规范见 [模板规范](references/模板规范.md) 和 [验收清单](references/验收清单.md)。
