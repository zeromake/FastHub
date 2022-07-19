#!/bin/python3

import re
from typing import Dict
from io import StringIO

classRe = r"(public|private|protected)? *class +([\w_0-9]+)( +extends +([\w_0-9]+))?"
bindRe = r" *(@[\w_0-9]+ +)*@(BindView|BindColor|BindString)\((R\.[\w_0-9]+\.[\w_0-9]+)\) +((protected|public|private) +)?([\w_0-9]+) +([\w_0-9]+) *;"
superRe = r"( *)super\( *([\w_0-9]+),?.*"
importRe = r" *import +butterknife\.(BindView|BindColor|BindString);"
bindClickRe = r"(public|protected)? *@OnClick\((R\.id\.[\w_0-9]+)\) +void +([\w_0-9]+)\((View +[\w_0-9]+)?\) +\{?"
butterKnifeBindRe = r"( *)ButterKnife.bind\( *([\w_0-9]+) *\) *;"

def class_handle(output, matchGroup: re.Match[str], meta: Dict) -> bool:
    if meta.get("className", None):
        return True
    meta["className"] = matchGroup.group(2)
    meta["type"] = matchGroup.group(4)
    return True

def bind_handle(output, matchGroup: re.Match[str], meta: Dict) -> bool:
    meta["binds"].append(matchGroup)
    line = re.sub(r"@(BindView|BindColor|BindString)(\(R\.\w+\.\w+\)) +", "", matchGroup.string)
    output.write(line)
    return False

def super_handle(output, matchGroup: re.Match[str], meta: Dict) -> bool:
    if meta["type"] != "ViewHolder" and meta["type"] != "BaseViewHolder":
        return True
    output.write(matchGroup.string)
    viewField = matchGroup.group(2)
    tab = matchGroup.group(1)
    res_line = "\n{}Context $$context = {}.getContext();\n{}Resources $$res = $$context.getResources();\n\n".format(tab, viewField, tab)
    res = False
    def writeRes():
        output.write(res_line)
        meta["import"].append("import android.content.Context;\n")
        meta["import"].append("import android.content.res.Resources;\n")
        meta["import"].append("import androidx.core.content.ContextCompat;\n")
    for match in meta["binds"]:
        t = match.group(2)
        if t == "BindView":
            output.write("{}this.{} = {}.findViewById({});\n".format(tab, match.group(7), viewField, match.group(3)))
        elif t == "BindColor":
            if not res:
                res = True
                writeRes()
            output.write("{}this.{} = ContextCompat.getColor($$context, {});\n".format(tab, match.group(7), match.group(3)))
        elif t == "BindString":
            if not res:
                res = True
                writeRes()
            output.write("{}this.{} = $$res.getString({});\n".format(tab, match.group(7), match.group(3)))
    
    for click in meta["click"]:
        print(click.groups())

    return False

def import_handle(output, matchGroup: re.Match[str], meta: Dict) -> bool:
    return False

def bind_click_handle(output, matchGroup: re.Match[str], meta: Dict) -> bool:
    line = re.sub(r"@OnClick(\(R\.id\.\[w_0-9]+\)) +", "", matchGroup.string)
    output.write(line)
    meta["click"].append(matchGroup)
    return False

matchs = (
    (
        classRe,
        class_handle,
    ),
    (
        bindRe,
        bind_handle,
    ),
    (
        superRe,
        super_handle,
    ),
    (
        importRe,
        import_handle,
    ),
    (
        bindClickRe,
        bind_click_handle,
    )
)




def convert(input, output):
    with open(input, 'r', encoding='utf-8') as input_file, open(output, 'w+', encoding='utf-8', newline='') as output_file, StringIO() as mem:
        meta = {
            "className": None,
            "binds": [],
            "type": "ViewHolder",
            "import": [],
            "click": [],
        }
        header = True
        for line in input_file:
            if header and not line.startswith("package ") and line.strip() != "":
                header = False
            if header:
                outIo = output_file
            else:
                outIo = mem
            write = True
            for match in matchs:
                matchGroup = re.match(match[0], line)
                if matchGroup:
                    write = match[1](outIo, matchGroup, meta)
                    break
            if write:
                outIo.write(line)
        for line in meta["import"]:
            output_file.write(line)
        mem.seek(0)
        for line in mem:
            output_file.write(line)
        


if __name__ == "__main__":
    convert(
        "D:\\project\\FastHub\\app\\src\\main\\java\\com\\fastaccess\\ui\\adapter\\viewholder\\PinnedReposViewHolder.java",
        ".\\PinnedReposViewHolder.java",
    )