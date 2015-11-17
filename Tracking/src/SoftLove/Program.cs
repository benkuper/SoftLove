using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Text.RegularExpressions;
using SoftLove.Zoning;
using SoftLove.Communication;

using ZeroMQ;

namespace SoftLove
{
    class Program
    {

        public static void Main()
        {
            bool choice = false;
            Regex rgx = new Regex(@"^[1-4]$");
            String input = "";
            while (choice == false)
            {
                Console.WriteLine("Que souhaitez-vous faire ?");
                Console.WriteLine("1. Enregister des zones");
                Console.WriteLine("2. Lancement de la reconnaissance de Position, Gestes et Attitudes");
                Console.WriteLine("3. Tester le Publisher");
                Console.WriteLine("4. Exit");
                Console.Write("Choix : ");
                input = Console.ReadLine();
                if (rgx.IsMatch(input)) { choice = true; };
            }
            switch (input)
            {
                case "1":
                    Console.Write("Nom du fichier XML (création si non trouvé) : ");
                    ZoneRegister reg = new ZoneRegister(Console.ReadLine());
                    reg.Launch();
                    break;

                case "2": //TODO
                    Console.WriteLine("Lancement du programme...TODO");
                    break;

                case "3":
                    Publisher.TestPublisher();
                    break;

                case "4":
                    Console.WriteLine("Au revoir...");
                    Thread.Sleep(2000);
                    break;
                default:
                    Console.WriteLine("Au revoir...");
                    Thread.Sleep(2000);
                    break;
            }
        }
    }
}
