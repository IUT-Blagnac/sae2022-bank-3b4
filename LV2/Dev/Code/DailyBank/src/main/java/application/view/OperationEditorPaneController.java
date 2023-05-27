package application.view;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import oracle.net.aso.c;

public class OperationEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	private int idCompteDestinataire;



	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * fenetre de dialogue qui varie en fonction du mode de l'operation (CREDIT, DEBIT, VIREMENT)
	 * @param cpte
	 * @param mode
	 * @return Operation operationResultat le resultat de l'operation
	 * @author Loïs Pacqueteau
	 * 
	 * */
		public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;
		

		this.primaryStage.setWidth(600);
		this.primaryStage.setHeight(350);
		switch (mode) {
		case DEBIT:
			
			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			this.lblCompteDestinataire.setVisible(false);
			this.txtCompteDestinataire.setVisible(false);
			this.btnOk.setText("Effectuer Débit");
			this.btnCancel.setText("Annuler débit");
			ObservableList<String> listTypesOpesPossibles = FXCollections.observableArrayList();
			listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_DEBIT_GUICHET);

			this.cbTypeOpe.setItems(listTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case CREDIT:
			
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer Crédit");
			this.btnCancel.setText("Annuler Crédit");
			this.lblCompteDestinataire.setVisible(false);
			this.txtCompteDestinataire.setVisible(false);
			listTypesOpesPossibles = FXCollections.observableArrayList();
			listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_CREDIT_GUICHET);

			this.cbTypeOpe.setItems(listTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		case VIREMENT:
			
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			this.lblCompteDestinataire.setText("Compte destinataire");
			this.txtCompteDestinataire.setVisible(true);
			this.btnOk.setText("Effectuer Virement");
			this.btnCancel.setText("Annuler Virement");
			listTypesOpesPossibles = FXCollections.observableArrayList();
			listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_VIREMENT_GUICHET);
			
			
			this.cbTypeOpe.setItems(listTypesOpesPossibles);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
		// break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;
	@FXML
	private Label lblCompteDestinataire;
	@FXML
	private TextField txtCompteDestinataire;
	
	/**
	 * Action sur le bouton annuler
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}
	
	/**
	 * Action sur le bouton ok cette methode sert a gerer les exceptions et a verifier les regles de gestion,
	 * si tout est bon elle enregistre l'operation dans la base de données
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * 
	 */
	@FXML
	private void doAjouter() throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		switch (this.categorieOperation) {
		case DEBIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			montant = -1;
			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
					
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (montant <= 0) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			String typeOp = this.cbTypeOpe.getValue();
			System.out.println(typeOp);
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
		case CREDIT:
			double montant2;
			String typeOp2 = this.cbTypeOpe.getValue();
			try {
				montant2 = Double.parseDouble(this.txtMontant.getText().trim());
				this.operationResultat = new Operation(-1, montant2, null, null, this.compteEdite.idNumCli, typeOp2);
				if (montant2 <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			
			
			this.primaryStage.close();
			break;
		case VIREMENT:
			double montant3;
			String typeOp3 = this.cbTypeOpe.getValue();
			try {
				montant3 = Double.parseDouble(this.txtMontant.getText().trim());
				this.idCompteDestinataire =Integer.parseInt(this.txtCompteDestinataire.getText().trim()) ;
				this.operationResultat = new Operation(-1, montant3, null, null,idCompteDestinataire, typeOp3);
				if (montant3 <= 0){
					this.txtMontant.getStyleClass().add("borderred");
					this.lblMontant.getStyleClass().add("borderred");
					this.txtMontant.requestFocus();
					return;
				}
				Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
				ArrayList<CompteCourant> listCptes = new ArrayList<>();
				listCptes = acc.getCompteCourants(this.compteEdite.idNumCli);
				boolean trouve = false;
				for (CompteCourant compteCourant : listCptes) {
					if(compteCourant.idNumCompte==idCompteDestinataire){
						trouve = true;
						break;
					}
				}

				
				if(!trouve){
					this.txtCompteDestinataire.getStyleClass().add("borderred");
					this.lblCompteDestinataire.getStyleClass().add("borderred");
					this.txtCompteDestinataire.requestFocus();
					return;
				}
				
				if(idCompteDestinataire==this.compteEdite.idNumCompte){
					this.txtCompteDestinataire.getStyleClass().add("borderred");
					this.lblCompteDestinataire.getStyleClass().add("borderred");
					this.txtCompteDestinataire.requestFocus();
					return;
				}

				if(acc.getCompteCourant(idCompteDestinataire).estCloture.equals("O")){
                    this.txtCompteDestinataire.getStyleClass().add("borderred");
					this.lblCompteDestinataire.getStyleClass().add("borderred");
					this.txtCompteDestinataire.requestFocus();
					return;
                }
					
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			this.primaryStage.close();
			break;
		}

	}
}
