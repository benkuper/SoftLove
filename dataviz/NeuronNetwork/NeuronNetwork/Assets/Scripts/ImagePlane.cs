using UnityEngine;
using System.Collections;
using DG.Tweening;

public class ImagePlane : MonoBehaviour {

    Texture2D oldTexture;
    Texture2D currentTexture;

    Material mat;

    WWW www;
    public float alpha;

    // Use this for initialization
    void Start()
    {
        mat = GetComponent<Renderer>().material;
        currentTexture = new Texture2D(4, 4, TextureFormat.DXT1, false);
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void setImageURL(string url)
    {
        StartCoroutine("loadImageURL", url);
    }

    IEnumerator loadImageURL(string url)
    {
        mat.color = Color.red;
        // assign the downloaded image to the main texture of the object
        if (currentTexture != null)
        {
            oldTexture = currentTexture;
        }

       
        www = new WWW(url);
        yield return www;

      
        if(www.error == null && www.isDone)
        {
            currentTexture = www.texture;
            if (oldTexture != null)
            {
                glitchLoop();
                Invoke("fixTexture", .5f);
            }
            else
            {
                fixTexture();
            }
        }else
        {
            Debug.LogWarning("Got Error loading " + url + " > " + www.error);
            currentTexture = null;
        }

       
        
    }

    void glitchLoop()
    {
        if (oldTexture != null)
        {
            mat.mainTexture = Random.value > .5f ? currentTexture : oldTexture;
            mat.color = Color.white;
            transform.DOScaleX(transform.localScale.y *  mat.mainTexture.width /mat.mainTexture.height, .3f);
        }
        Invoke("glitchLoop", .02f);

    }

    void fixTexture()
    {
        CancelInvoke("glitchLoop");
        mat.mainTexture = currentTexture;
    }

    public void setAlpha(float val)
    {
        alpha = val;
        mat.color = new Color32((byte)(mat.color.r*255), (byte)(mat.color.g*255), (byte)(mat.color.b*255), (byte)(alpha*255));
    }
}
