---
name: android-modern-app
description: 基于内置可编译 Demo 创建或初始化 Android 13+ Kotlin Compose APK。用户要求新建 Android App、APK、Compose 工程、底部导航应用或需要处理 edge-to-edge、WindowInsets、IME、深色模式及启动图标时使用。
---

# Modern Android App

创建项目时复用 `assets/demo/`，不要从零重建 Gradle、主题、导航或 Insets 基础设施。

## 执行

1. 读取目标项目的 `AGENTS.md`、工作区规则和现有文件；非空目标目录不得静默覆盖。
2. 读取 `references/模板规范.md`，运行 `scripts/创建项目.py` 创建基础项目。
3. 根据需求替换示例页面和底部导航项；保留 edge-to-edge、Insets、主题和 Navigation 3 基础设施。
4. 默认生成与应用主题匹配的国漫风美少女头像，并运行 `scripts/准备应用图标.py` 生成 adaptive icon；无法生成时保留模板图标并明确说明。
5. 不默认加入 Hilt、Room、网络层、多模块、Clean Architecture、权限或发布签名；只按实际需求扩展。
6. 运行 `:app:assembleDebug` 和 `:app:lintDebug`，按 `references/验收清单.md` 复核结果。
7. 只有在模拟器或真机上实际安装并操作后，才能报告运行验证通过；纯构建结果必须标为编译验证。

## 固定约束

- `minSdk` 保持 33，除非用户明确要求修改。
- 使用 Kotlin、Jetpack Compose、Material 3 和 Kotlin DSL，不创建 `res/layout/` 页面。
- 每条系统边只由一个组件消费 Insets，禁止重复叠加系统栏 padding。
- 页面内容不得被状态栏、导航栏、底部导航或 IME 遮挡。
- 依赖使用固定版本，不使用动态版本。
- Debug APK 使用 Android 默认 Debug Key；不在仓库保存发布密钥。
