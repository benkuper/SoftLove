using UnityEngine;
using System.Collections;
using DG.Tweening;

public class CamControl : MonoBehaviour {

    Transform target;
    float distance = 3f;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
       if(target) transform.DOLookAt(target.position, .5f);
	}

    public void focus(Transform t)
    {
        target = t;
        
        transform.DOMove(target.position+Random.onUnitSphere*distance, 1f);
    }
}
