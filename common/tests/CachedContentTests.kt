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

package org.jetbrains.webdemo.common

import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import org.mockito.Mockito.*
import org.jetbrains.webdemo.common.tests.helpers.ifCall
import kotlin.test.assertTrue
import kotlin.test.assertFalse

RunWith(javaClass<JUnit4>())
class CachedContentTests {
    test fun `check init`() {
        val version = 123.toLong()
        val content = "abcdef"

        val vc = object: VersionedContent<String> {
            override fun version() = version
            override fun snapshot() = ContentSnapshot(version, content)
        }

        var mapped = false
        val mod = "modified"

        val cc = CachedContent(vc) {
            mapped = true
            it + mod
        }

        assertTrue(mapped)
        assertEquals(version, cc.version)
        assertEquals(content + mod, cc.content)
    }

    test fun `check update`() {
        var mapped = false
        val startVersion = 123.toLong()
        val nextVersion = 125.toLong()
        val content = hashMap(startVersion to "abcdef", nextVersion to "qwerty")
        val mod = "modified"

        var version = startVersion
        val vc = object: VersionedContent<String> {
            override fun version() = version
            override fun snapshot() = ContentSnapshot(version, content[version].orEmpty())
        }

        val cc = CachedContent(vc) {
            mapped = true
            it + mod
        }

        assertTrue(mapped)
        assertEquals(version, cc.version)
        assertEquals(content[version].orEmpty() + mod, cc.content)

        mapped = false
        version = nextVersion

        assertEquals(startVersion, cc.version)
        assertFalse(mapped)
        assertEquals((content[version].orEmpty()) + mod, cc.content)
        assertTrue(mapped)
        assertEquals(nextVersion, cc.version)
    }

    test fun `check that behavior is lazy`() {
        val version = 123.toLong()
        val content = "abcdef"

        val vc = object: VersionedContent<String> {
            override fun version() = version
            override fun snapshot(): ContentSnapshot<String> {
                snapshotCallCounter++
                return ContentSnapshot(version, content)
            }
            var snapshotCallCounter = 0
        }

        var mapped = false
        val mod = "modified"

        val cc = CachedContent(vc) {
            mapped = true
            it + mod
        }

        assertTrue(mapped)
        assertEquals(version, cc.version)
        assertEquals(content + mod, cc.content)

        mapped = false
        assertEquals(version, cc.version)
        assertFalse(mapped)
        assertEquals(content + mod, cc.content)
        assertFalse(mapped)
        assertEquals(1, vc.snapshotCallCounter)
    }
}