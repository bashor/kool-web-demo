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

import kotlin.test.assertEquals
import org.json.JSONObject
import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

RunWith(javaClass<JUnit4>())
public class ExampleHolderTests {
    test fun `exampleHolder to json string`() {
        val name = "examplename"
        val text = "description"
        val targets = setOf(TargetPlatform.JAVA, TargetPlatform.CANVAS)
        val targetsStr = "java canvas"
        val args = "arg1 arg2"
        val source = "fun main(){}"
        val example = ExampleHolder(name, text, targets, args, source)

        val jsonString = example.toJsonString()

        val json = JSONObject(jsonString)
        assertEquals(name, json[NAME_PROP])
        assertEquals(text, json[TEXT_PROP])
        assertEquals(targetsStr, json[TARGET_PROP])
        assertEquals(args, json[ARGS_PROP])
        assertEquals(source, json[SOURCE_PROP])
    }
}