using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SoftLove.Acquisition;
using System.Threading;
using Microsoft.Kinect;

namespace SoftLove.GestureBehaviour.Gestures
{
    class StandSitGesture : IGestureRecognizer
    {
        /// <summary>
        /// Paramètres
        /// </summary>
        private double a, b;

        /// <summary>
        /// Positions
        /// </summary>
        private double y1 = 0,
        y2 = 0,
        z1 = 0,
        z2 = 0;

        private string actualPosition = "";

        /// <summary>
        /// Retourne "debout" si le skelette est debout
        /// </summary>
        /// <returns></returns>
        public string GetInfo()
        {
            return actualPosition;
        }

        public bool IsRecognized(Skeleton[] skeleton)
        {
            try
            {
                if (skeleton.Length > 0)
                {
                    var skel = skeleton.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if (skel != null)
                    {
                        if (skel.Position.Y > (a * skel.Position.Z + b)){actualPosition = "debout";}
                        else { actualPosition = "assis"; }
                        return true;
                    }
                }
                return false;
            }
            catch (NullReferenceException)
            {
                return false;
            }
        }


        public void InitPosition(SkeletonAcquisition acquire)
        {
            unsafe
            {
                Console.WriteLine("Point 1 à enregistrer, appuyez sur Entrée...");
                Console.ReadKey();
                StorePoint1(acquire);

                Console.WriteLine("Point 2 à enregistrer, appuyez sur Entrée...");
                Console.ReadKey();
                StorePoint2(acquire);

                a = (y2 - y1) / (z2 - z1);
                b = y1 - a * z1;
                Console.WriteLine("y = "+a+"x + "+b);
            }
        }

        
        /// <summary>
        /// Ces fonctions sont horriblement moches, désolé...
        /// </summary>
        /// <param name="acquire"></param>
        private void StorePoint1(SkeletonAcquisition acquire)
        {
            try
            {
                if (acquire.SkelData.Length > 0)
                {
                    var skel = acquire.SkelData.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if (skel != null)
                    {
                        y1 = skel.Position.Y;
                        z1 = skel.Position.Z;
                    }
                }
            }
            catch (NullReferenceException)
            {
                Console.WriteLine("Aucun Skeleton trouvé, nouvelle tentative...");
                Thread.Sleep(500);
                unsafe
                {
                    StorePoint1(acquire);
                }
            }
        }

        /// <summary>
        /// Ces fonctions sont horriblement moches, désolé...
        /// </summary>
        /// <param name="acquire"></param>
        private void StorePoint2(SkeletonAcquisition acquire)
        {
            try
            {
                if (acquire.SkelData.Length > 0)
                {
                    var skel = acquire.SkelData.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if (skel != null)
                    {
                        y2 = skel.Position.Y;
                        z2 = skel.Position.Z;
                    }
                }
            }
            catch (NullReferenceException)
            {
                Console.WriteLine("Aucun Skeleton trouvé, nouvelle tentative...");
                Thread.Sleep(500);
                unsafe
                {
                    StorePoint2(acquire);
                }
            }
        }
    }
}
