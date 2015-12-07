using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Kinect;

namespace SoftLove.GestureBehaviour.Gestures
{
    class HandsTogetherGesture : IGestureRecognizer
    {
        /// <summary>
        /// Information à renvoyer
        /// </summary>
        /// <returns></returns>
        public string GetInfo()
        {
            return "MainsJointes";
        }

        /// <summary>
        /// Fonction de reconnaissance du geste "Mains Jointes"
        /// </summary>
        /// <param name="skeletonData"></param>
        /// <returns></returns>
        public bool IsRecognized(Skeleton[] skeleton)
        {
            try
            {
                if (skeleton.Length > 0)
                {
                    var skel = skeleton.Where(u => u.TrackingState == SkeletonTrackingState.Tracked).FirstOrDefault();
                    if (skel != null) {
                        return (Math.Abs(
                            skel.Joints[JointType.HandRight].Position.Y -
                            skel.Joints[JointType.HandLeft].Position.Y) +
                            Math.Abs(skel.Joints[JointType.HandRight].Position.X -
                            skel.Joints[JointType.HandLeft].Position.X)) < 0.04;
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
