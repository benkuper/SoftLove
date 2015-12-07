using UnityEngine;
using System.Collections;
using UnityEditor;


[CustomEditor(typeof(Voxygen))]
public class VoxygenEditor : Editor {


    public override void OnInspectorGUI()
    {
        DrawDefaultInspector();

        Voxygen voxygen = (Voxygen)target; 
        if (GUILayout.Button("Speech")) voxygen.speech(voxygen.text);
    }
}
