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

package org.jetbrains.webdemo.common

import org.json.JSONObject

public val NAME_PROP: String = "name"
public val TEXT_PROP: String = "text"
public val TARGET_PROP: String = "target"
public val ARGS_PROP: String = "args"
public val SOURCE_PROP: String = "source"

public data class ExampleHolder(val name: String,
                          val text: String,
                          val targets: Set<TargetPlatform>,
                          val args: String,
                          val source: String) {

    fun toString(): String = "ExampleHolder(name=$name, text=$text, targets=${targets.toSortedSet()}, args=$args, source=$source)"
}

inline fun ExampleHolder.toJsonString(): String {
    val result = JSONObject()

    result.put(NAME_PROP, this.name)
    result.put(TEXT_PROP, this.text)
    result.put(TARGET_PROP, this.targets.toSortedSet().map { it.toString().toLowerCase() }.makeString(" "))
    result.put(ARGS_PROP, this.args)
    result.put(SOURCE_PROP, this.source)

    return result.toString().orEmpty()
}