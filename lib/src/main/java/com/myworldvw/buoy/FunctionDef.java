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
