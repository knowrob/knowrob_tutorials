cmake_minimum_required(VERSION 2.4.6)
include($ENV{ROS_ROOT}/core/rosbuild/rosbuild.cmake)

rosbuild_init()
rosbuild_include(rosprolog utils)

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



# replace path to local OWL files

rosbuild_find_ros_package(ias_knowledge_base)
set( OWL_PATH_PREFIX ${ias_knowledge_base_PACKAGE_PATH} )
fix_ros_destination_path(OWL_PATH_PREFIX)

file(GLOB inputs owl/*.in)
foreach( _in ${inputs} )
  string(REPLACE ".in" "" _out ${_in})
  configure_file( ${_in} ${_out} )
endforeach( _in )