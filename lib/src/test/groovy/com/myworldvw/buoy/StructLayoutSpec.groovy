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

import com.myworldvw.buoy.util.InnerT
import com.myworldvw.buoy.util.TestUtil
import spock.lang.Specification

class StructLayoutSpec extends Specification {

    def "should correctly map struct fields"(){
        when:
        def numbers = TestUtil.makeNumbersT()
        numbers.set((short)1, 2)

        then:
        numbers.addShort() == 3
        numbers.addNumbersT() == 3;
    }

    def "should correctly nest inline and struct pointers and map to models"(){
        when:
        def outer = TestUtil.makeOuterT()
        def mapper = TestUtil.makeMapper()

        def first = outer.nestedArray.get(0, new InnerT(), mapper)
        def second = outer.nestedArray.get(1, new InnerT(), mapper)
        def third = outer.nestedArray.get(2, new InnerT(), mapper)

        then:
        outer.outerA == (byte)123
        outer.nested.innerA == (byte)45
        outer.nested.innerB == 67
        first.innerA == (byte) 11
        first.innerB == 22
        second.innerA == (byte) 33
        second.innerB == 44
        third.innerA == (byte) 55
        third.innerB == 66
        outer.nestedPtr.innerA == (byte)89
        outer.nestedPtr.innerB == 10

    }

}
