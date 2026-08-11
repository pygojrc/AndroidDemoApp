#!/usr/bin/env -S uv run --script
# /// script
# requires-python = ">=3.11"
# dependencies = [
#   "Pillow==12.0.0",
# ]
# ///
"""将正方形头像原图转换为 Android adaptive icon 前景资源。"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

from PIL import Image, ImageOps


DENSITIES = {
    "mdpi": 108,
    "hdpi": 162,
    "xhdpi": 216,
    "xxhdpi": 324,
    "xxxhdpi": 432,
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="准备 Android adaptive icon 前景资源")
    parser.add_argument("-i", "--input", required=True, type=Path, help="输入头像原图")
    parser.add_argument("-o", "--output", required=True, type=Path, help="Android res 目录")
    parser.add_argument("-b", "--background", default="#F4C6DF", help="adaptive icon 背景色")
    return parser.parse_args()


def validate_color(value: str) -> str:
    if not re.fullmatch(r"#[0-9A-Fa-f]{6}", value):
        raise ValueError("背景色必须使用 #RRGGBB 格式")
    return value.upper()


def load_square(path: Path) -> Image.Image:
    if not path.is_file():
        raise FileNotFoundError(f"输入图片不存在：{path}")
    with Image.open(path) as source:
        image = ImageOps.exif_transpose(source).convert("RGB")
    width, height = image.size
    if min(width, height) < 512:
        raise ValueError(f"输入图片至少需要 512×512，当前为 {width}×{height}")
    edge = min(width, height)
    left = (width - edge) // 2
    top = (height - edge) // 2
    return image.crop((left, top, left + edge, top + edge))


def write_foregrounds(image: Image.Image, output: Path) -> None:
    for density, size in DENSITIES.items():
        directory = output / f"mipmap-{density}"
        directory.mkdir(parents=True, exist_ok=True)
        resized = image.resize((size, size), Image.Resampling.LANCZOS)
        resized.save(directory / "ic_launcher_foreground.webp", "WEBP", quality=95, method=6)


def update_background(output: Path, color: str) -> None:
    colors_file = output / "values" / "colors.xml"
    if not colors_file.is_file():
        raise FileNotFoundError(f"颜色资源不存在：{colors_file}")
    content = colors_file.read_text(encoding="utf-8")
    pattern = r'(<color name="launcher_icon_background">)#[0-9A-Fa-f]{6}(</color>)'
    updated, count = re.subn(pattern, rf"\g<1>{color}\g<2>", content, count=1)
    if count != 1:
        raise RuntimeError("无法定位 launcher_icon_background 颜色资源")
    colors_file.write_text(updated, encoding="utf-8")


def main() -> int:
    args = parse_args()
    try:
        source = load_square(args.input.expanduser().resolve())
        output = args.output.expanduser().resolve()
        output.mkdir(parents=True, exist_ok=True)
        color = validate_color(args.background)
        write_foregrounds(source, output)
        update_background(output, color)
        print(f"应用图标资源已写入：{output}")
        return 0
    except (FileNotFoundError, RuntimeError, ValueError, OSError) as exc:
        print(f"图标处理失败：{exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
