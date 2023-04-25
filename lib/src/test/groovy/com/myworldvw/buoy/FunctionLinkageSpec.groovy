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

import com.myworldvw.buoy.util.Statics
import com.myworldvw.buoy.util.TestUtil
import spock.lang.Specification

class FunctionLinkageSpec extends Specification {

    def "should map function handles correctly"(){

        when:
        TestUtil.makeFunctionHandles()

        then:
        noExceptionThrown()
    }

    def "should map add integers correctly"(){

        when:
        def functions = TestUtil.makeFunctionHandles()

        then:
        functions.add != null
        functions.add(1, 2) == 3
    }

    def "should fill static handles only in a static use context"(){
        when:
        TestUtil.fillStatics()

        then:
        Statics.staticAdd != null
    }

    def "should not fill static handles in an instance use context"(){

        when:
        def functions = TestUtil.makeFunctionHandles()

        then:
        functions.unused == null
    }

}
