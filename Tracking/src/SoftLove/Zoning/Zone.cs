using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using SoftLove.Exceptions;

namespace SoftLove.Zoning
{
    /// <summary>
    /// Une Zone contient 2 points
    /// </summary>
    class Zone
    {
        /// <summary>
        /// ID {A,B,C,...}
        /// </summary>
        private string id;

        public string ID
        {
            get { return id; }
            set { id = value; }
        }

        /// <summary>
        /// List de Point (length = 2)
        /// </summary>
        private List<Point> points;

        public List<Point> Points
        {
            get { return points; }
            set { points = value; }
        }

        /// <summary>
        /// Document XML dans lequel sauvegarder la zone
        /// </summary>
        private XmlDocument xmlDoc;

        public XmlDocument XmlDoc
        {
            get { return xmlDoc; }
            set { xmlDoc = value; }
        }

        /// <summary>
        /// Creation d'une Zone avec _xmlDoc comme document XML de sauvegarder, et _idZone comme ID
        /// </summary>
        /// <param name="_xmlDoc"></param>
        /// <param name="_idZone"></param>
        public Zone(XmlDocument _xmlDoc, string _idZone)
        {
            ID = _idZone;
            Points = new List<Point>(2);
            XmlDoc = _xmlDoc;
        }

        public Zone(XmlNode _xmlZone)
        {
            Points = new List<Point>(2);
            CreateZoneFromXml(_xmlZone);
        }

        /// <summary>
        /// Ajout d'un point dans la zone
        /// </summary>
        /// <param name="_x"></param>
        /// <param name="_z"></param>
        public void AddPoint(double _x, double _z)
        {
            Points.Add(new Point(_x, _z));
        }

        /// <summary>
        /// Sauvegarde de la zone dans le fichier 
        ///         Y 
        ///         |
        ///         |
        /// ________|________ X
        ///         |\
        ///         | \
        ///         |  \
        ///             Z
        /// </summary>
        public void Store()
        {
            if(Points.Count != 2)
            {
                throw new NumberOfPointsException("Le nombre de points doit-être de 2 si vous voulez enregistrer une zone");
            }
            var xmlZone = XmlDoc.CreateElement("zone");
            xmlZone.SetAttribute("id",ID);
            var xmlZones = XmlDoc.DocumentElement;
            xmlZones.AppendChild(xmlZone);
            foreach (Point point in Points)
            {
                var xmlPoint = XmlDoc.CreateElement("point");
                xmlZone.AppendChild(xmlPoint);
                var xmlPointX = XmlDoc.CreateElement("x");
                xmlPointX.AppendChild(XmlDoc.CreateTextNode(point.X.ToString()));
                xmlPoint.AppendChild(xmlPointX);
                var xmlPointZ = XmlDoc.CreateElement("z");
                xmlPointZ.AppendChild(XmlDoc.CreateTextNode(point.Z.ToString()));
                xmlPoint.AppendChild(xmlPointZ);
            }
        }

        private void CreateZoneFromXml(XmlNode _xmlNode)
        {
            ID = _xmlNode.Attributes["id"].Value;
            foreach (XmlNode point in _xmlNode.ChildNodes)
            {
                Points.Add(new Point(Convert.ToDouble(point.ChildNodes[0].InnerXml), Convert.ToDouble(point.ChildNodes[1].InnerXml)));
            }
        }

        public bool InZone(double x, double z)
        {
            Console.WriteLine("Zone:" + ID);
            Console.WriteLine("X:" + x + " Z:" + z);
            Console.WriteLine("X1:" + Points[0].X + " Z1:" + Points[0].Z);
            Console.WriteLine("X2:" + Points[1].X + " Z2:" + Points[1].Z);
            return ( (x <= Math.Max(Points[0].X,Points[1].X))
                && (x >= Math.Min(Points[0].X, Points[1].X))
                && (z <= Math.Max(Points[0].Z, Points[1].Z))
                && (z >= Math.Min(Points[0].Z, Points[1].Z)) );
        }
    }

    /// <summary>
    /// Un Point est defini par une valeur X et une valeur Z
    /// </summary>
    internal class Point
    {
        /// <summary>
        /// x value
        /// </summary>
        private double x;

        public double X
        {
            get { return x; }
            set { x = value; }
        }

        /// <summary>
        /// y value
        /// </summary>
        private double z;

        public double Z
        {
            get { return z; }
            set { z = value; }
        }

        /// <summary>
        /// Creation d'un Point
        /// </summary>
        /// <param name="_x"></param>
        /// <param name="_z"></param>
        public Point(double _x, double _z)
        {
            X = _x;
            Z = _z;
        }
    }
}
