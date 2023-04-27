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
#include "main.h"
#include <cstdlib>

int32_t add(int32_t a, int32_t b){
    return a + b;
}


int32_t add_short(int32_t a, int16_t b){
    return a + b;
}


int32_t add_numbers_t(numbers_t* n){
    return n->a + n->b;
}

int32_t set_and_get_error(int32_t err){
    test_error = err;
    return test_error;
}

int32_t get_error(){
    return test_error;
}

int32_t* get_error_address(){
    return &test_error;
}

outer_t make_outer_t_value(inner_t* nested){
    nested->a = 89;
    nested->b = 10;
    return {
        123,
        {
            45,
            67
        },
        {
            {
                11,
                22
            },
            {
                33,
                44
            },
            {
                55,
                66
            }
        },
        nested
    };
}

inner_t* make_inner_t_array(){
    inner_t* arr = (inner_t*) malloc(sizeof(inner_t) * 3);
    arr[0].a = 11;
    arr[0].b = 22;
    arr[1].a = 33;
    arr[1].b = 44;
    arr[2].a = 55;
    arr[2].b = 66;
    return arr;
}