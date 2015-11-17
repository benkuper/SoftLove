using UnityEngine;
using System.Collections;
using DG.Tweening;

public class Node : MonoBehaviour {

    Animator animator;
    bool opened;
    float rotSpeed;
    Transform innerCube;
    Vector3 initRot;
    Material cubeMat;
    
    public Color focusColor;

	// Use this for initialization
	void Start () {
        animator = GetComponent<Animator>();
        innerCube = transform.FindChild("loopCubeInner");
        initRot = innerCube.rotation.eulerAngles;
        cubeMat = innerCube.GetComponent<Renderer>().material;

        opened = false;
        cubeMat.color = Color.white * .2f;
	}
	
	// Update is called once per frame
	void Update () {
        if (opened)
        {
            innerCube.Rotate(Vector3.up * rotSpeed);
        }
	}

    public void setFocus(bool val)
    {
        animator.SetBool("opened", val);
        opened = val;

        DOTween.To(()=>this.rotSpeed, speed => this.rotSpeed = speed,opened?1:0, 1);

        if (!opened) innerCube.DORotate(initRot, 1);

        cubeMat.DOColor(opened ? focusColor : Color.white * .4f, 1);
    }
}
