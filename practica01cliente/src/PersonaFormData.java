public class PersonaFormData {
    private final String idText;
    private final String nombreText;
    private final String emailText;
    private final String telefonoText;

    public PersonaFormData(String idText, String nombreText, String emailText, String telefonoText) {
        this.idText = idText;
        this.nombreText = nombreText;
        this.emailText = emailText;
        this.telefonoText = telefonoText;
    }

    public int parseId(boolean required) {
        String normalizedId = normalize(idText);
        if (normalizedId == null) {
            if (required) {
                throw new IllegalArgumentException("El id es obligatorio.");
            }
            return 0;
        }

        try {
            int id = Integer.parseInt(normalizedId);
            if (id <= 0) {
                throw new IllegalArgumentException("El id debe ser un entero positivo.");
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El id debe ser un entero positivo.", ex);
        }
    }

    public String normalizedNombre() {
        return normalize(nombreText);
    }

    public String normalizedEmail() {
        return normalize(emailText);
    }

    public String normalizedTelefono() {
        return normalize(telefonoText);
    }

    public void validateForCreateOrUpdate() {
        parseId(true);
        if (normalizedNombre() == null) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
