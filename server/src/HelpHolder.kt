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

/**
 * Created by IntelliJ IDEA.
 * User: Zalim Bahsorov
 * Date: 10/27/12
 * Time: 7:00 PM
 */

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


class HelpHolder(path: String, val containerTag: String) {
    val file = File(path)
    var help = ""
    var lastModified: Long = 0

    fun updateHelp() {
        if (file.lastModified() == lastModified)
            return

        val result= JSONArray()

        val doc = Document(file)

        if (doc == null) {
            return
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

            if (map.notEmpty())
                result.put(map)
        }

        lastModified = file.lastModified()
        help = result.toString()!!
    }

    fun toString(): String {
        updateHelp()
        return help
    }
}