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

package org.jetbrains.webdemo.common.utils.tests

import org.jetbrains.webdemo.common.utils.*

import org.junit.Test as test
import kotlin.test.expect
import junit.framework.TestCase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

RunWith(javaClass<JUnit4>())
class ArrayExtensionsTests {
    test fun `first from empty Array`() {
        expect(null) {
            array<Int>().first
        }
    }

    test fun `first from nonempty Array`() {
        expect(1) {
            array<Int>(1, 2, 3).first
        }
    }
}
