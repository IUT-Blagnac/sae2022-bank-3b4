package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class Access_BD_Emprunt {

    public Access_BD_Emprunt() {
    }

    public void insertEmprunt(double taux, double capital, int durree,
            CompteCourant compte)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        try {
            Connection con = LogToDatabase.getConnexion();

            String query = "INSERT INTO emprunt VALUES (" + "seq_id_emprunt.NEXTVAL" + ", " + "?" + ", " + "?"
                    + ", "
                    + "?" + ", " + "TO_DATE('2023-05-22', 'YYYY-MM-DD')" + ", " + "?" + ")";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setDouble(1, taux);
            pst.setDouble(2, capital);
            pst.setInt(3, durree);
            pst.setInt(4, compte.idNumCli);

            System.err.println(query);

            int result = pst.executeUpdate();
            pst.close();

            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.CompteCourant, Order.INSERT,
                        "Insert anormal (insert de moins ou plus d'une ligne)", null, result);
            }

            query = "SELECT seq_id_emprunt.CURRVAL from DUAL";

            System.err.println(query);
            PreparedStatement pst2 = con.prepareStatement(query);

            ResultSet rs = pst2.executeQuery();
            rs.next();

            con.commit();
            rs.close();
            pst2.close();

        } catch (SQLException e) {
            throw new DataAccessException(Table.CompteCourant, Order.INSERT, "Erreur accès", e);
        }

    }

    public ArrayList<String> getemprunt(int idNumCli)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<String> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();

			PreparedStatement pst;

			String query;
			
				query = "SELECT * FROM emprunt where idAg = ?";
				query += " ORDER BY idnumcli";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCli);

			
			System.err.println("#");

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				double idEmprunt = rs.getDouble("idemprunt");
				double taux = rs.getDouble("taux");
				double capital = rs.getDouble("capitalemp");
				int durreemp = rs.getInt("durreeemp");
                int idNumCli1 = rs.getInt("idnumcli");

				alResult.add(""+idEmprunt+" | "+ taux + " | " +capital+" | " + durreemp + " | " + idNumCli1);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}
}
