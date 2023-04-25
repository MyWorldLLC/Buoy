/*
 * Copyright (c) 2023. MyWorld, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myworldvw.buoy

import com.myworldvw.buoy.util.Globals
import com.myworldvw.buoy.util.TestUtil
import spock.lang.Specification

import java.lang.foreign.MemoryAddress
import java.lang.foreign.ValueLayout

class GlobalLinkageSpec extends Specification {

    def "should link to global field"(){
        when:
        def globals = TestUtil.makeGlobals()

        then:
        Globals.@error.address() != MemoryAddress.NULL
        Globals.@error.address() == globals.getErrorAddress()
    }

    def "should set & read global field correctly"(){
        when:
        def globals = TestUtil.makeGlobals()
        Globals.@error.set(ValueLayout.JAVA_INT, 0, 0xFFEB)

        then:
        Globals.@error.get(ValueLayout.JAVA_INT, 0) == globals.getError()
        globals.getError() == 0xFFEB
    }

}
