using UnityEngine;
using System.Collections;
using System.IO;

public class DataText : MonoBehaviour {

    TextMesh tm;
    string[] lines;
    int numLines;

    int viewLines = 10;
    int _start;
   

	// Use this for initialization
    void Start()
    {
        tm = GetComponent<TextMesh>(); 
        
        StreamReader myFile = new StreamReader(Application.dataPath + "/ipsum.txt");
        string myString = myFile.ReadToEnd();
        setText(myString);

        setEnabled(false);
    }

    public void setEnabled(bool val)
    {
        GetComponent<Renderer>().enabled = val;
    }
	
	// Update is called once per frame
	void Update () {
        
	}


    void loopSeek()
    {
        CancelInvoke("loopSeek");
        start += 3;
        Invoke("loopSeek", .05f + Random.value * .5f);
    }

    void setText(string txt)
    {
        lines = txt.Split(new char[] { '\n' });
        numLines = lines.Length;
        start = 0;

        loopSeek();
    }

    void updateTM()
    {
        string s = "";
        for (int i = start; i < start + viewLines && i < numLines; i++)
        {
            s += lines[i] + "\n";
        }

        tm.text = s;
    }

    public int start
    {
        get { return _start; }
        set { 
            _start = value;
            if (start >= lines.Length) start = 0;
            updateTM();
        }
    }
}
