package primitives;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the transparency and reflection coefficients of {@link Material}
 * and for the course-style setter aliases (setKD, setKS).
 */
class MaterialTests {
   /** Default constructor to satisfy JavaDoc generator */
   MaterialTests() { /* to satisfy JavaDoc generator */ }

   /** A new material is neither transparent nor reflective */
   @Test
   void testDefaults() {
      Material m = new Material();
      assertEquals(Double3.ZERO, m.kT, "Default kT must be ZERO");
      assertEquals(Double3.ZERO, m.kR, "Default kR must be ZERO");
      // the existing defaults must not change
      assertEquals(Double3.ONE, m.kA, "Default kA must stay ONE");
      assertEquals(Double3.ZERO, m.kD, "Default kD must stay ZERO");
      assertEquals(Double3.ZERO, m.kS, "Default kS must stay ZERO");
      assertEquals(0, m.nShininess, "Default shininess must stay 0");
   }

   /** The scalar setters apply the value to all three channels */
   @Test
   void testScalarSetters() {
      Material m = new Material();
      // TC01: kT
      m.setKT(0.3);
      assertEquals(0.3, m.kT.d1(), 1e-12, "Wrong kT red");
      assertEquals(0.3, m.kT.d2(), 1e-12, "Wrong kT green");
      assertEquals(0.3, m.kT.d3(), 1e-12, "Wrong kT blue");
      // TC02: kR
      m.setKR(0.7);
      assertEquals(0.7, m.kR.d1(), 1e-12, "Wrong kR red");
      assertEquals(0.7, m.kR.d2(), 1e-12, "Wrong kR green");
      assertEquals(0.7, m.kR.d3(), 1e-12, "Wrong kR blue");
      // TC03: an int literal (as in the course tests: setKR(1)) goes to the double overload
      m.setKR(1);
      assertEquals(Double3.ONE, m.kR, "setKR(1) must set all channels to 1");
      // TC04: the two coefficients are independent
      assertEquals(0.3, m.kT.d1(), 1e-12, "setKR must not change kT");
   }

   /** The Double3 setters keep each channel separate */
   @Test
   void testDouble3Setters() {
      Material m = new Material();
      // TC01: kT with different channels
      m.setKT(new Double3(0.5, 0, 0.25));
      assertEquals(0.5, m.kT.d1(), 1e-12, "Wrong kT red");
      assertEquals(0, m.kT.d2(), 1e-12, "Wrong kT green");
      assertEquals(0.25, m.kT.d3(), 1e-12, "Wrong kT blue");
      // TC02: kR with different channels
      m.setKR(new Double3(0.5, 0, 0.4));
      assertEquals(0.5, m.kR.d1(), 1e-12, "Wrong kR red");
      assertEquals(0, m.kR.d2(), 1e-12, "Wrong kR green");
      assertEquals(0.4, m.kR.d3(), 1e-12, "Wrong kR blue");
   }

   /** Every setter returns the same material, so calls can be chained */
   @Test
   void testSettersReturnSameInstance() {
      Material m = new Material();
      assertSame(m, m.setKT(0.1), "setKT(double)");
      assertSame(m, m.setKT(new Double3(0.1, 0.2, 0.3)), "setKT(Double3)");
      assertSame(m, m.setKR(0.1), "setKR(double)");
      assertSame(m, m.setKR(new Double3(0.1, 0.2, 0.3)), "setKR(Double3)");
      assertSame(m, m.setKD(0.1), "setKD(double)");
      assertSame(m, m.setKD(new Double3(0.1, 0.2, 0.3)), "setKD(Double3)");
      assertSame(m, m.setKS(0.1), "setKS(double)");
      assertSame(m, m.setKS(new Double3(0.1, 0.2, 0.3)), "setKS(Double3)");
   }

   /** setKD and setKS behave exactly like the existing setkD and setkS */
   @Test
   void testAliasesMatchExistingSetters() {
      Double3 rgb = new Double3(0.1, 0.2, 0.3);
      // TC01: kD
      assertEquals(new Material().setkD(0.4).kD, new Material().setKD(0.4).kD, "setKD(double)");
      assertEquals(rgb, new Material().setKD(rgb).kD, "setKD(Double3)");
      // TC02: kS
      assertEquals(new Material().setkS(0.4).kS, new Material().setKS(0.4).kS, "setKS(double)");
      assertEquals(rgb, new Material().setKS(rgb).kS, "setKS(Double3)");
      // TC03: the aliases write to the same fields, so the two spellings can be mixed in one chain
      Material m = new Material().setKD(0.4).setkS(0.3).setShininess(100).setKT(0.3);
      assertEquals(new Double3(0.4), m.kD, "Mixed chain: kD");
      assertEquals(new Double3(0.3), m.kS, "Mixed chain: kS");
      assertEquals(100, m.nShininess, "Mixed chain: shininess");
      assertEquals(new Double3(0.3), m.kT, "Mixed chain: kT");
      // TC04: the aliases do not touch the other coefficients
      Material n = new Material().setKD(0.4).setKS(0.6);
      assertEquals(Double3.ONE, n.kA, "Aliases must not change kA");
      assertEquals(Double3.ZERO, n.kT, "Aliases must not change kT");
      assertEquals(Double3.ZERO, n.kR, "Aliases must not change kR");
   }
}
