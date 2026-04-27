import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Interfaces.IPersona;
import Interfaces.IPersonaController;

public class PersonaCrudFrame extends JFrame {
    private static final Color INK = new Color(21, 24, 34);
    private static final Color PAPER = new Color(247, 242, 233);
    private static final Color ACCENT = new Color(188, 73, 73);
    private static final Color ACCENT_ALT = new Color(41, 99, 160);
    private static final Color PANEL = new Color(255, 252, 246);

    private final IPersonaController personaController;
    private final JTextField idField = new JTextField();
    private final JTextField nombreField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField telefonoField = new JTextField();
    private final JLabel statusLabel = new JLabel("Conectado al servidor RMI.");
    private final JTextArea activityArea = new JTextArea();
    private final DefaultTableModel tableModel = new DefaultTableModel(
        new Object[] {"Id", "Nombre", "Email", "Telefono"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable personaTable = new JTable(tableModel);

    public PersonaCrudFrame(IPersonaController personaController) {
        this.personaController = personaController;

        setTitle("Demostracion RMI CRUD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);
        setContentPane(buildContent());

        activityArea.setEditable(false);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        activityArea.setBackground(INK);
        activityArea.setForeground(PAPER);
        activityArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        activityArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        personaTable.setRowHeight(28);
        personaTable.setFillsViewportHeight(true);
        personaTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        personaTable.getTableHeader().setBackground(INK);
        personaTable.getTableHeader().setForeground(PAPER);
        personaTable.setSelectionBackground(new Color(224, 211, 188));
        personaTable.setSelectionForeground(INK);
        personaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadSelectedRowIntoForm();
            }
        });

        loadPersonas();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(PAPER);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        return root;
    }

    private Component buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel eyebrow = new JLabel("CLIENTE VISUAL");
        eyebrow.setFont(new Font("Dialog", Font.BOLD, 12));
        eyebrow.setForeground(ACCENT_ALT);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Panel de demostracion RMI para Personas");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(INK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Crea, consulta, actualiza y elimina registros remotos desde una sola ventana."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(87, 84, 78));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(eyebrow);
        header.add(Box.createVerticalStrut(6));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private Component buildCenter() {
        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setOpaque(false);
        center.add(buildFormPanel(), BorderLayout.WEST);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    private Component buildFormPanel() {
        JPanel card = createCard(new Dimension(340, 0));
        card.setLayout(new BorderLayout(0, 16));

        JLabel title = new JLabel("Formulario");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(INK);
        card.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 12, 0);

        configureField(idField);
        configureField(nombreField);
        configureField(emailField);
        configureField(telefonoField);

        addLabeledField(form, gbc, "Id", idField);
        addLabeledField(form, gbc, "Nombre", nombreField);
        addLabeledField(form, gbc, "Email", emailField);
        addLabeledField(form, gbc, "Telefono", telefonoField);

        gbc.insets = new Insets(12, 0, 0, 0);
        form.add(buildButtonPanel(), gbc);

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private Component buildButtonPanel() {
        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);

        buttons.add(createButton("Crear", ACCENT, e -> createPersona()), gbc);
        gbc.gridy++;
        buttons.add(createButton("Buscar por id", ACCENT_ALT, e -> findPersona()), gbc);
        gbc.gridy++;
        buttons.add(createButton("Actualizar", INK, e -> updatePersona()), gbc);
        gbc.gridy++;
        buttons.add(createButton("Eliminar", new Color(112, 35, 35), e -> deletePersona()), gbc);
        gbc.gridy++;
        buttons.add(createButton("Listar todo", new Color(61, 104, 78), e -> loadPersonas()), gbc);
        gbc.gridy++;
        buttons.add(createSecondaryButton("Limpiar", e -> clearForm()), gbc);

        return buttons;
    }

    private Component buildTablePanel() {
        JPanel panel = createCard(null);
        panel.setLayout(new BorderLayout(0, 16));

        JLabel title = new JLabel("Registros remotos");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(INK);
        panel.add(title, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(personaTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(214, 205, 192)));
        panel.add(tableScroll, BorderLayout.CENTER);

        JScrollPane logScroll = new JScrollPane(activityArea);
        logScroll.setPreferredSize(new Dimension(0, 190));
        logScroll.setBorder(BorderFactory.createTitledBorder("Actividad"));
        panel.add(logScroll, BorderLayout.SOUTH);

        return panel;
    }

    private Component buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(INK);
        footer.add(statusLabel);
        return footer;
    }

    private JPanel createCard(Dimension preferredSize) {
        JPanel card = new JPanel();
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(214, 205, 192), 1),
            new EmptyBorder(18, 18, 18, 18)
        ));
        if (preferredSize != null) {
            card.setPreferredSize(preferredSize);
        }
        return card;
    }

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(INK);

        JPanel fieldBlock = new JPanel();
        fieldBlock.setLayout(new BoxLayout(fieldBlock, BoxLayout.Y_AXIS));
        fieldBlock.setOpaque(false);
        fieldBlock.add(label);
        fieldBlock.add(Box.createVerticalStrut(6));
        fieldBlock.add(field);

        panel.add(fieldBlock, gbc);
        gbc.gridy++;
    }

    private void configureField(JTextField field) {
        field.setPreferredSize(new Dimension(0, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBackground(Color.WHITE);
        field.setForeground(INK);
        field.setCaretColor(INK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(201, 192, 179), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private JButton createButton(String label, Color background, java.awt.event.ActionListener action) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(PAPER);
        button.setBackground(background);
        button.setBorder(new EmptyBorder(12, 14, 12, 14));
        button.addActionListener(action);
        return button;
    }

    private JButton createSecondaryButton(String label, java.awt.event.ActionListener action) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(INK);
        button.setBackground(new Color(232, 223, 210));
        button.setBorder(new EmptyBorder(12, 14, 12, 14));
        button.addActionListener(action);
        return button;
    }

    private void createPersona() {
        performRemoteAction("Crear", () -> {
            PersonaFormData data = readFormData();
            data.validateForCreateOrUpdate();
            IPersona persona = buildPersona(data, true);
            int result = personaController.add(persona);
            ensureSuccess(result == IPersonaController.ADD_EXITO, addMessage(result));
            appendLog("CREATE", persona.getString());
            return "Registro creado correctamente.";
        }, true);
    }

    private void findPersona() {
        performRemoteAction("Buscar", () -> {
            PersonaFormData data = readFormData();
            int id = data.parseId(true);
            IPersona persona = personaController.findOne(id);
            ensureSuccess(persona.getId() != 0, "No existe un registro con ese id.");
            fillForm(persona);
            setSinglePersonaTable(persona);
            appendLog("READ", persona.getString());
            return "Consulta remota completada.";
        }, false);
    }

    private void updatePersona() {
        performRemoteAction("Actualizar", () -> {
            PersonaFormData data = readFormData();
            data.validateForCreateOrUpdate();
            IPersona persona = buildPersona(data, true);
            int result = personaController.update(persona);
            ensureSuccess(result == IPersonaController.UPDATE_EXITO, updateMessage(result));
            appendLog("UPDATE", persona.getString());
            return "Registro actualizado correctamente.";
        }, true);
    }

    private void deletePersona() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Se eliminara el registro indicado. Deseas continuar?",
            "Confirmar eliminacion",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        performRemoteAction("Eliminar", () -> {
            PersonaFormData data = readFormData();
            int id = data.parseId(true);
            int result = personaController.delete(id);
            ensureSuccess(result == IPersonaController.DELETE_EXITO, deleteMessage(result));
            appendLog("DELETE", "Id " + id);
            clearForm();
            return "Registro eliminado correctamente.";
        }, true);
    }

    private void loadPersonas() {
        performRemoteAction("Listar", () -> {
            List<IPersona> personas = personaController.list();
            refreshTable(personas);
            appendLog("LIST", "Total de registros: " + personas.size());
            return "Listado remoto actualizado.";
        }, false);
    }

    private void performRemoteAction(String actionName, RemoteAction action, boolean refreshAfterSuccess) {
        statusLabel.setText(actionName + " en progreso...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return action.run();
            }

            @Override
            protected void done() {
                try {
                    statusLabel.setText(get());
                    if (refreshAfterSuccess) {
                        loadPersonas();
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setText("Error durante " + actionName.toLowerCase() + ".");
                    JOptionPane.showMessageDialog(
                        PersonaCrudFrame.this,
                        cause.getMessage(),
                        "Operacion remota fallida",
                        JOptionPane.ERROR_MESSAGE
                    );
                    appendLog("ERROR", cause.getMessage());
                }
            }
        };
        worker.execute();
    }

    private PersonaFormData readFormData() {
        return new PersonaFormData(
            idField.getText(),
            nombreField.getText(),
            emailField.getText(),
            telefonoField.getText()
        );
    }

    private IPersona buildPersona(PersonaFormData data, boolean requireId) throws RemoteException {
        IPersona persona = personaController.newInstance();
        int id = data.parseId(requireId);
        if (id > 0) {
            persona.setId(id);
        }
        persona.setNombre(data.normalizedNombre());
        persona.setEmail(data.normalizedEmail());
        persona.setTelefono(data.normalizedTelefono());
        return persona;
    }

    private void fillForm(IPersona persona) throws RemoteException {
        idField.setText(String.valueOf(persona.getId()));
        nombreField.setText(safe(persona.getNombre()));
        emailField.setText(safe(persona.getEmail()));
        telefonoField.setText(safe(persona.getTelefono()));
    }

    private void refreshTable(List<IPersona> personas) throws RemoteException {
        tableModel.setRowCount(0);
        for (IPersona persona : personas) {
            tableModel.addRow(new Object[] {
                persona.getId(),
                safe(persona.getNombre()),
                safe(persona.getEmail()),
                safe(persona.getTelefono())
            });
        }
    }

    private void setSinglePersonaTable(IPersona persona) throws RemoteException {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[] {
            persona.getId(),
            safe(persona.getNombre()),
            safe(persona.getEmail()),
            safe(persona.getTelefono())
        });
    }

    private void loadSelectedRowIntoForm() {
        int row = personaTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nombreField.setText(safe((String) tableModel.getValueAt(row, 1)));
        emailField.setText(safe((String) tableModel.getValueAt(row, 2)));
        telefonoField.setText(safe((String) tableModel.getValueAt(row, 3)));
        statusLabel.setText("Registro cargado en el formulario.");
    }

    private void clearForm() {
        idField.setText("");
        nombreField.setText("");
        emailField.setText("");
        telefonoField.setText("");
        personaTable.clearSelection();
        statusLabel.setText("Formulario limpio.");
    }

    private void ensureSuccess(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private String addMessage(int code) {
        return switch (code) {
            case IPersonaController.ADD_ID_DUPLICADO -> "Ya existe un registro con ese id.";
            case IPersonaController.ADD_SIN_EXITO -> "No fue posible crear el registro.";
            default -> "Respuesta inesperada del servidor: " + code;
        };
    }

    private String updateMessage(int code) {
        return switch (code) {
            case IPersonaController.UPDATE_INEXISTE -> "No existe un registro con ese id.";
            case IPersonaController.UPDATE_ID_NULO -> "El id es obligatorio.";
            case IPersonaController.UPDATE_SIN_EXITO -> "No fue posible actualizar el registro.";
            default -> "Respuesta inesperada del servidor: " + code;
        };
    }

    private String deleteMessage(int code) {
        return switch (code) {
            case IPersonaController.DELETE_ID_INEXISTE -> "No existe un registro con ese id.";
            case IPersonaController.DELETE_ID_NULO -> "El id es obligatorio.";
            case IPersonaController.DELETE_SIN_EXITO -> "No fue posible eliminar el registro.";
            default -> "Respuesta inesperada del servidor: " + code;
        };
    }

    private void appendLog(String action, String detail) {
        activityArea.append("[" + action + "] " + detail + "\n");
        activityArea.setCaretPosition(activityArea.getDocument().getLength());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    @FunctionalInterface
    private interface RemoteAction {
        String run() throws Exception;
    }
}
