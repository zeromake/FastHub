package com.fastaccess.ui.widgets.contributions

/**
 * Copyright 2022 zeromake<a390720046@qq.com>
 * All right reserved.
 */
class ContributionsHtmlProvider {
    companion object {
        private const val LEVEL_STRING = "data-level=\""
        private const val DATA_STRING = "data-count=\""
        private const val DATE_STRING = "data-date=\""
        private const val COMMON_ENDING = "\""
        private const val DATE_DELIMITER = "-"

        @JvmStatic
        fun getContributions(string: String?): List<ContributionsDay> {
            val contributions = ArrayList<ContributionsDay>()
            if (string.isNullOrEmpty()) return contributions
            var dataPos = -1
            var datePos = -1
            var levelPos = -1
            while (true) {
                dataPos = string.indexOf(DATA_STRING, dataPos + 1)
                datePos = string.indexOf(DATE_STRING, datePos + 1)
                levelPos = string.indexOf(LEVEL_STRING, levelPos + 1)

                if (dataPos == -1 || datePos == -1 || levelPos == -1) break
                // data-count parse
                val dataStartPos = dataPos + DATA_STRING.length
                val dataEndPos = string.indexOf(COMMON_ENDING, dataStartPos)
                if (dataEndPos == -1) break
                val dataString = string.substring(dataStartPos, dataEndPos)
                val data = dataString.toInt()

                // date parse
                val dateStartPos = datePos + DATE_STRING.length
                val dateEndPos = string.indexOf(COMMON_ENDING, dateStartPos)
                if (dateEndPos == -1) break
                val dateString = string.substring(dateStartPos, dateEndPos)
                val dates = dateString.split(DATE_DELIMITER)
                if (dates.size < 3) break

                val year = dates[0].toInt()
                val month = dates[1].toInt()
                val day = dates[2].toInt()

                // level parse
                val levelStart = levelPos + LEVEL_STRING.length
                val levelEnd = string.indexOf(COMMON_ENDING, levelStart)
                if (levelEnd == -1) break
                val levelString = string.substring(levelStart, levelEnd)
                val level = levelString.toInt()
                contributions.add(
                    ContributionsDay(
                        year,
                        month,
                        day,
                        level,
                        data
                    )
                )
            }
            return contributions
        }
    }
}