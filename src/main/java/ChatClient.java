import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient implements Runnable {
	private final String SERVER_ADDRESS = "127.0.0.1";
	private final int PORT = 8754;
	private ClientSocket clientSocket;
	private Scanner scanner;

	public ChatClient() {
		scanner = new Scanner(System.in);
	}

	public void start() throws UnknownHostException, IOException {
		try {
			clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, PORT));

			System.out.println("Cliente conectado ao servidor em " + SERVER_ADDRESS + ":" + PORT);

			new Thread(this).start();

			messageLoop();
		} finally {
			clientSocket.close();
		}
	}

	@Override
	public void run() {
		String message;
		while ((message = clientSocket.getMessage()) != null) {
			System.out.println("Mensagem recebida do servidor: " + message);
		}
	}

	public void messageLoop() throws IOException {
		String msg;
		do {
			System.out.print("Digite uma mensagem (ou sair para finalizar): ");
			msg = scanner.nextLine();

			clientSocket.sendMessage(msg);

		} while (!msg.equalsIgnoreCase("sair"));
	}

	public static void main(String[] args) {
		try {
			ChatClient client = new ChatClient();
			client.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {

			System.out.println("Erro ao iniciar cliente " + e.getMessage());
		}

		System.out.println("Cliente finalizado");
	}
}
