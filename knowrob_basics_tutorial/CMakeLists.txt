cmake_minimum_required(VERSION 2.4.6)
include($ENV{ROS_ROOT}/core/rosbuild/rosbuild.cmake)

rosbuild_init()


#######################################
# reset local paths in Prolog files

rosbuild_find_ros_package(knowrob_tutorial)
set( LOCAL_PACKAGE_PATH ${knowrob_tutorial_PACKAGE_PATH} )

rosbuild_find_ros_package(ias_knowledge_base)
set( OWL_PATH_PREFIX ${ias_knowledge_base_PACKAGE_PATH} )

rosbuild_find_ros_package(ias_semantic_map)
set( IAS_MAP_PATH_PREFIX ${ias_semantic_map_PACKAGE_PATH} )


file(GLOB inputs prolog/*.in)
foreach( _in ${inputs} )
  string(REPLACE ".in" "" _out ${_in})
  configure_file( ${_in} ${_out} )
endforeach( _in )

file(GLOB inputs owl/*.in)
foreach( _in ${inputs} )
  string(REPLACE ".in" "" _out ${_in})
  configure_file( ${_in} ${_out} )
endforeach( _in )


