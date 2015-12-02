using UnityEngine;
using System.Collections;

public class Emotion : MonoBehaviour {

    EmotionSlider[] sliders;

	// Use this for initialization
	void Start () {
        OSCMaster.emotionReceived += emotionReceived;
        sliders = GetComponentsInChildren<EmotionSlider>();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    void emotionReceived(string type, float value)
    {
        DataText.log("Emotion received : " + type + " > " + value);
        EmotionSlider s = getSliderForType(type);
        if (s == null) return;
        s.setValue(value);
    }

    EmotionSlider getSliderForType(string type)
    {
        foreach (EmotionSlider s in sliders) if (s.type == type) return s;
        return null;
    }
}
