using UnityEngine;
using System.Collections;

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
//using System.Web;

public class Voxygen : MonoBehaviour {

    public string user = "leclairobscur@gmail.com";
    string key = "DrRg57k4";

    Voxygen instance;
    AudioSource source;

    public enum Voice {Philippe, ArnaudAutoritaire, ArnaudEnjoue,ArnaudNeutre}
    public string text;

    void Awake()
    {
        instance = this;
    }

    // Use this for initialization
    void Start () {
        source = GetComponent<AudioSource>();

        speech("Cool%20super");
        //playRequest("http://ws.voxygen.fr/tts1?coding=lin&frequency=16000&header=wav-header&text=Cool+ta+mere+encul%25C3%25&user=leclairobscur@gmail.com&voice=Philippe&hmac=e52176278bc61f8c01ff413f4abc8d76");
    }

    // Update is called once per frame
    void Update()
    {
    }

    public void speech(string text)
    {
        Debug.Log("Speech "+ text);
        
        string request = getRequest(text);
        playRequest(request);
    }

    public void playRequest(string request)
    {
        StartCoroutine("playRequestCoroutine", request);

    }

    IEnumerator playRequestCoroutine(string request)
    {
        WWW www = new WWW(request);
        yield return www;

        try
        {
            Debug.Log("Got result " + www.text);
            source.clip = www.GetAudioClip(false,true, AudioType.WAV);
            source.Play();
        }
        catch (Exception e)
        { 
            Debug.Log("Error retrieving result (" + e.Message + "), www data :" + www.text);
        }
    }

    string getRequest(string text)
    {
        System.Collections.Generic.SortedList<string, string> paramsOrdered = new SortedList<string, string>();
        paramsOrdered.Add("user", user);
        paramsOrdered.Add("voice", "Philippe");
        paramsOrdered.Add("coding", "lin");
        paramsOrdered.Add("parsing", "tags");
        paramsOrdered.Add("frequency", "48000");
        paramsOrdered.Add("header", "wav-header");

         

        byte[] bytes = Encoding.UTF8.GetBytes(text);
        //Console.Write(BitConverter.ToString(bytes));
        paramsOrdered.Add("text", Encoding.UTF8.GetString(bytes));

        //Calcul de la valeur de hashsage
        System.Text.UTF8Encoding encoding = new System.Text.UTF8Encoding();
        byte[] keyByte = encoding.GetBytes(key);

        System.Security.Cryptography.HMACMD5 hmacMD5 = new System.Security.Cryptography.HMACMD5(keyByte);
        hmacMD5.Initialize();

        string concatParams = "";

        for (int i = 0; i < paramsOrdered.Count; i++)
            concatParams += paramsOrdered.ElementAt(i).Key + "=" + paramsOrdered.ElementAt(i).Value;

        byte[] hashByte = hmacMD5.ComputeHash(encoding.GetBytes(concatParams));

        // Construction de l'url d'invocation
        string webRequestString = @"http://ws.voxygen.fr/ws/tts1?";
        for (int i = 0; i < paramsOrdered.Count; i++)
        {
            if (i > 0) webRequestString += "&";
            webRequestString += paramsOrdered.ElementAt(i).Key + "=" + paramsOrdered.ElementAt(i).Value;
        }

        webRequestString += "&hmac=" + ByteToString(hashByte);
        return webRequestString;
    }
   

     public static String ByteToString(byte[] ba)
    {
        StringBuilder hex = new StringBuilder(ba.Length * 2);
        foreach (byte b in ba)
            hex.AppendFormat("{0:x2}", b);
        return hex.ToString();
     }
}
