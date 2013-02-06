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

import org.jetbrains.webdemo.server.Settings

private val EXAMPLE_TAG = "example"
private val KEYWORD_TAG = "keyword"

public class WebIdeHandlerImpl: WebIdeHandler {
    public override val helpForKeywords = HelpLoader(Settings.HELP_FOR_KEYWORDS_PATH, KEYWORD_TAG)
    public override val helpForExamples = HelpLoader(Settings.HELP_FOR_EXAMPLES_PATH, EXAMPLE_TAG)
    public override val examples = ExamplesLoader(helpForExamples)
    public override val hierarchy = ExamplesHierarchyGenerator(helpForExamples)
}