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
