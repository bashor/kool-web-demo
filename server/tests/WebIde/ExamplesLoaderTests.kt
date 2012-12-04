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

import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import org.jetbrains.webdemo.server.Settings
import org.jetbrains.webdemo.common.VersionedContent

RunWith(javaClass<JUnit4>())
public class ExamplesLoaderTests {
    test fun `first`() {
//        val helpForExamples = HelpLoader(Settings.HELP_FOR_EXAMPLES_PATH, EXAMPLE_TAG)
//        checkHelpForExamples(helpForExamples)
//
//        val loader = ExamplesLoader(helpForExamples)
    }

    fun checkHelpForExamples(help: VersionedContent<List<Map<String, String>>>) {
//        assertEqual(expectedHelpForExamples, help.content())
    }
}