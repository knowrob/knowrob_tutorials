cmake_minimum_required(VERSION 2.4.6)
include($ENV{ROS_ROOT}/core/rosbuild/rosbuild.cmake)

rosbuild_init()

set(EXECUTABLE_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/bin)
set(LIBRARY_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/lib)

rosbuild_genmsg()
rosbuild_gensrv()


add_classpath(${PROJECT_SOURCE_DIR}/bin)
add_runtime_classpath(${PROJECT_SOURCE_DIR}/bin)

rosbuild_find_ros_package(rosjava_jni)
add_classpath(${rosjava_jni_PACKAGE_PATH}/bin)

add_java_source_dir(${PROJECT_SOURCE_DIR}/src)
rospack_add_java_executable(dummy_service edu.tum.cs.ias.knowrob.tutorial.DummyService)
rospack_add_java_executable(dummy_publisher edu.tum.cs.ias.knowrob.tutorial.DummyPublisher)

