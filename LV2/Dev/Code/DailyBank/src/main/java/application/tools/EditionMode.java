package application.tools;

/**
*
*
*Différents modes d'édition sont disponibles pour les données 
*(client, employé, compte, etc.). Trois modes sont définis :
*
*CRÉATION : L'employé crée une nouvelle donnée. La fenêtre est initialement
*vide et l'employé peut saisir les informations avant de les valider ou d'annuler.
*
*MODIFICATION : L'employé modifie une donnée existante. La fenêtre affiche 
*initialement les informations à modifier. L'employé peut ensuite saisir les modifications
*et les valider ou les annuler.
*
*SUPPRESSION : L'employé supprime une donnée.
*La fenêtre affiche initialement la donnée à supprimer et aucune saisie n'est possible.
*L'employé peut ensuite confirmer ou annuler la suppression.
*/
public enum EditionMode {
	CREATION, MODIFICATION, SUPPRESSION
}