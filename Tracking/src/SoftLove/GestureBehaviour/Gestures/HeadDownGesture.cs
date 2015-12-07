using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Kinect;

namespace SoftLove.GestureBehaviour.Gestures
{
    class HeadDownGesture : IGestureRecognizer
    {
        public string GetInfo()
        {
            return "HeadDown";
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
                        return (Math.Abs(
                            skel.Joints[JointType.Head].Position.Y -
                            skel.Joints[JointType.ShoulderCenter].Position.Y)) < 0.19;
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
