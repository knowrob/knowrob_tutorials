/** <module> perception_tutorial

  This module provides routines to interface the the object_detection_detector
  system, i.e. to read data and interpret the results.

  Copyright (C) 2010 by Moritz Tenorth

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a ttoy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

@author Moritz Tenorth
@license GPL
*/

:- module(perception_tutorial,
    [
      comp_object_detection/2,
      obj_detections_listener/1
    ]).

:- use_module(library('semweb/rdf_db')).
:- use_module(library('semweb/rdfs')).
:- use_module(library('owl')).
:- use_module(library('owl_parser')).
:- use_module(library('knowrob_coordinates')).


:- rdf_db:rdf_register_ns(knowrob,  'http://knowrob.org/kb/knowrob.owl#',  [keep(true)]).



% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
% Service-based perception interface
%

%% comp_object_detection(-ObjInst) is det.
%
% Queries the dummy_object_detector service and creates the
% internal object representations in the knowledge base.
%
% @param ObjInst IRI of the created object instance
%
comp_object_detection(ObjInst, _ObjClass) :-

  % Call the DetectObject service for retrieving a new object detection.
  % The method returns a reference to the Java ObjectDetection message object
  jpl_new('org.knowrob.tutorials.DummyClient', [], Client),
  jpl_list_to_array(['org.knowrob.tutorials.DummyClient'], Arr),
  jpl_call('org.knowrob.utils.ros.RosUtilities', runRosjavaNode, [Client, Arr], _),

  jpl_call(Client, 'callObjDetectionService', [], ObjectDetection),

  % Read type -> simple string; combine with KnowRob namespace
  jpl_call(ObjectDetection, 'getType', [], T),
  atom_concat('http://knowrob.org/kb/knowrob.owl#', T, Type),

  jpl_call(ObjectDetection, 'getPose', [], PoseMatrix),
  knowrob_coordinates:matrix4d_to_list(PoseMatrix,PoseList),


  % Create the object representations in the knowledge base
  % The third argument is the type of object perception describing 
  % the method how the object has been detected
  create_object_perception(Type, PoseList, ['DummyObjectDetection'], ObjInst).






% % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % % %
%
% Topic-based perception interface
%


%% obj_detections_listener(-Listener) is det.
%
% Creates a DummySubscriber and starts the threads for listening to object
% detections published on the /dummy_object_detections topic. The detections
% are then added to KnowRob by the listener (which is running in a separate
% thread in parallel to KnowRob).
%
% @param Listener Reference to the Java DummySubscriber object
%
obj_detections_listener(Listener) :-
  jpl_new('org.knowrob.tutorials.DummySubscriber', [], Listener),
  jpl_list_to_array(['org.knowrob.tutorials.DummySubscriber'], Arr),
  jpl_call('org.knowrob.utils.ros.RosUtilities', runRosjavaNode, [Listener, Arr], _).




