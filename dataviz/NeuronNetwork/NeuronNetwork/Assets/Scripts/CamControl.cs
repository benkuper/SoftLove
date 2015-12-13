using UnityEngine;
using System.Collections;
using DG.Tweening;

public class CamControl : MonoBehaviour {

    Transform target;
    Vector3 initPos;

    CC_Glitch glitch1;
    GlitchEffect glitch2;

    Material pingMat;

	// Use this for initialization
	void Start () {
        initPos = transform.position;
        glitch1 = GetComponent<CC_Glitch>();
        glitch2 = GetComponent<GlitchEffect>();

        OSCMaster.glitchReceived += glitchReceived;
        OSCMaster.pingReceived += pingReceived;

        pingMat = transform.FindChild("Ping").GetComponent<Renderer>().material;
    }

    


    // Update is called once per frame
    void Update () {
        if (target) transform.DOLookAt(target.position, .5f);
        else transform.DOLookAt(Vector3.zero, .5f);
    }

    public void focus(Transform t,float distance)
    {
        target = t;

        Vector3 targetPos;
        if (target != null) targetPos = getRandomFaceVec(t, distance, 1f);
        else targetPos = initPos;

        transform.DOMove(targetPos, 1f).SetEase(Ease.InOutQuad);

        OSCMaster.sendFloat("/dataviz/zoom", target != null ? 1 : 0);
      
    }

    Vector3 getRandomFaceVec(Transform t, float dist, float random)
    {
        return new Vector3(t.position.x + Random.Range(-random, random), t.position.y + Random.Range(-random, random), t.position.z - dist);
    }

    private void pingReceived()
    {
        //DataText.log("Ping !");
        pingMat.color = Color.white;
        pingMat.DOColor(Color.black, .3f);
    }

    void glitchReceived(int glitchID, bool value)
    {
        switch (glitchID)
        {
            case 1:
                glitch1.enabled = value;
                break;
            case 2:
                glitch2.enabled = value;
                break;
        }
    }

}



