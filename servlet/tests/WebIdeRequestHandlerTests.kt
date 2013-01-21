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

package org.jetbrains.webdemo.servlet

import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.jetbrains.webdemo.server.webIde.WebIdeHandler
import org.jetbrains.webdemo.common.VersionedContent
import org.jetbrains.webdemo.common.ExampleHolder
import org.jetbrains.webdemo.common.ContentSnapshot
import kotlin.test.fail
import kotlin.test.assertEquals
import org.jetbrains.webdemo.common.utils.json.toJsonString
import org.jetbrains.webdemo.common.CachedContent

RunWith(javaClass<JUnit4>())
public class WebIdeRequestHandlerTests {
    val content = listOf(mapOf("some" to "data", "1" to "2"), mapOf("other" to "data"))
    val contentAsJson = content.toJsonString()
    val anotherContent = listOf(mapOf("another" to "data"))
    val anotherContentAsJson = anotherContent.toJsonString()

    //fixme  after issue KT-3146 will be fixed
    class UpdatableHelpForKeywordsHandler(): UpdatableWebIdeHandler<List<Map<String, String>>>() {
        public override val helpForKeywords = object: VersionedContent<List<Map<String, String>>> {
            override fun version(): Long = version
            override fun snapshot() = ContentSnapshot(version(), content!!)
        }
    }

    test fun `static keywords content`() {
        doTestStaticContent(
                { UpdatableHelpForKeywordsHandler() },
                { this.helpForKeywords },
                content,
                contentAsJson)
    }

    test fun `update keywords content`() {
        doTestContentUpdating(
                { UpdatableHelpForKeywordsHandler() },
                { this.helpForKeywords },
                content,
                contentAsJson,
                anotherContent,
                anotherContentAsJson)
    }

    fun <T> doTestStaticContent(creator: () -> UpdatableWebIdeHandler<T>,
                                field: WebIdeRequestHandler.() -> CachedContent<T, String>,
                                content: T,
                                expected: String) {

        val version: Long = 123

        val webIdeHandler = creator()

        webIdeHandler.version = version
        webIdeHandler.content = content

        //fixme drop named argument
        val webIdeRequestHandler = WebIdeRequestHandler(webIdeHandler = webIdeHandler)

        assertEquals(version, webIdeRequestHandler.field().version)
        assertEquals(expected, webIdeRequestHandler.field().content)

        // request again
        assertEquals(version, webIdeRequestHandler.field().version)
        assertEquals(expected, webIdeRequestHandler.field().content)
    }

    fun <T> doTestContentUpdating(creator: () -> UpdatableWebIdeHandler<T>,
                                  field: WebIdeRequestHandler.() -> CachedContent<T, String>,
                                  content: T,
                                  expected: String,
                                  anotherContent: T,
                                  anotherContentExpected: String) {

        val version: Long = 123

        val webIdeHandler = creator()

        webIdeHandler.version = version
        webIdeHandler.content = content

        //fixme drop named argument
        val webIdeRequestHandler = WebIdeRequestHandler(webIdeHandler = webIdeHandler)

        assertEquals(version, webIdeRequestHandler.field().version)
        assertEquals(expected, webIdeRequestHandler.field().content)

        val newVersion: Long = 345
        webIdeHandler.version = newVersion
        webIdeHandler.content = anotherContent

        assertEquals(version, webIdeRequestHandler.field().version)
        assertEquals(anotherContentExpected, webIdeRequestHandler.field().content)

        // request again
        assertEquals(newVersion, webIdeRequestHandler.field().version)
        assertEquals(anotherContentExpected, webIdeRequestHandler.field().content)
    }

    class AlwaysFailedVersionedContent<T>(val initValue: T): VersionedContent<T> {
        var inited = false
        override fun version(): Long {
            if (inited) {
                fail()
            }
            return 0
        }
        override fun snapshot(): ContentSnapshot<T> {
            if (inited) {
                fail()
            }
            inited = true
            return ContentSnapshot(0, initValue)
        }
    }

    abstract class UpdatableWebIdeHandler<T: Any>() : WebIdeHandler {
        var version: Long = 0
        var content: T? = null

        class object {
            val emptyHelp = listOf<Map<String, String>>()
            val emptyExamples = mapOf<String, ExampleHolder>()
            val emptyHierarchy = listOf<Map<String, Any>>()
        }
        
        public override val helpForKeywords: VersionedContent<List<Map<String, String>>> = AlwaysFailedVersionedContent<List<Map<String, String>>>(emptyHelp)
        public override val helpForExamples: VersionedContent<List<Map<String, String>>> = AlwaysFailedVersionedContent<List<Map<String, String>>>(emptyHelp)
        public override val examples: VersionedContent<Map<String, ExampleHolder>> = AlwaysFailedVersionedContent<Map<String, ExampleHolder>>(emptyExamples)
        public override val hierarchy: VersionedContent<List<Map<String, Any>>> = AlwaysFailedVersionedContent<List<Map<String, Any>>>(emptyHierarchy)
    }
}