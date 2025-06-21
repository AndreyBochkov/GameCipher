package com.ab.GameCipher.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

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

fun serialize(list: List<Map<Char, Char>>): String {
    val temp = list.joinToString(";") { map ->
        map.entries.joinToString(":") { (key, value) ->
            "${key}${value}"
        }
    }
    return temp
}

fun deserialize(serialized: String): List<Map<Char, Char>> {
    if (serialized.isEmpty()) return emptyList()
    return serialized.split(";").map { mapStr ->
        if (mapStr.isEmpty()) emptyMap()
        else mapStr.split(":").associate { pairStr ->
            pairStr[0] to pairStr[1]
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

fun getFromClipboard(context: Context): String {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = clipboard.primaryClip
    return if (clip != null && clip.itemCount > 0) clip.getItemAt(0).text.toString()
        else ""
}