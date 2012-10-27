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

package org.jetbrains.webdemo.server.Settings

import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Zalim Bahsorov
 * Date: 10/27/12
 * Time: 6:40 PM
 */

val EXAMPLES_DIRECTORY = "examples";
val HELP_DIRECTORY = "help";

val HELP_FOR_EXAMPLES_FILE = "helpExamples.xml";
val HELP_FOR_KEYWORDS_FILE = "helpWords.xml";

val HELP_FOR_EXAMPLES_PATH = EXAMPLES_DIRECTORY + File.separator + HELP_FOR_EXAMPLES_FILE
val HELP_FOR_KEYWORDS_PATH = HELP_DIRECTORY + File.separator + HELP_FOR_KEYWORDS_FILE;