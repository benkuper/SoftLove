using UnityEngine;
using System.Collections;
using DG.Tweening;

public class Node : MonoBehaviour {

    bool opened;
    public float rotSpeed = .5f;
    public float posSpeed = 1f;
    public float posRadius = 3f;
    Vector3 initPos;

    Material mat;

    float scaleSeed;
    
    public Color focusColor;

	// Use this for initialization
	void Start () {
        initPos = transform.position;

        scaleSeed = Random.Range(.2f, .7f);
        setBaseScale(.5f);
        transform.rotation = Random.rotation;
        opened = false;
        mat = GetComponent<Renderer>().material;
        setFocus(false);
	}

    public void setBaseScale(float baseScale)
    {
       transform.localScale = Vector3.one * baseScale * scaleSeed;
    }
	
	// Update is called once per frame
	void Update () {
        if (!opened)
        {
            transform.Rotate(Vector3.up * rotSpeed);
            float tx = Mathf.PerlinNoise(initPos.x + Time.time * posSpeed, 0)-.5f;
            float ty = Mathf.PerlinNoise(0, initPos.y + Time.time * posSpeed)-.5f;
            float tz = Mathf.PerlinNoise(Time.time * posSpeed, Time.time * posSpeed)-.5f;
            transform.position = initPos + new Vector3(tx, ty, tz) * posRadius;
        }
        else
        {
            
        }
	}

    public void setFocus(bool val)
    {
        //animator.SetBool("opened", val);
        opened = val;

        //DOTween.To(()=>this.rotSpeed, speed => this.rotSpeed = speed,opened?1:0, 1);

        //if (!opened) innerCube.DORotate(initRot, 1);

        mat.DOColor(opened ? focusColor : Color.white * .4f, 1);
    }
}
