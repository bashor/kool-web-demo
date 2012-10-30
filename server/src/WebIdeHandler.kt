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

private val EXAMPLE_TAG = "example"
private val KEYWORD_TAG = "keyword"

private val NAME_TAG = "name"
private val TEXT_TAG = "text"
private val TARGET_TAG = "target"
private val ARGS_TAG = "args"

public class WebIdeHandler {
    private var examples: Map<String, ExampleOnServer> = hashMap<String, ExampleOnServer>()
    val exampleName2Path = hashMap<String, String>()
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

        return example.source
    }

    fun updateExamplesList(): String {
        examplesList = ""

        return examplesList
    }

    private fun examplesContentUpdatedHandler(rawExamples: List<Map<String, String>>) {
        val newExamples: HashMap<String, ExampleOnServer> = hashMap<String, ExampleOnServer>()

        for (rawExample in rawExamples) {
            val name = rawExample[NAME_TAG]
            if (name == null)
                continue

            val allTargets = TargetPlatform.values() map { it.toString().toLowerCase() }
            val targets = rawExample[TARGET_TAG]
                    .orEmpty()
                    .split(' ')
                    .filter { allTargets.contains(it) }
                    .map { TargetPlatform.valueOf(it.toUpperCase()) }
                    .toSet()

            newExamples.put(name,
                    ExampleOnServer(text = rawExample[TEXT_TAG].orEmpty(),
                            targets = if (targets.notEmpty()) targets else hashSet(TargetPlatform.JAVA),
                            args = rawExample[ARGS_TAG].orEmpty(),
                            source = ""))
        }

        examples = newExamples
    }
}