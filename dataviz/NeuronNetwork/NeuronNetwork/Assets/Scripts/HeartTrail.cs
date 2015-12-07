using UnityEngine;
using System.Collections;

public class HeartTrail : MonoBehaviour {

    public float radius = 1;
    public float speed = 1;
    public float ty = 0;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        float pos = Time.time * speed;
        float tx = Mathf.Cos(pos) * radius;
        float tz = Mathf.Sin(pos) * radius;
        transform.localPosition = new Vector3(tx, ty, tz);
	}
}
