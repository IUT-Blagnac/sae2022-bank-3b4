package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	/**
	 * Constructeur de la classe ComptesManagement permettant de charger la vue de
	 * gestion des comptes
	 * 
	 * @param _parentStage Stage parent de la vue
	 * @param _dbstate     Etat actuel de l'application DailyBank
	 * @param client       Client dont on veut afficher les comptes
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));

			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();

			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");

			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();

			this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
		}
	}

	/**
	 * Methode permettant d'afficher la vue de gestion des comptes
	 */
	public void doComptesManagementDialog() {
		this.cmcViewController.displayDialog();
	}

	/**
	 * Methode permettant de gerer les operations d'un compte
	 */
	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	/**
	 * Crée un nouveau compte courant.
	 *
	 * @return Le compte courant créé, ou {@code null} si aucun compte n'a été créé.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de
	 *                                    données se produit.
	 * @throws ApplicationException       Si une erreur d'application se produit
	 *                                    lors de l'insertion du compte.
	 * @author Bradley DJEDJE
	 */
	public CompteCourant creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			try {
				// Temporaire jusqu'à implémentation
				// compte = null;
				Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
				acc.insertCompte(compte);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
		return compte;
	}

	/**
	 * Clôture un compte courant spécifié.
	 *
	 * @param c Le compte courant à clôturer.
	 * @return Le compte courant clôturé, ou {@code null} si la clôture n'a pas été
	 *         effectuée.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de
	 *                                    données se produit.
	 * @throws ApplicationException       Si une erreur d'application se produit
	 *                                    lors de la clôture du compte.
	 * @author Bradley DJEDJE
	 */
	public CompteCourant cloturerCompte(CompteCourant c) {
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		CompteCourant result = cep.doCompteEditorDialog(this.clientDesComptes, c, EditionMode.SUPPRESSION);
		if (result != null) {
			try {
				if (c.solde > 0) {
					AlertUtilities.showAlert(primaryStage, "Erreur cloturation", "Le solde trop grand",
							"Le solde doit etre égal a 0", AlertType.ERROR);
					return null;
				}
				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				result.setCloturer();
				ac.deleteCompte(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

	/**
	 * Methode permettant de creer un emprunt
	 * 
	 * @param compte Compte courant pour lequel on veut creer un emprunt
	 */
	public void creerEmprunt(CompteCourant compte) {
		EmpruntManagement cep = new EmpruntManagement(this.primaryStage, this.dailyBankState, compte);
	}

	/**
	 * Methode permettant de recuperer les comptes d'un client
	 * return ArrayList<CompteCourant> Liste des comptes du client
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}

	/**
	 * Methode permettant de gerer les prelevements d'un compte
	 * 
	 * @param cptCourant Compte courant pour lequel on veut gerer les prelevements
	 */
	public void gererPrelevements(CompteCourant cptCourant) {
		PrelevementsManagement pm = new PrelevementsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cptCourant);
		pm.doPrelevementsManagementDialog();
	}

}
