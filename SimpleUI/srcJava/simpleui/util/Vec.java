package simpleui.util;

import java.io.Serializable;

/**
 * see {@link Vec#Vec(float, float, float)}
 * 
 * @author Spobo
 * 
 */
public class Vec implements Serializable {

	private static final float SMALLEST_DISTANCE = 0.0001f;
	private static final String LOG_TAG = "Vec";
	public static final float deg2rad = 0.01745329238474369f;
	final public static float rad2deg = (float) (180.0f / Math.PI);

	/**
	 * @param x
	 *            value on red axis (east direction=longitude)
	 * @param y
	 *            value on green axis (north direction=latitude)
	 * @param z
	 *            value on blue axis (sky direction=altitude=height)
	 */
	public float x, y, z = 0;

	/**
	 * @param x
	 *            value on red axis (east direction=longitude)
	 * @param y
	 *            value on green axis (north direction=latitude)
	 * @param z
	 *            value on blue axis (sky direction=altitude=height)
	 */
	public Vec(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * inits x,y,z with 0
	 */
	public Vec() {
	}

	public Vec(Vec vecToCopy) {
		this.x = vecToCopy.x;
		this.y = vecToCopy.y;
		this.z = vecToCopy.z;
	}

	/**
	 * @param vec2
	 * @return itself, no copy! just if you want to go on doing more like
	 *         v.add(v2).add(v3)
	 */
	public Vec add(Vec vec2) {
		x += vec2.x;
		y += vec2.y;
		z += vec2.z;
		return this;
	}

	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	/**
	 * returns the leght of a given vector
	 * 
	 * @param a
	 * @return
	 */
	public static float vectorLength(Vec a) {
		return (float) Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
	}

	public Vec sub(Vec vec2) {
		x -= vec2.x;
		y -= vec2.y;
		z -= vec2.z;
		return this;
	}

	public Vec normalize() {
		return mult(1 / vectorLength(this));
	}

	public static float abs(float d) {
		if (d < 0) {
			return -d;
		}
		return d;
	}

	public Vec mult(float factor) {
		x = x * factor;
		y = y * factor;
		z = z * factor;
		return this;
	}

	/**
	 * rotates the vector COUNTERCLOCKWISE around the z axis. Warning this is
	 * different to {@link GLCamera#getCameraAnglesInDegree()}[0] which will
	 * give the rotation CLOCKWISE around the Z axsis
	 * 
	 * <br>
	 * <br>
	 * 
	 * EXAMPLE: vector (0,10,0) is currently pointing north. passing 90 degree
	 * here will cause it to point to -10 0 0 (so west)
	 * 
	 * <br>
	 * <br>
	 * 
	 * if you want to rotate according to the current camera rotation you have
	 * to calculate 360-angle first
	 * 
	 * @param angleInDegree
	 */
	public synchronized Vec rotateAroundZAxis(double angleInDegree) {
		/*
		 * Rotation matrix:
		 * 
		 * cos a -sin a 0
		 * 
		 * sin a cos a 0
		 * 
		 * 0 0 1
		 */
		angleInDegree = Math.toRadians(angleInDegree);
		float cos = (float) Math.cos(angleInDegree);
		float sin = (float) Math.sin(angleInDegree);
		float x2 = cos * x - sin * y;
		y = sin * x + cos * y;
		x = x2;
		return this;
	}

	/**
	 * @param r0
	 *            the reference circle radius agains which the second will be
	 *            compared
	 * @param r1
	 * @param d
	 * @return the percentage (from 0 to 1) how much the second circle overlaps
	 *         with the first one (e.g. r0=2, r1=1, d=0 will result in 0.25)
	 */
	public static double getCircleIntersectionAreaInPercent(double r0,
			double r1, double d) {
		double r = getCircleIntersectionArea(r0, r1, d);
		return r / (Math.PI * r0 * r0);
	}

	public static double getCircleIntersectionArea(double r0, double r1,
			double d) {
		double rr0 = r0 * r0;
		double rr1 = r1 * r1;

		// Circles do not overlap
		if (d > r1 + r0) {
			return 0;
		}

		// Circle1 is completely inside circle0
		else if (d <= Math.abs(r0 - r1) && r0 >= r1) {
			// Return area of circle1
			return Math.PI * rr1;
		}

		// Circle0 is completely inside circle1
		else if (d <= Math.abs(r0 - r1) && r0 < r1) {
			// Return area of circle0
			return Math.PI * rr0;
		}

		// Circles partially overlap
		else {
			double phi = (Math.acos((rr0 + (d * d) - rr1) / (2 * r0 * d))) * 2;
			double theta = (Math.acos((rr1 + (d * d) - rr0) / (2 * r1 * d))) * 2;
			double area1 = 0.5 * theta * rr1 - 0.5 * rr1 * Math.sin(theta);
			double area2 = 0.5 * phi * rr0 - 0.5 * rr0 * Math.sin(phi);

			// Return area of intersection
			return area1 + area2;
		}
	}

	/**
	 * To get the point 10 meters away 30 degree clockwise from north you just
	 * have to pass (10, 30) as parameters
	 * 
	 * @param distanceInMeters
	 * @param angleInDegree
	 *            CLOCKWISE rotation angle
	 * @return
	 */
	public static Vec rotatedVecInXYPlane(float distanceInMeters,
			double angleInDegree) {
		Vec v = new Vec(distanceInMeters, 0, 0);
		v.rotateAroundZAxis(angleInDegree);
		return v;
	}

	/**
	 * Rotates counterclockwise around the x axis
	 * 
	 * @param angleInDegree
	 */
	public synchronized Vec rotateAroundXAxis(double angleInDegree) {
		/*
		 * Rotation matrix:
		 * 
		 * 1 0 0
		 * 
		 * 0 cos a sin a
		 * 
		 * 0 -sin a cos a
		 */
		angleInDegree = Math.toRadians(angleInDegree);
		float cos = (float) Math.cos(angleInDegree);
		float sin = (float) Math.sin(angleInDegree);
		float y2 = cos * y + sin * z;
		z = cos * z - sin * y;
		y = y2;
		return this;
	}

	public Vec div(float factor) {
		x = x / factor;
		y = y / factor;
		z = z / factor;
		return this;
	}

	public static Vec mult(float factor, Vec oldVec) {
		Vec v = oldVec.copy();
		v.x *= factor;
		v.y *= factor;
		v.z *= factor;
		return v;
	}

	/**
	 * @param a
	 * @param b
	 * @return a positive distance value or -1 if something was wrong
	 */
	public static float distance(Vec a, Vec b) {
		if (a == null || b == null) {
			return -1;
		}
		return vectorLength(new Vec(a.x - b.x, a.y - b.y, a.z - b.z));
	}

	/**
	 * The same method like {@link Vec#distance(Vec, Vec)} but only the XY-plane
	 * is taken into account
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float XYdistance(Vec a, Vec b) {
		if (a == null || b == null) {
			return -1;
		}
		return vectorLength(new Vec(a.x - b.x, a.y - b.y, 0));
	}

	private static boolean parallelVecs(Vec vec1, Vec vec2) {
		if ((vec1.copy().normalize()).equals(vec2.copy().normalize())) {
			return true;
		}
		return false;
	}

	/**
	 * @param vec
	 * @param factor
	 *            the same factor as in {@link Vec#round(float)}
	 * @return
	 */
	public boolean equals(Vec vec, float factor) {

		this.round(factor);
		vec.round(factor);

		if ((x == vec.x) && (y == vec.y) && (z == vec.z)) {
			return true;
		}
		return false;
	}

	/**
	 * @param factor
	 *            pass 100 if you want to cut 0.12345678 to 0.12 and 1000 to cut
	 *            it to 0.123
	 */
	public void round(float factor) {
		x = Math.round(x * factor) / factor;
		y = Math.round(y * factor) / factor;
		z = Math.round(z * factor) / factor;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vec) {
			return equals((Vec) o, 1000000f);
		}
		return super.equals(o);
	}

	public static Vec add(Vec a, Vec b) {
		float x = a.x + b.x;
		float y = a.y + b.y;
		float z = a.z + b.z;
		return new Vec(x, y, z);
	}

	/**
	 * @param a
	 * @param b
	 * @return a-b
	 */
	public static Vec sub(Vec a, Vec b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		return new Vec(x, y, z);
	}

	/**
	 * returns the orthogonal vector in 2d so z=0
	 * 
	 * @param a
	 * @return
	 */
	public static Vec getOrthogonalHorizontal(Vec a) {
		/*
		 * => a*orthogonal=0 <=> a.x*orthogonal.x+a.y*orthogonal.y=0
		 * 
		 * set orthogonal.y=-1 => orthogonal.x=a.y/a.x
		 */
		if (a.x == 0) {
			return new Vec(1, 0, 0);
		}
		return new Vec(a.y / a.x, -1, 0);
	}

	// TODO you can't mirror a 3d line an another line so extend to plane
	// // this "mirrors" a vector (for eg the mirror line is the ground =(1,0)
	// and
	// // a is the force of a ball which hits it
	// public static Vec mirror(Vec mirrorLine, Vec a) {
	// /*
	// * Its a orthogonal projection: => a'=a+(a *
	// * mirrorLine)/(|mirrorLine|^2) * mirrorLine
	// */
	//
	// // TODO not correct i think, it returns the senkrechte??
	// // has to be Vec mirrored = Vec.sub(mult(2,Vec.project(a,
	// // mirrorLine)),a); ??
	//
	// Vec mirrored = Vec.add(a, Vec.project(a, mirrorLine));
	// return mirrored;
	// }

	/**
	 * This is the scalar product of two vectors
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static float multScalar(Vec a, Vec b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	// Read also info at Vec.mirror()
	/** returns the shadow of the tree on the ground TODO add explanation here! */
	public static Vec orthogonalProjection(Vec tree, Vec ground) {
		return mult((multScalar(tree, ground) / (multScalar(ground, ground))),
				ground);
	}

	public Vec copy() {
		return new Vec(x, y, z);
	}

	/**
	 * @param length
	 * @return the resized vector to allow chains
	 */
	public Vec setLength(float length) {
		if (length == 0f) {
			length = 0.00000001f;
		}
		if (x == 0f && y == 0f && z == 0f) {
			Log.w(LOG_TAG,
					"Request to setLength on 0 Vector which is impossible");
			return this;
		}
		return mult(length / vectorLength(this));
	}

	public float getLength() {
		return Vec.vectorLength(this);
	}

	public void setToVec(Vec b) {
		x = b.x;
		y = b.y;
		z = b.z;
	}

	public void setToZero() {
		x = 0;
		y = 0;
		z = 0;
	}

	@Override
	public String toString() {
		return "Vec: (" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * can be used for smooth scrolling or just morphing from vec a to b
	 * 
	 * @param target
	 * @param newPos
	 * @param factor
	 *            should be >0 and <1
	 * @return
	 */
	public static void morphToNewVec(Vec target, Vec newPos, float factor) {
		if (factor > 1) {
			factor = 1;
		}
		/*
		 * the other way would be:
		 * 
		 * final Vec dif = Vec.sub(newPos, target); dif.mult(factor);
		 * target.add(dif);
		 */

		target.x += factor * (newPos.x - target.x);
		target.y += factor * (newPos.y - target.y);
		target.z += factor * (newPos.z - target.z);
	}

	public static void morphToNewAngleVec(Vec target, Vec newRotation,
			float timeDelta) {
		morphToNewAngleVec(target, newRotation.x, newRotation.y, newRotation.z,
				timeDelta);
	}

	/**
	 * 
	 * @param target
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param timeDelta
	 */
	public static void morphToNewAngleVec(Vec target, float newX, float newY,
			float newZ, float timeDelta) {
		if (timeDelta > 1) {
			timeDelta = 1;
		}

		float deltaX = (newX - target.x);
		if (deltaX > 180) {
			target.x -= (360 - deltaX) * timeDelta;
		} else if (deltaX < -180) {
			target.x += (360 + deltaX) * timeDelta;
		} else if (!(-SMALLEST_DISTANCE < deltaX && deltaX < SMALLEST_DISTANCE)) {
			target.x += (deltaX) * timeDelta;
		}
		if (target.x < 0) {
			target.x += 360;
		}
		if (target.x >= 360) {
			target.x -= 360;
		}

		float deltaY = (newY - target.y);
		if (deltaY > 180) {
			target.y -= (360 - deltaY) * timeDelta;
		} else if (deltaY < -180) {
			target.y += (360 + deltaY) * timeDelta;
		} else if (!(-SMALLEST_DISTANCE < deltaY && deltaY < SMALLEST_DISTANCE)) {
			target.y += (deltaY) * timeDelta;
		}
		if (target.y < 0) {
			target.y += 360;
		}
		if (target.y >= 360) {
			target.y -= 360;
		}

		float deltaZ = newZ - target.z;
		if (deltaZ > 180) {
			target.z -= (360 - deltaZ) * timeDelta;
		} else if (deltaZ < -180) {
			target.z += (360 + deltaZ) * timeDelta;
		} else if (!(-SMALLEST_DISTANCE < deltaZ && deltaZ < SMALLEST_DISTANCE)) {
			target.z += (deltaZ) * timeDelta;
		}
		if (target.z < 0) {
			target.z += 360;
		}
		if (target.z >= 360) {
			target.z -= 360;
		}

	}

	public boolean isNullVector() {
		return ((x == 0) && (y == 0) && (z == 0));
	}

	/**
	 * see {@link Vec#toAngleVec()} <br>
	 * <br>
	 * 
	 * After this method there are some common adjustments.<br>
	 * The following is for object rotation: <br>
	 * v.x -= 90;<br>
	 * v.z *= -1;<br>
	 * obj.setRotation(v); <br>
	 * <br>
	 * The other one is for camera rotation: <br>
	 * v.x*=-1; <br>
	 * camera.setRotation(v); <br>
	 * <br>
	 * The reason is that OpenGL e.g. needs negative x rotation values, or the
	 * -90 is because the object should not be rotated when the rotation is
	 * parallel to the ground
	 * 
	 * 
	 * @param from
	 *            the position where you are standing
	 * @param to
	 *            the position where you want to look at
	 */
	public void toAngleVec(Vec from, Vec to) {
		this.setToVec(to);
		sub(from);
		toAngleVec();
	}

	/**
	 * x=0 if the angle is facing to the ground, 90 if it looks to the horizon
	 * and 180 if it looks in the sky. All the values in between are possible
	 * too. So x will be between 0 and 180<br>
	 * <br>
	 * 
	 * y=always 0<br>
	 * <br>
	 * 
	 * z=the compass angle. 0 is north, 45 is northeast, 90 is east,
	 */
	public void toAngleVec() {
		if ((x == 0) && (y == 0)) {
			if (z > 0) {
				// the angle is looking directly in the sky (eg 0,0,1)
				x = 180;
				y = 0;
				z = 0;
				return;
			} else {
				// the angle is looking directly on the ground (eg 0,0,-1)
				x = 0;
				y = 0;
				z = 0;
				return;
			}
		}

		/*
		 * arcCos has a value range from -180 to 180 so you have to check on
		 * which side (east (x>=0) or west (x<0) if it would be a compass) the x
		 * value is
		 */
		float zAngle;
		if (x >= 0) {
			zAngle = (float) Math.acos(y / Math.sqrt(x * x + y * y)) * rad2deg;
		} else {
			zAngle = 360.0f - (float) Math.acos(y / Math.sqrt(x * x + y * y))
					* rad2deg;
		}

		/*
		 * if y is 0 the x value cant be set to 0 as well, so check this first
		 * and if y is 0 use angle between x and z and not y and z as usual
		 */
		if (y != 0) {
			x = 0;
			x = (float) Math.acos(-z / getLength()) * rad2deg;
		} else {
			x = (float) Math.acos(-z / getLength()) * rad2deg;
		}

		y = 0;
		// then set the compass rotation to the z value
		z = zAngle;
	}

	public static Vec copy(Vec valueVec) {
		if (valueVec == null) {
			return null;
		}
		return valueVec.copy();
	}

	/**
	 * @return the mirrored version on the x y and z axis
	 */
	public Vec getNegativeClone() {
		return new Vec(-x, -y, -z);
	}

	/**
	 * @param center
	 * @param minDistance
	 *            to the center
	 * @param maxDistance
	 *            to the center
	 * @return A random vector with the same z value
	 */
	public static Vec getNewRandomPosInXYPlane(Vec center, float minDistance,
			float maxDistance) {

		if (center == null) {
			return null;
		}

		float rndDistance = (float) (Math.random()
				* (maxDistance - minDistance) + minDistance);
		Vec rndPos = new Vec(rndDistance, 0, 0);
		rndPos.rotateAroundZAxis(Math.random() * 359);
		rndPos.x += center.x;
		rndPos.y += center.y;
		rndPos.z = 0;
		return rndPos;
	}

	public static Vec getNewRandomPosInXYZ(Vec center, float minDistance,
			float maxDistance) {

		if (center == null) {
			return null;
		}

		float rndDistance = (float) (Math.random()
				* (maxDistance - minDistance) + minDistance);
		Vec rndPos = new Vec(rndDistance, 0, 0);
		rndPos.rotateAroundXAxis(Math.random() * 359);
		rndPos.rotateAroundZAxis(Math.random() * 359);
		rndPos.x += center.x;
		rndPos.y += center.y;
		rndPos.z += center.z;
		return rndPos;
	}

	/**
	 * Given 2 vectors this calculates the vector which is orthogonal to the
	 * plane the two vectors create
	 * 
	 * @param v1
	 * @param v2
	 * @return the orthogonal vector
	 */
	public static Vec calcNormalVec(Vec uVec, Vec vVec) {
		Vec ret = new Vec();
		ret.x = (uVec.y * vVec.z) - (uVec.z * vVec.y);
		ret.y = (uVec.z * vVec.x) - (uVec.x * vVec.z);
		ret.z = (uVec.x * vVec.y) - (uVec.y * vVec.x);
		return ret;
	}

	public float scalarMult(Vec b) {
		return x * b.x + y * b.y + z * b.z;
	}

	public void setTo(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * rotation counterclockwise starting from east axis
	 * 
	 * @param x
	 * @param y
	 * @return a value from 0 to 359
	 */
	public static float getRotationAroundZAxis(float x, float y) {
		/*
		 * Scalarproduct rule:
		 * 
		 * cos(w)=a*b/(|a|*|b|)
		 * 
		 * a is (1,0,0)
		 */
		float result = (float) Math.acos((x / Math.sqrt(x * x + y * y)))
				* rad2deg;
		if (y < 0) {
			return 360 - result;
		}
		return result;

	}
}
