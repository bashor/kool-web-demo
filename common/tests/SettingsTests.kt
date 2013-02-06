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
import kotlin.test.assertTrue
import org.junit.Test as test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

RunWith(javaClass<JUnit4>())
public class SettingsTests {
    test fun `check is_testing flag`() {
        assertTrue(Settings.IS_TESTING)
    }

    test fun `property getter`() {
        assertEquals("testsData", Settings.getProperty("app_home", "."))
        assertEquals("true", Settings.getProperty("is_testing", "false"))
    }

    test fun `property setter`() {
        val property = "testProperty"
        val testValue = "test value"

        Settings.setProperty(property, testValue)

        assertEquals(testValue, Settings.getProperty(property, ""))
    }
}