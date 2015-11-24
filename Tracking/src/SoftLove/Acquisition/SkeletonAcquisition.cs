using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

using Microsoft.Kinect;
using SoftLove.Zoning;
using SoftLove.GestureBehaviour;

namespace SoftLove.Acquisition
{
    class SkeletonAcquisition
    {

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
        /// Données du skeletons
        /// </summary>
        private Skeleton[] skelData;

        public Skeleton[] SkelData
        {
            get { return skelData; }
            set { skelData = value; }
        }

        /// <summary>
        /// Objet Localisation associé
        /// </summary>
        private Localisation localizer;

        public Localisation Localizer
        {
            get { return localizer; }
            set { localizer = value; }
        }

        /// <summary>
        /// Objet GBTreatement associé
        /// </summary>
        private GBTreatment gestureRecognizer;

        public GBTreatment GestureRecognizer
        {
            get { return gestureRecognizer; }
            set { gestureRecognizer = value; }
        }

        /// <summary>
        /// Instanciation d'un nouveau SkeletonAcquisition
        /// </summary>
        /// <param name="_sensor"></param>
        /// <param name="_localizer"></param>
        /// <param name="_gestureRecognizer"></param>
        public SkeletonAcquisition(KinectSensor _sensor, Localisation _localizer, GBTreatment _gestureRecognizer)
        {
            Sensor = _sensor;
            Localizer = _localizer;
            Localizer.SkeletonAcquire = this;
            // TODO
            //GestureRecognizer = _gestureRecognizer;
            //GestureRecognizer.SkeletonAcquire = this;

        }

        /// <summary>
        /// Instanciation d'un nouveau SkeletonAcquisition. Nécessite d'associer un Localizer et un GestureRecognizer si besoin
        /// </summary>
        /// <param name="_sensor"></param>
        public SkeletonAcquisition(KinectSensor _sensor)
        {
            Sensor = _sensor;
        }

        /// <summary>
        /// Initialisation de la Kinect, activation du tracking de skeleton
        /// </summary>
        public void InitSensor()
        {
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
            using (SkeletonFrame skeletonFrame = e.OpenSkeletonFrame()) // Open the Skeleton frame
            {
                if (skeletonFrame != null && SkelData != null) // check that a frame is available
                {
                    skeletonFrame.CopySkeletonDataTo(SkelData); // get the skeletal information in this frame
                }
            }
        }

        /// <summary>
        /// Lancement
        /// </summary>
        public void Launch()
        {
            try
            {
                if (!Sensor.IsRunning)
                {
                    InitSensor();
                }
                if (Localizer != null)
                {
                    Thread locThread = new Thread(Localizer.Launch);
                    locThread.Start();
                }
                if (GestureRecognizer != null)
                {
                    // TODO
                }
            }
            catch (NullReferenceException)
            {
                Console.WriteLine("Aucune Kinect connectée...");
            }
        }
    }
}
