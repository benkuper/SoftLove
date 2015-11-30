using UnityEngine;
using System.Collections;

public class CameraOrbit : MonoBehaviour {

	public Transform target;
	Vector2 pos;

	public Vector3 offset;

	public float distance = 10f;
	public Vector2 speed = new Vector2 (250, 120);
	public Vector2 yLimit = new Vector2(-20,80);

	bool active;
			
	void Start () {

		Vector3 angles = transform.eulerAngles;
		pos = new Vector2(angles.x,angles.y);
	}
	
	void LateUpdate () {
		active = Input.GetMouseButton (1);
		if (target) {
			
			distance -= Input.mouseScrollDelta.y;

			if(active)
			{
				pos.x += Input.GetAxis("Mouse X") * speed.x * 0.02f;
				pos.y -= Input.GetAxis("Mouse Y") * speed.y * 0.02f;
				
				pos.y = ClampAngle(pos.y, yLimit.x, yLimit.y);
			}

			Quaternion rotation = Quaternion.Euler(pos.y, pos.x, 0);
			Vector3 position = rotation * new  Vector3(0f, 0f, -distance) + target.position;
			
			transform.rotation = rotation;
			transform.position = position + offset;
		}
	}
	
	float ClampAngle (float angle, float min, float max) {
		if (angle < -360)
			angle += 360;
		if (angle > 360)
			angle -= 360;
		return Mathf.Clamp (angle, min, max);
	}
	
}
