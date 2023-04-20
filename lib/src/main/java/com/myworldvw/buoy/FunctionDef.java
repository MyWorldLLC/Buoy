package com.myworldvw.buoy;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.util.ArrayList;
import java.util.List;

public class FunctionDef {

    public static Builder create(){
        return new Builder();
    }

    public static class Builder {
        protected MemoryLayout retValue;
        protected List<MemoryLayout> paramValues = new ArrayList<>();

        public Builder withReturn(MemoryLayout layout){
            retValue = layout;
            return this;
        }

        public Builder withParam(MemoryLayout layout){
            paramValues.add(layout);
            return this;
        }

        public FunctionDescriptor build(){
            var params = paramValues.toArray(new MemoryLayout[]{});
            return retValue == null ?
                    FunctionDescriptor.ofVoid(params) :
                    FunctionDescriptor.of(retValue, params);
        }
    }
}
