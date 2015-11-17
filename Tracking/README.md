# Tracking
D�tection de position, placement en zones, d�tection de gestes

## Informations de d�ploiement
Utilisation de Visual Studio 2015.

### Gestion des d�pendances : Nugets.
Installation des packages : "Install Packages"

### SDK Microsoft Kinect : T�l�charger et Installer le SDK 1.8

## Publisher

Le programme de tracking envoi plusieurs informations : la position (x,z), la zone actuelle (A,B,present,absent,...)
Pour y souscrire :
- Port 5555, topic : "geste" --> Re�u : "A","B",... ou "present","absent"
- Port 5555, topic : "position" --> Re�u : "X:Z"
- Port 5555, topic : "geste" --> TODO

## Architecture
|_ src
	|_ SoftLove.sln : Fichier Projet Visual Studio
	|_ SoftLove/
			|_ amd64 : DLLs 64b
			|_ i386 : DLLs x86
			|_ Communication
					|_ Publisher.cs : Classe du Publisher, utilis� pour l'envoi des donn�es
			|_ Exceptions : Classes d'Exceptions...
			|_ Zoning
					|_ Enums.cs : Divers Enums
					|_ Localisation.cs : Classe utilis�e pour le placement d'un individue dans une zones
					|_ Zone.cs : D�finition d'une Zone
					|_ ZoneRegister : Classe utilis�e pour la configuration de zones
			|_ GestureBehaviour
					|_ GBTreatment.cs : TODO
			