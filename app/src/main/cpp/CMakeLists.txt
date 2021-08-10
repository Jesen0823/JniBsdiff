cmake_minimum_required(VERSION 3.10.2)

project("jnibsdiff")

file(GLOB bzip_source ${CMAKE_SOURCE_DIR}/bzip/*.c)

include_directories(${CMAKE_SOURCE_DIR}/bzip)

add_library(
        jnibsdiff
        SHARED
        native-lib.cpp
        #bspatcher.cpp
        bspatch.c
        ${bzip_source}
)

find_library(
        log-lib
        log)

target_link_libraries(
        jnibsdiff
        ${log-lib})