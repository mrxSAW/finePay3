package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaiementdbTest {
    @Test
    @DisplayName("Test facture paid partial")
    void testStatuPartial(){
        double montantTotal = 1000;
        double montantPayee = 100;
        String status = Paiementdb.getFactureStatut(montantPayee,montantTotal);
        assertEquals("PENDING",status);
    }

    @Test
    @DisplayName("Test facture paid")
    void testStatuPayee(){
        double montantTotal = 1000;
        double montantPayee = 1000;
        String status = Paiementdb.getFactureStatut(montantPayee,montantTotal);
        assertEquals("PAID",status);
    }
    @Test
    @DisplayName("Test facture unpaid")
    void testStatuNonPayye(){
        double montantTotal = 1000;
        double montantPayee =0;
        String status = Paiementdb.getFactureStatut(montantPayee,montantTotal);
        assertEquals("PENDING",status);
    }
}