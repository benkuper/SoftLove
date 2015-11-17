using UnityEngine;
using System.Collections;
using DG.Tweening;

public class Connection : MonoBehaviour {

    public Node n1;
    public Node n2;

    LineRenderer lr;
    Material lineMat;

    bool isActive;
    public Color activeColor;

	// Use this for initialization
	void Start () {
        lr = GetComponent<LineRenderer>();
        lineMat = lr.material;
        setActive(false);
	}
	
	// Update is called once per frame
	void Update () {
	    if(n1 == null || n2 == null) return;

        lr.SetPosition(0, n1.transform.position);
        lr.SetPosition(1, n2.transform.position);
	}

    public void setNodes(Node n1, Node n2)
    {
        this.n1 = n1;
        this.n2 = n2;
    }

    public bool hasNode(Node n)
    {
        return n == n1 || n == n2;
    }

    public void setActive(bool val)
    {
        isActive = val;
        lineMat.DOColor(isActive?activeColor:Color.white*.2f,1);
    }
}
