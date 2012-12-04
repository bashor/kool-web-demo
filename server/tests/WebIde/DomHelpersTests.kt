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

package org.jetbrains.webdemo.server.webIde.domHelpers

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.jetbrains.webdemo.common.tests.helpers.ifCall
import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import org.w3c.dom.Node
import org.w3c.dom.NodeList

RunWith(javaClass<JUnit4>())
class DomHelpersTests {
    test fun `node with name and value`() {
        val node = getFirstNodeFromString("<$NODE_NAME>$NODE_VALUE</$NODE_NAME>", NODE_NAME)
        assertEquals(NODE_NAME, node.name)
    }

    test fun `node with name and without body(value)`() {
        val node = getFirstNodeFromString("<$NODE_NAME/>", NODE_NAME)
        assertEquals(NODE_NAME, node.name)
    }

    test fun `node with name (null)`() {
        val node = mock(javaClass<Node>())
        ifCall(node.getNodeName()).thenReturn(null)

        assertEquals("", node.name)
    }

    test fun `node with value (nonempty)`() {
        testValue(input = NODE_VALUE, expected = NODE_VALUE)
    }

    test fun `node without value (null)`() {
        testValue(input = null, expected = "")
    }

    test fun `get inner for empty node`() {
        testInner("")
    }

    test fun `get inner for Text node`() {
        testInner(NODE_VALUE)
    }

    test fun `get inner with anchor`() {
        val input = """<a href="url">text</a>"""

        val expected = """<a target="_blank" href="url">text</a>"""

        testInner(input, expected)
    }

    test fun `get inner for node with nested nodes`() {
        val input = """Line 2 demonstrates the
                <a href="http://confluence.jetbrains.net/display/Kotlin/Control+structures#Controlstructures-Forloop">
                <b>for</b>-loop</a>, <br/> that would have been called "enhanced" if there were any other for-loop in Kotlin."""

        val expected = """Line 2 demonstrates the
                <a target="_blank" href="http://confluence.jetbrains.net/display/Kotlin/Control+structures#Controlstructures-Forloop">
                <b>for</b>-loop</a>, <br></br> that would have been called "enhanced" if there were any other for-loop in Kotlin."""


        testInner(input, expected)
    }

    test fun `node iterator`() {
        val arr = Array(7, { it })
        val input = arr.map { "<$NODE_NAME>$it</$NODE_NAME>" } .makeString(separator = "", prefix = "<root>", postfix = "</root>")

        val nodeList = getNodeListFromString(input, NODE_NAME)
        val result = nodeList.iterator().map { (it as Node).inner }

        assertEquals(arr.makeString(), result.makeString())
    }

    private val NODE_NAME = "somenode"
    private val NODE_VALUE = "some value"

    fun testValue(input: String?, expected: String?) {
        val node = mock(javaClass<Node>())
        ifCall(node.getNodeValue()).thenReturn(input)

        assertEquals(expected, node.value)
    }

    private fun testInner(value: String, expected: String? = null) {
        val node = getFirstNodeFromString("<$NODE_NAME>$value</$NODE_NAME>", NODE_NAME)
        assertEquals(expected ?: value, node.inner)
    }

    fun getNodeListFromString(input: String, nodeName: String): NodeList {
        val doc = input.toDocument()
        assertNotNull(doc)

        val nodeList = doc!!.getElementsByTagName(nodeName)
        assertNotNull(nodeList)

        return nodeList
    }

    private fun getFirstNodeFromString(input: String, nodeName: String): Node {
        val nodeList = getNodeListFromString(input, nodeName)
        assertEquals(1, nodeList.getLength())

        val node = nodeList.item(0)
        assertNotNull(node)

        return node as Node
    }
}