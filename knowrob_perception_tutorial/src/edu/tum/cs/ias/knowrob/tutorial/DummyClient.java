/*
 * Copyright (C) 2012 by Moritz Tenorth
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.tum.cs.ias.knowrob.tutorial;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import ros.*;

import ros.pkg.geometry_msgs.msg.Pose;
import ros.pkg.knowrob_perception_tutorial.msg.ObjectDetection;
import ros.pkg.knowrob_perception_tutorial.srv.DetectObject;


/**
 * Service client for the KnowRob tutorial dummy object detector
 * 
 * @author Moritz Tenorth, tenorth@cs.uni-bremen.de
 */
public class DummyClient {

	static Boolean rosInitialized = false;
	static Ros ros;
	static NodeHandle n;


	/**
	 * Constructor: initializes the ROS environment
	 *
	 * @param node_name A unique node name
	 */
	public DummyClient(String node_name) {
		initRos(node_name);
	}


	/**
	 * Initialize the ROS environment if it has not yet been initialized
	 *
	 * @param node_name A unique node name
	 */
	protected static void initRos(String node_name) {

		ros = Ros.getInstance();

		if(!Ros.getInstance().isInitialized()) {
			ros.init(node_name);
		}
		n = ros.createNodeHandle();

	}


	/**
	 * Call the dummy_object_detector service and return the result
	 * 
	 * @return An ObjectDetection with the pose and type of the detected object
	 */
	public ObjectDetection callObjDetectionService() {

		ObjectDetection r=null;
		try {

			// call the dummy_object_detector service
			DetectObject.Request req = new DetectObject.Request();

			ServiceClient<DetectObject.Request, DetectObject.Response, DetectObject> cl =
					n.serviceClient("/dummy_object_detector", new DetectObject());

			r = cl.call(req).obj;
			cl.shutdown();

		} catch (RosException e) {
			ros.logError("ROSClient: Call to service /dummy_object_detector failed");
		}
		return r;
	}


	/**
	 * Utility method: convert a ROS pose into a Java vecmath 4x4 pose matrix
	 *
	 * @param p Pose (ROS geometry_msgs)
	 * @return 4x4 pose matrix
	 */
	protected static Matrix4d quaternionToMatrix(Pose p) {

		return new Matrix4d(new Quat4d(p.orientation.x, p.orientation.y, p.orientation.z, p.orientation.w), 
				new Vector3d(p.position.x, p.position.y, p.position.z), 1.0);
	}
}
