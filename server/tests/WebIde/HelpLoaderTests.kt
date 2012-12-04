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
import org.jetbrains.webdemo.server.webIde.domHelpers.toDocument
import kotlin.test.assertNotNull
import kotlin.test.expect
import org.jetbrains.webdemo.server.Attention
import kotlin.test.failsWith
import kotlin.test.assertTrue
import kotlin.test.assertEquals

RunWith(javaClass<JUnit4>())
public class ParseHelpFromDocumentTests {
    test fun `document without container tag`() {
        val doc = "<anothertag>somedate1</anothertag>".toDocument()
        assertNotNull(doc)

        assertTrue(parseHelpFromDocument(doc!!, "tag").isEmpty())
    }

    test fun `parse correct document with hindrances`() {
        val input =
                """<root>
                       <container>
                           <name>name_1</name>
                           <source>fun f_1 {}</source>
                           <target>java</target>
                       </container>
                       <hindrance>some data</hindrance>
                       <container>
                           <name>name_2</name>
                           <source>fun f_2 {}</source>
                       </container>
                   </root>"""

        val doc = input.toDocument()
        assertNotNull(doc)

        val result = parseHelpFromDocument(doc!!, "container")

        val expected = "[{target=java, source=fun f_1 {}, name=name_1}, {source=fun f_2 {}, name=name_2}]"
        assertEquals(expected, result.toString())
    }

}