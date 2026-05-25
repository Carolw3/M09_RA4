import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Scanner;

public class Client {

    private static final String DIR_ARRIBADA = "/tmp";

    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private Socket socket;

    public void connectar() throws IOException {
        System.out.println("Connectant a -> " + Servidor.HOST + ":" + Servidor.PORT);
        socket = new Socket(Servidor.HOST, Servidor.PORT);
        System.out.println("Connexio acceptada: " + socket.getLocalAddress());

        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine().trim();

            sortida.writeObject(nomFitxer);
            sortida.flush();

            if (nomFitxer.equals("sortir")) {
                System.out.println("Sortint...");
                break;
            }

            byte[] contingut = (byte[]) entrada.readObject();

            if (contingut == null) {
                System.out.println("El servidor no ha pogut enviar el fitxer: " + nomFitxer);
                continue;
            }

            String nomCurt = new File(nomFitxer).getName();
            String rutaDesti = DIR_ARRIBADA + File.separator + nomCurt;

            System.out.println("Nom del fitxer a guardar: " + rutaDesti);
            Files.write(Paths.get(rutaDesti), contingut);
            System.out.println("Fitxer rebut i guardat com: " + rutaDesti);
        }
    }

    public void tancarConnexio() throws IOException {
        socket.close();
        System.out.println("Connexio tancada.");
    }

    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.connectar();
            client.rebreFitxers();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                client.tancarConnexio();
            } catch (IOException e) {
                System.out.println("Error tancant connexió: " + e.getMessage());
            }
        }
    }
}