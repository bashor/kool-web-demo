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
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import org.jetbrains.webdemo.common.ContentSnapshot
import org.jetbrains.webdemo.common.VersionedContent
import org.jetbrains.webdemo.common.utils.notEmpty
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.SAXException
import org.jetbrains.webdemo.server.webIde.domHelpers.*
import org.jetbrains.webdemo.server.webIde.domHelpers.toDocument
import org.jetbrains.webdemo.server.ExceptionAnalyzerUtils.sendToAnalyzer
import org.jetbrains.webdemo.server.Attention

class HelpLoader(path: String, private val containerTag: String): VersionedContent<List<Map<String, String>>> {
    private val file = File(path)

    override fun version(): Long = file.lastModified()
    override fun snapshot(): ContentSnapshot<List<Map<String, String>>> {
        val version = version()
        val document = file.toDocument()

        if (document == null) {
            return ContentSnapshot(version, list<Map<String, String>>())
        }

        val elements = parseHelpFromDocument(document, containerTag)
        return ContentSnapshot(version, elements)
    }
}

private fun parseHelpFromDocument(document: Document, containerTag: String): List<Map<String, String>> {
    val elements = arrayListOf<Map<String, String>>()

    val nodeList = document.getElementsByTagName(containerTag)

    for (node in nodeList) {
        //fixme after issue KT-2982 will be fixed
        if (node !is Element || !node.hasChildNodes())
            continue

        val map = hashMapOf<String, String>();
        for (subNode in node.getChildNodes()!!) {
            //fixme after issue KT-2982 will be fixed
            if (subNode !is Element)
                continue

            map.put(subNode.name, subNode.inner)
        }

        if (map.notEmpty()) {
            elements.add(map)
        }
    }

    return elements
}