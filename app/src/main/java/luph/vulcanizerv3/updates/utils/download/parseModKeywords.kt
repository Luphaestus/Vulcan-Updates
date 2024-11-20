package luph.vulcanizerv3.updates.utils.download

import luph.vulcanizerv3.updates.data.ModDetails

fun parseModKeywords(modDetails: List<ModDetails>?): MutableMap<String, MutableList<ModDetails>> {
    if (modDetails.isNullOrEmpty()) return mutableMapOf()
    val keywordMap = mutableMapOf<String, MutableList<ModDetails>>()
    modDetails.forEach { mod ->
        val modKeywords = mod.keywords
        modKeywords.forEach { keyword ->
            if (keywordMap.containsKey(keyword)) {
                keywordMap[keyword]!!.add(mod)
            } else {
                keywordMap[keyword] = mutableListOf(mod)
            }
        }
    }
    return keywordMap
}