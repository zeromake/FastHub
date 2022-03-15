package com.fastaccess.data.dao.wiki

data class FirebaseWikiConfigModel(
    var sideBarListTitle: String = "a",
    var sideBarUl: String = ".js-wiki-sidebar-toggle-display > ul",
    var sideBarList: String = "li",
    var wikiWrapper: String = "#wiki-wrapper",
    var wikiHeader: String = ".gh-header > h1.gh-header-title",
    var sideBarListLink: String = "href",
    var wikiBody: String = "#wiki-body",
    var wikiSubHeader: String = ".gh-header-meta",
    var wikiContent: String = "#wiki-content"
) {}