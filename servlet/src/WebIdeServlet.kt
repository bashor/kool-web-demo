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

package org.jetbrains.webdemo.servlet

import org.jetbrains.webdemo.common.ContentWatcher
import org.jetbrains.webdemo.common.WebIdeCommands.*
import org.jetbrains.webdemo.common.utils.first
import org.jetbrains.webdemo.common.utils.json.toJsonString
import org.jetbrains.webdemo.server.WebIdeHandler

class WebIdeServlet : BaseHttpServlet() {
    val webIdeHandler = WebIdeHandler()
    val helpForKeywords = ContentWatcher(webIdeHandler.helpForKeywords, { it.toJsonString() })
    val helpForExamples = ContentWatcher(webIdeHandler.helpForExamples, { it.toJsonString() })
    val examplesList = ContentWatcher(webIdeHandler.examplesInfo, { it.hierarchy.toJsonString() })
    val examples = ContentWatcher(webIdeHandler.examplesInfo, { it.examples })

    override fun handle(command: String, params: Map<String, Array<String>>): String? = when (command) {
        LOAD_HELP_FOR_EXAMPLES -> helpForExamples.content
        LOAD_HELP_FOR_WORDS -> helpForKeywords.content
        LOAD_EXAMPLES_LIST -> examplesList.content
        LOAD_EXAMPLE -> {
            val name = params["name"]?.first
            if (name == null) {
                //todo handling error
                ""
            } else {
                examples.content[name]?.toJsonString() ?: ""
            }
        }
        UPDATE_EXAMPLES_LIST -> "" //webIdeHandler.updateExamplesList()
        else -> null
    }
}