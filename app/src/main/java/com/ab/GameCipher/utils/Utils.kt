package com.ab.GameCipher.utils

val appropriateEncryptedCharList: List<Char> = ('a'..'z').toList().shuffled()
val appropriateDecryptedCharList: List<Char> = ('A'..'Z').toList()

fun decipherUsingMap(encrypted: String, decipherStateMap: Map<Char, Char>): String {
    val decrypted: StringBuilder = StringBuilder(encrypted.length)
    for (c in encrypted) {
        decrypted.append(decipherStateMap[c] ?: c)
    }
    return decrypted.toString()
}