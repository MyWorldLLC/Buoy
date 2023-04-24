package com.myworldvw.buoy

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

        then:
        outer.outerA == (byte)123
        outer.nested.innerA == (byte)45
        outer.nested.innerB == 67
        outer.nestedPtr.innerA == (byte)89
        outer.nestedPtr.innerB == 10

    }

}
