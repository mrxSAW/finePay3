package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatistiqueServiceTest {

    @Test
    void ajouterPaiement_montantNormal() throws Exception {

        double montant = 100.0;
        int idFacture = 1;

        // Mocks
        Connection conn = mock(Connection.class);
        PreparedStatement psCheck = mock(PreparedStatement.class);
        PreparedStatement psTotal = mock(PreparedStatement.class);
        PreparedStatement stmtPaiement = mock(PreparedStatement.class);
        PreparedStatement stmtCommission = mock(PreparedStatement.class);
        PreparedStatement stmtUpdate = mock(PreparedStatement.class);

        ResultSet rsCheck = mock(ResultSet.class);
        ResultSet rsTotal = mock(ResultSet.class);
        ResultSet rsKeys = mock(ResultSet.class);

        try (MockedStatic<DBConnection> mocked = mockStatic(DBConnection.class)) {

            mocked.when(DBConnection::createConnection).thenReturn(conn);

            // Routing prepareStatement
            when(conn.prepareStatement(startsWith("SELECT montant"))).thenReturn(psCheck);
            when(conn.prepareStatement(startsWith("SELECT COALESCE"))).thenReturn(psTotal);
            when(conn.prepareStatement(startsWith("INSERT INTO paiement"), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(stmtPaiement);
            when(conn.prepareStatement(startsWith("INSERT INTO commission")))
                    .thenReturn(stmtCommission);
            when(conn.prepareStatement(startsWith("UPDATE facture")))
                    .thenReturn(stmtUpdate);

            // Facture exists
            when(psCheck.executeQuery()).thenReturn(rsCheck);
            when(rsCheck.next()).thenReturn(true);
            when(rsCheck.getDouble("montant")).thenReturn(1000.0);
            when(rsCheck.getString("status")).thenReturn("UNPAID");

            // Total payé
            when(psTotal.executeQuery()).thenReturn(rsTotal);
            when(rsTotal.next()).thenReturn(true);
            when(rsTotal.getDouble("total")).thenReturn(0.0);

            // Generated payment id
            when(stmtPaiement.executeUpdate()).thenReturn(1);
            when(stmtPaiement.getGeneratedKeys()).thenReturn(rsKeys);
            when(rsKeys.next()).thenReturn(true);
            when(rsKeys.getInt(1)).thenReturn(10);

            // Run
            StatistiqueService service = new StatistiqueService();
            double commission = service.ajouterPaiement(montant, idFacture);

            // Expected 2%
            assertEquals(2.0, commission, 1e-9);
        }
    }

    @Test
    void ajouterPaiement_montantZero() throws Exception {

        double montant = 0.0;
        int idFacture = 1;

        Connection conn = mock(Connection.class);
        PreparedStatement psCheck = mock(PreparedStatement.class);
        PreparedStatement psTotal = mock(PreparedStatement.class);
        ResultSet rsCheck = mock(ResultSet.class);
        ResultSet rsTotal = mock(ResultSet.class);

        try (MockedStatic<DBConnection> mocked = mockStatic(DBConnection.class)) {

            mocked.when(DBConnection::createConnection).thenReturn(conn);

            when(conn.prepareStatement(startsWith("SELECT montant"))).thenReturn(psCheck);
            when(conn.prepareStatement(startsWith("SELECT COALESCE"))).thenReturn(psTotal);

            when(psCheck.executeQuery()).thenReturn(rsCheck);
            when(rsCheck.next()).thenReturn(true);
            when(rsCheck.getDouble("montant")).thenReturn(1000.0);
            when(rsCheck.getString("status")).thenReturn("UNPAID");

            when(psTotal.executeQuery()).thenReturn(rsTotal);
            when(rsTotal.next()).thenReturn(true);
            when(rsTotal.getDouble("total")).thenReturn(0.0);

            StatistiqueService service = new StatistiqueService();
            double commission = service.ajouterPaiement(montant, idFacture);

            assertEquals(0.0, commission, 1e-9);
        }
    }

    @Test
    void ajouterPaiement_montantEleve() throws Exception {

        double montant = 1_000_000.0;
        int idFacture = 1;

        Connection conn = mock(Connection.class);
        PreparedStatement psCheck = mock(PreparedStatement.class);
        PreparedStatement psTotal = mock(PreparedStatement.class);
        PreparedStatement stmtPaiement = mock(PreparedStatement.class);
        PreparedStatement stmtCommission = mock(PreparedStatement.class);
        PreparedStatement stmtUpdate = mock(PreparedStatement.class);

        ResultSet rsCheck = mock(ResultSet.class);
        ResultSet rsTotal = mock(ResultSet.class);
        ResultSet rsKeys = mock(ResultSet.class);

        try (MockedStatic<DBConnection> mocked = mockStatic(DBConnection.class)) {

            mocked.when(DBConnection::createConnection).thenReturn(conn);

            when(conn.prepareStatement(startsWith("SELECT montant"))).thenReturn(psCheck);
            when(conn.prepareStatement(startsWith("SELECT COALESCE"))).thenReturn(psTotal);
            when(conn.prepareStatement(startsWith("INSERT INTO paiement"), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(stmtPaiement);
            when(conn.prepareStatement(startsWith("INSERT INTO commission")))
                    .thenReturn(stmtCommission);
            when(conn.prepareStatement(startsWith("UPDATE facture")))
                    .thenReturn(stmtUpdate);

            when(psCheck.executeQuery()).thenReturn(rsCheck);
            when(rsCheck.next()).thenReturn(true);
            when(rsCheck.getDouble("montant")).thenReturn(2_000_000.0);
            when(rsCheck.getString("status")).thenReturn("UNPAID");

            when(psTotal.executeQuery()).thenReturn(rsTotal);
            when(rsTotal.next()).thenReturn(true);
            when(rsTotal.getDouble("total")).thenReturn(0.0);

            when(stmtPaiement.executeUpdate()).thenReturn(1);
            when(stmtPaiement.getGeneratedKeys()).thenReturn(rsKeys);
            when(rsKeys.next()).thenReturn(true);
            when(rsKeys.getInt(1)).thenReturn(99);

            StatistiqueService service = new StatistiqueService();
            double commission = service.ajouterPaiement(montant, idFacture);

            assertEquals(20_000.0, commission, 1e-9);
        }
    }
}




