package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class exportFileTest {

        @Test
        void testFactureFileName() {
            String result = exportFile.generateFactureFileName(123);
            assertEquals("facture_123.pdf", result);
        }

        @Test
        void testRecuFileName() {
            String result = exportFile.generateRecuFileName(456);
            assertEquals("recu_456.pdf", result);
        }

        @Test
        void testRapportFileName() {
            String result = exportFile.generateRapportFileName(1, 2026);
            assertEquals("rapport012026.xls", result);
        }
}