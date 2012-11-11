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
import java.util.HashMap
import org.jetbrains.webdemo.common.*
import org.jetbrains.webdemo.common.utils.files.baseName
import org.jetbrains.webdemo.common.utils.files.div
import org.jetbrains.webdemo.common.utils.join

private val FILES_PROP = "files"
private val TYPE_PROP = "type"
private val FOLDER = "folder"
private val FILE = "file"
private val KT_EXTENSION = "kt"
private val ORDER_FILE = "order.txt"

public class ExamplesInfoLoader: VersionedContent<ExamplesInfo> {
    var lastUpdateTime: Long = 0

    override fun version(): Long = lastUpdateTime

    override fun content(): ContentSnapshot<ExamplesInfo> {
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH)
        if (!root.exists()) {
            //todo error handling
            //            ToExceptionAnalyzer
            //            ToConsole

//            return "Examples root doesn't exists"
        }

        val hierarchy = buildHierarchy(root)

        return ContentSnapshot(lastUpdateTime, ExamplesInfo(hierarchy, examples))
    }

    fun buildHierarchy(root: File): List<Map<String, Any>> {
        val hierarchy = ArrayList<Map<String, Any>>()

       val order = (root / ORDER_FILE).readLines()

        fun process(file: File) {
            val baseName = file.baseName
            val map = hashMap<String, Any>(NAME_PROP to baseName)

            if (file.isDirectory()) {
                map.putAll(TYPE_PROP to FOLDER, FILES_PROP to buildHierarchy(file))
            } else {
                map.putAll(TYPE_PROP to FILE)
                val source = file.readText()

                val example = examples[baseName]
                val newExample = if (example == null) {
                    //todo report
                    Example(name = baseName,
                            text = "",
                            targets = hashSet(TargetPlatform.JAVA),
                            args = "",
                            source = source)
                } else {
                    example.copy(source = source)
                }

                examples[baseName] = newExample

                map.putAll(TARGET_PROP to newExample.targets.toList().map { it.toString().toLowerCase() }.join(" "))
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

    private var examples = hashMap<String, Example>()

    fun examplesUpdated(rawExamples: List<Map<String, String>>) {
        val newExamples: HashMap<String, Example> = hashMap<String, Example>()

        for (rawExample in rawExamples) {
            val name = rawExample[NAME_PROP]
            if (name == null)
                continue

            val allTargets = TargetPlatform.values() map { it.toString().toLowerCase() }
            val targets = rawExample[TARGET_PROP]
                    .orEmpty()
                    .split(' ')
                    .filter { allTargets.contains(it) }
                    .map { TargetPlatform.valueOf(it.toUpperCase()) }
                    .toSet()

            newExamples.put(name,
                    Example(name = name,
                            text = rawExample[TEXT_PROP].orEmpty(),
                            targets = if (targets.notEmpty()) targets else hashSet(TargetPlatform.JAVA),
                            args = rawExample[ARGS_PROP].orEmpty(),
                            source = ""))
        }

        examples = newExamples
        lastUpdateTime = System.currentTimeMillis()
    }
}