package org.knowrob.tutorials;

import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

public class ObjectDetection {

	public String type;
	public Matrix4d pose;
	
	public ObjectDetection(knowrob_tutorial_msgs.ObjectDetection o) {
		this.type = o.getType();
		this.pose = quaternionToMatrix(o.getPose());
	}

	public String getType() {
		return type;
	}
	
	public Matrix4d getPose() {
		return pose;
	}
	
	
	/**
	 * Utility method: convert a ROS pose into a Java vecmath 4x4 pose matrix
	 *
	 * @param p Pose (ROS geometry_msgs)
	 * @return 4x4 pose matrix
	 */
	private Matrix4d quaternionToMatrix(PoseStamped ps) {

		Pose p = ps.getPose();
		return new Matrix4d(new Quat4d(p.getOrientation().getX(), p.getOrientation().getY(), p.getOrientation().getZ(), p.getOrientation().getW()), 
				new Vector3d(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ()), 1.0);
	}
}

