using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Microsoft.Kinect;

namespace SoftLove.Factory
{
    static class SensorFactory
    {
        /// <summary>
        /// Génère une instance de KinectSensor par ID
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public static KinectSensor GenerateSensor(string id)
        {
            return KinectSensor.KinectSensors.Where(s => s.Status == KinectStatus.Connected).Where(i => i.UniqueKinectId == id).FirstOrDefault();
        }

        /// <summary>
        /// Génère une instance du premier KinectSensor disponible
        /// </summary>
        /// <returns></returns>
        public static KinectSensor GenerateSensor()
        {
            return KinectSensor.KinectSensors.Where(s => s.Status == KinectStatus.Connected).FirstOrDefault();
        }
    }
}
