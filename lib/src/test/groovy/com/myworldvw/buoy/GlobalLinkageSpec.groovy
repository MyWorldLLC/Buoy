package com.myworldvw.buoy

import com.myworldvw.buoy.util.Globals
import com.myworldvw.buoy.util.TestUtil
import spock.lang.Specification

import java.lang.foreign.MemoryAddress
import java.lang.foreign.ValueLayout

class GlobalLinkageSpec extends Specification {

    def "should link to errno"(){
        when:
        def globals = TestUtil.makeGlobals()

        then:
        Globals.@error.address() != MemoryAddress.NULL
        Globals.@error.set(ValueLayout.JAVA_INT, 0, 0xFFEB)
        Globals.@error.get(ValueLayout.JAVA_INT, 0) == globals.getError()
        globals.getError() == 0xFFEB
    }

}
