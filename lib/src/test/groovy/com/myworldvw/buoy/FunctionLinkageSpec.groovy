package com.myworldvw.buoy

import com.myworldvw.buoy.util.TestUtil
import spock.lang.Specification

class FunctionLinkageSpec extends Specification {

    def mapper = new NativeMapper()
    def model = TestUtil.create()

    def "should add integers correctly"(){

        when:
        def functions = TestUtil.makeFunctionHandles()
        functions.addFn != null
        functions.addFn.invoke(1, 2) == 3

        then:
        noExceptionThrown()
    }

}
