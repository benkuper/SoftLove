using UnityEngine;
using System.Collections;
using DG.Tweening;

public class Zone : MonoBehaviour {

    public string zoneID;

    public bool active;
    public Color matColor;

    float targetY;
    float targetScaleY;

    Transform aimX;
    Transform aimY;
    Transform aimZ;

  

    // Use this for initialization
    void Start() {
        targetY = transform.localPosition.y;
        targetScaleY = transform.localScale.y;

        aimX = transform.FindChild("aimX");
        aimY = transform.FindChild("aimY");
        aimZ = transform.FindChild("aimZ");

        GetComponent<Renderer>().material.color = matColor;
        aimX.GetComponent<Renderer>().material.color = matColor;
        aimY.GetComponent<Renderer>().material.color = matColor;
        aimZ.GetComponent<Renderer>().material.color = matColor;

        setActive(false);

    }
	
	// Update is called once per frame
	void Update () {
        if (Input.GetKeyDown(KeyCode.Z)) setActive(!active);
	}

    public void setAimPos(Vector3 pos)
    {
        aimX.DOLocalMove(new Vector3(0f, pos.y, pos.z),.2f);
        aimY.DOLocalMove(new Vector3(pos.x, 0, pos.z), .2f);
        aimZ.DOLocalMove(new Vector3(pos.x, pos.y, 0), .2f);
    }

    public void setActive(bool value)
    {
        active = value;
        transform.DOLocalMoveY(active ? targetY : .075f,.5f).SetEase(Ease.OutQuad);
        transform.DOScaleY(active ? targetScaleY : .1f, .3f).SetEase(Ease.OutQuad);
        setAimVisible(active);
    }

    void setAimVisible(bool value)
    {
        aimX.GetComponent<Renderer>().enabled = value;
        aimY.GetComponent<Renderer>().enabled = value;
        aimZ.GetComponent<Renderer>().enabled = value;
    }
}
