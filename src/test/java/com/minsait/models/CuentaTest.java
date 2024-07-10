package com.minsait.models;

import com.minsait.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    Cuenta cuenta;
    TestInfo testInfo;
    TestReporter testReporter;

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo=testInfo;
        this.testReporter=testReporter;
        testReporter.publishEntry("Iniciando el método");
        this.cuenta=new Cuenta("Ricardo", new BigDecimal(1000));
        testReporter.publishEntry("Ejecutando: "+testInfo.getTestMethod().get().getName());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando todos los test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando todos los test");
    }

    @Nested
    class CuentaOperaciones{
        @Test
        void testRetirar(){
            cuenta.retirar(new BigDecimal("100"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
        }

        @Test
        void testDepositar(){
            cuenta.depositar(new BigDecimal("100"));

            assertNotNull(cuenta.getSaldo());
            assertEquals("1100", cuenta.getSaldo().toPlainString());
        }
        @Test
        void testTransferir(){
            Cuenta cuenta2=new Cuenta("Eduardo", new BigDecimal("100000"));
            Banco banco=new Banco();
            banco.setNombre("BBVA");
            banco.transferir(cuenta2, cuenta, new BigDecimal(5000));

            assertEquals("95000", cuenta2.getSaldo().toPlainString());
            assertEquals("6000", cuenta.getSaldo().toPlainString());
        }
    }

    @Nested
    class CuentaPrivados{
        @Test
        void testMetodoPrivado() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Banco banco=new Banco();
            Method metodoPrivado=Banco.class.getDeclaredMethod("metodo");
            metodoPrivado.setAccessible(true);
            String resultado=(String) metodoPrivado.invoke(banco);

            assertEquals("Hola", resultado);
        }

        @Test
        void testMetodoVoid() throws Exception{
            Banco banco=new Banco();
            Method metodoVoid=Banco.class.getDeclaredMethod("metodoVoid");
            metodoVoid.setAccessible(true);
            metodoVoid.invoke(banco);

            assertEquals("Hola Mundo", banco.getEstado());
        }
    }

    @Nested
    @DisplayName("Probando atributos de la cuenta")
    class CuentaNombreSaldo{
        @Test
        void testNombre(){
            var esperado="Ricardo";
            var real=cuenta.getPersona();

            assertNotNull(real);
            assertEquals(esperado, real);
            assertTrue(esperado.equals(real));
        }
        @Test
        void testSaldo(){
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO)<0);
            assertEquals(1000, cuenta.getSaldo().intValue());
            assertTrue("1000".equals(cuenta.getSaldo().toPlainString()));
        }
        @Test
        void testReferencia(){
            Cuenta cuenta2=new Cuenta("Ricardo", new BigDecimal("1000"));
            // Comparar si la cuenta es igual a la cuenta2
            assertEquals(cuenta, cuenta2);// 448ff1a8
        }
    }

    /**
     * Investigar el árbol de excepciones de Java
     */
    @Test
    @DisplayName("Probando excepción por saldo insuficiente")
    //@Disabled // JUnit4 @Ignore
    void testException(){
        var exception=assertThrows(DineroInsuficienteException.class, ()->
            cuenta.retirar(new BigDecimal(1001))
        );
        var actual=exception.getMessage();
        var esperado="Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testRelacionBancoCuentas(){
        Cuenta cuenta2=new Cuenta("Yessica", new BigDecimal(100_000));
        Banco banco=new Banco();
        banco.setNombre("BBVA");
        banco.addCuenta(cuenta);
        banco.addCuenta(cuenta2);
        banco.transferir(cuenta2, cuenta, new BigDecimal(5000));

        assertAll(
                ()-> assertEquals("95000", cuenta2.getSaldo().toPlainString()),
                ()->assertEquals("6000", cuenta.getSaldo().toPlainString()),
                ()->assertEquals(2, banco.getCuentas().size()),
                ()->assertEquals("BBVA", cuenta.getBanco().getNombre()),
                ()->assertEquals(cuenta.getPersona(), banco.getCuentas().get(0).getPersona()),

                ()->assertEquals(cuenta2.getPersona(),banco.getCuentas().stream()
                        .filter(c->c.getPersona().equals(cuenta2.getPersona()))
                        .findFirst().get().getPersona()
                ),
                ()->assertEquals(cuenta2.getPersona(),banco.getCuentas().stream()
                        .map(Cuenta::getPersona)
                        .filter(persona->persona.equals(cuenta2.getPersona()))
                        .findFirst().get()
                ),
                ()->assertTrue(banco.getCuentas().stream()
                        .anyMatch(c->c.getPersona().equals(cuenta2.getPersona()))
                )
        );
        /**
         * Verificar el nombre de la persona si es mismo que tiene la cuenta con la del banco, sin usar
         * indices
         * Esperado: Persona de la cuenta
         * Actual: A traves del objeto banco recuperar el nombre de la persona sin usar indices
         *
         * Ayuda:
         * -Es con Streams
         * -Puede ser con assertEquals o assertTrue
         */
    }
}