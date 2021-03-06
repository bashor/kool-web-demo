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
import java.util.ArrayList
import org.jetbrains.webdemo.common.*
import org.jetbrains.webdemo.common.utils.files.baseName
import org.jetbrains.webdemo.common.utils.files.div
import org.jetbrains.webdemo.common.utils.sort
import org.jetbrains.webdemo.server.Attention
import org.jetbrains.webdemo.server.ExceptionAnalyzerUtils.sendToAnalyzer

private val FILES_PROP = "files"
private val TYPE_PROP = "type"
private val FOLDER = "folder"
private val FILE = "file"
private val KT_EXTENSION = "kt"
private val ORDER_FILE = "order.txt"

private val DEFAULT_TARGET_STR = TargetPlatform.JAVA.toString().toLowerCase()

fun ExamplesHierarchyGenerator(helpForExamples: VersionedContent<List<Map<String, String>>>) =
                ExamplesProcessor<List<Map<String, Any>>>(helpForExamples) {
                    (root, name2rawExamples) -> generateHierarchy(root, name2rawExamples, { sendToAnalyzer(it) })
                }

private fun generateHierarchy(root: File, name2rawExamples: Map<String, Map<String, String>>, errorReporter: (Throwable) -> Unit): List<Map<String, Any>> {
    val hierarchy = ArrayList<Map<String, Any>>()

    fun process(file: File) {
        if (!file.exists()) {
            errorReporter(Attention("File '$file' not found."))
            return
        }

        val baseName = file.baseName
        val map = hashMapOf<String, Any>(NAME_PROP to baseName)

        if (file.isDirectory()) {
            map.putAll(TYPE_PROP  to FOLDER,
                       FILES_PROP to generateHierarchy(file, name2rawExamples, errorReporter))
        } else {
            val rawExample = name2rawExamples[baseName]
            val target =
                    if (rawExample != null) {
                        rawExample[TARGET_PROP] ?: DEFAULT_TARGET_STR
                    } else {
                            errorReporter(Attention("Example '$baseName' doesn't have description."))
                        DEFAULT_TARGET_STR
                    }

            map.putAll(TYPE_PROP   to FILE,
                       TARGET_PROP to target)

        }

        hierarchy.add(map)
    }

    val orderFile = root / ORDER_FILE

    val orderLines =
            if (orderFile.exists()) {
                orderFile.readLines()
            } else {
                errorReporter(Attention("Order file '${orderFile.path}' not found."))
                listOf<String>()
            }

    orderLines.forEach { process(root / it) }

    val additionally = root.listFiles { (it.isDirectory() || it.extension == KT_EXTENSION) && !orderLines.contains(it.name) }

    if (additionally == null) {
        errorReporter(Attention("Additionally files list is null. Currnet dir is '${root.path}'."))
        return hierarchy
    }

    if (additionally.isNotEmpty()) {
        if (orderFile.exists())
            errorReporter(Attention("Order file '${orderFile.path}' doesn't contain some files."))

        additionally.sort().forEach { process(it) }
    }

    return hierarchy
}