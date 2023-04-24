#include <cstdint>

#ifdef _WIN32
#    define EXPORT __declspec(dllexport)
#elif
#    define EXPORT
#endif

extern "C" {
    EXPORT int32_t error;

    EXPORT int32_t add(int32_t a, int32_t b);
    EXPORT int32_t add_short(int32_t a, int16_t b);

    EXPORT typedef struct {
        int16_t a;
        int32_t b;
    } numbers_t;

    EXPORT int32_t add_numbers_t(numbers_t* n);

    EXPORT int32_t set_and_get_error(int32_t err);
    EXPORT int32_t get_error();
}