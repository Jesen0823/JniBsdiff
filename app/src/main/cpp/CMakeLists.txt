cmake_minimum_required(VERSION 3.10.2)

project("jnibsdiff")

add_library(
        jnibsdiff
        SHARED
        native-lib.cpp)

find_library(
        log-lib
        log)


target_link_libraries(
        jnibsdiff
        ${log-lib})