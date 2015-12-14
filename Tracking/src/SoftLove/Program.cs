using SoftLove.Acquisition;
using SoftLove.Communication;
using SoftLove.Factory;
using SoftLove.GestureBehaviour;
using SoftLove.Zoning;
using SoftLove.GestureBehaviour.Gestures;
using System;
using System.Text.RegularExpressions;
using System.Threading;

namespace SoftLove
{
    class Program
    {

        public static void Main()
        {
            bool choice = false;
            /// Si vous voulez rajouter des options, n'oubliez pas de modifier la Regex du choix ! (pour l'instant seulement 1, 2, 3 ou 4
            Regex rgx = new Regex(@"^[1-4]$");
            String input = "";
            while (choice == false)
            {
                /// Choix du programme à lancer
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
                /// Création des zones. Gère aussi l'ajout de nouvelles zones sur un ancien fichier XML
                case "1":
                    Console.Write("Nom du fichier XML (création si non trouvé) : ");
                    ZoneRegister reg = new ZoneRegister(Console.ReadLine());
                    reg.Launch();
                    break;

                /// Lancement du zoning et/ou reconnaissance de gestes
                case "2":
                    Publisher pub = new Publisher();
                    /// La fonction "GenerateSensor" récupère la 1ere Kinect. Voir la classe "SensoreFactory" pour pouvoir choisir la kinect
                    /// avec un ID
                    SkeletonAcquisition acquire = new SkeletonAcquisition(SensorFactory.GenerateSensor());
                    acquire.GestureRecognizer = new GBTreatment(pub);
                    acquire.GestureRecognizer.AttachGestureRecognizer(new HandsTogetherGesture());
                    acquire.GestureRecognizer.AttachGestureRecognizer(new HelloGesture());
                    acquire.GestureRecognizer.AttachGestureRecognizer(new StandSitGesture());
                    acquire.Launch(); /// L'initialisation se fait automatiquement au lancement si elle n'est pas faite à la main
                    Console.ReadKey();
                    break;

                /// Exemple de Code pour plusieurs Kinects :
                /// 
                // SkeletonAcquisition acquire1 = new SkeletonAcquisition(SensorFactory.GenerateSensor("1"));
                // SkeletonAcquisition acquire2 = new SkeletonAcquisition(SensorFactory.GenerateSensor("2"));
                /// Attachement des localisateurs (pour les zones) et/ou des reconnaisseurs de gestes
                // acquire1.GestureRecognizer = new GBTreatment(pub);
                // acquire1.Localizer = new Localisation("zones1.xml","Z",pub);
                // acquire2.GestureRecognizer = new GBTreatment(pub); /// Possibilité de partager le même plublisher !
                // acquire2.Localizer = new Localisation("zones2.xml","Z",pub); /// Possibilité de partager le même plublisher !
                /// Et maintenant on peut attacher les gestes...
                // acquire1.GestureRecognizer.AttachGestureRecognizer(new HandsTogetherGesture());
                // acquire1.GestureRecognizer.AttachGestureRecognizer(new HelloGesture());
                // acquire1.GestureRecognizer.AttachGestureRecognizer(new HeadDownGesture());
                ///
                /// ...

                /// Tests du publisher
                case "3":
                    Publisher.TestPublisher();
                    break;

                /// Au revoir !
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
