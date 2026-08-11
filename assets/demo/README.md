# 现代 Android 示例

这是 `android-modern-app` Skill 的可编译 Demo 和项目模板。它提供 Android 13+ Compose 应用常用的最小 UI 骨架，生成新项目后可以直接替换页面内容和导航项。

## Demo 内容

- Kotlin、Jetpack Compose、Material 3 和 Navigation 3
- Edge-to-edge，以及状态栏、导航栏、刘海/挖孔屏和 IME Insets 处理
- “首页”“组件”“设置”三个底部导航页面
- 与底部导航共享状态的左侧 `ModalNavigationDrawer`
- Snackbar、AlertDialog、ModalBottomSheet 和 DropdownMenu
- Checkbox、Switch、Slider、TextField 和 IME 滚动示例
- 右下角可展开的 Floating Action Button
- 只能在应用安全内容区域内移动的可拖动悬浮框
- 浅色、深色和 Android 动态配色
- 国漫风美少女头像 adaptive、round 和 themed icon

应用内悬浮框不需要 `SYSTEM_ALERT_WINDOW` 权限，也不会启动后台 Service。跨应用系统悬浮窗涉及特殊权限、`WindowManager` 和 Service 生命周期，不属于默认模板；业务明确需要时再单独添加。

## 页面操作

- 点击顶部菜单按钮打开左侧侧边菜单。
- 使用底部导航或侧边菜单切换三个一级页面。
- 在“组件”页面测试反馈、弹层、选择控件和输入框。
- 点击右下角 FAB 展开 Snackbar 与悬浮框快捷操作。
- 拖动悬浮框检查边界约束，点击关闭按钮隐藏。
- 在输入框中打开软键盘，检查内容和底部导航是否被遮挡。

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
- Demo 版本 0.1.1

## 构建

```bash
ANDROID_HOME=/home/ms/Android/Sdk JAVA_HOME=/home/ms/.jdks/jdk-17.0.19 ./gradlew :app:assembleDebug
ANDROID_HOME=/home/ms/Android/Sdk JAVA_HOME=/home/ms/.jdks/jdk-17.0.19 ./gradlew :app:lintDebug
```

Debug APK：`app/build/outputs/apk/debug/app-debug.apk`。

Demo 只使用 XML 定义 Android 资源和 Manifest，不包含传统 XML 页面布局，也不依赖 Hilt、Room、网络库或第三方 UI 框架。

## Insets 约定

- 根 `Scaffold` 统一放置顶部栏、底部栏、Snackbar 和 FAB。
- 页面应用并消费 `Scaffold` 的 `innerPadding`。
- 抽屉和底部弹层使用各自的 Material 3 Insets 边界。
- 组件页只在滚动内容层应用 IME Insets。
- 可拖动悬浮框位于页面内容区域内，不覆盖系统栏和底部导航。

构建和 lint 只能证明编译验证通过；安装、旋转、软键盘和手势行为仍需在 Android 13+ 模拟器或真机上实际检查。
