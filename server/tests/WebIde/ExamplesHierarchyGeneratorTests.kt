/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.webdemo.server.webIde

import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import org.jetbrains.webdemo.server.Settings
import org.jetbrains.webdemo.common.utils.files.div
import org.jetbrains.webdemo.common.utils.throwable.message
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

RunWith(javaClass<JUnit4>())
public class ExamplesHierarchyGeneratorTests {
    test fun `testdir without order file`() {
        doTest("without order file",
                { listOf("Order file '${it / "order.txt"}' not found.") },
                mapOf("Multi-declarations" to mapOf<String, String>(), "Data classes" to mapOf<String, String>()))
    }

    test fun `testdir which contain some example without descripton`() {
        doTest("examples without descriptions",
                { listOf("Example 'Data classes2' doesn't have description.",
                         "Example 'Multi-declarations2' doesn't have description.") },
                mapOf<String, Map<String, String>>())
    }

    test fun `testdir with order file which doesn't contain some files`() {
        doTest("lost files",
                { listOf("Order file '${it / "order.txt"}' doesn't contain some files.") },
                mapOf("lost example" to mapOf("text" to "description")))
    }

    test fun `order with non existent file`() {
        doTest("order with non existent file",
                { listOf("File '${ it /  "example 1" }' not found.", "File '${ it /  "example 2" }' not found.") },
                mapOf<String, Map<String, String>>())
    }

    fun doTest(innerDir: String, expectedErrors: (root: File) -> List<String>, rawExamples: Map<String, Map<String, String>>) {
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH) / innerDir

        val errors = arrayListOf<String>()
        generateHierarchy(root, rawExamples, { errors.add(it.message) } )

        assertEquals(expectedErrors(root), errors)
    }

    test fun `full test for examples hierarchy`() {
        val helpForExamples = HelpLoader(Settings.HELP_FOR_EXAMPLES_PATH, EXAMPLE_TAG)
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH)
        //fixme it's workaround for avoiding compiling problems with help.content()
        val content = helpForExamples.snapshot().content

        val errors = arrayListOf<String>()
        val examples = generateHierarchy(root, transformRawExamplesListToMap(content), { errors.add(it.message) } )

        val expected = (root / "hierarchy.expected").readLines()
        val expectedErrors = listOf(
                "Example 'Traffic light' doesn't have description.",
                "Example 'Data classes' doesn't have description.",
                "Example 'Data classes2' doesn't have description.",
                "Example 'Multi-declarations' doesn't have description.",
                "Example 'Multi-declarations2' doesn't have description.",
                "Example 'lost example' doesn't have description.",
                "File '${ root / "Canvas/Creatures.kt" }' not found.",
                "File '${ root / "Canvas/Fancy lines.kt" }' not found.",
                "File '${ root / "Canvas/Hello, Kotlin.kt" }' not found.",
                "File '${ root / "Hello, world!/Reading a name from the command line.kt" }' not found.",
                "File '${ root / "order with non existent file/example 1" }' not found.",
                "File '${ root / "order with non existent file/example 2" }' not found.",
                "Order file '${ root / "Hello, world!/order.txt" }' doesn't contain some files.",
                "Order file '${ root / "/order.txt" }' doesn't contain some files.",
                "Order file '${ root / "without order file/order.txt" }' not found.",
                "Order file '${ root / "lost files/order.txt" }' doesn't contain some files.")

        assertEquals(expected.toSortedList(), examples.map { it.toString() }.toSortedList())
        assertEquals(expectedErrors.toSortedList(), errors.toSortedList())
    }
}