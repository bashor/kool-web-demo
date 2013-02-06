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
import org.jetbrains.webdemo.common.*
import org.jetbrains.webdemo.server.Settings

class ExamplesProcessor<R>(
        helpForExamples: VersionedContent<List<Map<String, String>>>,
        val exampleDirectoryPath: String = Settings.EXAMPLES_DIRECTORY_PATH,
        val processor: (File, Map<String, Map<String, String>>) -> R): VersionedContent<R> {

    val cachedHelpForExamples = CachedContent(helpForExamples, { it })

    override fun version(): Long = cachedHelpForExamples.source.version()

    override fun snapshot(): ContentSnapshot<R> {
        val root = File(exampleDirectoryPath)
        if (!root.exists()) {
            throw InternalError("Root directory of Examples doesn't exists.\nSettings.EXAMPLES_DIRECTORY_PATH = \"${Settings.EXAMPLES_DIRECTORY_PATH}\"")
        }

        val name2rawExamples = transformRawExamplesListToMap(cachedHelpForExamples.content)
        return ContentSnapshot(cachedHelpForExamples.version, processor(root, name2rawExamples))
    }
}

private fun transformRawExamplesListToMap(rawExamples: List<Map<String, String>>): Map<String, Map<String, String>> {
    val name2rawExample = hashMapOf<String, Map<String, String>>()
    for (rawExample in rawExamples) {
        val name = rawExample[NAME_PROP]
        if (name == null)
            continue

        name2rawExample.put(name, rawExample)
    }
    return name2rawExample
}
