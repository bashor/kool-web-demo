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
import java.util.HashMap
import org.jetbrains.webdemo.common.TargetPlatform
import org.jetbrains.webdemo.common.utils.files.baseName
import org.jetbrains.webdemo.common.utils.files.div
import org.jetbrains.webdemo.common.utils.join
import org.json.JSONArray
import org.json.JSONObject

private val EXAMPLE_TAG = "example"
private val KEYWORD_TAG = "keyword"

private val NAME_PROP = "name"
private val TEXT_PROP = "text"
private val TARGET_PROP = "target"
private val ARGS_PROP = "args"
private val SOURCE_PROP = "source"

private val FILES_PROP = "files"
private val TYPE_PROP = "type"
private val FOLDER = "folder"
private val FILE = "file"

public class WebIdeHandler {
    private var examples = hashMap<String, ExampleOnServer>()
    var examplesList = ""

    private val helpForKeywords = HelpHolder(Settings.HELP_FOR_KEYWORDS_PATH, KEYWORD_TAG)
    private val helpForExamples = HelpHolder(Settings.HELP_FOR_EXAMPLES_PATH, EXAMPLE_TAG, { examplesContentUpdatedHandler(it) })

    fun loadHelpForExamples() : String = helpForExamples.content

    fun loadHelpForWords() : String = helpForKeywords.content

    fun loadExamplesList(): String = examplesList

    fun loadExample(name: String): String {
        val example = examples[name]
        if (example == null)
            // todo logging
            return ""

        val result = JSONObject()

        result.put(TEXT_PROP, example.text)
        result.put(TARGET_PROP, example.targets.toList().map { it.toString().toLowerCase() }.join(" "))
        result.put(ARGS_PROP, example.args)
        result.put(SOURCE_PROP, example.source)

        return result.toString()
    }

    fun updateExamplesList(): String {
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH)
        if (!root.exists()) {
            //todo error handling
//            ToExceptionAnalyzer
//            ToConsole

            return "Examples root doesn't exists"
        }

        examplesList = buildExamplesList(root).toString()

        return "Examples were loaded. "
    }

    fun buildExamplesList(root: File): JSONArray {
        val result = JSONArray()

        val ORDER_TXT = "order.txt"
        val order = (root / ORDER_TXT).readLines()

        fun process(file: File) {
            val map = hashMap<String, Any>(NAME_PROP to file.baseName)

            if (file.isDirectory()) {
                map.putAll(TYPE_PROP to FOLDER, FILES_PROP to buildExamplesList(file))
            } else {
                map.putAll(TYPE_PROP to FILE)
                val source = file.readText()

                val example = examples[file.baseName]
                val newExample = if (example == null) {
                    //todo report
                    ExampleOnServer(
                            text = "",
                            targets = hashSet(TargetPlatform.JAVA),
                            args = "",
                            source = source)
                } else {
                    example.copy(source = source)
                }

                examples[file.baseName] = newExample

                map.putAll(TARGET_PROP to newExample.targets.toList().map { it.toString().toLowerCase() }.join(" "))
            }

            result.put(map)
        }

        order.forEach { process(root / it) }

        val KT_EXTENSION = "kt"
        val additionally = root.listFiles { (it.isDirectory() || it.extension == KT_EXTENSION) && !order.contains(it.name)}

        if (additionally == null) {
            //todo
            return result
        }

        if (additionally.notEmpty()) {
            //todo
            return result
        }

        additionally.forEach { process(it) }

        return result
    }

    private fun examplesContentUpdatedHandler(rawExamples: List<Map<String, String>>) {
        val newExamples: HashMap<String, ExampleOnServer> = hashMap<String, ExampleOnServer>()

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
                    ExampleOnServer(text = rawExample[TEXT_PROP].orEmpty(),
                            targets = if (targets.notEmpty()) targets else hashSet(TargetPlatform.JAVA),
                            args = rawExample[ARGS_PROP].orEmpty(),
                            source = ""))
        }

        examples = newExamples
    }

    {
        updateExamplesList()
    }
}