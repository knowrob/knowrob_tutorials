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


import java.util.Random;

import ros.*;
import ros.communication.Time;
import ros.pkg.knowrob_perception_tutorial.msg.ObjectDetection;
import ros.pkg.knowrob_perception_tutorial.srv.DetectObject;


/**
 * Simple service that return object detections at random positions
 * 
 * @author Moritz Tenorth, tenorth@cs.uni-bremen.de
 *
 */
public class DummyService {

	static Boolean rosInitialized = false;
	static Ros ros;
	static NodeHandle n;

	static Random rand;
	
	static String[] obj_types = new String[]{"Cup", "DinnerPlate", "TableKnife", "DinnerFork", "SoupSpoon", "DrinkingBottle"};
	
	
	
	/**
	 * Create dummy object detections of random type at random positions
	 * 
	 * @return Object detection message
	 */
	private static ObjectDetection generateDummyObjectDetection() {

		ObjectDetection obj =  new ObjectDetection();

		obj.type = obj_types[rand.nextInt(6)];

		obj.pose.header.frame_id = "map";
		obj.pose.header.stamp = Time.now();

		obj.pose.pose.position.x = rand.nextDouble() * 3;
		obj.pose.pose.position.y = rand.nextDouble() * 3;
		obj.pose.pose.position.z = rand.nextDouble() * 3;

		return obj;
		
	}
	

	/**
	 * Start service providing dummy object detections
	 * 
	 * @param args none
	 */
	public static void main(String[] args) {

		ros = Ros.getInstance();

		if(!Ros.getInstance().isInitialized()) {
			ros.init("knowrob_tutorial_dummy_service");
		}
		n = ros.createNodeHandle();
		rand = new Random(Time.now().nsecs);
		
		// create simple callback method that generates fake object detections
		// using the generateDummyObjectDetection method
		ServiceServer.Callback<DetectObject.Request,DetectObject.Response> scb =
				new ServiceServer.Callback<DetectObject.Request,DetectObject.Response>() {

			public DetectObject.Response call(DetectObject.Request request) {
				
				DetectObject.Response res = new DetectObject.Response();
				res.obj = generateDummyObjectDetection();
				return res;
			}
		};

		
		// start the service and spin
		try {
			
			n.advertiseService("dummy_object_detection", new DetectObject(), scb);
			ros.spin();
			
		} catch (RosException e) {
			e.printStackTrace();
		} finally {
			n.shutdown();
		}
	}
}
