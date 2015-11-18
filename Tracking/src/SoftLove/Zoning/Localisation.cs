using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Threading.Tasks;

using Microsoft.Kinect;
using SoftLove.Communication;
using SoftLove.Exceptions;
using System.Threading;

namespace SoftLove.Zoning
{
    class Localisation
    {
        public static readonly string ABSENT = "absent";
        public static readonly string PRESENT = "present";

        /// <summary>
        /// Zone Actuelle
        /// </summary>
        private string zone;

        public string Zone
        {
            get { return zone; }
            set { zone = value; }
        }

        /// <summary>
        /// Statut du skeleton
        /// </summary>
        private string statut;

        public string Statut
        {
            get { return statut; }
            set { statut = value; }
        }


        /// <summary>
        /// Liste des zones importees
        /// </summary>
        private List<Zone> zones;

        public List<Zone> Zones
        {
            get { return zones; }
            set { zones = value; }
        }

        /// <summary>
        /// Publisher du localisateur
        /// </summary>
        private Publisher pub;

        public Publisher Pub
        {
            get { return pub; }
            set { pub = value; }
        }

        /// <summary>
        /// Données du skeletons
        /// </summary>
        private Skeleton[] skelData;

        public Skeleton[] SkelData
        {
            get { return skelData; }
            set { skelData = value; }
        }

        /// <summary>
        /// Kinect
        /// </summary>
        private KinectSensor sensor;

        public KinectSensor Sensor
        {
            get { return sensor; }
            set { sensor = value; }
        }

        private string nomZoneDefaut;

        public string ZoneDefaut
        {
            get { return nomZoneDefaut; }
            set { nomZoneDefaut = value; }
        }



        public Localisation(string _filename, string _nomZoneDefaut, Publisher _pub)
        {
            Zones = new List<Zone>();
            ReadXml(_filename);
            Pub = _pub;
            InitSensor();
            Statut = ABSENT;
            Zone = "";
            ZoneDefaut = _nomZoneDefaut;
        }

        /// <summary>
        /// Initialisation de la Kinect, activation du tracking de skeleton
        /// </summary>
        private void InitSensor()
        {
            Sensor = KinectSensor.KinectSensors.Where(s => s.Status == KinectStatus.Connected).FirstOrDefault(); // Init the kinect
            SkelData = new Skeleton[Sensor.SkeletonStream.FrameSkeletonArrayLength]; // Allocate ST data
            Sensor.SkeletonStream.Enable();
            Sensor.SkeletonFrameReady += AcquireSkeletonAndSendPosition; // Get Ready for Skeleton Ready Events
            Sensor.Start();
        }

        /// <summary>
        /// Fonction d'acquisition du skeleton
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AcquireSkeletonAndSendPosition(Object sender, SkeletonFrameReadyEventArgs e)
        {
            using (SkeletonFrame skeletonFrame = e.OpenSkeletonFrame()) // Open the Skeleton frame
            {
                if (skeletonFrame != null && SkelData != null) // check that a frame is available
                {
                    skeletonFrame.CopySkeletonDataTo(SkelData); // get the skeletal information in this frame
                }
            }
        }


        private void SendPosition()
        {
            try
            {
                if (SkelData.Length > 0)
                {
                    var skel = SkelData.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if (skel != null)
                    {
                        Pub.SendInfo(Publisher.POSITION, Math.Round(skel.Position.X, 2) + ":" + Math.Round(skel.Position.Z, 2));
                    }
                }
            }
            catch (NullReferenceException)
            {
            }
        }

        private void SendZone()
        {
            try
            {
                if (SkelData.Length > 0)
                {
                    var skel = SkelData.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if (skel != null)
                    {
                        if (Statut == ABSENT)
                        {
                            Pub.SendInfo(Publisher.ZONE, PRESENT);
                            Statut = PRESENT;
                        }
                        FindZone(skel);
                    }
                    else
                    {
                        if (Statut == PRESENT)
                        {
                            Pub.SendInfo(Publisher.ZONE, ABSENT);
                            Statut = ABSENT;
                        }
                    }
                }
            }
            catch (NullReferenceException)
            {
            }
        }

        private void FindZone(Skeleton _skeleton)
        {
            bool zoneTrouvee = false;
            foreach (Zone zoneXML in Zones)
            {
                if (zoneXML.InZone(_skeleton.Position.X, _skeleton.Position.Z))
                {
                    if (zoneXML.ID != Zone)
                    {
                        Pub.SendInfo(Publisher.ZONE, zoneXML.ID);
                        Zone = zoneXML.ID;
                    }
                    zoneTrouvee = true;
                    break;
                }
            }
            if (!zoneTrouvee && Zone != ZoneDefaut)
            {
                Pub.SendInfo(Publisher.ZONE, ZoneDefaut);
                Zone = ZoneDefaut;
            }
        }

        /// <summary>
        /// Lecture du fichier XML
        /// </summary>
        /// <param name="_filename"></param>
        private void ReadXml(string _filename)
        {
            var doc = new XmlDocument();
            try
            {
                doc.Load(_filename);
                foreach (XmlNode elem in doc.DocumentElement.ChildNodes)
                {
                    Console.WriteLine(elem.InnerXml);
                    if(elem != null)
                    {
                        Zones.Add(new Zone(elem));
                    }
                }
            }
            catch (Exception ex) { Console.WriteLine(ex); }
        }

        /// <summary>
        /// Lancement
        /// </summary>
        public void Launch()
        {
            while (true)
            {
                SendPosition();
                SendZone();
            }
        }

    }
}
