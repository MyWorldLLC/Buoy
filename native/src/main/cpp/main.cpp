#include "main.h"

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
    return outer_t{
        123,
        inner_t{
            45,
            67
        },
        nested
    };
}