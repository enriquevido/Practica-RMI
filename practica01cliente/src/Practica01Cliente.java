import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Interfaces.IPersonaController;

public class Practica01Cliente {
    public static void main(String[] args) throws Exception {
        try {
            IPersonaController personaController =
                (IPersonaController) Naming.lookup("rmi://localhost/PersonaController");

            SwingUtilities.invokeLater(() -> {
                PersonaCrudFrame.useSystemLookAndFeel();
                PersonaCrudFrame frame = new PersonaCrudFrame(personaController);
                frame.setVisible(true);
            });
        } catch (NotBoundException ex) {
            Logger.getLogger(Practica01Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Practica01Cliente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                null,
                "No fue posible conectar con el servidor RMI.\nAsegurate de ejecutar primero Practica01Servidor.",
                "Conexion fallida",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            Logger.getLogger(Practica01Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
