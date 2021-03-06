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

import java.io.File
import kotlin.test.assertEquals
import org.jetbrains.webdemo.common.utils.files.div
import org.jetbrains.webdemo.common.utils.throwable.message
import org.jetbrains.webdemo.server.Settings
import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

RunWith(javaClass<JUnit4>())
public class ExamplesLoaderTests {
    test fun `duplicated examples`() {
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH + "WithDuplicate")
        val errors = arrayListOf<String>()
        loadExamples(root, mapOf("example" to mapOf<String, String>()), { errors.add(it.message) } )

        val expectedErrors = listOf("Duplicated example name 'example'.")

        assertEquals(expectedErrors, errors)
    }

    test fun `full test for load examples`() {
        //todo split this test?
        val helpForExamples = HelpLoader(Settings.HELP_FOR_EXAMPLES_PATH, EXAMPLE_TAG)
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH)
        //fixme it's workaround for avoiding compiling problems with help.content()
        val content = helpForExamples.snapshot().content

        val errors = arrayListOf<String>()
        val examples = loadExamples(root, transformRawExamplesListToMap(content), { errors.add(it.message) } )

        //fixme fix dependence to targets
        val expected = (root / "examplesList.expected").readText().split("\n---\n").toList()
        val errorsExpected = (root / "examplesListErrors.expected").readLines()

        assertEquals(expected.toSortedList(), examples.iterator().map { it.toString() }.toList().toSortedList())
        assertEquals(errorsExpected.toSortedList(), errors.toSortedList())
    }
}