using UnityEngine;
using System.Collections;
using DG.Tweening;
public class HeartMonitor : MonoBehaviour {

    HeartTrail trail;
    TextMesh tm; 

    int heartRate;
    public float pulseHeight = 3;

    float lastPulse;
	// Use this for initialization
	void Start () {



        OSCMaster.heartReceived += heartReceived;

        trail = GetComponentInChildren<HeartTrail>();
        tm = GetComponentInChildren<TextMesh>();
        setHeartRate(0);

        pulse();

    } 
	
	// Update is called once per frame
	void Update () {
        float pulsePerSecond = 60f / heartRate;
	    if(Time.time-lastPulse > pulsePerSecond)
        {
            pulse();
        }
	}

    void heartReceived(int value)
    {
        setHeartRate(value);
    }

    void setHeartRate(int value)
    {
        heartRate = value;
        tm.text = "Heart Rate\n" + value + " bpm";
    }

    void pulse()
    {
        trail.ty = pulseHeight;
        DOTween.Kill("trailTween");
        DOTween.To(() => trail.ty, ty => trail.ty = ty, 0, .5f).SetId("trailTween").SetEase(Ease.OutElastic);
        lastPulse = Time.time;
    }
}
