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

        /// <summary>
        /// Ajout d'un point dans la zone
        /// </summary>
        /// <param name="_x"></param>
        /// <param name="_y"></param>
        public void AddPoint(double _x, double _y)
        {
            Points.Add(new Point(_x, _y));
        }

        /// <summary>
        /// Sauvegarde de la zone dans le fichier XML
        /// </summary>
        public void Store()
        {
            if(Points.Count != 2)
            {
                throw new NumberOfPointsException("Number of points must be 2 if you want to store a zone");
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
                var xmlPointY = XmlDoc.CreateElement("y");
                xmlPointY.AppendChild(XmlDoc.CreateTextNode(point.Y.ToString()));
                xmlPoint.AppendChild(xmlPointY);
            }
        }
    }

    /// <summary>
    /// Un Point est defini par une valeur X et une valeur Y
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
        private double y;

        public double Y
        {
            get { return y; }
            set { y = value; }
        }

        /// <summary>
        /// Creation d'un Point
        /// </summary>
        /// <param name="_x"></param>
        /// <param name="_y"></param>
        public Point(double _x, double _y)
        {
            X = _x;
            Y = _y;
        }
    }
}
