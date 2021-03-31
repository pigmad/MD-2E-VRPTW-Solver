# MD-2E-VRPTW-Solver
Projet de fin d'études - PRD

# Description du problème 

On étudie une extension du problème 2E-VRP définit comme suit :

On se place dans un graphe G = (S,A) pondéré où S est l’ensemble des sommets correspondant à l’union des dépôts, des satellites et 
des clients et A est l’union des arrêtes reliant les
dépôts aux satellites et des arcs reliant les satellites aux clients, la pondération des arrêtes
correspond à la distance entre chaque sommet.
- Un unique dépôt D0 et un coût de manutention cd correspondant au chargement d’un
véhicule (dans le cas où les véhicules sont déjà chargés en début de journée on a cd = 0)
- Un ensemble de S satellites et un coût de manutention cs correspondant au chargement/déchargement d’un véhicule
- Un ensemble de C clients, chaque client à une demande de livraison dc et un coût de
manutention cc correspondant au déchargement du véhicule
- Une flotte F1 de véhicules homogènes, partant depuis un dépôt retournant au même
dépôt après avoir visité au moins un satellite. Tous les véhicules sont dotés d’une capacité
maximale k1 et d’un coût d’utilisation q1.
- Une flotte F2 de véhicules homogènes, partant depuis un satellite et retournant au même
satellite après avoir visité au moins un client. Tous les véhicules sont dotés d’une capacité
maximale k2 et d’un coût d’utilisation q2.
- On considère que les véhicules circulent à la même vitesse sur les arrêtes ce qui implique
que la matrice des distances et la matrice des temps de trajet sont égales.
- La fonction objectif consiste à minimiser le coût total du système correspondant à la somme
des coûts de déplacement des flottes de véhicules (la distance entre deux sommets), des
coûts de manutention des cargaisons et des coûts d’utilisation des véhicules.

Dans l'extension de notre problème on considère possible la mutualisation des flottes de véhicules entre les sites et on ajoute les modifications suivantes :

- Le problème devient multi dépôts, on considère un ensemble de dépôts D.
- On ajoute une contrainte dure de livraison aux clients définie en ajoutant à chaque client c
une fenêtre de livraison [e,l] où e est la date de livraison au plus tôt et l la date de livraison
au plus tard, un véhicule livrant le client c doit impérativement arriver dans cet intervalle
de temps.
- On lève la contrainte qui empêche un véhicule de transiter entre deux dépôts ou entre
deux satellites. Un véhicule peut passer récupérer les cargaisons des clients dans plusieurs
satellites ou dépôts avant d’effectuer sa livraison. On mutualise ainsi les flottes de véhicules.
- On autorise la livraison de la commande d’un client vers le satellite à être répartie dans
plusieurs véhicules, donc uniquement pour le premier niveau. La livraison du satellite au
client doit être faite en utilisant un unique véhicule.

# Méthodes de résolution

## Génération d'une solution initiale 

Basée sur l'heuristique de Clarke & Wright. Modifiée pour ajouter la possibilité de fusion au milieu d'une tournée.
On résout d'abord le second niveau en introduisant une nouvelle possibilité de fusion dans le cas où les clients i et j sont affectées à des satellites différents. Dans le cas où i et j sont affectés au même satellite on conserve la fusion classique.

![FusionsGithub](https://user-images.githubusercontent.com/34888994/112842765-2ed8e480-90a2-11eb-8786-55f661ea2d60.png)

La solution obtenue est repércutée sur l'ordre supérieur donnant la demande de chaque satellite. On résout le premier niveau en utilisant la même méthode.

# Guide d'installation

## Installation sur machine pour utilisation

Deux options sont possible pour installer et utiliser le projet.
La première option consiste à télécharger le projet pré-compilé en jar exécutable et disponible
sur l’espace github du projet Release du projet.
Ce jar contient toutes les sources du projet et peut être exécuté sur tous les environnements via
la commande : java -jar Solver-x.x.jar
La seconde option est de télécharger les sources complètes du projet sur l’espace github du projet
Espace Github du projet et de compiler les fichiers sources du projet en utilisant la commande
maven : mvn clean install. Cette commande compile le projet, lance les tests et crée un exécutable
jar dans le dossier target.
De la même manière on peut exécuter le projet via la commande : java -jar Solver-x.x.jar.

## Installation sur machine pour développement

Dans le cas d’une reprise du projet pour développement, le projet git peut être fork vers un autre
compte utilisateur. Une fois le projet fork vers le nouveau compte, le projet peut être importé
via la commande : git clone et ajouté comme projet dans un IDE.

# Guide d'utilisation

Comme précisé dans le Document d’installation, le projet se lance via la commande : java -jar
Solver-x.x.jar.

A l’exécution un explorateur de fichier s’ouvre pour permettre à l’utilisateur de choisir les fichiers
d’instances à traiter.

![Util1](https://user-images.githubusercontent.com/34888994/113135192-1f80a500-9222-11eb-83e9-be3c7dddc82a.png)

L’utilisateur a la possibilité de choisir un seul ou plusieurs fichiers à traiter.

![Util2](https://user-images.githubusercontent.com/34888994/113135289-3e7f3700-9222-11eb-9296-b092ef8f8265.png)

Pour chaque fichier sélectionné, l’instance est lue et résolue. La console affiche des informations pendant l’exécution (instances lue, solution trouvée, temps de résolution...).

![Util3](https://user-images.githubusercontent.com/34888994/113135310-450dae80-9222-11eb-828a-44bb564d0240.png)

A la fin de l’exécution dans le répertoire contenant les fichiers d’instances sélectionnées, pour chacun des fichiers se trouvera un fichier de solution contenant la solution, la valeur de la fonction objectif et la faisabilité de la solution. 
On retrouve également un fichier récapitulant l’exécution et donnant l’ensemble des solutions, pour chaque instance résolue note le nom de l’instance, le
temps de résolution, la fonction objectif trouvée, la faisabilité de la solution.

![Util4](https://user-images.githubusercontent.com/34888994/113135330-4b9c2600-9222-11eb-9969-e97a5f356d2b.png)
