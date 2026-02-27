package org.example;

import com.mysql.cj.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FactureTest {

@Test
    void paymentTotalPaidTest() throws Exception{
    FactureService service = new FactureService();
    Facture facture = new Facture();
    service.creerFacture(facture);
}
@Test
    void cc(){

}


//@Test
//    void paymentPArtial(){
//    Facture facture = new Facture(2,LocalDate.now(),100, "PENDING", null,null);
//    facture.ajouterPayment(50);
//    System.out.println("Payment partial paid "+facture.getStatus());
//    assertEquals("PENDING",facture.getStatus());
//}
//@Test
//    void restPending(){
//    Facture facture = new Facture(3, LocalDate.now(),150,"PENDING", null,null);
//    System.out.println(" No payment yet "+facture.getStatus());
//    assertEquals("PENDING",facture.getStatus());
//}
}