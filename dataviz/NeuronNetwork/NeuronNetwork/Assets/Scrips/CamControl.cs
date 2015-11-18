using UnityEngine;
using System.Collections;
using DG.Tweening;

public class CamControl : MonoBehaviour {

    Transform target;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if (target) transform.DOLookAt(target.position, .5f);
	}

    public void focus(Transform t,float distance)
    {
        target = t;

        transform.DOMove(getRandomFaceVec(t, distance, distance / 2f), 1f).SetEase(Ease.InOutQuad);
    }


    Vector3 getRandomFaceVec(Transform t, float dist, float random)
    {
        return new Vector3(t.position.x + Random.Range(-random, random), t.position.y + Random.Range(-random, random), t.position.z - dist);
    }
}



