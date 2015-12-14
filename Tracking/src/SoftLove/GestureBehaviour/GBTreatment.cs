using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Kinect;
using SoftLove.Acquisition;
using System.Threading;
using SoftLove.GestureBehaviour.Gestures;

using SoftLove.Communication;

namespace SoftLove.GestureBehaviour
{
    class GBTreatment
    {   
        /// <summary>
        /// Liste des gestes à reconnaitre
        /// </summary>
        private List<IGestureRecognizer> recognizers;

        public List<IGestureRecognizer> Recognizers
        {
            get { return recognizers; }
            set { recognizers = value; }
        }

        /// <summary>
        /// Module d'acquisition du Skeleton. Obligatoire car ce module tire ses informations depuis le Skeleton
        /// </summary>
        private SkeletonAcquisition skeletonAcquire;

        public SkeletonAcquisition SkeletonAcquire
        {
            get { return skeletonAcquire; }
            set { skeletonAcquire = value; }
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
        /// Dernière information envoyée
        /// </summary>
        private string lastInfo;

        public string LastInfo
        {
            get { return lastInfo; }
            set { lastInfo = value; }
        }


        /// <summary>
        /// Instanciation du détecteur de gestes
        /// </summary>
        /// <param name="_pub"></param>
        public GBTreatment(Publisher _pub)
        {
            Pub = _pub;
            LastInfo = "";
            Recognizers = new List<IGestureRecognizer>();
            Pub.SendInfo(Publisher.GESTE, "start");
        }

        /// <summary>
        /// Envoi de l'information sur les gestes depuis le publisher
        /// </summary>
        private void SendGesture()
        {
            foreach (IGestureRecognizer recognizer in Recognizers)
            {
                if (recognizer.IsRecognized(SkeletonAcquire.SkelData))
                {
                    string newInfo = recognizer.GetInfo();
                    if (newInfo != LastInfo)
                    {
                        LastInfo = newInfo;  // Un geste ne peut donc être produit qu'une seule fois, il est nécessaire de produire un autre geste pour envoyer un nouveau geste
                        Pub.SendInfo(Publisher.GESTE, LastInfo);
                    }
                }
            }
        }

        /// <summary>
        /// Ajout d'un geste à la liste des gestes à reconnaitre
        /// </summary>
        /// <param name="_gR"></param>
        public void AttachGestureRecognizer(IGestureRecognizer _gR)
        {
            Recognizers.Add(_gR);
        }

        private bool ContainsStandSit()
        {
            foreach (IGestureRecognizer classe in Recognizers)
            {
                if (classe is StandSitGesture) return true;
            }
            return false;
        }

        private StandSitGesture GetStandSitGesture()
        {
            foreach (IGestureRecognizer classe in Recognizers)
            {
                if (classe is StandSitGesture) return (StandSitGesture)classe;
            }
            return null;
        }

        /// <summary>
        /// Lancement
        /// </summary>
        public void Launch()
        {
            if (this.ContainsStandSit()){
                GetStandSitGesture().InitPosition(SkeletonAcquire);
            }
            while (true)
            {
                SendGesture();
                Thread.Sleep(500);
            }
        }
    }
}
