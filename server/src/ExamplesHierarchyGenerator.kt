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
import java.util.ArrayList
import org.jetbrains.webdemo.common.*
import org.jetbrains.webdemo.common.utils.files.baseName
import org.jetbrains.webdemo.common.utils.files.div

private val FILES_PROP = "files"
private val TYPE_PROP = "type"
private val FOLDER = "folder"
private val FILE = "file"
private val KT_EXTENSION = "kt"
private val ORDER_FILE = "order.txt"

public class ExamplesHierarchyGenerator(helpForExamples: VersionedContent<List<Map<String, String>>>):
                AbstractExamplesProcessor<List<Map<String, Any>>>(helpForExamples) {

    val DEFAULT_TARGET = TargetPlatform.JAVA.toString().toLowerCase()

    protected override fun process(root: File, name2rawExamples: Map<String, Map<String, String>>): List<Map<String, Any>> {
        val hierarchy = ArrayList<Map<String, Any>>()

        //todo check exist ORDER_FILE
        val order = (root / ORDER_FILE).readLines()

        fun process(file: File) {
            val baseName = file.baseName
            val map = hashMap<String, Any>(NAME_PROP to baseName)

            if (file.isDirectory()) {
                map.putAll(TYPE_PROP  to FOLDER,
                           FILES_PROP to process(file, name2rawExamples))
            } else {
                val rawExample = name2rawExamples[baseName]
                val target = if (rawExample != null) {
                    rawExample[TARGET_PROP] ?: DEFAULT_TARGET
                } else {
                    //todo not found example description
                    DEFAULT_TARGET
                }

                map.putAll(TYPE_PROP   to FILE,
                           TARGET_PROP to target)

            }

            hierarchy.add(map)
        }

        order.forEach { process(root / it) }

        val additionally = root.listFiles { (it.isDirectory() || it.extension == KT_EXTENSION) && !order.contains(it.name)}

        if (additionally == null) {
            //todo ???
            return hierarchy
        }

        if (additionally.notEmpty()) {
            //todo

            additionally.forEach { process(it) }
        }

        return hierarchy
    }
}