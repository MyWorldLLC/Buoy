package com.myworldvw.buoy

import com.myworldvw.buoy.util.TestUtil
import spock.lang.Specification

import java.lang.foreign.MemoryAddress
import java.lang.foreign.ValueLayout

class GlobalLinkageSpec extends Specification {

    def "should link to errno"(){
        when:
        def globals = TestUtil.makeGlobals()

        then:
        globals.@errno.address() != MemoryAddress.NULL
        globals.@errno.get(ValueLayout.JAVA_INT, 0) == globals.getErrno()
    }

}
