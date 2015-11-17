using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;

using ZeroMQ;

using SoftLove.Exceptions;


namespace SoftLove.Communication
{
    
    public class Publisher
    {
        public static readonly string GESTE = "GESTE";
        public static readonly string ZONE = "ZONE";
        public static readonly string POSITION = "POSITION";

        /// <summary>
        /// Socket du Publisher
        /// </summary>
        private ZSocket socket;

        public ZSocket Socket
        {
            get { return socket; }
            set { socket = value; }
        }

        /// <summary>
        /// Context de la Socket
        /// </summary>
        private ZContext context;

        public ZContext Context
        {
            get { return context; }
            set { context = value; }
        }

        /// <summary>
        /// Creation d'un Publisher
        /// Les valeurs par defaut sont _ip = "*", _port = 5555
        /// </summary>
        /// <param name="_ip"></param>
        /// <param name="_port"></param>
        public Publisher(string _ip = "*", int _port = 5555)
        {
            // Initialisation
            Context = new ZContext();
            Socket = new ZSocket(context, ZSocketType.PUB);

            // Bind
            this.BindSocket(_ip, _port);
        }

        /// <summary>
        /// Construit l'information avec un ID et une information
        /// 
        /// <exception cref="IdPublisherException">
        /// Thrown when <paramref name="_id"/> isn't in {Publisher.GESTE,Publisher.ZONE,Publisher.POSITION}
        /// </exception>
        /// </summary>
        /// <param name="_id"></param>
        /// <param name="_info"></param>
        public void SendInfo(string _id, string _info)
        {
            if (_id != GESTE.ToString() && _id != ZONE.ToString() && _id != POSITION.ToString())
            {
                throw new IdPublisherException("Id must be in {" + GESTE + "," + ZONE + "," + POSITION + "}");
            }
            Socket.Send(new ZFrame(_id + " " + _info));
            Console.WriteLine("Sent : " + _id + " " + _info);
        }

        /// <summary>
        /// Bind the socket to a specific IP and port
        /// IP can be "*" (= all interfaces)
        /// </summary>
        /// <param name="_ip"></param>
        /// <param name="_port"></param>
        private void BindSocket(string _ip, int _port)
        {
            Socket.Bind("tcp://"+_ip+":"+_port);
        }

        /// <summary>
        /// Test le publisher
        /// </summary>
        public static void TestPublisher()
        {
            Publisher myPub = new Publisher();
            try
            {
                myPub.SendInfo(Publisher.GESTE, "DEBOUT");

                Thread.Sleep(10000);

                myPub.SendInfo(Publisher.POSITION, "2:43");

                Thread.Sleep(1000);
                myPub.SendInfo(Publisher.GESTE, "ASSIS");

                Thread.Sleep(2000);
                myPub.SendInfo(Publisher.GESTE, "ALLONGE");

            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
        }
    }
}
