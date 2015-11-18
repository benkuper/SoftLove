using DG.Tweening;
using System.Collections;
using UnityEngine;

public class Connection : MonoBehaviour {

    public Node n1;
    public Node n2;

    LineRenderer lr;
    Material lineMat;

    bool isActive;
    public Color activeColor;
    
    public float speed = 1;

    float _lineWidth = .02f;

   

	// Use this for initialization
	void Awake () {
        lr = GetComponent<LineRenderer>();
        lineMat = lr.material;
	}
	
	// Update is called once per frame
	void Update () {
	    if(n1 == null || n2 == null) return;

        lr.SetPosition(0, n1.transform.position);
        lr.SetPosition(1, n2.transform.position);

       
        lineMat.SetTextureOffset("_MainTex", new Vector2(Time.time * speed, 1));
	}

    public void setNodes(Node n1, Node n2)
    {
        this.n1 = n1;
        this.n2 = n2;
        if (n1 == null || n2 == null)
        {
            Debug.Log("Problem !" + n1 + "/" + n2);
            return;
        }
        float dist = Vector3.Distance(n1.transform.position, n2.transform.position);
        lineMat.SetTextureScale("_MainTex", Vector2.right * (dist*.3f+Random.value*5f));
    }

    public bool hasNode(Node n)
    {
        return n == n1 || n == n2;
    }

    public void setActive(bool val)
    {
        isActive = val;
        if (isActive)
        {
            lineMat.color = Color.white;
            lineWidth = .2f;
            DOTween.To(() => this.lineWidth, w => this.lineWidth = w, .02f, 1f);
        }

        speed = val ? Random.Range(.5f, 3f) : Random.Range(.1f, .4f);
        if (Random.value > .5f) speed = -speed;
        lineMat.DOColor(isActive ? activeColor : new Color(1, 1, 1, .1f), 1);
    }

    public void clean()
    {
        lineMat.DOColor(Color.black, 1);
    }


    public float lineWidth
    {
        get { return _lineWidth; }
        set { _lineWidth = value; lr.SetWidth(lineWidth, lineWidth); }
    }
}
