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
import org.jetbrains.webdemo.common.*

fun transformRawExamplesListToMap(rawExamples: List<Map<String, String>>): Map<String, Map<String, String>> {
    val name2rawExample = hashMap<String, Map<String, String>>()
    for (rawExample in rawExamples) {
        val name = rawExample[NAME_PROP]
        if (name == null)
            continue

        name2rawExample.put(name, rawExample)
    }
    return name2rawExample
}

abstract class AbstractExamplesProcessor<R>(helpForExamples: VersionedContent<List<Map<String, String>>>): VersionedContent<R> {
    val helpForExamplesWatcher = ContentWatcher(helpForExamples, { it })

    override fun version(): Long = helpForExamplesWatcher.source.version()

    override fun snapshot(): ContentSnapshot<R> {
        val root = File(Settings.EXAMPLES_DIRECTORY_PATH)
        if (!root.exists()) {
            throw InternalError("Root directory of Examples doesn't exists.\nSettings.EXAMPLES_DIRECTORY_PATH = \"${Settings.EXAMPLES_DIRECTORY_PATH}\"")
        }

        val name2rawExamples = transformRawExamplesListToMap(helpForExamplesWatcher.content)
        return ContentSnapshot(helpForExamplesWatcher.version, process(root, name2rawExamples))
    }

    protected abstract fun process(root: File, name2rawExamples: Map<String, Map<String, String>>): R
}