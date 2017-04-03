package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Thibaud Besseau
 * @author Nathalie Megevand
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client
{
	private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
  @Override
  public void clearDataStore() throws IOException {
    LOG.log(Level.FINE, "Clear the Data Store");

    // Ask the server to clear the Data Store
	  printWriter.println("CLEAR");
	  printWriter.flush();
	  buffReader.readLine();
  }

  @Override
  public List<Student> listStudents() throws IOException {
    LOG.log(Level.FINE, "Obtain the list of students");

    // Ask the server to send the list of the students
	  printWriter.println("LIST");
	  printWriter.flush();

    // Convert the JSON and put the result in listOfStudent
    StudentsList listOfStudents = JsonObjectMapper.parseJson(buffReader.readLine(), StudentsList.class);

    // return the list of student
    return listOfStudents.getStudents();  }
  
}
