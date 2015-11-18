using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.IO;
using System.Threading;

using Microsoft.Kinect;
using SoftLove.Exceptions;

namespace SoftLove.Zoning
{
    class ZoneRegister
    {
        /// <summary>
        /// Document XML utilise par le ZoneRgister
        /// </summary>
        private XmlDocument xmlDoc;

        public XmlDocument XmlDoc
        {
            get { return xmlDoc; }
            set { xmlDoc = value; }
        }

        /// <summary>
        /// Nom du fichier XML
        /// </summary>
        private string xmlName;

        public string XmlName
        {
            get { return xmlName; }
            set { xmlName = value; }
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

        /// <summary>
        /// Constructeur d'un ZoneRegister, utilise un fichier XML. Si le fichier n'existe pas, il sera cree.
        /// </summary>
        /// <param name="_xmlFileName"></param>
        public ZoneRegister(String _xmlFileName)
        {
            XmlDoc = new XmlDocument();
            XmlName = _xmlFileName;
            try
            {
                XmlDoc.Load(_xmlFileName);
            }catch (FileNotFoundException)
            {
                CreateEmptyXmlDocument(_xmlFileName);
            }
        }

        /// <summary>
        /// Lancement
        /// </summary>
        public void Launch()
        {
            InitSensor();
            bool creerZone = true;
            while (creerZone)
            {
                CreateZone();
                Console.Write("Créer une nouvelle zone (Y/n) ? ");
                if (Console.ReadLine() == "n") { creerZone = false; }
            }
            XmlDoc.Save(XmlName);
            Console.WriteLine("Exit...");
            Console.ReadKey();
        }

        /// <summary>
        /// Creation d'un nouveau fichier XML
        /// </summary>
        /// <param name="_xmlFileName"></param>
        private void CreateEmptyXmlDocument(string _xmlFileName)
        {
            XmlDoc.AppendChild(XmlDoc.CreateXmlDeclaration("1.0", "UTF-8", "yes"));
            XmlElement xmlZones = XmlDoc.CreateElement("zones");
            xmlZones.AppendChild(XmlDoc.CreateTextNode(""));
            XmlDoc.AppendChild(xmlZones);
            XmlDoc.Save(_xmlFileName);
            XmlDoc.Load(_xmlFileName);
        }

        /// <summary>
        /// Initialisation de la Kinect, activation du tracking de skeleton
        /// </summary>
        private void InitSensor()
        {
            Sensor = KinectSensor.KinectSensors.Where(s => s.Status == KinectStatus.Connected).FirstOrDefault(); // Init the kinect
            SkelData = new Skeleton[Sensor.SkeletonStream.FrameSkeletonArrayLength]; // Allocate ST data
            Sensor.SkeletonStream.Enable();
            Sensor.SkeletonFrameReady += AcquireSkeleton; // Get Ready for Skeleton Ready Events
            Sensor.Start();
        }

        /// <summary>
        /// Fonction d'acquisition du skeleton
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AcquireSkeleton(Object sender, SkeletonFrameReadyEventArgs e)
        {
            using(SkeletonFrame skeletonFrame = e.OpenSkeletonFrame()) // Open the Skeleton frame
            {
                if (skeletonFrame != null && SkelData != null) // check that a frame is available
                {
                    skeletonFrame.CopySkeletonDataTo(SkelData); // get the skeletal information in this frame
                }
            }
        }

        /// <summary>
        /// Creation d'une nouvelle zone avec 2 points
        /// </summary>
        private void CreateZone()
        {
            Console.Write("Zone ID (\"A\",\"B\",...) : ");
            Zone zone = new Zone(XmlDoc, Console.ReadLine());
            for (int i = 0; i < 2; i++)
            {
                Console.WriteLine("Point " + (i + 1) + ", appuyez sur Entrer quand vous êtes prêt...");
                Console.ReadKey();
                ConvertAndStoreSkelPosition(zone);
            }
            zone.Store();
        }

        /// <summary>
        /// Recupere les positions X et Z du skeleton, et cree un nouveau points dans la zone
        /// </summary>
        /// <param name="_zone"></param>
        private void ConvertAndStoreSkelPosition(Zone _zone)
        {
            try
            {
                if (SkelData.Length > 0)
                {
                    var skel = SkelData.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if(skel != null)
                    {
                        _zone.AddPoint(Math.Round(skel.Position.X,2), Math.Round(skel.Position.Z,2));
                    }    
                    else
                    {
                        throw new SkeletonNotFoundException("Skeleton not found");
                    }
                }
            }
            catch (SkeletonNotFoundException)
            {
                Console.WriteLine("Skeleton non trouvé, nouvelle tentative...");
                Thread.Sleep(500);
                ConvertAndStoreSkelPosition(_zone);
            }
        }
    }
}
