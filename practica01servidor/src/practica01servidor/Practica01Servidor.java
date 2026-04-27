package practica01servidor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

import Interfaces.IPersonaController;

public class Practica01Servidor {
    public static void main(String[] args) throws Exception {
        try{
            LocateRegistry.createRegistry(1099);
            IPersonaController PersonaController = new PersonaController();
            Naming.rebind("rmi://localhost/PersonaController", PersonaController);
            System.out.println("Escuchando...");
        } catch (RemoteException ex) {
            Logger.getLogger(Practica01Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Practica01Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

