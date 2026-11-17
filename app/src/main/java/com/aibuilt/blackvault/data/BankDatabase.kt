package com.aibuilt.blackvault.data

data class BankInfo(val name: String, val country: String, val code: String)

data class IndianBank(val prefix: String, val name: String)

data class UsBank(val name: String, val routingNumbers: List<String>)

object BankDatabase {

    val indianBanks: List<IndianBank> = listOf(
        IndianBank("SBIN", "State Bank of India"),
        IndianBank("HDFC", "HDFC Bank"),
        IndianBank("ICIC", "ICICI Bank"),
        IndianBank("UTIB", "Axis Bank"),
        IndianBank("KKBK", "Kotak Mahindra Bank"),
        IndianBank("PUNB", "Punjab National Bank"),
        IndianBank("BARB", "Bank of Baroda"),
        IndianBank("CNRB", "Canara Bank"),
        IndianBank("UBIN", "Union Bank of India"),
        IndianBank("IDFB", "IDFC First Bank"),
        IndianBank("YESB", "Yes Bank"),
        IndianBank("IOBA", "Indian Overseas Bank"),
        IndianBank("IDIB", "Indian Bank"),
        IndianBank("BKID", "Bank of India"),
        IndianBank("MAHB", "Bank of Maharashtra")
    )

    val usBanks: List<UsBank> = listOf(
        UsBank("JPMorgan Chase", listOf("021000021", "021202337", "322271627", "071000013")),
        UsBank("Bank of America", listOf("026009593", "121000358", "111000025")),
        UsBank("Wells Fargo", listOf("121000248", "111900659", "121042882")),
        UsBank("Citibank", listOf("021000089", "321171184", "021272655")),
        UsBank("US Bank", listOf("091000022", "122105155", "081000210")),
        UsBank("PNC", listOf("043000096", "031000053", "083000108")),
        UsBank("Goldman Sachs", listOf("026014051", "124085204", "021001088")),
        UsBank("Capital One", listOf("051405515", "031176110", "056073502")),
        UsBank("TD Bank", listOf("031101266", "011600033", "211370545")),
        UsBank("Truist", listOf("053101121", "061000104", "051000020")),
        UsBank("HSBC USA", listOf("021001088", "022000020", "021001208")),
        UsBank("Ally", listOf("124003116", "053101121", "124085204")),
        UsBank("Charles Schwab", listOf("121202211", "021000089", "121000248")),
        UsBank("American Express", listOf("124085204", "096016765", "021000021"))
    )

    val sweepOrder: List<String> = listOf(
        "JPMorgan Chase",
        "Bank of America",
        "Wells Fargo",
        "Citibank",
        "US Bank",
        "PNC",
        "Goldman Sachs",
        "Capital One",
        "TD Bank",
        "Truist",
        "HSBC USA",
        "Ally",
        "Charles Schwab",
        "American Express"
    )

    fun detect(raw: String): BankInfo? {
        val query = raw.trim().uppercase()
        if (query.isEmpty()) return null

        if (query.all { it.isDigit() }) {
            if (query.length < 4) return null
            val exact = usBanks.firstOrNull { bank -> bank.routingNumbers.any { it == query } }
            if (exact != null) return BankInfo(exact.name, "UNITED STATES", query)
            val prefixMatch = usBanks.firstOrNull { bank ->
                bank.routingNumbers.any { it.startsWith(query) || query.startsWith(it) }
            }
            return prefixMatch?.let { BankInfo(it.name, "UNITED STATES", query) }
        }

        val letters = query.filter { it.isLetter() }
        if (letters.length < 4) return null
        val prefix = letters.take(4)
        val bank = indianBanks.firstOrNull { it.prefix == prefix }
        return bank?.let { BankInfo(it.name, "INDIA", query) }
    }

    fun isIndianIfsc(value: String): Boolean = Regex("^[A-Z]{4}0[A-Z0-9]{6}$").matches(value)

    fun isUsRouting(value: String): Boolean = Regex("^\\d{9}$").matches(value)

    fun isValidCode(value: String): Boolean = isIndianIfsc(value) || isUsRouting(value)
}
