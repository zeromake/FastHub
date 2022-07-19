package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R

/**
 * Created by Kosh on 17 Feb 2017, 7:45 PM
 */
enum class FilesType(@get:DrawableRes @param:DrawableRes val icon: Int = R.drawable.ic_file_document) {
    file(R.drawable.ic_file_document), dir(R.drawable.ic_folder), blob(R.drawable.ic_file_document), tree(
        R.drawable.ic_folder
    ),
    symlink(R.drawable.ic_submodule);
}