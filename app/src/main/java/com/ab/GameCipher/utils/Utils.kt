package com.ab.GameCipher.utils

val appropriateEncryptedCharList: List<Char> = ('a'..'z').toList().shuffled()
val appropriateDecryptedCharList: List<Char> = ('A'..'Z').toList()

fun decipherUsingMap(encrypted: String, cipherStateMap: Map<Char, Char>, decipherStateMap: Map<Char, Char>): String {
    val decrypted: StringBuilder = StringBuilder(encrypted.length)
    println(cipherStateMap)
    for (c in encrypted) {
        decrypted.append(decipherStateMap[cipherStateMap[c] ?: c] ?: c)
    }
    return decrypted.toString()
}