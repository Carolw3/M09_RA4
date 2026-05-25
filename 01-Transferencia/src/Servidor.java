import java.io.*;
import java.net.*;

public class Servidor {

    static final int PORT = 9999;
    static final String HOST = "localhost";

    public Socket connectar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexio acceptada: /" + socket.getInetAddress().getHostAddress());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        System.out.println("Tancant connexió amb el client: /" + socket.getInetAddress().getHostAddress());
        socket.close();
    }

    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

        String nomFitxer = null;

        while (true) {
            System.out.println("Esperant el nom del fitxer del client...");
            nomFitxer = (String) entrada.readObject();

            if (nomFitxer == null || nomFitxer.equals("sortir")) {
                System.out.println("Error llegint el fitxer del client: " + nomFitxer + ". Sortint...");
                break;
            }

            System.out.println("Nomfitxer rebut: " + nomFitxer);

            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut;
            try {
                contingut = fitxer.getContingut();
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
            } catch (FileNotFoundException e) {
                System.out.println("Fitxer no trobat: " + nomFitxer);
                sortida.writeObject(null);
                sortida.flush();
                continue;
            }

            sortida.writeObject(contingut);
            sortida.flush();
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = null;

        try {
            socket = servidor.connectar();
            servidor.enviarFitxers(socket);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    servidor.tancarConnexio(socket);
                } catch (IOException e) {
                    System.out.println("Error tancant connexió: " + e.getMessage());
                }
            }
        }
    }
}