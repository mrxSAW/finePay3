package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ExportFileTest {

    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreIn() {
        System.setIn(originalIn);
    }

    private void fakeInput(int id, int mois, int annee) {
        String input = id + "\n" + mois + "\n" + annee + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void sommeCorrecteDesMontants() throws Exception {
        fakeInput(1, 2, 2026);

        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getDouble("montant")).thenReturn(100.0, 200.0);
        when(rs.getDouble("totalPaye")).thenReturn(50.0, 200.0);

        // the rest of columns (because your code reads them)
        when(rs.getInt("id")).thenReturn(10, 11);
        when(rs.getDate("date")).thenReturn(
                java.sql.Date.valueOf("2026-02-05"),
                java.sql.Date.valueOf("2026-02-10")
        );
        when(rs.getString("clientNom")).thenReturn("Ali", "Sara");
        when(rs.getString("status")).thenReturn("PAID", "UNPAID");

        try (MockedStatic<DBConnection> mocked = mockStatic(DBConnection.class)) {
            mocked.when(DBConnection::createConnection).thenReturn(conn);

            double total = exportFile.exporterFacturesPrestataire();

            assertEquals(300.0, total, 0.0001);
        }
    }

    @Test
    void casListeVide() throws Exception {
        fakeInput(1, 2, 2026);

        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        try (MockedStatic<DBConnection> mocked = mockStatic(DBConnection.class)) {
            mocked.when(DBConnection::createConnection).thenReturn(conn);

            double total = exportFile.exporterFacturesPrestataire();

            assertEquals(0.0, total, 0.0001);
        }
    }

    @Test
    void plusieursFactures() throws Exception {
        fakeInput(1, 2, 2026);

        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, true, true, false);
        when(rs.getDouble("montant")).thenReturn(10.0, 20.5, 300.0, 5.5);
        when(rs.getDouble("totalPaye")).thenReturn(0.0, 10.0, 100.0, 5.5);

        when(rs.getInt("id")).thenReturn(1, 2, 3, 4);
        when(rs.getDate("date")).thenReturn(
                java.sql.Date.valueOf("2026-02-01"),
                java.sql.Date.valueOf("2026-02-02"),
                java.sql.Date.valueOf("2026-02-03"),
                java.sql.Date.valueOf("2026-02-04")
        );
        when(rs.getString("clientNom")).thenReturn("A", "B", "C", "D");
        when(rs.getString("status")).thenReturn("UNPAID", "PARTIAL", "PAID", "PAID");

        try (MockedStatic<DBConnection> mocked = mockStatic(DBConnection.class)) {
            mocked.when(DBConnection::createConnection).thenReturn(conn);

            double total = exportFile.exporterFacturesPrestataire();

            assertEquals(336.0, total, 0.0001);
        }
    }
}