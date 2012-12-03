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

package org.jetbrains.webdemo.common.utils

import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

RunWith(javaClass<JUnit4>())
public class StringTests {
    test fun `empty string`() {
        assertEquals("", "".stripMargin())
    }

    test fun `without margin char`() {
        val text = "text without margin char"
        assertEquals(text, text.stripMargin())
    }

    test fun `with default margin char`() {
        doTestForStringWithMarginChar()
    }

    test fun `with another margin char`() {
        doTestForStringWithMarginChar('*')
    }

    test fun `margin char in the middle of text`() {
        val text = "text |without| margin char"
        assertEquals(text, text.stripMargin())
    }

    test fun `with many margin char in one line`() {
        doTestForStringWithMarginChar(withHindrances = true)
    }

    fun doTestForStringWithMarginChar(marginChar: Char = '|', withHindrances: Boolean = false) {
        val h = if (withHindrances) "$marginChar" else ""
        val input = """${marginChar}first ${h}line
                       ${marginChar}second ${h}line${h}
                       line without ${h}margin
                       ${marginChar}yet another ${h}line"""

        val expected = "first ${h}line\nsecond ${h}line${h}\n                       line without ${h}margin\nyet another ${h}line"

        val out = if (marginChar == '|') input.stripMargin() else input.stripMargin(marginChar)
        assertEquals(expected, out)
    }


}