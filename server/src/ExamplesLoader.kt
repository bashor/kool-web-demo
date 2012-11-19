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
import org.jetbrains.webdemo.common.*
import org.jetbrains.webdemo.common.utils.files.baseName

val ALL_TARGETS = TargetPlatform.values() map { it.toString().toUpperCase() }
val DEFAULT_TARGETS = hashSet(TargetPlatform.JAVA)

public class ExamplesLoader(helpForExamples: VersionedContent<List<Map<String, String>>>): AbstractExamplesProcessor<Map<String, Example>>(helpForExamples) {

    protected override fun process(root: File, name2rawExamples: Map<String, Map<String, String>>): Map<String, Example> {
        val examples: HashMap<String, Example> = hashMap<String, Example>()

        root recurse {
            if (it.extension == KT_EXTENSION) {
                val baseName = it.baseName
                val rawExample = name2rawExamples[baseName]
                if (rawExample != null) {
                    val source = it.readText()
                    val targets = rawExample[TARGET_PROP]
                            .orEmpty()
                            .toUpperCase()
                            .split(' ')
                            .filter { ALL_TARGETS.contains(it) }
                            .map { TargetPlatform.valueOf(it) }
                            .toSet()

                    val example = Example(name = baseName,
                                          text = rawExample[TEXT_PROP].orEmpty(),
                                          targets = if (targets.notEmpty()) targets else DEFAULT_TARGETS,
                                          args = rawExample[ARGS_PROP].orEmpty(),
                                          source = source)

                    examples.put(baseName, example)
                }
            }
        }

        return examples
    }
}