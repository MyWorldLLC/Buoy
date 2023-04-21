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

}
