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
#include <cstdint>

#ifdef _WIN32
#    define EXPORT __declspec(dllexport)
#else
#    define EXPORT
#endif

extern "C" {
    EXPORT int32_t test_error;

    EXPORT int32_t add(int32_t a, int32_t b);
    EXPORT int32_t add_short(int32_t a, int16_t b);

    EXPORT typedef struct {
        int16_t a;
        int32_t b;
    } numbers_t;

    EXPORT typedef struct {
        int8_t a;
        int32_t b;
    } inner_t;

    EXPORT typedef struct {
        int8_t outer_a;
        inner_t nested;
        inner_t* nested_ptr;
    } outer_t;

    EXPORT int32_t add_numbers_t(numbers_t* n);

    EXPORT int32_t set_and_get_error(int32_t err);
    EXPORT int32_t get_error();
    EXPORT int32_t* get_error_address();

    EXPORT outer_t make_outer_t_value(inner_t* nested);
}