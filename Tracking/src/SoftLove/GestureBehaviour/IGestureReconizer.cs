using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Kinect;

namespace SoftLove.GestureBehaviour
{
    interface IGestureRecognizer
    {
        /// <summary>
        /// Vrai si reconnu, faux sinon
        /// </summary>
        /// <returns></returns>
        bool IsRecognized(Skeleton[] skeleton);

        /// <summary>
        /// Renvoi une string décrivant le geste
        /// </summary>
        /// <returns></returns>
        string GetInfo();
    }
}
