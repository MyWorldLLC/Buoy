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

import java.lang.foreign.SegmentScope
import java.lang.foreign.ValueLayout

class ArraySpec extends Specification {

    def "should get from arrays correctly"(){
        when:
        def array = TestUtil.makeInnerTArray()
        def mapper = new NativeMapper(TestUtil.testLib)
        mapper.register(InnerT.class)

        def first = array.get(0, new InnerT(), mapper)
        def second = array.get(1, new InnerT(), mapper)
        def third = array.get(2, new InnerT(), mapper)

        then:
        array.length() == 3
        first.innerA == (byte) 11
        first.innerB == 22
        second.innerA == (byte) 33
        second.innerB == 44
        third.innerA == (byte) 55
        third.innerB == 66
    }

    def "should set array element fields correctly"(){
        when:
        def array = TestUtil.makeInnerTArray()
        def mapper = new NativeMapper(TestUtil.testLib)
        mapper.register(InnerT.class)

        def first = array.get(0, new InnerT(), mapper)
        def second = array.get(1, new InnerT(), mapper)
        def third = array.get(2, new InnerT(), mapper)

        second.setInnerB(77)

        then:
        first.innerA == (byte) 11
        first.innerB == 22
        second.innerA == (byte) 33
        second.innerB == 77
        third.innerA == (byte) 55
        third.innerB == 66

    }

    def "should set primitive array elements correctly"(){
        when:
        def array = Platform.allocate(ValueLayout.JAVA_INT, 3, SegmentScope.global())
        Array.setInt(array, 0, 0xAA)
        Array.setInt(array, 1, 0xBB)
        Array.setInt(array, 2, 0xCC)

        then:
        Array.getInt(array, 0) == 0xAA
        Array.getInt(array, 1) == 0xBB
        Array.getInt(array, 2) == 0xCC
    }
}
