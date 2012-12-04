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
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.webdemo.common.*
import kotlin.dom.removeClass

RunWith(javaClass<JUnit4>())
public class AbstractExamplesProcessorTests {
    //todo
    test fun a() {
    }
}

RunWith(javaClass<JUnit4>())
public class TransformRawExamplesListToMapTests {
    test fun `for empty list`() {
        assertTrue(transformRawExamplesListToMap(list<Map<String, String>>()).empty)
    }

    test fun `skiping items without name`() {
        val input = arrayListOf<Map<String, String>>(
                map(NAME_PROP to "name1",
                        TEXT_PROP to "text1",
                        TARGET_PROP to "target1",
                        ARGS_PROP to "arg1",
                        SOURCE_PROP to "source1"),
                map(//without name
                        TEXT_PROP to "text2",
                        TARGET_PROP to "target2",
                        ARGS_PROP to "arg1 arg2",
                        SOURCE_PROP to "source2"),
                map(NAME_PROP to "name3",
                        TEXT_PROP to "text3",
                        TARGET_PROP to "target3",
                        ARGS_PROP to "arg3",
                        SOURCE_PROP to "source3"))

        val out = transformRawExamplesListToMap(input)

        input.remove(1)
        assertEquals(input.toString(), out.values().toString())
    }

    test fun `all transformed`() {
        val input = list<Map<String, String>>(
                map(NAME_PROP to "name1",
                        TEXT_PROP to "text1",
                        TARGET_PROP to "target1",
                        ARGS_PROP to "arg1",
                        SOURCE_PROP to "source1"),
                map(NAME_PROP to "name2",
                        TEXT_PROP to "text2",
                        TARGET_PROP to "target2",
                        ARGS_PROP to "arg1 arg2",
                        SOURCE_PROP to "source2"),
                map(NAME_PROP to "name3",
                        TEXT_PROP to "text3",
                        TARGET_PROP to "target3",
                        ARGS_PROP to "arg3",
                        SOURCE_PROP to "source3"))

        val out = transformRawExamplesListToMap(input)

        assertEquals(input.toString(), out.values().toString())
    }
}