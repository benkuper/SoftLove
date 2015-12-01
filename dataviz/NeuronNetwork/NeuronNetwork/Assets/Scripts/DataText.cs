using UnityEngine;
using System.Collections;
using System.IO;

public class DataText : MonoBehaviour {

    static DataText instance;

    TextMesh tm;
    string[] lines;
    int numLines;

    public int viewLines = 10;
    int _start;

    int maxLogs = 22;
    int currentLog;

    void Awake()
    {
        instance = this;
        tm = GetComponent<TextMesh>();
        
    }
	// Use this for initialization
    void Start()
    {
        OSCMaster.consoleReceived += consoleReceived;
        OSCMaster.logReceived += logReceived;
        OSCMaster.clearConsoleReceived += clearConsoleReceived;

        clearConsole();
        log("Console init.");
        
        //StreamReader myFile = new StreamReader(Application.dataPath + "/ipsum.txt");
        //string myString = myFile.ReadToEnd();
        //setLoopText(myString);

    }

    

    private void logReceived(string text)
    {
        log(text);
    }

    private void consoleReceived(bool active)
    {
        setEnabled(active);
    }

    private void clearConsoleReceived()
    {
        clearConsole();
    }

    public void setEnabled(bool val)
    {
        GetComponent<Renderer>().enabled = val;
    }
	
	// Update is called once per frame
	void Update () {
        
	}


    public static void log(string text)
    {
        if (instance.currentLog == instance.maxLogs) clearConsole();
        instance.tm.text = text + "\n" + instance.tm.text;
        instance.currentLog++;
    }
    
    public static void clearConsole()
    {
        instance.tm.text = "";
        instance.currentLog = 0;
    }

    void loopSeek()
    {
        CancelInvoke("loopSeek");
        start += 3;
        Invoke("loopSeek", .05f + Random.value * .5f);
    }

    void setLoopText(string txt)
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
