using UnityEngine;
using System.Collections;
using DG.Tweening;

public class EmotionSlider : MonoBehaviour {

    Transform bar;
    public string type;

	// Use this for initialization
	void Start () {
        bar = transform.FindChild("bg").FindChild("bar");
        setValue(.5f);
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void setValue(float value)
    {
        bar.DOScaleX(value*.99f,.3f);
    }
}
