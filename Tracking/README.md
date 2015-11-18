# Tracking
Détection de position, placement en zones, détection de gestes

## Informations de déploiement
Utilisation de Visual Studio 2015 : https://www.visualstudio.com/en-us/downloads/download-visual-studio-vs.aspx

### Gestion des dépendances : NuGet.
https://www.nuget.org/

Téléchargement automatique des dépendances lors de la génération de la solution.

### SDK Microsoft Kinect
Télécharger et Installer le SDK 1.8 : https://www.microsoft.com/en-us/download/details.aspx?id=40278


## Publisher

Le programme de tracking envoi plusieurs informations : la position (x,z), la zone actuelle (A,B,present,absent,...)
Pour y souscrire :
- Port 5555, topic : "zone" --> Reçu : "A","B",... ou "present","absent"
- Port 5555, topic : "position" --> Reçu : "X:Z"
- Port 5555, topic : "geste" --> TODO

## Architecture de la solution

|_ src

	|_ SoftLove.sln : Fichier Projet Visual Studio
	
	|_ SoftLove/
	
			|_ amd64/ : DLLs 64b
			
			|_ i386/ : DLLs x86
			
			|_ Communication/
			
					|_ Publisher.cs : Classe du Publisher, utilisé pour l'envoi des données
					
			|_ Exceptions/ : Classes d'Exceptions...
			
			|_ Zoning/
			
					|_ Enums.cs : Divers Enum
					
					|_ Localisation.cs : Classe utilisée pour le placement d'un individu dans une zone
					
					|_ Zone.cs : Définition d'une Zone
					
					|_ ZoneRegister : Classe utilisée pour la configuration de zones
			
			|_ GestureBehaviour/
					
					|_ GBTreatment.cs : TODO
			
			|_ Program.cs : Lancement du programme...
