package com.ab.GameCipher.utils

val appropriateEncryptedCharList: List<Char> = ('a'..'z').toList().shuffled()
val appropriateDecryptedCharList: List<Char> = ('A'..'Z').toList()

fun decipherUsingMap(encrypted: String, cipherStateMap: Map<Char, Char>, decipherStateMap: Map<Char, Char>): String {
    val decrypted: StringBuilder = StringBuilder(encrypted.length)
    var temp: Char
    for (c in encrypted) {
        temp = cipherStateMap[c] ?: c
        decrypted.append(decipherStateMap[temp] ?: temp)
    }
    return decrypted.toString()
}