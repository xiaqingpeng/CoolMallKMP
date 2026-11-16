
import json
import sys

def escape(value):
    return value.replace("\\", "\\\\").replace('"', '\\"')

def generate_region(item, level=0):
    indent = "    " * level
    lines = [
        f'{indent}Region(',
        f'{indent}    id = "{escape(str(item["c"]))}",',
        f'{indent}    name = "{escape(item["n"])}"'
    ]

    children = item.get("ch", [])

    if children:
        lines[-1] += ","
        lines.append(f"{indent}    children = listOf(")

        for index, child in enumerate(children):
            child_code = generate_region(child, level + 2)
            if index < len(children) - 1:
                child_code[-1] += ","
            lines.extend(child_code)

        lines.append(f"{indent}    )")

    lines.append(f"{indent})")
    return lines


def main():
    input_file = sys.argv[1] if len(sys.argv) > 1 else "pca.json"

    with open(input_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    lines = [
        "package com.joker.coolmallkmp.feature.user.data",
        "",
        "import com.joker.coolmallkmp.feature.user.model.Region",
        "",
        "/**",
        " * 全国行政区划数据",
        " * 数据来源：民政部国家地名信息库",
        " */",
        "object RegionData {",
        "    fun getProvinces(): List<Region> {",
        "        return listOf("
    ]

    for index, province in enumerate(data):
        region_lines = generate_region(province, 3)

        if index < len(data) - 1:
            region_lines[-1] += ","

        lines.extend(region_lines)

    lines.extend([
        "        )",
        "    }",
        "}"
    ])

    with open("RegionData.kt", "w", encoding="utf-8") as f:
        f.write("\n".join(lines))

    print(f"生成完成：RegionData.kt")
    print(f"省级行政区数量：{len(data)}")


if __name__ == "__main__":
    main()
