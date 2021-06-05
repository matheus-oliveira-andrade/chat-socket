import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {

	private final int PORT = 8754;
	private ServerSocket serverSocket;
	private final List<ClientSocket> clients = new LinkedList<>();

	public void start() throws IOException {
		System.out.println("Servidor iniciado na porta " + PORT);
		serverSocket = new ServerSocket(PORT);
		clientConnectionLoop();
	}

	public void clientConnectionLoop() throws IOException {
		while (true) {
			ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
			clients.add(clientSocket);
			new Thread(() -> clientMessageLoop(clientSocket)).start();
		}
	}

	private void clientMessageLoop(ClientSocket clientSocket) {
		String message;
		try {
			while ((message = clientSocket.getMessage()) != null) {
				if ("sair".equalsIgnoreCase(message))
					return;

				System.out.println("Mensagem recebida do cliente" + clientSocket.getRemoteAddress() + ": " + message);

				sendMessageForAll(clientSocket, message);
			}
		} finally {
			clientSocket.close();
		}
	}

	private void sendMessageForAll(ClientSocket sender, String msg) {
		Iterator<ClientSocket> iterator = clients.iterator();

		while (iterator.hasNext()) {
			ClientSocket clientSocket = iterator.next();
			if (!sender.equals(clientSocket)) {
				boolean sent = clientSocket.sendMessage(msg);
				if (!sent) {
					iterator.remove();
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			ChatServer server = new ChatServer();
			server.start();
		} catch (IOException e) {
			System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
		}

		System.out.println("Servidor finalizado");
	}
}
