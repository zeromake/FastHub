package com.prettifier.pretty.helper

object HtmlHelper {
    const val HTML_HEADER = """<!DOCTYPE html>
<html lang="en">
"""
    const val HEAD_HEADER = """<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
    """
    const val HEAD_BOTTOM = """
</head>
"""
    const val BODY_HEADER = """
<body>"""

    const val BODY_BOTTOM = """
<body>"""
    const val HTML_BOTTOM = """
</html>"""


    const val MEAT_VIEWPORT_DEFAULT = """
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
"""
    const val MEAT_VIEWPORT_PRETTIFY ="""
    <meta name="viewport" content="width=device-width, height=device-height, initial-scale=.5,user-scalable=yes"/>
"""
}