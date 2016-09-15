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


import java.util.Random;

import knowrob_tutorial_msgs.ObjectDetection;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import org.ros.node.topic.Publisher;


/**
 * Simple publisher that publishes object detections at random positions on a topic
 * 
 * @author Moritz Tenorth, tenorth@cs.uni-bremen.de
 *
 */
public class DummyPublisher extends AbstractNodeMain {

	Random rand;
	ConnectedNode node;
	static String[] obj_types = new String[]{"Cup", "DinnerPlate", "TableKnife", "DinnerFork", "SoupSpoon", "DrinkingBottle"};


	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("knowrob_tutorial_dummy_publisher");
	}


	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// save reference to the ROS node
		this.node = connectedNode;
		this.rand = new Random(node.getCurrentTime().nsecs);
		
		final Publisher<knowrob_tutorial_msgs.ObjectDetection> publisher =
				connectedNode.newPublisher("dummy_object_detections", knowrob_tutorial_msgs.ObjectDetection._TYPE);

		// This CancellableLoop will be canceled automatically when the node shuts down.
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			
			@Override
			protected void loop() throws InterruptedException {

				ObjectDetection obj = generateDummyObjectDetection();
				publisher.publish(obj);

				System.out.println("detected " + obj.getType());
				Thread.sleep(1000);
			}
		});
	}




	/**
	 * Create dummy object detections of random type at random positions
	 * 
	 * @return Object detection message
	 */
	private ObjectDetection generateDummyObjectDetection() {

		ObjectDetection obj =  node.getTopicMessageFactory().newFromType(ObjectDetection._TYPE);

		obj.setType(obj_types[rand.nextInt(6)]);

		obj.getPose().getHeader().setFrameId("map");
		obj.getPose().getHeader().setStamp(node.getCurrentTime());

		obj.getPose().getPose().getPosition().setX(rand.nextDouble() * 3);
		obj.getPose().getPose().getPosition().setY(rand.nextDouble() * 3);
		obj.getPose().getPose().getPosition().setZ(rand.nextDouble() * 3);

		obj.getPose().getPose().getOrientation().setW(1);
		obj.getPose().getPose().getOrientation().setX(0);
		obj.getPose().getPose().getOrientation().setY(0);
		obj.getPose().getPose().getOrientation().setZ(0);

		return obj;

	}
}
