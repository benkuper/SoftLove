using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Kinect;

namespace SoftLove.GestureBehaviour.Gestures
{
    class HelloGesture : IGestureRecognizer
    {
        /// <summary>
        /// Informations à renvoyer
        /// </summary>
        /// <returns></returns>
        public string GetInfo()
        {
            return "hello";
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
                        return ((skel.Joints[JointType.HandRight].Position.Y -
                                skel.Joints[JointType.Head].Position.Y) > 0)
                                || ((skel.Joints[JointType.HandLeft].Position.Y -
                                skel.Joints[JointType.Head].Position.Y) > 0)
                                ;
                    }
                }
                return false;
            }
            catch (NullReferenceException)
            {
                return false;
            }
        }
    }
}
