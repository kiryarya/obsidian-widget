package com.example.obsidiantaskwidget

import org.junit.Test
import org.junit.Assert.*

class YamlParserTest {

    @Test
    fun parseTask_validFrontmatter_returnsTask() {
        val content = """
            ---
            title: My Task
            status: todo
            due: 2023-10-31
            priority: high
            ---
            # Some notes
        """.trimIndent()

        val task = YamlParser.parseTask("task.md", content)
        assertNotNull(task)
        assertEquals("My Task", task?.title)
        assertEquals("todo", task?.status)
        assertEquals("2023-10-31", task?.due)
        assertEquals("high", task?.priority)
        assertEquals("task.md", task?.filename)
    }

    @Test
    fun parseTask_missingTitle_usesFilename() {
        val content = """
            ---
            status: in-progress
            ---
        """.trimIndent()

        val task = YamlParser.parseTask("MyFile.md", content)
        assertNotNull(task)
        assertEquals("MyFile", task?.title)
        assertEquals("in-progress", task?.status)
    }

    @Test
    fun parseTask_noFrontmatter_returnsNull() {
        val content = """
            # Just a note
            No frontmatter here
        """.trimIndent()

        val task = YamlParser.parseTask("note.md", content)
        assertNull(task)
    }

    @Test
    fun parseTask_quotedValues_returnsCleanValues() {
        val content = """
            ---
            status: "todo"
            title: 'Quoted Title'
            ---
        """.trimIndent()

        val task = YamlParser.parseTask("test.md", content)
        assertNotNull(task)
        assertEquals("todo", task?.status)
        assertEquals("Quoted Title", task?.title)
    }
}
