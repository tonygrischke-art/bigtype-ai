package com.aetheria.bigtype.keyboard

data class KeyDef(
    val label: String,
    val value: String,
    val longPressValue: String? = null,
    val isSpecial: Boolean = false
)

val NumberRow = listOf(
    KeyDef("1", "1", "!"), KeyDef("2", "2", "@"), KeyDef("3", "3", "#"),
    KeyDef("4", "4", "$"), KeyDef("5", "5", "%"), KeyDef("6", "6", "^"),
    KeyDef("7", "7", "&"), KeyDef("8", "8", "*"), KeyDef("9", "9", "("),
    KeyDef("0", "0", ")")
)

val QwertyRows = listOf(
    listOf(
        KeyDef("q","q","1"), KeyDef("w","w","2"), KeyDef("e","e","3"),
        KeyDef("r","r","4"), KeyDef("t","t","5"), KeyDef("y","y","6"),
        KeyDef("u","u","7"), KeyDef("i","i","8"), KeyDef("o","o","9"),
        KeyDef("p","p","0")
    ),
    listOf(
        KeyDef("a","a","@"), KeyDef("s","s","#"), KeyDef("d","d","$"),
        KeyDef("f","f","_"), KeyDef("g","g","-"), KeyDef("h","h","+"),
        KeyDef("j","j","="), KeyDef("k","k","("), KeyDef("l","l",")")
    ),
    listOf(
        KeyDef("⇧","",isSpecial=true), KeyDef("z","z","["), KeyDef("x","x","]"),
        KeyDef("c","c","{"), KeyDef("v","v","}"), KeyDef("b","b","<"),
        KeyDef("n","n",">"), KeyDef("m","m","?"), KeyDef("⌫","",isSpecial=true)
    )
)

val HexRows = listOf(
    listOf(KeyDef("q","q"), KeyDef("w","w"), KeyDef("e","e"), KeyDef("r","r"), KeyDef("t","t"), KeyDef("y","y")),
    listOf(KeyDef("u","u"), KeyDef("i","i"), KeyDef("o","o"), KeyDef("p","p"), KeyDef("a","a"), KeyDef("s","s")),
    listOf(KeyDef("d","d"), KeyDef("f","f"), KeyDef("g","g"), KeyDef("h","h"), KeyDef("j","j"), KeyDef("k","k")),
    listOf(KeyDef("l","l"), KeyDef("z","z"), KeyDef("x","x"), KeyDef("c","c"), KeyDef("v","v"), KeyDef("b","b")),
    listOf(KeyDef("n","n"), KeyDef("m","m"), KeyDef(",",","), KeyDef(".",".")  , KeyDef("?","?"), KeyDef("!","!"))
)