#!/usr/bin/env python3
"""从内置 Demo 安全创建新的 Android 项目。"""

from __future__ import annotations

import argparse
import html
import re
import shutil
import subprocess
import sys
from pathlib import Path


TEMPLATE_PACKAGE = "io.github.pygojrc.androidmoderndemo"
TEMPLATE_APP_NAME = "现代 Android 示例"
TEMPLATE_PROJECT_NAME = "AndroidModernDemo"
TEXT_SUFFIXES = {".kt", ".kts", ".xml", ".toml", ".properties", ".md"}
KOTLIN_KEYWORDS = {
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return", "super",
    "this", "throw", "true", "try", "typealias", "typeof", "val", "var", "when", "while",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="从 android-modern-app Demo 创建 Android 项目")
    parser.add_argument("-o", "--output", required=True, help="输出目录")
    parser.add_argument("-n", "--name", required=True, help="应用显示名称")
    parser.add_argument("-p", "--package", required=True, help="namespace，默认同时作为 applicationId")
    parser.add_argument("-a", "--application-id", help="单独指定 applicationId")
    parser.add_argument("-i", "--icon", type=Path, help="可选的头像原图")
    return parser.parse_args()


def validate_package(value: str, label: str) -> str:
    parts = value.split(".")
    identifier = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*$")
    if len(parts) < 2 or any(not identifier.fullmatch(part) for part in parts):
        raise ValueError(f"{label} 必须是至少两段的合法 Java/Kotlin 包名：{value}")
    if any(part in KOTLIN_KEYWORDS for part in parts):
        raise ValueError(f"{label} 不能包含 Kotlin 关键字：{value}")
    return value


def validate_name(value: str) -> str:
    name = value.strip()
    if not name or any(ord(char) < 32 for char in name):
        raise ValueError("应用名称不能为空或包含控制字符")
    return name


def kotlin_string(value: str) -> str:
    return value.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$")


def replace_text_files(root: Path, old: str, new: str) -> None:
    for path in root.rglob("*"):
        if not path.is_file() or path.suffix not in TEXT_SUFFIXES:
            continue
        content = path.read_text(encoding="utf-8")
        if old in content:
            path.write_text(content.replace(old, new), encoding="utf-8")


def move_package_directory(source_root: Path, namespace: str) -> None:
    old_dir = source_root.joinpath(*TEMPLATE_PACKAGE.split("."))
    if not old_dir.exists():
        return
    new_dir = source_root.joinpath(*namespace.split("."))
    new_dir.parent.mkdir(parents=True, exist_ok=True)
    if new_dir.exists():
        raise FileExistsError(f"目标源码目录已存在：{new_dir}")
    shutil.move(str(old_dir), str(new_dir))

    current = old_dir.parent
    while current != source_root and current.exists():
        try:
            current.rmdir()
        except OSError:
            break
        current = current.parent


def update_project_values(target: Path, name: str, namespace: str, application_id: str) -> None:
    replace_text_files(target, TEMPLATE_PACKAGE, namespace)

    build_file = target / "app" / "build.gradle.kts"
    build_text = build_file.read_text(encoding="utf-8")
    expected = f'applicationId = "{namespace}"'
    if expected not in build_text:
        raise RuntimeError("无法定位 applicationId，请检查 Demo 模板是否已变化")
    build_file.write_text(
        build_text.replace(expected, f'applicationId = "{application_id}"', 1),
        encoding="utf-8",
    )

    strings_file = target / "app" / "src" / "main" / "res" / "values" / "strings.xml"
    strings_text = strings_file.read_text(encoding="utf-8")
    strings_file.write_text(
        strings_text.replace(TEMPLATE_APP_NAME, html.escape(name, quote=True)),
        encoding="utf-8",
    )

    readme_file = target / "README.md"
    readme_text = readme_file.read_text(encoding="utf-8")
    readme_file.write_text(
        readme_text.replace(f"# {TEMPLATE_APP_NAME}", f"# {name}", 1),
        encoding="utf-8",
    )

    settings_file = target / "settings.gradle.kts"
    settings_text = settings_file.read_text(encoding="utf-8")
    settings_file.write_text(
        settings_text.replace(TEMPLATE_PROJECT_NAME, kotlin_string(target.name)),
        encoding="utf-8",
    )

    move_package_directory(target / "app" / "src" / "main" / "kotlin", namespace)
    move_package_directory(target / "app" / "src" / "androidTest" / "kotlin", namespace)


def prepare_icon(icon: Path, target: Path) -> None:
    source = icon.expanduser().resolve()
    if not source.is_file():
        raise FileNotFoundError(f"图标原图不存在：{source}")
    uv = shutil.which("uv")
    if uv is None:
        raise RuntimeError("处理图标需要 uv，但当前 PATH 中未找到 uv")
    script = Path(__file__).resolve().with_name("准备应用图标.py")
    resource_dir = target / "app" / "src" / "main" / "res"
    icon_source = target / "icon-source" / "国漫美少女头像.png"
    icon_source.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, icon_source)
    subprocess.run(
        [uv, "run", "--script", str(script), "-i", str(source), "-o", str(resource_dir)],
        check=True,
    )


def main() -> int:
    args = parse_args()
    try:
        name = validate_name(args.name)
        namespace = validate_package(args.package, "namespace")
        application_id = validate_package(args.application_id or namespace, "applicationId")

        template = (Path(__file__).resolve().parent.parent / "assets" / "demo").resolve()
        target = Path(args.output).expanduser().resolve()
        if not template.is_dir():
            raise FileNotFoundError(f"内置 Demo 不存在：{template}")
        if target == template or template in target.parents:
            raise ValueError("输出目录不能是内置 Demo 或其子目录")
        if target.exists() and any(target.iterdir()):
            raise FileExistsError(f"目标目录非空，已拒绝覆盖：{target}")

        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copytree(
            template,
            target,
            dirs_exist_ok=target.exists(),
            ignore=shutil.ignore_patterns(".gradle", ".idea", "build", "local.properties"),
        )
        update_project_values(target, name, namespace, application_id)
        if args.icon:
            prepare_icon(args.icon, target)

        print(f"项目已创建：{target}")
        print(f"namespace：{namespace}")
        print(f"applicationId：{application_id}")
        print("构建命令：./gradlew :app:assembleDebug")
        print("APK：app/build/outputs/apk/debug/app-debug.apk")
        return 0
    except (FileNotFoundError, FileExistsError, RuntimeError, ValueError, subprocess.CalledProcessError) as exc:
        print(f"创建失败：{exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
