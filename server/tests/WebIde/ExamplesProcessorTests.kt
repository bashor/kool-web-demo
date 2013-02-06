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

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.failsWith
import org.jetbrains.webdemo.common.*
import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

RunWith(javaClass<JUnit4>())
public class ExamplesProcessorTests {
    test fun `call with invalid path`() {
        failsWith(javaClass<InternalError>()) {
            ExamplesProcessor(versionedContentMock, "invalid path") { (file, map) -> } .snapshot()
        }
    }

    test fun `check processor calling`() {
        var called = false
        ExamplesProcessor(versionedContentMock) {(file, map) -> called = true} .snapshot()

        assertTrue(called)
    }

    test fun `check version`() {
        val exampleProcessor = ExamplesProcessor(versionedContentMock) {(file, map) -> }
        var i = versionedContentMock.version() + 1

        assertEquals(i++, exampleProcessor.version());
        assertEquals(i, exampleProcessor.version());
    }

    private val versionedContentMock = object : VersionedContent<List<Map<String, String>>> {
        private var i: Long = 0
        override fun version(): Long = i++
        override fun snapshot(): ContentSnapshot<List<Map<String, String>>> = ContentSnapshot(version(), arrayListOf())
    }
}

RunWith(javaClass<JUnit4>())
public class TransformRawExamplesListToMapTests {
    test fun `for empty list`() {
        assertTrue(transformRawExamplesListToMap(listOf<Map<String, String>>()).empty)
    }

    test fun `skiping items without name`() {
        val input = arrayListOf<Map<String, String>>(
          /*1*/ mapOf(NAME_PROP to "name1",
                        TEXT_PROP to "text1",
                        TARGET_PROP to "target1",
                        ARGS_PROP to "arg1",
                        SOURCE_PROP to "source1"),
          /*2*/ mapOf(//without name
                        TEXT_PROP to "text2",
                        TARGET_PROP to "target2",
                        ARGS_PROP to "arg1 arg2",
                        SOURCE_PROP to "source2"),
          /*3*/ mapOf(NAME_PROP to "name3",
                        TEXT_PROP to "text3",
                        TARGET_PROP to "target3",
                        ARGS_PROP to "arg3",
                        SOURCE_PROP to "source3"))

        val out = transformRawExamplesListToMap(input)

        input.remove(1)
        assertEquals(input.toString(), out.values().toString())
    }

    test fun `all transformed`() {
        val input = listOf<Map<String, String>>(
                mapOf(NAME_PROP to "name1",
                        TEXT_PROP to "text1",
                        TARGET_PROP to "target1",
                        ARGS_PROP to "arg1",
                        SOURCE_PROP to "source1"),
                mapOf(NAME_PROP to "name2",
                        TEXT_PROP to "text2",
                        TARGET_PROP to "target2",
                        ARGS_PROP to "arg1 arg2",
                        SOURCE_PROP to "source2"),
                mapOf(NAME_PROP to "name3",
                        TEXT_PROP to "text3",
                        TARGET_PROP to "target3",
                        ARGS_PROP to "arg3",
                        SOURCE_PROP to "source3"))

        val out = transformRawExamplesListToMap(input)

        assertEquals(input.toString(), out.values().toString())
    }
}
