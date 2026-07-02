package com.jetpackduba.gitnuro.domain

import com.jetpackduba.gitnuro.domain.models.ConflictBlock
import com.jetpackduba.gitnuro.domain.models.ConflictChoice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConflictParserTest {

    private val twoWay = """
        line 1
        <<<<<<< HEAD
        our change
        =======
        their change
        >>>>>>> feature
        line 5
    """.trimIndent()

    private val diff3 = """
        start
        <<<<<<< HEAD
        our line
        ||||||| base
        original line
        =======
        their line
        >>>>>>> other
        end
    """.trimIndent()

    @Test
    fun `parses a simple two-way conflict into unchanged and conflict blocks`() {
        val file = ConflictParser.parse("a.txt", twoWay)

        assertEquals(3, file.blocks.size)
        assertEquals(1, file.conflictCount)

        val conflict = file.blocks[1] as ConflictBlock.Conflict
        assertEquals(listOf("our change"), conflict.ours)
        assertEquals(listOf("their change"), conflict.theirs)
    }

    @Test
    fun `drops the base section of a diff3 conflict`() {
        val file = ConflictParser.parse("a.txt", diff3)
        val conflict = file.blocks.filterIsInstance<ConflictBlock.Conflict>().single()

        assertEquals(listOf("our line"), conflict.ours)
        assertEquals(listOf("their line"), conflict.theirs)
    }

    @Test
    fun `resolving as OURS keeps our lines and drops the markers`() {
        val file = ConflictParser.parse("a.txt", twoWay)
        val resolved = ConflictParser.resolve(file, mapOf(0 to ConflictChoice.OURS))

        assertEquals("line 1\nour change\nline 5", resolved)
        assertFalse(ConflictParser.hasConflictMarkers(resolved))
    }

    @Test
    fun `resolving as THEIRS keeps their lines`() {
        val file = ConflictParser.parse("a.txt", twoWay)
        val resolved = ConflictParser.resolve(file, mapOf(0 to ConflictChoice.THEIRS))

        assertEquals("line 1\ntheir change\nline 5", resolved)
    }

    @Test
    fun `resolving as OURS_THEN_THEIRS keeps both sides in order`() {
        val file = ConflictParser.parse("a.txt", twoWay)
        val resolved = ConflictParser.resolve(file, mapOf(0 to ConflictChoice.OURS_THEN_THEIRS))

        assertEquals("line 1\nour change\ntheir change\nline 5", resolved)
    }

    @Test
    fun `conflict with no explicit choice defaults to ours`() {
        val file = ConflictParser.parse("a.txt", twoWay)
        val resolved = ConflictParser.resolve(file, emptyMap())

        assertEquals("line 1\nour change\nline 5", resolved)
    }

    @Test
    fun `content without markers round-trips unchanged`() {
        val plain = "a\nb\nc"
        val file = ConflictParser.parse("a.txt", plain)

        assertEquals(0, file.conflictCount)
        assertFalse(ConflictParser.hasConflictMarkers(plain))
        assertEquals(plain, ConflictParser.resolve(file, emptyMap()))
    }

    @Test
    fun `detects conflict markers`() {
        assertTrue(ConflictParser.hasConflictMarkers(twoWay))
    }

    @Test
    fun `handles multiple conflicts in one file independently`() {
        val content = """
            top
            <<<<<<< HEAD
            ours A
            =======
            theirs A
            >>>>>>> x
            middle
            <<<<<<< HEAD
            ours B
            =======
            theirs B
            >>>>>>> x
            bottom
        """.trimIndent()

        val file = ConflictParser.parse("a.txt", content)
        assertEquals(2, file.conflictCount)

        val resolved = ConflictParser.resolve(
            file,
            mapOf(0 to ConflictChoice.OURS, 1 to ConflictChoice.THEIRS),
        )
        assertEquals("top\nours A\nmiddle\ntheirs B\nbottom", resolved)
    }
}
