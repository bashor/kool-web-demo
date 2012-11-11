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
import javax.naming.Context
import javax.naming.InitialContext

//todo find a more elegant solution
private val envContext = InitialContext().lookup("java:comp/env") as Context

//todo check: can it  throw an exception?
val APP_HOME = envContext.lookup("app_home") as String

val EXAMPLES_DIRECTORY = "examples";
val HELP_DIRECTORY = "help";

val HELP_FOR_EXAMPLES_FILE = "helpExamples.xml"
val HELP_FOR_KEYWORDS_FILE = "helpWords.xml"

val EXAMPLES_DIRECTORY_PATH = APP_HOME + EXAMPLES_DIRECTORY
val HELP_DIRECTORY_PATH = APP_HOME + HELP_DIRECTORY

val HELP_FOR_EXAMPLES_PATH: String = EXAMPLES_DIRECTORY_PATH + File.separator + HELP_FOR_EXAMPLES_FILE
val HELP_FOR_KEYWORDS_PATH: String = HELP_DIRECTORY_PATH + File.separator + HELP_FOR_KEYWORDS_FILE;
