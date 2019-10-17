package regex


fun main() {
    val xx = Regex("^\\d{3}? \\d{4} \\d{4}?$").matches("139 1650 793")
    val xx2 = "1393 1650 7932222".replace("/(\\d{3}) (\\d{4}) (\\d{4})/", "\$1/\$2/\$3")
    println(xx)
    println(xx2)
}
