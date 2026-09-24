package com.aetheria.bigtype.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SymSpellTest {
    private val sym = SymSpell(listOf("hello", "world", "keyboard", "love", "test"))

    @Test
    fun `exact match returns zero distance`() {
        val res = sym.lookup("hello")
        assertEquals("hello", res.first().word)
        assertEquals(0, res.first().distance)
    }

    @Test
    fun `single typo corrected`() {
        val res = sym.lookup("helo")
        assertEquals("hello", res.first().word)
    }

    @Test
    fun `far off input returns empty or low ranked`() {
        val res = sym.lookup("zzzqqq")
        assertTrue(res.isEmpty())
    }
}

class SwipeDecoderTest {
    private val decoder = SwipeDecoder(listOf("the", "this", "test", "toast"))

    @Test
    fun `raw path fallback when no lexicon match makes sense`() {
        decoder.onKey("x"); decoder.onKey("q"); decoder.onKey("z")
        // nothing meaningful - best guess should at least return something
        assertTrue(decoder.decodeBest().isNotBlank())
    }

    @Test
    fun `prefix glide favors matching word`() {
        decoder.onKey("t"); decoder.onKey("e")
        val top = decoder.decode(1).first()
        assertTrue(top == "test" || top == "te")
    }

    @Test
    fun `reset clears path`() {
        decoder.onKey("t")
        decoder.reset()
        assertEquals("", decoder.decodeBest())
    }
}
