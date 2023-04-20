package com.myworldvw.buoy

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

}
