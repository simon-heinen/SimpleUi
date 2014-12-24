package simpleui.dataStructures;

import java.util.ArrayList;
import java.util.HashMap;

public class HashMap3D<T> {

	HashMap<Long, T> hashMap = new HashMap<Long, T>();

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param t
	 * @return returns the old object located at the coordinates (or null if
	 *         there was none)
	 */
	public T add(short x, short y, short z, T t) {
		long key = 293 * 1 + x;
		key = 293 * key + y;
		key = 293 * key + z;
		return hashMap.put(key, t);
	}

	public T get(int x, int y, int z) {
		long key = 293 * 1 + x;
		key = 293 * key + y;
		key = 293 * key + z;
		return hashMap.get(key);
	}

	public T remove(int x, int y, int z) {
		long key = 293 * 1 + x;
		key = 293 * key + y;
		key = 293 * key + z;
		return hashMap.remove(key);
	}

	public HashMap<Long, T> getHashMap() {
		return hashMap;
	}

	public void getInBoxArea(ArrayList<T> results, int centerX, int centerY,
			int centerZ, int boxSize) {
		for (int x = 0; x < boxSize; x++) {
			for (int y = 0; y < boxSize; y++) {
				for (int z = 0; z < boxSize; z++) {
					T ele = get(centerX - boxSize / 2 + x, centerY - boxSize
							/ 2 + y, centerZ - boxSize / 2 + z);
					if (ele != null) {
						results.add(ele);
					}
				}
			}
		}
	}
}
