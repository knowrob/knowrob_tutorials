/*
 * Copyright (C) 2012-2014 by Moritz Tenorth
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

package org.knowrob.tutorials;

import geometry_msgs.Pose;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import knowrob_tutorial_msgs.ObjectDetection;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;



/**
 * Service client for the KnowRob tutorial dummy object detector
 * 
 * @author Moritz Tenorth, tenorth@cs.uni-bremen.de
 */
public class DummyClient extends AbstractNodeMain {

	ServiceClient<knowrob_tutorial_msgs.DetectObjectRequest, knowrob_tutorial_msgs.DetectObjectResponse> serviceClient;
	ConnectedNode node;
	
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("knowrob_tutorial_client");
	}


	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// save reference to the ROS node
		this.node = connectedNode;
		
	}


	/**
	 * Call the dummy_object_detector service and return the result
	 * 
	 * @return An ObjectDetection with the pose and type of the detected object
	 */
	public ObjectDetection callObjDetectionService() {
		
		// start service client
		try {
			serviceClient = node.newServiceClient("dummy_object_detector", knowrob_tutorial_msgs.DetectObject._TYPE);
			
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		
		final knowrob_tutorial_msgs.DetectObjectRequest req = serviceClient.newMessage();
		
		
		// call the service and 
		serviceClient.call(req, new ServiceResponseListener<knowrob_tutorial_msgs.DetectObjectResponse>() {
			
			@Override
			public void onSuccess(knowrob_tutorial_msgs.DetectObjectResponse response) {
				node.getLog().info(String.format("Detected object of type %d", response.getObj().getType()));
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
		
		// TODO: build structure using queue to return the result of the service call
		return r;
	}

	/**
	 * Utility method: convert a ROS pose into a Java vecmath 4x4 pose matrix
	 *
	 * @param p Pose (ROS geometry_msgs)
	 * @return 4x4 pose matrix
	 */
	public static Matrix4d quaternionToMatrix(Pose p) {

		return new Matrix4d(new Quat4d(p.getOrientation().getX(), p.getOrientation().getY(), p.getOrientation().getZ(), p.getOrientation().getW()), 
				new Vector3d(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ()), 1.0);
	}
}
