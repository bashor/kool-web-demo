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

import com.sun.xml.internal.ws.util.xml.NodeListIterator
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Text
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.jetbrains.webdemo.server.ExceptionAnalyzerUtils.sendToAnalyzer
import org.jetbrains.webdemo.server.ExceptionAnalyzerUtils.Attachment
import java.io.File
import javax.xml.parsers.DocumentBuilder
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import org.xml.sax.SAXException
import java.io.StringReader
import java.io.Reader
import org.jetbrains.webdemo.common.utils.io.*
import java.io.FileInputStream
import org.xml.sax.InputSource

inline fun NodeList.iterator(): NodeListIterator = NodeListIterator(this)

val Node.name: String
    inline get() = this.getNodeName().orEmpty()

val Node.value: String
    inline get() = this.getNodeValue().orEmpty()

private fun NamedNodeMap.get(i: Int) = this.item(i)

val Node.inner: String
    inline get() {
        val children = this.getChildNodes()
        if (children == null)
            return ""

        val inner = StringBuilder()
        for (node in children) {
            if (node is Text) {
                inner.append(node.value)
            } else if (node is Node) {
                val attributes = StringBuilder()

                if (node.name == "a") {
                    attributes.append(" target=\"_blank\"");
                }

                val map = node.getAttributes()
                if (map != null) {
                    for (i in 0..map.getLength() - 1) {
                        attributes.append(" ${ map[i]?.name ?: "" }=\"${ map[i]?.value ?: "" }\"")
                    }
                }

                inner.append("<${ node.name }${ attributes }>${ node.inner }</${ node.name }>")
            }
        }

        return inner.toString()
    }

inline fun File.toDocument() = parseToDocument(InputSource(FileInputStream(this)), { Attachment(this) })
inline fun String.toDocument() = parseToDocument(InputSource(this.reader), { Attachment("<String>", this) })

private fun parseToDocument(input: InputSource, getAttachment: () -> Attachment): Document? {
    fun sendException(e: Throwable) {
        val attachment = getAttachment()
        sendToAnalyzer(exception = e, lastAction = "Create org.w3c.dom.Document for ${attachment.name}", attachment = attachment)
    }

    val docBuilderFactory = DocumentBuilderFactory.newInstance()
    try {
        val docBuilder = docBuilderFactory?.newDocumentBuilder()
        val document = docBuilder?.parse(input)

        document?.getDocumentElement()?.normalize()
        return document
    } catch (e: IOException) {
        sendException(e)
        return null;
    } catch (e: ParserConfigurationException) {
        sendException(e)
        return null;
    } catch (e: SAXException) {
        sendException(e)
        return null;
    }}
