public class PersonaFormDataTest {
    public static void main(String[] args) {
        trimsValuesAndConvertsOptionalBlanksToNull();
        requiresPositiveIdForMutatingOperations();
    }

    private static void trimsValuesAndConvertsOptionalBlanksToNull() {
        PersonaFormData data = new PersonaFormData(" 7 ", " Ana ", "   ", " 5551234 ");

        assertEquals(7, data.parseId(true), "El id debe convertirse a entero.");
        assertEquals("Ana", data.normalizedNombre(), "El nombre debe recortarse.");
        assertEquals(null, data.normalizedEmail(), "El email vacío debe tratarse como nulo.");
        assertEquals("5551234", data.normalizedTelefono(), "El teléfono debe recortarse.");
    }

    private static void requiresPositiveIdForMutatingOperations() {
        expectFailure("El id es obligatorio.", () -> new PersonaFormData(" ", "Ana", null, null).parseId(true));
        expectFailure("El id debe ser un entero positivo.", () -> new PersonaFormData("0", "Ana", null, null).parseId(true));
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " Esperado: " + expected + ", actual: " + actual);
        }
    }

    private static void expectFailure(String expectedMessage, ThrowingRunnable action) {
        try {
            action.run();
            throw new AssertionError("Se esperaba un IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertEquals(expectedMessage, ex.getMessage(), "Mensaje de error inesperado.");
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
