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