package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 1).
 *
 * @author Olivier Liechti
 * @author Thibaud Besseau
 * @author Nathalie Megevand
 */
public class RouletteV2ClientHandler implements IClientHandler {

	final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());

	private final IStudentsStore store;
	private int numberOfCommands = 0;


	public RouletteV2ClientHandler (IStudentsStore store) {
		this.store = store;
	}

	@Override
	public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

		writer.println("Hello. Online HELP is available. Will you find it?");
		writer.flush();

		String command;
		boolean done = false;
		while (!done && ((command = reader.readLine()) != null)) {
			numberOfCommands++;
			LOG.log(Level.INFO, "COMMAND: {0}", command);
			switch (command.toUpperCase()) {
				case RouletteV1Protocol.CMD_RANDOM:
					RandomCommandResponse rcResponse = new RandomCommandResponse();
					try {
						rcResponse.setFullname(store.pickRandomStudent().getFullname());
					} catch (EmptyStoreException ex) {
						rcResponse.setError("There is no student, you cannot pick a random one");
					}
					writer.println(JsonObjectMapper.toJson(rcResponse));
					writer.flush();
					break;
				case RouletteV1Protocol.CMD_HELP:
					writer.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
					break;
				case RouletteV1Protocol.CMD_INFO:
					InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
					writer.println(JsonObjectMapper.toJson(response));
					writer.flush();
					break;
				case RouletteV1Protocol.CMD_LOAD:
					writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
					writer.flush();
					store.importData(reader);

					LoadCommandResponse data = new LoadCommandResponse(RouletteV2Protocol.CONFIRM_SUCCESS, store.getNumberOfStudents());
					writer.println(JsonObjectMapper.toJson(data));
					writer.flush();
					break;
				case RouletteV1Protocol.CMD_BYE:
					ByeCommandReponse bye = new ByeCommandReponse(RouletteV2Protocol.CONFIRM_SUCCESS, numberOfCommands);
					writer.println(JsonObjectMapper.toJson(bye));
					writer.flush();
					done = true;
					break;

				case RouletteV2Protocol.CMD_LIST: // V2
					// Creation of the student list
					StudentsList listOfStudents = new StudentsList();
					listOfStudents.addAll(store.listStudents());
					writer.println(JsonObjectMapper.toJson(listOfStudents));
					writer.flush();
					break;

				case RouletteV2Protocol.CMD_CLEAR: // V2
					//clear the strore
					store.clear();
					writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
					writer.flush();
					break;

				default:
					writer.println("Huh? please use HELP if you don't know what commands are available.");
					writer.flush();
					break;
			}
			writer.flush();
		}

	}

}
