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

package org.jetbrains.webdemo.server

import java.io.File
import org.w3c.dom.Node
import org.w3c.dom.Element
import java.util.HashMap
import org.json.JSONArray
import org.w3c.dom.Document
import javax.xml.parsers.ParserConfigurationException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.SAXException
import com.sun.xml.internal.ws.util.xml.NodeListIterator
import org.jetbrains.webdemo.common.utils.notEmpty

public fun Document(file : File) : Document? {
    val docBuilderFactory = DocumentBuilderFactory.newInstance()
    try {
        val docBuilder = docBuilderFactory?.newDocumentBuilder()
        val document = docBuilder?.parse(file)

        document?.getDocumentElement()?.normalize()
        return document!!
    } catch (e: IOException) {
        //todo logging
        return null;
    } catch (e: ParserConfigurationException) {
        //todo logging
        return null;
    } catch (e: SAXException) {
        //todo logging
        return null;
    }
}

fun org.w3c.dom.NodeList.iterator() = NodeListIterator(this)
val org.w3c.dom.Node.name : String
        get() = this.getNodeName()!!

val org.w3c.dom.Node.value : String
    get() = this.getNodeValue()!!


class HelpHolder(path: String,
                 private val containerTag: String,
                 private val contentUpdatedHandler: (List<Map<String, String>>) -> Unit = {}) {

    private val file = File(path)
    private var lastModified: Long = 0

    public var content: String = ""
        get() {
            //todo check file existing???
            if (file.lastModified() != lastModified)
                $content = loadHelp()
            return $content
        }
        private set

    private fun loadHelp(): String {
        val elements = arrayList<Map<String, String>>()
        val result = JSONArray()

        val doc = Document(file)

        if (doc == null) {
            //todo logging
            return ""
        }

        val nodeList = doc.getElementsByTagName(containerTag)!!

        for (node in nodeList) {
            //todo fix this workaround after issue KT-2982 will be fixed
            if (node !is Node || node.hasChildNodes() || node.getNodeType() != Node.ELEMENT_NODE)
                continue

            val map = hashMap<String, String>();
            for (subNode in node.getChildNodes()!!) {
                //todo fix this workaround after issue KT-2982 will be fixed
                if (subNode !is Node)
                    continue

                map.put(subNode.name, subNode.value)
            }

            if (map.notEmpty()) {
                elements.add(map)
                result.put(map)
            }
        }

        lastModified = file.lastModified()
        contentUpdatedHandler(elements)
        return result.toString().orEmpty()
    }
}