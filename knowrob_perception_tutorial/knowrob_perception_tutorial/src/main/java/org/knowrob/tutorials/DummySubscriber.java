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

package org.knowrob.tutorials;

import geometry_msgs.Pose;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import knowrob_tutorial_msgs.ObjectDetection;

import org.knowrob.prolog.PrologInterface;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;


/**
 * Subscriber to KnowRob tutorial dummy object detections publisher
 * 
 * @author Moritz Tenorth, tenorth@cs.uni-bremen.de
 *
 */
public class DummySubscriber extends AbstractNodeMain {

	ConnectedNode node;
	BlockingQueue<ObjectDetection> detections;
	Thread updateKnowRobObjDetections;
	
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("knowrob_tutorial_dummy_subscriber");
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// save reference to the ROS node
		this.node = connectedNode;
		
		this.detections = new LinkedBlockingQueue<ObjectDetection>();
		
		
		// wait for node to be ready
		try {
			while(node ==null) {
				Thread.sleep(200);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// start subscriber in a separate thread that fills the 'detections' queue
	    Subscriber<ObjectDetection> subscriber = connectedNode.newSubscriber("dummy_object_detections", ObjectDetection._TYPE);
	    subscriber.addMessageListener(new MessageListener<ObjectDetection>() {
	      @Override
	      public void onNewMessage(knowrob_tutorial_msgs.ObjectDetection message) {
	    	  try {
				detections.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	      }
	    });

	    
	    // start thread that reads the detections and adds them to KnowRob
		updateKnowRobObjDetections = new Thread( new UpdateKnowrobThread() );
		updateKnowRobObjDetections.start();
		
	}



	/**
	 * Read perceptions from the QueueingCallback buffer and create the
	 * corresponding object representations in KnowRob.
	 *
	 * @author Moritz Tenorth, tenorth@cs.uni-bremen.de
	 *
	 */
	public class UpdateKnowrobThread implements Runnable {

		@Override 
		public void run() {

			try {
				
				node.executeCancellableLoop(new CancellableLoop() {
					
					@Override
					protected void loop() throws InterruptedException {

						ObjectDetection obj = detections.take();

						Matrix4d p = quaternionToMatrix(obj.getPose().getPose());					
						String q = "create_object_perception(" +
									"'http://knowrob.org/kb/knowrob.owl#"+obj.getType()+"', [" 
									+ p.m00 + ","+ p.m01 + ","+ p.m02 + ","+ p.m03 + ","
									+ p.m10 + ","+ p.m11 + ","+ p.m12 + ","+ p.m13 + ","
									+ p.m20 + ","+ p.m21 + ","+ p.m22 + ","+ p.m23 + ","
									+ p.m30 + ","+ p.m31 + ","+ p.m32 + ","+ p.m33 +
									"], ['DummyObjectDetection'], ObjInst)";

						// uncomment to see the resulting query printed to the KnowRob console
						//System.err.println(q);
						
						PrologInterface.executeQuery(q);
					}
				});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
