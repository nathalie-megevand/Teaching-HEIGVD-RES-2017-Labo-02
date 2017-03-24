package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

  // socket for server connection
  private Socket serverSocket = null;
  // input from server
  private BufferedReader buffReader = null;
  protected PrintWriter printWriter = null;



  @Override
  public void connect(String server, int port) throws IOException {
  	 //we open a connection with the serveur
      serverSocket = new Socket(server, port);
      // we open a input and a ouput stream
      printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
      buffReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

      //read the welcomme message on the serveur
	  buffReader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
  	//send the message BYE
    printWriter.println(RouletteV1Protocol.CMD_BYE);
    printWriter.flush();

    //close the input and ouput stream
	printWriter.close();
    buffReader.close();
    //close the connection with the server
    serverSocket.close();
  }

  @Override
  public boolean isConnected()
  {

      if (serverSocket != null)
      {
        return serverSocket.isConnected() && !serverSocket.isClosed();
      }
      else
      {
        return false;
      }
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    ArrayList<Student> studentsList = new ArrayList<Student>();
    studentsList.add(new Student(fullname));
    loadStudents(studentsList);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException
  {
	  //display loading message
	  printWriter.println(RouletteV1Protocol.CMD_LOAD);
	  printWriter.flush();

	  //test if the server respond correctly
	  if(!buffReader.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START))
		  throw new IOException("Error during the loading !");

	  //browse all the students
	  for(Student currentStudent : students)
	  {
		  //test if the fullName is not empty
		  if (currentStudent.getFullname().length() >= 1)
		  {
			  //display full name
			  printWriter.println(currentStudent.getFullname());
		  }
	  }

	  //announced the end of the transfert with ENDOFDATA
	  printWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
	  printWriter.flush();
	  //read the message from the server who announced the end of data reception
	  buffReader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException
  {
  	//we send a message to the server . This message say random because we want obtain a random student
	  printWriter.println(RouletteV1Protocol.CMD_RANDOM);
	  printWriter.flush();

	  //we receive the answer from the server
	  RandomCommandResponse response = JsonObjectMapper.parseJson(buffReader.readLine(), RandomCommandResponse.class);

	  //test if the reponse from the server is correct
	   if(response.getError() != null)
	  {
		  throw new EmptyStoreException();
	  }
	  else
	  {
		  return new Student(response.getFullname());
	  }

  }

  @Override
  public int getNumberOfStudents() throws IOException
  {
	  //we send the message info to the server
	  printWriter.println(RouletteV1Protocol.CMD_INFO);
	  printWriter.flush();

	  InfoCommandResponse response = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponse.class);
	  return response.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException
  {
	  //we send the message info to the server
	  printWriter.println(RouletteV1Protocol.CMD_INFO);
	  printWriter.flush();
	  InfoCommandResponse info = JsonObjectMapper.parseJson(buffReader.readLine(), InfoCommandResponse.class);
	  return info.getProtocolVersion();
  }
}
